/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.gl.blending;

import java.util.Optional;
import net.irisshaders.iris.Iris;

public enum BlendModeFunction {
    ZERO(0),
    ONE(1),
    SRC_COLOR(768),
    ONE_MINUS_SRC_COLOR(769),
    DST_COLOR(774),
    ONE_MINUS_DST_COLOR(775),
    SRC_ALPHA(770),
    ONE_MINUS_SRC_ALPHA(771),
    DST_ALPHA(772),
    ONE_MINUS_DST_ALPHA(773),
    SRC_ALPHA_SATURATE(776);

    private final int glId;

    private BlendModeFunction(int glFormat) {
        this.glId = glFormat;
    }

    public static Optional<BlendModeFunction> fromString(String name) {
        try {
            return Optional.of(BlendModeFunction.valueOf(name));
        }
        catch (IllegalArgumentException e) {
            Iris.logger.warn("Invalid blend mode! " + name);
            return Optional.empty();
        }
    }

    public int getGlId() {
        return this.glId;
    }
}

