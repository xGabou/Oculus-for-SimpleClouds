package net.Gabou.oculus_for_simpleclouds.mixin;

import net.Gabou.oculus_for_simpleclouds.IrisWeatherApi;
import net.Gabou.oculus_for_simpleclouds.SimpleCloudsUniforms;
import net.irisshaders.iris.gl.state.FogMode;
import net.irisshaders.iris.gl.uniform.DynamicUniformHolder;
import net.irisshaders.iris.shaderpack.IdMap;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.uniforms.CommonUniforms; // adjust if your package differs
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.shaderpack.properties.PackDirectives;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(value = CommonUniforms.class, remap = false)
public class CommonUniformsMixin {

    @Inject(
            method = "addDynamicUniforms",
            at = @At("TAIL")
    )
    private static void ocs_addSCUniforms(DynamicUniformHolder uniforms, FogMode fogMode, CallbackInfo ci) {

        // sc_State (vec4)
        uniforms.uniform4f(
                net.irisshaders.iris.gl.uniform.UniformUpdateFrequency.PER_FRAME,
                "sc_State",
                () -> {
                    Vector4f state = SimpleCloudsUniforms.sampleCloudState();
                    return new Vector4f(state.x, state.y, state.z, state.w);
                }
        );

        // sc_Type (vec4)
        uniforms.uniform4f(
                net.irisshaders.iris.gl.uniform.UniformUpdateFrequency.PER_FRAME,
                "sc_Type",
                () -> {
                    Vector4f type = SimpleCloudsUniforms.sampleCloudType();
                    return new Vector4f(type.x, type.y, type.z, type.w);
                }
        );

        // sc_CloudShadowFactor (float)
        uniforms.uniform1f(
                net.irisshaders.iris.gl.uniform.UniformUpdateFrequency.PER_FRAME,
                "sc_CloudShadowFactor",
                () -> SimpleCloudsUniforms.sampleCloudShadow()
        );

        uniforms.uniform1i(
                net.irisshaders.iris.gl.uniform.UniformUpdateFrequency.PER_FRAME,
                "sc_CloudLayerTex",
                () -> SimpleCloudsUniforms.prepareCloudLayerTexture()
                        .map(t -> t.textureUnit())
                        .orElse(0)
        );

        uniforms.uniform1f(
                UniformUpdateFrequency.PER_FRAME,
                "sc_Time",
                () -> {
                    Minecraft client = Minecraft.getInstance();
                    if (client.level == null) {
                        return 0.0f;
                    }
                    float tickDelta = CapturedRenderingState.INSTANCE.getTickDelta();
                    return (float) client.level.getGameTime() + tickDelta;
                }
        );


    }



    @Inject(method = "getRainStrength", at = @At("HEAD"), cancellable = true)
    private static void pa$overrideRainStrength(CallbackInfoReturnable<Float> cir) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) {
            cir.setReturnValue(0.0F);
            return;
        }

        float tickDelta = CapturedRenderingState.INSTANCE.getTickDelta();
        float strength = Mth.clamp(
                IrisWeatherApi.getRainStrength(client.level, tickDelta),
                0.0F, 1.0F
        );

        cir.setReturnValue(strength);
    }
}
