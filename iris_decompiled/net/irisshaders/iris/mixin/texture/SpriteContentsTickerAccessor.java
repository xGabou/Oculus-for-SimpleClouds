/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.SpriteContents$AnimatedTexture
 *  net.minecraft.client.renderer.texture.SpriteContents$Ticker
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package net.irisshaders.iris.mixin.texture;

import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={SpriteContents.Ticker.class})
public interface SpriteContentsTickerAccessor {
    @Accessor(value="frame")
    public int getFrame();

    @Accessor(value="frame")
    public void setFrame(int var1);

    @Accessor(value="subFrame")
    public int getSubFrame();

    @Accessor(value="subFrame")
    public void setSubFrame(int var1);

    @Accessor(value="animationInfo")
    public SpriteContents.AnimatedTexture getAnimationInfo();
}

