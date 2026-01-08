package net.Gabou.oculus_for_simpleclouds.mixin.dh;

import dev.nonamecrackers2.simpleclouds.SimpleCloudsMod;
import dev.nonamecrackers2.simpleclouds.client.dh.event.SimpleCloudsDhForgeEvents;
import dev.nonamecrackers2.simpleclouds.client.event.impl.DetermineCloudRenderPipelineEvent;
import net.Gabou.oculus_for_simpleclouds.dh.ShaderAwareDhPipeline;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SimpleCloudsDhForgeEvents.class, remap = false)
public class SimpleCloudsDhForgeEventsMixin {

    @Inject(method = "determineRenderPipeline", at = @At("HEAD"), cancellable = true)
    private static void oculusSC$selectPipeline(DetermineCloudRenderPipelineEvent event, CallbackInfo ci) {
        if (!SimpleCloudsMod.dhLoaded()) {
            return;
        }
        if (CompatHelper.areShadersRunning()) {
            event.overridePipeline(ShaderAwareDhPipeline.INSTANCE);
            ci.cancel();
        }
    }
}

