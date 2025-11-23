/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  org.joml.Vector4f
 */
package net.irisshaders.iris.uniforms;

import com.mojang.blaze3d.systems.RenderSystem;
import net.irisshaders.iris.gl.state.FogMode;
import net.irisshaders.iris.gl.uniform.DynamicUniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import org.joml.Vector4f;

public class IrisInternalUniforms {
    private IrisInternalUniforms() {
    }

    public static void addFogUniforms(DynamicUniformHolder uniforms, FogMode fogMode) {
        uniforms.uniform4f(UniformUpdateFrequency.PER_FRAME, "iris_FogColor", () -> {
            float[] fogColor = RenderSystem.getShaderFogColor();
            return new Vector4f(fogColor[0], fogColor[1], fogColor[2], fogColor[3]);
        });
        uniforms.uniform1f(UniformUpdateFrequency.PER_FRAME, "iris_FogStart", RenderSystem::getShaderFogStart).uniform1f(UniformUpdateFrequency.PER_FRAME, "iris_FogEnd", RenderSystem::getShaderFogEnd);
        uniforms.uniform1f("iris_FogDensity", () -> Math.max(0.0f, CapturedRenderingState.INSTANCE.getFogDensity()), notifier -> {});
        uniforms.uniform1f("iris_currentAlphaTest", CapturedRenderingState.INSTANCE::getCurrentAlphaTest, notifier -> {});
        uniforms.uniform1f("alphaTestRef", CapturedRenderingState.INSTANCE::getCurrentAlphaTest, notifier -> {});
    }
}

