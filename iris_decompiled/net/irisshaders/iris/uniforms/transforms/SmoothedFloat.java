/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.uniforms.transforms;

import net.irisshaders.iris.gl.uniform.FloatSupplier;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import net.irisshaders.iris.uniforms.SystemTimeUniforms;

public class SmoothedFloat
implements FloatSupplier {
    private static final double LN_OF_2 = Math.log(2.0);
    private final FloatSupplier unsmoothed;
    private final float decayConstantUp;
    private final float decayConstantDown;
    private float accumulator;
    private boolean hasInitialValue;

    public SmoothedFloat(float halfLifeUp, float halfLifeDown, FloatSupplier unsmoothed, FrameUpdateNotifier updateNotifier) {
        this.decayConstantUp = this.computeDecay(halfLifeUp * 0.1f);
        this.decayConstantDown = this.computeDecay(halfLifeDown * 0.1f);
        this.unsmoothed = unsmoothed;
        updateNotifier.addListener(this::update);
    }

    private static float exponentialDecayFactor(float k, float t) {
        return (float)Math.exp(-k * t);
    }

    private static float lerp(float v0, float v1, float t) {
        return (1.0f - t) * v0 + t * v1;
    }

    private void update() {
        if (!this.hasInitialValue) {
            this.accumulator = this.unsmoothed.getAsFloat();
            this.hasInitialValue = true;
            return;
        }
        float newValue = this.unsmoothed.getAsFloat();
        float lastFrameTime = SystemTimeUniforms.TIMER.getLastFrameTime();
        float smoothingFactor = 1.0f - SmoothedFloat.exponentialDecayFactor(newValue > this.accumulator ? this.decayConstantUp : this.decayConstantDown, lastFrameTime);
        this.accumulator = SmoothedFloat.lerp(this.accumulator, newValue, smoothingFactor);
    }

    private float computeDecay(float halfLife) {
        return (float)(1.0 / ((double)halfLife / LN_OF_2));
    }

    @Override
    public float getAsFloat() {
        if (!this.hasInitialValue) {
            return this.unsmoothed.getAsFloat();
        }
        return this.accumulator;
    }
}

