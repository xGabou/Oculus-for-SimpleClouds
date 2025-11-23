package net.Gabou.oculus_for_simpleclouds.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import net.Gabou.oculus_for_simpleclouds.dh.ShaderAwareDhPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import dev.nonamecrackers2.simpleclouds.mixin.MixinRenderTargetAccessor;

/**
 * Final safety-net composite: blends the cloud target after all rendering so shader packs
 * cannot erase SimpleClouds.
 */
@Mixin(value = GameRenderer.class, priority = 500, remap = false)
public class GameRendererDebugBlitMixin {

    private static int compositeProgram = -1;
    private static int compositeVao = -1;
    private static int compositeVbo = -1;
    private static int locCloudColor = -1;
    private static int locCloudDepth = -1;
    private static int locSceneDepth = -1;
    private static int locDepthBias = -1;

    @Inject(method = "render", at = @At("TAIL"))
    private void ofsc$debugBlitClouds(float tickDelta, long nanoTime, boolean renderLevel, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (!renderLevel || mc.level == null) {
            return;
        }
        // Only force composite when shaders are running; vanilla path already renders clouds correctly.
        if (!CompatHelper.areShadersRunning()) {
            return;
        }
        SimpleCloudsRenderer.getOptionalInstance().ifPresent(renderer -> {
            if (renderer.getCloudTarget() == null) return;
            var window = mc.getWindow();
            int cloudColorTex = renderer.getCloudTarget().getColorTextureId();
            int cloudDepthTex = renderer.getCloudTarget().getDepthTextureId();
            int sceneDepthTex = mc.getMainRenderTarget().getDepthTextureId();
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

            int mainFbo = ((MixinRenderTargetAccessor) mc.getMainRenderTarget()).simpleclouds$getFrameBufferId();
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, mainFbo);
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GL20.glUseProgram(compositeProgram);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, cloudColorTex);
            GL20.glUniform1i(locCloudColor, 0);
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, cloudDepthTex);
            GL20.glUniform1i(locCloudDepth, 1);
            if (sceneDepthTex > 0) {
                GL13.glActiveTexture(GL13.GL_TEXTURE2);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, sceneDepthTex);
                GL20.glUniform1i(locSceneDepth, 2);
            }
            GL20.glUniform1f(locDepthBias, 1e-4f);

            GL30.glBindVertexArray(compositeVao);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);

            GL30.glBindVertexArray(prevVAO);
            if (!blendEnabled) GL11.glDisable(GL11.GL_BLEND);
            if (depthEnabled) GL11.glEnable(GL11.GL_DEPTH_TEST);
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
        String fragmentSrc = "#version 150\nin vec2 vUv;uniform sampler2D uCloudColor;uniform sampler2D uCloudDepth;uniform sampler2D uSceneDepth;uniform float uBias;out vec4 fragColor;void main(){float cloudD=texture(uCloudDepth,vUv).r; if(cloudD<=0.0){discard;} float sceneD=texture(uSceneDepth,vUv).r; bool sceneValid = sceneD < 0.9999; if(sceneValid && cloudD>=sceneD - uBias){discard;} vec4 col=texture(uCloudColor,vUv); fragColor=col;}";
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
}
