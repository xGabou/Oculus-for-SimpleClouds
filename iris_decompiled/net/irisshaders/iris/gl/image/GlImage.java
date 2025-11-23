/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  org.lwjgl.opengl.ARBClearTexture
 */
package net.irisshaders.iris.gl.image;

import com.mojang.blaze3d.platform.GlStateManager;
import net.irisshaders.iris.gl.GLDebug;
import net.irisshaders.iris.gl.GlResource;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.texture.InternalTextureFormat;
import net.irisshaders.iris.gl.texture.PixelFormat;
import net.irisshaders.iris.gl.texture.PixelType;
import net.irisshaders.iris.gl.texture.TextureType;
import org.lwjgl.opengl.ARBClearTexture;

public class GlImage
extends GlResource {
    protected final String name;
    protected final String samplerName;
    protected final TextureType target;
    protected final PixelFormat format;
    protected final InternalTextureFormat internalTextureFormat;
    protected final PixelType pixelType;
    private final boolean clear;

    public GlImage(String name, String samplerName, TextureType target, PixelFormat format, InternalTextureFormat internalFormat, PixelType pixelType, boolean clear, int width, int height, int depth) {
        super(IrisRenderSystem.createTexture(target.getGlType()));
        this.name = name;
        this.samplerName = samplerName;
        this.target = target;
        this.format = format;
        this.internalTextureFormat = internalFormat;
        this.pixelType = pixelType;
        this.clear = clear;
        GLDebug.nameObject(5890, this.getGlId(), name);
        IrisRenderSystem.bindTextureForSetup(target.getGlType(), this.getGlId());
        target.apply(this.getGlId(), width, height, depth, internalFormat.getGlFormat(), format.getGlFormat(), pixelType.getGlFormat(), null);
        int texture = this.getGlId();
        this.setup(texture, width, height, depth);
        IrisRenderSystem.bindTextureForSetup(target.getGlType(), 0);
    }

    protected void setup(int texture, int width, int height, int depth) {
        boolean isInteger = this.internalTextureFormat.getPixelFormat().isInteger();
        IrisRenderSystem.texParameteri(texture, this.target.getGlType(), 10241, isInteger ? 9728 : 9729);
        IrisRenderSystem.texParameteri(texture, this.target.getGlType(), 10240, isInteger ? 9728 : 9729);
        IrisRenderSystem.texParameteri(texture, this.target.getGlType(), 10242, 33071);
        if (height > 0) {
            IrisRenderSystem.texParameteri(texture, this.target.getGlType(), 10243, 33071);
        }
        if (depth > 0) {
            IrisRenderSystem.texParameteri(texture, this.target.getGlType(), 32882, 33071);
        }
        IrisRenderSystem.texParameteri(texture, this.target.getGlType(), 33085, 0);
        IrisRenderSystem.texParameteri(texture, this.target.getGlType(), 33082, 0);
        IrisRenderSystem.texParameteri(texture, this.target.getGlType(), 33083, 0);
        IrisRenderSystem.texParameterf(texture, this.target.getGlType(), 34049, 0.0f);
        ARBClearTexture.glClearTexImage((int)texture, (int)0, (int)this.format.getGlFormat(), (int)this.pixelType.getGlFormat(), (int[])null);
    }

    public String getName() {
        return this.name;
    }

    public String getSamplerName() {
        return this.samplerName;
    }

    public TextureType getTarget() {
        return this.target;
    }

    public boolean shouldClear() {
        return this.clear;
    }

    public int getId() {
        return this.getGlId();
    }

    public void updateNewSize(int width, int height) {
    }

    @Override
    protected void destroyInternal() {
        GlStateManager._deleteTexture((int)this.getGlId());
    }

    public InternalTextureFormat getInternalFormat() {
        return this.internalTextureFormat;
    }

    public String toString() {
        return "GlImage name " + this.name + " format " + this.format + "internalformat " + this.internalTextureFormat + " pixeltype " + this.pixelType;
    }

    public PixelFormat getFormat() {
        return this.format;
    }

    public PixelType getPixelType() {
        return this.pixelType;
    }

    public static class Relative
    extends GlImage {
        private final float relativeHeight;
        private final float relativeWidth;

        public Relative(String name, String samplerName, PixelFormat format, InternalTextureFormat internalFormat, PixelType pixelType, boolean clear, float relativeWidth, float relativeHeight, int currentWidth, int currentHeight) {
            super(name, samplerName, TextureType.TEXTURE_2D, format, internalFormat, pixelType, clear, (int)((float)currentWidth * relativeWidth), (int)((float)currentHeight * relativeHeight), 0);
            this.relativeWidth = relativeWidth;
            this.relativeHeight = relativeHeight;
        }

        @Override
        public void updateNewSize(int width, int height) {
            IrisRenderSystem.bindTextureForSetup(this.target.getGlType(), this.getGlId());
            this.target.apply(this.getGlId(), (int)((float)width * this.relativeWidth), (int)((float)height * this.relativeHeight), 0, this.internalTextureFormat.getGlFormat(), this.format.getGlFormat(), this.pixelType.getGlFormat(), null);
            int texture = this.getGlId();
            this.setup(texture, width, height, 0);
            IrisRenderSystem.bindTextureForSetup(this.target.getGlType(), 0);
        }
    }
}

