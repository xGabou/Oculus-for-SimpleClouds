#ifndef ATMOSPHERIC_COMMON_GLSL
    #define ATMOSPHERIC_COMMON_GLSL

    float AtmosphericHeightFade(float height, float density, float stormFactor) {
        float h = max(height, 0.0);
        float d = max(density, 0.0001);
        float s = max(stormFactor, 0.0005);
        float fade = exp(-h * d * s);
        return clamp(fade, 0.0, 1.0);
    }

#endif
