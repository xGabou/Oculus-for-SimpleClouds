package dev.bettersimpleclouds.performance;

import dev.bettersimpleclouds.core.ModIds;
import dev.bettersimpleclouds.core.RequiredModsMixinPlugin;

/**
 * Gates the Simple Clouds performance mixins on {@code simpleclouds} being present, so the hub still loads cleanly
 * when Simple Clouds isn't installed (the mixin references Simple Clouds' own renderer-settings class).
 */
public final class SimpleCloudsPerformanceMixinPlugin extends RequiredModsMixinPlugin {

    public SimpleCloudsPerformanceMixinPlugin() {
        super("Simple Clouds performance", ModIds.SIMPLE_CLOUDS);
    }
}
