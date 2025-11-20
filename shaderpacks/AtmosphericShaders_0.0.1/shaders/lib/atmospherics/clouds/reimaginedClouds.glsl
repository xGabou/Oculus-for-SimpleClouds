#include "/lib/atmospherics/clouds/cloudCoord.glsl"
#include "/lib/util/sc_bridge.glsl"
#include "/lib/atmospherics/atmoCommon.glsl"
#include "/lib/atmospherics/clouds/cloudIntegration.glsl"

const float cloudStretch = 5.5;
const float cloudHeight  = cloudStretch * 2.0;

bool GetCloudNoise(vec3 tracePos, inout vec3 tracePosM, int cloudAltitude) {
    tracePosM = ModifyTracePos(tracePos, cloudAltitude);
    vec2 coord = GetRoundedCloudCoord(tracePosM.xz, 0.125);

    #if USE_SC
        float thicknessScale = Get_SC_ThicknessScale();
    #else
        float thicknessScale = 1.0;
    #endif

    #ifdef DEFERRED1
        float noise = texture2D(colortex3, coord).b;
    #else
        float noise = texture2D(gaux4, coord).b;
    #endif

    float threshold = clamp(abs(cloudAltitude - tracePos.y) / (cloudStretch * thicknessScale), 0.001, 0.999);
    threshold = pow2(pow2(pow2(threshold)));
    return noise > threshold * 0.5 + 0.25;
}

vec4 GetVolumetricClouds(int cloudAltitude, float distanceThreshold, inout float cloudLinearDepth, float skyFade, float skyMult0, vec3 cameraPos, vec3 nPlayerPos, float lViewPosM, float VdotS, float VdotU, float dither) {
    vec4 volumetricClouds = vec4(0.0);

    #if USE_SC
        float thicknessScale = Get_SC_ThicknessScale();
        float visibility = Get_SC_VisibilityFactor();
    #else
        float thicknessScale = 1.0;
        float visibility = 1.0;
    #endif

    float thicknessStretch = cloudStretch * thicknessScale;
    float distanceScale = mix(0.85, 1.2, clamp(thicknessScale - 0.5, 0.0, 1.0));
    float dynamicCloudHeight = cloudHeight * thicknessScale;

    float higherPlaneAltitude = cloudAltitude + thicknessStretch;
    float lowerPlaneAltitude  = cloudAltitude - thicknessStretch;

    float lowerPlaneDistance  = (lowerPlaneAltitude - cameraPos.y) / nPlayerPos.y;
    float higherPlaneDistance = (higherPlaneAltitude - cameraPos.y) / nPlayerPos.y;
    float minPlaneDistance = min(lowerPlaneDistance, higherPlaneDistance);
          minPlaneDistance = max(minPlaneDistance, 0.0);
    float maxPlaneDistance = max(lowerPlaneDistance, higherPlaneDistance);
    if (maxPlaneDistance < 0.0) return vec4(0.0);
    float planeDistanceDif = maxPlaneDistance - minPlaneDistance;

    #if CLOUD_QUALITY == 1 || !defined DEFERRED1
        int sampleCount = max(int(planeDistanceDif) / 16, 6);
    #elif CLOUD_QUALITY == 2
        int sampleCount = max(int(planeDistanceDif) / 8, 12);
    #elif CLOUD_QUALITY == 3
        int sampleCount = max(int(planeDistanceDif), 12);
    #endif

    float stepMult = planeDistanceDif / sampleCount;
    vec3 traceAdd = nPlayerPos * stepMult;
    vec3 tracePos = cameraPos + minPlaneDistance * nPlayerPos;
    tracePos += traceAdd * dither;
    tracePos.y -= traceAdd.y;

    #ifdef FIX_AMD_REFLECTION_CRASH
        sampleCount = min(sampleCount, 30);
    #endif

    for (int i = 0; i < sampleCount; i++) {
        tracePos += traceAdd;

        vec3 cloudPlayerPos = tracePos - cameraPos;
        float lTracePos = length(cloudPlayerPos);
        float lTracePosXZ = length(cloudPlayerPos.xz);
        float cloudMult = 1.0;
        if (lTracePosXZ > distanceThreshold) break;
        if (lTracePos > lViewPosM) {
            if (skyFade < 0.7) continue;
            else cloudMult = skyMult0;
        }

        vec3 tracePosM;
        if (GetCloudNoise(tracePos, tracePosM, cloudAltitude)) {
            float lightMult = 1.0;

            #if SHADOW_QUALITY > -1
                        float shadowLength = min(shadowDistance, far) * 0.9166667;
                        if (shadowLength > lTracePos)
                        if (GetShadowOnCloud(tracePos, cameraPos, cloudAltitude, lowerPlaneAltitude, higherPlaneAltitude)) {
            #ifdef CLOUD_CLOSED_AREA_CHECK
                            if (eyeBrightness.y != 240) continue;
                            else
            #endif
                            lightMult = 0.25;
                        }
            #endif

            float cloudShading = 1.0 - (higherPlaneAltitude - tracePos.y) / dynamicCloudHeight;
            float VdotSM1 = max0(sunVisibility > 0.5 ? VdotS : - VdotS);

            #if CLOUD_QUALITY >= 2
            #ifdef DEFERRED1
                        float cloudShadingM = 1.0 - pow2(cloudShading);
            #else
                        float cloudShadingM = 1.0 - cloudShading;
            #endif

            float gradientNoise = InterleavedGradientNoiseForClouds();

            vec3 cLightPos = tracePosM;
            vec3 cLightPosAdd = normalize(ViewToPlayer(lightVec * 1000000000.0)) * vec3(0.08);
            cLightPosAdd *= shadowTime;

            float light = 2.0;
            cLightPos += (1.0 + gradientNoise) * cLightPosAdd;
            #ifdef DEFERRED1
                        light -= texture2D(colortex3, GetRoundedCloudCoord(cLightPos.xz, 0.125)).b * cloudShadingM;
            #else
                        light -= texture2D(gaux4, GetRoundedCloudCoord(cLightPos.xz, 0.125)).b * cloudShadingM;
            #endif
                        cLightPos += gradientNoise * cLightPosAdd;
            #ifdef DEFERRED1
                        light -= texture2D(colortex3, GetRoundedCloudCoord(cLightPos.xz, 0.125)).b * cloudShadingM;
            #else
                        light -= texture2D(gaux4, GetRoundedCloudCoord(cLightPos.xz, 0.125)).b * cloudShadingM;
            #endif

                        float VdotSM2 = VdotSM1 * shadowTime * 0.25;
                        VdotSM2 += 0.5 * cloudShading + 0.08;
                        cloudShading = VdotSM2 * light * lightMult;
            #endif

            #if USE_SC
                        cloudShading = Apply_SC_TypeShading(cloudShading);
            #endif

                        vec3 colorSample = cloudAmbientColor + cloudLightColor * (0.07 + cloudShading);
                        vec3 cloudSkyColor = GetSky(VdotU, VdotS, dither, true, false);

            #ifdef ATM_COLOR_MULTS
                        cloudSkyColor *= sqrtAtmColorMult;
            #endif

                        float distanceThresholdScaled = distanceThreshold * distanceScale;
                        float distanceRatio = (distanceThresholdScaled - lTracePosXZ) / distanceThresholdScaled;
                        float cloudDistanceFactor = clamp(distanceRatio, 0.0, 0.75);

            #ifndef DISTANT_HORIZONS
                        float cloudFogFactor = cloudDistanceFactor;
            #else
                        float cloudFogFactor = pow1_5(clamp(distanceRatio, 0.0, 1.0)) * 0.75;
            #endif

                        float skyMult1 = 1.0 - 0.2 * (1.0 - skyFade) * max(sunVisibility2, nightFactor);
                        float skyMult2 = 1.0 - 0.33333 * skyFade;

            #if USE_SC
                        float scCoverage = clamp(max(Get_SC_StormDarkness(), Get_SC_ThicknessRaw()), 0.0, 1.0);
                        float fogMask = 1.0 - pow(scCoverage, 0.6);
                        float fogMixBase = clamp(cloudFogFactor * skyMult2, 0.0, 1.0);
                        float fogMix = clamp(fogMixBase * fogMask, 0.0, 1.0);
                        colorSample = mix(cloudSkyColor, colorSample * skyMult1, fogMix);
            #else
                        colorSample = mix(cloudSkyColor, colorSample * skyMult1, cloudFogFactor * skyMult2);
            #endif

            #if ATM_CLOUD_TINT == 1
            #if USE_SC
                        float stormMask = clamp(Get_SC_StormDarkness(), 0.0, 1.0);
            #else
                        float stormMask = 0.0;
            #endif
                        colorSample = ApplyCloudRimShading(colorSample, tracePos, cameraPos, sunVec, cloudSkyColor, stormMask);
                        colorSample = ApplyCloudAltitudeGrade(colorSample, tracePos, stormMask);
                        colorSample = ApplyCloudStormColor(colorSample, stormMask);
            #endif

            #if USE_SC
                        float scDark = mix(1.0, 0.65, Get_SC_StormDarkness());
                        float scThickDark = mix(1.0, 0.85, Get_SC_ThicknessRaw());
                        colorSample *= scDark;
                        colorSample *= scThickDark;
                        float densityDark = mix(1.0, 0.75, Get_SC_StormDarkness());
                        float densityThick = mix(1.0, 0.88, Get_SC_ThicknessRaw());
            #else
                        float densityDark = 1.0;
                        float densityThick = 1.0;
            #endif

            colorSample *= pow2(1.0 - maxBlindnessDarkness);

            cloudLinearDepth = sqrt(lTracePos / renderDistance);

            float rawAlpha = pow(
                cloudDistanceFactor * 1.33333,
                0.5 + 10.0 * pow(abs(VdotSM1), 90.0)
            );

            volumetricClouds.a = rawAlpha * cloudMult * visibility * densityDark * densityThick;
            volumetricClouds.rgb = colorSample;

            break;
        }
    }

    return volumetricClouds;
}
