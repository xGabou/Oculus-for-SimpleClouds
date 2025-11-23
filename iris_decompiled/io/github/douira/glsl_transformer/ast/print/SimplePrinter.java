/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.print;

import io.github.douira.glsl_transformer.ast.print.TokenProcessor;
import io.github.douira.glsl_transformer.ast.print.token.PrintToken;

public class SimplePrinter
implements TokenProcessor {
    private StringBuilder builder = new StringBuilder();

    @Override
    public String generateString() {
        return this.builder.toString();
    }

    @Override
    public void appendToken(PrintToken token) {
        String content = token.getContent();
        if (content != null) {
            this.builder.append(content);
        }
    }

    public StringBuilder getBuilder() {
        return this.builder;
    }

    @Override
    public void appendDirectly(String content) {
        this.builder.append(content);
    }

    @Override
    public void appendDirectly(char content) {
        this.builder.append(content);
    }
}

