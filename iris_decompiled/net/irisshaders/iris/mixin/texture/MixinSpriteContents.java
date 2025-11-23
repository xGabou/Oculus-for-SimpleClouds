/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.renderer.texture.MipmapGenerator
 *  net.minecraft.client.renderer.texture.SpriteContents
 *  net.minecraft.client.renderer.texture.SpriteContents$Ticker
 *  net.minecraft.client.renderer.texture.SpriteTicker
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.mixin.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.texture.SpriteContentsExtension;
import net.irisshaders.iris.texture.mipmap.CustomMipmapGenerator;
import net.minecraft.client.renderer.texture.MipmapGenerator;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteTicker;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={SpriteContents.class})
public class MixinSpriteContents
implements SpriteContentsExtension {
    @Unique
    @Nullable
    private SpriteContents.Ticker createdTicker;

    @Redirect(method={"increaseMipLevel"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/texture/MipmapGenerator;generateMipLevels([Lcom/mojang/blaze3d/platform/NativeImage;I)[Lcom/mojang/blaze3d/platform/NativeImage;"))
    private NativeImage[] iris$redirectMipmapGeneration(NativeImage[] nativeImages, int mipLevel) {
        CustomMipmapGenerator.Provider provider;
        CustomMipmapGenerator generator;
        MixinSpriteContents mixinSpriteContents = this;
        if (mixinSpriteContents instanceof CustomMipmapGenerator.Provider && (generator = (provider = (CustomMipmapGenerator.Provider)((Object)mixinSpriteContents)).getMipmapGenerator()) != null) {
            try {
                return generator.generateMipLevels(nativeImages, mipLevel);
            }
            catch (Exception e) {
                Iris.logger.error("ERROR MIPMAPPING", e);
            }
        }
        return MipmapGenerator.m_246246_((NativeImage[])nativeImages, (int)mipLevel);
    }

    @Inject(method={"createTicker()Lnet/minecraft/client/renderer/texture/SpriteTicker;"}, at={@At(value="RETURN")})
    private void onReturnCreateTicker(CallbackInfoReturnable<SpriteTicker> cir) {
        SpriteTicker ticker = (SpriteTicker)cir.getReturnValue();
        if (ticker instanceof SpriteContents.Ticker) {
            SpriteContents.Ticker innerTicker;
            this.createdTicker = innerTicker = (SpriteContents.Ticker)ticker;
        }
    }

    @Override
    @Nullable
    public SpriteContents.Ticker getCreatedTicker() {
        return this.createdTicker;
    }
}

