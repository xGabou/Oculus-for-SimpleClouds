/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package dev.nonamecrackers2.simpleclouds.common.noise;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.nonamecrackers2.simpleclouds.common.noise.AbstractNoiseSettings;
import java.util.Map;
import java.util.Objects;

public class StaticNoiseSettings
extends AbstractNoiseSettings<StaticNoiseSettings> {
    public static final Codec<StaticNoiseSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.FLOAT.fieldOf("height").forGetter(i -> Float.valueOf(i.getParam(AbstractNoiseSettings.Param.HEIGHT))), (App)Codec.FLOAT.fieldOf("value_offset").forGetter(i -> Float.valueOf(i.getParam(AbstractNoiseSettings.Param.VALUE_OFFSET))), (App)Codec.FLOAT.fieldOf("scale_x").forGetter(i -> Float.valueOf(i.getParam(AbstractNoiseSettings.Param.SCALE_X))), (App)Codec.FLOAT.fieldOf("scale_y").forGetter(i -> Float.valueOf(i.getParam(AbstractNoiseSettings.Param.SCALE_Y))), (App)Codec.FLOAT.fieldOf("scale_z").forGetter(i -> Float.valueOf(i.getParam(AbstractNoiseSettings.Param.SCALE_Z))), (App)Codec.FLOAT.fieldOf("fade_distance").forGetter(i -> Float.valueOf(i.getParam(AbstractNoiseSettings.Param.FADE_DISTANCE))), (App)Codec.FLOAT.fieldOf("height_offset").forGetter(i -> Float.valueOf(i.getParam(AbstractNoiseSettings.Param.HEIGHT_OFFSET))), (App)Codec.FLOAT.fieldOf("value_scale").forGetter(i -> Float.valueOf(i.getParam(AbstractNoiseSettings.Param.VALUE_SCALE)))).apply((Applicative)instance, (height, valueOffset, scaleX, scaleY, scaleZ, fadeDistance, heightOffset, valueScale) -> {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put((Object)AbstractNoiseSettings.Param.HEIGHT, height);
        builder.put((Object)AbstractNoiseSettings.Param.VALUE_OFFSET, valueOffset);
        builder.put((Object)AbstractNoiseSettings.Param.SCALE_X, scaleX);
        builder.put((Object)AbstractNoiseSettings.Param.SCALE_Y, scaleY);
        builder.put((Object)AbstractNoiseSettings.Param.SCALE_Z, scaleZ);
        builder.put((Object)AbstractNoiseSettings.Param.FADE_DISTANCE, fadeDistance);
        builder.put((Object)AbstractNoiseSettings.Param.HEIGHT_OFFSET, heightOffset);
        builder.put((Object)AbstractNoiseSettings.Param.VALUE_SCALE, valueScale);
        return new StaticNoiseSettings((ImmutableMap<AbstractNoiseSettings.Param, Float>)builder.build());
    }));
    public static final StaticNoiseSettings DEFAULT = new StaticNoiseSettings();
    private final Map<AbstractNoiseSettings.Param, Float> values;

    public StaticNoiseSettings(AbstractNoiseSettings<?> settings) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (AbstractNoiseSettings.Param param : AbstractNoiseSettings.Param.values()) {
            builder.put((Object)param, (Object)Float.valueOf(settings.getParam(param)));
        }
        this.values = builder.build();
        this.packParameters();
    }

    public StaticNoiseSettings(ImmutableMap<AbstractNoiseSettings.Param, Float> values) {
        this.values = values;
        this.packParameters();
    }

    private StaticNoiseSettings() {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (AbstractNoiseSettings.Param param : AbstractNoiseSettings.Param.values()) {
            builder.put((Object)param, (Object)Float.valueOf(param.getDefaultValue()));
        }
        this.values = builder.build();
        this.packParameters();
    }

    @Override
    public float getParam(AbstractNoiseSettings.Param param) {
        return Objects.requireNonNull(this.values.get((Object)param), "Value is missing for param '" + param + "'").floatValue();
    }

    @Override
    protected boolean setParamRaw(AbstractNoiseSettings.Param param, float values) {
        return false;
    }

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops, T prefix) {
        return CODEC.encode((Object)this, ops, prefix);
    }
}

