/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.util.FastColor$ABGR32
 */
package net.irisshaders.iris.texture.util;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.util.FastColor;

public class ImageManipulationUtil {
    public static NativeImage scaleNearestNeighbor(NativeImage image, int newWidth, int newHeight) {
        NativeImage scaled = new NativeImage(image.m_85102_(), newWidth, newHeight, false);
        float xScale = (float)newWidth / (float)image.m_84982_();
        float yScale = (float)newHeight / (float)image.m_85084_();
        for (int y = 0; y < newHeight; ++y) {
            for (int x = 0; x < newWidth; ++x) {
                float unscaledX = ((float)x + 0.5f) / xScale;
                float unscaledY = ((float)y + 0.5f) / yScale;
                scaled.m_84988_(x, y, image.m_84985_((int)unscaledX, (int)unscaledY));
            }
        }
        return scaled;
    }

    public static NativeImage scaleBilinear(NativeImage image, int newWidth, int newHeight) {
        NativeImage scaled = new NativeImage(image.m_85102_(), newWidth, newHeight, false);
        float xScale = (float)newWidth / (float)image.m_84982_();
        float yScale = (float)newHeight / (float)image.m_85084_();
        for (int y = 0; y < newHeight; ++y) {
            for (int x = 0; x < newWidth; ++x) {
                float unscaledX = ((float)x + 0.5f) / xScale;
                float unscaledY = ((float)y + 0.5f) / yScale;
                int x1 = Math.round(unscaledX);
                int y1 = Math.round(unscaledY);
                int x0 = x1 - 1;
                int y0 = y1 - 1;
                boolean x0valid = true;
                boolean y0valid = true;
                boolean x1valid = true;
                boolean y1valid = true;
                if (x0 < 0) {
                    x0valid = false;
                }
                if (y0 < 0) {
                    y0valid = false;
                }
                if (x1 >= image.m_84982_()) {
                    x1valid = false;
                }
                if (y1 >= image.m_85084_()) {
                    y1valid = false;
                }
                int finalColor = 0;
                if (x0valid & y0valid & x1valid & y1valid) {
                    leftWeight = (float)x1 + 0.5f - unscaledX;
                    rightWeight = unscaledX - ((float)x0 + 0.5f);
                    float topWeight = (float)y1 + 0.5f - unscaledY;
                    float bottomWeight = unscaledY - ((float)y0 + 0.5f);
                    float weightTL = leftWeight * topWeight;
                    float weightTR = rightWeight * topWeight;
                    float weightBL = leftWeight * bottomWeight;
                    float weightBR = rightWeight * bottomWeight;
                    int colorTL = image.m_84985_(x0, y0);
                    int colorTR = image.m_84985_(x1, y0);
                    int colorBL = image.m_84985_(x0, y1);
                    int colorBR = image.m_84985_(x1, y1);
                    finalColor = ImageManipulationUtil.blendColor(colorTL, colorTR, colorBL, colorBR, weightTL, weightTR, weightBL, weightBR);
                } else if (x0valid & x1valid) {
                    leftWeight = (float)x1 + 0.5f - unscaledX;
                    rightWeight = unscaledX - ((float)x0 + 0.5f);
                    int validY = y0valid ? y0 : y1;
                    int colorLeft = image.m_84985_(x0, validY);
                    int colorRight = image.m_84985_(x1, validY);
                    finalColor = ImageManipulationUtil.blendColor(colorLeft, colorRight, leftWeight, rightWeight);
                } else if (y0valid & y1valid) {
                    float topWeight = (float)y1 + 0.5f - unscaledY;
                    float bottomWeight = unscaledY - ((float)y0 + 0.5f);
                    int validX = x0valid ? x0 : x1;
                    int colorTop = image.m_84985_(validX, y0);
                    int colorBottom = image.m_84985_(validX, y1);
                    finalColor = ImageManipulationUtil.blendColor(colorTop, colorBottom, topWeight, bottomWeight);
                } else {
                    finalColor = image.m_84985_(x0valid ? x0 : x1, y0valid ? y0 : y1);
                }
                scaled.m_84988_(x, y, finalColor);
            }
        }
        return scaled;
    }

    private static int blendColor(int c0, int c1, int c2, int c3, float w0, float w1, float w2, float w3) {
        return FastColor.ABGR32.m_266248_((int)ImageManipulationUtil.blendChannel(FastColor.ABGR32.m_266503_((int)c0), FastColor.ABGR32.m_266503_((int)c1), FastColor.ABGR32.m_266503_((int)c2), FastColor.ABGR32.m_266503_((int)c3), w0, w1, w2, w3), (int)ImageManipulationUtil.blendChannel(FastColor.ABGR32.m_266247_((int)c0), FastColor.ABGR32.m_266247_((int)c1), FastColor.ABGR32.m_266247_((int)c2), FastColor.ABGR32.m_266247_((int)c3), w0, w1, w2, w3), (int)ImageManipulationUtil.blendChannel(FastColor.ABGR32.m_266446_((int)c0), FastColor.ABGR32.m_266446_((int)c1), FastColor.ABGR32.m_266446_((int)c2), FastColor.ABGR32.m_266446_((int)c3), w0, w1, w2, w3), (int)ImageManipulationUtil.blendChannel(FastColor.ABGR32.m_266313_((int)c0), FastColor.ABGR32.m_266313_((int)c1), FastColor.ABGR32.m_266313_((int)c2), FastColor.ABGR32.m_266313_((int)c3), w0, w1, w2, w3));
    }

    private static int blendChannel(int v0, int v1, int v2, int v3, float w0, float w1, float w2, float w3) {
        return Math.round((float)v0 * w0 + (float)v1 * w1 + (float)v2 * w2 + (float)v3 * w3);
    }

    private static int blendColor(int c0, int c1, float w0, float w1) {
        return FastColor.ABGR32.m_266248_((int)ImageManipulationUtil.blendChannel(FastColor.ABGR32.m_266503_((int)c0), FastColor.ABGR32.m_266503_((int)c1), w0, w1), (int)ImageManipulationUtil.blendChannel(FastColor.ABGR32.m_266247_((int)c0), FastColor.ABGR32.m_266247_((int)c1), w0, w1), (int)ImageManipulationUtil.blendChannel(FastColor.ABGR32.m_266446_((int)c0), FastColor.ABGR32.m_266446_((int)c1), w0, w1), (int)ImageManipulationUtil.blendChannel(FastColor.ABGR32.m_266313_((int)c0), FastColor.ABGR32.m_266313_((int)c1), w0, w1));
    }

    private static int blendChannel(int v0, int v1, float w0, float w1) {
        return Math.round((float)v0 * w0 + (float)v1 * w1);
    }
}

