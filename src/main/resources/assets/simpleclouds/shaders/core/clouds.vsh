#version 430

in vec3 Position;

#moj_import <simpleclouds:opaque.glsl>

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;
uniform vec3 SunColor;
uniform float SunWarmth;
uniform float LightPower;
uniform float AmbientLight;
uniform vec3 DarknessColorModifier;
uniform bool UseNormals;
uniform float FogStart;
uniform float FogEnd;

out vec4 vertexColor;
out float fogDistance;
out vec3 viewSpacePos;
flat out vec3 faceNormal;

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

vec4 mixLight(vec3 lightDir0, vec3 lightDir1, vec3 normal, vec4 color, float fogDistance)
{
    lightDir0 = normalize(lightDir0);
    lightDir1 = normalize(lightDir1);

    float sunFacing = max(0.0, dot(lightDir0, normal));
    float fillFacing = max(0.0, dot(lightDir1, normal));
    float nearFactor = 1.0 - smoothstep(FogStart, FogEnd, fogDistance);
    float warmStrength = smooth01(SunWarmth) * mix(0.30, 0.92, nearFactor);

    float sunLuma = max(luminance(SunColor), 1.0E-4);
    vec3 sunTint = mix(vec3(1.0), SunColor / sunLuma, 0.72);

    float ambient = mix(AmbientLight, 0.72, warmStrength);
    float sunTerm = sunFacing * mix(LightPower, 0.82, warmStrength);
    float fillTerm = fillFacing * LightPower * mix(0.30, 0.16, warmStrength);
    float lightAccum = saturate(ambient + sunTerm + fillTerm);

    vec3 neutralLit = color.rgb * lightAccum;
    float tintStrength = warmStrength * mix(0.14, 0.44, sunFacing);
    vec3 finalLit = mix(neutralLit, neutralLit * sunTint, tintStrength);
    return vec4(finalLit, color.a);
}

void main()
{
    SideInfo info = sides.data[gl_InstanceID];

    vec4 transformedPos = vec4(Position, 1.0) * transformations[uint(info.side)];
    vec3 sideOffset = vec3(info.x, info.y, info.z);
    vec4 finalPos = vec4(transformedPos.xyz * info.radius + sideOffset, 1.0);
    vec4 modelPos = ModelViewMat * finalPos;

    gl_Position = ProjMat * modelPos;
    fogDistance = length(modelPos.xz);
    viewSpacePos = modelPos.xyz;
    faceNormal = normals[uint(info.side)];

    vec4 finalCol = vec4(mix(DarknessColorModifier, vec3(1.0), info.brightness), 1.0);
    if (UseNormals)
    {
        vertexColor = mixLight(Light0_Direction, Light1_Direction, faceNormal, finalCol, fogDistance);
    }
    else
    {
        vertexColor = finalCol;
    }
}
