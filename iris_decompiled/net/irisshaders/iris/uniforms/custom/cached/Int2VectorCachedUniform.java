/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector2i
 *  org.joml.Vector2ic
 *  org.lwjgl.opengl.GL21
 */
package net.irisshaders.iris.uniforms.custom.cached;

import java.util.function.Supplier;
import kroppeb.stareval.function.FunctionReturn;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.parsing.VectorType;
import net.irisshaders.iris.uniforms.custom.cached.VectorCachedUniform;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.opengl.GL21;

public class Int2VectorCachedUniform
extends VectorCachedUniform<Vector2i> {
    public Int2VectorCachedUniform(String name, UniformUpdateFrequency updateFrequency, Supplier<Vector2i> supplier) {
        super(name, updateFrequency, new Vector2i(), supplier);
    }

    @Override
    protected void setFrom(Vector2i other) {
        ((Vector2i)this.cached).set((Vector2ic)other);
    }

    @Override
    public void push(int location) {
        GL21.glUniform2i((int)location, (int)((Vector2i)this.cached).x, (int)((Vector2i)this.cached).y);
    }

    @Override
    public void writeTo(FunctionReturn functionReturn) {
        functionReturn.objectReturn = this.cached;
    }

    @Override
    public VectorType getType() {
        return VectorType.I_VEC2;
    }
}

