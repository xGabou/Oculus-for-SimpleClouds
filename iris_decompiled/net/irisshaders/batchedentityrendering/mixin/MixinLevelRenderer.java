/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderBuffers
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package net.irisshaders.batchedentityrendering.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.irisshaders.batchedentityrendering.impl.DrawCallTrackingRenderBuffers;
import net.irisshaders.batchedentityrendering.impl.FullyBufferedMultiBufferSource;
import net.irisshaders.batchedentityrendering.impl.Groupable;
import net.irisshaders.batchedentityrendering.impl.RenderBuffersExt;
import net.irisshaders.batchedentityrendering.impl.TransparencyType;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value={LevelRenderer.class}, priority=999)
public class MixinLevelRenderer {
    private static final String RENDER_ENTITY = "Lnet/minecraft/client/renderer/LevelRenderer;renderEntity(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V";
    @Shadow
    private RenderBuffers f_109464_;
    @Unique
    private Groupable groupable;

    @Inject(method={"renderLevel"}, at={@At(value="HEAD")})
    private void batchedentityrendering$beginLevelRender(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (this.f_109464_ instanceof DrawCallTrackingRenderBuffers) {
            ((DrawCallTrackingRenderBuffers)this.f_109464_).resetDrawCounts();
        }
        ((RenderBuffersExt)this.f_109464_).beginLevelRendering();
        MultiBufferSource.BufferSource provider = this.f_109464_.m_110104_();
        if (provider instanceof Groupable) {
            this.groupable = (Groupable)provider;
        }
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;renderEntity(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V")})
    private void batchedentityrendering$preRenderEntity(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (this.groupable != null) {
            this.groupable.startGroup();
        }
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;renderEntity(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V", shift=At.Shift.AFTER)})
    private void batchedentityrendering$postRenderEntity(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (this.groupable != null) {
            this.groupable.endGroup();
        }
    }

    @Inject(method={"renderLevel"}, at={@At(value="CONSTANT", args={"stringValue=translucent"})}, locals=LocalCapture.CAPTURE_FAILHARD)
    private void batchedentityrendering$beginTranslucents(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        MultiBufferSource.BufferSource bufferSource = this.f_109464_.m_110104_();
        if (bufferSource instanceof FullyBufferedMultiBufferSource) {
            FullyBufferedMultiBufferSource fullyBufferedMultiBufferSource = (FullyBufferedMultiBufferSource)bufferSource;
            fullyBufferedMultiBufferSource.readyUp();
        }
        if (WorldRenderingSettings.INSTANCE.shouldSeparateEntityDraws()) {
            Minecraft.m_91087_().m_91307_().m_6182_("entity_draws_opaque");
            bufferSource = this.f_109464_.m_110104_();
            if (bufferSource instanceof FullyBufferedMultiBufferSource) {
                FullyBufferedMultiBufferSource source = (FullyBufferedMultiBufferSource)bufferSource;
                source.endBatchWithType(TransparencyType.OPAQUE);
                source.endBatchWithType(TransparencyType.OPAQUE_DECAL);
            } else {
                this.f_109464_.m_110104_().m_109911_();
            }
        } else {
            Minecraft.m_91087_().m_91307_().m_6182_("entity_draws");
            this.f_109464_.m_110104_().m_109911_();
        }
    }

    @Inject(method={"renderLevel"}, at={@At(value="CONSTANT", args={"stringValue=translucent"}, shift=At.Shift.AFTER)}, locals=LocalCapture.CAPTURE_FAILHARD)
    private void batchedentityrendering$endTranslucents(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (WorldRenderingSettings.INSTANCE.shouldSeparateEntityDraws()) {
            this.f_109464_.m_110104_().m_109911_();
        }
    }

    @Inject(method={"renderLevel"}, at={@At(value="RETURN")})
    private void batchedentityrendering$endLevelRender(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        ((RenderBuffersExt)this.f_109464_).endLevelRendering();
        this.groupable = null;
    }
}

