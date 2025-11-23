/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 */
package dev.nonamecrackers2.simpleclouds.common.noise;

import com.google.common.collect.Lists;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.nonamecrackers2.simpleclouds.common.noise.AbstractLayeredNoise;
import dev.nonamecrackers2.simpleclouds.common.noise.ModifiableNoiseSettings;
import dev.nonamecrackers2.simpleclouds.common.noise.StaticLayeredNoise;
import java.util.Collection;

public class ModifiableLayeredNoise
extends AbstractLayeredNoise<ModifiableNoiseSettings> {
    public ModifiableLayeredNoise() {
        super(Lists.newArrayList());
    }

    public ModifiableLayeredNoise(Collection<ModifiableNoiseSettings> layers) {
        super(Lists.newArrayList(layers));
    }

    public ModifiableLayeredNoise addNoiseLayer(ModifiableNoiseSettings layer) {
        if (!this.noiseLayers.contains(layer)) {
            this.noiseLayers.add(layer);
            this.recalculateHeights();
        }
        return this;
    }

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops, T prefix) {
        return StaticLayeredNoise.CODEC.encode((Object)this.toStatic(), ops, prefix);
    }

    public boolean removeNoiseLayer(ModifiableNoiseSettings layer) {
        boolean flag = this.noiseLayers.remove(layer);
        if (flag) {
            this.recalculateHeights();
        }
        return flag;
    }

    public StaticLayeredNoise toStatic() {
        return new StaticLayeredNoise(this);
    }
}

