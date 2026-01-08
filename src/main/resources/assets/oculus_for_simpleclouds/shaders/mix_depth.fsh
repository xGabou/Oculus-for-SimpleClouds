#version 150

in vec2 vUv;

uniform sampler2D mcDepth;
uniform sampler2D dhDepth;
uniform int reverseDepth;
uniform float bias;

void main() {
    float dMc = texture(mcDepth, vUv).r;
    float dDh = texture(dhDepth, vUv).r;

    float clear = (reverseDepth == 1) ? 0.0 : 1.0;

    bool mcClear = abs(dMc - clear) < 0.00001;
    bool dhClear = abs(dDh - clear) < 0.00001;

    if (dhClear) {
        gl_FragDepth = dMc + bias;
        return;
    }

    if (mcClear) {
        gl_FragDepth = dDh + bias;
        return;
    }

    float merged = (reverseDepth == 1) ? max(dMc, dDh) : min(dMc, dDh);
    gl_FragDepth = merged + bias;
}
