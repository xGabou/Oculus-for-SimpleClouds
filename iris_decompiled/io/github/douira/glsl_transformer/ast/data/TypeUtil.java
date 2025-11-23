/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.data;

import io.github.douira.glsl_transformer.ast.data.TokenTyped;
import oculus.org.antlr.v4.runtime.Token;

public class TypeUtil {
    public static <E extends TokenTyped> E enumFromToken(E[] enumValues, Token token) {
        for (E value : enumValues) {
            if (value.getTokenType() != token.getType()) continue;
            return value;
        }
        throw new IllegalArgumentException("Unknown token: " + token.getText());
    }

    public static <E> E enumFromToken(E[] values, int[] tokenTypes, Token token) {
        if (values.length != tokenTypes.length) {
            throw new IllegalArgumentException("values.length != tokenTypes.length");
        }
        for (int i = 0; i < values.length; ++i) {
            if (tokenTypes[i] != token.getType()) continue;
            return values[i];
        }
        throw new IllegalArgumentException("Unknown token: " + token.getText());
    }
}

