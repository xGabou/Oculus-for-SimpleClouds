package net.Gabou.oculus_for_simpleclouds.mixin;

import net.Gabou.oculus_for_simpleclouds.SimpleCloudsUniforms;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL13C;
import org.lwjgl.opengl.GL20C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.irisshaders.iris.pipeline.programs.ExtendedShader")
public class ExtendedShaderMixin {

    @Inject(
            method = "apply",
            at = @At("TAIL")
    )
    private void injectSimpleCloudUniforms(CallbackInfo ci) {
        int program = GL20C.glGetInteger(GL20C.GL_CURRENT_PROGRAM);
        if (program == 0) return;

        // Cloud State
        Vector4f state = SimpleCloudsUniforms.sampleCloudState();
        int loc1 = GL20C.glGetUniformLocation(program, "sc_State");
        if (loc1 >= 0) {
            GL20C.glUniform4f(loc1, state.x, state.y, state.z, state.w);
        }

        // Cloud Type
        Vector4f type = SimpleCloudsUniforms.sampleCloudType();
        int loc2 = GL20C.glGetUniformLocation(program, "sc_Type");
        if (loc2 >= 0) {
            GL20C.glUniform4f(loc2, type.x, type.y, type.z, type.w);
        }
        // Cloud Shadow Factor (derived from base storminess)
        // Shadow Strength from SC
        float shadowStrength = SimpleCloudsUniforms.sampleCloudShadow(); // we add this method below
        int loc3 = GL20C.glGetUniformLocation(program, "sc_CloudShadowFactor");
        if (loc3 >= 0) {
            GL20C.glUniform1f(loc3, shadowStrength);
        }

        int samplerLoc = GL20C.glGetUniformLocation(program, "sc_CloudLayerTex");
        if (samplerLoc >= 0) {
            SimpleCloudsUniforms.prepareCloudLayerTexture().ifPresent(textureState -> {
                int previousActive = GL11C.glGetInteger(GL13C.GL_ACTIVE_TEXTURE);
                int unitIndex = textureState.textureUnit();
                GL13C.glActiveTexture(GL13C.GL_TEXTURE0 + unitIndex);
                GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, textureState.textureId());
                GL20C.glUniform1i(samplerLoc, unitIndex);
                GL13C.glActiveTexture(previousActive);
            });
        }


    }


}
