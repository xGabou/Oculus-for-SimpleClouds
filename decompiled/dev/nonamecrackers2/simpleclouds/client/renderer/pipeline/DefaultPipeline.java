/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  com.mojang.blaze3d.platform.GlStateManager$DestFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SourceFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexSorting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.level.material.FogType
 *  nonamecrackers2.crackerslib.common.compat.CompatHelper
 *  org.joml.Matrix4f
 */
package dev.nonamecrackers2.simpleclouds.client.renderer.pipeline;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import dev.nonamecrackers2.simpleclouds.client.framebuffer.FrameBufferUtils;
import dev.nonamecrackers2.simpleclouds.client.framebuffer.WeightedBlendingTarget;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.renderer.pipeline.CloudsRenderPipeline;
import dev.nonamecrackers2.simpleclouds.client.world.FogRenderMode;
import dev.nonamecrackers2.simpleclouds.common.config.SimpleCloudsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.material.FogType;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import org.joml.Matrix4f;

public class DefaultPipeline
implements CloudsRenderPipeline {
    protected DefaultPipeline() {
    }

    @Override
    public void prepare(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ, Frustum frustum) {
    }

    @Override
    public void afterSky(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ, Frustum frustum) {
        ProfilerFiller p = mc.m_91307_();
        float[] cloudCol = renderer.getCloudColor(partialTick);
        float cloudR = cloudCol[0];
        float cloudG = cloudCol[1];
        float cloudB = cloudCol[2];
        if (((Boolean)SimpleCloudsConfig.CLIENT.atmosphericClouds.get()).booleanValue()) {
            p.m_6180_("atmospheric_clouds");
            renderer.getAtmosphericCloudRenderer().render(stack, projMat, partialTick, camX, camY, camZ, cloudR, cloudG, cloudB);
            mc.m_91385_().m_83947_(false);
            p.m_7238_();
        }
        p.m_6180_("clouds");
        stack.m_85836_();
        renderer.translateClouds(stack, camX, camY, camZ);
        p.m_6180_("clouds_opaque");
        RenderTarget cloudTarget = renderer.getCloudTarget();
        cloudTarget.m_83954_(Minecraft.f_91002_);
        cloudTarget.m_83947_(false);
        CloudMeshGenerator generator = renderer.getMeshGenerator();
        SimpleCloudsRenderer.renderCloudsOpaque(generator, stack, projMat, renderer.getFogStart(), renderer.getFogEnd(), partialTick, cloudR, cloudG, cloudB, (Frustum)((Boolean)SimpleCloudsConfig.CLIENT.frustumCulling.get() != false ? frustum : null));
        renderer.copyDepthFromCloudsToMain();
        p.m_6182_("clouds_transparent");
        WeightedBlendingTarget transparencyTarget = renderer.getCloudTransparencyTarget();
        transparencyTarget.m_83954_(Minecraft.f_91002_);
        if (generator.transparencyEnabled()) {
            renderer.copyDepthFromCloudsToTransparency();
            transparencyTarget.m_83947_(false);
            SimpleCloudsRenderer.renderCloudsTransparency(generator, stack, projMat, renderer.getFogStart(), renderer.getFogEnd(), partialTick, cloudR, cloudG, cloudB, (Frustum)((Boolean)SimpleCloudsConfig.CLIENT.frustumCulling.get() != false ? frustum : null));
        }
        p.m_7238_();
        stack.m_85849_();
        p.m_6180_("clouds_composite");
        renderer.doFinalCompositePass(stack, partialTick, projMat);
        p.m_7238_();
        p.m_7238_();
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
            RenderSystem.setProjectionMatrix((Matrix4f)projMat, (VertexSorting)VertexSorting.f_276450_);
            p.m_7238_();
        }
        mc.m_91385_().m_83947_(CompatHelper.isVrActive());
    }

    @Override
    public void beforeWeather(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ, Frustum frustum) {
        if (SimpleCloudsConfig.CLIENT.fogMode.get() == FogRenderMode.SCREEN_SPACE && mc.f_91063_.m_109153_().m_167685_() == FogType.NONE) {
            renderer.doScreenSpaceWorldFog(stack, projMat, partialTick);
            mc.m_91385_().m_83947_(false);
        }
    }

    @Override
    public void afterLevel(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ, Frustum frustum) {
    }
}

