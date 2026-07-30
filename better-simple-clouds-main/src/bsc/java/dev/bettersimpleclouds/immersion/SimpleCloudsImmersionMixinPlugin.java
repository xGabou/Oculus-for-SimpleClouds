package dev.bettersimpleclouds.immersion;

import dev.bettersimpleclouds.core.ModIds;
import dev.bettersimpleclouds.core.RequiredModsMixinPlugin;

/**
 * Gates the Simple Clouds in-cloud immersion mixins on {@code simpleclouds} being present, so the hub still loads
 * cleanly when Simple Clouds isn't installed (its mixins reference Simple Clouds' own classes).
 */
public final class SimpleCloudsImmersionMixinPlugin extends RequiredModsMixinPlugin {

    public SimpleCloudsImmersionMixinPlugin() {
        super("Simple Clouds in-cloud immersion", ModIds.SIMPLE_CLOUDS);
    }
}
