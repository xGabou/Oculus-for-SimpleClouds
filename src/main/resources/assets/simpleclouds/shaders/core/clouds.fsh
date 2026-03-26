#version 430

uniform sampler2D BayerMatrixSampler;

uniform vec4 ColorModulator;
uniform bool UseNormals;
uniform vec3 SunDirection;
uniform vec3 SunColor;
uniform float SunWarmth;
uniform float DitherScale;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in vec4 vertexColor;
in float fogDistance;
in vec3 viewSpacePos;
flat in vec3 faceNormal;

out vec4 fragColor;

float saturate(float value)
{
    return clamp(value, 0.0, 1.0);
}

float smooth01(float value)
{
    float clamped = saturate(value);
    return clamped * clamped * (3.0 - 2.0 * clamped);
}

float luminance(vec3 color)
{
    return dot(color, vec3(0.2126, 0.7152, 0.0722));
}

void main()
{
    float fade = ColorModulator.a;
    float r = texture(BayerMatrixSampler, gl_FragCoord.xy * DitherScale).r;
    if (fade < r)
        discard;

    vec3 baseColor = vertexColor.rgb * ColorModulator.rgb;
    vec3 litColor = baseColor;

    if (UseNormals)
    {
        vec3 normal = normalize(faceNormal);
        vec3 sunDir = normalize(SunDirection);
        float sunFacing = smooth01(saturate(dot(normal, sunDir)));
        float nearFactor = 1.0 - smoothstep(FogStart, FogEnd, fogDistance);

        float sunLuma = max(luminance(SunColor), 1.0E-4);
        vec3 sunTint = mix(vec3(1.0), SunColor / sunLuma, 0.90);
        float sunColorWarmth = saturate(
            max(sunTint.r - sunTint.b, 0.0)
            + 0.35 * max(sunTint.g - sunTint.b, 0.0)
            + 0.20 * max(sunTint.r - sunTint.g, 0.0)
        );
        float atmosphericStrength = smooth01(saturate(max(SunWarmth * 1.35, sunColorWarmth)));
        float sunInfluence = saturate(sunFacing * atmosphericStrength * mix(0.28, 1.15, nearFactor));

        float baseLuma = luminance(baseColor);
        vec3 warmTarget = vec3(baseLuma) * sunTint * mix(1.02, 1.22, atmosphericStrength);
        vec3 sunTarget = mix(baseColor, warmTarget, 0.92);
        float brightnessBoost = 1.0 + sunFacing * atmosphericStrength * mix(0.06, 0.16, nearFactor);
        litColor = mix(baseColor, sunTarget * brightnessBoost, sunInfluence);
    }

    vec4 color = vec4(litColor, vertexColor.a);
    color = mix(color, FogColor, smoothstep(FogStart, FogEnd, fogDistance));

    fragColor = vec4(color.rgb, 1.0);
}
