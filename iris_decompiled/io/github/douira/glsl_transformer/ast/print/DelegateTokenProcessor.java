/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.print;

import io.github.douira.glsl_transformer.ast.print.TokenProcessor;
import io.github.douira.glsl_transformer.ast.print.token.PrintToken;

public abstract class DelegateTokenProcessor
implements TokenProcessor {
    protected final TokenProcessor delegate;

    public DelegateTokenProcessor(TokenProcessor delegate) {
        this.delegate = delegate;
    }

    @Override
    public String generateString() {
        return this.delegate.generateString();
    }

    @Override
    public void appendToken(PrintToken token) {
        this.delegate.appendToken(token);
    }

    @Override
    public void appendDirectly(String content) {
        this.delegate.appendDirectly(content);
    }

    @Override
    public void appendDirectly(char content) {
        this.delegate.appendDirectly(content);
    }
}

