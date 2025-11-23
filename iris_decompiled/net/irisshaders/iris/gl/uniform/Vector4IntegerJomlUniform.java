/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector4i
 */
package net.irisshaders.iris.gl.uniform;

import java.util.function.Supplier;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.state.ValueUpdateNotifier;
import net.irisshaders.iris.gl.uniform.Uniform;
import org.joml.Vector4i;

public class Vector4IntegerJomlUniform
extends Uniform {
    private final Supplier<Vector4i> value;
    private Vector4i cachedValue = null;

    Vector4IntegerJomlUniform(int location, Supplier<Vector4i> value) {
        this(location, value, null);
    }

    Vector4IntegerJomlUniform(int location, Supplier<Vector4i> value, ValueUpdateNotifier notifier) {
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
        Vector4i newValue = this.value.get();
        if (!newValue.equals((Object)this.cachedValue)) {
            this.cachedValue = newValue;
            IrisRenderSystem.uniform4i(this.location, newValue.x, newValue.y, newValue.z, newValue.w);
        }
    }
}

