/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.parser;

import io.github.douira.glsl_transformer.token_filter.TokenFilter;
import oculus.org.antlr.v4.runtime.CharStream;
import oculus.org.antlr.v4.runtime.Token;
import oculus.org.antlr.v4.runtime.TokenFactory;
import oculus.org.antlr.v4.runtime.TokenSource;

public class FilterTokenSource
implements TokenSource {
    private TokenSource source;
    private TokenFilter<?> filter;

    public FilterTokenSource(TokenSource tokenSource) {
        this.source = tokenSource;
    }

    public void setTokenFilter(TokenFilter<?> tokenFilter) {
        this.filter = tokenFilter;
    }

    public TokenFilter<?> getTokenFilter() {
        return this.filter;
    }

    public void resetState() {
        if (this.filter != null) {
            this.filter.resetState();
        }
    }

    @Override
    public Token nextToken() {
        Token token;
        if (this.source == null) {
            throw new IllegalStateException("Missing a token source but a token was requested!");
        }
        if (this.filter == null) {
            return this.source.nextToken();
        }
        do {
            if ((token = this.source.nextToken()).getType() != -1) continue;
            return token;
        } while (!this.filter.isTokenAllowed(token));
        return token;
    }

    @Override
    public int getLine() {
        return this.source.getLine();
    }

    @Override
    public int getCharPositionInLine() {
        return this.source.getCharPositionInLine();
    }

    @Override
    public CharStream getInputStream() {
        return this.source.getInputStream();
    }

    @Override
    public String getSourceName() {
        return this.source.getSourceName();
    }

    @Override
    public void setTokenFactory(TokenFactory<?> factory) {
        this.source.setTokenFactory(factory);
    }

    @Override
    public TokenFactory<?> getTokenFactory() {
        return this.source.getTokenFactory();
    }
}

