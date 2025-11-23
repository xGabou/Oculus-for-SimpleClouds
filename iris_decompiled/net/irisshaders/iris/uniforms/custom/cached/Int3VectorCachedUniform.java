/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3i
 *  org.joml.Vector3ic
 *  org.lwjgl.opengl.GL21
 */
package net.irisshaders.iris.uniforms.custom.cached;

import java.util.function.Supplier;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.parsing.VectorType;
import net.irisshaders.iris.uniforms.custom.cached.VectorCachedUniform;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.lwjgl.opengl.GL21;

public class Int3VectorCachedUniform
extends VectorCachedUniform<Vector3i> {
    public Int3VectorCachedUniform(String name, UniformUpdateFrequency updateFrequency, Supplier<Vector3i> supplier) {
        super(name, updateFrequency, new Vector3i(), supplier);
    }

    @Override
    protected void setFrom(Vector3i other) {
        ((Vector3i)this.cached).set((Vector3ic)other);
    }

    @Override
    public void push(int location) {
        GL21.glUniform3i((int)location, (int)((Vector3i)this.cached).x, (int)((Vector3i)this.cached).y, (int)((Vector3i)this.cached).z);
    }

    @Override
    public VectorType getType() {
        return VectorType.I_VEC3;
    }
}

