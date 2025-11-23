/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node;

import io.github.douira.glsl_transformer.ast.data.TokenTyped;
import io.github.douira.glsl_transformer.ast.data.TypeUtil;
import oculus.org.antlr.v4.runtime.Token;

public enum Profile implements TokenTyped
{
    CORE(291),
    COMPATIBILITY(292),
    ES(293);

    public final int tokenType;

    private Profile(int tokenType) {
        this.tokenType = tokenType;
    }

    public boolean isCore() {
        return this == CORE;
    }

    public boolean isCompatibility() {
        return this == COMPATIBILITY;
    }

    @Override
    public int getTokenType() {
        return this.tokenType;
    }

    public static Profile fromToken(Token token) {
        return (Profile)TypeUtil.enumFromToken((TokenTyped[])Profile.values(), (Token)token);
    }
}

