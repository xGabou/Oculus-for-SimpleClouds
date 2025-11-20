#ifndef ATMOSPHERIC_RAIN_COMMON_GLSL
    #define ATMOSPHERIC_RAIN_COMMON_GLSL

    #include "/lib/util/sc_bridge.glsl"

    float Hash12(vec2 p) {
        vec3 p3 = fract(vec3(p.xyx) * 0.1031);
        p3 += dot(p3, p3.yzx + 33.33);
        return fract((p3.x + p3.y) * p3.z);
    }

    vec2 Hash22(vec2 p) {
        vec3 p3 = fract(vec3(p.xyx) * vec3(0.1031, 0.1030, 0.0973));
        p3 += dot(p3, p3.yzx + 33.33);
        return fract(vec2(p3.x + p3.y, p3.y + p3.z));
    }

    vec2 Rotate90(vec2 v) {
        return vec2(-v.y, v.x);
    }

    vec2 GetAtmosphericWindDir() {
        float baseAngle = frameTimeCounter * 0.045
                        + (cameraPosition.x + cameraPosition.z) * 0.0005;
        vec2 dir = vec2(cos(baseAngle), sin(baseAngle));
        return normalize(dir);
    }

    float GetAtmosphericWindStrength() {
        float weatherMask = clamp(rainFactor * 1.15 + wetness * 0.35, 0.0, 1.25);
        float gust =
            sin(frameTimeCounter * 0.21 + cameraPosition.x * 0.01) * 0.5 + 0.5;
        return mix(0.35, 1.0, weatherMask) * (0.6 + 0.4 * gust);
    }

    vec2 GetAtmosphericWindVector() {
        return GetAtmosphericWindDir() * GetAtmosphericWindStrength();
    }

    float GetStormActivity() {
        float scStorm = clamp(Get_SC_StormDarkness(), 0.0, 1.0);
        float scThick = clamp(Get_SC_ThicknessRaw(), 0.0, 1.0);
        float coverage = max(scStorm, scThick);
        float rainfall = clamp(rainFactor + wetness * 0.5, 0.0, 1.0);
        return clamp(max(coverage, rainfall), 0.0, 1.0);
    }

    float GetStormDryingMask() {
        float dryMask = clamp(wetness, 0.0, 1.0);
        dryMask = mix(dryMask, pow2(dryMask), 0.5);
        return dryMask;
    }

    float GetStormStretchFactor() {
        return mix(1.0, 1.6, GetStormActivity());
    }

#endif
