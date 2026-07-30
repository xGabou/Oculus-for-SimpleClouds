package dev.bettersimpleclouds.immersion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;

import dev.nonamecrackers2.simpleclouds.client.mesh.lod.LevelOfDetail;
import dev.nonamecrackers2.simpleclouds.client.mesh.lod.LevelOfDetailConfig;

/**
 * Caps Simple Clouds' cloud-mesh level of detail so every cloud chunk renders at <b>at most</b> the configured LOD
 * level - far clouds still render, just never coarser than the cap, instead of degrading into single coarse cubes.
 *
 * <p>It does NOT cull: it rebuilds the chosen {@link LevelOfDetailConfig} so the coarser LOD rings are replaced by more
 * rings of the capped (finer) scale, keeping the cloud area's extent the same. So where Simple Clouds would have drawn
 * a few huge LOD cubes, you instead get more, finer cubes - the clouds keep their shape out to the horizon. The lower
 * the cap, the finer (and the more geometry), so it is opt-in: {@code 0} (off) or a cap at/above the config's own
 * coarsest level returns the original untouched.</p>
 *
 * <p>Hooked on {@code CloudMeshGenerator.Builder#lodConfig} - the setter the renderer feeds the active config through
 * before building the generator (confirmed: {@code SimpleCloudsRenderer} calls it). A change takes effect when the
 * cloud mesh generator is next (re)built - a resource reload (F3+T) or restart. Gated on {@code simpleclouds};
 * {@code remap = false} (third-party target); the injector is {@code require = 0} so it degrades to a no-op if a future
 * Simple Clouds renames the builder.</p>
 */
@Mixin(targets = "dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator$Builder", remap = false)
public abstract class CloudLodCapMixin {

    private static final Logger BSC_LOGGER = LogUtils.getLogger();

    @ModifyVariable(method = "lodConfig", at = @At("HEAD"), argsOnly = true, require = 0)
    private LevelOfDetailConfig bsc$capLodConfig(final LevelOfDetailConfig original) {
        if (original == null)
            return null;
        final int maxLevel = BetterSimpleCloudsConfig.cloudMaxLodLevel();
        final LevelOfDetail[] lods = original.getLods();
        // LOD levels: primary chunks = level 0, lods[i] = level i+1. Keep `maxLevel` ring entries. If the cap is off
        // (< 1) or at/beyond the config's own depth, there is nothing coarser to replace - leave it untouched.
        if (maxLevel < 1 || lods == null || maxLevel >= lods.length)
            return original;
        final int keep = maxLevel;
        final int capScale = lods[keep - 1].chunkScale();
        if (capScale <= 0)
            return original;
        // Refill the distance the dropped coarser rings covered with extra rings of the capped (finer) scale, so the
        // overall cloud area keeps the same extent (just rendered finer out there).
        int removedCoverage = 0;
        for (int i = keep; i < lods.length; i++)
            removedCoverage += lods[i].chunkScale() * lods[i].spread();
        final int extraSpread = (removedCoverage + capScale - 1) / capScale; // round up
        final LevelOfDetail[] capped = new LevelOfDetail[keep];
        for (int i = 0; i < keep - 1; i++)
            capped[i] = new LevelOfDetail(lods[i].chunkScale(), lods[i].spread());
        capped[keep - 1] = new LevelOfDetail(capScale, lods[keep - 1].spread() + extraSpread);
        BSC_LOGGER.info(
            "[Better Simple Clouds] Capped cloud LOD to level {}: {} LOD rings -> {} (coarsest chunk scale {} -> {}).",
            maxLevel, lods.length, capped.length, lods[lods.length - 1].chunkScale(), capScale);
        return new LevelOfDetailConfig(original.getPrimaryChunkSpan(), capped);
    }
}
