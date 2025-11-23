/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3i
 */
package net.irisshaders.iris.gl.uniform;

import java.util.function.Supplier;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.state.ValueUpdateNotifier;
import net.irisshaders.iris.gl.uniform.Uniform;
import org.joml.Vector3i;

public class Vector3IntegerUniform
extends Uniform {
    private final Vector3i cachedValue = new Vector3i();
    private final Supplier<Vector3i> value;

    Vector3IntegerUniform(int location, Supplier<Vector3i> value) {
        super(location);
        this.value = value;
    }

    Vector3IntegerUniform(int location, Supplier<Vector3i> value, ValueUpdateNotifier notifier) {
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
        Vector3i newValue = this.value.get();
        if (!newValue.equals((Object)this.cachedValue)) {
            this.cachedValue.set(newValue.x(), newValue.y(), newValue.z());
            IrisRenderSystem.uniform3i(this.location, this.cachedValue.x(), this.cachedValue.y(), this.cachedValue.z());
        }
    }
}

