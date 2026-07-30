package dev.bettersimpleclouds;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;
import dev.bettersimpleclouds.core.ModIds;
import dev.bettersimpleclouds.immersion.InCloudImmersionPatch;
import dev.bettersimpleclouds.spawning.CloudTypeSweep;

/**
 * <b>Better Simple Clouds</b> &mdash; improvements and fixes for
 * <a href="https://modrinth.com/mod/simple-clouds">Simple Clouds</a>:
 *
 * <ul>
 *   <li><b>In-cloud immersion</b> &mdash; solid cloud faces, a filled interior with an opaque view-distance shell,
 *       drifting motes and optional fog while flying inside a cloud (client).</li>
 *   <li><b>Far-cloud LOD</b> &mdash; cap LOD coarseness, soften/cull far small clouds, remove floating cubes
 *       (client).</li>
 *   <li><b>Storm fog</b> &mdash; distant rain visible behind clouds; optional full rain-blur disable (client).</li>
 *   <li><b>Transparency fix</b> &mdash; clears the mottled look of translucent cloud edges seen through other
 *       clouds (client).</li>
 *   <li><b>Cloud spawning blacklist</b> &mdash; block any cloud type from ever spawning (both sides; the server is
 *       authoritative in normal play).</li>
 *   <li><b>Performance</b> &mdash; optional concurrent cloud mesh generation (client, experimental).</li>
 * </ul>
 *
 * <p>Mixin-based features self-gate through {@link dev.bettersimpleclouds.core.RequiredModsMixinPlugin} subclasses;
 * event-based features are registered here, guarded on mod presence + physical side.</p>
 */
@Mod(BetterSimpleClouds.MOD_ID)
public final class BetterSimpleClouds {

    public static final String MOD_ID = "bettersimpleclouds";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BetterSimpleClouds(final IEventBus modBus, final ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, BetterSimpleCloudsConfig.COMMON_SPEC);
        if (FMLEnvironment.dist == Dist.CLIENT)
            container.registerConfig(ModConfig.Type.CLIENT, BetterSimpleCloudsConfig.CLIENT_SPEC);

        LOGGER.info("[Better Simple Clouds] loaded.");

        registerInCloudImmersion();
        registerCloudTypeSweep();
    }

    private void registerCloudTypeSweep() {
        // Common (both sides): the sweep must run server-side, where cloud spawning is authoritative, so removing
        // blacklisted regions there syncs the removal to clients. References Simple Clouds classes, so register only
        // when Simple Clouds is present.
        if (allPresent(ModIds.SIMPLE_CLOUDS)) {
            NeoForge.EVENT_BUS.register(new CloudTypeSweep());
            logFeature("cloud-type blacklist sweep", "ACTIVE");
        } else {
            logFeature("cloud-type blacklist sweep", "dormant (missing: " + ModIds.SIMPLE_CLOUDS + ")");
        }
    }

    private void registerInCloudImmersion() {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            logFeature("in-cloud immersion", "inactive (server side)");
            return;
        }
        if (allPresent(ModIds.SIMPLE_CLOUDS)) {
            NeoForge.EVENT_BUS.register(new InCloudImmersionPatch());
            InCloudImmersionPatch.logReady();
            logFeature("in-cloud immersion", "ACTIVE");
        } else {
            logFeature("in-cloud immersion", "dormant (missing: " + ModIds.SIMPLE_CLOUDS + ")");
        }
    }

    private static boolean allPresent(final String... modIds) {
        final ModList mods = ModList.get();
        if (mods == null)
            return false;
        for (final String id : modIds)
            if (!mods.isLoaded(id))
                return false;
        return true;
    }

    private static void logFeature(final String name, final String status) {
        LOGGER.info("[Better Simple Clouds]   feature '{}' -> {}", name, status);
    }
}
