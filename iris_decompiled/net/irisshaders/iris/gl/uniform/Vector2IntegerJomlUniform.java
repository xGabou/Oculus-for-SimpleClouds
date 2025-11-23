/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector2i
 */
package net.irisshaders.iris.gl.uniform;

import java.util.function.Supplier;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.state.ValueUpdateNotifier;
import net.irisshaders.iris.gl.uniform.Uniform;
import org.joml.Vector2i;

public class Vector2IntegerJomlUniform
extends Uniform {
    private final Supplier<Vector2i> value;
    private Vector2i cachedValue = null;

    Vector2IntegerJomlUniform(int location, Supplier<Vector2i> value) {
        this(location, value, null);
    }

    Vector2IntegerJomlUniform(int location, Supplier<Vector2i> value, ValueUpdateNotifier notifier) {
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
        Vector2i newValue = this.value.get();
        if (!newValue.equals((Object)this.cachedValue)) {
            this.cachedValue = newValue;
            IrisRenderSystem.uniform2i(this.location, newValue.x, newValue.y);
        }
    }
}

