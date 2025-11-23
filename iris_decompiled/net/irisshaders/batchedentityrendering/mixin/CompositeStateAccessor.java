/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderStateShard$TransparencyStateShard
 *  net.minecraft.client.renderer.RenderType$CompositeState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package net.irisshaders.batchedentityrendering.mixin;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={RenderType.CompositeState.class})
public interface CompositeStateAccessor {
    @Accessor(value="transparencyState")
    public RenderStateShard.TransparencyStateShard getTransparency();
}

