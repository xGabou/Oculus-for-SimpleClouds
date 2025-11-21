package net.Gabou.oculus_for_simpleclouds.mixin.dh;

import dev.nonamecrackers2.simpleclouds.client.dh.event.SimpleCloudsDhForgeEvents;
import dev.nonamecrackers2.simpleclouds.client.event.impl.DetermineCloudRenderPipelineEvent;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleCloudsDhForgeEvents.class)
public class SimpleCloudsDhForgeEventsMixin {
    @Inject(method = "determineRenderPipeline", at = @At("HEAD"), cancellable = true)
    private static void oculus_for_simpleclouds$selectPipeline(DetermineCloudRenderPipelineEvent event, CallbackInfo ci) {
        if (CompatHelper.areShadersRunning()) {
            event.overridePipeline(ShaderAwareDhPipeline.INSTANCE);
            ci.cancel();
        }
    }
}
