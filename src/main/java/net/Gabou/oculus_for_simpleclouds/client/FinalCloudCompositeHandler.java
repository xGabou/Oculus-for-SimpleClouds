//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.Gabou.oculus_for_simpleclouds.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.mixin.MixinRenderTargetAccessor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
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
    private static final Map<String, Long> depthStageLogMs = new HashMap<>();
    private static int depthDebugSampleFbo = -1;
    private static String lastDepthSelectionSource = "";
    private static int lastDepthSelectionTex = -1;
    private static String lastCaptureMode = "";
    private static int lastCaptureTex = -1;

    private FinalCloudCompositeHandler() {
    }

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

    public static void captureDepth(RenderTarget source) {
        combinedValidThisFrame = false;
        capturedThisFrame = false;
        externalSceneDepthTex = -1;
        if (source != null) {
            int w = source.width;
            int h = source.height;
            if (!ensureSceneDepthTarget(w, h)) {
                return;
            }
            int prevReadFbo = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
            int prevDrawFbo = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
            int sourceFbo = ((MixinRenderTargetAccessor)source).simpleclouds$getFrameBufferId();
            if (sourceFbo > 0 && capturedSceneDepthTex > 0 && captureFbo > 0) {
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, sourceFbo);
                int attachmentType = GL30.glGetFramebufferAttachmentParameteri(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE);
                if (attachmentType == GL11.GL_TEXTURE) {
                    int depthName = GL30.glGetFramebufferAttachmentParameteri(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME);
                    if (depthName > 0) {
                        externalSceneDepthTex = depthName;
                        capturedW = w;
                        capturedH = h;
                        capturedThisFrame = true;
                        logCapturePathChange("direct", depthName, w, h);
                        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, prevReadFbo);
                        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, prevDrawFbo);
                        logDepthSnapshot("capture_depth_texture", source.getDepthTextureId(), -1);
                        return;
                    }
                }

                GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, sourceFbo);
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, captureFbo);
                GL30.glBlitFramebuffer(0, 0, w, h, 0, 0, w, h, GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
                GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, prevReadFbo);
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, prevDrawFbo);
                capturedThisFrame = true;
                logCapturePathChange("blit", capturedSceneDepthTex, w, h);
                logDepthSnapshot("capture_depth_blit", source.getDepthTextureId(), -1);
            }
        }
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
            if (capturedThisFrame && capturedSceneDepthTex > 0 && capturedW > 0 && capturedH > 0) {
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
        if (externalSceneDepthTex > 0) {
            return externalSceneDepthTex;
        }
        if (originalDepthTex > 0) {
            return originalDepthTex;
        }
        if (combinedValidThisFrame && combinedSceneDepthTex > 0) {
            return combinedSceneDepthTex;
        }
        if (capturedSceneDepthTex > 0) {
            return capturedSceneDepthTex;
        }
        return cloudDepthTex;
    }

    private static String selectPrimarySceneDepthSource(int originalDepthTex, int cloudDepthTex) {
        if (externalSceneDepthTex > 0) {
            return "external";
        }
        if (originalDepthTex > 0) {
            return "original";
        }
        if (combinedValidThisFrame && combinedSceneDepthTex > 0) {
            return "combined";
        }
        if (capturedSceneDepthTex > 0) {
            return "captured";
        }
        return "cloud";
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

    private static boolean ensureSceneDepthTarget(int w, int h) {
        if (w <= 0 || h <= 0) {
            return false;
        }
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

        if (w != capturedW || h != capturedH) {
            GL11.glTexImage2D(3553, 0, 33190, w, h, 0, 6402, 5126, (ByteBuffer)null);
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
