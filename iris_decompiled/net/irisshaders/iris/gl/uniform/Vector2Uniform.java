/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector2f
 */
package net.irisshaders.iris.gl.uniform;

import java.util.function.Supplier;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.state.ValueUpdateNotifier;
import net.irisshaders.iris.gl.uniform.Uniform;
import org.joml.Vector2f;

public class Vector2Uniform
extends Uniform {
    private final Supplier<Vector2f> value;
    private Vector2f cachedValue = null;

    Vector2Uniform(int location, Supplier<Vector2f> value) {
        super(location);
        this.value = value;
    }

    Vector2Uniform(int location, Supplier<Vector2f> value, ValueUpdateNotifier notifier) {
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
        Vector2f newValue = this.value.get();
        if (!newValue.equals((Object)this.cachedValue)) {
            this.cachedValue = newValue;
            IrisRenderSystem.uniform2f(this.location, newValue.x, newValue.y);
        }
    }
}

