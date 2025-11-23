/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.platform.GlStateManager$ColorMask
 *  com.mojang.blaze3d.platform.GlStateManager$DepthState
 */
package net.irisshaders.iris.gl.blending;

import com.mojang.blaze3d.platform.GlStateManager;
import net.irisshaders.iris.gl.blending.ColorMask;
import net.irisshaders.iris.mixin.GlStateManagerAccessor;

public class DepthColorStorage {
    private static boolean originalDepthEnable;
    private static ColorMask originalColor;
    private static boolean depthColorLocked;

    public static boolean isDepthColorLocked() {
        return depthColorLocked;
    }

    public static void disableDepthColor() {
        if (!depthColorLocked) {
            GlStateManager.ColorMask colorMask = GlStateManagerAccessor.getCOLOR_MASK();
            GlStateManager.DepthState depthState = GlStateManagerAccessor.getDEPTH();
            originalDepthEnable = depthState.f_84627_;
            originalColor = new ColorMask(colorMask.f_84608_, colorMask.f_84609_, colorMask.f_84610_, colorMask.f_84611_);
        }
        depthColorLocked = false;
        GlStateManager._depthMask((boolean)false);
        GlStateManager._colorMask((boolean)false, (boolean)false, (boolean)false, (boolean)false);
        depthColorLocked = true;
    }

    public static void deferDepthEnable(boolean enabled) {
        originalDepthEnable = enabled;
    }

    public static void deferColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        originalColor = new ColorMask(red, green, blue, alpha);
    }

    public static void unlockDepthColor() {
        if (!depthColorLocked) {
            return;
        }
        depthColorLocked = false;
        GlStateManager._depthMask((boolean)originalDepthEnable);
        GlStateManager._colorMask((boolean)originalColor.isRedMasked(), (boolean)originalColor.isGreenMasked(), (boolean)originalColor.isBlueMasked(), (boolean)originalColor.isAlphaMasked());
    }
}

