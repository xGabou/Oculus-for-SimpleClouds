#version 430

in vec3 Position;

#moj_import <simpleclouds:opaque.glsl>

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;
uniform float LightPower;
uniform float AmbientLight;
uniform vec3 DarknessColorModifier;
uniform bool UseNormals;

out vec4 vertexColor;
out float fogDistance;

const float MIN_VISIBLE_BRIGHTNESS = 0.07;
const vec3 MIN_DARKNESS_COLOR = vec3(0.055, 0.065, 0.090);

vec3 safeNormalize(vec3 value, vec3 fallback)
{
    float lengthSquared = dot(value, value);
    if (!(lengthSquared > 1.0E-8) || isnan(lengthSquared) || isinf(lengthSquared))
        return fallback;
    return value * inversesqrt(lengthSquared);
}

vec4 mixLight(vec3 lightDir0, vec3 lightDir1, vec3 normal, vec4 color)
{
    lightDir0 = safeNormalize(lightDir0, vec3(0.0, 1.0, 0.0));
    lightDir1 = safeNormalize(lightDir1, vec3(0.0, -1.0, 0.0));
    normal = safeNormalize(normal, vec3(0.0, 1.0, 0.0));
    float light0 = max(0.0, dot(lightDir0, normal));
    float light1 = max(0.0, dot(lightDir1, normal));
    float lightAccum = min(1.0, (light0 + light1) * LightPower + AmbientLight);
    return vec4(vec3(color.r * lightAccum, color.g * lightAccum, color.b), color.a);
}

void main()
{
    SideInfo info = sides.data[gl_InstanceID];

    int sideIndexInt = clamp(info.side, 0, 5);
    uint sideIndex = uint(sideIndexInt);
    float safeBrightness = info.brightness;
    if (isnan(safeBrightness) || isinf(safeBrightness))
        safeBrightness = 1.0;
    safeBrightness = mix(MIN_VISIBLE_BRIGHTNESS, 1.0, clamp(safeBrightness, 0.0, 1.0));

    vec4 transformedPos = vec4(Position, 1.0) * transformations[sideIndex];
    vec3 sideOffset = vec3(info.x, info.y, info.z);
    vec4 finalPos = vec4(transformedPos.xyz * info.radius + sideOffset, 1.0);
    gl_Position = ProjMat * ModelViewMat * finalPos;
    fogDistance = length((ModelViewMat * finalPos).xz);

    vec3 safeDarknessColor = max(DarknessColorModifier, MIN_DARKNESS_COLOR);
    vec4 finalCol = vec4(mix(safeDarknessColor, vec3(1.0), safeBrightness), 1.0);
    if (UseNormals)
    {
        vec3 normal = normals[sideIndex];
        vertexColor = mixLight(Light0_Direction, Light1_Direction, normal, finalCol);
    }
    else
    {
        vertexColor = finalCol;
    }
}
