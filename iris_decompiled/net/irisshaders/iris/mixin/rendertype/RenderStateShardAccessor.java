/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderStateShard
 *  net.minecraft.client.renderer.RenderStateShard$TransparencyStateShard
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package net.irisshaders.iris.mixin.rendertype;

import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={RenderStateShard.class})
public interface RenderStateShardAccessor {
    @Accessor(value="TRANSLUCENT_TRANSPARENCY")
    public static RenderStateShard.TransparencyStateShard getTranslucentTransparency() {
        throw new AssertionError();
    }

    @Accessor(value="name")
    public String getName();
}

