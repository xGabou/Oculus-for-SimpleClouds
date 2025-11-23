/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Streams
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.util.profiling.ProfilerFiller
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package dev.nonamecrackers2.simpleclouds.common.cloud;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudTypeSource;
import dev.nonamecrackers2.simpleclouds.common.cloud.SimpleCloudsConstants;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CloudTypeDataManager
extends SimpleJsonResourceReloadListener
implements CloudTypeSource {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final CloudTypeDataManager SERVER = new CloudTypeDataManager();
    private Map<ResourceLocation, CloudType> cloudTypes = ImmutableMap.of((Object)SimpleCloudsConstants.EMPTY.id(), (Object)SimpleCloudsConstants.EMPTY);
    private CloudType[] indexedCloudTypes = new CloudType[]{SimpleCloudsConstants.EMPTY};

    public CloudTypeDataManager() {
        super(GSON, "cloud_types");
    }

    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager manager, ProfilerFiller filler) {
        HashMap types = Maps.newHashMap();
        for (Map.Entry<ResourceLocation, JsonElement> entry : files.entrySet()) {
            ResourceLocation id = entry.getKey();
            JsonElement element = entry.getValue();
            try {
                JsonObject object = GsonHelper.m_13918_((JsonElement)element, (String)"root");
                types.put(id, CloudType.readFromJson(id, object));
            }
            catch (JsonSyntaxException e) {
                LOGGER.warn("Failed to load cloud type '" + id + "'", (Throwable)e);
            }
        }
        this.indexedCloudTypes = (CloudType[])Streams.concat((Stream[])new Stream[]{Stream.of(SimpleCloudsConstants.EMPTY), types.values().stream().sorted(Comparator.comparing(t -> t.id().toString()))}).toArray(CloudType[]::new);
        types.put(SimpleCloudsConstants.EMPTY.id(), SimpleCloudsConstants.EMPTY);
        this.cloudTypes = ImmutableMap.copyOf((Map)types);
        LOGGER.info("Loaded {} cloud types", (Object)this.cloudTypes.size());
    }

    @Override
    public CloudType getCloudTypeForId(ResourceLocation id) {
        return this.getCloudTypes().get(id);
    }

    public Map<ResourceLocation, CloudType> getCloudTypes() {
        return this.cloudTypes;
    }

    @Override
    public CloudType[] getIndexedCloudTypes() {
        return this.indexedCloudTypes;
    }

    public static CloudTypeDataManager getServerInstance() {
        return SERVER;
    }
}

