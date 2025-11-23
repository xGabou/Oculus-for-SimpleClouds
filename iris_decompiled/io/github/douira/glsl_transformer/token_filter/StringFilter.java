/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.token_filter;

import io.github.douira.glsl_transformer.ast.transform.JobParameters;
import io.github.douira.glsl_transformer.token_filter.TokenFilter;
import java.util.Set;
import oculus.org.antlr.v4.runtime.Token;

public class StringFilter<J extends JobParameters>
extends TokenFilter<J> {
    private Set<String> disallowed;

    public StringFilter(Set<String> disallowed) {
        this.disallowed = disallowed;
    }

    public StringFilter(String disallowed) {
        this.disallowed = Set.of((Object)disallowed);
    }

    public StringFilter(String ... disallowed) {
        this.disallowed = Set.of((Object[])disallowed);
    }

    @Override
    public boolean isTokenAllowed(Token token) {
        return !this.disallowed.contains(token.getText());
    }
}

