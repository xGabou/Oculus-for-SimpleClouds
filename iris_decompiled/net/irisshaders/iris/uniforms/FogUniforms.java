/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.shaders.FogShape
 *  com.mojang.blaze3d.systems.RenderSystem
 *  org.joml.Vector3f
 */
package net.irisshaders.iris.uniforms;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.irisshaders.iris.gl.state.FogMode;
import net.irisshaders.iris.gl.state.StateUpdateNotifiers;
import net.irisshaders.iris.gl.uniform.DynamicUniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import org.joml.Vector3f;

public class FogUniforms {
    private FogUniforms() {
    }

    public static void addFogUniforms(DynamicUniformHolder uniforms, FogMode fogMode) {
        if (fogMode == FogMode.OFF) {
            uniforms.uniform1i(UniformUpdateFrequency.ONCE, "fogMode", () -> 0);
            uniforms.uniform1i(UniformUpdateFrequency.ONCE, "fogShape", () -> -1);
        } else if (fogMode == FogMode.PER_VERTEX || fogMode == FogMode.PER_FRAGMENT) {
            uniforms.uniform1i("fogMode", () -> {
                float fogDensity = CapturedRenderingState.INSTANCE.getFogDensity();
                if (fogDensity < 0.0f) {
                    return 9729;
                }
                return 2049;
            }, listener -> {});
            uniforms.uniform1i(UniformUpdateFrequency.PER_FRAME, "fogShape", () -> RenderSystem.getShaderFogShape() == FogShape.CYLINDER ? 1 : 0);
        }
        uniforms.uniform1f("fogDensity", () -> Math.max(0.0f, CapturedRenderingState.INSTANCE.getFogDensity()), notifier -> {});
        uniforms.uniform1f("fogStart", RenderSystem::getShaderFogStart, listener -> StateUpdateNotifiers.fogStartNotifier.setListener(listener));
        uniforms.uniform1f("fogEnd", RenderSystem::getShaderFogEnd, listener -> StateUpdateNotifiers.fogEndNotifier.setListener(listener));
        uniforms.uniform3f(UniformUpdateFrequency.PER_FRAME, "fogColor", () -> {
            float[] fogColor = RenderSystem.getShaderFogColor();
            return new Vector3f(fogColor[0], fogColor[1], fogColor[2]);
        });
    }
}

