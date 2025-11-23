/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 */
package net.irisshaders.iris.targets;

import com.mojang.blaze3d.platform.GlStateManager;
import net.irisshaders.iris.gl.GLDebug;
import net.irisshaders.iris.gl.GlResource;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.texture.DepthBufferFormat;

public class DepthTexture
extends GlResource {
    public DepthTexture(String name, int width, int height, DepthBufferFormat format) {
        super(IrisRenderSystem.createTexture(3553));
        int texture = this.getGlId();
        this.resize(width, height, format);
        GLDebug.nameObject(5890, texture, name);
        IrisRenderSystem.texParameteri(texture, 3553, 10241, 9728);
        IrisRenderSystem.texParameteri(texture, 3553, 10240, 9728);
        IrisRenderSystem.texParameteri(texture, 3553, 10242, 33071);
        IrisRenderSystem.texParameteri(texture, 3553, 10243, 33071);
        GlStateManager._bindTexture((int)0);
    }

    void resize(int width, int height, DepthBufferFormat format) {
        IrisRenderSystem.texImage2D(this.getTextureId(), 3553, 0, format.getGlInternalFormat(), width, height, 0, format.getGlType(), format.getGlFormat(), null);
    }

    public int getTextureId() {
        return this.getGlId();
    }

    @Override
    protected void destroyInternal() {
        GlStateManager._deleteTexture((int)this.getGlId());
    }
}

