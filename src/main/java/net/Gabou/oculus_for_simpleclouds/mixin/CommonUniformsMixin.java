package net.Gabou.oculus_for_simpleclouds.mixin;

import net.Gabou.oculus_for_simpleclouds.IrisWeatherApi;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.uniforms.CommonUniforms; // adjust if your package differs
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;



@Mixin(value = CommonUniforms.class, remap = false)
public abstract class CommonUniformsMixin {

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
