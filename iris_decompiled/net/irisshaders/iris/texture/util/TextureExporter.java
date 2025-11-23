/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  org.apache.commons.io.FilenameUtils
 */
package net.irisshaders.iris.texture.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.FilenameUtils;

public class TextureExporter {
    public static void exportTextures(String directory, String filename, int textureId, int mipLevel, int width, int height) {
        String extension = FilenameUtils.getExtension((String)filename);
        String baseName = filename.substring(0, filename.length() - extension.length() - 1);
        for (int level = 0; level <= mipLevel; ++level) {
            TextureExporter.exportTexture(directory, baseName + "_" + level + "." + extension, textureId, level, width >> level, height >> level);
        }
    }

    public static void exportTexture(String directory, String filename, int textureId, int level, int width, int height) {
        NativeImage nativeImage = new NativeImage(width, height, false);
        RenderSystem.bindTexture((int)textureId);
        nativeImage.m_85045_(level, false);
        File dir = new File(Minecraft.m_91087_().f_91069_, directory);
        dir.mkdirs();
        File file = new File(dir, filename);
        Util.m_183992_().execute(() -> {
            try {
                nativeImage.m_85056_(file);
            }
            catch (Exception exception) {
            }
            finally {
                nativeImage.close();
            }
        });
    }
}

