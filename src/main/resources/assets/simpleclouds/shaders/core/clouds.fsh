#version 430

uniform sampler2D BayerMatrixSampler;

uniform vec4 ColorModulator;
uniform float DitherScale;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in vec4 vertexColor;
in float fogDistance;

out vec4 fragColor;

void main()
{
    float fade = ColorModulator.a;
    float r = texture(BayerMatrixSampler, gl_FragCoord.xy * DitherScale).r;
    if (fade < r)
        discard;

    vec4 color = vertexColor * vec4(ColorModulator.rgb, 1.0);
    color = mix(color, FogColor, smoothstep(FogStart, FogEnd, fogDistance));

    if (any(isnan(color.rgb)) || any(isinf(color.rgb)))
        color.rgb = max(vertexColor.rgb * ColorModulator.rgb, vec3(0.08, 0.09, 0.12));
    color.rgb = max(color.rgb, vec3(0.0));

    fragColor = vec4(color.rgb, 1.0);
}
