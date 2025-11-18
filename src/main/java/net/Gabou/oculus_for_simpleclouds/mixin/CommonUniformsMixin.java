package net.Gabou.oculus_for_simpleclouds.mixin;

import net.Gabou.oculus_for_simpleclouds.IrisWeatherApi;
import net.Gabou.oculus_for_simpleclouds.SimpleCloudsUniforms;
import net.irisshaders.iris.shaderpack.IdMap;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.uniforms.CommonUniforms; // adjust if your package differs
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.shaderpack.properties.PackDirectives;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(value = CommonUniforms.class, remap = false)
public abstract class CommonUniformsMixin {

    @Inject(
            method = "addNonDynamicUniforms",
            at = @At("TAIL")
    )
    private static void ocs$injectSimpleCloudsUniforms(
            UniformHolder uniforms,
            IdMap idMap,
            PackDirectives directives,
            FrameUpdateNotifier updateNotifier,
            CallbackInfo ci
    ) {
        // vec4 simpleCloudsCloudState
        uniforms.uniform4f(
                UniformUpdateFrequency.PER_FRAME,
                "simpleCloudsCloudState",
                SimpleCloudsUniforms::sampleCloudState
        );

        // vec4 simpleCloudsCloudType
        uniforms.uniform4f(
                UniformUpdateFrequency.PER_FRAME,
                "simpleCloudsCloudType",
                SimpleCloudsUniforms::sampleCloudType
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
