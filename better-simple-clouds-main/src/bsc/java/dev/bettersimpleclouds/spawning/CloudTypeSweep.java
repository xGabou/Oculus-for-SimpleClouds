package dev.bettersimpleclouds.spawning;

import net.minecraft.world.level.Level;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManagerHolder;

/**
 * Fallback that sweeps away any cloud region whose type is on the {@code blockedCloudTypes} blacklist (see
 * {@link BlockedCloudTypes}) every {@value #SWEEP_INTERVAL_TICKS} ticks.
 *
 * <p>Blocked clouds are prevented at the source by two mixins: the weighted-roll re-roll
 * ({@link dev.bettersimpleclouds.spawning.mixin.CloudSpawningConfigBlacklistMixin}) for Simple Clouds' own
 * natural spawns, and the region-creation gate
 * ({@link dev.bettersimpleclouds.spawning.mixin.CloudGeneratorBlacklistMixin}) which refuses them from
 * <i>any</i> source, including clouds added straight through the Simple Clouds API by other mods (e.g. Project
 * Atmosphere). This sweep only mops up tiny regions that were never created here - e.g. loaded from a save or synced
 * from a server that doesn't have the option on: it walks the active {@link CloudManager}'s regions and drops every
 * blocked one (Simple Clouds' {@code removeClouds}; on the server that also syncs the removal to clients).</p>
 *
 * <p>Registered on the game event bus only when {@code simpleclouds} is present (so the Simple Clouds references are
 * safe), on <b>both</b> sides - it fires for each ticking level, so the server (authoritative) and any client-only
 * generation are both covered. Removal is a cheap predicate over a small region list, run once every two seconds.</p>
 */
public final class CloudTypeSweep {

    /** Sweep cadence in ticks (40 ticks = ~2 seconds). */
    private static final int SWEEP_INTERVAL_TICKS = 40;

    @SubscribeEvent
    public void onLevelTick(final LevelTickEvent.Post event) {
        if (!BlockedCloudTypes.anyBlocked())
            return;
        final Level level = event.getLevel();
        if (level.getGameTime() % SWEEP_INTERVAL_TICKS != 0L)
            return;
        if (!(level instanceof CloudManagerHolder<?> holder))
            return;
        final CloudManager<?> manager = holder.getCloudManager();
        if (manager == null)
            return;
        manager.getCloudGenerator().removeClouds(region -> BlockedCloudTypes.isBlocked(region.getCloudTypeId()));
    }
}
