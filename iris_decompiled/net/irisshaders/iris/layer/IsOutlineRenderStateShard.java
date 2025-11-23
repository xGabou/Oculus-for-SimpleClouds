/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderStateShard
 */
package net.irisshaders.iris.layer;

import net.irisshaders.iris.layer.GbufferPrograms;
import net.minecraft.client.renderer.RenderStateShard;

public class IsOutlineRenderStateShard
extends RenderStateShard {
    public static final IsOutlineRenderStateShard INSTANCE = new IsOutlineRenderStateShard();

    private IsOutlineRenderStateShard() {
        super("iris:is_outline", GbufferPrograms::beginOutline, GbufferPrograms::endOutline);
    }
}

