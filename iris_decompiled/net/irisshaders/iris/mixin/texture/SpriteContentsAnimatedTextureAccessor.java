/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.SpriteContents$AnimatedTexture
 *  net.minecraft.client.renderer.texture.SpriteContents$FrameInfo
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package net.irisshaders.iris.mixin.texture;

import java.util.List;
import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={SpriteContents.AnimatedTexture.class})
public interface SpriteContentsAnimatedTextureAccessor {
    @Accessor(value="frames")
    public List<SpriteContents.FrameInfo> getFrames();

    @Invoker(value="uploadFrame")
    public void invokeUploadFrame(int var1, int var2, int var3);
}

