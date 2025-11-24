#ifndef INCLUDE_WATER_FOG
#include "/lib/util/sc_bridge.glsl"
    #define INCLUDE_WATER_FOG
    
    float GetWaterFog(float lViewPos) {
        #if WATER_FOG_MULT != 100
            #define WATER_FOG_MULT_M WATER_FOG_MULT * 0.01;
            lViewPos *= WATER_FOG_MULT_M;
        #endif

        #if LIGHTSHAFT_QUALI > 0 && SHADOW_QUALITY > -1
            float fog = lViewPos / 48.0;
            fog *= fog;
        #else
            float fog = lViewPos / 32.0;
        #endif

        float result = 1.0 - exp(-fog);

        #if USE_SC
        {
            float scDark  = mix(1.0, 0.40, Get_SC_StormDarkness());
            float scThick = mix(1.0, 0.70, Get_SC_ThicknessRaw());
            result *= scDark * scThick;
        }
        #endif
    return result;
    }
#endif