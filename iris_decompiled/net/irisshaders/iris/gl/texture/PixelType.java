/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.gl.texture;

import java.util.Locale;
import java.util.Optional;
import net.irisshaders.iris.gl.GlVersion;

public enum PixelType {
    BYTE(5120, GlVersion.GL_11),
    SHORT(5122, GlVersion.GL_11),
    INT(5124, GlVersion.GL_11),
    HALF_FLOAT(5131, GlVersion.GL_30),
    FLOAT(5126, GlVersion.GL_11),
    UNSIGNED_BYTE(5121, GlVersion.GL_11),
    UNSIGNED_BYTE_3_3_2(32818, GlVersion.GL_12),
    UNSIGNED_BYTE_2_3_3_REV(33634, GlVersion.GL_12),
    UNSIGNED_SHORT(5123, GlVersion.GL_11),
    UNSIGNED_SHORT_5_6_5(33635, GlVersion.GL_12),
    UNSIGNED_SHORT_5_6_5_REV(33636, GlVersion.GL_12),
    UNSIGNED_SHORT_4_4_4_4(32819, GlVersion.GL_12),
    UNSIGNED_SHORT_4_4_4_4_REV(33637, GlVersion.GL_12),
    UNSIGNED_SHORT_5_5_5_1(32820, GlVersion.GL_12),
    UNSIGNED_SHORT_1_5_5_5_REV(33638, GlVersion.GL_12),
    UNSIGNED_INT(5125, GlVersion.GL_11),
    UNSIGNED_INT_8_8_8_8(32821, GlVersion.GL_12),
    UNSIGNED_INT_8_8_8_8_REV(33639, GlVersion.GL_12),
    UNSIGNED_INT_10_10_10_2(32822, GlVersion.GL_12),
    UNSIGNED_INT_2_10_10_10_REV(33640, GlVersion.GL_12);

    private final int glFormat;
    private final GlVersion minimumGlVersion;

    private PixelType(int glFormat, GlVersion minimumGlVersion) {
        this.glFormat = glFormat;
        this.minimumGlVersion = minimumGlVersion;
    }

    public static Optional<PixelType> fromString(String name) {
        try {
            return Optional.of(PixelType.valueOf(name.toUpperCase(Locale.US)));
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
}

