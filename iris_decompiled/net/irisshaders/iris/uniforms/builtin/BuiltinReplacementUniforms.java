/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package net.irisshaders.iris.uniforms.builtin;

import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import org.joml.Matrix4f;

public class BuiltinReplacementUniforms {
    private static final Matrix4f lightmapTextureMatrix = new Matrix4f(0.00390625f, 0.0f, 0.0f, 0.0f, 0.0f, 0.00390625f, 0.0f, 0.0f, 0.0f, 0.0f, 0.00390625f, 0.0f, 0.03125f, 0.03125f, 0.03125f, 1.0f);

    public static void addBuiltinReplacementUniforms(UniformHolder uniforms) {
        uniforms.uniformMatrix(UniformUpdateFrequency.ONCE, "iris_LightmapTextureMatrix", () -> lightmapTextureMatrix);
    }
}

