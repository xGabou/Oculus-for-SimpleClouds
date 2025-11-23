/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonSyntaxException
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.util.profiling.ProfilerFiller
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package dev.nonamecrackers2.simpleclouds.common.cloud.spawning;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import dev.nonamecrackers2.simpleclouds.SimpleCloudsMod;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudTypeSource;
import dev.nonamecrackers2.simpleclouds.common.cloud.spawning.CloudSpawningConfig;
import java.util.Map;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CloudSpawningDataManager
extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static CloudSpawningDataManager instance;
    private CloudTypeSource source;
    private CloudSpawningConfig config;

    public CloudSpawningDataManager(CloudTypeSource source) {
        super(GSON, "cloud_spawning");
        this.source = source;
        this.config = CloudSpawningConfig.EMPTY;
    }

    public CloudSpawningConfig getConfig() {
        return this.config;
    }

    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller filler) {
        JsonElement root = resources.get(SimpleCloudsMod.id("config"));
        if (root == null) {
            LOGGER.error("Could not find root Simple Clouds config");
            this.config = CloudSpawningConfig.EMPTY;
            return;
        }
        ImmutableMap.Builder entries = ImmutableMap.builder();
        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            if (entry.getValue() == root) continue;
            try {
                CloudSpawningConfig.Info info = CloudSpawningConfig.readInfo(this.source, GsonHelper.m_13918_((JsonElement)entry.getValue(), (String)"root"));
                entries.put((Object)info.cloudType(), (Object)info);
            }
            catch (JsonSyntaxException | IllegalArgumentException | NullPointerException e) {
                LOGGER.error("Failed to parse spawn info for file '" + entry.getKey() + "'", e);
                this.config = CloudSpawningConfig.EMPTY;
            }
        }
        try {
            this.config = CloudSpawningConfig.fromJson(this.source, GsonHelper.m_13918_((JsonElement)root, (String)"root"), (ImmutableMap<ResourceLocation, CloudSpawningConfig.Info>)entries.build());
        }
        catch (JsonSyntaxException | IllegalArgumentException | NullPointerException e) {
            LOGGER.error("Failed to parse cloud spawn config", e);
            this.config = CloudSpawningConfig.EMPTY;
        }
    }

    public static void optionalInitialize(CloudTypeSource cloudTypeSource) {
        if (instance == null) {
            instance = new CloudSpawningDataManager(cloudTypeSource);
        }
    }

    public static CloudSpawningDataManager getInstance() {
        return Objects.requireNonNull(instance, "Not initialized");
    }
}

