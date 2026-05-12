//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.Gabou.oculus_for_simpleclouds.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.mixin.MixinRenderTargetAccessor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

@EventBusSubscriber(
        modid = "oculus_for_simpleclouds",
        bus = Bus.FORGE,
        value = {Dist.CLIENT}
)
public final class FinalCloudCompositeHandler {
    private static int compositeProgram = -1;
    private static int compositeVao = -1;
    private static int compositeVbo = -1;
    private static int locCloudColor = -1;
    private static int locCloudDepth = -1;
    private static int locSceneDepth = -1;
    private static int locDepthBias = -1;
    private static int locReverseDepth = -1;
    private static int externalSceneDepthTex = -1;
    private static int capturedSceneDepthTex = -1;
    private static int captureFbo = -1;
    private static int capturedW = -1;
    private static int capturedH = -1;
    private static boolean capturedThisFrame = false;
    private static boolean reverseDepthDetected = false;
    private static int combinedSceneDepthTex = -1;
    private static int combinedW = -1;
    private static int combinedH = -1;
    private static boolean combinedValidThisFrame = false;
    private static int mixedDepthTex = -1;
    private static int mixedDepthFbo = -1;
    private static int mixedDepthW = -1;
    private static int mixedDepthH = -1;
    private static int mixDepthProgram = -1;
    private static int mixDepthVao = -1;
    private static int mixDepthVbo = -1;
    private static int mixDepthMcDepthLoc = -1;
    private static int mixDepthDhDepthLoc = -1;
    private static int mixDepthReverseLoc = -1;
    private static int mixDepthBiasLoc = -1;
    private static long mixDepthShaderTimestamp = -1L;
    private static final boolean DEBUG_DEPTH_SNAPSHOTS = Boolean.getBoolean("ofsc.debug.depthSnapshots");
    private static final boolean DEBUG_DEPTH_EVENTS = Boolean.getBoolean("ofsc.debug.depthLog");
    private static final Map<String, Long> depthStageLogMs = new HashMap<>();
    private static int depthDebugSampleFbo = -1;
    private static String lastDepthSelectionSource = "";
    private static int lastDepthSelectionTex = -1;
    private static String lastCaptureMode = "";
    private static int lastCaptureTex = -1;
    private static int resolvedDhDepthTex = -1;
    private static int resolvedDhDepthFbo = -1;
    private static int resolvedDhDepthW = -1;
    private static int resolvedDhDepthH = -1;
    private static int resolvedDhDepthFormat = -1;
    private static int depthCopyProgram = -1;
    private static int depthCopyVao = -1;
    private static int depthCopyVbo = -1;
    private static int depthCopySamplerLoc = -1;

    private FinalCloudCompositeHandler() {
    }

    public interface DepthSource {
        int getDepthTexture();

        int getWidth();

        int getHeight();

        int getInternalFormat();

        boolean isValid();

        String getSourceName();
    }

    private abstract static class TextureDepthSource implements DepthSource {
        private final String sourceName;
        private final int depthTexture;
        private final int width;
        private final int height;
        private final int internalFormat;

        protected TextureDepthSource(String sourceName, int depthTexture, int width, int height, int internalFormat) {
            this.sourceName = sourceName;
            this.depthTexture = depthTexture;
            this.width = width;
            this.height = height;
            this.internalFormat = internalFormat;
        }

        @Override
        public int getDepthTexture() {
            return this.depthTexture;
        }

        @Override
        public int getWidth() {
            return this.width;
        }

        @Override
        public int getHeight() {
            return this.height;
        }

        @Override
        public int getInternalFormat() {
            return this.internalFormat;
        }

        @Override
        public boolean isValid() {
            return this.depthTexture > 0 && this.width > 0 && this.height > 0;
        }

        @Override
        public String getSourceName() {
            return this.sourceName;
        }
    }

    public static final class VanillaDepthSource extends TextureDepthSource {
        private VanillaDepthSource(String sourceName, int depthTexture, int width, int height, int internalFormat) {
            super(sourceName, depthTexture, width, height, internalFormat);
        }
    }

    public static final class DhDepthSource extends TextureDepthSource {
        private DhDepthSource(String sourceName, int depthTexture, int width, int height, int internalFormat) {
            super(sourceName, depthTexture, width, height, internalFormat);
        }
    }

    public static final class CombinedDepthSource extends TextureDepthSource {
        private CombinedDepthSource(String sourceName, int depthTexture, int width, int height, int internalFormat) {
            super(sourceName, depthTexture, width, height, internalFormat);
        }
    }

    private static final DepthSource INVALID_DEPTH_SOURCE = new TextureDepthSource("invalid", -1, -1, -1, -1) {
    };

    @SubscribeEvent
    public static void onRenderStage(RenderLevelStageEvent event) {
        if (CompatHelper.areShadersRunning()) {
            if (event.getStage() == Stage.AFTER_LEVEL) {
                compositeClouds();
                capturedThisFrame = false;
                combinedValidThisFrame = false;
            }

        }
    }

    public static DepthSource captureVanillaDepthSource(RenderTarget source) {
        combinedValidThisFrame = false;
        capturedThisFrame = false;
        externalSceneDepthTex = -1;
        if (source == null) {
            return INVALID_DEPTH_SOURCE;
        }

        int sourceFbo = ((MixinRenderTargetAccessor) source).simpleclouds$getFrameBufferId();
        if (sourceFbo <= 0) {
            return INVALID_DEPTH_SOURCE;
        }

        int prevFramebuffer = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, sourceFbo);
        try {
            int attachmentType = GL30.glGetFramebufferAttachmentParameteri(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE);
            int attachmentName = GL30.glGetFramebufferAttachmentParameteri(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME);
            if (attachmentName <= 0) {
                return INVALID_DEPTH_SOURCE;
            }

            if (attachmentType == GL11.GL_TEXTURE) {
                int width = Math.max(source.width, queryTextureLevelParameter(attachmentName, GL11.GL_TEXTURE_WIDTH));
                int height = Math.max(source.height, queryTextureLevelParameter(attachmentName, GL11.GL_TEXTURE_HEIGHT));
                int internalFormat = queryTextureLevelParameter(attachmentName, GL11.GL_TEXTURE_INTERNAL_FORMAT);
                externalSceneDepthTex = attachmentName;
                capturedW = width;
                capturedH = height;
                capturedThisFrame = true;
                logCapturePathChange("direct", attachmentName, width, height);
                logDepthSnapshot("capture_depth_texture", source.getDepthTextureId(), -1);
                return new VanillaDepthSource("external", attachmentName, width, height, internalFormat);
            }

            if (attachmentType == GL30.GL_RENDERBUFFER) {
                int width = queryRenderbufferParameter(attachmentName, GL30.GL_RENDERBUFFER_WIDTH, source.width);
                int height = queryRenderbufferParameter(attachmentName, GL30.GL_RENDERBUFFER_HEIGHT, source.height);
                int internalFormat = queryRenderbufferParameter(attachmentName, GL30.GL_RENDERBUFFER_INTERNAL_FORMAT, 0);
                if (!blitDepthAttachmentToResolveTarget(sourceFbo, width, height, internalFormat, true)) {
                    return INVALID_DEPTH_SOURCE;
                }
                capturedThisFrame = true;
                logCapturePathChange("blit", capturedSceneDepthTex, width, height);
                logDepthSnapshot("capture_depth_blit", source.getDepthTextureId(), -1);
                return new VanillaDepthSource("captured", capturedSceneDepthTex, width, height, internalFormat);
            }

            return INVALID_DEPTH_SOURCE;
        } finally {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, prevFramebuffer);
        }
    }

    public static DepthSource captureDhDepthSource(int fbo, int fallbackW, int fallbackH) {
        if (fbo <= 0) {
            return INVALID_DEPTH_SOURCE;
        }

        int previousFbo = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
        try {
            int attachmentType = GL30.glGetFramebufferAttachmentParameteri(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE);
            int attachmentName = GL30.glGetFramebufferAttachmentParameteri(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME);
            if (attachmentName <= 0) {
                return INVALID_DEPTH_SOURCE;
            }

            if (attachmentType == GL11.GL_TEXTURE) {
                int width = Math.max(fallbackW, queryTextureLevelParameter(attachmentName, GL11.GL_TEXTURE_WIDTH));
                int height = Math.max(fallbackH, queryTextureLevelParameter(attachmentName, GL11.GL_TEXTURE_HEIGHT));
                int internalFormat = queryTextureLevelParameter(attachmentName, GL11.GL_TEXTURE_INTERNAL_FORMAT);
                return new DhDepthSource("dh_texture", attachmentName, width, height, internalFormat);
            }

            if (attachmentType == GL30.GL_RENDERBUFFER) {
                int width = queryRenderbufferParameter(attachmentName, GL30.GL_RENDERBUFFER_WIDTH, fallbackW);
                int height = queryRenderbufferParameter(attachmentName, GL30.GL_RENDERBUFFER_HEIGHT, fallbackH);
                int internalFormat = queryRenderbufferParameter(attachmentName, GL30.GL_RENDERBUFFER_INTERNAL_FORMAT, 0);
                if (!blitDepthAttachmentToResolveTarget(fbo, width, height, internalFormat, false)) {
                    return INVALID_DEPTH_SOURCE;
                }
                return new DhDepthSource("dh_resolved", resolvedDhDepthTex, width, height, internalFormat);
            }

            return INVALID_DEPTH_SOURCE;
        } finally {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, previousFbo);
        }
    }

    public static DepthSource getCombinedSceneDepthSource() {
        if (!combinedValidThisFrame || combinedSceneDepthTex <= 0 || combinedW <= 0 || combinedH <= 0) {
            return INVALID_DEPTH_SOURCE;
        }
        int internalFormat = queryTextureLevelParameter(combinedSceneDepthTex, GL11.GL_TEXTURE_INTERNAL_FORMAT);
        return new CombinedDepthSource("combined", combinedSceneDepthTex, combinedW, combinedH, internalFormat);
    }

    public static boolean copyDepthToTarget(RenderTarget target, DepthSource depthSource) {
        if (target == null || depthSource == null || !depthSource.isValid()) {
            return false;
        }
        int targetDepthTex = target.getDepthTextureId();
        if (targetDepthTex <= 0 || !ensureDepthCopyProgram()) {
            return false;
        }
        int copyW = Math.min(target.width, depthSource.getWidth());
        int copyH = Math.min(target.height, depthSource.getHeight());
        if (copyW <= 0 || copyH <= 0) {
            return false;
        }
        return copyDepthTextureToTarget(target, depthSource.getDepthTexture(), copyW, copyH);
    }

    public static void captureDepth(RenderTarget source) {
        captureVanillaDepthSource(source);
    }

    /**
     * Called by the DH shader-aware pipeline to provide a pre-merged depth texture
     * that already includes both vanilla and DH depth. When set, this texture is
     * preferred by the composite pass for depth testing.
     */
    public static void setCombinedSceneDepthTex(int tex, int w, int h) {
        combinedSceneDepthTex = tex;
        combinedW = w;
        combinedH = h;
        combinedValidThisFrame = tex > 0 && w > 0 && h > 0;
    }

    public static int getCapturedSceneDepthTex() {
        return capturedSceneDepthTex;
    }

    public static int getExternalSceneDepthTex() {
        return externalSceneDepthTex;
    }

    public static int getCapturedW() {
        return capturedW;
    }

    public static int getCapturedH() {
        return capturedH;
    }

    private static void compositeClouds() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            int mainDepthTex = mc.getMainRenderTarget() != null ? mc.getMainRenderTarget().getDepthTextureId() : -1;
            int selectedSceneDepthTex = selectPrimarySceneDepthTex(mainDepthTex, -1);
            if (capturedThisFrame && selectedSceneDepthTex > 0) {
                SimpleCloudsRenderer.getOptionalInstance().ifPresent((renderer) -> {
                    if (renderer.getCloudTarget() != null) {
                        int windowW = mc.getWindow().getWidth();
                        int windowH = mc.getWindow().getHeight();
                        int cloudColorTex = renderer.getCloudTarget().getColorTextureId();
                        int cloudDepthTex = renderer.getCloudTarget().getDepthTextureId();
                        if (cloudColorTex > 0 && cloudDepthTex > 0) {
                            if (ensureCompositeProgram()) {
                                int prevProgram = GL11.glGetInteger(35725);
                                int prevFbo = GL11.glGetInteger(36006);
                                int prevVAO = GL11.glGetInteger(34229);
                                boolean depthEnabled = GL11.glIsEnabled(2929);
                                boolean blendEnabled = GL11.glIsEnabled(3042);
                                boolean depthMask = GL11.glGetBoolean(2930);
                                int prevDepthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
                                int prevBlendSrcRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
                                int prevBlendDstRgb = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
                                int prevBlendSrcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
                                int prevBlendDstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
                                int prevActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
                                int mainFbo = ((MixinRenderTargetAccessor)mc.getMainRenderTarget()).simpleclouds$getFrameBufferId();
                                int originalDepthTex = mc.getMainRenderTarget().getDepthTextureId();
                                GL30.glBindFramebuffer(36160, mainFbo);
                                int prevDepthAttachmentType = GL30.glGetFramebufferAttachmentParameteri(36160, 36096, 36048);
                                int prevDepthAttachmentName = GL30.glGetFramebufferAttachmentParameteri(36160, 36096, 36049);
                                GL11.glViewport(0, 0, windowW, windowH);
                                int depthTestTex = selectPrimarySceneDepthTex(originalDepthTex, cloudDepthTex);
                                boolean swappedDepthAttachment = false;
                                if (depthTestTex > 0 && (prevDepthAttachmentType != 3553 || prevDepthAttachmentName != depthTestTex)) {
                                    GL30.glFramebufferTexture2D(36160, 36096, 3553, depthTestTex, 0);
                                    swappedDepthAttachment = true;
                                }
                                reverseDepthDetected = detectReverseDepth();
                                GL11.glEnable(2929);
                                GL11.glDepthFunc(reverseDepthDetected ? 518 : 515);

                                RenderSystem.depthMask(false);
                                GL11.glEnable(3042);
                                GL14.glBlendFuncSeparate(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
                                GL20.glUseProgram(compositeProgram);
                                GL13.glActiveTexture(33984);
                                GL11.glBindTexture(3553, cloudColorTex);
                                GL20.glUniform1i(locCloudColor, 0);
                                GL13.glActiveTexture(33985);
                                GL11.glBindTexture(3553, cloudDepthTex);
                                GL20.glUniform1i(locCloudDepth, 1);
                                int sceneDepthTex = selectPrimarySceneDepthTex(originalDepthTex, cloudDepthTex);
                                String sceneDepthSource = selectPrimarySceneDepthSource(originalDepthTex, cloudDepthTex);
                                logSceneDepthSelectionChange(sceneDepthSource, sceneDepthTex, originalDepthTex, cloudDepthTex);
                                logDepthSnapshot("final_composite", originalDepthTex, cloudDepthTex);
                                GL13.glActiveTexture(33986);
                                GL11.glBindTexture(3553, sceneDepthTex);
                                GL20.glUniform1i(locSceneDepth, 2);
                                GL20.glUniform1f(locDepthBias, 0.001F);
                                GL20.glUniform1i(locReverseDepth, reverseDepthDetected ? 1 : 0);
                                //maybeLogDepthSamples(cloudDepthTex, capturedSceneDepthTex, windowW, windowH);
                                GL30.glBindVertexArray(compositeVao);
                                GL11.glDrawArrays(4, 0, 3);
                                GL30.glBindVertexArray(prevVAO);
                                if (!blendEnabled) {
                                    GL11.glDisable(3042);
                                }

                                RenderSystem.depthMask(true);
                                GL11.glDepthMask(depthMask);
                                GL11.glDepthFunc(prevDepthFunc);
                                GL14.glBlendFuncSeparate(prevBlendSrcRgb, prevBlendDstRgb, prevBlendSrcAlpha, prevBlendDstAlpha);
                                if (swappedDepthAttachment) {
                                    if (prevDepthAttachmentType == 3553) {
                                        GL30.glFramebufferTexture2D(36160, 36096, 3553, prevDepthAttachmentName, 0);
                                    } else if (prevDepthAttachmentType == 36161) {
                                        GL30.glFramebufferRenderbuffer(36160, 36096, 36161, prevDepthAttachmentName);
                                    } else if (originalDepthTex > 0) {
                                        GL30.glFramebufferTexture2D(36160, 36096, 3553, originalDepthTex, 0);
                                    }
                                }

                                if (depthEnabled) {
                                    GL11.glEnable(2929);
                                } else {
                                    GL11.glDisable(2929);
                                }

                                GL20.glUseProgram(prevProgram);
                                GL30.glBindFramebuffer(36160, prevFbo);
                                GL13.glActiveTexture(33984);
                                GL11.glBindTexture(3553, 0);
                                GL13.glActiveTexture(prevActiveTexture);
                            }
                        }
                    }
                });
            }
        }
    }

    private static int selectPrimarySceneDepthTex(int originalDepthTex, int cloudDepthTex) {
        if (combinedValidThisFrame && combinedSceneDepthTex > 0) {
            return combinedSceneDepthTex;
        }
        if (externalSceneDepthTex > 0) {
            return externalSceneDepthTex;
        }
        if (originalDepthTex > 0) {
            return originalDepthTex;
        }
        if (capturedSceneDepthTex > 0) {
            return capturedSceneDepthTex;
        }
        return cloudDepthTex;
    }

    private static String selectPrimarySceneDepthSource(int originalDepthTex, int cloudDepthTex) {
        if (combinedValidThisFrame && combinedSceneDepthTex > 0) {
            return "combined";
        }
        if (externalSceneDepthTex > 0) {
            return "external";
        }
        if (originalDepthTex > 0) {
            return "original";
        }
        if (capturedSceneDepthTex > 0) {
            return "captured";
        }
        return "cloud";
    }

    private static int queryTextureLevelParameter(int textureId, int parameter) {
        if (textureId <= 0) {
            return 0;
        }
        int previousTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        int value = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, parameter);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTexture);
        return value;
    }

    private static int queryRenderbufferParameter(int renderbufferId, int parameter, int fallback) {
        if (renderbufferId <= 0) {
            return fallback;
        }
        int previousRenderbuffer = GL11.glGetInteger(GL30.GL_RENDERBUFFER_BINDING);
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderbufferId);
        int value = GL30.glGetRenderbufferParameteri(GL30.GL_RENDERBUFFER, parameter);
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, previousRenderbuffer);
        return value > 0 ? value : fallback;
    }

    private static boolean blitDepthAttachmentToResolveTarget(int sourceFbo, int width, int height, int internalFormat, boolean vanillaTarget) {
        if (sourceFbo <= 0 || width <= 0 || height <= 0 || internalFormat <= 0) {
            return false;
        }
        if (vanillaTarget) {
            if (!ensureSceneDepthTarget(width, height, internalFormat)) {
                return false;
            }
        } else {
            if (!ensureDhResolvedDepthTarget(width, height, internalFormat)) {
                return false;
            }
        }

        int destinationFbo = vanillaTarget ? captureFbo : resolvedDhDepthFbo;
        int prevReadFbo = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        int prevDrawFbo = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, sourceFbo);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, destinationFbo);
        GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, prevReadFbo);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, prevDrawFbo);

        if (vanillaTarget) {
            externalSceneDepthTex = -1;
            capturedW = width;
            capturedH = height;
        }
        return GlStateManager._getError() == GL11.GL_NO_ERROR;
    }

    private static boolean ensureDhResolvedDepthTarget(int width, int height, int internalFormat) {
        if (width <= 0 || height <= 0 || internalFormat <= 0) {
            return false;
        }
        if (resolvedDhDepthTex == -1) {
            resolvedDhDepthTex = GL11.glGenTextures();
        }
        if (resolvedDhDepthFbo == -1) {
            resolvedDhDepthFbo = GL30.glGenFramebuffers();
        }

        int previousFramebuffer = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        int previousTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, resolvedDhDepthTex);
        if (width != resolvedDhDepthW || height != resolvedDhDepthH || internalFormat != resolvedDhDepthFormat) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, internalFormat, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
            resolvedDhDepthW = width;
            resolvedDhDepthH = height;
            resolvedDhDepthFormat = internalFormat;
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, resolvedDhDepthFbo);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, resolvedDhDepthTex, 0);
        GL11.glDrawBuffer(GL11.GL_NONE);
        GL11.glReadBuffer(GL11.GL_NONE);
        int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, previousFramebuffer);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTexture);
        return status == GL30.GL_FRAMEBUFFER_COMPLETE;
    }

    private static boolean ensureCompositeProgram() {
        if (compositeProgram != -1 && compositeVao != -1 && compositeVbo != -1) {
            return true;
        } else {
            String vertexSrc = "#version 150\nin vec2 aPos;out vec2 vUv;void main(){vUv=aPos*0.5+0.5;gl_Position=vec4(aPos,0.0,1.0);}";
            String fragmentSrc = "#version 150\nin vec2 vUv;uniform sampler2D uCloudColor;uniform sampler2D uCloudDepth;uniform sampler2D uSceneDepth;uniform float uBias;uniform int uReverse;out vec4 fragColor;void main(){vec4 cloud=texture(uCloudColor,vUv);if(cloud.a<=0.04){discard;}float cloudD=texture(uCloudDepth,vUv).r;float sceneD=texture(uSceneDepth,vUv).r;if(uReverse==0&&sceneD<=0.0){sceneD=1.0;}if(cloudD<=0.0||cloudD>=1.0){discard;}bool sceneIsSky=uReverse==1?sceneD<=0.0000001:sceneD>=0.9999999;float fragD=uReverse==1?cloudD+uBias:cloudD-uBias;if(!sceneIsSky){if(uReverse==1){if(fragD<sceneD){discard;}}else{if(fragD>sceneD){discard;}}}gl_FragDepth=fragD;fragColor=vec4(cloud.rgb*cloud.a,cloud.a);}";
            int vert = GL20.glCreateShader(35633);
            GL20.glShaderSource(vert, vertexSrc);
            GL20.glCompileShader(vert);
            if (GL20.glGetShaderi(vert, 35713) != 1) {
                System.out.println("[OFSC WARN] Composite vertex shader compile failed: " + GL20.glGetShaderInfoLog(vert));
                GL20.glDeleteShader(vert);
                return false;
            } else {
                int frag = GL20.glCreateShader(35632);
                GL20.glShaderSource(frag, fragmentSrc);
                GL20.glCompileShader(frag);
                if (GL20.glGetShaderi(frag, 35713) != 1) {
                    System.out.println("[OFSC WARN] Composite fragment shader compile failed: " + GL20.glGetShaderInfoLog(frag));
                    GL20.glDeleteShader(vert);
                    GL20.glDeleteShader(frag);
                    return false;
                } else {
                    compositeProgram = GL20.glCreateProgram();
                    GL20.glAttachShader(compositeProgram, vert);
                    GL20.glAttachShader(compositeProgram, frag);
                    GL20.glLinkProgram(compositeProgram);
                    GL20.glDeleteShader(vert);
                    GL20.glDeleteShader(frag);
                    if (GL20.glGetProgrami(compositeProgram, 35714) != 1) {
                        System.out.println("[OFSC WARN] Composite program link failed: " + GL20.glGetProgramInfoLog(compositeProgram));
                        GL20.glDeleteProgram(compositeProgram);
                        compositeProgram = -1;
                        return false;
                    } else {
                        locCloudColor = GL20.glGetUniformLocation(compositeProgram, "uCloudColor");
                        locCloudDepth = GL20.glGetUniformLocation(compositeProgram, "uCloudDepth");
                        locSceneDepth = GL20.glGetUniformLocation(compositeProgram, "uSceneDepth");
                        locDepthBias = GL20.glGetUniformLocation(compositeProgram, "uBias");
                        locReverseDepth = GL20.glGetUniformLocation(compositeProgram, "uReverse");
                        compositeVao = GL30.glGenVertexArrays();
                        compositeVbo = GL15.glGenBuffers();
                        GL30.glBindVertexArray(compositeVao);
                        GL15.glBindBuffer(34962, compositeVbo);
                        GL15.glBufferData(34962, new float[]{-1.0F, -1.0F, 3.0F, -1.0F, -1.0F, 3.0F}, 35044);
                        int posLoc = GL20.glGetAttribLocation(compositeProgram, "aPos");
                        GL20.glEnableVertexAttribArray(posLoc);
                        GL20.glVertexAttribPointer(posLoc, 2, 5126, false, 8, 0L);
                        GL30.glBindVertexArray(0);
                        return true;
                    }
                }
            }
        }
    }

    private static boolean ensureDepthCopyProgram() {
        if (depthCopyProgram != -1 && depthCopyVao != -1 && depthCopyVbo != -1) {
            return true;
        }
        String vertexSrc = "#version 150\nin vec2 aPos;out vec2 vUv;void main(){vUv=aPos*0.5+0.5;gl_Position=vec4(aPos,0.0,1.0);}";
        String fragmentSrc = "#version 150\nin vec2 vUv;uniform sampler2D uDepth;void main(){gl_FragDepth=texture(uDepth,vUv).r;}";
        int vert = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vert, vertexSrc);
        GL20.glCompileShader(vert);
        if (GL20.glGetShaderi(vert, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
            System.out.println("[OFSC WARN] Depth copy vertex shader compile failed: " + GL20.glGetShaderInfoLog(vert));
            GL20.glDeleteShader(vert);
            return false;
        }
        int frag = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(frag, fragmentSrc);
        GL20.glCompileShader(frag);
        if (GL20.glGetShaderi(frag, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
            System.out.println("[OFSC WARN] Depth copy fragment shader compile failed: " + GL20.glGetShaderInfoLog(frag));
            GL20.glDeleteShader(vert);
            GL20.glDeleteShader(frag);
            return false;
        }
        depthCopyProgram = GL20.glCreateProgram();
        GL20.glAttachShader(depthCopyProgram, vert);
        GL20.glAttachShader(depthCopyProgram, frag);
        GL20.glLinkProgram(depthCopyProgram);
        GL20.glDeleteShader(vert);
        GL20.glDeleteShader(frag);
        if (GL20.glGetProgrami(depthCopyProgram, GL20.GL_LINK_STATUS) != GL11.GL_TRUE) {
            System.out.println("[OFSC WARN] Depth copy program link failed: " + GL20.glGetProgramInfoLog(depthCopyProgram));
            GL20.glDeleteProgram(depthCopyProgram);
            depthCopyProgram = -1;
            return false;
        }
        depthCopySamplerLoc = GL20.glGetUniformLocation(depthCopyProgram, "uDepth");
        depthCopyVao = GL30.glGenVertexArrays();
        depthCopyVbo = GL15.glGenBuffers();
        GL30.glBindVertexArray(depthCopyVao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, depthCopyVbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, new float[]{-1.0F, -1.0F, 3.0F, -1.0F, -1.0F, 3.0F}, GL15.GL_STATIC_DRAW);
        int posLoc = GL20.glGetAttribLocation(depthCopyProgram, "aPos");
        GL20.glEnableVertexAttribArray(posLoc);
        GL20.glVertexAttribPointer(posLoc, 2, GL11.GL_FLOAT, false, 2 * Float.BYTES, 0);
        GL30.glBindVertexArray(0);
        return true;
    }

    private static boolean copyDepthTextureToTarget(RenderTarget target, int depthTexture, int width, int height) {
        int previousProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        int previousFbo = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        int previousVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        int previousActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        boolean depthEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        int previousDepthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        ByteBuffer colorMask = BufferUtils.createByteBuffer(4);
        GL11.glGetBooleanv(GL11.GL_COLOR_WRITEMASK, colorMask);
        IntBuffer viewport = BufferUtils.createIntBuffer(4);
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, ((MixinRenderTargetAccessor) target).simpleclouds$getFrameBufferId());
        GL11.glViewport(0, 0, width, height);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_ALWAYS);
        GL11.glDepthMask(true);
        GL11.glColorMask(false, false, false, false);

        GL20.glUseProgram(depthCopyProgram);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
        if (depthCopySamplerLoc >= 0) {
            GL20.glUniform1i(depthCopySamplerLoc, 0);
        }
        GL30.glBindVertexArray(depthCopyVao);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
        GL30.glBindVertexArray(previousVao);

        GL20.glUseProgram(previousProgram);
        GL11.glDepthFunc(previousDepthFunc);
        GL11.glDepthMask(depthMask);
        if (!depthEnabled) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }
        if (blendEnabled) {
            GL11.glEnable(GL11.GL_BLEND);
        } else {
            GL11.glDisable(GL11.GL_BLEND);
        }
        GL11.glColorMask(colorMask.get(0) != 0, colorMask.get(1) != 0, colorMask.get(2) != 0, colorMask.get(3) != 0);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, previousFbo);
        GL11.glViewport(viewport.get(0), viewport.get(1), viewport.get(2), viewport.get(3));
        GL13.glActiveTexture(previousActiveTexture);
        return GlStateManager._getError() == GL11.GL_NO_ERROR;
    }

    private static boolean ensureMixedDepthTarget(int w, int h) {
        if (w <= 0 || h <= 0) {
            return false;
        }
        boolean resized = w != mixedDepthW || h != mixedDepthH;
        if (mixedDepthTex == -1 || resized) {
            if (mixedDepthTex != -1) {
                GL11.glDeleteTextures(mixedDepthTex);
            }
            mixedDepthTex = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, mixedDepthTex);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 33071);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 33071);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_DEPTH_COMPONENT32F, w, h, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer)null);
            mixedDepthW = w;
            mixedDepthH = h;
        }

        if (mixedDepthFbo == -1 || resized) {
            if (mixedDepthFbo != -1) {
                GL30.glDeleteFramebuffers(mixedDepthFbo);
            }
            mixedDepthFbo = GL30.glGenFramebuffers();
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, mixedDepthFbo);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, mixedDepthTex, 0);
        GL11.glDrawBuffer(GL11.GL_NONE);
        GL11.glReadBuffer(GL11.GL_NONE);
        int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("[OFSC WARN] Mixed depth FBO incomplete (" + status + ").");
            return false;
        }

        return true;
    }

    private static boolean ensureMixDepthProgram() {
        long timestamp = getMixDepthShaderTimestamp();
        boolean needsReload = mixDepthProgram == -1 || timestamp != mixDepthShaderTimestamp;
        if (!needsReload) {
            return true;
        }
        destroyMixDepthProgram();
        destroyMixedDepthTarget();
        if (timestamp == -1L) {
            return false;
        }

        String vertexSrc = readMixDepthShader("mix_depth.vsh");
        String fragmentSrc = readMixDepthShader("mix_depth.fsh");
        if (vertexSrc == null || fragmentSrc == null) {
            return false;
        }

        int vert = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vert, vertexSrc);
        GL20.glCompileShader(vert);
        if (GL20.glGetShaderi(vert, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
            System.out.println("[OFSC WARN] Mix depth vertex shader compile failed: " + GL20.glGetShaderInfoLog(vert));
            GL20.glDeleteShader(vert);
            return false;
        }

        int frag = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(frag, fragmentSrc);
        GL20.glCompileShader(frag);
        if (GL20.glGetShaderi(frag, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
            System.out.println("[OFSC WARN] Mix depth fragment shader compile failed: " + GL20.glGetShaderInfoLog(frag));
            GL20.glDeleteShader(vert);
            GL20.glDeleteShader(frag);
            return false;
        }

        mixDepthProgram = GL20.glCreateProgram();
        GL20.glAttachShader(mixDepthProgram, vert);
        GL20.glAttachShader(mixDepthProgram, frag);
        GL20.glLinkProgram(mixDepthProgram);
        GL20.glDeleteShader(vert);
        GL20.glDeleteShader(frag);
        if (GL20.glGetProgrami(mixDepthProgram, GL20.GL_LINK_STATUS) != GL11.GL_TRUE) {
            System.out.println("[OFSC WARN] Mix depth program link failed: " + GL20.glGetProgramInfoLog(mixDepthProgram));
            GL20.glDeleteProgram(mixDepthProgram);
            mixDepthProgram = -1;
            return false;
        }

        mixDepthMcDepthLoc = GL20.glGetUniformLocation(mixDepthProgram, "mcDepth");
        mixDepthDhDepthLoc = GL20.glGetUniformLocation(mixDepthProgram, "dhDepth");
        mixDepthReverseLoc = GL20.glGetUniformLocation(mixDepthProgram, "reverseDepth");
        mixDepthBiasLoc = GL20.glGetUniformLocation(mixDepthProgram, "bias");
        mixDepthVao = GL30.glGenVertexArrays();
        mixDepthVbo = GL15.glGenBuffers();
        GL30.glBindVertexArray(mixDepthVao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mixDepthVbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, new float[]{-1.0F, -1.0F, 3.0F, -1.0F, -1.0F, 3.0F}, GL15.GL_STATIC_DRAW);
        int posLoc = GL20.glGetAttribLocation(mixDepthProgram, "aPos");
        GL20.glEnableVertexAttribArray(posLoc);
        GL20.glVertexAttribPointer(posLoc, 2, GL11.GL_FLOAT, false, 2 * Float.BYTES, 0);
        GL30.glBindVertexArray(0);
        mixDepthShaderTimestamp = timestamp;
        return true;
    }

    private static boolean buildMixedDepth(int windowW, int windowH, int mcDepthTex, int dhDepthTex) {
        if (mcDepthTex <= 0) {
            return false;
        }
        if (dhDepthTex <= 0) {
            dhDepthTex = mcDepthTex;
        }
        if (!ensureMixDepthProgram()) {
            return false;
        }
        if (!ensureMixedDepthTarget(windowW, windowH)) {
            return false;
        }

        int prevProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        int prevFbo = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        int prevVAO = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        boolean depthEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        int prevDepthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        java.nio.IntBuffer viewport = BufferUtils.createIntBuffer(4);
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, mixedDepthFbo);
        GL11.glViewport(0, 0, windowW, windowH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_ALWAYS);
        GL11.glDepthMask(true);

        GL20.glUseProgram(mixDepthProgram);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, mcDepthTex);
        if (mixDepthMcDepthLoc >= 0) {
            GL20.glUniform1i(mixDepthMcDepthLoc, 0);
        }
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, dhDepthTex);
        if (mixDepthDhDepthLoc >= 0) {
            GL20.glUniform1i(mixDepthDhDepthLoc, 1);
        }
        if (mixDepthReverseLoc >= 0) {
            GL20.glUniform1i(mixDepthReverseLoc, reverseDepthDetected ? 1 : 0);
        }
        if (mixDepthBiasLoc >= 0) {
            GL20.glUniform1f(mixDepthBiasLoc, 0.0F);
        }

        GL30.glBindVertexArray(mixDepthVao);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
        GL30.glBindVertexArray(prevVAO);

        GL20.glUseProgram(prevProgram);
        GL11.glDepthFunc(prevDepthFunc);
        GL11.glDepthMask(depthMask);
        if (!depthEnabled) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }
        if (blendEnabled) {
            GL11.glEnable(GL11.GL_BLEND);
        } else {
            GL11.glDisable(GL11.GL_BLEND);
        }
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, prevFbo);
        GL11.glViewport(viewport.get(0), viewport.get(1), viewport.get(2), viewport.get(3));
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        return true;
    }

    private static long getMixDepthShaderTimestamp() {
        Path vertexPath = resolveMixDepthShaderPath("mix_depth.vsh");
        Path fragmentPath = resolveMixDepthShaderPath("mix_depth.fsh");
        long vertTime = getFileTimestamp(vertexPath);
        long fragTime = getFileTimestamp(fragmentPath);
        if (vertTime == -1L || fragTime == -1L) {
            return -1L;
        }
        return Math.max(vertTime, fragTime);
    }

    private static long getFileTimestamp(Path path) {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (IOException ex) {
            return -1L;
        }
    }

    private static String readMixDepthShader(String name) {
        Path path = resolveMixDepthShaderPath(name);
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            System.out.println("[OFSC WARN] Failed to load mix depth shader: " + path);
            return null;
        }
    }

    private static Path resolveMixDepthShaderPath(String name) {
        Path direct = Paths.get("run", "shaderpacks", "Shaders-for-simpleClouds-main", "shaders", name);
        if (Files.exists(direct)) {
            return direct;
        }
        Path fallback = Paths.get("shaderpacks", "Shaders-for-simpleClouds-main", "shaders", name);
        if (Files.exists(fallback)) {
            return fallback;
        }
        return direct;
    }

    private static void destroyMixDepthProgram() {
        if (mixDepthProgram != -1) {
            GL20.glDeleteProgram(mixDepthProgram);
            mixDepthProgram = -1;
        }
        if (mixDepthVbo != -1) {
            GL15.glDeleteBuffers(mixDepthVbo);
            mixDepthVbo = -1;
        }
        if (mixDepthVao != -1) {
            GL30.glDeleteVertexArrays(mixDepthVao);
            mixDepthVao = -1;
        }
        mixDepthMcDepthLoc = -1;
        mixDepthDhDepthLoc = -1;
        mixDepthReverseLoc = -1;
        mixDepthBiasLoc = -1;
        mixDepthShaderTimestamp = -1L;
    }

    private static void destroyMixedDepthTarget() {
        if (mixedDepthTex != -1) {
            GL11.glDeleteTextures(mixedDepthTex);
            mixedDepthTex = -1;
        }
        if (mixedDepthFbo != -1) {
            GL30.glDeleteFramebuffers(mixedDepthFbo);
            mixedDepthFbo = -1;
        }
        mixedDepthW = -1;
        mixedDepthH = -1;
    }

    public static void logDepthSnapshot(String stage, int originalDepthTex, int cloudDepthTex) {
        if (!DEBUG_DEPTH_SNAPSHOTS) {
            return;
        }
        long now = System.currentTimeMillis();
        Long last = depthStageLogMs.get(stage);
        if (last != null && now - last < 1000L) {
            return;
        }
        depthStageLogMs.put(stage, now);
        System.out.println("[OFSC DEBUG] " + stage + " depth samples: "
                + describeDepthTexture("external", externalSceneDepthTex)
                + " | " + describeDepthTexture("captured", capturedSceneDepthTex)
                + " | " + describeDepthTexture("combined", combinedSceneDepthTex)
                + " | " + describeDepthTexture("original", originalDepthTex)
                + " | " + describeDepthTexture("cloud", cloudDepthTex));
    }

    private static boolean ensureSceneDepthTarget(int w, int h, int internalFormat) {
        if (w <= 0 || h <= 0) {
            return false;
        }
        int resolvedInternalFormat = internalFormat > 0 ? internalFormat : GL30.GL_DEPTH_COMPONENT32F;
        if (capturedSceneDepthTex == -1) {
            capturedSceneDepthTex = GL11.glGenTextures();
            GL11.glBindTexture(3553, capturedSceneDepthTex);
            GL11.glTexParameteri(3553, 10241, 9728);
            GL11.glTexParameteri(3553, 10240, 9728);
            GL11.glTexParameteri(3553, 10242, 33071);
            GL11.glTexParameteri(3553, 10243, 33071);
        } else {
            GL11.glBindTexture(3553, capturedSceneDepthTex);
        }

        int currentFormat = GL11.glGetTexLevelParameteri(3553, 0, GL11.GL_TEXTURE_INTERNAL_FORMAT);
        if (w != capturedW || h != capturedH || currentFormat != resolvedInternalFormat) {
            GL11.glTexImage2D(3553, 0, resolvedInternalFormat, w, h, 0, 6402, 5126, (ByteBuffer)null);
            capturedW = w;
            capturedH = h;
        }

        if (captureFbo == -1) {
            captureFbo = GL30.glGenFramebuffers();
        }

        int prevFramebuffer = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, captureFbo);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, capturedSceneDepthTex, 0);
        GL11.glDrawBuffer(GL11.GL_NONE);
        GL11.glReadBuffer(GL11.GL_NONE);
        int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, prevFramebuffer);
        if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("[OFSC WARN] Scene depth capture FBO incomplete (" + status + ") tex="
                    + capturedSceneDepthTex + " fbo=" + captureFbo + " size=" + w + "x" + h);
            return false;
        }
        return true;
    }

    private static void logSceneDepthSelectionChange(String source, int tex, int originalDepthTex, int cloudDepthTex) {
        if (!DEBUG_DEPTH_EVENTS) {
            return;
        }
        if (tex == lastDepthSelectionTex && source.equals(lastDepthSelectionSource)) {
            return;
        }
        lastDepthSelectionSource = source;
        lastDepthSelectionTex = tex;
        System.out.println("[OFSC DEBUG] Scene depth select: source=" + source
                + " tex=" + tex
                + " external=" + externalSceneDepthTex
                + " original=" + originalDepthTex
                + " combined=" + (combinedValidThisFrame ? combinedSceneDepthTex : -1)
                + " captured=" + capturedSceneDepthTex
                + " cloud=" + cloudDepthTex
                + " capturedThisFrame=" + capturedThisFrame);
    }

    private static void logCapturePathChange(String mode, int tex, int w, int h) {
        if (!DEBUG_DEPTH_EVENTS) {
            return;
        }
        if (tex == lastCaptureTex && mode.equals(lastCaptureMode)) {
            return;
        }
        lastCaptureMode = mode;
        lastCaptureTex = tex;
        System.out.println("[OFSC DEBUG] Depth capture: mode=" + mode + " tex=" + tex + " size=" + w + "x" + h);
    }

    private static float sampleDepthAt(int tex, float u, float v) {
        if (tex <= 0) {
            return -1.0F;
        } else {
            GL11.glBindTexture(3553, tex);
            int w = GL11.glGetTexLevelParameteri(3553, 0, 4096);
            int h = GL11.glGetTexLevelParameteri(3553, 0, 4097);
            GL11.glBindTexture(3553, 0);
            if (w <= 0 || h <= 0) {
                return -1.0F;
            }
            if (!ensureDepthDebugSampleFbo()) {
                return -1.0F;
            }

            int x = Math.max(0, Math.min(w - 1, Math.round(u * (float)(w - 1))));
            int y = Math.max(0, Math.min(h - 1, Math.round(v * (float)(h - 1))));
            int prevReadFbo = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, depthDebugSampleFbo);
            GL30.glFramebufferTexture2D(GL30.GL_READ_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, tex, 0);
            if (GL30.glCheckFramebufferStatus(GL30.GL_READ_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
                GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, prevReadFbo);
                return -1.0F;
            }
            FloatBuffer buf = BufferUtils.createFloatBuffer(1);
            GL11.glReadPixels(x, y, 1, 1, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, buf);
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, prevReadFbo);
            return buf.get(0);
        }
    }

    private static boolean ensureDepthDebugSampleFbo() {
        if (depthDebugSampleFbo == -1) {
            depthDebugSampleFbo = GL30.glGenFramebuffers();
            int prevFramebuffer = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, depthDebugSampleFbo);
            GL11.glDrawBuffer(GL11.GL_NONE);
            GL11.glReadBuffer(GL11.GL_NONE);
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, prevFramebuffer);
        }
        return depthDebugSampleFbo > 0;
    }

    private static String describeDepthTexture(String label, int tex) {
        if (tex <= 0) {
            return label + "=none";
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
        int w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        return label + "#" + tex + "(" + w + "x" + h + ")"
                + "[c=" + sampleDepthAt(tex, 0.50F, 0.50F)
                + ",l=" + sampleDepthAt(tex, 0.25F, 0.50F)
                + ",r=" + sampleDepthAt(tex, 0.75F, 0.50F)
                + ",t=" + sampleDepthAt(tex, 0.50F, 0.75F)
                + ",b=" + sampleDepthAt(tex, 0.50F, 0.25F) + "]";
    }

    private static boolean detectReverseDepth() {
        int depthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        float depthClear = GL11.glGetFloat(GL11.GL_DEPTH_CLEAR_VALUE);
        return depthFunc == GL11.GL_GEQUAL || depthFunc == GL11.GL_GREATER || depthClear < 0.5f;
    }
}
