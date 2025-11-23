/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.platform.GlStateManager$DestFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SourceFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.blaze3d.vertex.VertexSorting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.util.profiling.ProfilerFiller
 *  org.joml.Matrix4f
 *  org.lwjgl.opengl.GL30
 */
package dev.nonamecrackers2.simpleclouds.client.dh.pipeline;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import dev.nonamecrackers2.simpleclouds.client.framebuffer.FrameBufferUtils;
import dev.nonamecrackers2.simpleclouds.client.framebuffer.WeightedBlendingTarget;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.renderer.WorldEffects;
import dev.nonamecrackers2.simpleclouds.client.renderer.pipeline.CloudsRenderPipeline;
import dev.nonamecrackers2.simpleclouds.common.config.SimpleCloudsConfig;
import dev.nonamecrackers2.simpleclouds.mixin.MixinRenderTargetAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.profiling.ProfilerFiller;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;

public class DhSupportPipeline
implements CloudsRenderPipeline {
    public static final DhSupportPipeline INSTANCE = new DhSupportPipeline();

    private DhSupportPipeline() {
    }

    @Override
    public void prepare(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ, Frustum frustum) {
    }

    @Override
    public void afterSky(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ, Frustum frustum) {
        if (((Boolean)SimpleCloudsConfig.CLIENT.atmosphericClouds.get()).booleanValue()) {
            ProfilerFiller p = mc.m_91307_();
            float[] cloudCol = renderer.getCloudColor(partialTick);
            float cloudR = cloudCol[0];
            float cloudG = cloudCol[1];
            float cloudB = cloudCol[2];
            p.m_6180_("atmospheric_clouds");
            renderer.getAtmosphericCloudRenderer().render(stack, projMat, partialTick, camX, camY, camZ, cloudR, cloudG, cloudB);
            mc.m_91385_().m_83947_(false);
            p.m_7238_();
        }
    }

    @Override
    public void beforeWeather(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ, Frustum frustum) {
    }

    @Override
    public void afterLevel(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ, Frustum frustum) {
    }

    @Override
    public void beforeDistantHorizonsApplyShader(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ, Frustum frustum, int dhFbo) {
        RenderTarget cloudTarget = renderer.getCloudTarget();
        cloudTarget.m_83954_(Minecraft.f_91002_);
        WeightedBlendingTarget transparencyTarget = renderer.getCloudTransparencyTarget();
        transparencyTarget.m_83954_(Minecraft.f_91002_);
        GL30.glBindFramebuffer((int)36008, (int)dhFbo);
        GL30.glBindFramebuffer((int)36009, (int)((MixinRenderTargetAccessor)cloudTarget).simpleclouds$getFrameBufferId());
        GL30.glBlitFramebuffer((int)0, (int)0, (int)cloudTarget.f_83915_, (int)cloudTarget.f_83916_, (int)0, (int)0, (int)cloudTarget.f_83915_, (int)cloudTarget.f_83916_, (int)256, (int)9728);
        GL30.glBindFramebuffer((int)36009, (int)((MixinRenderTargetAccessor)((Object)transparencyTarget)).simpleclouds$getFrameBufferId());
        GL30.glBlitFramebuffer((int)0, (int)0, (int)cloudTarget.f_83915_, (int)cloudTarget.f_83916_, (int)0, (int)0, (int)transparencyTarget.f_83915_, (int)transparencyTarget.f_83916_, (int)256, (int)9728);
        GL30.glBindFramebuffer((int)36160, (int)dhFbo);
    }

    @Override
    public void afterDistantHorizonsRender(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ, Frustum frustum, int dhFbo) {
        float[] cloudCol = renderer.getCloudColor(partialTick);
        float cloudR = cloudCol[0];
        float cloudG = cloudCol[1];
        float cloudB = cloudCol[2];
        ProfilerFiller p = mc.m_91307_();
        p.m_6180_("clouds");
        stack.m_85836_();
        renderer.translateClouds(stack, camX, camY, camZ);
        p.m_6180_("clouds_opaque");
        RenderTarget cloudTarget = renderer.getCloudTarget();
        cloudTarget.m_83947_(false);
        CloudMeshGenerator generator = renderer.getMeshGenerator();
        SimpleCloudsRenderer.renderCloudsOpaque(generator, stack, projMat, renderer.getFogStart(), renderer.getFogEnd(), partialTick, cloudR, cloudG, cloudB, (Frustum)((Boolean)SimpleCloudsConfig.CLIENT.frustumCulling.get() != false ? frustum : null));
        p.m_6182_("clouds_transparent");
        WeightedBlendingTarget transparencyTarget = renderer.getCloudTransparencyTarget();
        if (generator.transparencyEnabled()) {
            renderer.copyDepthFromCloudsToTransparency();
            transparencyTarget.m_83947_(false);
            SimpleCloudsRenderer.renderCloudsTransparency(generator, stack, projMat, renderer.getFogStart(), renderer.getFogEnd(), partialTick, cloudR, cloudG, cloudB, (Frustum)((Boolean)SimpleCloudsConfig.CLIENT.frustumCulling.get() != false ? frustum : null));
        }
        p.m_7238_();
        stack.m_85849_();
        p.m_6180_("cloud_shadows");
        renderer.doCloudShadowProcessing(stack, partialTick, projMat, camX, camY, camZ, cloudTarget.m_83980_());
        p.m_7238_();
        p.m_6180_("clouds_composite");
        renderer.doFinalCompositePass(stack, partialTick, projMat);
        p.m_7238_();
        p.m_7238_();
        Matrix4f oldMcProjMat = RenderSystem.getProjectionMatrix();
        if (((Boolean)SimpleCloudsConfig.CLIENT.renderStormFog.get()).booleanValue()) {
            p.m_6180_("storm_fog");
            renderer.doStormPostProcessing(stack, partialTick, projMat, camX, camY, camZ, cloudR, cloudG, cloudB);
            RenderTarget target = renderer.getBlurTarget();
            target.m_83954_(Minecraft.f_91002_);
            target.m_83947_(true);
            FrameBufferUtils.blitTargetPreservingAlpha(renderer.getStormFogTarget(), mc.m_91268_().m_85441_(), mc.m_91268_().m_85442_());
            renderer.doBlurPostProcessing(partialTick);
            mc.m_91385_().m_83947_(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ZERO, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE);
            renderer.getBlurTarget().m_83957_(mc.m_91268_().m_85441_(), mc.m_91268_().m_85442_(), false);
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            p.m_7238_();
        }
        mc.m_91385_().m_83947_(false);
        GlStateManager._glFramebufferTexture2D((int)36160, (int)36096, (int)3553, (int)cloudTarget.m_83980_(), (int)0);
        RenderSystem.setProjectionMatrix((Matrix4f)projMat, (VertexSorting)VertexSorting.f_276450_);
        stack.m_85836_();
        stack.m_85837_(-camX, -camY, -camZ);
        DhSupportPipeline.renderLightning(renderer.getWorldEffectsManager(), renderer, mc, stack, partialTick, camX, camY, camZ);
        stack.m_85849_();
        RenderSystem.setProjectionMatrix((Matrix4f)oldMcProjMat, (VertexSorting)VertexSorting.f_276450_);
        GlStateManager._glFramebufferTexture2D((int)36160, (int)36096, (int)3553, (int)mc.m_91385_().m_83980_(), (int)0);
    }

    private static void renderLightning(WorldEffects effects, SimpleCloudsRenderer renderer, Minecraft mc, PoseStack stack, float partialTick, double camX, double camY, double camZ) {
        Tesselator tesselator = Tesselator.m_85913_();
        BufferBuilder builder = tesselator.m_85915_();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        if (effects.hasLightningToRender()) {
            float cachedFogStart = RenderSystem.getShaderFogStart();
            RenderSystem.setShaderFogStart((float)Float.MAX_VALUE);
            builder.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
            RenderSystem.setShader(GameRenderer::m_172753_);
            RenderSystem.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE);
            effects.forLightning(bolt -> {
                if (bolt.getPosition().distance((float)camX, (float)camY, (float)camZ) <= 2000.0f && bolt.getFade(partialTick) > 0.5f) {
                    mc.f_91073_.m_6580_(2);
                }
                float dist = bolt.getPosition().distance((float)camX, (float)camY, (float)camZ);
                bolt.render(stack, (VertexConsumer)builder, partialTick, 1.0f, 1.0f, 1.0f, renderer.getFadeFactorForDistance(dist));
            });
            tesselator.m_85914_();
            RenderSystem.setShaderFogStart((float)cachedFogStart);
            RenderSystem.defaultBlendFunc();
        }
        RenderSystem.disableBlend();
    }
}

