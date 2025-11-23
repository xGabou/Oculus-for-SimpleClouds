/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.gl.uniform;

import java.util.Arrays;
import java.util.function.Supplier;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.state.ValueUpdateNotifier;
import net.irisshaders.iris.gl.uniform.Uniform;

public class Vector4ArrayUniform
extends Uniform {
    private final Supplier<float[]> value;
    private float[] cachedValue = new float[4];

    Vector4ArrayUniform(int location, Supplier<float[]> value) {
        this(location, value, null);
    }

    Vector4ArrayUniform(int location, Supplier<float[]> value, ValueUpdateNotifier notifier) {
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
        float[] newValue = this.value.get();
        if (!Arrays.equals(newValue, this.cachedValue)) {
            this.cachedValue = newValue;
            IrisRenderSystem.uniform4f(this.location, this.cachedValue[0], this.cachedValue[1], this.cachedValue[2], this.cachedValue[3]);
        }
    }
}

