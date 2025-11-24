#ifndef ATMOSPHERIC_RAIN_SPLASHES_GLSL
    #define ATMOSPHERIC_RAIN_SPLASHES_GLSL

    void ApplyRainSplashes(inout vec3 color, vec2 screenUv, vec3 viewPos,
                           vec3 playerPos, float sceneDepth) {
        #ifndef OVERWORLD
            return;
        #endif
        if (rainFactor < 0.01 || sceneDepth >= 0.9999) return;
        if (isEyeInWater == 1 || isEyeInWater == 2) return;

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
        float horizontalMask = smoothstep(0.65, 0.98, abs(normal.y));
        if (horizontalMask <= 0.0001) return;

        vec2 uvAbove = clamp(screenUv - vec2(0.0, invView.y * 3.0), safeMin, safeMax);
        float depthAbove = texture2D(depthtex0, uvAbove).r;
        float downwardMask = 0.0;
        if (depthAbove >= 0.9999) {
            downwardMask = 1.0;
        } else {
            vec3 viewAbove = ScreenToView(vec3(uvAbove, depthAbove));
            float clearance = length(viewAbove) - length(viewPos);
            downwardMask = smoothstep(0.35, 2.0, clearance);
        }

        float detection = horizontalMask * downwardMask;
        if (detection <= 0.0001) return;

        vec2 worldXZ = playerPos.xz + cameraPosition.xz;
        float density = mix(0.5, 1.35, clamp(rainFactor + wetness * 0.5, 0.0, 1.0));
        vec2 cell = floor(worldXZ * density);
        vec2 cellFrac = fract(worldXZ * density);
        vec2 dropCenter = Hash22(cell);
        vec2 local = cellFrac - dropCenter;

        float hash = Hash12(cell);
        float bigDrop = step(0.82, hash);
        float dropLife = mix(0.18, 0.32, mix(rainFactor, 0.7, bigDrop));
        float dropRate = mix(0.35, 0.85, rainFactor);
        float timer = fract(hash + frameTimeCounter * dropRate);
        float active = step(timer, dropLife);
        if (active < 0.5) return;

        float phase = clamp(timer / dropLife, 0.0, 1.0);
        vec2 windDir = GetAtmosphericWindDir();
        vec2 windPerp = Rotate90(windDir);
        float alongStretch = mix(0.9, 1.5, rainFactor) * mix(1.0, 1.25, bigDrop);
        float perpStretch = mix(1.0, 0.65, rainFactor) * mix(1.0, 0.8, bigDrop);
        vec2 ellipse = vec2(dot(local, windDir) / alongStretch,
                            dot(local, windPerp) / perpStretch);
        float dist = length(ellipse);

        float baseRadius = mix(0.04, 0.25, phase) * mix(1.0, 1.35, bigDrop);
        float rippleWidth = mix(0.05, 0.14, mix(0.4, 0.8, bigDrop));
        float ripple = smoothstep(rippleWidth, 0.0, abs(dist - baseRadius));

        float splashPulse = pow(1.0 - phase, 1.5);
        float highlight = splashPulse * mix(1.0, 1.35, bigDrop);

        float intensity = detection * active * mix(0.08, 0.35, rainFactor);
        float rippleIntensity = ripple * intensity;
        float highlightIntensity = highlight * intensity;

        vec3 rippleTint = mix(vec3(0.08, 0.12, 0.16), vec3(0.18, 0.25, 0.32), rainFactor);
        vec3 highlightTint = mix(vec3(0.24, 0.33, 0.42), vec3(0.38, 0.46, 0.55), rainFactor);

        color += rippleTint * rippleIntensity;
        color += highlightTint * highlightIntensity;
    }

#endif
