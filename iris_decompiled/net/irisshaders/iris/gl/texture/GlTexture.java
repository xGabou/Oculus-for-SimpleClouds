/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  org.lwjgl.system.MemoryUtil
 */
package net.irisshaders.iris.gl.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.function.IntSupplier;
import net.irisshaders.iris.gl.GlResource;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.texture.TextureAccess;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.gl.texture.TextureUploadHelper;
import net.irisshaders.iris.shaderpack.texture.TextureFilteringData;
import org.lwjgl.system.MemoryUtil;

public class GlTexture
extends GlResource
implements TextureAccess {
    private final TextureType target;

    public GlTexture(TextureType target, int sizeX, int sizeY, int sizeZ, int internalFormat, int format, int pixelType, byte[] pixels, TextureFilteringData filteringData) {
        super(GlStateManager._genTexture());
        IrisRenderSystem.bindTextureForSetup(target.getGlType(), this.getGlId());
        TextureUploadHelper.resetTextureUploadState();
        ByteBuffer buffer = MemoryUtil.memAlloc((int)pixels.length);
        buffer.put(pixels);
        buffer.flip();
        target.apply(this.getGlId(), sizeX, sizeY, sizeZ, internalFormat, format, pixelType, buffer);
        MemoryUtil.memFree((Buffer)buffer);
        int texture = this.getGlId();
        IrisRenderSystem.texParameteri(texture, target.getGlType(), 10241, filteringData.shouldBlur() ? 9729 : 9728);
        IrisRenderSystem.texParameteri(texture, target.getGlType(), 10240, filteringData.shouldBlur() ? 9729 : 9728);
        IrisRenderSystem.texParameteri(texture, target.getGlType(), 10242, filteringData.shouldClamp() ? 33071 : 10497);
        if (sizeY > 0) {
            IrisRenderSystem.texParameteri(texture, target.getGlType(), 10243, filteringData.shouldClamp() ? 33071 : 10497);
        }
        if (sizeZ > 0) {
            IrisRenderSystem.texParameteri(texture, target.getGlType(), 32882, filteringData.shouldClamp() ? 33071 : 10497);
        }
        IrisRenderSystem.texParameteri(texture, target.getGlType(), 33085, 0);
        IrisRenderSystem.texParameteri(texture, target.getGlType(), 33082, 0);
        IrisRenderSystem.texParameteri(texture, target.getGlType(), 33083, 0);
        IrisRenderSystem.texParameterf(texture, target.getGlType(), 34049, 0.0f);
        IrisRenderSystem.bindTextureForSetup(target.getGlType(), 0);
        this.target = target;
    }

    public TextureType getTarget() {
        return this.target;
    }

    public void bind(int unit) {
        IrisRenderSystem.bindTextureToUnit(this.target.getGlType(), unit, this.getGlId());
    }

    @Override
    public TextureType getType() {
        return this.target;
    }

    @Override
    public IntSupplier getTextureId() {
        return () -> this.getGlId();
    }

    @Override
    protected void destroyInternal() {
        GlStateManager._deleteTexture((int)this.getGlId());
    }
}

