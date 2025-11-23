/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 */
package net.irisshaders.iris.gl.texture;

import com.mojang.blaze3d.platform.GlStateManager;

public class TextureUploadHelper {
    private TextureUploadHelper() {
    }

    public static void resetTextureUploadState() {
        GlStateManager._pixelStore((int)3314, (int)0);
        GlStateManager._pixelStore((int)3315, (int)0);
        GlStateManager._pixelStore((int)3316, (int)0);
        GlStateManager._pixelStore((int)3317, (int)4);
    }
}

