/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.data.DataGenerator
 *  net.minecraftforge.data.event.GatherDataEvent
 */
package dev.nonamecrackers2.simpleclouds.common.event;

import dev.nonamecrackers2.simpleclouds.common.data.SimpleCloudsCloudSpawningConfigProvider;
import dev.nonamecrackers2.simpleclouds.common.data.SimpleCloudsCloudTypeProvider;
import dev.nonamecrackers2.simpleclouds.common.data.SimpleCloudsLangProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;

public class SimpleCloudsDataEvents {
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(event.includeClient(), SimpleCloudsLangProvider::new);
        generator.addProvider(true, SimpleCloudsCloudTypeProvider::new);
        generator.addProvider(true, SimpleCloudsCloudSpawningConfigProvider::new);
    }
}

