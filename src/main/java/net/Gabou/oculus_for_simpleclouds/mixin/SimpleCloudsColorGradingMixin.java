package net.Gabou.oculus_for_simpleclouds.mixin;

import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import net.Gabou.oculus_for_simpleclouds.visual.CloudColorGrader;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SimpleCloudsRenderer.class, remap = false)
public abstract class SimpleCloudsColorGradingMixin {

    @Inject(method = "getCloudColor(F)[F", at = @At("RETURN"), cancellable = true)
    private void oculus_for_simpleclouds$gradeCloudColor(float partialTick, CallbackInfoReturnable<float[]> cir) {
        float[] color = cir.getReturnValue();
        if (color == null || color.length < 3) {
            return;
        }

        float[] graded = CloudColorGrader.grade(Minecraft.getInstance(), partialTick, color[0], color[1], color[2]);
        cir.setReturnValue(graded);
    }
}
