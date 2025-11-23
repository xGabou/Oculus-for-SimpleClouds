/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.blaze3d.platform.NativeImage$Format
 *  net.minecraft.client.renderer.texture.DynamicTexture
 */
package net.irisshaders.iris.targets.backed;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.Objects;
import java.util.Random;
import java.util.function.IntSupplier;
import net.irisshaders.iris.gl.texture.TextureAccess;
import net.irisshaders.iris.gl.texture.TextureType;
import net.minecraft.client.renderer.texture.DynamicTexture;

public class NativeImageBackedNoiseTexture
extends DynamicTexture
implements TextureAccess {
    public NativeImageBackedNoiseTexture(int size) {
        super(NativeImageBackedNoiseTexture.create(size));
    }

    private static NativeImage create(int size) {
        NativeImage image = new NativeImage(NativeImage.Format.RGBA, size, size, false);
        Random random = new Random(0L);
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                int color = random.nextInt() | 0xFF000000;
                image.m_84988_(x, y, color);
            }
        }
        return image;
    }

    public void m_117985_() {
        NativeImage image = Objects.requireNonNull(this.m_117991_());
        this.m_117966_();
        image.m_85013_(0, 0, 0, 0, 0, image.m_84982_(), image.m_85084_(), true, false, false, false);
    }

    @Override
    public TextureType getType() {
        return TextureType.TEXTURE_2D;
    }

    @Override
    public IntSupplier getTextureId() {
        return () -> ((NativeImageBackedNoiseTexture)this).m_117963_();
    }
}

