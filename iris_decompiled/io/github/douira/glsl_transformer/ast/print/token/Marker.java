/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.print.token;

import io.github.douira.glsl_transformer.ast.print.token.PrintToken;

public class Marker
extends PrintToken {
    @Override
    public String getContent() {
        return null;
    }

    @Override
    public boolean isCommonFormattingNewline() {
        return false;
    }

    @Override
    public boolean endsWithNewline() {
        return false;
    }
}

