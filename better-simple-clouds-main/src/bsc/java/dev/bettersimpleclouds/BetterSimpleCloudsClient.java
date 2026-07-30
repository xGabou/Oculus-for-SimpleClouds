package dev.bettersimpleclouds;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import dev.bettersimpleclouds.client.gui.BscOptionsScreen;

/**
 * Client-only: registers Better Simple Clouds' own options screen (cloud-type blacklist, debug) behind
 * <b>Mods &rarr; Better Simple Clouds &rarr; Config</b>. The video/visual options are surfaced in Sodium's video
 * settings instead (see {@code dev.bettersimpleclouds.client.SodiumConfigIntegration}).
 */
@Mod(value = BetterSimpleClouds.MOD_ID, dist = Dist.CLIENT)
public final class BetterSimpleCloudsClient {

    public BetterSimpleCloudsClient(final ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class,
            (modContainer, parent) -> new BscOptionsScreen(parent));
    }
}
