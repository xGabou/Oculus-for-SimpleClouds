/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.gl.texture;

import java.util.Locale;
import java.util.Optional;
import net.irisshaders.iris.gl.GlVersion;

public enum PixelFormat {
    RED(6403, GlVersion.GL_11, false),
    RG(33319, GlVersion.GL_30, false),
    RGB(6407, GlVersion.GL_11, false),
    BGR(32992, GlVersion.GL_12, false),
    RGBA(6408, GlVersion.GL_11, false),
    BGRA(32993, GlVersion.GL_12, false),
    RED_INTEGER(36244, GlVersion.GL_30, true),
    RG_INTEGER(33320, GlVersion.GL_30, true),
    RGB_INTEGER(36248, GlVersion.GL_30, true),
    BGR_INTEGER(36250, GlVersion.GL_30, true),
    RGBA_INTEGER(36249, GlVersion.GL_30, true),
    BGRA_INTEGER(36251, GlVersion.GL_30, true);

    private final int glFormat;
    private final GlVersion minimumGlVersion;
    private final boolean isInteger;

    private PixelFormat(int glFormat, GlVersion minimumGlVersion, boolean isInteger) {
        this.glFormat = glFormat;
        this.minimumGlVersion = minimumGlVersion;
        this.isInteger = isInteger;
    }

    public static Optional<PixelFormat> fromString(String name) {
        try {
            return Optional.of(PixelFormat.valueOf(name.toUpperCase(Locale.US)));
        }
        catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public int getGlFormat() {
        return this.glFormat;
    }

    public GlVersion getMinimumGlVersion() {
        return this.minimumGlVersion;
    }

    public boolean isInteger() {
        return this.isInteger;
    }
}

