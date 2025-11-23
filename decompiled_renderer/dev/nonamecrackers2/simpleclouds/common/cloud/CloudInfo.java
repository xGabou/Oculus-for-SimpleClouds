/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.weather.WeatherType
 *  net.minecraft.util.Mth
 */
package dev.nonamecrackers2.simpleclouds.common.cloud;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.nonamecrackers2.simpleclouds.api.common.cloud.weather.WeatherType;
import dev.nonamecrackers2.simpleclouds.common.noise.NoiseSettings;
import java.nio.ByteBuffer;
import net.minecraft.util.Mth;

public interface CloudInfo {
    public static final int BYTES_PER_TYPE = 24;
    public static final float STORMINESS_MAX = 1.0f;
    public static final float STORM_START_MAX = 256.0f;
    public static final float STORM_FADE_DISTANCE_MAX = 1600.0f;
    public static final float TRANSPARENCY_FADE_MAX = 32.0f;

    public NoiseSettings noiseConfig();

    public WeatherType weatherType();

    public float storminess();

    public float stormStart();

    public float stormFadeDistance();

    public float transparencyFade();

    default public JsonObject toJson() throws JsonSyntaxException {
        JsonObject object = new JsonObject();
        object.add("noise_settings", (JsonElement)NoiseSettings.CODEC.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)this.noiseConfig()).resultOrPartial(error -> {
            throw new JsonSyntaxException(error);
        }).orElseThrow());
        object.addProperty("weather_type", this.weatherType().m_7912_());
        object.addProperty("storminess", (Number)Float.valueOf(Mth.m_14036_((float)this.storminess(), (float)0.0f, (float)1.0f)));
        object.addProperty("storm_start", (Number)Float.valueOf(Mth.m_14036_((float)this.stormStart(), (float)0.0f, (float)256.0f)));
        object.addProperty("storm_fade_distance", (Number)Float.valueOf(Mth.m_14036_((float)this.stormFadeDistance(), (float)0.0f, (float)1600.0f)));
        object.addProperty("transparency_fade", (Number)Float.valueOf(Mth.m_14036_((float)this.transparencyFade(), (float)0.0f, (float)32.0f)));
        return object;
    }

    default public int packToBuffer(ByteBuffer b, int layerIndex) {
        int layerCount = this.noiseConfig().layerCount();
        b.putInt(layerIndex);
        b.putInt(layerIndex + layerCount);
        b.putFloat(this.storminess());
        b.putFloat(this.stormStart());
        b.putFloat(this.stormFadeDistance());
        b.putFloat(this.transparencyFade());
        return layerIndex + layerCount;
    }
}

