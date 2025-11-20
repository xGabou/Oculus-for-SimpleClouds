#ifndef SC_BRIDGE_GLSL
#define SC_BRIDGE_GLSL

// SC ENABLE FLAGS (manual override, Iris cannot auto-detect)
#define USE_SC 1
#define USE_SC_CLOUDS 0

//--------------------------------------------------
// FULL CLOUDS FEATURE SET (SimpleClouds active)
//--------------------------------------------------
#if USE_SC_CLOUDS

    //--------------------------------------------------
    // Uniforms (guaranteed declared exactly once)
    //--------------------------------------------------
    #ifndef SC_UNIFORMS_DECLARED
        #define SC_UNIFORMS_DECLARED
        uniform vec4  sc_State;            // Visibility, Thickness, Storminess, SmoothStorm
        uniform vec4  sc_Type;             // Cloud-type weighting
        uniform float sc_CloudShadowFactor;
        uniform sampler2D sc_CloudLayerTex;
    #endif

    #ifndef CLOUD_SHADOW_QUALITY_MODE
        #define CLOUD_SHADOW_QUALITY_MODE 2
    #endif

    //--------------------------------------------------
    // Safe SC State fetch
    //--------------------------------------------------
    vec4 Get_SC_StateSafe() {
        vec4 state = sc_State;
        float avail = smoothstep(0.0001, 0.0005, dot(state, vec4(1.0)));
        return mix(vec4(0.0), state, avail);
    }

    float Get_SC_VisibilityFactor()        { return Get_SC_StateSafe().x; }
    float Get_SC_ThicknessRaw()            { return Get_SC_StateSafe().y; }
    float Get_SC_StorminessValue()         { return Get_SC_StateSafe().z; }
    float Get_SC_SmoothStorminessValue()   { return Get_SC_StateSafe().w; }

    //--------------------------------------------------
    // Storm + thickness
    //--------------------------------------------------
    float Get_SC_StormDarkness() {
        float a = Get_SC_StorminessValue();
        float b = Get_SC_SmoothStorminessValue();
        return clamp(mix(a, b, 0.6), 0.0, 1.0);
    }

    float scStormDark = Get_SC_StormDarkness();

    float Get_SC_ThicknessScale() {
        // Raw 0.0→1.0 remapped to 0.5→1.5
        return mix(0.5, 1.5, Get_SC_ThicknessRaw());
    }

    //--------------------------------------------------
    // Cloud type mask
    //--------------------------------------------------
    vec4 Get_SC_TypeMask() {
        vec4 m = clamp(sc_Type, 0.0, 1.0);
        float s = m.x + m.y + m.z + m.w;
        return (s <= 0.0001) ? vec4(1.0, 0.0, 0.0, 0.0) : m / s;
    }

    float Apply_SC_TypeShading(float shadingValue) {
        float shading = clamp(shadingValue, 0.0, 2.0);
        vec4 mask = Get_SC_TypeMask();
        vec4 variants = vec4(
            pow(shading, 0.70),
            shading,
            pow(shading, 1.10),
            pow(shading, 1.35)
        );
        return dot(mask, variants);
    }

    //--------------------------------------------------
    // Cloud shadow strength (procedural fallback)
    //--------------------------------------------------
    float Get_SC_ShadowStrength() {
        float thick = Get_SC_ThicknessRaw();
        float storm = Get_SC_StormDarkness();
        float coverage = clamp(thick * 0.65 + storm * 0.45, 0.0, 1.0);
        float shadow = smoothstep(0.25, 0.85, coverage);
        return shadow * 0.85;
    }

    int Get_SC_CloudShadowMode() {
        return clamp(CLOUD_SHADOW_QUALITY_MODE, 0, 2);
    }

    //--------------------------------------------------
    // Cloud layer texture (from Oculus-for-SC)
    //--------------------------------------------------
    bool SC_HasCloudLayerTexture() {
        ivec2 t = textureSize(sc_CloudLayerTex, 0);
        return (t.x > 0 && t.y > 0);
    }

    #ifdef FRAGMENT_SHADER
        vec2 Get_SC_CloudLayerUV() {
            #ifdef cameraPosition
                return fract(cameraPosition.xz * 0.000244140625);
            #else
                return vec2(0.0);
            #endif
        }
    #else
        vec2 Get_SC_CloudLayerUV() {
            return vec2(0.0);
        }
    #endif


    float Sample_SC_CloudLayerShadow() {
        if (!SC_HasCloudLayerTexture()) return -1.0;
        float c = texture2D(sc_CloudLayerTex, Get_SC_CloudLayerUV()).r;
        return clamp(c, 0.0, 1.0);
    }

    //--------------------------------------------------
    // Combined shadow result
    //--------------------------------------------------
    float Get_SC_FinalShadow() {
        int mode = Get_SC_CloudShadowMode();

        // Mode 2 = full texture-driven shadows
        if (mode == 2) {
            float s = Sample_SC_CloudLayerShadow();

            if (s >= 0.0) {
                if (s < 0.05 &&
                    Get_SC_VisibilityFactor() >= 2.5 &&
                    Get_SC_ThicknessRaw() <= 0.02) return 0.0;

                float cloudDark = smoothstep(0.20, 0.90, s) * 0.75;
                return cloudDark;
            }

            mode = 1; // fallback
        }

        // Mode 0 = SC-provided default
        if (mode == 0) {
            if (sc_CloudShadowFactor > 0.0)
                return clamp(sc_CloudShadowFactor, 0.0, 1.0);
        }

        return Get_SC_ShadowStrength();
    }


//--------------------------------------------------
// SC DISABLED → Full valid stub API
//--------------------------------------------------
#else

    #define Get_SC_VisibilityFactor()        0.0
    #define Get_SC_ThicknessRaw()            0.0
    #define Get_SC_StorminessValue()         0.0
    #define Get_SC_SmoothStorminessValue()   0.0

    float Get_SC_StormDarkness()         { return 0.0; }
    float Get_SC_ThicknessScale()        { return 1.0; }

    float Apply_SC_TypeShading(float x) { return x; }

    float scStormDark = 0.0;

    float Get_SC_ShadowStrength()        { return 0.0; }
    int   Get_SC_CloudShadowMode()       { return 0; }

    bool SC_HasCloudLayerTexture()       { return false; }
    vec2 Get_SC_CloudLayerUV()           { return vec2(0.0); }
    float Sample_SC_CloudLayerShadow()   { return -1.0; }

    float Get_SC_FinalShadow()           { return 0.0; }

#endif

#endif // SC_BRIDGE_GLSL
