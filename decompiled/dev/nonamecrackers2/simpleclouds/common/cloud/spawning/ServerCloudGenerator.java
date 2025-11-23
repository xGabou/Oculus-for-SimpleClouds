/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package dev.nonamecrackers2.simpleclouds.common.cloud.spawning;

import com.google.common.collect.Lists;
import dev.nonamecrackers2.simpleclouds.common.cloud.SimpleCloudsConstants;
import dev.nonamecrackers2.simpleclouds.common.cloud.region.CloudGetter;
import dev.nonamecrackers2.simpleclouds.common.cloud.region.CloudRegion;
import dev.nonamecrackers2.simpleclouds.common.cloud.spawning.CloudGenerator;
import dev.nonamecrackers2.simpleclouds.common.cloud.spawning.CloudSpawningConfig;
import dev.nonamecrackers2.simpleclouds.common.world.ServerCloudManager;
import dev.nonamecrackers2.simpleclouds.common.world.SpawnRegion;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerCloudGenerator
extends CloudGenerator {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int AUTO_SYNC_INTERVAL = 240;
    private int syncTimer = 240;
    private boolean requiresSync;

    public ServerCloudGenerator(CloudGetter getter, Supplier<CloudSpawningConfig> config) {
        super(getter, config);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        ListTag regions = new ListTag();
        for (CloudRegion region : this.getClouds()) {
            regions.add((Object)region.toTag());
        }
        tag.m_128365_("regions", (Tag)regions);
        tag.m_128405_("ticks_till_next_gen", this.ticksTillNextGen);
        return tag;
    }

    public void readTag(CompoundTag tag) {
        ListTag regionsTag = tag.m_128437_("regions", 10);
        ArrayList regions = Lists.newArrayList();
        for (int i = 0; i < regionsTag.size(); ++i) {
            CompoundTag regionTag = regionsTag.m_128728_(i);
            try {
                regions.add(new CloudRegion(regionTag));
                continue;
            }
            catch (IllegalArgumentException e) {
                LOGGER.error("Failed to read cloud region: ", (Throwable)e);
            }
        }
        this.setClouds(regions);
        this.ticksTillNextGen = tag.m_128451_("ticks_till_next_gen");
    }

    public boolean checkAndResetSync() {
        boolean flag = this.requiresSync;
        this.requiresSync = false;
        return flag;
    }

    @Override
    public boolean addCloud(CloudRegion region, CloudGenerator.Order order) {
        if (!super.addCloud(region, order)) {
            return false;
        }
        this.requiresSync = true;
        return true;
    }

    @Override
    public boolean removeClouds(Predicate<CloudRegion> predicate) {
        if (!super.removeClouds(predicate)) {
            return false;
        }
        this.requiresSync = true;
        return true;
    }

    @Override
    public void tick(Level level, float speed) {
        super.tick(level, speed);
        if (this.syncTimer > 0) {
            --this.syncTimer;
            if (this.syncTimer == 0) {
                this.requiresSync = true;
                this.syncTimer = 240;
            }
        }
    }

    @Override
    protected void onRegionVisibilityChange(CloudRegion region, boolean nowVisible) {
        this.requiresSync = true;
    }

    @Override
    protected List<SpawnRegion> determineValidSpawnRegions(RandomSource random, Level level) {
        return ServerCloudManager.regionsFromEntities(level.m_6907_(), SimpleCloudsConstants.SPAWN_RADIUS);
    }
}

