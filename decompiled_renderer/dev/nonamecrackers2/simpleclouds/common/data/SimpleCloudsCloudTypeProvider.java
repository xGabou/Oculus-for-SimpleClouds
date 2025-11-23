/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.weather.WeatherType
 *  net.minecraft.data.PackOutput
 */
package dev.nonamecrackers2.simpleclouds.common.data;

import dev.nonamecrackers2.simpleclouds.SimpleCloudsMod;
import dev.nonamecrackers2.simpleclouds.api.common.cloud.weather.WeatherType;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.data.CloudTypeProvider;
import dev.nonamecrackers2.simpleclouds.common.noise.AbstractNoiseSettings;
import dev.nonamecrackers2.simpleclouds.common.noise.ModifiableLayeredNoise;
import dev.nonamecrackers2.simpleclouds.common.noise.ModifiableNoiseSettings;
import net.minecraft.data.PackOutput;

public class SimpleCloudsCloudTypeProvider
extends CloudTypeProvider {
    public SimpleCloudsCloudTypeProvider(PackOutput output) {
        super("simpleclouds", output);
    }

    @Override
    protected void addTypes() {
        this.addType(SimpleCloudsCloudTypeProvider.cumulonimbus());
        this.addType(SimpleCloudsCloudTypeProvider.cumulus());
        this.addType(SimpleCloudsCloudTypeProvider.ittyBitty());
        this.addType(SimpleCloudsCloudTypeProvider.nimbostratus());
        this.addType(SimpleCloudsCloudTypeProvider.smallCumulus());
        this.addType(SimpleCloudsCloudTypeProvider.stratocumulus());
        this.addType(SimpleCloudsCloudTypeProvider.stratus());
    }

    private static CloudType cumulonimbus() {
        ModifiableLayeredNoise noise = new ModifiableLayeredNoise();
        ModifiableNoiseSettings layer1 = new ModifiableNoiseSettings();
        layer1.setParam(AbstractNoiseSettings.Param.FADE_DISTANCE, 10.0f);
        layer1.setParam(AbstractNoiseSettings.Param.HEIGHT, 32.0f);
        layer1.setParam(AbstractNoiseSettings.Param.HEIGHT_OFFSET, 0.0f);
        layer1.setParam(AbstractNoiseSettings.Param.SCALE_X, 30.0f);
        layer1.setParam(AbstractNoiseSettings.Param.SCALE_Y, 30.0f);
        layer1.setParam(AbstractNoiseSettings.Param.SCALE_Z, 30.0f);
        layer1.setParam(AbstractNoiseSettings.Param.VALUE_OFFSET, 1.0f);
        layer1.setParam(AbstractNoiseSettings.Param.VALUE_SCALE, 1.0f);
        noise.addNoiseLayer(layer1);
        ModifiableNoiseSettings layer2 = new ModifiableNoiseSettings();
        layer2.setParam(AbstractNoiseSettings.Param.FADE_DISTANCE, 32.0f);
        layer2.setParam(AbstractNoiseSettings.Param.HEIGHT, 256.0f);
        layer2.setParam(AbstractNoiseSettings.Param.HEIGHT_OFFSET, 0.0f);
        layer2.setParam(AbstractNoiseSettings.Param.SCALE_X, 400.0f);
        layer2.setParam(AbstractNoiseSettings.Param.SCALE_Y, 400.0f);
        layer2.setParam(AbstractNoiseSettings.Param.SCALE_Z, 400.0f);
        layer2.setParam(AbstractNoiseSettings.Param.VALUE_OFFSET, 0.0f);
        layer2.setParam(AbstractNoiseSettings.Param.VALUE_SCALE, 1.0f);
        noise.addNoiseLayer(layer2);
        ModifiableNoiseSettings layer3 = new ModifiableNoiseSettings();
        layer3.setParam(AbstractNoiseSettings.Param.FADE_DISTANCE, 16.0f);
        layer3.setParam(AbstractNoiseSettings.Param.HEIGHT, 256.0f);
        layer3.setParam(AbstractNoiseSettings.Param.HEIGHT_OFFSET, 0.0f);
        layer3.setParam(AbstractNoiseSettings.Param.SCALE_X, 30.0f);
        layer3.setParam(AbstractNoiseSettings.Param.SCALE_Y, 30.0f);
        layer3.setParam(AbstractNoiseSettings.Param.SCALE_Z, 30.0f);
        layer3.setParam(AbstractNoiseSettings.Param.VALUE_OFFSET, 0.0f);
        layer3.setParam(AbstractNoiseSettings.Param.VALUE_SCALE, 0.1f);
        noise.addNoiseLayer(layer3);
        return new CloudType(SimpleCloudsMod.id("cumulonimbus"), WeatherType.THUNDERSTORM, 0.6f, 16.0f, 128.0f, 0.0f, noise);
    }

    private static CloudType cumulus() {
        ModifiableLayeredNoise noise = new ModifiableLayeredNoise();
        ModifiableNoiseSettings layer1 = new ModifiableNoiseSettings();
        layer1.setParam(AbstractNoiseSettings.Param.FADE_DISTANCE, 8.0f);
        layer1.setParam(AbstractNoiseSettings.Param.HEIGHT, 32.0f);
        layer1.setParam(AbstractNoiseSettings.Param.HEIGHT_OFFSET, 16.0f);
        layer1.setParam(AbstractNoiseSettings.Param.SCALE_X, 50.0f);
        layer1.setParam(AbstractNoiseSettings.Param.SCALE_Y, 100.0f);
        layer1.setParam(AbstractNoiseSettings.Param.SCALE_Z, 50.0f);
        layer1.setParam(AbstractNoiseSettings.Param.VALUE_OFFSET, -0.5f);
        layer1.setParam(AbstractNoiseSettings.Param.VALUE_SCALE, 1.0f);
        noise.addNoiseLayer(layer1);
        ModifiableNoiseSettings layer2 = new ModifiableNoiseSettings();
        layer2.setParam(AbstractNoiseSettings.Param.FADE_DISTANCE, 10.0f);
        layer2.setParam(AbstractNoiseSettings.Param.HEIGHT, 32.0f);
        layer2.setParam(AbstractNoiseSettings.Param.HEIGHT_OFFSET, 16.0f);
        layer2.setParam(AbstractNoiseSettings.Param.SCALE_X, 10.0f);
        layer2.setParam(AbstractNoiseSettings.Param.SCALE_Y, 10.0f);
        layer2.setParam(AbstractNoiseSettings.Param.SCALE_Z, 10.0f);
        layer2.setParam(AbstractNoiseSettings.Param.VALUE_OFFSET, 0.0f);
        layer2.setParam(AbstractNoiseSettings.Param.VALUE_SCALE, 0.5f);
        noise.addNoiseLayer(layer2);
        return new CloudType(SimpleCloudsMod.id("cumulus"), WeatherType.NONE, 0.2f, 16.0f, 16.0f, 0.2f, noise);
    }

    private static CloudType ittyBitty() {
        ModifiableNoiseSettings noise = new ModifiableNoiseSettings();
        noise.setParam(AbstractNoiseSettings.Param.FADE_DISTANCE, 10.0f);
        noise.setParam(AbstractNoiseSettings.Param.HEIGHT, 32.0f);
        noise.setParam(AbstractNoiseSettings.Param.HEIGHT_OFFSET, 0.0f);
        noise.setParam(AbstractNoiseSettings.Param.SCALE_X, 30.0f);
        noise.setParam(AbstractNoiseSettings.Param.SCALE_Y, 10.0f);
        noise.setParam(AbstractNoiseSettings.Param.SCALE_Z, 30.0f);
        noise.setParam(AbstractNoiseSettings.Param.VALUE_OFFSET, -0.8f);
        noise.setParam(AbstractNoiseSettings.Param.VALUE_SCALE, 1.0f);
        return new CloudType(SimpleCloudsMod.id("itty_bitty"), WeatherType.NONE, 0.0f, 16.0f, 32.0f, 0.2f, noise);
    }

    private static CloudType nimbostratus() {
        ModifiableLayeredNoise noise = new ModifiableLayeredNoise();
        ModifiableNoiseSettings layer1 = new ModifiableNoiseSettings();
        layer1.setParam(AbstractNoiseSettings.Param.FADE_DISTANCE, 16.0f);
        layer1.setParam(AbstractNoiseSettings.Param.HEIGHT, 96.0f);
        layer1.setParam(AbstractNoiseSettings.Param.HEIGHT_OFFSET, 0.0f);
        layer1.setParam(AbstractNoiseSettings.Param.SCALE_X, 400.0f);
        layer1.setParam(AbstractNoiseSettings.Param.SCALE_Y, 400.0f);
        layer1.setParam(AbstractNoiseSettings.Param.SCALE_Z, 400.0f);
        layer1.setParam(AbstractNoiseSettings.Param.VALUE_OFFSET, 1.0f);
        layer1.setParam(AbstractNoiseSettings.Param.VALUE_SCALE, 1.0f);
        noise.addNoiseLayer(layer1);
        ModifiableNoiseSettings layer2 = new ModifiableNoiseSettings();
        layer2.setParam(AbstractNoiseSettings.Param.FADE_DISTANCE, 16.0f);
        layer2.setParam(AbstractNoiseSettings.Param.HEIGHT, 64.0f);
        layer2.setParam(AbstractNoiseSettings.Param.HEIGHT_OFFSET, 64.0f);
        layer2.setParam(AbstractNoiseSettings.Param.SCALE_X, 400.0f);
        layer2.setParam(AbstractNoiseSettings.Param.SCALE_Y, 400.0f);
        layer2.setParam(AbstractNoiseSettings.Param.SCALE_Z, 400.0f);
        layer2.setParam(AbstractNoiseSettings.Param.VALUE_OFFSET, 2.0f);
        layer2.setParam(AbstractNoiseSettings.Param.VALUE_SCALE, 1.0f);
        noise.addNoiseLayer(layer2);
        ModifiableNoiseSettings layer3 = new ModifiableNoiseSettings();
        layer3.setParam(AbstractNoiseSettings.Param.FADE_DISTANCE, 10.0f);
        layer3.setParam(AbstractNoiseSettings.Param.HEIGHT, 128.0f);
        layer3.setParam(AbstractNoiseSettings.Param.HEIGHT_OFFSET, 0.0f);
        layer3.setParam(AbstractNoiseSettings.Param.SCALE_X, 50.0f);
        layer3.setParam(AbstractNoiseSettings.Param.SCALE_Y, 50.0f);
        layer3.setParam(AbstractNoiseSettings.Param.SCALE_Z, 50.0f);
        layer3.setParam(AbstractNoiseSettings.Param.VALUE_OFFSET, 0.0f);
        layer3.setParam(AbstractNoiseSettings.Param.VALUE_SCALE, 0.3f);
        noise.addNoiseLayer(layer3);
        return new CloudType(SimpleCloudsMod.id("nimbostratus"), WeatherType.THUNDERSTORM, 0.5f, 16.0f, 128.0f, 0.0f, noise);
    }

    private static CloudType smallCumulus() {
        ModifiableNoiseSettings noise = new ModifiableNoiseSettings();
        noise.setParam(AbstractNoiseSettings.Param.FADE_DISTANCE, 10.0f);
        noise.setParam(AbstractNoiseSettings.Param.HEIGHT, 32.0f);
        noise.setParam(AbstractNoiseSettings.Param.HEIGHT_OFFSET, 0.0f);
        noise.setParam(AbstractNoiseSettings.Param.SCALE_X, 30.0f);
        noise.setParam(AbstractNoiseSettings.Param.SCALE_Y, 10.0f);
        noise.setParam(AbstractNoiseSettings.Param.SCALE_Z, 30.0f);
        noise.setParam(AbstractNoiseSettings.Param.VALUE_OFFSET, -0.5f);
        noise.setParam(AbstractNoiseSettings.Param.VALUE_SCALE, 1.0f);
        return new CloudType(SimpleCloudsMod.id("small_cumulus"), WeatherType.NONE, 0.1f, 10.0f, 16.0f, 0.1f, noise);
    }

    private static CloudType stratocumulus() {
        ModifiableLayeredNoise noise = new ModifiableLayeredNoise();
        ModifiableNoiseSettings layer1 = new ModifiableNoiseSettings();
        layer1.setParam(AbstractNoiseSettings.Param.FADE_DISTANCE, 10.0f);
        layer1.setParam(AbstractNoiseSettings.Param.HEIGHT, 64.0f);
        layer1.setParam(AbstractNoiseSettings.Param.HEIGHT_OFFSET, 64.0f);
        layer1.setParam(AbstractNoiseSettings.Param.SCALE_X, 200.0f);
        layer1.setParam(AbstractNoiseSettings.Param.SCALE_Y, 80.0f);
        layer1.setParam(AbstractNoiseSettings.Param.SCALE_Z, 200.0f);
        layer1.setParam(AbstractNoiseSettings.Param.VALUE_OFFSET, -0.1f);
        layer1.setParam(AbstractNoiseSettings.Param.VALUE_SCALE, 1.0f);
        noise.addNoiseLayer(layer1);
        ModifiableNoiseSettings layer2 = new ModifiableNoiseSettings();
        layer2.setParam(AbstractNoiseSettings.Param.FADE_DISTANCE, 10.0f);
        layer2.setParam(AbstractNoiseSettings.Param.HEIGHT, 64.0f);
        layer2.setParam(AbstractNoiseSettings.Param.HEIGHT_OFFSET, 64.0f);
        layer2.setParam(AbstractNoiseSettings.Param.SCALE_X, 30.0f);
        layer2.setParam(AbstractNoiseSettings.Param.SCALE_Y, 30.0f);
        layer2.setParam(AbstractNoiseSettings.Param.SCALE_Z, 30.0f);
        layer2.setParam(AbstractNoiseSettings.Param.VALUE_OFFSET, 0.0f);
        layer2.setParam(AbstractNoiseSettings.Param.VALUE_SCALE, 0.2f);
        noise.addNoiseLayer(layer2);
        return new CloudType(SimpleCloudsMod.id("stratocumulus"), WeatherType.NONE, 0.6f, 64.0f, 48.0f, 0.02f, noise);
    }

    private static CloudType stratus() {
        ModifiableLayeredNoise noise = new ModifiableLayeredNoise();
        ModifiableNoiseSettings layer1 = new ModifiableNoiseSettings();
        layer1.setParam(AbstractNoiseSettings.Param.FADE_DISTANCE, 10.0f);
        layer1.setParam(AbstractNoiseSettings.Param.HEIGHT, 32.0f);
        layer1.setParam(AbstractNoiseSettings.Param.HEIGHT_OFFSET, 0.0f);
        layer1.setParam(AbstractNoiseSettings.Param.SCALE_X, 500.0f);
        layer1.setParam(AbstractNoiseSettings.Param.SCALE_Y, 30.0f);
        layer1.setParam(AbstractNoiseSettings.Param.SCALE_Z, 100.0f);
        layer1.setParam(AbstractNoiseSettings.Param.VALUE_OFFSET, 1.0f);
        layer1.setParam(AbstractNoiseSettings.Param.VALUE_SCALE, 1.0f);
        noise.addNoiseLayer(layer1);
        ModifiableNoiseSettings layer2 = new ModifiableNoiseSettings();
        layer2.setParam(AbstractNoiseSettings.Param.FADE_DISTANCE, 10.0f);
        layer2.setParam(AbstractNoiseSettings.Param.HEIGHT, 32.0f);
        layer2.setParam(AbstractNoiseSettings.Param.HEIGHT_OFFSET, 0.0f);
        layer2.setParam(AbstractNoiseSettings.Param.SCALE_X, 10.0f);
        layer2.setParam(AbstractNoiseSettings.Param.SCALE_Y, 10.0f);
        layer2.setParam(AbstractNoiseSettings.Param.SCALE_Z, 10.0f);
        layer2.setParam(AbstractNoiseSettings.Param.VALUE_OFFSET, 0.0f);
        layer2.setParam(AbstractNoiseSettings.Param.VALUE_SCALE, 0.5f);
        noise.addNoiseLayer(layer2);
        return new CloudType(SimpleCloudsMod.id("stratus"), WeatherType.RAIN, 0.4f, 0.0f, 32.0f, 0.0f, noise);
    }
}

