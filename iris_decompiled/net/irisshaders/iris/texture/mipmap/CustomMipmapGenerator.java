/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.texture.mipmap;

import com.mojang.blaze3d.platform.NativeImage;
import org.jetbrains.annotations.Nullable;

public interface CustomMipmapGenerator {
    public NativeImage[] generateMipLevels(NativeImage[] var1, int var2);

    public static interface Provider {
        @Nullable
        public CustomMipmapGenerator getMipmapGenerator();
    }
}

