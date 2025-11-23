/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue$Consumer
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.util.random.Weight
 *  net.minecraft.util.valueproviders.BiasedToBottomInt
 *  net.minecraft.util.valueproviders.ConstantFloat
 *  net.minecraft.util.valueproviders.FloatProvider
 *  net.minecraft.util.valueproviders.IntProvider
 *  net.minecraft.util.valueproviders.UniformFloat
 *  net.minecraft.util.valueproviders.UniformInt
 */
package dev.nonamecrackers2.simpleclouds.common.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.nonamecrackers2.simpleclouds.SimpleCloudsMod;
import dev.nonamecrackers2.simpleclouds.common.cloud.spawning.CloudSpawningConfig;
import dev.nonamecrackers2.simpleclouds.common.data.CloudSpawningConfigProvider;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.util.random.Weight;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.util.valueproviders.UniformInt;

public class SimpleCloudsCloudSpawningConfigProvider
extends CloudSpawningConfigProvider {
    private static final IntProvider SPAWN_INTERVAL = BiasedToBottomInt.m_146367_((int)2400, (int)12000);
    private static final int MAX_FORMATIONS = 5;
    private static final int MAX_INITIAL_FORMATIONS = 3;

    public SimpleCloudsCloudSpawningConfigProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void addEntries() {
        this.addEntry(new CloudSpawningConfig.Info(SimpleCloudsMod.id("cumulonimbus"), Weight.m_146282_((int)2), (FloatProvider)UniformFloat.m_146605_((float)0.03f, (float)0.07f), (IntProvider)UniformInt.m_146622_((int)6000, (int)10000), (IntProvider)UniformInt.m_146622_((int)48000, (int)72000), (IntProvider)UniformInt.m_146622_((int)1200, (int)2400), (FloatProvider)UniformFloat.m_146605_((float)0.3f, (float)0.6f), true, 1000));
        this.addEntry(new CloudSpawningConfig.Info(SimpleCloudsMod.id("nimbostratus"), Weight.m_146282_((int)3), (FloatProvider)UniformFloat.m_146605_((float)0.03f, (float)0.08f), (IntProvider)UniformInt.m_146622_((int)5000, (int)8000), (IntProvider)UniformInt.m_146622_((int)36000, (int)72000), (IntProvider)UniformInt.m_146622_((int)1200, (int)2400), (FloatProvider)UniformFloat.m_146605_((float)0.25f, (float)0.5f), true, 900));
        this.addEntry(new CloudSpawningConfig.Info(SimpleCloudsMod.id("stratus"), Weight.m_146282_((int)4), (FloatProvider)UniformFloat.m_146605_((float)0.03f, (float)0.1f), (IntProvider)BiasedToBottomInt.m_146367_((int)5000, (int)8000), (IntProvider)BiasedToBottomInt.m_146367_((int)48000, (int)72000), (IntProvider)UniformInt.m_146622_((int)1200, (int)2400), (FloatProvider)UniformFloat.m_146605_((float)0.25f, (float)0.45f), true, 800));
        this.addEntry(new CloudSpawningConfig.Info(SimpleCloudsMod.id("stratocumulus"), Weight.m_146282_((int)10), (FloatProvider)UniformFloat.m_146605_((float)0.1f, (float)0.2f), (IntProvider)UniformInt.m_146622_((int)5000, (int)10000), (IntProvider)BiasedToBottomInt.m_146367_((int)24000, (int)48000), (IntProvider)UniformInt.m_146622_((int)1200, (int)2400), (FloatProvider)ConstantFloat.m_146458_((float)1.0f), false, 700));
        this.addEntry(new CloudSpawningConfig.Info(SimpleCloudsMod.id("cumulus"), Weight.m_146282_((int)12), (FloatProvider)UniformFloat.m_146605_((float)0.1f, (float)0.2f), (IntProvider)UniformInt.m_146622_((int)6000, (int)10000), (IntProvider)BiasedToBottomInt.m_146367_((int)36000, (int)48000), (IntProvider)UniformInt.m_146622_((int)1200, (int)2400), (FloatProvider)ConstantFloat.m_146458_((float)1.0f), false, 600));
        this.addEntry(new CloudSpawningConfig.Info(SimpleCloudsMod.id("small_cumulus"), Weight.m_146282_((int)10), (FloatProvider)UniformFloat.m_146605_((float)0.1f, (float)0.2f), (IntProvider)UniformInt.m_146622_((int)4000, (int)10000), (IntProvider)BiasedToBottomInt.m_146367_((int)36000, (int)48000), (IntProvider)UniformInt.m_146622_((int)1200, (int)2400), (FloatProvider)ConstantFloat.m_146458_((float)1.0f), false, 500));
        this.addEntry(new CloudSpawningConfig.Info(SimpleCloudsMod.id("itty_bitty"), Weight.m_146282_((int)12), (FloatProvider)UniformFloat.m_146605_((float)0.1f, (float)0.2f), (IntProvider)UniformInt.m_146622_((int)4000, (int)10000), (IntProvider)BiasedToBottomInt.m_146367_((int)36000, (int)48000), (IntProvider)UniformInt.m_146622_((int)1200, (int)2400), (FloatProvider)ConstantFloat.m_146458_((float)1.0f), false, 400));
    }

    @Override
    public CompletableFuture<?> m_213708_(CachedOutput output) {
        JsonObject root = new JsonObject();
        root.add("spawn_interval", (JsonElement)IntProvider.f_146532_.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)SPAWN_INTERVAL).resultOrPartial(e -> {
            throw new IllegalArgumentException((String)e);
        }).get());
        root.addProperty("max_formations", (Number)5);
        root.addProperty("max_initial_formations", (Number)3);
        ArrayList futures = Lists.newArrayList();
        this.jsonForPaths(SimpleCloudsMod.id("config"), (MessagePassingQueue.Consumer<Path>)((MessagePassingQueue.Consumer)p -> futures.add(DataProvider.m_253162_((CachedOutput)output, (JsonElement)root, (Path)p))));
        futures.add(super.m_213708_(output));
        return CompletableFuture.allOf((CompletableFuture[])futures.toArray(CompletableFuture[]::new));
    }
}

