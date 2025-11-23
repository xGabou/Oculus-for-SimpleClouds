/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  org.lwjgl.BufferUtils
 */
package net.irisshaders.iris.targets.backed;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import net.irisshaders.iris.gl.GLDebug;
import net.irisshaders.iris.gl.GlResource;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.texture.TextureUploadHelper;
import org.lwjgl.BufferUtils;

public class SingleColorTexture
extends GlResource {
    public SingleColorTexture(int red, int green, int blue, int alpha) {
        super(IrisRenderSystem.createTexture(3553));
        ByteBuffer pixel = BufferUtils.createByteBuffer((int)4);
        pixel.put((byte)red);
        pixel.put((byte)green);
        pixel.put((byte)blue);
        pixel.put((byte)alpha);
        pixel.position(0);
        int texture = this.getGlId();
        GLDebug.nameObject(5890, texture, "single color (" + red + ", " + green + "," + blue + "," + alpha + ")");
        IrisRenderSystem.texParameteri(texture, 3553, 10241, 9729);
        IrisRenderSystem.texParameteri(texture, 3553, 10240, 9729);
        IrisRenderSystem.texParameteri(texture, 3553, 10242, 10497);
        IrisRenderSystem.texParameteri(texture, 3553, 10243, 10497);
        TextureUploadHelper.resetTextureUploadState();
        IrisRenderSystem.texImage2D(texture, 3553, 0, 6408, 1, 1, 0, 6408, 5121, pixel);
    }

    public int getTextureId() {
        return this.getGlId();
    }

    @Override
    protected void destroyInternal() {
        GlStateManager._deleteTexture((int)this.getGlId());
    }
}

