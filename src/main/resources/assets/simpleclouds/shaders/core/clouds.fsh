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
in vec3 cloudLocalPos;
flat in vec3 faceNormal;

out vec4 fragColor;

const float OPAQUE_WARM_FLOOR = 0.18;
const float OPAQUE_WARM_STRENGTH = 0.65;
const float OPAQUE_WARM_TINT_BOOST = 1.35;
const float OPAQUE_VARIATION_SCALE = 0.014;
const float OPAQUE_SUN_BASE_MIN = 0.45;
const float OPAQUE_VARIATION_MIN = 0.80;
const float OPAQUE_EDGE_MIN = 0.90;
const float OPAQUE_BULK_MIN = 0.85;
const int OPAQUE_DEBUG_VIEW = 0;
// 0 = normal
// 1 = SunWarmth
// 2 = sunFacing
// 3 = silhouette
// 4 = variation
// 5 = interiorMask
// 6 = warmMask
// 7 = exaggerated warm effect

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

float hash12(vec2 p)
{
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}

float valueNoise(vec2 p)
{
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);

    float a = hash12(i);
    float b = hash12(i + vec2(1.0, 0.0));
    float c = hash12(i + vec2(0.0, 1.0));
    float d = hash12(i + vec2(1.0, 1.0));

    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

float warmVariation(vec3 position)
{
    vec2 uv0 = position.xz * OPAQUE_VARIATION_SCALE + position.yy * (OPAQUE_VARIATION_SCALE * 0.30);
    vec2 uv1 = position.xz * (OPAQUE_VARIATION_SCALE * 0.47) + vec2(17.3, -11.1);
    float n0 = valueNoise(uv0);
    float n1 = valueNoise(uv1);
    return mix(n0, n1, 0.35);
}

void main()
{
    float fade = ColorModulator.a;
    float r = texture(BayerMatrixSampler, gl_FragCoord.xy * DitherScale).r;
    if (fade < r)
        discard;

    vec3 baseColor = vertexColor.rgb * ColorModulator.rgb;
    vec3 litColor = baseColor;

    vec3 normal = normalize(faceNormal);
    vec3 sunDir = normalize(Light0_Direction);
    vec3 viewDir = normalize(-viewSpacePos);

    float nearFactor = 1.0 - smoothstep(FogStart, FogEnd, fogDistance);
    float warmStrength = smooth01(SunWarmth) * nearFactor;

    float sunDot = dot(normal, sunDir);
    float sunFacing = smoothstep(-0.05, 0.75, sunDot);

    float silhouette = 1.0 - abs(dot(normal, viewDir));
    silhouette = pow(saturate(silhouette), 1.35);

    float interiorMask = mix(0.55, 1.0, silhouette);
    float variationMask = warmVariation(cloudLocalPos);
    float variation = saturate(variationMask);

    float warmBase = mix(OPAQUE_SUN_BASE_MIN, 1.0, saturate(sunFacing));
    float warmVar = mix(OPAQUE_VARIATION_MIN, 1.0, variation);
    float warmEdge = mix(OPAQUE_EDGE_MIN, 1.0, silhouette);
    float warmBulk = mix(OPAQUE_BULK_MIN, 1.0, interiorMask);

    float warmMask = saturate(warmStrength * warmBase * warmVar * warmEdge * warmBulk);

    vec3 warmTint = mix(SunColor, vec3(1.0, 0.68, 0.50), 0.45);
    float warmAmount = clamp(OPAQUE_WARM_FLOOR + warmMask * OPAQUE_WARM_STRENGTH, 0.0, 1.0);

    if (OPAQUE_DEBUG_VIEW == 1)
    {
        fragColor = vec4(vec3(SunWarmth), 1.0);
        return;
    }
    if (OPAQUE_DEBUG_VIEW == 2)
    {
        fragColor = vec4(vec3(sunFacing), 1.0);
        return;
    }
    if (OPAQUE_DEBUG_VIEW == 3)
    {
        fragColor = vec4(vec3(silhouette), 1.0);
        return;
    }
    if (OPAQUE_DEBUG_VIEW == 4)
    {
        fragColor = vec4(vec3(variationMask), 1.0);
        return;
    }
    if (OPAQUE_DEBUG_VIEW == 5)
    {
        fragColor = vec4(vec3(interiorMask), 1.0);
        return;
    }
    if (OPAQUE_DEBUG_VIEW == 6)
    {
        fragColor = vec4(vec3(warmMask), 1.0);
        return;
    }
    if (OPAQUE_DEBUG_VIEW == 7)
    {
        fragColor = vec4(baseColor + vec3(1.0, 0.4, 0.15) * 2.0, 1.0);
        return;
    }

    litColor = mix(baseColor, baseColor * warmTint * OPAQUE_WARM_TINT_BOOST, warmAmount);

    vec4 color = vec4(litColor, vertexColor.a);
    color = mix(color, FogColor, smoothstep(FogStart, FogEnd, fogDistance));

    fragColor = vec4(color.rgb, 1.0);
}
