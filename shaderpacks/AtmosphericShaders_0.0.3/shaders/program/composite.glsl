/////////////////////////////////////
// Complementary Shaders by EminGT //
/////////////////////////////////////

//Common//
#include "/lib/common.glsl"

//////////Fragment Shader//////////Fragment Shader//////////Fragment Shader//////////
#ifdef FRAGMENT_SHADER

noperspective in vec2 texCoord;

flat in vec3 upVec, sunVec;

#ifdef LIGHTSHAFTS_ACTIVE
    flat in float vlFactor;
#endif

//Pipeline Constants//

//Common Variables//
float SdotU = dot(sunVec, upVec);
float sunFactor = SdotU < 0.0 ? clamp(SdotU + 0.375, 0.0, 0.75) / 0.75 : clamp(SdotU + 0.03125, 0.0, 0.0625) / 0.0625;
float sunVisibility = clamp(SdotU + 0.0625, 0.0, 0.125) / 0.125;
float sunVisibility2 = sunVisibility * sunVisibility;

vec2 view = vec2(viewWidth, viewHeight);

#ifdef OVERWORLD
    vec3 lightVec = sunVec * ((timeAngle < 0.5325 || timeAngle > 0.9675) ? 1.0 : -1.0);
#else
    vec3 lightVec = sunVec;
#endif

#ifdef LIGHTSHAFTS_ACTIVE
    float shadowTimeVar1 = abs(sunVisibility - 0.5) * 2.0;
    float shadowTimeVar2 = shadowTimeVar1 * shadowTimeVar1;
    float shadowTime = shadowTimeVar2 * shadowTimeVar2;
    float vlTime = min(abs(SdotU) - 0.05, 0.15) / 0.15;
#endif

//Common Functions//

//Includes//
#include "/lib/atmospherics/fog/waterFog.glsl"
#include "/lib/atmospherics/fog/caveFactor.glsl"
#include "/lib/util/spaceConversion.glsl"
#include "/lib/atmospherics/atmoCommon.glsl"
#include "/lib/atmospherics/rain/rainCommon.glsl"
#include "/lib/atmospherics/rain/rainSplashes.glsl"
#include "/lib/atmospherics/rain/wetSurface.glsl"

#ifdef BLOOM_FOG_COMPOSITE
    #include "/lib/atmospherics/fog/bloomFog.glsl"
#endif

#ifdef LIGHTSHAFTS_ACTIVE
    #ifdef END
        #include "/lib/atmospherics/enderBeams.glsl"
    #endif
    #include "/lib/atmospherics/volumetricLight.glsl"
#endif

#if WATER_MAT_QUALITY >= 3
    #include "/lib/materials/materialMethods/refraction.glsl"
#endif

#ifdef NETHER_STORM
    #include "/lib/atmospherics/netherStorm.glsl"
#endif

#ifdef ATM_COLOR_MULTS
    #include "/lib/colors/colorMultipliers.glsl"
#endif
#ifdef MOON_PHASE_INF_ATMOSPHERE
    #include "/lib/colors/moonPhaseInfluence.glsl"
#endif

#if RAINBOWS > 0 && defined OVERWORLD
    #include "/lib/atmospherics/rainbow.glsl"
#endif

#ifdef COLORED_LIGHT_FOG
    #include "/lib/misc/voxelization.glsl"
    #include "/lib/atmospherics/fog/coloredLightFog.glsl"
#endif

//Program//
void main() {
    vec2 baseUV = texCoord;
    vec3 baseColor = texelFetch(colortex0, texelCoord, 0).rgb;
    vec3 color = baseColor;
    #if ATM_STORM_BOOST == 1
        float stormActivity = GetStormActivity();
        if (stormActivity > 0.05) {
            vec2 wind = normalize(GetAtmosphericWindVector() + vec2(0.0001));
            vec2 shake = wind * 0.0008 * stormActivity;
            float shakePhase = frameTimeCounter * (0.35 + 0.65 * stormActivity);
            shake += vec2(sin(shakePhase), cos(shakePhase * 1.7)) * 0.0005 * stormActivity;
            vec2 shakenCoord = clamp(baseUV + shake, vec2(0.001), vec2(0.999));
            vec3 shakenColor = texture2D(colortex0, shakenCoord).rgb;
            color = mix(baseColor, shakenColor, clamp(stormActivity * 0.35, 0.0, 0.45));
        }
    #endif
    float z0 = texelFetch(depthtex0, texelCoord, 0).r;
    float z1 = texelFetch(depthtex1, texelCoord, 0).r;

    vec4 screenPos = vec4(texCoord, z0, 1.0);
    vec4 viewPos = gbufferProjectionInverse * (screenPos * 2.0 - 1.0);
    viewPos /= viewPos.w;
    float lViewPos = length(viewPos.xyz);
    vec3 worldPos0 = ViewToPlayer(viewPos.xyz);

    #if defined DISTANT_HORIZONS && !defined OVERWORLD
        float z0DH = texelFetch(dhDepthTex, texelCoord, 0).r;
        vec4 screenPosDH = vec4(texCoord, z0DH, 1.0);
        vec4 viewPosDH = dhProjectionInverse * (screenPosDH * 2.0 - 1.0);
        viewPosDH /= viewPosDH.w;
        lViewPos = min(lViewPos, length(viewPosDH.xyz));
    #endif

    float dither = texture2D(noisetex, texCoord * view / 128.0).b;
    #ifdef TAA
        dither = fract(dither + goldenRatio * mod(float(frameCounter), 3600.0));
    #endif

    /* TM5723: The "1.0 - translucentMult" trick is done because of the default color attachment
    value being vec3(0.0). This makes it vec3(1.0) to avoid issues especially on improved glass */
    vec3 translucentMult = 1.0 - texelFetch(colortex3, texelCoord, 0).rgb; //TM5723
    vec4 volumetricEffect = vec4(0.0);

    #if WATER_MAT_QUALITY >= 3
        DoRefraction(color, z0, z1, viewPos.xyz, lViewPos);
    #endif

    vec4 screenPos1 = vec4(texCoord, z1, 1.0);
    vec4 viewPos1 = gbufferProjectionInverse * (screenPos1 * 2.0 - 1.0);
    viewPos1 /= viewPos1.w;
    float lViewPos1 = length(viewPos1.xyz);

    #if defined DISTANT_HORIZONS && !defined OVERWORLD
        float z1DH = texelFetch(dhDepthTex1, texelCoord, 0).r;
        vec4 screenPos1DH = vec4(texCoord, z1DH, 1.0);
        vec4 viewPos1DH = dhProjectionInverse * (screenPos1DH * 2.0 - 1.0);
        viewPos1DH /= viewPos1DH.w;
        lViewPos1 = min(lViewPos1, length(viewPos1DH.xyz));
    #endif

    #if defined LIGHTSHAFTS_ACTIVE || RAINBOWS > 0 && defined OVERWORLD
        vec3 nViewPos = normalize(viewPos1.xyz);
        float VdotL = dot(nViewPos, lightVec);
    #endif

    #if defined NETHER_STORM || defined COLORED_LIGHT_FOG
        vec3 playerPos = ViewToPlayer(viewPos1.xyz);
        vec3 nPlayerPos = normalize(playerPos);
    #endif

    #if RAINBOWS > 0 && defined OVERWORLD
        if (isEyeInWater == 0) color += GetRainbow(translucentMult, z0, z1, lViewPos, lViewPos1, VdotL, dither);
    #endif

    #ifdef LIGHTSHAFTS_ACTIVE
        float vlFactorM = vlFactor;
        float VdotU = dot(nViewPos, upVec);

        volumetricEffect = GetVolumetricLight(color, vlFactorM, translucentMult, lViewPos, lViewPos1, nViewPos, VdotL, VdotU, texCoord, z0, z1, dither);
    #endif

    #ifdef NETHER_STORM
        volumetricEffect = GetNetherStorm(color, translucentMult, nPlayerPos, playerPos, lViewPos, lViewPos1, dither);
    #endif

    #ifdef ATM_COLOR_MULTS
        volumetricEffect.rgb *= GetAtmColorMult();
    #endif
    #ifdef MOON_PHASE_INF_ATMOSPHERE
        volumetricEffect.rgb *= moonPhaseInfluence;
    #endif

    #ifdef NETHER_STORM
        color = mix(color, volumetricEffect.rgb, volumetricEffect.a);
    #endif

    #ifdef COLORED_LIGHT_FOG
        vec3 lightFog = GetColoredLightFog(nPlayerPos, translucentMult, lViewPos, lViewPos1, dither);
        float lightFogMult = COLORED_LIGHT_FOG_I;
        //if (heldItemId == 40000 && heldItemId2 != 40000) lightFogMult = 0.0; // Hold spider eye to disable light fog

        #ifdef OVERWORLD
            lightFogMult *= 0.2 + 0.6 * mix(1.0, 1.0 - sunFactor * invRainFactor, eyeBrightnessM);
        #endif
    #endif

    if (isEyeInWater == 1) {
        if (z0 == 1.0) color.rgb = waterFogColor;

        vec3 underwaterMult = vec3(0.80, 0.87, 0.97);
        color.rgb *= underwaterMult * 0.85;
        volumetricEffect.rgb *= pow2(underwaterMult * 0.71);

    #ifdef COLORED_LIGHT_FOG
        lightFog *= underwaterMult;
    #endif
    } else if (isEyeInWater == 2) {
        if (z1 == 1.0) color.rgb = fogColor * 5.0;

        volumetricEffect.rgb *= 0.0;
        #ifdef COLORED_LIGHT_FOG
            lightFog *= 0.0;
        #endif
    }

    #ifdef COLORED_LIGHT_FOG
        color /= 1.0 + pow2(GetLuminance(lightFog)) * lightFogMult * 2.0;
        color += lightFog * lightFogMult * 0.5;
    #endif

    ApplyRainSplashes(color, texCoord, viewPos.xyz, worldPos0, z0);
    #if ATM_STORM_BOOST == 1
        ApplyWetSurfaceOverlay(color, texCoord, viewPos.xyz, worldPos0, z0, lViewPos, sunFactor);
    #endif

    #ifdef OVERWORLD
    #if ATM_COLOR_GRADE == 1
    {
        vec3 viewDir = normalize(viewPos.xyz);
        float horizonMask = smoothstep(0.02, 0.45, 1.0 - abs(viewDir.y));
        if (horizonMask > 0.0001) {

            float stormMask;

            #if USE_SC
                stormMask = clamp(Get_SC_StormDarkness(), 0.0, 1.0);
            #else
                stormMask = clamp(rainFactor, 0.0, 1.0); // fallback when SC is off
            #endif

            float absoluteHeight = worldPos0.y + cameraPosition.y;

            float heightFade = AtmosphericHeightFade(
                max(absoluteHeight, 0.0),
                0.008,
                1.0 + 0.6 * stormMask
            );

            float invRain = 1.0 - clamp(rainFactor, 0.0, 1.0);
            float dayBlend = smoothstep(-0.2, 0.2, SdotU);
            float nightBlend = 1.0 - dayBlend;
            float sunriseBlend = smoothstep(0.35, 0.95, 1.0 - abs(SdotU)) * dayBlend;

            vec3 warmTone  = vec3(0.85, 0.46, 0.28);
            vec3 coolTone  = vec3(0.16, 0.24, 0.40);
            vec3 stormTone = vec3(0.22, 0.28, 0.32);

            vec3 blendedTone = warmTone * sunriseBlend + coolTone * nightBlend;
            blendedTone = mix(blendedTone, stormTone, stormMask);

            float horizonWeight = horizonMask * heightFade * (0.35 + 0.4 * invRain);
            horizonWeight *= mix(1.0, 1.65, stormMask);

            color = mix(color, color + blendedTone * horizonWeight, horizonWeight);
        }

    }
    #endif
#endif

#if ATM_COLOR_GRADE == 1
        float altitude = cameraPosition.y + eyeAltitude;
        float altitudeNorm = clamp((altitude + 32.0) / 256.0, 0.0, 1.0);
        vec3 lowTone = vec3(1.02, 0.99, 0.96);
        vec3 highTone = vec3(0.9, 0.96, 1.05);
        vec3 tone = mix(lowTone, highTone, altitudeNorm);
        color *= tone;

        vec2 chromaOffset = (texCoord - 0.5) * 0.0009;
        float highlightMask = smoothstep(1.2, 4.0, max(color.r, max(color.g, color.b)));
        if (highlightMask > 0.0001) {
            vec3 chromaColor;
            chromaColor.r = texture2D(colortex0, clamp(texCoord + chromaOffset, vec2(0.001), vec2(0.999))).r;
            chromaColor.g = color.g;
            chromaColor.b = texture2D(colortex0, clamp(texCoord - chromaOffset, vec2(0.001), vec2(0.999))).b;
            color = mix(color, chromaColor, highlightMask * 0.15);
        }

        if (z0 == 1.0) {
            float thicknessFade = AtmosphericHeightFade(max(worldPos0.y + cameraPosition.y, 0.0), 0.01, 1.0 + 0.6 * GetStormActivity());
            color = mix(color, color * thicknessFade, 0.2);
        }
    #endif

    color = pow(color, vec3(2.2));
    #if USE_SC
    {
        float scStorm    = clamp(Get_SC_SmoothStorminessValue(), 0.0, 1.0);
        float scThick    = clamp(Get_SC_ThicknessRaw(), 0.0, 1.0);
        float scCoverage = clamp(max(scStorm, scThick), 0.0, 1.0);
        float scCurve    = smoothstep(0.12, 0.75, scCoverage);

        float scExposure = mix(1.0, 0.28, scCurve);
        color *= scExposure;

        // Kill any remaining sun shafts once thick storms roll in
        volumetricEffect.rgb *= 1.0 - scCurve;
    }
    #endif

    #ifdef LIGHTSHAFTS_ACTIVE
        #ifdef END
            volumetricEffect.rgb *= volumetricEffect.rgb;
        #endif

        color += volumetricEffect.rgb;
    #endif

    #if ATM_ENV_POLISH == 1
        if (rainFactor > 0.35) {
            vec2 dripCoord = texCoord * vec2(viewWidth, viewHeight) / 128.0 + frameTimeCounter * 0.02;
            float dripNoise = texture2D(noisetex, dripCoord).r;
            float dripPulse = texture2D(noisetex, dripCoord * 2.0).g;
            float droplet = smoothstep(0.55, 0.95, dripNoise + 0.3 * dripPulse);
            color *= 1.0 - droplet * 0.02;
        }

        float heatMask = smoothstep(0.65, 1.0, inDry) * smoothstep(0.4, 0.9, sunFactor) * (1.0 - rainFactor);
        if (heatMask > 0.0) {
            vec2 hazeOffset = vec2(
                sin(frameTimeCounter * 0.8 + texCoord.y * 120.0),
                cos(frameTimeCounter * 0.6 + texCoord.x * 90.0)
            ) * 0.0008 * heatMask;
            vec3 hazeColor = texture2D(colortex0, clamp(texCoord + hazeOffset, vec2(0.001), vec2(0.999))).rgb;
            color = mix(color, hazeColor, heatMask * 0.2);
        }

        float nightSilhouette = smoothstep(0.0, 0.08, 0.08 - sunVisibility);
        color *= mix(1.0, 0.82, nightSilhouette);
    #endif

    #if ATM_STORM_BOOST == 1
        float stormDark = GetStormActivity();
        if (stormDark > 0.05 && z0 == 1.0) {
            float lowBand = 1.0 - smoothstep(0.08, 0.35, texCoord.y);
            float darkness = stormDark * lowBand * 0.4;
            color *= 1.0 - darkness * 0.5;
            volumetricEffect.rgb *= 1.0 + darkness;
        }
    #endif

    float lightningPresence = step(0.5, lightningBoltPosition.w);
    if (lightningPresence > 0.5) {
        vec3 worldSample = cameraPosition + worldPos0;
        float lightningDistance = length(lightningBoltPosition.xyz - worldSample);
        float lightningReach = 1.0 - smoothstep(40.0, 220.0, lightningDistance);
        float lightningFlash = lightningPresence * lightningReach * smoothstep(0.3, 0.95, rainFactor + 0.15);
        vec3 flashTint = vec3(0.6, 0.7, 0.85);
        if (z0 == 1.0) {
            color += flashTint * lightningFlash;
        }
        float groundFade = exp(-lViewPos * 0.02);
        color += flashTint * 0.35 * lightningFlash * groundFade;
        volumetricEffect.rgb += flashTint * lightningFlash * 0.35;
    }

    #ifdef BLOOM_FOG_COMPOSITE
        color *= GetBloomFog(lViewPos); // Reminder: Bloom Fog can move between composite1-2-3
    #endif

    /* DRAWBUFFERS:0 */
    gl_FragData[0] = vec4(color, 1.0);

    // supposed to be #if defined LIGHTSHAFTS_ACTIVE && (LIGHTSHAFT_BEHAVIOUR == 1 && SHADOW_QUALITY >= 1 || defined END)
    #if LIGHTSHAFT_QUALI_DEFINE > 0 && LIGHTSHAFT_BEHAVIOUR == 1 && SHADOW_QUALITY >= 1 && defined OVERWORLD || defined END
        #if LENSFLARE_MODE > 0 || defined ENTITY_TAA_NOISY_CLOUD_FIX
            if (viewWidth + viewHeight - gl_FragCoord.x - gl_FragCoord.y > 1.5)
                vlFactorM = texelFetch(colortex4, texelCoord, 0).r;
        #endif

        /* DRAWBUFFERS:04 */
        gl_FragData[1] = vec4(vlFactorM, 0.0, 0.0, 1.0);
    #endif
}

#endif

//////////Vertex Shader//////////Vertex Shader//////////Vertex Shader//////////
#ifdef VERTEX_SHADER

noperspective out vec2 texCoord;

flat out vec3 upVec, sunVec;

#ifdef LIGHTSHAFTS_ACTIVE
    flat out float vlFactor;
#endif

//Attributes//

//Common Variables//

//Common Functions//

//Includes//

//Program//
void main() {
    gl_Position = ftransform();

    texCoord = (gl_TextureMatrix[0] * gl_MultiTexCoord0).xy;

    upVec = normalize(gbufferModelView[1].xyz);
    sunVec = GetSunVector();

    #ifdef LIGHTSHAFTS_ACTIVE
        #if LIGHTSHAFT_BEHAVIOUR == 1 && SHADOW_QUALITY >= 1 || defined END
            vlFactor = texelFetch(colortex4, ivec2(viewWidth-1, viewHeight-1), 0).r;
        #else
            #if LIGHTSHAFT_BEHAVIOUR == 2
                vlFactor = 0.0;
            #elif LIGHTSHAFT_BEHAVIOUR == 3
                vlFactor = 1.0;
            #endif
        #endif
    #endif
}

#endif
