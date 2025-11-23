/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3d
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 */
package net.irisshaders.iris.gl.uniform;

import java.util.function.Supplier;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.state.ValueUpdateNotifier;
import net.irisshaders.iris.gl.uniform.Uniform;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Vector3Uniform
extends Uniform {
    private final Vector3f cachedValue = new Vector3f();
    private final Supplier<Vector3f> value;

    Vector3Uniform(int location, Supplier<Vector3f> value) {
        super(location);
        this.value = value;
    }

    Vector3Uniform(int location, Supplier<Vector3f> value, ValueUpdateNotifier notifier) {
        super(location, notifier);
        this.value = value;
    }

    static Vector3Uniform converted(int location, Supplier<Vector3d> value) {
        Vector3f held = new Vector3f();
        return new Vector3Uniform(location, () -> {
            Vector3d updated = (Vector3d)value.get();
            held.set((float)updated.x, (float)updated.y, (float)updated.z);
            return held;
        });
    }

    static Vector3Uniform truncated(int location, Supplier<Vector4f> value) {
        Vector3f held = new Vector3f();
        return new Vector3Uniform(location, () -> {
            Vector4f updated = (Vector4f)value.get();
            held.set(updated.x(), updated.y(), updated.z());
            return held;
        });
    }

    @Override
    public void update() {
        this.updateValue();
        if (this.notifier != null) {
            this.notifier.setListener(this::updateValue);
        }
    }

    private void updateValue() {
        Vector3f newValue = this.value.get();
        if (!newValue.equals((Object)this.cachedValue)) {
            this.cachedValue.set(newValue.x(), newValue.y(), newValue.z());
            IrisRenderSystem.uniform3f(this.location, this.cachedValue.x(), this.cachedValue.y(), this.cachedValue.z());
        }
    }
}

