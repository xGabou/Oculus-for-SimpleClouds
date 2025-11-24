package net.Gabou.oculus_for_simpleclouds.client;

import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.mixin.MixinRenderTargetAccessor;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Lightweight final composite that draws the cloud color using the cloud depth buffer,
 * after capturing the main depth early in the frame.
 */
@Mod.EventBusSubscriber(modid = "oculus_for_simpleclouds", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class FinalCloudCompositeHandler {

    private static int compositeProgram = -1;
    private static int compositeVao = -1;
    private static int compositeVbo = -1;
    private static int locCloudColor = -1;
    private static int locCloudDepth = -1;
    private static int locSceneDepth = -1;
    private static int locDepthBias = -1;
    private static int locReverseDepth = -1;

    private static int capturedSceneDepthTex = -1;
    private static int captureFbo = -1;
    private static int capturedW = -1;
    private static int capturedH = -1;
    private static long lastDepthLogMs = 0L;
    private static boolean capturedThisFrame = false;
    private static boolean reverseDepthDetected = false;

    private FinalCloudCompositeHandler() {}

    @SubscribeEvent
    public static void onRenderStage(RenderLevelStageEvent event) {
        // Do not touch the non-shader path; SimpleClouds already composites there.
        if (!CompatHelper.areShadersRunning()) {
            return;
        }
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
            if (!capturedThisFrame) {
                captureDepth(Minecraft.getInstance().getMainRenderTarget());
                capturedThisFrame = true;
            }
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS && !capturedThisFrame) {
            captureDepth(Minecraft.getInstance().getMainRenderTarget());
            capturedThisFrame = true;
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            compositeClouds();
            capturedThisFrame = false; // reset for the next frame
        }
    }

    public static void captureDepth(com.mojang.blaze3d.pipeline.RenderTarget source) {
        if (source == null) {
            return;
        }
        int w = source.width;
        int h = source.height;
        ensureSceneDepthTarget(w, h);
        int prevReadFbo = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        int prevDrawFbo = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        int sourceFbo = ((MixinRenderTargetAccessor) source).simpleclouds$getFrameBufferId();
        if (sourceFbo <= 0 || capturedSceneDepthTex <= 0 || captureFbo <= 0) {
            return;
        }
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, sourceFbo);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, captureFbo);
        GL11.glReadBuffer(GL11.GL_NONE);
        GL30.glBlitFramebuffer(0, 0, w, h, 0, 0, w, h, GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, prevReadFbo);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, prevDrawFbo);
        capturedThisFrame = true;
    }

    private static void compositeClouds() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        if (!capturedThisFrame || capturedSceneDepthTex <= 0 || capturedW <= 0 || capturedH <= 0) {
            return;
        }
        SimpleCloudsRenderer.getOptionalInstance().ifPresent(renderer -> {
            if (renderer.getCloudTarget() == null) return;
            int windowW = mc.getWindow().getWidth();
            int windowH = mc.getWindow().getHeight();
            int cloudColorTex = renderer.getCloudTarget().getColorTextureId();
            int cloudDepthTex = renderer.getCloudTarget().getDepthTextureId();
            if (cloudColorTex <= 0 || cloudDepthTex <= 0) {
                return;
            }
            if (!ensureCompositeProgram()) {
                return;
            }

            int prevProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
            int prevFbo = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
            int prevVAO = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
            boolean depthEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
            boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
            boolean depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);

            int mainFbo = ((MixinRenderTargetAccessor) mc.getMainRenderTarget()).simpleclouds$getFrameBufferId();
            int originalDepthTex = mc.getMainRenderTarget().getDepthTextureId();
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, mainFbo);
            GL11.glViewport(0, 0, windowW, windowH);
            boolean depthAttachmentOk = false;
            if (capturedSceneDepthTex > 0) {
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, capturedSceneDepthTex, 0);
                int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
                depthAttachmentOk = status == GL30.GL_FRAMEBUFFER_COMPLETE;
                if (!depthAttachmentOk) {
                    System.out.println("[OFSC WARN] Composite FBO incomplete for depth (" + status + "); falling back to shader compare.");
                    GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, originalDepthTex, 0);
                }
            }
            if (depthAttachmentOk) {
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glDepthFunc(reverseDepthDetected ? GL11.GL_GEQUAL : GL11.GL_LEQUAL);
            } else {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
            }
            GL11.glDepthMask(false);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GL20.glUseProgram(compositeProgram);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, cloudColorTex);
            GL20.glUniform1i(locCloudColor, 0);
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, cloudDepthTex);
            GL20.glUniform1i(locCloudDepth, 1);
            GL13.glActiveTexture(GL13.GL_TEXTURE2);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, capturedSceneDepthTex);
            GL20.glUniform1i(locSceneDepth, 2);
            GL20.glUniform1f(locDepthBias, 1e-3f);
            GL20.glUniform1i(locReverseDepth, reverseDepthDetected ? 1 : 0);

            maybeLogDepthSamples(cloudDepthTex, capturedSceneDepthTex, windowW, windowH);

            GL30.glBindVertexArray(compositeVao);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);

            GL30.glBindVertexArray(prevVAO);
            if (!blendEnabled) GL11.glDisable(GL11.GL_BLEND);
            GL11.glDepthMask(depthMask);
            if (originalDepthTex > 0) {
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, originalDepthTex, 0);
            }
            if (depthEnabled) GL11.glEnable(GL11.GL_DEPTH_TEST); else GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL20.glUseProgram(prevProgram);
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, prevFbo);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        });
    }

    private static boolean ensureCompositeProgram() {
        if (compositeProgram != -1 && compositeVao != -1 && compositeVbo != -1) {
            return true;
        }
        String vertexSrc = "#version 150\nin vec2 aPos;out vec2 vUv;void main(){vUv=aPos*0.5+0.5;gl_Position=vec4(aPos,0.0,1.0);}";
        String fragmentSrc = "#version 150\nin vec2 vUv;uniform sampler2D uCloudColor;uniform sampler2D uCloudDepth;uniform sampler2D uSceneDepth;uniform float uBias;uniform int uReverse;out vec4 fragColor;void main(){float cloudD=texture(uCloudDepth,vUv).r;float sceneD=texture(uSceneDepth,vUv).r;if(cloudD<=0.0||cloudD>=1.0){discard;}if(sceneD<0.999){discard;}gl_FragDepth = cloudD - uBias;fragColor=texture(uCloudColor,vUv);}";
        int vert = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vert, vertexSrc);
        GL20.glCompileShader(vert);
        if (GL20.glGetShaderi(vert, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
            System.out.println("[OFSC WARN] Composite vertex shader compile failed: " + GL20.glGetShaderInfoLog(vert));
            GL20.glDeleteShader(vert);
            return false;
        }
        int frag = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(frag, fragmentSrc);
        GL20.glCompileShader(frag);
        if (GL20.glGetShaderi(frag, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
            System.out.println("[OFSC WARN] Composite fragment shader compile failed: " + GL20.glGetShaderInfoLog(frag));
            GL20.glDeleteShader(vert);
            GL20.glDeleteShader(frag);
            return false;
        }
        compositeProgram = GL20.glCreateProgram();
        GL20.glAttachShader(compositeProgram, vert);
        GL20.glAttachShader(compositeProgram, frag);
        GL20.glLinkProgram(compositeProgram);
        GL20.glDeleteShader(vert);
        GL20.glDeleteShader(frag);
        if (GL20.glGetProgrami(compositeProgram, GL20.GL_LINK_STATUS) != GL11.GL_TRUE) {
            System.out.println("[OFSC WARN] Composite program link failed: " + GL20.glGetProgramInfoLog(compositeProgram));
            GL20.glDeleteProgram(compositeProgram);
            compositeProgram = -1;
            return false;
        }
        locCloudColor = GL20.glGetUniformLocation(compositeProgram, "uCloudColor");
        locCloudDepth = GL20.glGetUniformLocation(compositeProgram, "uCloudDepth");
        locSceneDepth = GL20.glGetUniformLocation(compositeProgram, "uSceneDepth");
        locDepthBias = GL20.glGetUniformLocation(compositeProgram, "uBias");
        locReverseDepth = GL20.glGetUniformLocation(compositeProgram, "uReverse");

        compositeVao = GL30.glGenVertexArrays();
        compositeVbo = GL15.glGenBuffers();
        GL30.glBindVertexArray(compositeVao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, compositeVbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, new float[]{-1f, -1f, 3f, -1f, -1f, 3f}, GL15.GL_STATIC_DRAW);
        int posLoc = GL20.glGetAttribLocation(compositeProgram, "aPos");
        GL20.glEnableVertexAttribArray(posLoc);
        GL20.glVertexAttribPointer(posLoc, 2, GL11.GL_FLOAT, false, 2 * Float.BYTES, 0);
        GL30.glBindVertexArray(0);
        return true;
    }

    private static void ensureSceneDepthTarget(int w, int h) {
        if (capturedSceneDepthTex == -1) {
            capturedSceneDepthTex = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, capturedSceneDepthTex);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        } else {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, capturedSceneDepthTex);
        }
        if (w != capturedW || h != capturedH) {
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_DEPTH_COMPONENT24, w, h, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (java.nio.ByteBuffer) null);
            capturedW = w;
            capturedH = h;
        }
        if (captureFbo == -1) {
            captureFbo = GL30.glGenFramebuffers();
        }
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, captureFbo);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, capturedSceneDepthTex, 0);
        GL30.glDrawBuffer(GL11.GL_NONE);
        GL30.glReadBuffer(GL11.GL_NONE);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    private static void maybeLogDepthSamples(int cloudDepthTex, int sceneDepthTex, int w, int h) {
        long now = System.currentTimeMillis();
        if (now - lastDepthLogMs < 1000L) {
            return;
        }
        lastDepthLogMs = now;
        float sceneCenter = sampleDepthAt(sceneDepthTex, 0.5f, 0.5f);
        float sceneNearX = sampleDepthAt(sceneDepthTex, 0.25f, 0.5f);
        float sceneFarX = sampleDepthAt(sceneDepthTex, 0.75f, 0.5f);
        float cloudCenter = sampleDepthAt(cloudDepthTex, 0.5f, 0.5f);
        float cloudNearX = sampleDepthAt(cloudDepthTex, 0.25f, 0.5f);
        float cloudFarX = sampleDepthAt(cloudDepthTex, 0.75f, 0.5f);
        System.out.println("[OFSC DEBUG] Composite depth samples: sceneCenter=" + sceneCenter + " cloudCenter=" + cloudCenter + " size=" + w + "x" + h);
        System.out.println("[OFSC DEBUG] Composite depth spans: sceneNearX=" + sceneNearX + " sceneFarX=" + sceneFarX + " cloudNearX=" + cloudNearX + " cloudFarX=" + cloudFarX);
        reverseDepthDetected = false; // force forward depth order
    }

    private static float sampleDepthAt(int tex, float u, float v) {
        if (tex <= 0) return -1f;
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
        int w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
        if (w <= 0 || h <= 0 || (long) w * (long) h > 4_000_000L) { // safeguard huge allocations
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            return -1f;
        }
        int x = Math.max(0, Math.min(w - 1, (int) (u * w)));
        int y = Math.max(0, Math.min(h - 1, (int) (v * h)));
        int idx = y * w + x;
        FloatBuffer buf = ByteBuffer.allocateDirect((int) ((long) w * (long) h * Float.BYTES))
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, buf);
        float val = buf.get(idx);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        return val;
    }
}
