/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderStateShard
 */
package net.irisshaders.iris.layer;

import net.irisshaders.iris.layer.GbufferPrograms;
import net.minecraft.client.renderer.RenderStateShard;

public final class EntityRenderStateShard
extends RenderStateShard {
    public static final EntityRenderStateShard INSTANCE = new EntityRenderStateShard();

    private EntityRenderStateShard() {
        super("iris:is_entity", GbufferPrograms::beginEntities, GbufferPrograms::endEntities);
    }
}

