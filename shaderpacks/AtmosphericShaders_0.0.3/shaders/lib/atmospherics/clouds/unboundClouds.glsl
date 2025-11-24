#include "/lib/util/sc_bridge.glsl"

#if CLOUD_UNBOUND_SIZE_MULT != 100
    #define CLOUD_UNBOUND_SIZE_MULT_M CLOUD_UNBOUND_SIZE_MULT * 0.01
#endif

#if CLOUD_QUALITY == 1 || !defined DEFERRED1
    const float cloudStretchRaw = 11.0;
#elif CLOUD_QUALITY == 2
    const float cloudStretchRaw = 16.0;
#elif CLOUD_QUALITY == 3
    const float cloudStretchRaw = 18.0;
#endif

#if CLOUD_UNBOUND_SIZE_MULT <= 100
    const float cloudStretch = cloudStretchRaw;
#else
    const float cloudStretch = cloudStretchRaw / float(CLOUD_UNBOUND_SIZE_MULT_M);
#endif

const float cloudHeight = cloudStretch * 2.0;


// ------------------------------------------------------------
// 3D noise (unchanged)
// ------------------------------------------------------------
float Noise3D(vec3 p) {
    p.z = fract(p.z) * 128.0;
    float iz = floor(p.z);
    float fz = fract(p.z);
    vec2 a_off = vec2(23.0, 29.0) * (iz) / 128.0;
    vec2 b_off = vec2(23.0, 29.0) * (iz + 1.0) / 128.0;
    float a = texture2D(noisetex, p.xy + a_off).r;
    float b = texture2D(noisetex, p.xy + b_off).r;
    return mix(a, b, fz);
}



// ------------------------------------------------------------
// Cloud Noise (SC enhanced only when USE_SC)
// ------------------------------------------------------------
float GetCloudNoise(vec3 tracePos, int cloudAltitude, float lTracePosXZ, float cloudPlayerPosY) {
    vec3 tracePosM = tracePos.xyz * 0.00016;
    float wind = 0.0006;
    float noise = 0.0;
    float currentPersist = 1.0;
    float total = 0.0;

    #if CLOUD_SPEED_MULT == 100
        #define CLOUD_SPEED_MULT_M CLOUD_SPEED_MULT * 0.01
        wind *= syncedTime;
    #else
        #define CLOUD_SPEED_MULT_M CLOUD_SPEED_MULT * 0.01
        wind *= frameTimeCounter * CLOUD_SPEED_MULT_M;
    #endif

    #if CLOUD_UNBOUND_SIZE_MULT != 100
        tracePosM *= CLOUD_UNBOUND_SIZE_MULT_M;
        wind *= CLOUD_UNBOUND_SIZE_MULT_M;
    #endif


    #if CLOUD_QUALITY == 1
        int sampleCount = 2;
        float persistance = 0.6;
        float noiseMult = 0.95;
        tracePosM *= 0.5; wind *= 0.5;
    #elif CLOUD_QUALITY == 2 || !defined DEFERRED1
        int sampleCount = 4;
        float persistance = 0.5;
        float noiseMult = 1.07;
    #elif CLOUD_QUALITY == 3
        int sampleCount = 4;
        float persistance = 0.5;
        float noiseMult = 1.0;
    #endif

    #ifndef DEFERRED1
        noiseMult *= 1.2;
    #endif


    // Base perlin noise accumulation
    for (int i = 0; i < sampleCount; i++) {
    #if CLOUD_QUALITY >= 2
        noise += Noise3D(tracePosM + vec3(wind, 0.0, 0.0)) * currentPersist;
    #else
        noise += texture2D(noisetex, tracePosM.xz + vec2(wind, 0.0)).b * currentPersist;
    #endif

        total += currentPersist;
        tracePosM *= 3.0;
        wind *= 0.5;
        currentPersist *= persistance;
    }

    noise = pow2(noise / total);


    #if !defined DISTANT_HORIZONS
        #define CLOUD_BASE_ADD 0.65
        #define CLOUD_FAR_ADD 0.01
        #define CLOUD_ABOVE_ADD 0.1
    #else
        #define CLOUD_BASE_ADD 0.9
        #define CLOUD_FAR_ADD -0.005
        #define CLOUD_ABOVE_ADD 0.03
    #endif

    noiseMult *= CLOUD_BASE_ADD
               + CLOUD_FAR_ADD * sqrt(lTracePosXZ + 10.0)
               + CLOUD_ABOVE_ADD * clamp01(-cloudPlayerPosY / cloudHeight)
               + CLOUD_UNBOUND_RAIN_ADD * rainFactor;

    noise *= noiseMult * CLOUD_UNBOUND_AMOUNT;


    #if USE_SC
        float thicknessScale = Get_SC_ThicknessScale();
        float threshold = clamp(
            abs(cloudAltitude - tracePos.y) / (cloudStretch * thicknessScale),
            0.001, 0.999
        );
    #else
        float threshold = clamp(
            abs(cloudAltitude - tracePos.y) / cloudStretch,
            0.001, 0.999
        );
    #endif

    threshold = pow2(pow2(pow2(threshold)));

    return noise - (threshold * 0.2 + 0.25);
}



// ------------------------------------------------------------
// Volumetric Clouds (SC enhanced only when USE_SC)
// ------------------------------------------------------------
vec4 GetVolumetricClouds(
    int cloudAltitude,
    float distanceThreshold,
    inout float cloudLinearDepth,
    float skyFade,
    float skyMult0,
    vec3 cameraPos,
    vec3 nPlayerPos,
    float lViewPosM,
    float VdotS,
    float VdotU,
    float dither
) {
    vec4 volumetricClouds = vec4(0.0);


    #if USE_SC
        float thicknessScale = Get_SC_ThicknessScale();
        float thicknessStretch = cloudStretch * thicknessScale;
        float dynamicCloudHeight = cloudHeight * thicknessScale;
        float visibility = Get_SC_VisibilityFactor();
    #else
        float thicknessScale = 1.0;
        float thicknessStretch = cloudStretch;
        float dynamicCloudHeight = cloudHeight;
        float visibility = 1.0;
    #endif

    float distanceScale = mix(0.85, 1.2, clamp(thicknessScale - 0.5, 0.0, 1.0));

    float higherPlaneAltitude = cloudAltitude + thicknessStretch;
    float lowerPlaneAltitude  = cloudAltitude - thicknessStretch;


    float lowerPlaneDistance  = (lowerPlaneAltitude - cameraPos.y) / nPlayerPos.y;
    float higherPlaneDistance = (higherPlaneAltitude - cameraPos.y) / nPlayerPos.y;

    float minPlaneDistance = min(lowerPlaneDistance, higherPlaneDistance);
    minPlaneDistance = max(minPlaneDistance, 0.0);

    float maxPlaneDistance = max(lowerPlaneDistance, higherPlaneDistance);
    if (maxPlaneDistance < 0.0)
        return vec4(0.0);

    float planeDistanceDif = maxPlaneDistance - minPlaneDistance;


    #ifndef DEFERRED1
        float stepMult = 32.0;
    #elif CLOUD_QUALITY == 1
        float stepMult = 16.0;
    #elif CLOUD_QUALITY == 2
        float stepMult = 24.0;
    #elif CLOUD_QUALITY == 3
        float stepMult = 16.0;
    #endif


    #if CLOUD_UNBOUND_SIZE_MULT > 100
        stepMult = stepMult / sqrt(float(CLOUD_UNBOUND_SIZE_MULT_M));
    #endif

    int sampleCount = int(planeDistanceDif / stepMult + dither + 1);

    vec3 traceAdd = nPlayerPos * stepMult;
    vec3 tracePos = cameraPos + minPlaneDistance * nPlayerPos;

    tracePos += traceAdd * dither;
    tracePos.y -= traceAdd.y;


    float firstHitPos = 0.0;

    float VdotSM1 = max0(sunVisibility > 0.5 ? VdotS : -VdotS);
    float VdotSM1M = VdotSM1 * invRainFactor;
    float VdotSM2 = pow2(VdotSM1) * abs(sunVisibility - 0.5) * 2.0;
    float VdotSM3 = VdotSM2 * (2.5 + rainFactor) + 1.5 * rainFactor;


    #ifdef FIX_AMD_REFLECTION_CRASH
        sampleCount = min(sampleCount, 30);
    #endif


    for (int i = 0; i < sampleCount; i++) {
        tracePos += traceAdd;

        if (abs(tracePos.y - cloudAltitude) > thicknessStretch)
            break;

        vec3 cloudPlayerPos = tracePos - cameraPos;

        float lTracePos = length(cloudPlayerPos);
        float lTracePosXZ = length(cloudPlayerPos.xz);

        if (lTracePosXZ > distanceThreshold)
            break;

        float cloudMult = 1.0;

        if (lTracePos > lViewPosM) {
            if (skyFade < 0.7)
                continue;
            else
                cloudMult = skyMult0;
        }


        float cloudNoise = GetCloudNoise(tracePos, cloudAltitude, lTracePosXZ, cloudPlayerPos.y);

        if (cloudNoise > 0.00001) {

            if (firstHitPos < 1.0)
                firstHitPos = lTracePos;

            #if USE_SC
                        float opacityFactor = min1(cloudNoise * 8.0) * visibility;
            #else
                        float opacityFactor = min1(cloudNoise * 8.0);
            #endif

            float cloudShading =
                1.0 - (higherPlaneAltitude - tracePos.y) / dynamicCloudHeight;

            cloudShading *= 1.0 + 0.75 * VdotSM3 * (1.0 - opacityFactor);

            #if USE_SC
                        cloudShading = Apply_SC_TypeShading(cloudShading);
            #endif

            vec3 colorSample =
                cloudAmbientColor * (0.7 + 0.3 * cloudShading) +
                cloudLightColor   *        cloudShading;

            vec3 cloudSkyColor = GetSky(VdotU, VdotS, dither, true, false);

            #ifdef ATM_COLOR_MULTS
                        cloudSkyColor *= sqrtAtmColorMult;
            #endif

            float distanceThresholdScaled = distanceThreshold * distanceScale;
            float distanceRatio =
                (distanceThresholdScaled - lTracePosXZ) / distanceThresholdScaled;

            float cloudDistanceFactor = clamp(distanceRatio, 0.0, 0.8) * 1.25;

            #ifndef DISTANT_HORIZONS
                        float cloudFogFactor = cloudDistanceFactor;
            #else
                        float cloudFogFactor = clamp(distanceRatio, 0.0, 1.0);
            #endif

            float skyMult1 =
                1.0 - 0.2 * (1.0 - skyFade) * max(sunVisibility2, nightFactor);

            float skyMult2 =
                1.0 - 0.33333 * skyFade;

            float fogMixBase = clamp(cloudFogFactor * skyMult2 * 0.72, 0.0, 1.0);

            #if USE_SC
                float scCoverage = clamp(max(Get_SC_StormDarkness(), Get_SC_ThicknessRaw()), 0.0, 1.0);

                float fogMask = 1.0 - pow(scCoverage, 0.6);

                float fogMix = clamp(fogMixBase * fogMask, 0.0, 1.0);
            #else
                            float fogMix = fogMixBase;
            #endif

            colorSample = mix(
                cloudSkyColor,
                colorSample * skyMult1,
                fogMix
            );

            colorSample *= pow2(1.0 - maxBlindnessDarkness);


            volumetricClouds.rgb =
                mix(volumetricClouds.rgb, colorSample, 1.0 - min1(volumetricClouds.a));

            #if USE_SC
                        volumetricClouds.a += opacityFactor
                            * pow(cloudDistanceFactor,
                                0.5 + 10.0 * pow(abs(VdotSM1M), 90.0)
                            )
                            * cloudMult;
            #else
                        volumetricClouds.a += opacityFactor
                            * pow(cloudDistanceFactor,
                                0.5 + 10.0 * pow(abs(VdotSM1M), 90.0)
                            )
                            * cloudMult;
            #endif

            if (volumetricClouds.a > 0.9) {
                volumetricClouds.a = 1.0;
                break;
            }
        }
    }

    if (volumetricClouds.a > 0.5)
        cloudLinearDepth = sqrt(firstHitPos / renderDistance);

    return volumetricClouds;
}

