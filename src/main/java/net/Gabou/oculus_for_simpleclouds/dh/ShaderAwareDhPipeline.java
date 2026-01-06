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
import dev.nonamecrackers2.simpleclouds.client.dh.pipeline.DhSupportPipeline;
import dev.nonamecrackers2.simpleclouds.client.framebuffer.FrameBufferUtils;
import dev.nonamecrackers2.simpleclouds.client.framebuffer.WeightedBlendingTarget;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.renderer.WorldEffects;
import dev.nonamecrackers2.simpleclouds.client.renderer.pipeline.CloudsRenderPipeline;
import dev.nonamecrackers2.simpleclouds.common.config.SimpleCloudsConfig;
import dev.nonamecrackers2.simpleclouds.mixin.MixinRenderTargetAccessor;
import net.Gabou.oculus_for_simpleclouds.client.FinalCloudCompositeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.profiling.ProfilerFiller;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL12;

import java.nio.IntBuffer;

/**
 * Shader-aware replica of {@link dev.nonamecrackers2.simpleclouds.client.dh.pipeline.DhSupportPipeline}
 * that merges the main depth buffer into the cloud framebuffer before delegating
 * to the standard Distant Horizons rendering flow.
 */
public class ShaderAwareDhPipeline implements CloudsRenderPipeline, ShaderAwareDhSupportPipeline {
    public static final ShaderAwareDhPipeline INSTANCE = new ShaderAwareDhPipeline();
    private static final long DEBUG_INTERVAL_MS = 1000L;
    private static long lastDebugMs = 0L;
    private static String lastDebugMsg = "";
    private static boolean warnedZeroVerts = false;
    public static final boolean DEBUG_BLIT_CLOUD_TARGET = Boolean.getBoolean("ofsc.debug.blitClouds");
    private static int depthMergeDhCopyTex = -1;
    private static int depthMergeDhCopyW = -1;
    private static int depthMergeDhCopyH = -1;
    private static int combinedDepthTex = -1;
    private static int combinedDepthFbo = -1;
    private static int combinedDepthW = -1;
    private static int combinedDepthH = -1;
    private static int depthCombineProgram = -1;
    private static int depthCombineVao = -1;
    private static int depthCombineVbo = -1;
    private static int depthCombineDhSamplerLoc = -1;
    private static int depthCombineBaseSamplerLoc = -1;
    private static int depthCombineReverseLoc = -1;

    private ShaderAwareDhPipeline() {
    }
    public static boolean skipDepthCopy = false;

    private final CloudsRenderPipeline vanilla =
            DhSupportPipeline.INSTANCE;


    @Override
    public void prepare(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack stack, Matrix4f projMat,
                        float partialTick, double camX, double camY, double camZ, Frustum frustum) {
        vanilla.prepare(mc, renderer, stack, projMat, partialTick, camX, camY, camZ, frustum);
    }

    @Override
    public void beforeWeather(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack stack, Matrix4f projMat,
                              float partialTick, double camX, double camY, double camZ, Frustum frustum) {
        vanilla.beforeWeather(mc, renderer, stack, projMat, partialTick, camX, camY, camZ, frustum);
    }

    @Override
    public void afterLevel(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack stack, Matrix4f projMat,
                           float partialTick, double camX, double camY, double camZ, Frustum frustum) {
        vanilla.afterLevel(mc, renderer, stack, projMat, partialTick, camX, camY, camZ, frustum);
    }




    @Override
    public void afterSky(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack stack, Matrix4f projMat,
                         float partialTick, double camX, double camY, double camZ, Frustum frustum) {

        // This generates the actual cloud meshes.
        vanilla.afterSky(mc, renderer, stack, projMat, partialTick, camX, camY, camZ, frustum);

        // Your shader-aware atmospheric clouds
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
    public void beforeDistantHorizonsApplyShader(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ, Frustum frustum, int dhFbo) {
        if (!CompatHelper.areShadersRunning()) {
            vanilla.beforeDistantHorizonsApplyShader(
                    mc, renderer, stack, projMat, partialTick, camX, camY, camZ, frustum, dhFbo
            );
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
        // Capture vanilla depth now (pre-shader) for final composite occlusion.
        FinalCloudCompositeHandler.captureDepth(mainTarget);
        boolean copiedVanillaDepth = copyVanillaDepthToCloudTarget(cloudTarget, mainTarget);
        int vanillaDepthTex = FinalCloudCompositeHandler.getExternalSceneDepthTex();
        if (vanillaDepthTex <= 0) {
            vanillaDepthTex = FinalCloudCompositeHandler.getCapturedSceneDepthTex();
        }
        int vanillaW = FinalCloudCompositeHandler.getCapturedW();
        int vanillaH = FinalCloudCompositeHandler.getCapturedH();
        int dhDepthTex = resolveDepthAttachmentAsTexture(dhFbo, vanillaW > 0 ? vanillaW : cloudTarget.width, vanillaH > 0 ? vanillaH : cloudTarget.height);
        boolean mergedDh = dhDepthTex > 0 && mergeDhDepthIntoCloudDepth(cloudTarget, dhFbo);
        if (dhDepthTex > 0 && vanillaDepthTex > 0) {
            int combinedTex = mergeDepthForComposite(dhDepthTex, vanillaDepthTex, vanillaW > 0 ? vanillaW : cloudTarget.width, vanillaH > 0 ? vanillaH : cloudTarget.height, detectReverseDepth());
            if (combinedTex > 0) {
                FinalCloudCompositeHandler.setCombinedSceneDepthTex(combinedTex, vanillaW > 0 ? vanillaW : cloudTarget.width, vanillaH > 0 ? vanillaH : cloudTarget.height);
            }
        } else if (vanillaDepthTex > 0) {
            FinalCloudCompositeHandler.setCombinedSceneDepthTex(vanillaDepthTex, vanillaW, vanillaH);
        }
        debug(String.format(
                "DH shader pass: copiedVanilla=%s dhDepthTex=%d mergedDh=%s cloudDepth=%d cloudSize=%dx%d mainDepth=%d",
                copiedVanillaDepth, dhDepthTex, mergedDh, cloudTarget.getDepthTextureId(),
                cloudTarget.width, cloudTarget.height, mainTarget == null ? -1 : mainTarget.getDepthTextureId()
        ));
        if (!copiedVanillaDepth && !mergedDh) {
            System.out.println("[OFSC WARN] Could not copy vanilla depth and DH merge failed; clouds may render incorrectly this frame.");
        } else if (!mergedDh) {
            System.out.println("[OFSC WARN] DH depth merge unavailable; using vanilla depth only.");
        } else if (!copiedVanillaDepth) {
            System.out.println("[OFSC WARN] Vanilla depth copy failed; relying on DH depth only.");
        }
        GL30.glBindFramebuffer(36160, dhFbo);
    }

    @Override
    public void afterDistantHorizonsRender(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ, Frustum frustum, int dhFbo) {
        if (!CompatHelper.areShadersRunning()) {
            vanilla.afterDistantHorizonsRender(
                    mc, renderer, stack, projMat, partialTick, camX, camY, camZ, frustum, dhFbo
            );
            return;
        }
        RenderTarget cloudTarget = renderer.getCloudTarget();
        debug(String.format("DH clouds render: cloudDepth=%d color=%d size=%dx%d",
                cloudTarget.getDepthTextureId(),
                cloudTarget.getColorTextureId(),
                cloudTarget.width, cloudTarget.height));
        CloudMeshGenerator generator = renderer.getMeshGenerator();
        int opaqueVerts = generator != null ? generator.getOpaqueBufferBytesUsed() / 24 : -1;
        int transparentVerts = generator != null ? generator.getTransparentBufferBytesUsed() / 24 : -1;
        debug(String.format("DH clouds render: generator=%s transparency=%s fogStart=%.1f fogEnd=%.1f opaqueVerts=%d transparentVerts=%d",
                generator, generator != null && generator.transparencyEnabled(),
                renderer.getFogStart(), renderer.getFogEnd(), opaqueVerts, transparentVerts));
        if (opaqueVerts == 0 && transparentVerts == 0) {
            System.out.println("[OFSC WARN] Cloud mesh generator produced zero vertices (opaque and transparent). Status=" + (generator == null ? "null" : generator.getMeshGenStatus()));
            warnedZeroVerts = true;
        } else if (warnedZeroVerts) {
            // Only log the warning once until state changes
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
        GL11.glDepthFunc(GL11.GL_LEQUAL);
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
        SimpleCloudsRenderer.renderCloudsOpaque(generator, stack, projMat, renderer.getFogStart(), renderer.getFogEnd(), partialTick, cloudR, cloudG, cloudB, ((Boolean) SimpleCloudsConfig.CLIENT.frustumCulling.get()).booleanValue() ? frustum : null);
        p.popPush("clouds_transparent");
        WeightedBlendingTarget transparencyTarget = renderer.getCloudTransparencyTarget();
        if (generator.transparencyEnabled()) {
            renderer.copyDepthFromCloudsToTransparency();
            transparencyTarget.bindWrite(false);
            boolean depthMaskBeforeTransparent = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
            GL11.glDepthMask(false);
            SimpleCloudsRenderer.renderCloudsTransparency(generator, stack, projMat, renderer.getFogStart(), renderer.getFogEnd(), partialTick, cloudR, cloudG, cloudB, ((Boolean) SimpleCloudsConfig.CLIENT.frustumCulling.get()).booleanValue() ? frustum : null);
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
        p.push("clouds_composite");
        renderer.doFinalCompositePass(stack, partialTick, projMat);
        p.pop();
        p.pop();
        Matrix4f oldMcProjMat = RenderSystem.getProjectionMatrix();
        if (((Boolean) SimpleCloudsConfig.CLIENT.renderStormFog.get()).booleanValue()) {
            p.push("storm_fog");
            renderer.doStormPostProcessing(stack, partialTick, projMat, camX, camY, camZ, cloudR, cloudG, cloudB);
            RenderTarget target = renderer.getBlurTarget();
            target.clear(Minecraft.ON_OSX);
            target.bindWrite(true);
            FrameBufferUtils.blitTargetPreservingAlpha(renderer.getStormFogTarget(), mc.getWindow().getWidth(), mc.getWindow().getHeight());
            renderer.doBlurPostProcessing(partialTick);
            mc.getMainRenderTarget().bindWrite(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            renderer.getBlurTarget().blitToScreen(mc.getWindow().getWidth(), mc.getWindow().getHeight(), false);
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            p.pop();
        }

        mc.getMainRenderTarget().bindWrite(false);
        GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_TEXTURE_2D, cloudTarget.getColorTextureId(), 0);
        RenderSystem.setProjectionMatrix(projMat, VertexSorting.DISTANCE_TO_ORIGIN);

        stack.pushPose();
        stack.translate(-camX, -camY, -camZ);
        ShaderAwareDhPipeline.renderLightning(renderer.getWorldEffectsManager(), renderer, mc, stack, partialTick, camX, camY, camZ);
        stack.popPose();

        if (DEBUG_BLIT_CLOUD_TARGET) {
            debug("DEBUG_BLIT_CLOUD_TARGET active; blitting cloud target to screen");
            mc.getMainRenderTarget().bindWrite(false);
            cloudTarget.blitToScreen(mc.getWindow().getWidth(), mc.getWindow().getHeight(), false);
            // Also blit to default framebuffer to bypass any custom main target
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, ((MixinRenderTargetAccessor) cloudTarget).simpleclouds$getFrameBufferId());
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
            GL30.glBlitFramebuffer(0, 0, cloudTarget.width, cloudTarget.height, 0, 0, mc.getWindow().getWidth(), mc.getWindow().getHeight(), GL11.GL_COLOR_BUFFER_BIT, GL11.GL_LINEAR);
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, mc.getMainRenderTarget().frameBufferId);
        }

        RenderSystem.setProjectionMatrix(oldMcProjMat, VertexSorting.DISTANCE_TO_ORIGIN);
        GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_TEXTURE_2D, mc.getMainRenderTarget().getColorTextureId(), 0);
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

    @Override
    public boolean oculus_for_simpleclouds$isShaderAware() {
        return true;
    }

    private static boolean detectReverseDepth() {
        int depthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        float depthClear = GL11.glGetFloat(GL11.GL_DEPTH_CLEAR_VALUE);
        return depthFunc == GL11.GL_GEQUAL || depthFunc == GL11.GL_GREATER || depthClear < 0.5f;
    }

    /**
     * Merge the DH depth attachment into the cloud target's existing depth buffer.
     * The cloud target is expected to already contain vanilla depth.
     */
    private static boolean mergeDhDepthIntoCloudDepth(RenderTarget cloudTarget, int dhFbo) {
        int dhDepthTex = resolveDepthAttachmentAsTexture(dhFbo, cloudTarget.width, cloudTarget.height);
        if (dhDepthTex <= 0) {
            return false;
        }
        if (!ensureDepthMergeProgram()) {
            return false;
        }

        GlStateManager._getError(); // clear previous errors
        int previousFbo = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        IntBuffer viewport = BufferUtils.createIntBuffer(4);
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean depthEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, ((MixinRenderTargetAccessor) cloudTarget).simpleclouds$getFrameBufferId());
        GL11.glViewport(0, 0, cloudTarget.width, cloudTarget.height);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glColorMask(false, false, false, false);

        GL20.glUseProgram(depthMergeProgram);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, dhDepthTex);
        if (depthMergeSamplerLoc >= 0) {
            GL20.glUniform1i(depthMergeSamplerLoc, 0);
        }

        GL30.glBindVertexArray(depthMergeVao);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
        GL30.glBindVertexArray(0);

        GL20.glUseProgram(0);
        GL11.glColorMask(true, true, true, true);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        if (!depthEnabled) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }
        if (blendEnabled) {
            GL11.glEnable(GL11.GL_BLEND);
        } else {
            GL11.glDisable(GL11.GL_BLEND);
        }
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, previousFbo);
        GL11.glViewport(viewport.get(0), viewport.get(1), viewport.get(2), viewport.get(3));
        return GlStateManager._getError() == GL11.GL_NO_ERROR;
    }

    private static int resolveDepthAttachmentAsTexture(int fbo, int fallbackW, int fallbackH) {
        int previousFbo = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
        int attachmentType = GL30.glGetFramebufferAttachmentParameteri(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE);
        int depthName = -1;
        if (attachmentType == GL11.GL_TEXTURE) {
            depthName = GL30.glGetFramebufferAttachmentParameteri(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME);
        } else if (attachmentType == GL30.GL_RENDERBUFFER) {
            int rbName = GL30.glGetFramebufferAttachmentParameteri(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME);
            if (rbName > 0) {
                int prevRb = GL11.glGetInteger(GL30.GL_RENDERBUFFER_BINDING);
                GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rbName);
                int w = GL30.glGetRenderbufferParameteri(GL30.GL_RENDERBUFFER, GL30.GL_RENDERBUFFER_WIDTH);
                int h = GL30.glGetRenderbufferParameteri(GL30.GL_RENDERBUFFER, GL30.GL_RENDERBUFFER_HEIGHT);
                if (w <= 0 || h <= 0) {
                    w = fallbackW;
                    h = fallbackH;
                }
                ensureDhDepthCopyTexture(w, h);
                int prevReadFbo = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
                int prevReadBuffer = GL11.glGetInteger(GL11.GL_READ_BUFFER);
                GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, fbo);
                GL11.glReadBuffer(GL11.GL_NONE);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthMergeDhCopyTex);
                GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, w, h);
                GL11.glReadBuffer(prevReadBuffer);
                GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, prevReadFbo);
                GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, prevRb);
                depthName = depthMergeDhCopyTex;
            }
        }
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, previousFbo);
        return depthName;
    }

    private static boolean ensureDepthMergeProgram() {
        if (depthMergeProgram != -1 && depthMergeVao != -1 && depthMergeVbo != -1) {
            return true;
        }
        String vertexSrc = "#version 150\nin vec2 aPos;out vec2 vUv;void main(){vUv=aPos*0.5+0.5;gl_Position=vec4(aPos,0.0,1.0);}";
        String fragmentSrc = "#version 150\nin vec2 vUv;uniform sampler2D uDepth;void main(){float d=texture(uDepth,vUv).r;gl_FragDepth=d;}";
        int vert = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vert, vertexSrc);
        GL20.glCompileShader(vert);
        if (GL20.glGetShaderi(vert, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
            System.out.println("[OFSC WARN] Depth merge vertex shader compile failed: " + GL20.glGetShaderInfoLog(vert));
            GL20.glDeleteShader(vert);
            return false;
        }
        int frag = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(frag, fragmentSrc);
        GL20.glCompileShader(frag);
        if (GL20.glGetShaderi(frag, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
            System.out.println("[OFSC WARN] Depth merge fragment shader compile failed: " + GL20.glGetShaderInfoLog(frag));
            GL20.glDeleteShader(vert);
            GL20.glDeleteShader(frag);
            return false;
        }

        depthMergeProgram = GL20.glCreateProgram();
        GL20.glAttachShader(depthMergeProgram, vert);
        GL20.glAttachShader(depthMergeProgram, frag);
        GL20.glLinkProgram(depthMergeProgram);
        GL20.glDeleteShader(vert);
        GL20.glDeleteShader(frag);
        if (GL20.glGetProgrami(depthMergeProgram, GL20.GL_LINK_STATUS) != GL11.GL_TRUE) {
            System.out.println("[OFSC WARN] Depth merge program link failed: " + GL20.glGetProgramInfoLog(depthMergeProgram));
            GL20.glDeleteProgram(depthMergeProgram);
            depthMergeProgram = -1;
            return false;
        }

        depthMergeSamplerLoc = GL20.glGetUniformLocation(depthMergeProgram, "uDepth");
        depthMergeVao = GL30.glGenVertexArrays();
        depthMergeVbo = GL15.glGenBuffers();
        GL30.glBindVertexArray(depthMergeVao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, depthMergeVbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, new float[]{-1.0f, -1.0f, 3.0f, -1.0f, -1.0f, 3.0f}, GL15.GL_STATIC_DRAW);
        int posLoc = GL20.glGetAttribLocation(depthMergeProgram, "aPos");
        GL20.glEnableVertexAttribArray(posLoc);
        GL20.glVertexAttribPointer(posLoc, 2, GL11.GL_FLOAT, false, 2 * Float.BYTES, 0);
        GL30.glBindVertexArray(0);
        return true;
    }

    private static int depthMergeProgram = -1;
    private static int depthMergeVao = -1;
    private static int depthMergeVbo = -1;
    private static int depthMergeSamplerLoc = -1;

    /**
     * Create a merged depth texture that combines vanilla (base) depth and DH depth
     * for use during the final composite stage.
     */
    private static int mergeDepthForComposite(int dhDepthTex, int baseDepthTex, int w, int h, boolean reverseDepth) {
        if (dhDepthTex <= 0 || baseDepthTex <= 0) {
            return -1;
        }
        if (!ensureDepthCombineProgram() || !ensureCombinedDepthTarget(w, h)) {
            return -1;
        }

        int previousFbo = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        IntBuffer viewport = BufferUtils.createIntBuffer(4);
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean depthEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        int prevDepthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        boolean prevDepthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, combinedDepthFbo);
        GL11.glViewport(0, 0, w, h);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDepthFunc(GL11.GL_ALWAYS);
        GL11.glColorMask(false, false, false, false);

        GL20.glUseProgram(depthCombineProgram);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, dhDepthTex);
        if (depthCombineDhSamplerLoc >= 0) {
            GL20.glUniform1i(depthCombineDhSamplerLoc, 0);
        }
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, baseDepthTex);
        if (depthCombineBaseSamplerLoc >= 0) {
            GL20.glUniform1i(depthCombineBaseSamplerLoc, 1);
        }
        if (depthCombineReverseLoc >= 0) {
            GL20.glUniform1i(depthCombineReverseLoc, reverseDepth ? 1 : 0);
        }

        GL30.glBindVertexArray(depthCombineVao);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
        GL30.glBindVertexArray(0);

        GL20.glUseProgram(0);
        GL11.glColorMask(true, true, true, true);
        GL11.glDepthFunc(prevDepthFunc);
        GL11.glDepthMask(prevDepthMask);
        if (!depthEnabled) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }
        if (blendEnabled) {
            GL11.glEnable(GL11.GL_BLEND);
        } else {
            GL11.glDisable(GL11.GL_BLEND);
        }
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, previousFbo);
        GL11.glViewport(viewport.get(0), viewport.get(1), viewport.get(2), viewport.get(3));
        return combinedDepthTex;
    }

    private static boolean ensureDepthCombineProgram() {
        if (depthCombineProgram != -1 && depthCombineVao != -1 && depthCombineVbo != -1) {
            return true;
        }
        String vertexSrc = "#version 150\nin vec2 aPos;out vec2 vUv;void main(){vUv=aPos*0.5+0.5;gl_Position=vec4(aPos,0.0,1.0);}";
        String fragmentSrc = "#version 150\nin vec2 vUv;uniform sampler2D uDhDepth;uniform sampler2D uBaseDepth;uniform int uReverse;void main(){float dh=texture(uDhDepth,vUv).r;float base=texture(uBaseDepth,vUv).r;float merged=uReverse==1?max(dh,base):min(dh,base);gl_FragDepth=merged;}";
        int vert = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vert, vertexSrc);
        GL20.glCompileShader(vert);
        if (GL20.glGetShaderi(vert, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
            System.out.println("[OFSC WARN] Depth combine vertex shader compile failed: " + GL20.glGetShaderInfoLog(vert));
            GL20.glDeleteShader(vert);
            return false;
        }
        int frag = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(frag, fragmentSrc);
        GL20.glCompileShader(frag);
        if (GL20.glGetShaderi(frag, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
            System.out.println("[OFSC WARN] Depth combine fragment shader compile failed: " + GL20.glGetShaderInfoLog(frag));
            GL20.glDeleteShader(vert);
            GL20.glDeleteShader(frag);
            return false;
        }

        depthCombineProgram = GL20.glCreateProgram();
        GL20.glAttachShader(depthCombineProgram, vert);
        GL20.glAttachShader(depthCombineProgram, frag);
        GL20.glLinkProgram(depthCombineProgram);
        GL20.glDeleteShader(vert);
        GL20.glDeleteShader(frag);
        if (GL20.glGetProgrami(depthCombineProgram, GL20.GL_LINK_STATUS) != GL11.GL_TRUE) {
            System.out.println("[OFSC WARN] Depth combine program link failed: " + GL20.glGetProgramInfoLog(depthCombineProgram));
            GL20.glDeleteProgram(depthCombineProgram);
            depthCombineProgram = -1;
            return false;
        }

        depthCombineDhSamplerLoc = GL20.glGetUniformLocation(depthCombineProgram, "uDhDepth");
        depthCombineBaseSamplerLoc = GL20.glGetUniformLocation(depthCombineProgram, "uBaseDepth");
        depthCombineReverseLoc = GL20.glGetUniformLocation(depthCombineProgram, "uReverse");
        depthCombineVao = GL30.glGenVertexArrays();
        depthCombineVbo = GL15.glGenBuffers();
        GL30.glBindVertexArray(depthCombineVao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, depthCombineVbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, new float[]{-1.0f, -1.0f, 3.0f, -1.0f, -1.0f, 3.0f}, GL15.GL_STATIC_DRAW);
        int posLoc = GL20.glGetAttribLocation(depthCombineProgram, "aPos");
        GL20.glEnableVertexAttribArray(posLoc);
        GL20.glVertexAttribPointer(posLoc, 2, GL11.GL_FLOAT, false, 2 * Float.BYTES, 0);
        GL30.glBindVertexArray(0);
        return true;
    }

    private static boolean ensureCombinedDepthTarget(int w, int h) {
        if (combinedDepthTex == -1) {
            combinedDepthTex = GL11.glGenTextures();
        }
        if (combinedDepthFbo == -1) {
            combinedDepthFbo = GL30.glGenFramebuffers();
        }
        if (w != combinedDepthW || h != combinedDepthH) {
            combinedDepthW = w;
            combinedDepthH = h;
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, combinedDepthTex);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_DEPTH_COMPONENT32F, w, h, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (java.nio.ByteBuffer) null);
        }
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, combinedDepthFbo);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, combinedDepthTex, 0);
        GL11.glDrawBuffer(GL11.GL_NONE);
        GL11.glReadBuffer(GL11.GL_NONE);
        int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        return status == GL30.GL_FRAMEBUFFER_COMPLETE;
    }

    private static void ensureDhDepthCopyTexture(int w, int h) {
        if (depthMergeDhCopyTex == -1) {
            depthMergeDhCopyTex = GL11.glGenTextures();
        }
        if (w != depthMergeDhCopyW || h != depthMergeDhCopyH) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthMergeDhCopyTex);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_DEPTH_COMPONENT32F, w, h, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (java.nio.ByteBuffer) null);
            depthMergeDhCopyW = w;
            depthMergeDhCopyH = h;
        }
    }


    /**
     * Copy vanilla depth into the cloud target using copyTexSubImage to avoid
     * format mismatches between DH/vanilla depth attachments.
     */
    private static boolean copyVanillaDepthToCloudTarget(RenderTarget cloudTarget, RenderTarget source) {
        if (cloudTarget == null || source == null) {
            return false;
        }
        int cloudDepthTex = cloudTarget.getDepthTextureId();
        if (cloudDepthTex <= 0) {
            return false;
        }
        int sourceFbo = ((MixinRenderTargetAccessor) source).simpleclouds$getFrameBufferId();
        if (sourceFbo <= 0) {
            return false;
        }
        GlStateManager._getError(); // clear old errors
        int prevReadFbo = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        int prevTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        IntBuffer viewport = BufferUtils.createIntBuffer(4);
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);

        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, sourceFbo);
        int copyW = Math.min(cloudTarget.width, source.width);
        int copyH = Math.min(cloudTarget.height, source.height);
        GL11.glViewport(0, 0, copyW, copyH);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, cloudDepthTex);
        GL11.glReadBuffer(GL11.GL_NONE);
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, copyW, copyH);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, prevTexture);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, prevReadFbo);
        GL11.glViewport(viewport.get(0), viewport.get(1), viewport.get(2), viewport.get(3));
        return GlStateManager._getError() == GL11.GL_NO_ERROR;
    }

    private static void debug(String msg) {
    }
}
