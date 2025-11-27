//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.Gabou.oculus_for_simpleclouds.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.mixin.MixinRenderTargetAccessor;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
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
    private static long lastDepthLogMs = 0L;
    private static boolean capturedThisFrame = false;
    private static boolean reverseDepthDetected = false;
    private static boolean dhDepthMergedThisFrame = false;
    private static int dhMergeProgram = -1;
    private static int dhMergeVao = -1;
    private static int dhMergeVbo = -1;
    private static int dhMergeSamplerLoc = -1;
    private static final boolean DEBUG_DEPTH_SELECTION = Boolean.getBoolean("ofsc.debug.depthSelection");

    private FinalCloudCompositeHandler() {
    }

    @SubscribeEvent
    public static void onRenderStage(RenderLevelStageEvent event) {
        if (CompatHelper.areShadersRunning()) {
            if (event.getStage() == Stage.AFTER_LEVEL) {
                compositeClouds();
                capturedThisFrame = false;
            }

        }
    }

    public static void captureDepth(RenderTarget source) {
        if (source != null) {
            int w = source.width;
            int h = source.height;
            ensureSceneDepthTarget(w, h);
            dhDepthMergedThisFrame = false;
            externalSceneDepthTex = -1;
            int prevReadFbo = GL11.glGetInteger(36010);
            int prevDrawFbo = GL11.glGetInteger(36006);
            int sourceFbo = ((MixinRenderTargetAccessor)source).simpleclouds$getFrameBufferId();
            if (sourceFbo > 0 && capturedSceneDepthTex > 0 && captureFbo > 0) {
                GL30.glBindFramebuffer(36160, sourceFbo);
                int attachmentType = GL30.glGetFramebufferAttachmentParameteri(36160, 36096, 36048);
                if (attachmentType == 5890) {
                    int depthName = GL30.glGetFramebufferAttachmentParameteri(36160, 36096, 36049);
                    if (depthName > 0) {
                        externalSceneDepthTex = depthName;
                    }
                }

                GL30.glBindFramebuffer(36160, prevReadFbo);
                GL30.glBindFramebuffer(36160, prevDrawFbo);
                GL30.glBindFramebuffer(36008, sourceFbo);
                GL30.glBindFramebuffer(36009, captureFbo);
                GL11.glReadBuffer(0);
                GL30.glBlitFramebuffer(0, 0, w, h, 0, 0, w, h, 256, 9728);
                GL30.glBindFramebuffer(36008, prevReadFbo);
                GL30.glBindFramebuffer(36009, prevDrawFbo);
                capturedW = w;
                capturedH = h;
                capturedThisFrame = true;
            }
        }
    }

    /**
     * Merge a Distant Horizons depth texture into the captured scene depth so that far terrain
     * can occlude clouds during the final composite pass.
     */
    public static void mergeDistantHorizonsDepth(int dhDepthTex, boolean reverseDepth) {
        if (dhDepthTex <= 0 && capturedThisFrame && DEBUG_DEPTH_SELECTION) {
            System.out.println("[OFSC DEBUG] DH depth not exposed; falling back to captured vanilla depth only.");
        }
        if (!capturedThisFrame || dhDepthTex <= 0 || capturedSceneDepthTex <= 0 || captureFbo <= 0) {
            return;
        }
        int dhW = GL11.glGetTexLevelParameteri(3553, 0, 4096);
        int dhH = GL11.glGetTexLevelParameteri(3553, 0, 4097);
        if (dhW <= 0 || dhH <= 0) {
            return;
        }
        if (!ensureDhMergeProgram()) {
            return;
        }

        int prevProgram = GL11.glGetInteger(35725);
        int prevFbo = GL11.glGetInteger(36006);
        int prevVao = GL11.glGetInteger(34229);
        boolean blendEnabled = GL11.glIsEnabled(3042);
        boolean depthEnabled = GL11.glIsEnabled(2929);
        boolean depthMask = GL11.glGetBoolean(2930);
        int prevDepthFunc = GL11.glGetInteger(2932);
        IntBuffer viewport = ByteBuffer.allocateDirect(16).order(ByteOrder.nativeOrder()).asIntBuffer();
        GL11.glGetIntegerv(2978, viewport);

        GL30.glBindFramebuffer(36160, captureFbo);
        GL11.glViewport(0, 0, capturedW, capturedH);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDepthFunc(reverseDepth ? 518 : 515);
        GL11.glColorMask(false, false, false, false);

        GL20.glUseProgram(dhMergeProgram);
        GL13.glActiveTexture(33984);
        GL11.glBindTexture(3553, dhDepthTex);
        if (dhMergeSamplerLoc >= 0) {
            GL20.glUniform1i(dhMergeSamplerLoc, 0);
        }
        GL30.glBindVertexArray(dhMergeVao);
        GL11.glDrawArrays(4, 0, 3);
        GL30.glBindVertexArray(prevVao);

        GL11.glColorMask(true, true, true, true);
        GL11.glDepthFunc(prevDepthFunc);
        GL11.glDepthMask(depthMask);
        if (depthEnabled) {
            GL11.glEnable(2929);
        } else {
            GL11.glDisable(2929);
        }
        if (blendEnabled) {
            GL11.glEnable(3042);
        } else {
            GL11.glDisable(3042);
        }
        GL20.glUseProgram(prevProgram);
        GL30.glBindFramebuffer(36160, prevFbo);
        GL11.glViewport(viewport.get(0), viewport.get(1), viewport.get(2), viewport.get(3));

        reverseDepthDetected = reverseDepth;
        dhDepthMergedThisFrame = true;
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
                                int mainFbo = ((MixinRenderTargetAccessor)mc.getMainRenderTarget()).simpleclouds$getFrameBufferId();
                                int originalDepthTex = mc.getMainRenderTarget().getDepthTextureId();
                                int preferredSceneDepth = dhDepthMergedThisFrame && capturedSceneDepthTex > 0
                                        ? capturedSceneDepthTex
                                        : (externalSceneDepthTex > 0 ? externalSceneDepthTex : capturedSceneDepthTex);
                                GL30.glBindFramebuffer(36160, mainFbo);
                                GL11.glViewport(0, 0, windowW, windowH);
                                boolean depthAttachmentOk = false;
                                int depthTex = preferredSceneDepth;
                                int attachedDepthTex = 0;
                                if (depthTex > 0) {
                                    GL30.glFramebufferTexture2D(36160, 36096, 3553, depthTex, 0);
                                    int status = GL30.glCheckFramebufferStatus(36160);
                                    depthAttachmentOk = status == 36053;
                                    attachedDepthTex = depthAttachmentOk ? depthTex : 0;
                                    if (!depthAttachmentOk) {
                                        System.out.println("[OFSC WARN] Composite FBO incomplete for depth (" + status + "); falling back to shader compare.");
                                        GL30.glFramebufferTexture2D(36160, 36096, 3553, originalDepthTex, 0);
                                        attachedDepthTex = originalDepthTex;
                                    }
                                } else if (originalDepthTex > 0) {
                                    attachedDepthTex = originalDepthTex;
                                }

                                if (depthAttachmentOk || attachedDepthTex > 0) {
                                    GL11.glEnable(2929);
                                    GL11.glDepthFunc(reverseDepthDetected ? 518 : 515);
                                } else {
                                    GL11.glDisable(2929);
                                }

                                GL11.glDepthMask(false);
                                GL11.glEnable(3042);
                                GL11.glBlendFunc(770, 771);
                                GL20.glUseProgram(compositeProgram);
                                GL13.glActiveTexture(33984);
                                GL11.glBindTexture(3553, cloudColorTex);
                                GL20.glUniform1i(locCloudColor, 0);
                                GL13.glActiveTexture(33985);
                                GL11.glBindTexture(3553, cloudDepthTex);
                                GL20.glUniform1i(locCloudDepth, 1);
                                GL13.glActiveTexture(33986);
                                int sampledSceneDepth = attachedDepthTex > 0 ? attachedDepthTex
                                        : (preferredSceneDepth > 0 ? preferredSceneDepth
                                        : (externalSceneDepthTex > 0 ? externalSceneDepthTex : capturedSceneDepthTex));
                                GL11.glBindTexture(3553, sampledSceneDepth);
                                GL20.glUniform1i(locSceneDepth, 2);
                                GL20.glUniform1f(locDepthBias, 0.001F);
                                GL20.glUniform1i(locReverseDepth, reverseDepthDetected ? 1 : 0);
                                maybeLogDepthChoice(sampledSceneDepth, attachedDepthTex, cloudDepthTex, windowW, windowH);
                                GL30.glBindVertexArray(compositeVao);
                                GL11.glDrawArrays(4, 0, 3);
                                GL30.glBindVertexArray(prevVAO);
                                if (!blendEnabled) {
                                    GL11.glDisable(3042);
                                }

                                GL11.glDepthMask(depthMask);
                                if (originalDepthTex > 0) {
                                    GL30.glFramebufferTexture2D(36160, 36096, 3553, originalDepthTex, 0);
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
                            }
                        }
                    }
                });
            }
        }
    }

    private static boolean ensureCompositeProgram() {
        if (compositeProgram != -1 && compositeVao != -1 && compositeVbo != -1) {
            return true;
        } else {
            String vertexSrc = "#version 150\nin vec2 aPos;out vec2 vUv;void main(){vUv=aPos*0.5+0.5;gl_Position=vec4(aPos,0.0,1.0);}";
            String fragmentSrc = "#version 150\nin vec2 vUv;uniform sampler2D uCloudColor;uniform sampler2D uCloudDepth;uniform sampler2D uSceneDepth;uniform float uBias;uniform int uReverse;out vec4 fragColor;void main(){float cloudD=texture(uCloudDepth,vUv).r;float sceneD=texture(uSceneDepth,vUv).r;if(cloudD<=0.0||cloudD>=1.0){discard;}if(sceneD<0.999){discard;}gl_FragDepth = cloudD - uBias;fragColor=texture(uCloudColor,vUv);}";
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

    private static void ensureSceneDepthTarget(int w, int h) {
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

        GL30.glBindFramebuffer(36160, captureFbo);
        GL30.glFramebufferTexture2D(36160, 36096, 3553, capturedSceneDepthTex, 0);
        GL30.glDrawBuffer(0);
        GL30.glReadBuffer(0);
        GL30.glBindFramebuffer(36160, 0);
    }

    private static void maybeLogDepthChoice(int sampledSceneDepthTex, int attachedDepthTex, int cloudDepthTex, int w, int h) {
        if (!DEBUG_DEPTH_SELECTION) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastDepthLogMs < 500L) {
            return;
        }
        lastDepthLogMs = now;
        float sceneCenter = sampleDepthAt(sampledSceneDepthTex, 0.5F, 0.5F);
        float mergedCenter = dhDepthMergedThisFrame ? sampleDepthAt(capturedSceneDepthTex, 0.5F, 0.5F) : -1.0F;
        float cloudCenter = sampleDepthAt(cloudDepthTex, 0.5F, 0.5F);
        System.out.println(String.format(
                "[OFSC DEBUG] Depth choice: sampled=%d attached=%d dhMerged=%s reverseDepth=%s size=%dx%d sceneCenter=%.4f mergedCenter=%.4f cloudCenter=%.4f",
                sampledSceneDepthTex,
                attachedDepthTex,
                dhDepthMergedThisFrame,
                reverseDepthDetected,
                w,
                h,
                sceneCenter,
                mergedCenter,
                cloudCenter
        ));
    }

    private static void maybeLogDepthSamples(int cloudDepthTex, int sceneDepthTex, int w, int h) {
        long now = System.currentTimeMillis();
        if (now - lastDepthLogMs >= 1000L) {
            lastDepthLogMs = now;
            float sceneCenter = sampleDepthAt(sceneDepthTex, 0.5F, 0.5F);
            float sceneNearX = sampleDepthAt(sceneDepthTex, 0.25F, 0.5F);
            float sceneFarX = sampleDepthAt(sceneDepthTex, 0.75F, 0.5F);
            float cloudCenter = sampleDepthAt(cloudDepthTex, 0.5F, 0.5F);
            float cloudNearX = sampleDepthAt(cloudDepthTex, 0.25F, 0.5F);
            float cloudFarX = sampleDepthAt(cloudDepthTex, 0.75F, 0.5F);
            System.out.println("[OFSC DEBUG] Composite depth samples: sceneCenter=" + sceneCenter + " cloudCenter=" + cloudCenter + " size=" + w + "x" + h);
            System.out.println("[OFSC DEBUG] Composite depth spans: sceneNearX=" + sceneNearX + " sceneFarX=" + sceneFarX + " cloudNearX=" + cloudNearX + " cloudFarX=" + cloudFarX);
            reverseDepthDetected = false;
        }
    }

    private static boolean ensureDhMergeProgram() {
        if (dhMergeProgram != -1 && dhMergeVao != -1 && dhMergeVbo != -1) {
            return true;
        }
        String vertexSrc = "#version 150\nin vec2 aPos;out vec2 vUv;void main(){vUv=aPos*0.5+0.5;gl_Position=vec4(aPos,0.0,1.0);}";
        String fragmentSrc = "#version 150\nin vec2 vUv;uniform sampler2D uDepth;void main(){float d=texture(uDepth,vUv).r;if(d<=0.0||d>=1.0) discard;gl_FragDepth=d;}";
        int vert = GL20.glCreateShader(35633);
        GL20.glShaderSource(vert, vertexSrc);
        GL20.glCompileShader(vert);
        if (GL20.glGetShaderi(vert, 35713) != 1) {
            System.out.println("[OFSC WARN] DH merge vertex shader compile failed: " + GL20.glGetShaderInfoLog(vert));
            GL20.glDeleteShader(vert);
            return false;
        }
        int frag = GL20.glCreateShader(35632);
        GL20.glShaderSource(frag, fragmentSrc);
        GL20.glCompileShader(frag);
        if (GL20.glGetShaderi(frag, 35713) != 1) {
            System.out.println("[OFSC WARN] DH merge fragment shader compile failed: " + GL20.glGetShaderInfoLog(frag));
            GL20.glDeleteShader(vert);
            GL20.glDeleteShader(frag);
            return false;
        }

        dhMergeProgram = GL20.glCreateProgram();
        GL20.glAttachShader(dhMergeProgram, vert);
        GL20.glAttachShader(dhMergeProgram, frag);
        GL20.glLinkProgram(dhMergeProgram);
        GL20.glDeleteShader(vert);
        GL20.glDeleteShader(frag);
        if (GL20.glGetProgrami(dhMergeProgram, 35714) != 1) {
            System.out.println("[OFSC WARN] DH merge program link failed: " + GL20.glGetProgramInfoLog(dhMergeProgram));
            GL20.glDeleteProgram(dhMergeProgram);
            dhMergeProgram = -1;
            return false;
        }

        dhMergeSamplerLoc = GL20.glGetUniformLocation(dhMergeProgram, "uDepth");
        dhMergeVao = GL30.glGenVertexArrays();
        dhMergeVbo = GL15.glGenBuffers();
        GL30.glBindVertexArray(dhMergeVao);
        GL15.glBindBuffer(34962, dhMergeVbo);
        GL15.glBufferData(34962, new float[]{-1.0F, -1.0F, 3.0F, -1.0F, -1.0F, 3.0F}, 35044);
        int posLoc = GL20.glGetAttribLocation(dhMergeProgram, "aPos");
        GL20.glEnableVertexAttribArray(posLoc);
        GL20.glVertexAttribPointer(posLoc, 2, 5126, false, 8, 0L);
        GL30.glBindVertexArray(0);
        return true;
    }

    private static float sampleDepthAt(int tex, float u, float v) {
        if (tex <= 0) {
            return -1.0F;
        } else {
            GL11.glBindTexture(3553, tex);
            int w = GL11.glGetTexLevelParameteri(3553, 0, 4096);
            int h = GL11.glGetTexLevelParameteri(3553, 0, 4097);
            if (w > 0 && h > 0 && (long)w * (long)h <= 4000000L) {
                int x = Math.max(0, Math.min(w - 1, (int)(u * (float)w)));
                int y = Math.max(0, Math.min(h - 1, (int)(v * (float)h)));
                int idx = y * w + x;
                FloatBuffer buf = ByteBuffer.allocateDirect((int)((long)w * (long)h * 4L)).order(ByteOrder.nativeOrder()).asFloatBuffer();
                GL11.glGetTexImage(3553, 0, 6402, 5126, buf);
                float val = buf.get(idx);
                GL11.glBindTexture(3553, 0);
                return val;
            } else {
                GL11.glBindTexture(3553, 0);
                return -1.0F;
            }
        }
    }
}
