package dev.bettersimpleclouds.spawning;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import net.minecraft.resources.ResourceLocation;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;

import dev.nonamecrackers2.simpleclouds.client.cloud.ClientSideCloudTypeManager;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;

/**
 * Enumerates every cloud type the blacklist editor should offer: Simple Clouds' built-in types, whatever the
 * client currently knows about (types synced from the server / loaded from datapacks), and any id already on the
 * blacklist (so a blocked type from an uninstalled datapack still shows up and can be un-blocked).
 */
public final class KnownCloudTypes {

    /** Simple Clouds' shipped types, so the full list is offered even in the main menu (nothing synced yet). */
    private static final List<String> BUILT_IN = List.of(
        "simpleclouds:cumulonimbus",
        "simpleclouds:cumulus",
        "simpleclouds:itty_bitty",
        "simpleclouds:nimbostratus",
        "simpleclouds:small_cumulus",
        "simpleclouds:stratocumulus",
        "simpleclouds:stratus");

    /** @return all offerable cloud type ids, sorted for a stable UI. */
    public static List<ResourceLocation> all() {
        final TreeSet<ResourceLocation> ids = new TreeSet<>();
        for (final String s : BUILT_IN)
            ids.add(ResourceLocation.parse(s));
        try {
            final Map<ResourceLocation, CloudType> synced = ClientSideCloudTypeManager.getInstance().getCloudTypes();
            if (synced != null)
                ids.addAll(synced.keySet());
        } catch (final Throwable ignored) {
            // No client-side manager (e.g. dedicated-server context) - the static list still covers the built-ins.
        }
        for (final String s : BetterSimpleCloudsConfig.blockedCloudTypeIds()) {
            final ResourceLocation id = ResourceLocation.tryParse(s);
            if (id != null)
                ids.add(id);
        }
        return List.copyOf(ids);
    }

    private KnownCloudTypes() {}
}
