/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.gl.texture;

import java.util.Locale;
import java.util.Optional;
import net.irisshaders.iris.gl.GlVersion;
import net.irisshaders.iris.gl.texture.PixelFormat;

public enum InternalTextureFormat {
    RGBA(6408, GlVersion.GL_11, PixelFormat.RGBA),
    R8(33321, GlVersion.GL_30, PixelFormat.RED),
    RG8(33323, GlVersion.GL_30, PixelFormat.RG),
    RGB8(32849, GlVersion.GL_11, PixelFormat.RGB),
    RGBA8(32856, GlVersion.GL_11, PixelFormat.RGBA),
    R8_SNORM(36756, GlVersion.GL_31, PixelFormat.RED),
    RG8_SNORM(36757, GlVersion.GL_31, PixelFormat.RG),
    RGB8_SNORM(36758, GlVersion.GL_31, PixelFormat.RGB),
    RGBA8_SNORM(36759, GlVersion.GL_31, PixelFormat.RGBA),
    R16(33322, GlVersion.GL_30, PixelFormat.RED),
    RG16(33324, GlVersion.GL_30, PixelFormat.RG),
    RGB16(32852, GlVersion.GL_11, PixelFormat.RGB),
    RGBA16(32859, GlVersion.GL_11, PixelFormat.RGBA),
    R16_SNORM(36760, GlVersion.GL_31, PixelFormat.RED),
    RG16_SNORM(36761, GlVersion.GL_31, PixelFormat.RG),
    RGB16_SNORM(36762, GlVersion.GL_31, PixelFormat.RGB),
    RGBA16_SNORM(36763, GlVersion.GL_31, PixelFormat.RGBA),
    R16F(33325, GlVersion.GL_30, PixelFormat.RED),
    RG16F(33327, GlVersion.GL_30, PixelFormat.RG),
    RGB16F(34843, GlVersion.GL_30, PixelFormat.RGB),
    RGBA16F(34842, GlVersion.GL_30, PixelFormat.RGBA),
    R32F(33326, GlVersion.GL_30, PixelFormat.RED),
    RG32F(33328, GlVersion.GL_30, PixelFormat.RG),
    RGB32F(34837, GlVersion.GL_30, PixelFormat.RGB),
    RGBA32F(34836, GlVersion.GL_30, PixelFormat.RGBA),
    R8I(33329, GlVersion.GL_30, PixelFormat.RED_INTEGER),
    RG8I(33335, GlVersion.GL_30, PixelFormat.RG_INTEGER),
    RGB8I(36239, GlVersion.GL_30, PixelFormat.RGB_INTEGER),
    RGBA8I(36238, GlVersion.GL_30, PixelFormat.RGBA_INTEGER),
    R8UI(33330, GlVersion.GL_30, PixelFormat.RED_INTEGER),
    RG8UI(33336, GlVersion.GL_30, PixelFormat.RG_INTEGER),
    RGB8UI(36221, GlVersion.GL_30, PixelFormat.RGB_INTEGER),
    RGBA8UI(36220, GlVersion.GL_30, PixelFormat.RGBA_INTEGER),
    R16I(33331, GlVersion.GL_30, PixelFormat.RED_INTEGER),
    RG16I(33337, GlVersion.GL_30, PixelFormat.RG_INTEGER),
    RGB16I(36233, GlVersion.GL_30, PixelFormat.RGB_INTEGER),
    RGBA16I(36232, GlVersion.GL_30, PixelFormat.RGBA_INTEGER),
    R16UI(33332, GlVersion.GL_30, PixelFormat.RED_INTEGER),
    RG16UI(33338, GlVersion.GL_30, PixelFormat.RG_INTEGER),
    RGB16UI(36215, GlVersion.GL_30, PixelFormat.RGB_INTEGER),
    RGBA16UI(36214, GlVersion.GL_30, PixelFormat.RGBA_INTEGER),
    R32I(33333, GlVersion.GL_30, PixelFormat.RED_INTEGER),
    RG32I(33339, GlVersion.GL_30, PixelFormat.RG_INTEGER),
    RGB32I(36227, GlVersion.GL_30, PixelFormat.RGB_INTEGER),
    RGBA32I(36226, GlVersion.GL_30, PixelFormat.RGBA_INTEGER),
    R32UI(33334, GlVersion.GL_30, PixelFormat.RED_INTEGER),
    RG32UI(33340, GlVersion.GL_30, PixelFormat.RG_INTEGER),
    RGB32UI(36209, GlVersion.GL_30, PixelFormat.RGB_INTEGER),
    RGBA32UI(36208, GlVersion.GL_30, PixelFormat.RGBA_INTEGER),
    R3_G3_B2(10768, GlVersion.GL_11, PixelFormat.RGB),
    RGB5_A1(32855, GlVersion.GL_11, PixelFormat.RGBA),
    RGB10_A2(32857, GlVersion.GL_11, PixelFormat.RGBA),
    R11F_G11F_B10F(35898, GlVersion.GL_30, PixelFormat.RGB),
    RGB9_E5(35901, GlVersion.GL_30, PixelFormat.RGB);

    private final int glFormat;
    private final GlVersion minimumGlVersion;
    private final PixelFormat expectedPixelFormat;

    private InternalTextureFormat(int glFormat, GlVersion minimumGlVersion, PixelFormat expectedPixelFormat) {
        this.glFormat = glFormat;
        this.minimumGlVersion = minimumGlVersion;
        this.expectedPixelFormat = expectedPixelFormat;
    }

    public static Optional<InternalTextureFormat> fromString(String name) {
        try {
            return Optional.of(InternalTextureFormat.valueOf(name.toUpperCase(Locale.US)));
        }
        catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public int getGlFormat() {
        return this.glFormat;
    }

    public PixelFormat getPixelFormat() {
        return this.expectedPixelFormat;
    }

    public GlVersion getMinimumGlVersion() {
        return this.minimumGlVersion;
    }
}

