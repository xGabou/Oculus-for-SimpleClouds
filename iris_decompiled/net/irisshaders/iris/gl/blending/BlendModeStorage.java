/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.platform.GlStateManager$BlendState
 */
package net.irisshaders.iris.gl.blending;

import com.mojang.blaze3d.platform.GlStateManager;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.blending.BlendMode;
import net.irisshaders.iris.mixin.GlStateManagerAccessor;
import net.irisshaders.iris.mixin.statelisteners.BooleanStateAccessor;

public class BlendModeStorage {
    private static boolean originalBlendEnable;
    private static BlendMode originalBlend;
    private static boolean blendLocked;

    public static boolean isBlendLocked() {
        return blendLocked;
    }

    public static void overrideBlend(BlendMode override) {
        if (!blendLocked) {
            GlStateManager.BlendState blendState = GlStateManagerAccessor.getBLEND();
            originalBlendEnable = ((BooleanStateAccessor)blendState.f_84577_).isEnabled();
            originalBlend = new BlendMode(blendState.f_84578_, blendState.f_84579_, blendState.f_84580_, blendState.f_84581_);
        }
        blendLocked = false;
        if (override == null) {
            GlStateManager._disableBlend();
        } else {
            GlStateManager._enableBlend();
            GlStateManager._blendFuncSeparate((int)override.srcRgb(), (int)override.dstRgb(), (int)override.srcAlpha(), (int)override.dstAlpha());
        }
        blendLocked = true;
    }

    public static void overrideBufferBlend(int index, BlendMode override) {
        if (!blendLocked) {
            GlStateManager.BlendState blendState = GlStateManagerAccessor.getBLEND();
            originalBlendEnable = ((BooleanStateAccessor)blendState.f_84577_).isEnabled();
            originalBlend = new BlendMode(blendState.f_84578_, blendState.f_84579_, blendState.f_84580_, blendState.f_84581_);
        }
        if (override == null) {
            IrisRenderSystem.disableBufferBlend(index);
        } else {
            IrisRenderSystem.enableBufferBlend(index);
            IrisRenderSystem.blendFuncSeparatei(index, override.srcRgb(), override.dstRgb(), override.srcAlpha(), override.dstAlpha());
        }
        blendLocked = true;
    }

    public static void deferBlendModeToggle(boolean enabled) {
        originalBlendEnable = enabled;
    }

    public static void deferBlendFunc(int srcRgb, int dstRgb, int srcAlpha, int dstAlpha) {
        originalBlend = new BlendMode(srcRgb, dstRgb, srcAlpha, dstAlpha);
    }

    public static void restoreBlend() {
        if (!blendLocked) {
            return;
        }
        blendLocked = false;
        if (originalBlendEnable) {
            GlStateManager._enableBlend();
        } else {
            GlStateManager._disableBlend();
        }
        GlStateManager._blendFuncSeparate((int)originalBlend.srcRgb(), (int)originalBlend.dstRgb(), (int)originalBlend.srcAlpha(), (int)originalBlend.dstAlpha());
    }
}

