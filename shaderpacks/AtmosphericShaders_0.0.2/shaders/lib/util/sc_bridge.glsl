#ifndef SC_BRIDGE_GLSL
    #define SC_BRIDGE_GLSL

    // Force-enable SC support for all passes.
    // Detection CANNOT work in Iris because uniforms are declared too late.
    #define USE_SC 1

    #if USE_SC

        #ifndef SC_UNIFORMS_DECLARED
            #define SC_UNIFORMS_DECLARED
            uniform vec4 sc_State;
            uniform vec4 sc_Type;
            uniform float sc_CloudShadowFactor;
        #endif

        vec4 Get_SC_StateSafe() {
            vec4 state = clamp(sc_State, vec4(0.0), vec4(1.0));
            float availability = smoothstep(0.0001, 0.0005, dot(state, vec4(1.0)));
            vec4 fallback = vec4(0.0);
            return mix(fallback, state, availability);
        }

        float Get_SC_VisibilityFactor()        { return Get_SC_StateSafe().x; }
        float Get_SC_ThicknessRaw()            { return Get_SC_StateSafe().y; }
        float Get_SC_StorminessValue()         { return Get_SC_StateSafe().z; }
        float Get_SC_SmoothStorminessValue()   { return Get_SC_StateSafe().w; }

        float Get_SC_StormDarkness() {
            return clamp(
                mix(Get_SC_StorminessValue(), Get_SC_SmoothStorminessValue(), 0.6),
                0.0, 1.0
            );
        }

        #ifndef SC_SC_STORM_DARK_DEFINED
            #define SC_SC_STORM_DARK_DEFINED
            float scStormDark = Get_SC_StormDarkness();
        #endif

        float Get_SC_ThicknessScale() {
            return mix(0.5, 1.5, Get_SC_ThicknessRaw());
        }

        vec4 Get_SC_TypeMask() {
            vec4 mask = clamp(sc_Type, 0.0, 1.0);
            float sum = mask.x + mask.y + mask.z + mask.w;
            if (sum <= 0.0001) return vec4(1.0, 0.0, 0.0, 0.0);
            return mask / sum;
        }

        float Apply_SC_TypeShading(float shadingValue) {
            float shading = clamp(shadingValue, 0.0, 2.0);
            vec4 mask = Get_SC_TypeMask();
            vec4 shadedVariants = vec4(
                pow(shading, 0.70),
                shading,
                pow(shading, 1.10),
                pow(shading, 1.35)
            );
            return dot(mask, shadedVariants);
        }
        float Get_SC_ShadowStrength() {
            // storm darkness is too weak — we need actual cloud optical depth
            float thick = Get_SC_ThicknessRaw();     // raw cloud thickness
            float storm = Get_SC_StormDarkness();    // smooth intensity

            // cloud coverage approximation
            float coverage = clamp(thick * 0.65 + storm * 0.45, 0.0, 1.0);

            // convert to ground shadow darkness
            float shadow = smoothstep(0.25, 0.85, coverage);

            return shadow * 0.85; // never full black
        }
        float Get_SC_FinalShadow() {
            // choose Java override if > 0
            if (sc_CloudShadowFactor > 0.0) {
                return clamp(sc_CloudShadowFactor, 0.0, 1.0);
            }

            // otherwise use shader-side estimation
            return Get_SC_ShadowStrength();
        }


#else

        #define Get_SC_VisibilityFactor()      0.0
        #define Get_SC_ThicknessRaw()          0.0
        #define Get_SC_StorminessValue()       0.0
        #define Get_SC_SmoothStorminessValue() 0.0
        #define Get_SC_StormDarkness()         0.0
        #define Get_SC_ThicknessScale()        1.0
        #define Apply_SC_TypeShading(x)        (x)
        float scStormDark = 0.0;

    #endif

#endif
