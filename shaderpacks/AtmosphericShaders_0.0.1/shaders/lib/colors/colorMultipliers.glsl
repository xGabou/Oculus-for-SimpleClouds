#ifndef INCLUDE_LIGHT_AND_AMBIENT_MULTIPLIERS
#define INCLUDE_LIGHT_AND_AMBIENT_MULTIPLIERS

vec3 GetLightColorMult() {
    vec3 baseMult;

    #ifdef OVERWORLD
        vec3 morning = vec3(LIGHT_MORNING_R, LIGHT_MORNING_G, LIGHT_MORNING_B) * LIGHT_MORNING_I;
        vec3 noon    = vec3(LIGHT_NOON_R,    LIGHT_NOON_G,    LIGHT_NOON_B   ) * LIGHT_NOON_I;
        vec3 night   = vec3(LIGHT_NIGHT_R,   LIGHT_NIGHT_G,   LIGHT_NIGHT_B  ) * LIGHT_NIGHT_I;
        vec3 rain    = vec3(LIGHT_RAIN_R,    LIGHT_RAIN_G,    LIGHT_RAIN_B   ) * LIGHT_RAIN_I;

        baseMult = mix(noon, morning, invNoonFactor2);
        baseMult = mix(night, baseMult, sunVisibility2);

        // Default rain tint
        vec3 rainTint = dot(baseMult, vec3(0.33333)) * rain;
        baseMult = mix(baseMult, rainTint, rainFactor);

        #if USE_SC
            #if defined GBUFFERS_ENTITIES || defined GBUFFERS_HAND || defined GBUFFERS_TEXTURED
                // Entities keep their original multipliers to avoid "dark mode".
            #else
                float storm     = clamp(Get_SC_StormDarkness(), 0.0, 1.0);
                float thick     = clamp(Get_SC_ThicknessRaw(), 0.0, 1.0);

                float stormNorm = clamp(storm / 0.6, 0.0, 1.0);
                float stormC    = pow(stormNorm, 1.45);

                float multDim   = mix(1.0, 0.32, stormC);
                float thickDim  = mix(1.0, 0.70, thick);

                baseMult = baseMult * multDim * thickDim;
            #endif
        #endif

        return baseMult;

    #elif defined NETHER
        return vec3(LIGHT_NETHER_R, LIGHT_NETHER_G, LIGHT_NETHER_B) * LIGHT_NETHER_I;

    #elif defined END
        return vec3(LIGHT_END_R, LIGHT_END_G, LIGHT_END_B) * LIGHT_END_I;
    #endif
}

vec3 GetAtmColorMult() {
    vec3 baseMult;

    #ifdef OVERWORLD
        vec3 morning = vec3(ATM_MORNING_R, ATM_MORNING_G, ATM_MORNING_B) * ATM_MORNING_I;
        vec3 noon    = vec3(ATM_NOON_R,    ATM_NOON_G,    ATM_NOON_B   ) * ATM_NOON_I;
        vec3 night   = vec3(ATM_NIGHT_R,   ATM_NIGHT_G,   ATM_NIGHT_B  ) * ATM_NIGHT_I;
        vec3 rain    = vec3(ATM_RAIN_R,    ATM_RAIN_G,    ATM_RAIN_B   ) * ATM_RAIN_I;

        baseMult = mix(noon, morning, invNoonFactor2);
        baseMult = mix(night, baseMult, sunVisibility2);

        vec3 rainTint = dot(baseMult, vec3(0.33333)) * rain;
        baseMult = mix(baseMult, rainTint, rainFactor);

        #if USE_SC
            #if defined GBUFFERS_ENTITIES || defined GBUFFERS_HAND || defined GBUFFERS_TEXTURED
                // Keep ambient tint intact for entity-style passes.
            #else
                float storm     = clamp(Get_SC_StormDarkness(), 0.0, 1.0);
                float thick     = clamp(Get_SC_ThicknessRaw(), 0.0, 1.0);

                float stormNorm = clamp(storm / 0.6, 0.0, 1.0);
                float stormC    = pow(stormNorm, 1.25);

                float multDim   = mix(1.0, 0.45, stormC);
                float thickDim  = mix(1.0, 0.80, thick);

                baseMult = baseMult * multDim * thickDim;
            #endif
        #endif

        return baseMult;

    #elif defined NETHER
        return vec3(ATM_NETHER_R, ATM_NETHER_G, ATM_NETHER_B) * ATM_NETHER_I;

    #elif defined END
        return vec3(ATM_END_R, ATM_END_G, ATM_END_B) * ATM_END_I;
    #endif
}

vec3 lightColorMult;
vec3 atmColorMult;
vec3 sqrtAtmColorMult;

#endif
