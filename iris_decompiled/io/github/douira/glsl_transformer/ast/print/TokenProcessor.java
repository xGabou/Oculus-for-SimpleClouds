/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.print;

import io.github.douira.glsl_transformer.ast.print.token.PrintToken;

public interface TokenProcessor {
    public String generateString();

    public void appendToken(PrintToken var1);

    public void appendDirectly(String var1);

    public void appendDirectly(char var1);
}

