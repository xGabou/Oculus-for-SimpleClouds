/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.lwjgl.BufferUtils
 */
package net.irisshaders.iris.gl.uniform;

import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.FloatBuffer;
import java.util.function.Supplier;
import net.irisshaders.iris.gl.state.ValueUpdateNotifier;
import net.irisshaders.iris.gl.uniform.Uniform;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.BufferUtils;

public class MatrixUniform
extends Uniform {
    private final FloatBuffer buffer = BufferUtils.createFloatBuffer((int)16);
    private final Supplier<Matrix4f> value;
    private Matrix4f cachedValue = null;

    MatrixUniform(int location, Supplier<Matrix4f> value) {
        super(location);
        this.value = value;
    }

    MatrixUniform(int location, Supplier<Matrix4f> value, ValueUpdateNotifier notifier) {
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

    public void updateValue() {
        Matrix4f newValue = this.value.get();
        if (!newValue.equals((Object)this.cachedValue)) {
            this.cachedValue = new Matrix4f((Matrix4fc)newValue);
            this.cachedValue.get(this.buffer);
            this.buffer.rewind();
            RenderSystem.glUniformMatrix4((int)this.location, (boolean)false, (FloatBuffer)this.buffer);
        }
    }
}

