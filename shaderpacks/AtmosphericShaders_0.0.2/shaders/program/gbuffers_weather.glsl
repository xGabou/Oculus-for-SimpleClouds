/////////////////////////////////////
// Complementary Shaders by EminGT //
/////////////////////////////////////

//Common//
#include "/lib/common.glsl"
#include "/lib/atmospherics/rain/rainCommon.glsl"

//////////Fragment Shader//////////Fragment Shader//////////Fragment Shader//////////
#ifdef FRAGMENT_SHADER

flat in vec2 lmCoord;
in vec2 texCoord;

flat in vec3 upVec, sunVec;

flat in vec4 glColor;
flat in vec2 rainWindDir;

//Pipeline Constants//

//Common Variables//
float SdotU = dot(sunVec, upVec);
float sunFactor = SdotU < 0.0 ? clamp(SdotU + 0.375, 0.0, 0.75) / 0.75 : clamp(SdotU + 0.03125, 0.0, 0.0625) / 0.0625;
float sunVisibility = clamp(SdotU + 0.0625, 0.0, 0.125) / 0.125;
float sunVisibility2 = sunVisibility * sunVisibility;
float farMinusNear = far - near;
vec2 view = vec2(viewWidth, viewHeight);

//Common Functions//
float GetLinearDepth(float depth) {
    return (2.0 * near) / (far + near - depth * farMinusNear);
}

//Includes//
#include "/lib/colors/lightAndAmbientColors.glsl"

#ifdef COLOR_CODED_PROGRAMS
    #include "/lib/misc/colorCodedPrograms.glsl"
#endif

//Program//
void main() {
    vec2 sampleCoord = texCoord;
    float rainActivity = clamp(rainFactor + 0.35 * wetness, 0.0, 1.25);
    #if ATM_STORM_BOOST == 1
        float stormActivity = GetStormActivity();
        rainActivity = max(rainActivity, stormActivity * 1.2);
    #endif
    if (rainActivity > 0.0) {
        float jitterA = Hash12(gl_FragCoord.xy);
        float jitterB = Hash12(gl_FragCoord.yx + frameTimeCounter * 0.25);
        vec2 windDir = normalize(vec2(rainWindDir));
        vec2 perpDir = Rotate90(windDir);
        vec2 microOffset = (windDir * (jitterA - 0.5) + perpDir * (jitterB - 0.5));
        microOffset *= mix(0.01, 0.04, rainActivity);
        #if ATM_STORM_BOOST == 1
            float stormJitter = GetStormActivity();
            microOffset *= mix(1.0, 1.85, stormJitter);
            microOffset += windDir * 0.01 * stormJitter;
        #endif
        sampleCoord += microOffset;
    }

    vec4 color = texture2D(tex, sampleCoord);
    bool isSnow = color.r + color.g >= 1.5;
    if (isSnow && rainActivity > 0.0) {
        color = texture2D(tex, texCoord);
    }
    color *= glColor;

    if (color.a < 0.1 || isEyeInWater == 3) discard;

    if (isSnow) color.a *= snowTexOpacity;
    else {
        color.a *= rainTexOpacity;
        #if ATM_STORM_BOOST == 1
            float stormBoost = GetStormActivity();
            color.a *= mix(1.0, 1.45, stormBoost);
        #endif

        float velNoise = fract(Hash12(gl_FragCoord.yx * 0.75) + frameTimeCounter * 0.65);
        float thickNoise = Hash12(gl_FragCoord.xy * 0.5 + frameTimeCounter * 0.2);
        color.a *= mix(0.75, 1.25, thickNoise);
        color.rgb *= mix(0.85, 1.1, velNoise);

        vec2 screenUV = gl_FragCoord.xy / view;
        float sceneDepth = texture2D(depthtex0, screenUV).r;
        if (sceneDepth < 0.9999) {
            float rainLinear = GetLinearDepth(gl_FragCoord.z);
            float sceneLinear = GetLinearDepth(sceneDepth);
            float depthDelta = abs(sceneLinear - rainLinear) * farMinusNear;
            float proximity = exp(-depthDelta * 0.6);
            float depthShade = mix(1.0, 0.45, proximity);
            color.rgb *= depthShade;
        }
    }

    vec3 skyTone = mix(vec3(1.0), skyColor * 1.1, 0.4 * (1.0 - rainFactor));
    vec3 lightRain = (blocklightCol * 2.0 * lmCoord.x
               + (ambientColor + 0.2 * lightColor) * lmCoord.y * (0.6 + 0.3 * sunFactor));
    lightRain *= mix(vec3(1.0), skyTone, 0.5 * (1.0 - snowTexOpacity));
    
    #ifdef USE_SC
    {
        float storm = clamp(Get_SC_StormDarkness(), 0.0, 1.0);
        float thick = clamp(Get_SC_ThicknessRaw(), 0.0, 1.0);

        // 0 = clear sky, 0.6 = cumulonimbus reference
        float stormNorm  = clamp(storm / 0.6, 0.0, 1.0);
        float stormCurve = pow(stormNorm, 1.4);

        // Darken rain lighting
        float scMult = mix(1.0, 0.35, stormCurve);

        // Add a very mild thickness fading (prevent pure-black)
        scMult *= mix(1.0, 0.75, thick);
        lightRain *= scMult;

        float scOpacity = mix(1.0, 2.2, stormCurve);
        scOpacity *= mix(1.0, 1.25, thick);
        color.a = clamp(color.a * scOpacity + stormCurve * 0.15, 0.0, 1.0);

        // Hard-cap opacity during intense storms so sun discs can't show through
        if (stormCurve > 0.55 || rainFactor > 0.85) {
            color.a = 1.0;
        }

    }
    #endif


    color.rgb = sqrt3(color.rgb) * lightRain;

    #ifdef COLOR_CODED_PROGRAMS
        ColorCodeProgram(color, -1);
    #endif

    /* DRAWBUFFERS:0 */
    gl_FragData[0] = color;
}

#endif

//////////Vertex Shader//////////Vertex Shader//////////Vertex Shader//////////
#ifdef VERTEX_SHADER

flat out vec2 lmCoord;
out vec2 texCoord;

flat out vec3 upVec, sunVec;

flat out vec4 glColor;

//Attributes//

//Common Variables//

//Common Functions//

//Includes//

//Program//
void main() {
    vec4 position = gbufferModelViewInverse * gl_ModelViewMatrix * gl_Vertex;
    glColor = gl_Color;

    vec2 windDir = GetAtmosphericWindDir();
    rainWindDir = windDir;

    float rainActivity = clamp(rainFactor + 0.35 * wetness, 0.0, 1.25);
    #if ATM_STORM_BOOST == 1
        rainActivity = max(rainActivity, GetStormActivity());
    #endif
    if (rainActivity > 0.0) {
        float gust = mix(0.7, 1.3, Hash12(position.xz * 0.25 + frameTimeCounter * 0.15));
        float bendMagnitude = mix(0.008, 0.045, rainActivity) * gust;
        float heightMask = pow(clamp(gl_MultiTexCoord0.t, 0.0, 1.0), 1.25);
        float shearProfile = mix(1.0, 6.0, heightMask);
        #if ATM_STORM_BOOST == 1
            bendMagnitude *= GetStormStretchFactor();
        #endif
        position.xz += windDir * bendMagnitude * shearProfile;
    }

    #ifdef WAVING_RAIN
        float rainWavingFactor = eyeBrightnessM2; // Prevents clipping inside interiors
        position.xz += rainWavingFactor * (0.4 * position.y + 0.2) * vec2(sin(frameTimeCounter * 0.3) + 0.5, sin(frameTimeCounter * 0.5) * 0.5);
        position.xz *= 1.0 - 0.08 * position.y * rainWavingFactor;
    #endif

    gl_Position = gl_ProjectionMatrix * gbufferModelView * position;

    texCoord = (gl_TextureMatrix[0] * gl_MultiTexCoord0).xy;
    lmCoord  = GetLightMapCoordinates();

    upVec = normalize(gbufferModelView[1].xyz);
    sunVec = GetSunVector();
}

#endif
