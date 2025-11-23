/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.Level
 */
package dev.nonamecrackers2.simpleclouds.client.cloud.region;

import com.google.common.collect.Lists;
import dev.nonamecrackers2.simpleclouds.client.world.ClientCloudManager;
import dev.nonamecrackers2.simpleclouds.common.cloud.SimpleCloudsConstants;
import dev.nonamecrackers2.simpleclouds.common.cloud.region.CloudGetter;
import dev.nonamecrackers2.simpleclouds.common.cloud.spawning.CloudGenerator;
import dev.nonamecrackers2.simpleclouds.common.cloud.spawning.CloudSpawningConfig;
import dev.nonamecrackers2.simpleclouds.common.world.SpawnRegion;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class ClientCloudGenerator
extends CloudGenerator {
    public ClientCloudGenerator(CloudGetter cloudGetter, Supplier<CloudSpawningConfig> config) {
        super(cloudGetter, config);
    }

    @Override
    protected boolean shouldGenerateCloud(CloudSpawningConfig config, RandomSource random, Level level) {
        return !ClientCloudManager.isAvailableServerSide() && super.shouldGenerateCloud(config, random, level);
    }

    @Override
    protected List<SpawnRegion> determineValidSpawnRegions(RandomSource random, Level level) {
        LocalPlayer player = Minecraft.m_91087_().f_91074_;
        if (player != null) {
            return Lists.newArrayList((Object[])new SpawnRegion[]{new SpawnRegion(player.m_146903_(), player.m_146907_(), SimpleCloudsConstants.SPAWN_RADIUS)});
        }
        return Lists.newArrayList();
    }
}

