package dev.bettersimpleclouds.spawning.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.bettersimpleclouds.spawning.BlockedCloudTypes;

import dev.nonamecrackers2.simpleclouds.common.cloud.region.CloudRegion;
import dev.nonamecrackers2.simpleclouds.common.cloud.spawning.CloudGenerator;

/**
 * Prevents any cloud type whose per-type toggle is on (see {@link BlockedCloudTypes} - independent options for
 * {@code simpleclouds:itty_bitty} and {@code simpleclouds:cumulus}) from ever being added as a cloud region -
 * <b>at the moment of creation</b>, no matter who asks for it.
 *
 * <p>Every cloud region that ends up in the world funnels through one method:
 * {@link CloudGenerator#addCloud(CloudRegion, CloudGenerator.Order)}. Simple Clouds' own natural spawns
 * ({@code spawnCloud -> createRegion -> addCloud}), its initial generation on player join / on {@code /simpleclouds
 * refresh} ({@code doInitialGen -> addCloud}), and any add through the public Simple Clouds API - including
 * <b>Project Atmosphere</b> and other mods that spawn clouds programmatically - all call this same method.
 * {@code ServerCloudGenerator} overrides {@code addCloud} but calls {@code super.addCloud(...)}, so this HEAD injection
 * still runs on the authoritative server path. Gating here stops the tiny clouds from the very beginning through every
 * path, rather than deleting them after the fact.</p>
 *
 * <p>We simply return {@code false} (cancel the add). That's exactly the value {@code addCloud} already returns when it
 * refuses a region (unknown type, duplicate, region full), and every caller handles it: {@code spawnCloud} just treats
 * the point as a failed attempt, {@code doInitialGen} moves on, and API callers get the normal "not added" result. So
 * this is completely null-safe - unlike cancelling the weighted roll or {@code createRegion}, whose results get
 * dereferenced (see {@link CloudSpawningConfigBlacklistMixin}, which re-rolls natural spawns to another type so the
 * cloud count is preserved; this mixin is the catch-all that also covers API/Project-Atmosphere adds).</p>
 *
 * <p>Gated on {@code simpleclouds} by the spawning patch's mixin plugin; {@code remap = false} (third-party target).
 * Common (both sides) because cloud spawning is authoritative on the server in normal play.</p>
 */
@Mixin(value = CloudGenerator.class, remap = false)
public abstract class CloudGeneratorBlacklistMixin {

    @Inject(method = "addCloud", at = @At("HEAD"), cancellable = true)
    private void bsc$blockCloud(final CloudRegion region, final CloudGenerator.Order order,
                                final CallbackInfoReturnable<Boolean> cir) {
        if (region != null && BlockedCloudTypes.isBlocked(region.getCloudTypeId()))
            cir.setReturnValue(false);
    }
}
