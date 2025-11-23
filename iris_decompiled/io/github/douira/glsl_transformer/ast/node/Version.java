/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node;

import io.github.douira.glsl_transformer.ast.data.TokenTyped;
import io.github.douira.glsl_transformer.ast.data.TypeUtil;
import oculus.org.antlr.v4.runtime.Token;

public enum Version implements TokenTyped
{
    GLSLES10(296, 100, true),
    GLSLES30(301, 300, true),
    GLSLES31(302, 310, true),
    GLSLES32(303, 320, true),
    GLSL11(294, 110),
    GLSL12(295, 120),
    GLSL13(297, 130),
    GLSL14(298, 140),
    GLSL15(299, 150),
    GLSL33(300, 330),
    GLSL40(304, 400),
    GLSL41(305, 410),
    GLSL42(306, 420),
    GLSL43(307, 430),
    GLSL44(308, 440),
    GLSL45(309, 450),
    GLSL46(310, 460);

    public static Version latest;
    public final int tokenType;
    public final int number;
    public final boolean es;

    private Version(int tokenType, int number, boolean es) {
        this.tokenType = tokenType;
        this.number = number;
        this.es = es;
    }

    private Version(int tokenType, int version) {
        this(tokenType, version, false);
    }

    @Override
    public int getTokenType() {
        return this.tokenType;
    }

    public static Version fromToken(Token token) {
        return (Version)TypeUtil.enumFromToken((TokenTyped[])Version.values(), (Token)token);
    }

    public static Version fromNumber(int number) {
        for (Version version : Version.values()) {
            if (version.number != number) continue;
            return version;
        }
        throw new IllegalArgumentException("Unknown version: " + number);
    }

    static {
        latest = GLSL46;
    }
}

