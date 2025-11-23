/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector2f
 *  org.joml.Vector2fc
 *  org.lwjgl.opengl.GL21
 */
package net.irisshaders.iris.uniforms.custom.cached;

import java.util.function.Supplier;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.parsing.VectorType;
import net.irisshaders.iris.uniforms.custom.cached.VectorCachedUniform;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.lwjgl.opengl.GL21;

public class Float2VectorCachedUniform
extends VectorCachedUniform<Vector2f> {
    public Float2VectorCachedUniform(String name, UniformUpdateFrequency updateFrequency, Supplier<Vector2f> supplier) {
        super(name, updateFrequency, new Vector2f(), supplier);
    }

    @Override
    protected void setFrom(Vector2f other) {
        ((Vector2f)this.cached).set((Vector2fc)other);
    }

    @Override
    public void push(int location) {
        GL21.glUniform2f((int)location, (float)((Vector2f)this.cached).x, (float)((Vector2f)this.cached).y);
    }

    @Override
    public VectorType getType() {
        return VectorType.VEC2;
    }
}

