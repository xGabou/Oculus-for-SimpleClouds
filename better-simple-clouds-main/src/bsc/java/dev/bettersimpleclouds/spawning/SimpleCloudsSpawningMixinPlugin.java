package dev.bettersimpleclouds.spawning;

import dev.bettersimpleclouds.core.ModIds;
import dev.bettersimpleclouds.core.RequiredModsMixinPlugin;

/**
 * Gates the Simple Clouds spawning mixins on {@code simpleclouds} being present, so the mod still loads cleanly when
 * Simple Clouds isn't installed (the mixins reference Simple Clouds' own spawning classes).
 *
 * <p>Unlike the in-cloud immersion patch, this one is <b>common</b> (not client-only): cloud spawning runs on whichever
 * side generates clouds - the integrated/dedicated server in normal play, or the client when it falls back to local
 * generation - so the mixin and its config must apply on both sides for blacklisted clouds to truly never spawn.</p>
 */
public final class SimpleCloudsSpawningMixinPlugin extends RequiredModsMixinPlugin {

    public SimpleCloudsSpawningMixinPlugin() {
        super("Simple Clouds spawning (cloud-type blacklist)", ModIds.SIMPLE_CLOUDS);
    }
}
