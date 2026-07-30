package dev.bettersimpleclouds.immersion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.bettersimpleclouds.immersion.CloudWorldFogRange;

import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;

/**
 * Brackets Simple Clouds' screen-space world-fog pass so it fogs the clouds over the cloud field's own extent instead
 * of the terrain's &mdash; see {@link CloudWorldFogRange} for why that pass otherwise buries the clouds at night.
 *
 * <p>The swap is done around the whole method rather than by redirecting the individual uniform writes: the method sets
 * them inside a loop over every post pass, so one push/pop pair covers all of them and keeps working if Simple Clouds
 * adds another pass or reorders the uniforms.</p>
 *
 * <p>{@code require = 0} keeps the mod loading if a future Simple Clouds renames the method; the pass then simply keeps
 * using the terrain fog as it does today.</p>
 */
@Mixin(SimpleCloudsRenderer.class)
public abstract class ScreenSpaceWorldFogRangeMixin {

    @Inject(method = "doScreenSpaceWorldFog", at = @At("HEAD"), require = 0)
    private void bsc$cloudWorldFogPush(final CallbackInfo ci) {
        CloudWorldFogRange.push();
    }

    @Inject(method = "doScreenSpaceWorldFog", at = @At("RETURN"), require = 0)
    private void bsc$cloudWorldFogPop(final CallbackInfo ci) {
        CloudWorldFogRange.pop();
    }
}
