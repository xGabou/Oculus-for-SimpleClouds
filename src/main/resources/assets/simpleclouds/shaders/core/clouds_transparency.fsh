//https://jcgt.org/published/0002/02/09/paper.pdf and http://casual-effects.blogspot.com/2015/03/implemented-weighted-blended-order.html

#version 430

uniform sampler2D BayerMatrixSampler;

uniform vec4 ColorModulator;
uniform vec3 SunColor;
uniform float SunWarmth;
uniform float DitherScale;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in vec4 vertexColor;
in float fogDistance;
in float vertexDistance;

layout(location = 0) out vec4 accumColor;
layout(location = 1) out float revealage;

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
	
	vec4 color = vertexColor * vec4(ColorModulator.rgb, 1.0);
	float nearFactor = 1.0 - smoothstep(FogStart, FogEnd, fogDistance);
	float translucency = 1.0 - vertexColor.a;
	float warmStrength = smooth01(SunWarmth) * mix(0.45, 1.0, nearFactor);
	float sunLuma = max(luminance(SunColor), 1.0E-4);
	vec3 sunTint = mix(vec3(1.0), SunColor / sunLuma, 0.88);
	float tintStrength = warmStrength * mix(0.55, 0.90, translucency);
	color.rgb = mix(color.rgb, color.rgb * sunTint * mix(1.0, 1.12, warmStrength), tintStrength);
	color = mix(color, FogColor, smoothstep(FogStart, FogEnd, fogDistance));
	
	vec4 premul = vec4(color.r * color.a, color.g * color.a, color.b * color.a, color.a);

	float z = min(vertexDistance / 1000.0, 1.0);
	float weight = max(premul.a * 3000.0 * pow(1.0 - z, 3.0), 0.01);
	
    accumColor = premul * weight;
    revealage = premul.a;
}
