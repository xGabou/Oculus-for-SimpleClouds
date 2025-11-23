/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.platform.TextureUtil
 *  com.mojang.blaze3d.systems.RenderSystem
 *  org.lwjgl.opengl.GL20
 *  org.lwjgl.opengl.GL30
 */
package dev.nonamecrackers2.simpleclouds.client.framebuffer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class WeightedBlendingTarget
extends RenderTarget {
    private final boolean highPrecisionDepth;
    protected int revealageTextureId;

    public WeightedBlendingTarget(int width, int height, boolean clearError, boolean highPrecisionDepth) {
        super(true);
        RenderSystem.assertOnRenderThreadOrInit();
        this.highPrecisionDepth = highPrecisionDepth;
        this.m_83941_(width, height, clearError);
        this.m_83931_(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public boolean isStencilEnabled() {
        return false;
    }

    public void enableStencil() {
    }

    public void m_83930_() {
        super.m_83930_();
        if (this.revealageTextureId > -1) {
            TextureUtil.releaseTextureId((int)this.revealageTextureId);
            this.revealageTextureId = -1;
        }
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
            this.revealageTextureId = TextureUtil.generateTextureId();
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
        GlStateManager._texImage2D((int)3553, (int)0, (int)34842, (int)this.f_83915_, (int)this.f_83916_, (int)0, (int)6408, (int)5121, (IntBuffer)null);
        GlStateManager._bindTexture((int)this.revealageTextureId);
        GlStateManager._texParameter((int)3553, (int)10242, (int)33071);
        GlStateManager._texParameter((int)3553, (int)10243, (int)33071);
        GlStateManager._texImage2D((int)3553, (int)0, (int)33321, (int)this.f_83915_, (int)this.f_83916_, (int)0, (int)6403, (int)5121, (IntBuffer)null);
        GlStateManager._glBindFramebuffer((int)36160, (int)this.f_83920_);
        GlStateManager._glFramebufferTexture2D((int)36160, (int)36064, (int)3553, (int)this.f_83923_, (int)0);
        GlStateManager._glFramebufferTexture2D((int)36160, (int)36065, (int)3553, (int)this.revealageTextureId, (int)0);
        GlStateManager._glFramebufferTexture2D((int)36160, (int)36096, (int)3553, (int)this.f_83924_, (int)0);
        this.m_83949_();
        this.m_83954_(clearErrors);
        this.m_83963_();
    }

    public void m_83945_(RenderTarget other) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._glBindFramebuffer((int)36008, (int)other.f_83920_);
        GlStateManager._glBindFramebuffer((int)36009, (int)this.f_83920_);
        GlStateManager._glBlitFrameBuffer((int)0, (int)0, (int)other.f_83915_, (int)other.f_83916_, (int)0, (int)0, (int)this.f_83915_, (int)this.f_83916_, (int)256, (int)9728);
        GlStateManager._glBindFramebuffer((int)36160, (int)0);
    }

    public void m_83936_(int mode) {
        super.m_83936_(mode);
        GlStateManager._bindTexture((int)this.revealageTextureId);
        GlStateManager._texParameter((int)3553, (int)10241, (int)mode);
        GlStateManager._texParameter((int)3553, (int)10240, (int)mode);
        GlStateManager._bindTexture((int)0);
    }

    public void m_83947_(boolean viewport) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.m_83961_(viewport));
        } else {
            this.m_83961_(viewport);
        }
    }

    private void m_83961_(boolean viewport) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._glBindFramebuffer((int)36160, (int)this.f_83920_);
        if (viewport) {
            GlStateManager._viewport((int)0, (int)0, (int)this.f_83917_, (int)this.f_83918_);
        }
        GL20.glDrawBuffers((int[])new int[]{36064, 36065});
    }

    public void m_83954_(boolean clearErrors) {
        RenderSystem.assertOnRenderThreadOrInit();
        this.m_83947_(true);
        GL30.glClearBufferfv((int)6144, (int)0, (float[])new float[]{0.0f, 0.0f, 0.0f, 0.0f});
        GL30.glClearBufferfv((int)6144, (int)1, (float[])new float[]{1.0f, 0.0f, 0.0f, 0.0f});
        GL30.glClearBufferfv((int)6145, (int)0, (float[])new float[]{1.0f});
        this.m_83970_();
    }

    public int getRevealageTextureId() {
        return this.revealageTextureId;
    }
}

