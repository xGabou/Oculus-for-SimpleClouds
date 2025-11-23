/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.blaze3d.platform.NativeImage$Format
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.util.FastColor$ABGR32
 */
package net.irisshaders.iris.targets.backed;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.FastColor;

public class NativeImageBackedSingleColorTexture
extends DynamicTexture {
    public NativeImageBackedSingleColorTexture(int red, int green, int blue, int alpha) {
        super(NativeImageBackedSingleColorTexture.create(FastColor.ABGR32.m_266248_((int)alpha, (int)blue, (int)green, (int)red)));
    }

    public NativeImageBackedSingleColorTexture(int rgba) {
        this(rgba >> 24 & 0xFF, rgba >> 16 & 0xFF, rgba >> 8 & 0xFF, rgba & 0xFF);
    }

    private static NativeImage create(int color) {
        NativeImage image = new NativeImage(NativeImage.Format.RGBA, 1, 1, false);
        image.m_84988_(0, 0, color);
        return image;
    }
}

