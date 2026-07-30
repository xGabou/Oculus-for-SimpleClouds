package dev.bettersimpleclouds.reterraforged;

import dev.bettersimpleclouds.core.ModIds;
import dev.bettersimpleclouds.core.RequiredModsMixinPlugin;

/**
 * Gates the ReTerraForged worldgen guard on {@code reterraforged} being present. The guard stops a crash where a mod
 * that samples biomes before worldgen is initialized (e.g. ProjectAtmosphere's server-start forecast) hits a null
 * generator context inside ReTerraForged's {@code CellSampler}.
 */
public final class ReterraforgedMixinPlugin extends RequiredModsMixinPlugin {

    public ReterraforgedMixinPlugin() {
        super("ReTerraForged worldgen guard", ModIds.RETERRAFORGED);
    }
}
