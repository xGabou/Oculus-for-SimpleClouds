#ifndef ATMOSPHERIC_CLOUD_INTEGRATION_GLSL
    #define ATMOSPHERIC_CLOUD_INTEGRATION_GLSL

    vec3 ApplyCloudRimShading(vec3 colorSample, vec3 tracePos, vec3 cameraPos,
                              vec3 sunVec, vec3 skyColor, float stormMask) {
        #if ATM_CLOUD_TINT == 0
            return colorSample;
        #endif

        vec3 toCamera = normalize(tracePos - cameraPos);
        float rim = pow(max(dot(toCamera, sunVec), 0.0), 2.0);
        float horizonFactor = smoothstep(0.0, 0.35, 1.0 - abs(toCamera.y));
        float rimMask = rim * horizonFactor * (1.0 - stormMask);
        vec3 rimTint = mix(skyColor, vec3(1.0, 0.95, 0.85), 0.5);
        return mix(colorSample, colorSample + rimTint * rimMask * 0.25, 0.6);
    }

    vec3 ApplyCloudAltitudeGrade(vec3 colorSample, vec3 tracePos, float stormMask) {
        #if ATM_CLOUD_TINT == 0
            return colorSample;
        #endif

        float altitude = max(tracePos.y + cameraPosition.y, 1.0);
        float density = AtmosphericHeightFade(altitude * 0.005, 0.85, 1.0 + 0.5 * stormMask);
        vec3 warm = vec3(1.04, 1.0, 0.96);
        vec3 cool = vec3(0.88, 0.93, 1.02);
        vec3 grade = mix(warm, cool, clamp(altitude / 512.0, 0.0, 1.0));
        return colorSample * mix(grade, vec3(1.0), 0.3 * density);
    }

    vec3 ApplyCloudStormColor(vec3 colorSample, float stormMask) {
        #if ATM_CLOUD_TINT == 0
            return colorSample;
        #endif

        vec3 stormTone = vec3(0.65, 0.72, 0.8);
        vec3 graded = mix(colorSample, colorSample * stormTone, smoothstep(0.1, 0.8, stormMask));
        return graded;
    }

#endif
