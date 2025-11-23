/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.parsing;

import net.irisshaders.iris.uniforms.SystemTimeUniforms;

public class SmoothFloat {
    private static final double LN_OF_2 = Math.log(2.0);
    private float accumulator;
    private boolean hasInitialValue;
    private float cachedHalfLifeUp;
    private float cachedDecayUp;
    private float cachedHalfLifeDown;
    private float cachedDecayDown;

    private static float exponentialDecayFactor(float k, float t) {
        return (float)Math.exp(-k * t);
    }

    private static float lerp(float v0, float v1, float t) {
        return (1.0f - t) * v0 + t * v1;
    }

    public float updateAndGet(float value, float halfLifeUp, float halfLifeDown) {
        float decay;
        if (halfLifeUp != this.cachedHalfLifeUp) {
            this.cachedHalfLifeUp = halfLifeUp;
            this.cachedDecayUp = halfLifeUp == 0.0f ? 0.0f : this.computeDecay(halfLifeUp * 0.1f);
        }
        if (halfLifeDown != this.cachedHalfLifeDown) {
            this.cachedHalfLifeDown = halfLifeDown;
            this.cachedDecayDown = halfLifeDown == 0.0f ? 0.0f : this.computeDecay(halfLifeDown * 0.1f);
        }
        if (!this.hasInitialValue) {
            this.accumulator = value;
            this.hasInitialValue = true;
            return this.accumulator;
        }
        float lastFrameTime = SystemTimeUniforms.TIMER.getLastFrameTime();
        float f = decay = value > this.accumulator ? this.cachedDecayUp : this.cachedDecayDown;
        if (decay == 0.0f) {
            this.accumulator = value;
            return this.accumulator;
        }
        float smoothingFactor = 1.0f - SmoothFloat.exponentialDecayFactor(decay, lastFrameTime);
        this.accumulator = SmoothFloat.lerp(this.accumulator, value, smoothingFactor);
        return this.accumulator;
    }

    private float computeDecay(float halfLife) {
        return (float)(1.0 / ((double)halfLife / LN_OF_2));
    }
}

