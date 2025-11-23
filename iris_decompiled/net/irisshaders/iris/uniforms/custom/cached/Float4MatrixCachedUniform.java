/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.lwjgl.opengl.GL21
 */
package net.irisshaders.iris.uniforms.custom.cached;

import java.util.function.Supplier;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.parsing.MatrixType;
import net.irisshaders.iris.uniforms.custom.cached.VectorCachedUniform;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.opengl.GL21;

public class Float4MatrixCachedUniform
extends VectorCachedUniform<Matrix4f> {
    private final float[] buffer = new float[16];

    public Float4MatrixCachedUniform(String name, UniformUpdateFrequency updateFrequency, Supplier<Matrix4f> supplier) {
        super(name, updateFrequency, new Matrix4f(), supplier);
    }

    @Override
    protected void setFrom(Matrix4f other) {
        ((Matrix4f)this.cached).set((Matrix4fc)other);
    }

    @Override
    public void push(int location) {
        ((Matrix4f)this.cached).get(this.buffer);
        GL21.glUniformMatrix4fv((int)location, (boolean)false, (float[])this.buffer);
    }

    @Override
    public MatrixType<Matrix4f> getType() {
        return MatrixType.MAT4;
    }
}

