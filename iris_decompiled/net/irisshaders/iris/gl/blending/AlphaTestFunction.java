/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.gl.blending;

import java.util.Optional;

public enum AlphaTestFunction {
    NEVER(512, null),
    LESS(513, "<"),
    EQUAL(514, "=="),
    LEQUAL(515, "<="),
    GREATER(516, ">"),
    NOTEQUAL(517, "!="),
    GEQUAL(518, ">="),
    ALWAYS(519, null);

    private final int glId;
    private final String expression;

    private AlphaTestFunction(int glFormat, String expression) {
        this.glId = glFormat;
        this.expression = expression;
    }

    public static Optional<AlphaTestFunction> fromGlId(int glId) {
        return switch (glId) {
            case 512 -> Optional.of(NEVER);
            case 513 -> Optional.of(LESS);
            case 514 -> Optional.of(EQUAL);
            case 515 -> Optional.of(LEQUAL);
            case 516 -> Optional.of(GREATER);
            case 517 -> Optional.of(NOTEQUAL);
            case 518 -> Optional.of(GEQUAL);
            case 519 -> Optional.of(ALWAYS);
            default -> Optional.empty();
        };
    }

    public static Optional<AlphaTestFunction> fromString(String name) {
        if ("GL_ALWAYS".equals(name)) {
            return Optional.of(ALWAYS);
        }
        try {
            return Optional.of(AlphaTestFunction.valueOf(name));
        }
        catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public int getGlId() {
        return this.glId;
    }

    public String getExpression() {
        return this.expression;
    }
}

