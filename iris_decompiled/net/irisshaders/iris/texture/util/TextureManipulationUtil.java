/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  net.minecraft.client.Minecraft
 */
package net.irisshaders.iris.texture.util;

import com.mojang.blaze3d.platform.GlStateManager;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.minecraft.client.Minecraft;

public class TextureManipulationUtil {
    private static int colorFillFBO = -1;

    public static void fillWithColor(int textureId, int maxLevel, int rgba) {
        if (colorFillFBO == -1) {
            colorFillFBO = GlStateManager.glGenFramebuffers();
        }
        int previousFramebufferId = GlStateManager._getInteger((int)36006);
        float[] previousClearColor = new float[4];
        IrisRenderSystem.getFloatv(3106, previousClearColor);
        int previousTextureId = GlStateManager._getInteger((int)32873);
        int[] previousViewport = new int[4];
        IrisRenderSystem.getIntegerv(2978, previousViewport);
        GlStateManager._glBindFramebuffer((int)36160, (int)colorFillFBO);
        GlStateManager._clearColor((float)((float)(rgba >> 24 & 0xFF) / 255.0f), (float)((float)(rgba >> 16 & 0xFF) / 255.0f), (float)((float)(rgba >> 8 & 0xFF) / 255.0f), (float)((float)(rgba & 0xFF) / 255.0f));
        GlStateManager._bindTexture((int)textureId);
        for (int level = 0; level <= maxLevel; ++level) {
            int width = GlStateManager._getTexLevelParameter((int)3553, (int)level, (int)4096);
            int height = GlStateManager._getTexLevelParameter((int)3553, (int)level, (int)4097);
            GlStateManager._viewport((int)0, (int)0, (int)width, (int)height);
            GlStateManager._glFramebufferTexture2D((int)36160, (int)36064, (int)3553, (int)textureId, (int)level);
            GlStateManager._clear((int)16384, (boolean)Minecraft.f_91002_);
            GlStateManager._glFramebufferTexture2D((int)36160, (int)36064, (int)3553, (int)0, (int)level);
        }
        GlStateManager._glBindFramebuffer((int)36160, (int)previousFramebufferId);
        GlStateManager._clearColor((float)previousClearColor[0], (float)previousClearColor[1], (float)previousClearColor[2], (float)previousClearColor[3]);
        GlStateManager._bindTexture((int)previousTextureId);
        GlStateManager._viewport((int)previousViewport[0], (int)previousViewport[1], (int)previousViewport[2], (int)previousViewport[3]);
    }
}

