/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Decoder
 *  com.mojang.serialization.DynamicOps
 */
package dev.nonamecrackers2.simpleclouds.common.noise;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import dev.nonamecrackers2.simpleclouds.common.noise.StaticLayeredNoise;
import dev.nonamecrackers2.simpleclouds.common.noise.StaticNoiseSettings;
import java.util.List;

public interface NoiseSettings {
    public static final List<Decoder> VALID_DECODERS = ImmutableList.of(StaticNoiseSettings.CODEC, StaticLayeredNoise.CODEC);
    public static final Codec<NoiseSettings> CODEC = new Codec<NoiseSettings>(){

        public <T> DataResult<Pair<NoiseSettings, T>> decode(DynamicOps<T> ops, T input) {
            for (Decoder decoder : VALID_DECODERS) {
                DataResult result = decoder.decode(ops, input);
                if (!result.result().isPresent()) continue;
                return result;
            }
            return DataResult.error(() -> "Could not decode noise settings; unknown type");
        }

        public <T> DataResult<T> encode(NoiseSettings input, DynamicOps<T> ops, T prefix) {
            return input.encode(ops, prefix);
        }
    };
    public static final NoiseSettings EMPTY = new NoiseSettings(){

        @Override
        public float[] packForShader() {
            return new float[0];
        }

        @Override
        public int layerCount() {
            return 0;
        }

        @Override
        public <T> DataResult<T> encode(DynamicOps<T> ops, T prefix) {
            return DataResult.success((Object)ops.emptyList());
        }

        @Override
        public int getStartHeight() {
            return 0;
        }

        @Override
        public int getEndHeight() {
            return 0;
        }
    };

    public <T> DataResult<T> encode(DynamicOps<T> var1, T var2);

    public float[] packForShader();

    public int layerCount();

    public int getStartHeight();

    public int getEndHeight();
}

