/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 */
package dev.nonamecrackers2.simpleclouds.common.noise;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.nonamecrackers2.simpleclouds.common.noise.AbstractLayeredNoise;
import dev.nonamecrackers2.simpleclouds.common.noise.ModifiableLayeredNoise;
import dev.nonamecrackers2.simpleclouds.common.noise.ModifiableNoiseSettings;
import dev.nonamecrackers2.simpleclouds.common.noise.StaticNoiseSettings;
import java.util.Collection;
import java.util.List;

public class StaticLayeredNoise
extends AbstractLayeredNoise<StaticNoiseSettings> {
    public static final Codec<StaticLayeredNoise> CODEC = Codec.list(StaticNoiseSettings.CODEC).xmap(list -> {
        ImmutableList.Builder builder = ImmutableList.builder();
        for (StaticNoiseSettings settings : list) {
            builder.add((Object)settings);
        }
        return new StaticLayeredNoise((ImmutableList<StaticNoiseSettings>)builder.build());
    }, AbstractLayeredNoise::getNoiseLayers);

    public StaticLayeredNoise(Collection<StaticNoiseSettings> noiseLayers) {
        super(ImmutableList.copyOf(noiseLayers));
    }

    private StaticLayeredNoise(ImmutableList<StaticNoiseSettings> noiseLayers) {
        super(noiseLayers);
    }

    public StaticLayeredNoise(ModifiableLayeredNoise modifiableLayeredNoise) {
        super((List)modifiableLayeredNoise.noiseLayers.stream().map(ModifiableNoiseSettings::toStatic).collect(ImmutableList.toImmutableList()));
    }

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops, T prefix) {
        return CODEC.encode((Object)this, ops, prefix);
    }
}

