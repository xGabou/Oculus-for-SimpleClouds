/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.token_filter;

import io.github.douira.glsl_transformer.ast.transform.JobParameters;
import io.github.douira.glsl_transformer.token_filter.TokenFilter;
import oculus.org.antlr.v4.runtime.Token;

public class NewlineFilter<J extends JobParameters>
extends TokenFilter<J> {
    private boolean lastWasNewline;

    @Override
    public void resetState() {
        super.resetState();
        this.lastWasNewline = false;
    }

    @Override
    public boolean isTokenAllowed(Token token) {
        boolean isNewline = token.getType() == 271;
        boolean allowToken = !this.lastWasNewline || !isNewline;
        this.lastWasNewline = isNewline;
        return allowToken;
    }
}

