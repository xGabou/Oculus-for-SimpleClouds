/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue$Consumer
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.resources.ResourceLocation
 */
package dev.nonamecrackers2.simpleclouds.common.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudInfo;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.data.DualPathProvider;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public abstract class CloudTypeProvider
extends DualPathProvider {
    private final String modid;
    private final Map<ResourceLocation, CloudInfo> cloudTypes = Maps.newHashMap();

    public CloudTypeProvider(String modid, PackOutput output) {
        super(output, "cloud_types");
        this.modid = modid;
    }

    protected abstract void addTypes();

    protected void addType(String id, CloudInfo info) {
        this.cloudTypes.put(new ResourceLocation(this.modid, id), info);
    }

    protected void addType(CloudType type) {
        this.addType(type.id().m_135815_(), type);
    }

    public CompletableFuture<?> m_213708_(CachedOutput output) {
        this.addTypes();
        HashSet ids = Sets.newHashSet();
        ArrayList futures = Lists.newArrayList();
        this.cloudTypes.forEach((id, info) -> {
            if (!ids.add(id)) {
                throw new IllegalArgumentException("Duplicate cloud type " + id);
            }
            JsonObject object = info.toJson();
            this.jsonForPaths((ResourceLocation)id, (MessagePassingQueue.Consumer<Path>)((MessagePassingQueue.Consumer)p -> futures.add(DataProvider.m_253162_((CachedOutput)output, (JsonElement)object, (Path)p))));
        });
        return CompletableFuture.allOf((CompletableFuture[])futures.toArray(CompletableFuture[]::new));
    }

    public String m_6055_() {
        return "Cloud Types";
    }
}

