/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  it.unimi.dsi.fastutil.ints.Int2IntArrayMap
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 */
package net.irisshaders.iris.gl.framebuffer;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.irisshaders.iris.gl.GlResource;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.texture.DepthBufferFormat;
import net.irisshaders.iris.texture.TextureInfoCache;

public class GlFramebuffer
extends GlResource {
    private final Int2IntMap attachments = new Int2IntArrayMap();
    private final int maxDrawBuffers = GlStateManager._getInteger((int)34852);
    private final int maxColorAttachments = GlStateManager._getInteger((int)36063);
    private boolean hasDepthAttachment = false;

    public GlFramebuffer() {
        super(IrisRenderSystem.createFramebuffer());
    }

    public void addDepthAttachment(int texture) {
        int internalFormat = TextureInfoCache.INSTANCE.getInfo(texture).getInternalFormat();
        DepthBufferFormat depthBufferFormat = DepthBufferFormat.fromGlEnumOrDefault(internalFormat);
        int fb = this.getGlId();
        if (depthBufferFormat.isCombinedStencil()) {
            IrisRenderSystem.framebufferTexture2D(fb, 36160, 33306, 3553, texture, 0);
        } else {
            IrisRenderSystem.framebufferTexture2D(fb, 36160, 36096, 3553, texture, 0);
        }
        this.hasDepthAttachment = true;
    }

    public void addColorAttachment(int index, int texture) {
        int fb = this.getGlId();
        IrisRenderSystem.framebufferTexture2D(fb, 36160, 36064 + index, 3553, texture, 0);
        this.attachments.put(index, texture);
    }

    public void noDrawBuffers() {
        IrisRenderSystem.drawBuffers(this.getGlId(), new int[]{0});
    }

    public void drawBuffers(int[] buffers) {
        int[] glBuffers = new int[buffers.length];
        int index = 0;
        if (buffers.length > this.maxDrawBuffers) {
            throw new IllegalArgumentException("Cannot write to more than " + this.maxDrawBuffers + " draw buffers on this GPU");
        }
        for (int buffer : buffers) {
            if (buffer >= this.maxColorAttachments) {
                throw new IllegalArgumentException("Only " + this.maxColorAttachments + " color attachments are supported on this GPU, but an attempt was made to write to a color attachment with index " + buffer);
            }
            glBuffers[index++] = 36064 + buffer;
        }
        IrisRenderSystem.drawBuffers(this.getGlId(), glBuffers);
    }

    public void readBuffer(int buffer) {
        IrisRenderSystem.readBuffer(this.getGlId(), 36064 + buffer);
    }

    public int getColorAttachment(int index) {
        return this.attachments.get(index);
    }

    public boolean hasDepthAttachment() {
        return this.hasDepthAttachment;
    }

    public void bind() {
        GlStateManager._glBindFramebuffer((int)36160, (int)this.getGlId());
    }

    public void bindAsReadBuffer() {
        GlStateManager._glBindFramebuffer((int)36008, (int)this.getGlId());
    }

    public void bindAsDrawBuffer() {
        GlStateManager._glBindFramebuffer((int)36009, (int)this.getGlId());
    }

    @Override
    protected void destroyInternal() {
        GlStateManager._glDeleteFramebuffers((int)this.getGlId());
    }

    public int getStatus() {
        this.bind();
        return GlStateManager.glCheckFramebufferStatus((int)36160);
    }

    public int getId() {
        return this.getGlId();
    }
}

