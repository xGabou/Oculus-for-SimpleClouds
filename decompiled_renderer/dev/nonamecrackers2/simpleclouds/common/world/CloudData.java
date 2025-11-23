/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.level.saveddata.SavedData
 */
package dev.nonamecrackers2.simpleclouds.common.world;

import dev.nonamecrackers2.simpleclouds.common.world.ServerCloudManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;

public class CloudData
extends SavedData {
    public static final String ID = "clouddata";
    private final ServerCloudManager manager;

    public CloudData(ServerCloudManager manager) {
        this.manager = manager;
    }

    public static CloudData load(ServerCloudManager manager, CompoundTag tag) {
        CloudData data = new CloudData(manager);
        if (tag.m_128441_("Seed")) {
            manager.setSeed(tag.m_128454_("Seed"));
        }
        if (tag.m_128441_("ScrollAngle")) {
            manager.setScrollAngle(tag.m_128457_("ScrollAngle"));
        }
        if (tag.m_128441_("Speed")) {
            manager.setCloudSpeed(tag.m_128457_("Speed"));
        }
        if (tag.m_128441_("Height")) {
            manager.setCloudHeight(tag.m_128451_("Height"));
        }
        manager.getCloudGenerator().readTag(tag.m_128469_("cloud_generator"));
        return data;
    }

    public CompoundTag m_7176_(CompoundTag tag) {
        tag.m_128356_("Seed", this.manager.getSeed());
        tag.m_128350_("ScrollAngle", this.manager.getScrollAngle());
        tag.m_128350_("Speed", this.manager.getCloudSpeed());
        tag.m_128405_("Height", this.manager.getCloudHeight());
        tag.m_128365_("cloud_generator", (Tag)this.manager.getCloudGenerator().toTag());
        return tag;
    }

    public boolean m_77764_() {
        return true;
    }
}

