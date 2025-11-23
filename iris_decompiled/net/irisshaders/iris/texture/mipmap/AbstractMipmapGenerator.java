/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 */
package net.irisshaders.iris.texture.mipmap;

import com.mojang.blaze3d.platform.NativeImage;
import net.irisshaders.iris.texture.mipmap.CustomMipmapGenerator;

public abstract class AbstractMipmapGenerator
implements CustomMipmapGenerator {
    @Override
    public NativeImage[] generateMipLevels(NativeImage[] image, int mipLevel) {
        if (mipLevel + 1 <= image.length) {
            return image;
        }
        NativeImage[] newImages = new NativeImage[mipLevel + 1];
        if (mipLevel > 0) {
            for (int level = 1; level <= mipLevel; ++level) {
                NativeImage prevMipmap = level == 1 ? image[0] : newImages[level - 1];
                NativeImage mipmap = new NativeImage(prevMipmap.m_84982_() >> 1, prevMipmap.m_85084_() >> 1, false);
                int width = mipmap.m_84982_();
                int height = mipmap.m_85084_();
                for (int x = 0; x < width; ++x) {
                    for (int y = 0; y < height; ++y) {
                        mipmap.m_84988_(x, y, this.blend(prevMipmap.m_84985_(x * 2, y * 2), prevMipmap.m_84985_(x * 2 + 1, y * 2), prevMipmap.m_84985_(x * 2, y * 2 + 1), prevMipmap.m_84985_(x * 2 + 1, y * 2 + 1)));
                    }
                }
                newImages[level] = mipmap;
            }
        }
        newImages[0] = image[0];
        return newImages;
    }

    public abstract int blend(int var1, int var2, int var3, int var4);
}

