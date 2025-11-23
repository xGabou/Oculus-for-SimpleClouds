/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniform
 *  org.joml.Matrix3f
 *  org.lwjgl.opengl.GL30C
 *  org.lwjgl.system.MemoryStack
 */
package net.irisshaders.iris.compat.sodium.impl.shader_overrides;

import java.nio.FloatBuffer;
import me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniform;
import org.joml.Matrix3f;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.system.MemoryStack;

public class GlUniformMatrix3f
extends GlUniform<Matrix3f> {
    public GlUniformMatrix3f(int index) {
        super(index);
    }

    public void set(Matrix3f value) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            FloatBuffer buf = stack.callocFloat(12);
            value.get(buf);
            GL30C.glUniformMatrix3fv((int)this.index, (boolean)false, (FloatBuffer)buf);
        }
    }
}

