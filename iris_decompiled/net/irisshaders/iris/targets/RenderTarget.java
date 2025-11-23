/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  org.joml.Vector2i
 */
package net.irisshaders.iris.targets;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import net.irisshaders.iris.gl.GLDebug;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.texture.InternalTextureFormat;
import net.irisshaders.iris.gl.texture.PixelFormat;
import net.irisshaders.iris.gl.texture.PixelType;
import org.joml.Vector2i;

public class RenderTarget {
    private static final ByteBuffer NULL_BUFFER = null;
    private final InternalTextureFormat internalFormat;
    private final PixelFormat format;
    private final PixelType type;
    private final int mainTexture;
    private final int altTexture;
    private int width;
    private int height;
    private boolean isValid = true;

    public RenderTarget(Builder builder) {
        this.internalFormat = builder.internalFormat;
        this.format = builder.format;
        this.type = builder.type;
        this.width = builder.width;
        this.height = builder.height;
        int[] textures = new int[2];
        GlStateManager._genTextures((int[])textures);
        this.mainTexture = textures[0];
        this.altTexture = textures[1];
        boolean isPixelFormatInteger = builder.internalFormat.getPixelFormat().isInteger();
        this.setupTexture(this.mainTexture, builder.width, builder.height, !isPixelFormatInteger);
        this.setupTexture(this.altTexture, builder.width, builder.height, !isPixelFormatInteger);
        if (builder.name != null) {
            GLDebug.nameObject(5890, this.mainTexture, builder.name + " main");
            GLDebug.nameObject(5890, this.mainTexture, builder.name + " alt");
        }
        GlStateManager._bindTexture((int)0);
    }

    public static Builder builder() {
        return new Builder();
    }

    private void setupTexture(int texture, int width, int height, boolean allowsLinear) {
        this.resizeTexture(texture, width, height);
        IrisRenderSystem.texParameteri(texture, 3553, 10241, allowsLinear ? 9729 : 9728);
        IrisRenderSystem.texParameteri(texture, 3553, 10240, allowsLinear ? 9729 : 9728);
        IrisRenderSystem.texParameteri(texture, 3553, 10242, 33071);
        IrisRenderSystem.texParameteri(texture, 3553, 10243, 33071);
    }

    private void resizeTexture(int texture, int width, int height) {
        IrisRenderSystem.texImage2D(texture, 3553, 0, this.internalFormat.getGlFormat(), width, height, 0, this.format.getGlFormat(), this.type.getGlFormat(), NULL_BUFFER);
    }

    void resize(Vector2i textureScaleOverride) {
        this.resize(textureScaleOverride.x, textureScaleOverride.y);
    }

    void resize(int width, int height) {
        this.requireValid();
        this.width = width;
        this.height = height;
        this.resizeTexture(this.mainTexture, width, height);
        this.resizeTexture(this.altTexture, width, height);
    }

    public InternalTextureFormat getInternalFormat() {
        return this.internalFormat;
    }

    public int getMainTexture() {
        this.requireValid();
        return this.mainTexture;
    }

    public int getAltTexture() {
        this.requireValid();
        return this.altTexture;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void destroy() {
        this.requireValid();
        this.isValid = false;
        GlStateManager._deleteTextures((int[])new int[]{this.mainTexture, this.altTexture});
    }

    private void requireValid() {
        if (!this.isValid) {
            throw new IllegalStateException("Attempted to use a deleted composite render target");
        }
    }

    public static class Builder {
        private InternalTextureFormat internalFormat = InternalTextureFormat.RGBA8;
        private int width = 0;
        private int height = 0;
        private PixelFormat format = PixelFormat.RGBA;
        private PixelType type = PixelType.UNSIGNED_BYTE;
        private String name = null;

        private Builder() {
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setInternalFormat(InternalTextureFormat format) {
            this.internalFormat = format;
            return this;
        }

        public Builder setDimensions(int width, int height) {
            if (width <= 0) {
                throw new IllegalArgumentException("Width must be greater than zero");
            }
            if (height <= 0) {
                throw new IllegalArgumentException("Height must be greater than zero");
            }
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder setPixelFormat(PixelFormat pixelFormat) {
            this.format = pixelFormat;
            return this;
        }

        public Builder setPixelType(PixelType pixelType) {
            this.type = pixelType;
            return this;
        }

        public RenderTarget build() {
            return new RenderTarget(this);
        }
    }
}

