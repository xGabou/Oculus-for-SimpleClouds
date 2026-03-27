#version 430

uniform sampler2D BayerMatrixSampler;

uniform vec4 ColorModulator;
uniform bool UseNormals;
uniform vec3 Light0_Direction;
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
        vec3 sunDir = normalize(Light0_Direction);
        float sunFacing = smooth01(saturate(dot(normal, sunDir)));
        float nearFactor = 1.0 - smoothstep(FogStart, FogEnd, fogDistance);
        float warmStrength = smooth01(SunWarmth) * mix(0.40, 1.0, nearFactor);
        float sunLuma = max(luminance(SunColor), 1.0E-4);
        vec3 sunTint = mix(vec3(1.0), SunColor / sunLuma, 0.88);
        float tintStrength = warmStrength * mix(0.20, 0.62, sunFacing);
        vec3 tintTarget = baseColor * sunTint * mix(1.0, 1.10, warmStrength);
        litColor = mix(baseColor, tintTarget, tintStrength);
    }

    vec4 color = vec4(litColor, vertexColor.a);
    color = mix(color, FogColor, smoothstep(FogStart, FogEnd, fogDistance));

    fragColor = vec4(color.rgb, 1.0);
}
