package dev.bettersimpleclouds.spawning.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandomList;

import dev.bettersimpleclouds.spawning.BlockedCloudTypes;

import dev.nonamecrackers2.simpleclouds.common.cloud.spawning.CloudSpawningConfig;

/**
 * Makes any cloud type whose per-type toggle is on (see
 * {@link dev.bettersimpleclouds.spawning.BlockedCloudTypes} - independent options for
 * {@code simpleclouds:itty_bitty} and {@code simpleclouds:cumulus}) never spawn.
 *
 * <p>Simple Clouds picks which cloud type to spawn next with a single weighted roll in
 * {@link CloudSpawningConfig#getRandom(RandomSource)} (a {@code WeightedRandomList} over the per-type spawn entries).
 * When the result is a blocked type we simply roll again (a few times) until it isn't, so its share of the spawn weight
 * goes to the other cloud types instead - the cloud count stays the same, there are just none of the blocked type.</p>
 *
 * <p>We deliberately <b>re-roll</b> rather than return {@link Optional#empty()}: {@code CloudGenerator.spawnCloud} calls
 * this method twice per attempt and passes the second result straight into {@code createRegion}, which dereferences it
 * without a null check - so handing back an empty result there would crash. Re-rolling always yields a valid type
 * (falling back to the original roll after a few tries, so it can never loop), keeping every caller null-safe.</p>
 *
 * <p>Gated on {@code simpleclouds} by the spawning patch's mixin plugin; {@code remap = false} (third-party target).
 * Common (both sides) because cloud spawning runs server-side in normal play.</p>
 */
@Mixin(value = CloudSpawningConfig.class, remap = false)
public abstract class CloudSpawningConfigBlacklistMixin {

    /** Re-rolls allowed before we give up and let the original roll stand (graceful, can't loop forever). */
    private static final int BSC_MAX_REROLLS = 8;

    @Shadow
    @Final
    private WeightedRandomList<CloudSpawningConfig.Info> weights;

    @Inject(method = "getRandom", at = @At("RETURN"), cancellable = true)
    private void bsc$dropIttyBitty(final RandomSource random,
                                   final CallbackInfoReturnable<Optional<CloudSpawningConfig.Info>> cir) {
        if (!BlockedCloudTypes.anyBlocked())
            return;
        Optional<CloudSpawningConfig.Info> result = cir.getReturnValue();
        int tries = 0;
        while (result != null && result.isPresent()
            && BlockedCloudTypes.isBlocked(result.get().cloudType()) && tries++ < BSC_MAX_REROLLS) {
            result = this.weights.getRandom(random);
        }
        cir.setReturnValue(result);
    }
}
