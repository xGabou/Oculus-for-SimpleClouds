package dev.bettersimpleclouds.immersion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;

import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;

/**
 * Disables the blur on Simple Clouds' distant rain (storm fog), for the "Disable Distant Rain Blur" option.
 *
 * <p>After drawing the (low-res) distant rain, Simple Clouds runs a box blur over it ({@code doBlurPostProcessing}) and
 * blends the blurred result across the WHOLE screen - so the far rain's softness smears onto the edges of everything,
 * including a near rain mod's close particles (e.g. Pretty Rain / particlerain). When the option is on we skip that blur
 * pass entirely: the far rain still renders, just crisp, and no longer bleeds onto the near rain. Applies live.</p>
 *
 * <p>Gated on {@code simpleclouds}; {@code remap = false} (third-party target).</p>
 */
@Mixin(value = SimpleCloudsRenderer.class, remap = false)
public abstract class StormFogBlurMixin {

    @Inject(method = "doBlurPostProcessing", at = @At("HEAD"), cancellable = true)
    private void bsc$skipStormFogBlur(final float partialTick, final CallbackInfo ci) {
        if (BetterSimpleCloudsConfig.disableDistantRainBlur())
            ci.cancel();
    }
}
