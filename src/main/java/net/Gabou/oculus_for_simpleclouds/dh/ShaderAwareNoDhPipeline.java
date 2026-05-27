package net.Gabou.oculus_for_simpleclouds.dh;

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
import dev.nonamecrackers2.simpleclouds.common.cloud.SimpleCloudsConstants;
import dev.nonamecrackers2.simpleclouds.client.renderer.pipeline.CloudsRenderPipeline;
import dev.nonamecrackers2.simpleclouds.common.config.SimpleCloudsConfig;
import dev.nonamecrackers2.simpleclouds.mixin.MixinRenderTargetAccessor;
import net.Gabou.oculus_for_simpleclouds.client.FinalCloudCompositeHandler;
import net.Gabou.oculus_for_simpleclouds.client.FinalCloudCompositeHandler.DepthSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.profiling.ProfilerFiller;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 * Shader-aware pipeline used when shaders are enabled but Distant Horizons is absent.
 * Copies vanilla depth into the cloud framebuffer in a shader-friendly way and reuses
 * the standard SimpleClouds rendering flow without any DH-specific steps.
 */
public class ShaderAwareNoDhPipeline implements CloudsRenderPipeline, ShaderAwareDhSupportPipeline {
    public static final ShaderAwareNoDhPipeline INSTANCE = new ShaderAwareNoDhPipeline();
    private static final long DEBUG_INTERVAL_MS = 1000L;
    private static long lastDebugMs = 0L;
    private static String lastDebugMsg = "";
    private static final boolean DEBUG_LOGGING = Boolean.getBoolean("ofsc.debug.dhPipeline");
    private static boolean warnedZeroVerts = false;
    private static boolean warnedStormFogSkipped = false;
    public static final boolean DEBUG_BLIT_CLOUD_TARGET = Boolean.getBoolean("ofsc.debug.blitClouds");
    public static final boolean ENABLE_STORM_FOG_WITH_SHADERS = Boolean.parseBoolean(System.getProperty("ofsc.enableStormFogWithShaders", "true"));
    public static final boolean ENABLE_TRANSPARENT_CLOUDS_WITH_SHADERS = Boolean.getBoolean("ofsc.enableTransparentCloudsWithShaders");

    private final CloudsRenderPipeline vanilla = CloudsRenderPipeline.SHADER_SUPPORT;

    private ShaderAwareNoDhPipeline() {
    }

    @Override
    public void prepare(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack stack, Matrix4f projMat,
                        float partialTick, double camX, double camY, double camZ, Frustum frustum) {
        vanilla.prepare(mc, renderer, stack, projMat, partialTick, camX, camY, camZ, frustum);
    }

    @Override
    public void beforeWeather(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack stack, Matrix4f projMat,
                              float partialTick, double camX, double camY, double camZ, Frustum frustum) {
        renderer.getWorldEffectsManager().renderPost(stack, partialTick, camX, camY, camZ, (float) SimpleCloudsConstants.CLOUD_SCALE);
        vanilla.beforeWeather(mc, renderer, stack, projMat, partialTick, camX, camY, camZ, frustum);
    }

    @Override
    public void afterLevel(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack stack, Matrix4f projMat,
                           float partialTick, double camX, double camY, double camZ, Frustum frustum) {
        if (!CompatHelper.areShadersRunning()) {
            vanilla.afterLevel(mc, renderer, stack, projMat, partialTick, camX, camY, camZ, frustum);
            return;
        }

        RenderTarget cloudTarget = renderer.getCloudTarget();
        if (cloudTarget == null) {
            return;
        }

        cloudTarget.clear(Minecraft.ON_OSX);
        WeightedBlendingTarget transparencyTarget = renderer.getCloudTransparencyTarget();
        transparencyTarget.clear(Minecraft.ON_OSX);

        RenderTarget mainTarget = mc.getMainRenderTarget();
        DepthSource vanillaDepthSource = FinalCloudCompositeHandler.getCapturedSceneDepthSource();
        if (!vanillaDepthSource.isValid()) {
            vanillaDepthSource = FinalCloudCompositeHandler.captureVanillaDepthSource(mainTarget);
        }
        boolean copiedVanillaDepth = FinalCloudCompositeHandler.copyDepthToTarget(cloudTarget, vanillaDepthSource);
        FinalCloudCompositeHandler.logDepthSnapshot("shader_no_dh_after_copy",
                mainTarget == null ? -1 : mainTarget.getDepthTextureId(),
                cloudTarget.getDepthTextureId());
        debug(String.format(
                "Shader-only pass: copiedVanilla=%s cloudDepth=%d cloudSize=%dx%d mainDepth=%d",
                copiedVanillaDepth, cloudTarget.getDepthTextureId(), cloudTarget.width, cloudTarget.height,
                mainTarget == null ? -1 : mainTarget.getDepthTextureId()
        ));
        if (!copiedVanillaDepth) {
            System.out.println("[OFSC WARN] Could not copy vanilla depth; clouds may render incorrectly this frame.");
        }

        CloudMeshGenerator generator = renderer.getMeshGenerator();
        int opaqueVerts = generator != null ? generator.getOpaqueBufferBytesUsed() / 24 : -1;
        int transparentVerts = generator != null ? generator.getTransparentBufferBytesUsed() / 24 : -1;
        if (opaqueVerts == 0 && transparentVerts == 0) {
            System.out.println("[OFSC WARN] Cloud mesh generator produced zero vertices (opaque and transparent). Status=" +
                    (generator == null ? "null" : generator.getMeshGenStatus()));
            warnedZeroVerts = true;
        } else if (warnedZeroVerts) {
            warnedZeroVerts = false;
        }

        float[] cloudCol = renderer.getCloudColor(partialTick);
        float cloudR = cloudCol[0];
        float cloudG = cloudCol[1];
        float cloudB = cloudCol[2];
        ProfilerFiller p = mc.getProfiler();
        p.push("clouds");
        boolean depthEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        int prevDepthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        boolean prevDepthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        if (DEBUG_BLIT_CLOUD_TARGET) {
            cloudTarget.bindWrite(false);
            GL11.glClearColor(1.0f, 0.0f, 1.0f, 1.0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        }
        stack.pushPose();
        renderer.translateClouds(stack, camX, camY, camZ);
        p.push("clouds_opaque");
        cloudTarget.bindWrite(false);
        SimpleCloudsRenderer.renderCloudsOpaque(generator, stack, projMat, renderer.getFogStart(), renderer.getFogEnd(), partialTick,
                cloudR, cloudG, cloudB, ((Boolean) SimpleCloudsConfig.CLIENT.frustumCulling.get()).booleanValue() ? frustum : null);
        p.popPush("clouds_transparent");
        if (ENABLE_TRANSPARENT_CLOUDS_WITH_SHADERS && generator.transparencyEnabled() && transparentVerts > 0) {
            renderer.copyDepthFromCloudsToTransparency();
            transparencyTarget.bindWrite(false);
            boolean depthMaskBeforeTransparent = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
            GL11.glDepthMask(false);
            SimpleCloudsRenderer.renderCloudsTransparency(generator, stack, projMat, renderer.getFogStart(), renderer.getFogEnd(),
                    partialTick, cloudR, cloudG, cloudB, ((Boolean) SimpleCloudsConfig.CLIENT.frustumCulling.get()).booleanValue() ? frustum : null);
            GL11.glDepthMask(depthMaskBeforeTransparent);
        }
        p.pop();
        stack.popPose();
        if (!depthEnabled) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }
        GL11.glDepthFunc(prevDepthFunc);
        GL11.glDepthMask(prevDepthMask);
        p.push("cloud_shadows");
        renderer.doCloudShadowProcessing(stack, partialTick, projMat, camX, camY, camZ, cloudTarget.getDepthTextureId());
        p.pop();
        p.pop();
        Matrix4f oldMcProjMat = RenderSystem.getProjectionMatrix();
        if (((Boolean) SimpleCloudsConfig.CLIENT.renderStormFog.get()).booleanValue() && ENABLE_STORM_FOG_WITH_SHADERS) {
            p.push("storm_fog");
            renderer.doStormPostProcessing(stack, partialTick, projMat, camX, camY, camZ, cloudR, cloudG, cloudB);
            RenderTarget target = renderer.getBlurTarget();
            target.clear(Minecraft.ON_OSX);
            target.bindWrite(true);
            FrameBufferUtils.blitTargetPreservingAlpha(renderer.getStormFogTarget(), mc.getWindow().getWidth(), mc.getWindow().getHeight());
            renderer.doBlurPostProcessing(partialTick);
            mc.getMainRenderTarget().bindWrite(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            renderer.getBlurTarget().blitToScreen(mc.getWindow().getWidth(), mc.getWindow().getHeight(), false);
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setProjectionMatrix(projMat, VertexSorting.DISTANCE_TO_ORIGIN);
            p.pop();
        } else if (((Boolean) SimpleCloudsConfig.CLIENT.renderStormFog.get()).booleanValue() && !warnedStormFogSkipped) {
            warnedStormFogSkipped = true;
        }

        mc.getMainRenderTarget().bindWrite(false);
        int previousMainDepthType = GL30.glGetFramebufferAttachmentParameteri(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE);
        int previousMainDepthName = GL30.glGetFramebufferAttachmentParameteri(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME);
        try {
            GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_TEXTURE_2D, cloudTarget.getDepthTextureId(), 0);
            RenderSystem.setProjectionMatrix(projMat, VertexSorting.DISTANCE_TO_ORIGIN);

            stack.pushPose();
            stack.translate(-camX, -camY, -camZ);
            renderLightning(renderer.getWorldEffectsManager(), renderer, mc, stack, partialTick, camX, camY, camZ);
            stack.popPose();

            if (DEBUG_BLIT_CLOUD_TARGET) {
                debug("DEBUG_BLIT_CLOUD_TARGET active; blitting cloud target to screen");
                mc.getMainRenderTarget().bindWrite(false);
                cloudTarget.blitToScreen(mc.getWindow().getWidth(), mc.getWindow().getHeight(), false);
                GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, ((MixinRenderTargetAccessor) cloudTarget).simpleclouds$getFrameBufferId());
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
                GL30.glBlitFramebuffer(0, 0, cloudTarget.width, cloudTarget.height, 0, 0, mc.getWindow().getWidth(), mc.getWindow().getHeight(), GL11.GL_COLOR_BUFFER_BIT, GL11.GL_LINEAR);
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, mc.getMainRenderTarget().frameBufferId);
            }
        } finally {
            RenderSystem.setProjectionMatrix(oldMcProjMat, VertexSorting.DISTANCE_TO_ORIGIN);
            mc.getMainRenderTarget().bindWrite(false);
            restoreDepthAttachment(previousMainDepthType, previousMainDepthName);
        }
    }

    @Override
    public void afterSky(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack stack, Matrix4f projMat,
                         float partialTick, double camX, double camY, double camZ, Frustum frustum) {
        vanilla.afterSky(mc, renderer, stack, projMat, partialTick, camX, camY, camZ, frustum);

        if (SimpleCloudsConfig.CLIENT.atmosphericClouds.get()) {
            ProfilerFiller p = mc.getProfiler();
            float[] cloudCol = renderer.getCloudColor(partialTick);
            p.push("atmospheric_clouds");
            renderer.getAtmosphericCloudRenderer().render(
                    stack, projMat, partialTick, camX, camY, camZ,
                    cloudCol[0], cloudCol[1], cloudCol[2]
            );
            mc.getMainRenderTarget().bindWrite(false);
            p.pop();
        }
    }

    @Override
    public boolean oculus_for_simpleclouds$isShaderAware() {
        return true;
    }

    private static void renderLightning(WorldEffects effects, SimpleCloudsRenderer renderer, Minecraft mc, PoseStack stack, float partialTick, double camX, double camY, double camZ) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        if (effects.hasLightningToRender()) {
            float cachedFogStart = RenderSystem.getShaderFogStart();
            RenderSystem.setShaderFogStart(Float.MAX_VALUE);
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            RenderSystem.setShader(GameRenderer::getRendertypeLightningShader);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            effects.forLightning(bolt -> {
                if (bolt.getPosition().distance((float) camX, (float) camY, (float) camZ) <= 2000.0f && bolt.getFade(partialTick) > 0.5f) {
                    mc.level.setSkyFlashTime(2);
                }
                float dist = bolt.getPosition().distance((float) camX, (float) camY, (float) camZ);
                bolt.render(stack, (VertexConsumer) builder, partialTick, 1.0f, 1.0f, 1.0f, renderer.getFadeFactorForDistance(dist));
            });
            tesselator.end();
            RenderSystem.setShaderFogStart(cachedFogStart);
            RenderSystem.defaultBlendFunc();
        }
        RenderSystem.disableBlend();
    }

    private static void restoreDepthAttachment(int attachmentType, int attachmentName) {
        if (attachmentType == GL11.GL_TEXTURE && attachmentName > 0) {
            GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_TEXTURE_2D, attachmentName, 0);
        } else if (attachmentType == GL30.GL_RENDERBUFFER && attachmentName > 0) {
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, attachmentName);
        } else {
            GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_TEXTURE_2D, 0, 0);
        }
    }

    private static void debug(String msg) {
        if (!DEBUG_LOGGING) {
            return;
        }
        long now = System.currentTimeMillis();
        if (!msg.equals(lastDebugMsg) || now - lastDebugMs > DEBUG_INTERVAL_MS) {
            //System.out.println("[OFSC DEBUG] " + msg);
            lastDebugMsg = msg;
            lastDebugMs = now;
        }
    }
}

