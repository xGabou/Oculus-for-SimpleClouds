#ifndef ATMOSPHERIC_SKY_NOISE_GLSL
    #define ATMOSPHERIC_SKY_NOISE_GLSL

    vec3 ApplySkyMicroNoise(vec3 color, vec3 viewDir, float nightFactor, float dither) {
        #if ATM_ENV_POLISH == 0
            return color;
        #endif

        vec2 noiseCoord = viewDir.xz * 0.15 + frameTimeCounter * 0.01;
        float largeNoise = texture2D(noisetex, noiseCoord).g;
        float detailNoise = texture2D(noisetex, noiseCoord * 0.5 + 0.125).b;
        float noise = (largeNoise * 0.7 + detailNoise * 0.3) - 0.5;
        float influence = mix(0.0025, 0.01, (1.0 - nightFactor) * (1.0 - rainFactor));
        influence *= 0.75 + 0.25 * dither;
        return color + noise * influence;
    }

#endif
