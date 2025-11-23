/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.spawning.SpawnInfo
 *  javax.annotation.Nullable
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.random.Weight
 *  net.minecraft.util.random.WeightedEntry
 *  net.minecraft.util.random.WeightedRandomList
 *  net.minecraft.util.valueproviders.ConstantFloat
 *  net.minecraft.util.valueproviders.ConstantInt
 *  net.minecraft.util.valueproviders.FloatProvider
 *  net.minecraft.util.valueproviders.IntProvider
 */
package dev.nonamecrackers2.simpleclouds.common.cloud.spawning;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.nonamecrackers2.simpleclouds.api.common.cloud.spawning.SpawnInfo;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudTypeSource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;

public class CloudSpawningConfig {
    public static final CloudSpawningConfig EMPTY = new CloudSpawningConfig((IntProvider)ConstantInt.f_146476_, 0, 0, (Map<ResourceLocation, Info>)ImmutableMap.of());
    private final IntProvider spawnInterval;
    private final int maxCloudRegions;
    private final int maxInitialRegions;
    private final Map<ResourceLocation, Info> weightsPerType;
    private final WeightedRandomList<Info> weights;

    private CloudSpawningConfig(IntProvider spawnInterval, int maxCloudRegions, int maxInitialRegions, Map<ResourceLocation, Info> weightsPerType) {
        this.spawnInterval = spawnInterval;
        this.maxCloudRegions = maxCloudRegions;
        this.maxInitialRegions = maxInitialRegions;
        this.weightsPerType = weightsPerType;
        this.weights = WeightedRandomList.m_146328_((List)Lists.newArrayList(weightsPerType.values()));
    }

    public static CloudSpawningConfig of(IntProvider spawnInterval, int maxCloudRegions, int maxInitialRegions, ImmutableMap<ResourceLocation, Info> weightsPerType) {
        if (weightsPerType.isEmpty()) {
            return EMPTY;
        }
        return new CloudSpawningConfig(spawnInterval, maxCloudRegions, maxInitialRegions, (Map<ResourceLocation, Info>)weightsPerType);
    }

    public static CloudSpawningConfig fromJson(CloudTypeSource typeValidator, JsonObject object, ImmutableMap<ResourceLocation, Info> entries) throws JsonSyntaxException, NullPointerException, IllegalArgumentException {
        if (entries.isEmpty()) {
            return EMPTY;
        }
        IntProvider spawnInterval = (IntProvider)IntProvider.f_146532_.parse((DynamicOps)JsonOps.INSTANCE, (Object)Objects.requireNonNull(object.get("spawn_interval"))).resultOrPartial(e -> {
            throw new JsonSyntaxException(e);
        }).get();
        int maxCloudRegions = GsonHelper.m_13927_((JsonObject)object, (String)"max_formations");
        int maxInitialRegions = GsonHelper.m_13927_((JsonObject)object, (String)"max_initial_formations");
        if (maxCloudRegions > 8 || maxInitialRegions > 8) {
            throw new IllegalArgumentException("Maximum cloud formations is 8");
        }
        return new CloudSpawningConfig(spawnInterval, maxCloudRegions, maxInitialRegions, (Map<ResourceLocation, Info>)entries);
    }

    public static Info readInfo(CloudTypeSource typeValidator, JsonObject object) throws JsonSyntaxException, NullPointerException, IllegalArgumentException {
        int orderWeight;
        String rawId = GsonHelper.m_13906_((JsonObject)object, (String)"type");
        ResourceLocation id = (ResourceLocation)ResourceLocation.m_135837_((String)rawId).resultOrPartial(e -> {
            throw new IllegalArgumentException((String)e);
        }).get();
        if (!typeValidator.doesCloudTypeExist(id)) {
            throw new IllegalArgumentException("Unknown cloud type with id '" + id + "'");
        }
        Weight weight = (Weight)Weight.f_146274_.parse((DynamicOps)JsonOps.INSTANCE, (Object)object.get("weight")).resultOrPartial(e -> {
            throw new JsonSyntaxException(e);
        }).get();
        FloatProvider speed = (FloatProvider)FloatProvider.m_146505_((float)0.0f, (float)10.0f).parse((DynamicOps)JsonOps.INSTANCE, (Object)object.get("speed")).resultOrPartial(e -> {
            throw new JsonSyntaxException(e);
        }).get();
        IntProvider radius = (IntProvider)IntProvider.f_146532_.parse((DynamicOps)JsonOps.INSTANCE, (Object)object.get("radius")).resultOrPartial(e -> {
            throw new JsonSyntaxException(e);
        }).get();
        IntProvider existTicks = (IntProvider)IntProvider.f_146532_.parse((DynamicOps)JsonOps.INSTANCE, (Object)object.get("exist_ticks")).resultOrPartial(e -> {
            throw new JsonSyntaxException(e);
        }).get();
        IntProvider growTicks = (IntProvider)IntProvider.m_146545_((int)0, (int)existTicks.m_142737_()).parse((DynamicOps)JsonOps.INSTANCE, (Object)object.get("grow_ticks")).resultOrPartial(e -> {
            throw new JsonSyntaxException(e);
        }).get();
        Object stretchFactor = object.has("stretch_factor") ? (FloatProvider)FloatProvider.m_146505_((float)0.01f, (float)Float.MAX_VALUE).parse((DynamicOps)JsonOps.INSTANCE, (Object)object.get("stretch_factor")).resultOrPartial(e -> {
            throw new JsonSyntaxException(e);
        }).orElse(ConstantFloat.m_146458_((float)1.0f)) : ConstantFloat.m_146458_((float)1.0f);
        boolean movesToPlayer = false;
        if (object.has("moves_to_player")) {
            movesToPlayer = GsonHelper.m_13912_((JsonObject)object, (String)"moves_to_player");
        }
        if ((orderWeight = GsonHelper.m_13927_((JsonObject)object, (String)"order_weight")) <= 0) {
            throw new IllegalArgumentException("Order weight must be >= 1");
        }
        return new Info(id, weight, speed, radius, existTicks, growTicks, (FloatProvider)stretchFactor, movesToPlayer, orderWeight);
    }

    public boolean isEmpty() {
        return this.weightsPerType.isEmpty();
    }

    public IntProvider getSpawnInterval() {
        return this.spawnInterval;
    }

    public int getMaxRegions() {
        return this.maxCloudRegions;
    }

    public int getMaxInitialRegions() {
        return this.maxInitialRegions;
    }

    @Nullable
    public Info getWeightInfo(ResourceLocation cloudType) {
        return this.weightsPerType.get(cloudType);
    }

    public Optional<Info> getRandom(RandomSource random) {
        return this.weights.m_216829_(random);
    }

    public record Info(ResourceLocation cloudType, Weight weight, FloatProvider speed, IntProvider radius, IntProvider existTicks, IntProvider growTicks, FloatProvider stretchFactor, boolean movesToPlayer, int orderWeight) implements WeightedEntry,
    SpawnInfo
    {
        public Weight m_142631_() {
            return this.weight;
        }

        public int determineExistTicks(RandomSource random) {
            return this.existTicks.m_214085_(random);
        }

        public int determineGrowTicks(RandomSource random) {
            return this.growTicks.m_214085_(random);
        }

        public int determineRadius(RandomSource random) {
            return this.radius.m_214085_(random);
        }

        public float determineSpeed(RandomSource random) {
            return this.speed.m_214084_(random);
        }

        public float determineStretchFactor(RandomSource random) {
            return this.stretchFactor.m_214084_(random);
        }

        public JsonObject toJson() throws IllegalArgumentException {
            JsonObject object = new JsonObject();
            object.addProperty("type", this.cloudType.toString());
            object.add("weight", (JsonElement)Weight.f_146274_.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)this.weight).resultOrPartial(e -> {
                throw new IllegalArgumentException((String)e);
            }).get());
            object.add("speed", (JsonElement)FloatProvider.m_146505_((float)0.0f, (float)10.0f).encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)this.speed).resultOrPartial(e -> {
                throw new IllegalArgumentException((String)e);
            }).get());
            object.add("radius", (JsonElement)IntProvider.f_146532_.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)this.radius).resultOrPartial(e -> {
                throw new IllegalArgumentException((String)e);
            }).get());
            object.add("exist_ticks", (JsonElement)IntProvider.f_146532_.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)this.existTicks).resultOrPartial(e -> {
                throw new IllegalArgumentException((String)e);
            }).get());
            object.add("grow_ticks", (JsonElement)IntProvider.m_146545_((int)0, (int)this.existTicks.m_142737_()).encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)this.radius).resultOrPartial(e -> {
                throw new IllegalArgumentException((String)e);
            }).get());
            object.add("stretch_factor", (JsonElement)FloatProvider.m_146505_((float)0.01f, (float)Float.MAX_VALUE).encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)this.stretchFactor).resultOrPartial(e -> {
                throw new IllegalArgumentException((String)e);
            }).get());
            object.addProperty("moves_to_player", Boolean.valueOf(this.movesToPlayer));
            if (this.orderWeight <= 0) {
                throw new IllegalArgumentException("Order weight must be >= 1");
            }
            object.addProperty("order_weight", (Number)this.orderWeight);
            return object;
        }
    }
}

