/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.ShaderInstance
 */
package net.irisshaders.iris.pipeline.programs;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pipeline.ShaderRenderingPipeline;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.pipeline.programs.ShaderKey;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;

public class ShaderAccess {
    public static ShaderInstance getParticleTranslucentShader() {
        ShaderInstance override;
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline instanceof ShaderRenderingPipeline && (override = ((ShaderRenderingPipeline)pipeline).getShaderMap().getShader(ShaderKey.PARTICLES_TRANS)) != null) {
            return override;
        }
        return GameRenderer.m_172829_();
    }

    public static ShaderInstance getMekanismFlameShader() {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline instanceof ShaderRenderingPipeline) {
            return ((ShaderRenderingPipeline)pipeline).getShaderMap().getShader(ShadowRenderingState.areShadowsCurrentlyBeingRendered() ? ShaderKey.MEKANISM_FLAME_SHADOW : ShaderKey.MEKANISM_FLAME);
        }
        return GameRenderer.m_172820_();
    }

    public static ShaderInstance getMekasuitShader() {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline instanceof ShaderRenderingPipeline) {
            return ((ShaderRenderingPipeline)pipeline).getShaderMap().getShader(ShadowRenderingState.areShadowsCurrentlyBeingRendered() ? ShaderKey.SHADOW_ENTITIES_CUTOUT : ShaderKey.ENTITIES_TRANSLUCENT);
        }
        return GameRenderer.m_172664_();
    }
}

