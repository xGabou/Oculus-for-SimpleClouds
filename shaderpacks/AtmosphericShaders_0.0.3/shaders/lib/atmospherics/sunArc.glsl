#ifndef ATMOSPHERIC_SUN_ARC_GLSL
    #define ATMOSPHERIC_SUN_ARC_GLSL

    vec3 ApplySunScatteringArc(vec3 color, vec3 sunDirection, vec3 viewDir,
                               float sunVisibility, float dither) {
        #if ATM_SUN_SCATTER == 0
            return color;
        #endif

        if (rainFactor > 0.25 || sunVisibility < 0.35) return color;

        float cosAngle = clamp(dot(viewDir, sunDirection), 0.0, 1.0);
        float arc = pow(cosAngle, mix(64.0, 24.0, sunVisibility));
        float mieLobe = pow(cosAngle, 3.0);
        float softness = smoothstep(0.0, 0.15, 1.0 - cosAngle);
        float intensity = arc * mieLobe * softness;
        intensity *= mix(0.35, 0.65, sunVisibility);
        intensity *= mix(1.0, 0.6, rainFactor);
        intensity *= 0.85 + 0.15 * dither;

        vec3 arcTint = vec3(1.0, 0.82, 0.62);
        arcTint = mix(arcTint, vec3(0.9, 0.9, 1.0), pow2(1.0 - sunVisibility));

        return color + arcTint * intensity;
    }

#endif
