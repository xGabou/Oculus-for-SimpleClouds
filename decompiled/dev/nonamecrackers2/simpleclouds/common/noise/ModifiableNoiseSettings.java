/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 */
package dev.nonamecrackers2.simpleclouds.common.noise;

import com.google.common.collect.Maps;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.nonamecrackers2.simpleclouds.common.noise.AbstractNoiseSettings;
import dev.nonamecrackers2.simpleclouds.common.noise.StaticNoiseSettings;
import java.util.Map;
import java.util.Objects;

public class ModifiableNoiseSettings
extends AbstractNoiseSettings<ModifiableNoiseSettings> {
    private final Map<AbstractNoiseSettings.Param, Float> values = Maps.newEnumMap(AbstractNoiseSettings.Param.class);

    public ModifiableNoiseSettings() {
        for (AbstractNoiseSettings.Param param : AbstractNoiseSettings.Param.values()) {
            this.values.put(param, Float.valueOf(param.getDefaultValue()));
        }
    }

    public ModifiableNoiseSettings(AbstractNoiseSettings<?> settings) {
        for (AbstractNoiseSettings.Param param : AbstractNoiseSettings.Param.values()) {
            this.values.put(param, Float.valueOf(settings.getParam(param)));
        }
    }

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops, T prefix) {
        return StaticNoiseSettings.CODEC.encode((Object)this.toStatic(), ops, prefix);
    }

    @Override
    public float getParam(AbstractNoiseSettings.Param param) {
        return Objects.requireNonNull(this.values.get((Object)param), "Value is missing for param '" + param + "'").floatValue();
    }

    @Override
    protected boolean setParamRaw(AbstractNoiseSettings.Param param, float value) {
        float previous = this.values.get((Object)param).floatValue();
        if (previous != value) {
            this.values.put(param, Float.valueOf(value));
            return true;
        }
        return false;
    }

    public StaticNoiseSettings toStatic() {
        return new StaticNoiseSettings(this);
    }
}

