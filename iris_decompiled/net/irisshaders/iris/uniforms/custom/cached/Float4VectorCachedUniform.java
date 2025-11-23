/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 *  org.lwjgl.opengl.GL21
 */
package net.irisshaders.iris.uniforms.custom.cached;

import java.util.function.Supplier;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.parsing.VectorType;
import net.irisshaders.iris.uniforms.custom.cached.VectorCachedUniform;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.opengl.GL21;

public class Float4VectorCachedUniform
extends VectorCachedUniform<Vector4f> {
    public Float4VectorCachedUniform(String name, UniformUpdateFrequency updateFrequency, Supplier<Vector4f> supplier) {
        super(name, updateFrequency, new Vector4f(), supplier);
    }

    @Override
    protected void setFrom(Vector4f other) {
        ((Vector4f)this.cached).set((Vector4fc)other);
    }

    @Override
    public void push(int location) {
        GL21.glUniform4f((int)location, (float)((Vector4f)this.cached).x, (float)((Vector4f)this.cached).y, (float)((Vector4f)this.cached).z, (float)((Vector4f)this.cached).w);
    }

    @Override
    public VectorType getType() {
        return VectorType.VEC4;
    }
}

