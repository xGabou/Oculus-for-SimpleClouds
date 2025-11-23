/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.gl.texture;

import java.nio.ByteBuffer;
import java.util.Optional;
import net.irisshaders.iris.gl.IrisRenderSystem;

public enum TextureType {
    TEXTURE_1D(3552),
    TEXTURE_2D(3553),
    TEXTURE_3D(32879),
    TEXTURE_RECTANGLE(32879);

    private final int glType;

    private TextureType(int glType) {
        this.glType = glType;
    }

    public static Optional<TextureType> fromString(String name) {
        try {
            return Optional.of(TextureType.valueOf(name));
        }
        catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public int getGlType() {
        return this.glType;
    }

    public void apply(int texture, int sizeX, int sizeY, int sizeZ, int internalFormat, int format, int pixelType, ByteBuffer pixels) {
        switch (this) {
            case TEXTURE_1D: {
                IrisRenderSystem.texImage1D(texture, this.getGlType(), 0, internalFormat, sizeX, 0, format, pixelType, pixels);
                break;
            }
            case TEXTURE_2D: 
            case TEXTURE_RECTANGLE: {
                IrisRenderSystem.texImage2D(texture, this.getGlType(), 0, internalFormat, sizeX, sizeY, 0, format, pixelType, pixels);
                break;
            }
            case TEXTURE_3D: {
                IrisRenderSystem.texImage3D(texture, this.getGlType(), 0, internalFormat, sizeX, sizeY, sizeZ, 0, format, pixelType, pixels);
            }
        }
    }
}

