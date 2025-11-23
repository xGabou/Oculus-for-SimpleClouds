/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.particle.ParticleEngine
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderBuffers
 *  net.minecraft.client.renderer.culling.Frustum
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.fantastic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.fantastic.ParticleRenderingPhase;
import net.irisshaders.iris.fantastic.PhasedParticleEngine;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.shaderpack.properties.ParticleRenderingSettings;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class})
public class MixinLevelRenderer {
    @Shadow
    @Final
    private Minecraft f_109461_;
    @Shadow
    private RenderBuffers f_109464_;

    @Inject(method={"renderLevel"}, at={@At(value="HEAD")})
    private void iris$resetParticleManagerPhase(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        ((PhasedParticleEngine)this.f_109461_.f_91061_).setParticleRenderingPhase(ParticleRenderingPhase.EVERYTHING);
    }

    @Inject(method={"renderLevel"}, at={@At(value="CONSTANT", args={"stringValue=entities"})})
    private void iris$renderOpaqueParticles(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        this.f_109461_.m_91307_().m_6182_("opaque_particles");
        MultiBufferSource.BufferSource bufferSource = this.f_109464_.m_110104_();
        ParticleRenderingSettings settings = this.getRenderingSettings();
        if (settings == ParticleRenderingSettings.BEFORE) {
            this.f_109461_.f_91061_.m_107336_(poseStack, bufferSource, lightTexture, camera, f);
        } else if (settings == ParticleRenderingSettings.MIXED) {
            ((PhasedParticleEngine)this.f_109461_.f_91061_).setParticleRenderingPhase(ParticleRenderingPhase.OPAQUE);
            this.f_109461_.f_91061_.m_107336_(poseStack, bufferSource, lightTexture, camera, f);
        }
    }

    @Redirect(method={"renderLevel"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/particle/ParticleEngine;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;)V"))
    private void iris$renderTranslucentAfterDeferred(ParticleEngine instance, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, LightTexture lightTexture, Camera camera, float f, Frustum frustum) {
        ParticleRenderingSettings settings = this.getRenderingSettings();
        if (settings == ParticleRenderingSettings.AFTER) {
            this.f_109461_.f_91061_.render(poseStack, bufferSource, lightTexture, camera, f, frustum);
        } else if (settings == ParticleRenderingSettings.MIXED) {
            ((PhasedParticleEngine)this.f_109461_.f_91061_).setParticleRenderingPhase(ParticleRenderingPhase.TRANSLUCENT);
            this.f_109461_.f_91061_.m_107336_(poseStack, bufferSource, lightTexture, camera, f);
        }
    }

    private ParticleRenderingSettings getRenderingSettings() {
        return Iris.getPipelineManager().getPipeline().map(WorldRenderingPipeline::getParticleRenderingSettings).orElse(ParticleRenderingSettings.MIXED);
    }
}

