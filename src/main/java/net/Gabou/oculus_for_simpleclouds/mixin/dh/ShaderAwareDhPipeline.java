package net.Gabou.oculus_for_simpleclouds.mixin.dh;

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

/**
 * Shader-aware replica of {@link dev.nonamecrackers2.simpleclouds.client.dh.pipeline.DhSupportPipeline}
 * that merges the main depth buffer into the cloud framebuffer before delegating
 * to the standard Distant Horizons rendering flow.
 */
public class ShaderAwareDhPipeline implements CloudsRenderPipeline, ShaderAwareDhSupportPipeline {
    public static final ShaderAwareDhPipeline INSTANCE = new ShaderAwareDhPipeline();

    private ShaderAwareDhPipeline() {
    }

    @Override
    public void prepare(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ, Frustum frustum) {
    }

    @Override
    public void afterSky(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ, Frustum frustum) {
        if (((Boolean) SimpleCloudsConfig.CLIENT.atmosphericClouds.get()).booleanValue()) {
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
        GL30.glBindFramebuffer(36008, dhFbo);
        GL30.glBindFramebuffer(36009, ((MixinRenderTargetAccessor) cloudTarget).simpleclouds$getFrameBufferId());
        GL30.glBlitFramebuffer(0, 0, cloudTarget.f_83915_, cloudTarget.f_83916_, 0, 0, cloudTarget.f_83915_, cloudTarget.f_83916_, 256, 9728);
        GL30.glBindFramebuffer(36009, ((MixinRenderTargetAccessor) ((Object) transparencyTarget)).simpleclouds$getFrameBufferId());
        GL30.glBlitFramebuffer(0, 0, cloudTarget.f_83915_, cloudTarget.f_83916_, 0, 0, transparencyTarget.f_83915_, transparencyTarget.f_83916_, 256, 9728);
        renderer.copyDepthFromMainToClouds();
        renderer.copyDepthFromCloudsToTransparency();
        GL30.glBindFramebuffer(36160, dhFbo);
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
        SimpleCloudsRenderer.renderCloudsOpaque(generator, stack, projMat, renderer.getFogStart(), renderer.getFogEnd(), partialTick, cloudR, cloudG, cloudB, (Frustum) (((Boolean) SimpleCloudsConfig.CLIENT.frustumCulling.get()).booleanValue() ? frustum : null));
        p.m_6182_("clouds_transparent");
        WeightedBlendingTarget transparencyTarget = renderer.getCloudTransparencyTarget();
        if (generator.transparencyEnabled()) {
            renderer.copyDepthFromCloudsToTransparency();
            transparencyTarget.m_83947_(false);
            SimpleCloudsRenderer.renderCloudsTransparency(generator, stack, projMat, renderer.getFogStart(), renderer.getFogEnd(), partialTick, cloudR, cloudG, cloudB, (Frustum) (((Boolean) SimpleCloudsConfig.CLIENT.frustumCulling.get()).booleanValue() ? frustum : null));
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
        if (((Boolean) SimpleCloudsConfig.CLIENT.renderStormFog.get()).booleanValue()) {
            p.m_6180_("storm_fog");
            renderer.doStormPostProcessing(stack, partialTick, projMat, camX, camY, camZ, cloudR, cloudG, cloudB);
            RenderTarget target = renderer.getBlurTarget();
            target.m_83954_(Minecraft.f_91002_);
            target.m_83947_(true);
            FrameBufferUtils.blitTargetPreservingAlpha(renderer.getStormFogTarget(), mc.m_91268_().m_85441_(), mc.m_91268_().m_85442_());
            renderer.doBlurPostProcessing(partialTick);
            mc.m_91385_().m_83947_(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            renderer.getBlurTarget().m_83957_(mc.m_91268_().m_85441_(), mc.m_91268_().m_85442_(), false);
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            p.m_7238_();
        }
        mc.m_91385_().m_83947_(false);
        GlStateManager._glFramebufferTexture2D(36160, 36096, 3553, cloudTarget.m_83980_(), 0);
        RenderSystem.setProjectionMatrix(projMat, VertexSorting.f_276450_);
        stack.m_85836_();
        stack.m_85837_(-camX, -camY, -camZ);
        ShaderAwareDhPipeline.renderLightning(renderer.getWorldEffectsManager(), renderer, mc, stack, partialTick, camX, camY, camZ);
        stack.m_85849_();
        RenderSystem.setProjectionMatrix(oldMcProjMat, VertexSorting.f_276450_);
        GlStateManager._glFramebufferTexture2D(36160, 36096, 3553, mc.m_91385_().m_83980_(), 0);
    }

    private static void renderLightning(WorldEffects effects, SimpleCloudsRenderer renderer, Minecraft mc, PoseStack stack, float partialTick, double camX, double camY, double camZ) {
        Tesselator tesselator = Tesselator.m_85913_();
        BufferBuilder builder = tesselator.m_85915_();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        if (effects.hasLightningToRender()) {
            float cachedFogStart = RenderSystem.getShaderFogStart();
            RenderSystem.setShaderFogStart(Float.MAX_VALUE);
            builder.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
            RenderSystem.setShader(GameRenderer::m_172753_);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            effects.forLightning(bolt -> {
                if (bolt.getPosition().distance((float) camX, (float) camY, (float) camZ) <= 2000.0f && bolt.getFade(partialTick) > 0.5f) {
                    mc.f_91073_.m_6580_(2);
                }
                float dist = bolt.getPosition().distance((float) camX, (float) camY, (float) camZ);
                bolt.render(stack, (VertexConsumer) builder, partialTick, 1.0f, 1.0f, 1.0f, renderer.getFadeFactorForDistance(dist));
            });
            tesselator.m_85914_();
            RenderSystem.setShaderFogStart(cachedFogStart);
            RenderSystem.defaultBlendFunc();
        }
        RenderSystem.disableBlend();
    }

    @Override
    public boolean oculus_for_simpleclouds$isShaderAware() {
        return true;
    }
}
