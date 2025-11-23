/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.platform.TextureUtil
 *  com.mojang.blaze3d.systems.RenderSystem
 *  org.joml.Matrix4f
 */
package dev.nonamecrackers2.simpleclouds.client.framebuffer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.IntBuffer;
import org.joml.Matrix4f;

public class ShadowMapBuffer
implements AutoCloseable {
    private final Matrix4f projMat;
    private final float near;
    private final float far;
    private final int viewWidth;
    private final int viewHeight;
    private final int texWidth;
    private final int texHeight;
    private final boolean hasColor;
    private int bufferId = -1;
    private int depthTextureId = -1;
    private int colorTextureId = -1;

    public ShadowMapBuffer(int viewWidth, int viewHeight, int texWidth, int texHeight, float near, float far, boolean withColor, boolean comparisonDepth) {
        this.projMat = new Matrix4f().setOrtho(0.0f, (float)viewWidth, (float)viewHeight, 0.0f, near, far);
        this.near = near;
        this.far = far;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.texWidth = texHeight;
        this.texHeight = texHeight;
        this.hasColor = withColor;
        this.bufferId = GlStateManager.glGenFramebuffers();
        this.depthTextureId = TextureUtil.generateTextureId();
        GlStateManager._bindTexture((int)this.depthTextureId);
        GlStateManager._texParameter((int)3553, (int)10241, (int)9729);
        GlStateManager._texParameter((int)3553, (int)10240, (int)9729);
        GlStateManager._texParameter((int)3553, (int)10242, (int)33071);
        GlStateManager._texParameter((int)3553, (int)10243, (int)33071);
        if (comparisonDepth) {
            GlStateManager._texParameter((int)3553, (int)34892, (int)34894);
            GlStateManager._texParameter((int)3553, (int)34893, (int)518);
            GlStateManager._texImage2D((int)3553, (int)0, (int)6402, (int)texWidth, (int)texHeight, (int)0, (int)6402, (int)5121, (IntBuffer)null);
        } else {
            GlStateManager._texParameter((int)3553, (int)34892, (int)0);
            GlStateManager._texImage2D((int)3553, (int)0, (int)6402, (int)texWidth, (int)texHeight, (int)0, (int)6402, (int)5126, (IntBuffer)null);
        }
        if (withColor) {
            this.colorTextureId = TextureUtil.generateTextureId();
            GlStateManager._bindTexture((int)this.colorTextureId);
            GlStateManager._texParameter((int)3553, (int)10241, (int)9728);
            GlStateManager._texParameter((int)3553, (int)10240, (int)9728);
            GlStateManager._texParameter((int)3553, (int)34892, (int)0);
            GlStateManager._texParameter((int)3553, (int)10242, (int)33071);
            GlStateManager._texParameter((int)3553, (int)10243, (int)33071);
            GlStateManager._texImage2D((int)3553, (int)0, (int)32849, (int)texWidth, (int)texHeight, (int)0, (int)6407, (int)5126, (IntBuffer)null);
        }
        GlStateManager._glBindFramebuffer((int)36160, (int)this.bufferId);
        GlStateManager._glFramebufferTexture2D((int)36160, (int)36096, (int)3553, (int)this.depthTextureId, (int)0);
        if (withColor) {
            GlStateManager._glFramebufferTexture2D((int)36160, (int)36064, (int)3553, (int)this.colorTextureId, (int)0);
        }
        GlStateManager._glBindFramebuffer((int)36160, (int)0);
        ShadowMapBuffer.checkFrameBufferStatus();
        GlStateManager._bindTexture((int)0);
    }

    public void bind() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glBindFramebuffer((int)36160, (int)this.bufferId);
        GlStateManager._viewport((int)0, (int)0, (int)this.texWidth, (int)this.texHeight);
    }

    public void clear(boolean osx) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._clearDepth((double)1.0);
        int bit = 256;
        if (this.hasColor()) {
            GlStateManager._clearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
            bit |= 0x4000;
        }
        RenderSystem.clear((int)bit, (boolean)osx);
    }

    public void unbind() {
        GlStateManager._glBindFramebuffer((int)36160, (int)0);
    }

    public float getNear() {
        return this.near;
    }

    public float getFar() {
        return this.far;
    }

    public int getViewWidth() {
        return this.viewWidth;
    }

    public int getViewHeight() {
        return this.viewHeight;
    }

    public int getTexWidth() {
        return this.texWidth;
    }

    public int getTexHeight() {
        return this.texHeight;
    }

    public int getFramebufferId() {
        return this.bufferId;
    }

    public int getDepthTexId() {
        return this.depthTextureId;
    }

    public int getColorTexId() {
        if (!this.hasColor()) {
            throw new IllegalStateException("Buffer does not have color");
        }
        return this.colorTextureId;
    }

    public boolean hasColor() {
        return this.hasColor;
    }

    public Matrix4f getProjMatrix() {
        return this.projMat;
    }

    @Override
    public void close() {
        if (this.depthTextureId != -1) {
            TextureUtil.releaseTextureId((int)this.depthTextureId);
            this.depthTextureId = -1;
        }
        if (this.colorTextureId != -1) {
            TextureUtil.releaseTextureId((int)this.colorTextureId);
            this.colorTextureId = -1;
        }
        if (this.bufferId != -1) {
            GlStateManager._glBindFramebuffer((int)36160, (int)0);
            GlStateManager._glDeleteFramebuffers((int)this.bufferId);
            this.bufferId = -1;
        }
    }

    public String toString() {
        return this.getClass().getSimpleName() + "[buffer=" + this.bufferId + ",depth=" + this.depthTextureId + ",color=" + this.colorTextureId + ",width=" + this.texWidth + ",height=" + this.texHeight + "]";
    }

    private static void checkFrameBufferStatus() {
        RenderSystem.assertOnRenderThreadOrInit();
        int i = GlStateManager.glCheckFramebufferStatus((int)36160);
        if (i != 36053) {
            if (i == 36054) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
            }
            if (i == 36055) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
            }
            if (i == 36059) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
            }
            if (i == 36060) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
            }
            if (i == 36061) {
                throw new RuntimeException("GL_FRAMEBUFFER_UNSUPPORTED");
            }
            if (i == 1285) {
                throw new RuntimeException("GL_OUT_OF_MEMORY");
            }
            throw new RuntimeException("checkFramebufferStatus returned unknown status:" + i);
        }
    }
}

