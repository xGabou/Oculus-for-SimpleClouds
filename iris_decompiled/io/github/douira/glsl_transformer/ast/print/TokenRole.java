/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.print;

public enum TokenRole {
    EXACT,
    EXTENDABLE_SPACE,
    BREAKABLE_SPACE,
    COMMON_FORMATTING;

    public static final TokenRole DEFAULT;

    static {
        DEFAULT = EXACT;
    }
}

