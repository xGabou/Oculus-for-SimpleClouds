/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderStateShard
 *  net.minecraft.client.renderer.RenderStateShard$TransparencyStateShard
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package net.irisshaders.batchedentityrendering.mixin;

import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={RenderStateShard.class})
public interface RenderStateShardAccessor {
    @Accessor(value="NO_TRANSPARENCY")
    public static RenderStateShard.TransparencyStateShard getNO_TRANSPARENCY() {
        throw new AssertionError();
    }

    @Accessor(value="GLINT_TRANSPARENCY")
    public static RenderStateShard.TransparencyStateShard getGLINT_TRANSPARENCY() {
        throw new AssertionError();
    }

    @Accessor(value="CRUMBLING_TRANSPARENCY")
    public static RenderStateShard.TransparencyStateShard getCRUMBLING_TRANSPARENCY() {
        throw new AssertionError();
    }
}

