#ifndef ATMOSPHERIC_WET_SURFACE_GLSL
    #define ATMOSPHERIC_WET_SURFACE_GLSL

    #include "/lib/atmospherics/atmoCommon.glsl"
    #include "/lib/atmospherics/rain/rainCommon.glsl"

    void ApplyWetSurfaceOverlay(inout vec3 color, vec2 screenUv, vec3 viewPos,
                                vec3 worldPos, float sceneDepth, float lViewPos,
                                float sunFactor) {
        #if ATM_STORM_BOOST == 0
            return;
        #endif
        #ifndef OVERWORLD
            return;
        #endif

        if (sceneDepth >= 0.9999) return;

        float wetMix = max(GetStormActivity(), GetStormDryingMask());
        if (wetMix <= 0.001) return;

        vec2 invView = vec2(1.0 / viewWidth, 1.0 / viewHeight);
        vec2 safeMin = invView * 0.5;
        vec2 safeMax = vec2(1.0) - safeMin;

        vec2 uvRight = clamp(screenUv + vec2(invView.x, 0.0), safeMin, safeMax);
        vec2 uvUp    = clamp(screenUv + vec2(0.0, invView.y), safeMin, safeMax);

        float depthRight = texture2D(depthtex0, uvRight).r;
        float depthUp    = texture2D(depthtex0, uvUp).r;
        if (depthRight >= 0.9999 || depthUp >= 0.9999) return;

        vec3 viewRight = ScreenToView(vec3(uvRight, depthRight));
        vec3 viewUp    = ScreenToView(vec3(uvUp, depthUp));

        vec3 dx = viewRight - viewPos;
        vec3 dy = viewUp - viewPos;
        vec3 normal = normalize(cross(dx, dy));

        float planar = smoothstep(0.55, 0.98, abs(normal.y));
        if (planar <= 0.0001) return;

        vec3 viewDir = normalize(viewPos);
        float fresnel = pow(clamp(1.0 - abs(dot(normal, -viewDir)), 0.0, 1.0), 3.0);

        float distanceFade = exp(-lViewPos * 0.015);
        float absoluteHeight = worldPos.y + cameraPosition.y;
        float stormFactor = 1.0 + 0.4 * clamp(Get_SC_StormDarkness(), 0.0, 1.0);
        float heightFade = AtmosphericHeightFade(max(absoluteHeight, 0.0), 0.02, stormFactor);

        float wetStrength = planar * wetMix * distanceFade * heightFade;
        if (wetStrength <= 0.0005) return;

        vec2 rippleWind = GetAtmosphericWindVector() * 0.0009;
        float ripplePhase = frameTimeCounter * 0.6 + dot(worldPos.xz + cameraPosition.xz, vec2(0.04, 0.07));
        vec2 rippleOffset = rippleWind * sin(ripplePhase)
                          + Rotate90(rippleWind) * cos(ripplePhase * 1.3) * 0.4;
        vec2 rippleCoord = clamp(screenUv + rippleOffset, safeMin, safeMax);
        vec3 rippleSample = texture2D(colortex0, rippleCoord).rgb;
        vec3 rippleDelta = rippleSample - color;
        color += rippleDelta * (wetStrength * 0.12);

        vec3 glossTint = mix(vec3(0.05, 0.07, 0.09), vec3(0.15, 0.18, 0.20), wetMix);
        color += glossTint * wetStrength * 0.25;

        vec3 reflectionColor = mix(color, rippleSample, 0.5) + vec3(0.02, 0.025, 0.03) * sunFactor;
        color = mix(color, reflectionColor, wetStrength * (0.35 + 0.35 * fresnel));
    }

#endif
