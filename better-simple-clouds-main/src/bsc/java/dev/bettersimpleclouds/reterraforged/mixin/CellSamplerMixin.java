package dev.bettersimpleclouds.reterraforged.mixin;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.minecraft.world.level.levelgen.DensityFunction;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;

/**
 * Stops a server-start crash from ReTerraForged when another mod samples biomes before worldgen is ready.
 *
 * <p>{@code raccoonman.reterraforged.world.worldgen.densityfunction.CellSampler#compute} resolves a deferred
 * {@code WorldLookup} (a memoising supplier) that dereferences ReTerraForged's {@code generatorContext}. When a mod
 * samples the biome source outside the normal worldgen flow - e.g. ProjectAtmosphere building its weather forecast in
 * {@code onServerStarted} - that context is still {@code null}, and the supplier throws a
 * {@link NullPointerException} that kills the server tick loop before the world can load.</p>
 *
 * <p>We probe the deferred lookup at the head of {@code compute}; if it isn't ready yet we return a neutral cell value
 * (0.0, the sampler's own minimum) instead of crashing. Guava's memoising supplier doesn't cache the failure, so once
 * worldgen finishes initialising the context the very next sample resolves normally - ReTerraForged terrain is
 * unaffected. String-targeted (no ReTerraForged compile dependency) and gated on {@code reterraforged}.</p>
 */
@Mixin(targets = "raccoonman.reterraforged.world.worldgen.densityfunction.CellSampler", remap = false)
public abstract class CellSamplerMixin {

    @Shadow @Final private Supplier deferredLookup;

    private static final Logger MIC_LOGGER = LogUtils.getLogger();
    private static boolean MIC_LOGGED;

    @Inject(method = "compute", at = @At("HEAD"), cancellable = true)
    private void mic$guardUninitializedContext(final DensityFunction.FunctionContext ctx, final CallbackInfoReturnable<Double> cir) {
        if (!BetterSimpleCloudsConfig.reterraforgedGuardEarlySampling())
            return;
        try {
            this.deferredLookup.get(); // forces ReTerraForged's WorldLookup; throws if its generator context isn't ready
        } catch (final Throwable t) {
            if (!MIC_LOGGED) {
                MIC_LOGGED = true;
                MIC_LOGGER.warn("[ReTerraForged guard] a biome sample ran before ReTerraForged's worldgen context was "
                        + "ready (e.g. ProjectAtmosphere's server-start forecast); returning a neutral value to avoid a "
                        + "crash. ReTerraForged terrain resolves normally once worldgen is initialised.", t);
            }
            cir.setReturnValue(0.0D);
        }
    }
}
