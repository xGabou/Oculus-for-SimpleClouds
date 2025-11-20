package net.Gabou.oculus_for_simpleclouds.mixin;

import net.Gabou.oculus_for_simpleclouds.SimpleCloudsUniforms;
import net.irisshaders.iris.gl.program.ProgramUniforms;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL13C;
import org.lwjgl.opengl.GL20C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ProgramUniforms.class)
public class ProgramUniformsMixin {
    @Inject(method = "update()V", at = @At("TAIL"),remap = false)
    private void uploadSCUniforms(CallbackInfo ci) {

        // Iris stores the active program ID globally inside GlStateManager
        int program = GL20C.glGetInteger(GL20C.GL_CURRENT_PROGRAM);
        if (program == 0) return;

        GL20C.glUseProgram(program);

        // 1. Cloud State
        Vector4f state = SimpleCloudsUniforms.sampleCloudState();
        int locState = GL20C.glGetUniformLocation(program, "sc_State");
        if (locState >= 0) {
            GL20C.glUniform4f(locState, state.x, state.y, state.z, state.w);
        }

        // 2. Cloud Type
        Vector4f type = SimpleCloudsUniforms.sampleCloudType();
        int locType = GL20C.glGetUniformLocation(program, "sc_Type");
        if (locType >= 0) {
            GL20C.glUniform4f(locType, type.x, type.y, type.z, type.w);
        }

        // 3. Cloud shadow factor
        float shadow = SimpleCloudsUniforms.sampleCloudShadow();
        int locShadow = GL20C.glGetUniformLocation(program, "sc_CloudShadowFactor");
        if (locShadow >= 0) {
            GL20C.glUniform1f(locShadow, shadow);
        }

        // 4. Cloud layer texture
        int locTex = GL20C.glGetUniformLocation(program, "sc_CloudLayerTex");
        if (locTex >= 0) {
            SimpleCloudsUniforms.prepareCloudLayerTexture().ifPresent(tex -> {
                GL13C.glActiveTexture(GL13C.GL_TEXTURE0 + tex.textureUnit());
                GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, tex.textureId());
                GL20C.glUniform1i(locTex, tex.textureUnit());
            });
        }
    }
}
