#version 150

in vec2 texCoord;
out vec4 fragColor;

// your uniforms
uniform vec4 simpleCloudsCloudState;
uniform vec4 simpleCloudsCloudType;

void main() {

    // Display each component clearly
    float stateDark   = simpleCloudsCloudState.z;   // expected 0 to 1
    float stateCover  = simpleCloudsCloudState.w;   // expected 0 to 1
    float typeStorm   = simpleCloudsCloudType.z;    // expected 0 to 1

    // Visual color code:
    // red   = darkness
    // blue  = cover
    // green = storminess
    // alpha = 1
    vec3 dbg = vec3(stateDark, typeStorm, stateCover);

    fragColor = vec4(dbg, 1.0);
}
