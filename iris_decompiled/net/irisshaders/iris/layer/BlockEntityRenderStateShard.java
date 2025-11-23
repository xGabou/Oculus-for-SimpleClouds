/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderStateShard
 */
package net.irisshaders.iris.layer;

import net.irisshaders.iris.layer.GbufferPrograms;
import net.minecraft.client.renderer.RenderStateShard;

public final class BlockEntityRenderStateShard
extends RenderStateShard {
    public static final BlockEntityRenderStateShard INSTANCE = new BlockEntityRenderStateShard();

    private BlockEntityRenderStateShard() {
        super("iris:is_block_entity", GbufferPrograms::beginBlockEntities, GbufferPrograms::endBlockEntities);
    }
}

