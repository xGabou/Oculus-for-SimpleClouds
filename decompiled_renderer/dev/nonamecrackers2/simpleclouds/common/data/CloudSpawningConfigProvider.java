/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue$Consumer
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 */
package dev.nonamecrackers2.simpleclouds.common.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.nonamecrackers2.simpleclouds.common.cloud.spawning.CloudSpawningConfig;
import dev.nonamecrackers2.simpleclouds.common.data.DualPathProvider;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

public abstract class CloudSpawningConfigProvider
extends DualPathProvider {
    private final List<CloudSpawningConfig.Info> entries = Lists.newArrayList();

    public CloudSpawningConfigProvider(PackOutput output) {
        super(output, "cloud_spawning");
    }

    protected abstract void addEntries();

    protected void addEntry(CloudSpawningConfig.Info entry) {
        this.entries.add(entry);
    }

    public CompletableFuture<?> m_213708_(CachedOutput output) {
        this.addEntries();
        HashSet ids = Sets.newHashSet();
        ArrayList futures = Lists.newArrayList();
        this.entries.forEach(entry -> {
            if (!ids.add(entry.cloudType())) {
                throw new IllegalArgumentException("Duplicate cloud spawning config entry " + entry.cloudType());
            }
            JsonObject object = entry.toJson();
            this.jsonForPaths(entry.cloudType(), (MessagePassingQueue.Consumer<Path>)((MessagePassingQueue.Consumer)p -> futures.add(DataProvider.m_253162_((CachedOutput)output, (JsonElement)object, (Path)p))));
        });
        return CompletableFuture.allOf((CompletableFuture[])futures.toArray(CompletableFuture[]::new));
    }

    public String m_6055_() {
        return "Cloud Spawning Config";
    }
}

