/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.seibel.distanthorizons.api.DhApi$Delayed
 *  dev.nonamecrackers2.simpleclouds.api.client.event.ModifyCloudRenderDistanceEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 */
package dev.nonamecrackers2.simpleclouds.client.dh.event;

import com.seibel.distanthorizons.api.DhApi;
import dev.nonamecrackers2.simpleclouds.api.client.event.ModifyCloudRenderDistanceEvent;
import dev.nonamecrackers2.simpleclouds.client.dh.pipeline.DhSupportPipeline;
import dev.nonamecrackers2.simpleclouds.client.event.impl.DetermineCloudRenderPipelineEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SimpleCloudsDhForgeEvents {
    @SubscribeEvent
    public static void modifyRenderDistance(ModifyCloudRenderDistanceEvent event) {
        float renderDistance = event.getRenderDistance();
        event.setRenderDistance(Math.min(renderDistance, (float)((Integer)DhApi.Delayed.configs.graphics().chunkRenderDistance().getValue()).intValue() * 16.0f));
    }

    @SubscribeEvent
    public static void determineRenderPipeline(DetermineCloudRenderPipelineEvent event) {
        event.overridePipeline(DhSupportPipeline.INSTANCE);
    }
}

