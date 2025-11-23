/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.print.token;

import io.github.douira.glsl_transformer.ast.print.token.ParserToken;
import io.github.douira.glsl_transformer.token_filter.TokenChannel;

public class EOFToken
extends ParserToken {
    public EOFToken() {
        super(TokenChannel.HIDDEN, -1);
    }

    @Override
    public String getContent() {
        return "";
    }
}

