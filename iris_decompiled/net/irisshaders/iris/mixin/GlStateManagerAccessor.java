/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.platform.GlStateManager$BlendState
 *  com.mojang.blaze3d.platform.GlStateManager$ColorMask
 *  com.mojang.blaze3d.platform.GlStateManager$DepthState
 *  com.mojang.blaze3d.platform.GlStateManager$TextureState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={GlStateManager.class})
public interface GlStateManagerAccessor {
    @Accessor(value="BLEND")
    public static GlStateManager.BlendState getBLEND() {
        throw new UnsupportedOperationException("Not accessed");
    }

    @Accessor(value="COLOR_MASK")
    public static GlStateManager.ColorMask getCOLOR_MASK() {
        throw new UnsupportedOperationException("Not accessed");
    }

    @Accessor(value="DEPTH")
    public static GlStateManager.DepthState getDEPTH() {
        throw new UnsupportedOperationException("Not accessed");
    }

    @Accessor(value="activeTexture")
    public static int getActiveTexture() {
        throw new UnsupportedOperationException("Not accessed");
    }

    @Accessor(value="TEXTURES")
    public static GlStateManager.TextureState[] getTEXTURES() {
        throw new UnsupportedOperationException("Not accessed");
    }
}

