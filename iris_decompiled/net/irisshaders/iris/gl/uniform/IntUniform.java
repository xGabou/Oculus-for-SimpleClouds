/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 */
package net.irisshaders.iris.gl.uniform;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.IntSupplier;
import net.irisshaders.iris.gl.state.ValueUpdateNotifier;
import net.irisshaders.iris.gl.uniform.Uniform;

public class IntUniform
extends Uniform {
    private final IntSupplier value;
    private int cachedValue = 0;

    IntUniform(int location, IntSupplier value) {
        this(location, value, null);
    }

    IntUniform(int location, IntSupplier value, ValueUpdateNotifier notifier) {
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
        int newValue = this.value.getAsInt();
        if (this.cachedValue != newValue) {
            this.cachedValue = newValue;
            RenderSystem.glUniform1i((int)this.location, (int)newValue);
        }
    }
}

