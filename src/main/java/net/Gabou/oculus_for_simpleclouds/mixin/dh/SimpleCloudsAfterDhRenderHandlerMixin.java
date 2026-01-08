package net.Gabou.oculus_for_simpleclouds.mixin.dh;

import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import net.Gabou.oculus_for_simpleclouds.dh.ShaderAwareDhPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SimpleCloudsRenderer.class, remap = false)
public abstract class SimpleCloudsAfterDhRenderHandlerMixin {
    @Inject(method = "copyDepthFromMainToClouds", at = @At("HEAD"), cancellable = true)
    private void oculus_for_simpleclouds$disableMainDepthCopy(CallbackInfo ci) {
        if (ShaderAwareDhPipeline.skipDepthCopy) {
            ci.cancel();
        }
    }
}
