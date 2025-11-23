package net.Gabou.oculus_for_simpleclouds.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import net.Gabou.oculus_for_simpleclouds.dh.ShaderAwareDhPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SimpleCloudsRenderer.class, remap = false)
public class SimpleCloudsNoUnsafeCopyMixin {

    @Inject(method = "_copyDepthSafe", at = @At("HEAD"), cancellable = true)
    private void oculusSC$cancelUnsafeDepthCopy(RenderTarget _to, RenderTarget from, CallbackInfo ci) {
        if (ShaderAwareDhPipeline.skipDepthCopy) {
            ShaderAwareDhPipeline.skipDepthCopy = false; // reset so next frame can retry
            ci.cancel();
        }

    }
}
