package net.Gabou.oculus_for_simpleclouds.mixin;


import net.Gabou.oculus_for_simpleclouds.IrisWeatherApi;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.uniforms.IrisExclusiveUniforms;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = IrisExclusiveUniforms.class, remap = false)
public class IrisExclusiveUniformsMixin {


    @Inject(method = "getThunderStrength", at = @At("HEAD"), cancellable = true)
    private static void pa$overrideThunderStrength(CallbackInfoReturnable<Float> cir) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            cir.setReturnValue(0.0F);
            return;
        }
        float tickDelta = CapturedRenderingState.INSTANCE.getTickDelta();
        float strength = Mth.clamp(
                IrisWeatherApi.getThunderStrength(mc.level, tickDelta),
                0.0F, 1.0F
        );

        cir.setReturnValue(strength);
    }
}
