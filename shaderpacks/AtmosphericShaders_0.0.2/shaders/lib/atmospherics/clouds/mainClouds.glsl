#include "/lib/colors/lightAndAmbientColors.glsl"
#include "/lib/colors/cloudColors.glsl"
#include "/lib/atmospherics/sky.glsl"
#include "/lib/util/sc_bridge.glsl"




float InterleavedGradientNoiseForClouds() {
    float n = 52.9829189 * fract(0.06711056 * gl_FragCoord.x + 0.00583715 * gl_FragCoord.y);
    #ifdef TAA
        return fract(n + goldenRatio * mod(float(frameCounter), 3600.0));
    #else
        return fract(n);
    #endif
}

#if SHADOW_QUALITY > -1
    vec3 GetShadowOnCloudPosition(vec3 tracePos, vec3 cameraPos) {
        vec3 wpos = PlayerToShadow(tracePos - cameraPos);
        float distb = sqrt(wpos.x * wpos.x + wpos.y * wpos.y);
        float distortFactor = 1.0 - shadowMapBias + distb * shadowMapBias;
        vec3 shadowPosition = vec3(vec2(wpos.xy / distortFactor), wpos.z * 0.2);
        return shadowPosition * 0.5 + 0.5;
    }

    bool GetShadowOnCloud(vec3 tracePos, vec3 cameraPos, int cloudAltitude, float lowerPlaneAltitude, float higherPlaneAltitude) {
        const float cloudShadowOffset = 0.5;

        vec3 shadowPosition0 = GetShadowOnCloudPosition(tracePos, cameraPos);
        if (length(shadowPosition0.xy * 2.0 - 1.0) < 1.0) {
            float shadowsample0 = shadow2D(shadowtex0, shadowPosition0).z;

            if (shadowsample0 == 0.0) return true;
        }

        return false;
    }
#endif

#ifdef CLOUDS_REIMAGINED
    #include "/lib/atmospherics/clouds/reimaginedClouds.glsl"
#endif
#ifdef CLOUDS_UNBOUND
    #include "/lib/atmospherics/clouds/unboundClouds.glsl"
#endif

vec4 GetClouds(inout float cloudLinearDepth, float skyFade, vec3 cameraPos, vec3 playerPos,
               float lViewPos, float VdotS, float VdotU, float dither, vec3 auroraBorealis, vec3 nightNebula) {
    
        // ===== SimpleClouds compatibility =====
    #if USE_SC

        float scCloudThickness = Get_SC_ThicknessRaw();
        float scCloudStormDark = Get_SC_StormDarkness();
        float scThicknessAmbient = mix(0.85, 1.2, scCloudThickness);
        float scThicknessLight   = mix(0.9, 1.3, scCloudThickness);

        vec3 scCloudStormTint = mix(vec3(1.0), vec3(0.65, 0.7, 0.78), scCloudStormDark);
        float scStormDim      = mix(1.0, 0.55, scCloudStormDark);

        vec3 cloudRainColor = mix(nightMiddleSkyColor, dayMiddleSkyColor, sunFactor) * scCloudStormTint;

        cloudAmbientColor = mix(
            ambientColor * (sunVisibility2 * (0.55 + 0.1 * noonFactor) + 0.35),
            cloudRainColor * 0.5,
            rainFactor
        );

        cloudLightColor = mix(
            lightColor * (0.9 + 0.2 * noonFactor),
            cloudRainColor * 0.25,
            noonFactor * rainFactor
        );
        cloudAmbientColor *= scThicknessAmbient * mix(1.0, 0.65, scCloudStormDark);
        cloudLightColor   *= scThicknessLight   * scStormDim;

    #else

        float scCloudThickness = 0.0;
        float scCloudStormDark = 0.0;
        float scThicknessAmbient = 1.0;
        float scThicknessLight   = 1.0;
        float scStormDim         = 1.0;
        vec3 cloudRainColor = mix(nightMiddleSkyColor, dayMiddleSkyColor, sunFactor);

        cloudAmbientColor = ambientColor;
        cloudLightColor   = lightColor;

    #endif

    vec4 clouds = vec4(0.0);
    float scThicknessScale = Get_SC_ThicknessScale();

    vec3 nPlayerPos = normalize(playerPos);
    float lViewPosM = lViewPos < renderDistance * 1.5 ? lViewPos - 1.0 : 1000000000.0;
    float skyMult0 = pow2(skyFade * 3.333333 - 2.333333);
    skyMult0 *= mix(0.85, 1.25, clamp(scThicknessScale - 0.5, 0.0, 1.0));

    float thresholdMix = pow2(clamp01(VdotU * 5.0));
    float thresholdF = mix(far, 1000.0, thresholdMix * 0.5 + 0.5);
    thresholdF *= mix(0.85, 1.2, clamp(scThicknessScale - 0.5, 0.0, 1.0));
    #ifdef DISTANT_HORIZONS
        thresholdF = max(thresholdF, renderDistance);
    #endif

    #ifdef CLOUDS_REIMAGINED
        cloudAmbientColor *= 1.0 - 0.25 * rainFactor;
    #endif

    vec3 cloudColorMult = vec3(1.0);
    #if CLOUD_R != 100 || CLOUD_G != 100 || CLOUD_B != 100
        cloudColorMult *= vec3(CLOUD_R, CLOUD_G, CLOUD_B) * 0.01;
    #endif
    cloudAmbientColor *= cloudColorMult;
    cloudLightColor *= cloudColorMult;

    #if !defined DOUBLE_REIM_CLOUDS || defined CLOUDS_UNBOUND
        clouds = GetVolumetricClouds(cloudAlt1i, thresholdF, cloudLinearDepth, skyFade, skyMult0,
                                        cameraPos, nPlayerPos, lViewPosM, VdotS, VdotU, dither);
    #else
        int maxCloudAlt = max(cloudAlt1i, cloudAlt2i);
        int minCloudAlt = min(cloudAlt1i, cloudAlt2i);

        if (abs(cameraPos.y - minCloudAlt) < abs(cameraPos.y - maxCloudAlt)) {
            clouds = GetVolumetricClouds(minCloudAlt, thresholdF, cloudLinearDepth, skyFade, skyMult0,
                                            cameraPos, nPlayerPos, lViewPosM, VdotS, VdotU, dither);
            if (clouds.a == 0.0) {
            clouds = GetVolumetricClouds(maxCloudAlt, thresholdF, cloudLinearDepth, skyFade, skyMult0,
                                            cameraPos, nPlayerPos, lViewPosM, VdotS, VdotU, dither);
            }
        } else {
            clouds = GetVolumetricClouds(maxCloudAlt, thresholdF, cloudLinearDepth, skyFade, skyMult0,
                                            cameraPos, nPlayerPos, lViewPosM, VdotS, VdotU, dither);
            if (clouds.a == 0.0) {
            clouds = GetVolumetricClouds(minCloudAlt, thresholdF, cloudLinearDepth, skyFade, skyMult0,
                                            cameraPos, nPlayerPos, lViewPosM, VdotS, VdotU, dither);
            }
        }
    #endif

    #ifdef ATM_COLOR_MULTS
        clouds.rgb *= sqrtAtmColorMult; // C72380KD - Reduced atmColorMult impact on some things
    #endif
    #ifdef MOON_PHASE_INF_ATMOSPHERE
        clouds.rgb *= moonPhaseInfluence;
    #endif

    #if AURORA_STYLE > 0
        clouds.rgb += auroraBorealis * 0.1;
    #endif
    #ifdef NIGHT_NEBULA
        clouds.rgb += nightNebula * 0.2;
    #endif

    clouds.rgb = mix(clouds.rgb, clouds.rgb * 0.5, scStormDark);

    return clouds;
}
