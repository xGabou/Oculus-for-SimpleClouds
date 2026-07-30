package dev.bettersimpleclouds.performance.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;

/**
 * <b>Simple Clouds &mdash; concurrent cloud mesh generation</b> (the single biggest cloud-FPS win).
 *
 * <p>Simple Clouds rebuilds its cloud mesh every frame on the GPU, one compute dispatch per cloud chunk. With its
 * {@code concurrentComputeDispatches} option <em>off</em> (the default) each chunk is dispatched with a blocking
 * {@code dispatchAndWait} and the per-chunk output is packed into a shared buffer, so the render thread does a
 * CPU&harr;GPU round-trip after <em>every single chunk</em>. At HIGH level-of-detail that is hundreds of stalls per
 * frame &mdash; in a captured profile {@code simple_clouds_prepare -> mesh_generation} was ~68% of the entire frame
 * even on an RTX&nbsp;5070, because the GPU was being serialized, not saturated. (The actual cloud <em>draw</em> was
 * ~2%.)</p>
 *
 * <p>Turning the option on switches Simple Clouds to fixed-size per-chunk mesh sections: each chunk's slot in the
 * output buffer is known ahead of time, so the dispatches no longer need a sync between them to find the next write
 * offset. Simple Clouds reads that choice through {@code CloudsRendererSettings.useFixedMeshDataSectionSize()} (its
 * {@code DEFAULT} settings instance &mdash; an anonymous subclass &mdash; returns the config value). We force that
 * method to {@code true} so the fast path is used without the player having to find and flip the setting, and without
 * editing Simple Clouds' own config file. It only trades a little VRAM for the fixed slots, which is nothing on a
 * modern GPU.</p>
 *
 * <p>This is consistent with no rebuild loop: Simple Clouds' {@code checkAndOrBeginInitialization} compares
 * {@code settings.useFixedMeshDataSectionSize()} against the generator's actual {@code usesFixedMeshDataSectionSize()};
 * once it has (re)built the generator in fixed mode the two agree, so it settles. Flipping our config toggle at
 * runtime flips this return value, which Simple Clouds notices and rebuilds the generator for &mdash; so the option
 * can be A/B-tested live.</p>
 *
 * <p>Gated on {@code simpleclouds} by {@link dev.bettersimpleclouds.performance.SimpleCloudsPerformanceMixinPlugin
 * the patch's mixin plugin}; the injector is {@code require = 0} and the config is {@code required = false}, so if a
 * future Simple Clouds version renames the anonymous settings class this simply no-ops instead of crashing.</p>
 */
@Mixin(targets = "dev.nonamecrackers2.simpleclouds.client.renderer.settings.CloudsRendererSettings$1", remap = false)
public abstract class ConcurrentCloudMeshMixin {

    @Inject(method = "useFixedMeshDataSectionSize", at = @At("HEAD"), cancellable = true, require = 0)
    private void bsc$forceConcurrentMeshGen(final CallbackInfoReturnable<Boolean> cir) {
        if (BetterSimpleCloudsConfig.cloudOptimizeMeshGeneration())
            cir.setReturnValue(true);
    }
}
