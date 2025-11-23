/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.ScAPICloudType
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.weather.WeatherType
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.GsonHelper
 */
package dev.nonamecrackers2.simpleclouds.common.cloud;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.nonamecrackers2.simpleclouds.api.common.cloud.ScAPICloudType;
import dev.nonamecrackers2.simpleclouds.api.common.cloud.weather.WeatherType;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudInfo;
import dev.nonamecrackers2.simpleclouds.common.noise.NoiseSettings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public record CloudType(ResourceLocation id, WeatherType weatherType, float storminess, float stormStart, float stormFadeDistance, float transparencyFade, NoiseSettings noiseConfig) implements CloudInfo,
ScAPICloudType
{
    private static float getOptionalRangedParam(JsonObject object, String name, float defaultValue, float min, float max) throws JsonSyntaxException {
        float value = GsonHelper.m_13820_((JsonObject)object, (String)name, (float)defaultValue);
        if (value < min || value > max) {
            throw new JsonSyntaxException("'" + name + "' is out of bounds. MIN: " + min + ", MAX: " + max);
        }
        return value;
    }

    public static CloudType readFromJson(ResourceLocation id, JsonObject object) throws JsonSyntaxException {
        JsonElement element;
        JsonElement jsonElement = element = object.has("noise_settings") ? object.get("noise_settings") : object.get("noise_layers");
        if (element == null) {
            throw new JsonSyntaxException("Please include one of 'noise_settings' or 'noise_layers'");
        }
        NoiseSettings settings = (NoiseSettings)NoiseSettings.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)element).resultOrPartial(error -> {
            throw new JsonSyntaxException(error);
        }).orElseThrow();
        if (settings.layerCount() > 4) {
            throw new JsonSyntaxException("Too many noise layers. Maximum amount of layers allowed is 4");
        }
        WeatherType weatherType = WeatherType.NONE;
        if (object.has("weather_type")) {
            String rawWeatherTypeId = GsonHelper.m_13906_((JsonObject)object, (String)"weather_type");
            boolean match = false;
            for (WeatherType type : WeatherType.values()) {
                if (!type.m_7912_().equals(rawWeatherTypeId)) continue;
                weatherType = type;
                match = true;
                break;
            }
            if (!match) {
                throw new JsonSyntaxException("Unknown weather type '" + rawWeatherTypeId + "'");
            }
        }
        float storminess = CloudType.getOptionalRangedParam(object, "storminess", 0.0f, 0.0f, 1.0f);
        float stormStart = CloudType.getOptionalRangedParam(object, "storm_start", 16.0f, 0.0f, 256.0f);
        float stormFadeDistance = CloudType.getOptionalRangedParam(object, "storm_fade_distance", 32.0f, 0.0f, 1600.0f);
        float transparencyFade = CloudType.getOptionalRangedParam(object, "transparency_fade", 0.0f, 0.0f, 32.0f);
        return new CloudType(id, weatherType, storminess, stormStart, stormFadeDistance, transparencyFade, settings);
    }
}

