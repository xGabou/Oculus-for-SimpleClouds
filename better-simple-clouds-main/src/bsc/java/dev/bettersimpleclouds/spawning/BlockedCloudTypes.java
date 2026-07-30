package dev.bettersimpleclouds.spawning;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.resources.ResourceLocation;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;

/**
 * Decides which Simple Clouds cloud types are currently blocked from spawning, from the
 * {@code blockedCloudTypes} config <b>blacklist</b> (any cloud type id can be listed; the default blocks only
 * {@code simpleclouds:itty_bitty}). Shared by the re-roll, the {@code addCloud} gate and the sweep so all three
 * agree on what's blocked at any moment.
 *
 * <p>The config stores plain strings; this parses them once and caches the resulting {@link ResourceLocation} set,
 * re-parsing only when the config hands back a different list (config edits/reloads swap the backing list, so an
 * identity check is enough). Lookups from the spawn path are therefore just a set {@code contains}.</p>
 */
public final class BlockedCloudTypes {

    private static volatile List<? extends String> cacheSource;
    private static volatile Set<ResourceLocation> cache = Set.of();

    /** @return true if the cloud type {@code id} is currently on the blacklist. */
    public static boolean isBlocked(final ResourceLocation id) {
        return id != null && blocked().contains(id);
    }

    /** @return true if at least one type is currently blocked - a cheap early-out before scanning/re-rolling. */
    public static boolean anyBlocked() {
        return !blocked().isEmpty();
    }

    /** @return the current blacklist as parsed ids (skipping any malformed entry). */
    public static Set<ResourceLocation> blocked() {
        final List<? extends String> source = BetterSimpleCloudsConfig.blockedCloudTypeIds();
        if (source != cacheSource) {
            final Set<ResourceLocation> parsed = new HashSet<>(source.size());
            for (final String s : source) {
                final ResourceLocation id = ResourceLocation.tryParse(s);
                if (id != null)
                    parsed.add(id);
            }
            cache = Set.copyOf(parsed);
            cacheSource = source;
        }
        return cache;
    }

    private BlockedCloudTypes() {}
}
