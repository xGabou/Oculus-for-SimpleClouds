/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.gl.texture;

import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public enum DepthBufferFormat {
    DEPTH(false),
    DEPTH16(false),
    DEPTH24(false),
    DEPTH32(false),
    DEPTH32F(false),
    DEPTH_STENCIL(true),
    DEPTH24_STENCIL8(true),
    DEPTH32F_STENCIL8(true);

    private final boolean combinedStencil;

    private DepthBufferFormat(boolean combinedStencil) {
        this.combinedStencil = combinedStencil;
    }

    @Nullable
    public static DepthBufferFormat fromGlEnum(int glenum) {
        return switch (glenum) {
            case 6402 -> DEPTH;
            case 33189 -> DEPTH16;
            case 33190 -> DEPTH24;
            case 33191 -> DEPTH32;
            case 36012 -> DEPTH32F;
            case 34041 -> DEPTH_STENCIL;
            case 35056 -> DEPTH24_STENCIL8;
            case 36013 -> DEPTH32F_STENCIL8;
            default -> null;
        };
    }

    public static DepthBufferFormat fromGlEnumOrDefault(int glenum) {
        DepthBufferFormat format = DepthBufferFormat.fromGlEnum(glenum);
        return (DepthBufferFormat)((Object)Objects.requireNonNullElse((Object)((Object)format), (Object)((Object)DEPTH)));
    }

    public int getGlInternalFormat() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case DEPTH -> 6402;
            case DEPTH16 -> 33189;
            case DEPTH24 -> 33190;
            case DEPTH32 -> 33191;
            case DEPTH32F -> 36012;
            case DEPTH_STENCIL -> 34041;
            case DEPTH24_STENCIL8 -> 35056;
            case DEPTH32F_STENCIL8 -> 36013;
        };
    }

    public int getGlType() {
        return this.isCombinedStencil() ? 34041 : 6402;
    }

    public int getGlFormat() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case DEPTH, DEPTH16 -> 5123;
            case DEPTH24, DEPTH32 -> 5125;
            case DEPTH32F -> 5126;
            case DEPTH_STENCIL, DEPTH24_STENCIL8 -> 34042;
            case DEPTH32F_STENCIL8 -> 36269;
        };
    }

    public boolean isCombinedStencil() {
        return this.combinedStencil;
    }
}

