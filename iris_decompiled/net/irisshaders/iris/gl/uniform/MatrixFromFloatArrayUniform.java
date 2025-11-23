/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  org.lwjgl.BufferUtils
 */
package net.irisshaders.iris.gl.uniform;

import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.function.Supplier;
import net.irisshaders.iris.gl.uniform.Uniform;
import org.lwjgl.BufferUtils;

public class MatrixFromFloatArrayUniform
extends Uniform {
    private final FloatBuffer buffer = BufferUtils.createFloatBuffer((int)16);
    private final Supplier<float[]> value;
    private float[] cachedValue = null;

    MatrixFromFloatArrayUniform(int location, Supplier<float[]> value) {
        super(location);
        this.value = value;
    }

    @Override
    public void update() {
        float[] newValue = this.value.get();
        if (!Arrays.equals(newValue, this.cachedValue)) {
            this.cachedValue = Arrays.copyOf(newValue, 16);
            this.buffer.put(this.cachedValue);
            this.buffer.rewind();
            RenderSystem.glUniformMatrix4((int)this.location, (boolean)false, (FloatBuffer)this.buffer);
        }
    }
}

