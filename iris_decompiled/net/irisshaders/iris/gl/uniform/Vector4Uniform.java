/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector4f
 */
package net.irisshaders.iris.gl.uniform;

import java.util.function.Supplier;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.state.ValueUpdateNotifier;
import net.irisshaders.iris.gl.uniform.Uniform;
import org.joml.Vector4f;

public class Vector4Uniform
extends Uniform {
    private final Vector4f cachedValue = new Vector4f();
    private final Supplier<Vector4f> value;

    Vector4Uniform(int location, Supplier<Vector4f> value) {
        this(location, value, null);
    }

    Vector4Uniform(int location, Supplier<Vector4f> value, ValueUpdateNotifier notifier) {
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
        Vector4f newValue = this.value.get();
        if (!newValue.equals((Object)this.cachedValue)) {
            this.cachedValue.set(newValue.x(), newValue.y(), newValue.z(), newValue.w());
            IrisRenderSystem.uniform4f(this.location, this.cachedValue.x(), this.cachedValue.y(), this.cachedValue.z(), this.cachedValue.w());
        }
    }
}

