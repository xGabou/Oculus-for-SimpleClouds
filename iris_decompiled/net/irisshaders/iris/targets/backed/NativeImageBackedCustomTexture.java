/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.renderer.texture.DynamicTexture
 */
package net.irisshaders.iris.targets.backed;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.IntSupplier;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.texture.TextureAccess;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.shaderpack.texture.CustomTextureData;
import net.minecraft.client.renderer.texture.DynamicTexture;

public class NativeImageBackedCustomTexture
extends DynamicTexture
implements TextureAccess {
    public NativeImageBackedCustomTexture(CustomTextureData.PngData textureData) throws IOException {
        super(NativeImageBackedCustomTexture.create(textureData.getContent()));
        if (textureData.getFilteringData().shouldBlur()) {
            IrisRenderSystem.texParameteri(this.m_117963_(), 3553, 10241, 9729);
            IrisRenderSystem.texParameteri(this.m_117963_(), 3553, 10240, 9729);
        }
        if (textureData.getFilteringData().shouldClamp()) {
            IrisRenderSystem.texParameteri(this.m_117963_(), 3553, 10242, 33071);
            IrisRenderSystem.texParameteri(this.m_117963_(), 3553, 10243, 33071);
        }
    }

    private static NativeImage create(byte[] content) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocateDirect(content.length);
        buffer.put(content);
        buffer.flip();
        return NativeImage.m_85062_((ByteBuffer)buffer);
    }

    public void m_117985_() {
        NativeImage image = Objects.requireNonNull(this.m_117991_());
        this.m_117966_();
        image.m_85013_(0, 0, 0, 0, 0, image.m_84982_(), image.m_85084_(), false, false, false, false);
    }

    @Override
    public TextureType getType() {
        return TextureType.TEXTURE_2D;
    }

    @Override
    public IntSupplier getTextureId() {
        return () -> ((NativeImageBackedCustomTexture)this).m_117963_();
    }
}

