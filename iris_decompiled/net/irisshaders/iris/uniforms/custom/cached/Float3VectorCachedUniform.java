/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.lwjgl.opengl.GL21
 */
package net.irisshaders.iris.uniforms.custom.cached;

import java.util.function.Supplier;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.parsing.VectorType;
import net.irisshaders.iris.uniforms.custom.cached.VectorCachedUniform;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL21;

public class Float3VectorCachedUniform
extends VectorCachedUniform<Vector3f> {
    public Float3VectorCachedUniform(String name, UniformUpdateFrequency updateFrequency, Supplier<Vector3f> supplier) {
        super(name, updateFrequency, new Vector3f(), supplier);
    }

    @Override
    protected void setFrom(Vector3f other) {
        ((Vector3f)this.cached).set((Vector3fc)other);
    }

    @Override
    public void push(int location) {
        GL21.glUniform3f((int)location, (float)((Vector3f)this.cached).x, (float)((Vector3f)this.cached).y, (float)((Vector3f)this.cached).z);
    }

    @Override
    public VectorType getType() {
        return VectorType.VEC3;
    }
}

