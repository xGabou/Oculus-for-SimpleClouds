/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraftforge.eventbus.api.Event
 */
package dev.nonamecrackers2.simpleclouds.client.event.impl;

import dev.nonamecrackers2.simpleclouds.client.renderer.pipeline.CloudsRenderPipeline;
import javax.annotation.Nullable;
import net.minecraftforge.eventbus.api.Event;

public class DetermineCloudRenderPipelineEvent
extends Event {
    private final CloudsRenderPipeline defaultPipeline;
    @Nullable
    private CloudsRenderPipeline overridenPipeline;

    public DetermineCloudRenderPipelineEvent(CloudsRenderPipeline defaultPipeline) {
        this.defaultPipeline = defaultPipeline;
    }

    public CloudsRenderPipeline getRenderPipeline() {
        return this.defaultPipeline;
    }

    @Nullable
    public CloudsRenderPipeline getOverridenPipeline() {
        return this.overridenPipeline;
    }

    public void overridePipeline(@Nullable CloudsRenderPipeline pipeline) {
        this.overridenPipeline = pipeline;
    }
}

