#ifndef INCLUDE_LIGHT_AND_AMBIENT_COLORS
    #define INCLUDE_LIGHT_AND_AMBIENT_COLORS

    #include "/lib/util/sc_bridge.glsl"

    #if defined OVERWORLD
        #ifndef COMPOSITE
            vec3 noonClearLightColor = vec3(0.7, 0.55, 0.4) * 1.9;
        #else
            vec3 noonClearLightColor = vec3(0.4, 0.7, 1.4);
        #endif
        vec3 noonClearAmbientColor = pow(skyColor, vec3(0.65)) * 0.85;

        #ifndef COMPOSITE
            vec3 sunsetClearLightColor = pow(vec3(0.64, 0.45, 0.3), vec3(1.5 + invNoonFactor)) * 5.0;
        #else
            vec3 sunsetClearLightColor = pow(vec3(0.62, 0.39, 0.24), vec3(1.5 + invNoonFactor)) * 6.8;
        #endif
        vec3 sunsetClearAmbientColor   = noonClearAmbientColor * vec3(1.21, 0.92, 0.76) * 0.95;

        #if !defined COMPOSITE && !defined DEFERRED1
            vec3 nightClearLightColor = vec3(0.15, 0.14, 0.20) * (0.4 + vsBrightness * 0.4);
        #elif defined DEFERRED1
            vec3 nightClearLightColor = vec3(0.11, 0.14, 0.20);
        #else
            vec3 nightClearLightColor = vec3(0.07, 0.12, 0.27);
        #endif
        vec3 nightClearAmbientColor   = vec3(0.09, 0.12, 0.17) * (1.55 + vsBrightness * 0.77);

        #ifdef SPECIAL_BIOME_WEATHER
            vec3 drlcSnowM = inSnowy * vec3(-0.06, 0.0, 0.04);
            vec3 drlcDryM = inDry * vec3(0.0, -0.03, -0.05);
        #else
            vec3 drlcSnowM = vec3(0.0), drlcDryM = vec3(0.0);
        #endif
        #if RAIN_STYLE == 2
            vec3 drlcRainMP = vec3(-0.03, 0.0, 0.02);
            #ifdef SPECIAL_BIOME_WEATHER
                vec3 drlcRainM = inRainy * drlcRainMP;
            #else
                vec3 drlcRainM = drlcRainMP;
            #endif
        #else
            vec3 drlcRainM = vec3(0.0);
        #endif
        vec3 dayRainLightColor   = vec3(0.21, 0.16, 0.13) * 0.85 + noonFactor * vec3(0.0, 0.02, 0.06)
                                + rainFactor * (drlcRainM + drlcSnowM + drlcDryM);
        vec3 dayRainAmbientColor = vec3(0.2, 0.2, 0.25) * (1.8 + 0.5 * vsBrightness);

        vec3 nightRainLightColor   = vec3(0.03, 0.035, 0.05) * (0.5 + 0.5 * vsBrightness);
        vec3 nightRainAmbientColor = vec3(0.16, 0.20, 0.3) * (0.75 + 0.6 * vsBrightness);

        #ifndef COMPOSITE
            float noonFactorDM = noonFactor;
        #else
            float noonFactorDM = noonFactor * noonFactor;
        #endif
        vec3 dayLightColor   = mix(sunsetClearLightColor, noonClearLightColor, noonFactorDM);
        vec3 dayAmbientColor = mix(sunsetClearAmbientColor, noonClearAmbientColor, noonFactorDM);

        vec3 clearLightColor   = mix(nightClearLightColor, dayLightColor, sunVisibility2);
        vec3 clearAmbientColor = mix(nightClearAmbientColor, dayAmbientColor, sunVisibility2);

        vec3 rainLightColor   = mix(nightRainLightColor, dayRainLightColor, sunVisibility2) * 2.5;
        vec3 rainAmbientColor = mix(nightRainAmbientColor, dayRainAmbientColor, sunVisibility2);

        vec3 baseLightColor   = mix(clearLightColor, rainLightColor, rainFactor);
        vec3 baseAmbientColor = mix(clearAmbientColor, rainAmbientColor, rainFactor);


        #if USE_SC
            float thicknessMask = clamp(Get_SC_ThicknessRaw(), 0.0, 1.0);
            float lightThicknessBoost   = mix(1.0, 0.85, thicknessMask);
            float ambientThicknessBoost = mix(1.0, 0.9, thicknessMask);
            #if defined GBUFFERS_ENTITIES || defined GBUFFERS_HAND || defined GBUFFERS_TEXTURED

                vec3 lightColor   = baseLightColor * lightThicknessBoost;
                vec3 ambientColor = baseAmbientColor * ambientThicknessBoost;

            #else
                    float storm    = clamp(Get_SC_StormDarkness(), 0.0, 1.0);
                    float thick    = clamp(Get_SC_ThicknessRaw(), 0.0, 1.0);

                    float stormNorm  = clamp(storm / 0.6, 0.0, 1.0);
                    float stormCurve = pow(stormNorm, 1.45);

                    float lightDim   = mix(1.0, 0.28, stormCurve);
                    float ambientDim = mix(1.0, 0.40, stormCurve);

                    float thickDimLight   = mix(1.0, 0.70, thick);
                    float thickDimAmbient = mix(1.0, 0.80, thick);

                    vec3 lightColor =
                        baseLightColor
                        * mix(1.0, 0.45, scStormDark)
                        * lightThicknessBoost
                        * lightDim
                        * thickDimLight;

                    vec3 ambientColor =
                        baseAmbientColor
                        * mix(1.0, 0.7, scStormDark)
                        * ambientThicknessBoost
                        * ambientDim
                        * thickDimAmbient;

            #endif

        #else
            float thicknessMask = 0.0;
            float lightThicknessBoost = 1.0;
            float ambientThicknessBoost = 1.0;
            vec3 lightColor   = baseLightColor * mix(1.0, 0.45, scStormDark) * lightThicknessBoost;
            vec3 ambientColor = baseAmbientColor * mix(1.0, 0.7, scStormDark) * ambientThicknessBoost;
        #endif


    #elif defined NETHER
        vec3 lightColor   = vec3(0.0);
        vec3 ambientColor = (netherColor + 0.5 * lavaLightColor) * (0.9 + 0.45 * vsBrightness);
    #elif defined END
        vec3 endLightColor = vec3(0.68, 0.51, 1.07);
        float endLightBalancer = 0.2 * vsBrightness;
        vec3 lightColor    = endLightColor * (0.35 - endLightBalancer);
        vec3 ambientColor  = endLightColor * (0.2 + endLightBalancer);
    #endif

#endif //INCLUDE_LIGHT_AND_AMBIENT_COLORS
