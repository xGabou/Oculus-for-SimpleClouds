/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.Level
 */
package dev.nonamecrackers2.simpleclouds.common.world;

import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import net.minecraft.world.level.Level;

public interface CloudManagerHolder<T extends Level> {
    public CloudManager<T> getCloudManager();
}

