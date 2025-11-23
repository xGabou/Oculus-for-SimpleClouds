/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.blaze3d.platform.NativeImage$Format
 *  net.minecraft.client.renderer.texture.SpriteContents
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.FastColor$ABGR32
 *  org.lwjgl.system.MemoryUtil
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package net.irisshaders.iris.mixin.bettermipmaps;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.Locale;
import net.irisshaders.iris.helpers.ColorSRGB;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={SpriteContents.class})
public class MixinTextureAtlasSprite {
    private static final float[] SRGB_TO_LINEAR = new float[256];
    @Mutable
    @Shadow
    @Final
    private NativeImage f_243904_;

    @Unique
    private static void iris$fillInTransparentPixelColors(NativeImage nativeImage) {
        long ppPixel = MixinTextureAtlasSprite.getPointerRGBA(nativeImage);
        int pixelCount = nativeImage.m_85084_() * nativeImage.m_84982_();
        float r = 0.0f;
        float g = 0.0f;
        float b = 0.0f;
        float totalWeight = 0.0f;
        for (int pixelIndex = 0; pixelIndex < pixelCount; ++pixelIndex) {
            long pPixel = ppPixel + (long)pixelIndex * 4L;
            int color = MemoryUtil.memGetInt((long)pPixel);
            int alpha = FastColor.ABGR32.m_266503_((int)color);
            if (alpha == 0) continue;
            float weight = alpha;
            r += ColorSRGB.srgbToLinear(FastColor.ABGR32.m_266313_((int)color)) * weight;
            g += ColorSRGB.srgbToLinear(FastColor.ABGR32.m_266446_((int)color)) * weight;
            b += ColorSRGB.srgbToLinear(FastColor.ABGR32.m_266247_((int)color)) * weight;
            totalWeight += weight;
        }
        if (totalWeight == 0.0f) {
            return;
        }
        int averageColor = ColorSRGB.linearToSrgb(r /= totalWeight, g /= totalWeight, b /= totalWeight, 0);
        for (int pixelIndex = 0; pixelIndex < pixelCount; ++pixelIndex) {
            long pPixel = ppPixel + (long)pixelIndex * 4L;
            int color = MemoryUtil.memGetInt((long)pPixel);
            int alpha = FastColor.ABGR32.m_266503_((int)color);
            if (alpha != 0) continue;
            MemoryUtil.memPutInt((long)pPixel, (int)averageColor);
        }
    }

    private static long getPointerRGBA(NativeImage nativeImage) {
        if (nativeImage.m_85102_() != NativeImage.Format.RGBA) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Tried to get pointer to RGBA pixel data on NativeImage of wrong format; have %s", nativeImage.m_85102_()));
        }
        return nativeImage.f_84964_;
    }

    @Redirect(method={"<init>"}, at=@At(value="FIELD", target="Lnet/minecraft/client/renderer/texture/SpriteContents;originalImage:Lcom/mojang/blaze3d/platform/NativeImage;", opcode=181))
    private void iris$beforeGenerateMipLevels(SpriteContents instance, NativeImage nativeImage, ResourceLocation resourceLocation) {
        if (resourceLocation.m_135815_().contains("leaves")) {
            this.f_243904_ = nativeImage;
            return;
        }
        MixinTextureAtlasSprite.iris$fillInTransparentPixelColors(nativeImage);
        this.f_243904_ = nativeImage;
    }

    static {
        for (int i = 0; i < 256; ++i) {
            MixinTextureAtlasSprite.SRGB_TO_LINEAR[i] = (float)Math.pow((double)i / 255.0, 2.2);
        }
    }
}

