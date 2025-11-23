/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.platform.TextureUtil
 *  com.mojang.blaze3d.systems.RenderSystem
 */
package dev.nonamecrackers2.simpleclouds.client.framebuffer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.IntBuffer;

public class CloudRenderTarget
extends RenderTarget {
    private final boolean highPrecisionDepth;

    public CloudRenderTarget(int width, int height, boolean clearError, boolean highPrecisionDepth) {
        super(true);
        this.highPrecisionDepth = highPrecisionDepth;
        RenderSystem.assertOnRenderThreadOrInit();
        this.m_83941_(width, height, clearError);
    }

    public void m_83950_(int width, int height, boolean clearErrors) {
        RenderSystem.assertOnRenderThreadOrInit();
        int i = RenderSystem.maxSupportedTextureSize();
        if (width > 0 && width <= i && height > 0 && height <= i) {
            this.f_83917_ = width;
            this.f_83918_ = height;
            this.f_83915_ = width;
            this.f_83916_ = height;
            this.f_83920_ = GlStateManager.glGenFramebuffers();
            this.f_83923_ = TextureUtil.generateTextureId();
            this.f_83924_ = TextureUtil.generateTextureId();
            GlStateManager._bindTexture((int)this.f_83924_);
            GlStateManager._texParameter((int)3553, (int)10241, (int)9728);
            GlStateManager._texParameter((int)3553, (int)10240, (int)9728);
            GlStateManager._texParameter((int)3553, (int)34892, (int)0);
            GlStateManager._texParameter((int)3553, (int)10242, (int)33071);
            GlStateManager._texParameter((int)3553, (int)10243, (int)33071);
            if (this.highPrecisionDepth) {
                GlStateManager._texImage2D((int)3553, (int)0, (int)36012, (int)this.f_83915_, (int)this.f_83916_, (int)0, (int)6402, (int)5126, (IntBuffer)null);
            } else {
                GlStateManager._texImage2D((int)3553, (int)0, (int)6402, (int)this.f_83915_, (int)this.f_83916_, (int)0, (int)6402, (int)5126, (IntBuffer)null);
            }
        } else {
            throw new IllegalArgumentException("Window " + width + "x" + height + " size out of bounds (max. size: " + i + ")");
        }
        this.m_83936_(9728);
        GlStateManager._bindTexture((int)this.f_83923_);
        GlStateManager._texParameter((int)3553, (int)10242, (int)33071);
        GlStateManager._texParameter((int)3553, (int)10243, (int)33071);
        GlStateManager._texImage2D((int)3553, (int)0, (int)32856, (int)this.f_83915_, (int)this.f_83916_, (int)0, (int)6408, (int)5121, (IntBuffer)null);
        GlStateManager._glBindFramebuffer((int)36160, (int)this.f_83920_);
        GlStateManager._glFramebufferTexture2D((int)36160, (int)36064, (int)3553, (int)this.f_83923_, (int)0);
        GlStateManager._glFramebufferTexture2D((int)36160, (int)36096, (int)3553, (int)this.f_83924_, (int)0);
        this.m_83949_();
        this.m_83954_(clearErrors);
        this.m_83963_();
    }
}

