package net.Gabou.oculus_for_simpleclouds.mixin;

import dev.nonamecrackers2.simpleclouds.SimpleCloudsMod;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.renderer.pipeline.CloudsRenderPipeline;
import net.Gabou.oculus_for_simpleclouds.dh.ShaderAwareNoDhPipeline;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SimpleCloudsRenderer.class, remap = false)
public abstract class SimpleCloudsForcePipelineMixin {

    @Inject(method = "getRenderPipeline", at = @At("HEAD"), cancellable = true)
    private void oculus_for_simpleclouds$forceShaderAwarePipeline(CallbackInfoReturnable<CloudsRenderPipeline> cir) {
        if (CompatHelper.areShadersRunning() && !SimpleCloudsMod.dhLoaded()) {
            cir.setReturnValue(ShaderAwareNoDhPipeline.INSTANCE);
        }
    }
}
