/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.gl.uniform;

import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.state.ValueUpdateNotifier;
import net.irisshaders.iris.gl.uniform.FloatSupplier;
import net.irisshaders.iris.gl.uniform.Uniform;

public class FloatUniform
extends Uniform {
    private final FloatSupplier value;
    private float cachedValue = 0.0f;

    FloatUniform(int location, FloatSupplier value) {
        this(location, value, null);
    }

    FloatUniform(int location, FloatSupplier value, ValueUpdateNotifier notifier) {
        super(location, notifier);
        this.value = value;
    }

    @Override
    public void update() {
        this.updateValue();
        if (this.notifier != null) {
            this.notifier.setListener(this::updateValue);
        }
    }

    private void updateValue() {
        float newValue = this.value.getAsFloat();
        if (this.cachedValue != newValue) {
            this.cachedValue = newValue;
            IrisRenderSystem.uniform1f(this.location, newValue);
        }
    }
}

