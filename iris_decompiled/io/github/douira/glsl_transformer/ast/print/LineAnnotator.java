/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.print;

import io.github.douira.glsl_transformer.ast.print.DelegateTokenProcessor;
import io.github.douira.glsl_transformer.ast.print.TokenProcessor;
import io.github.douira.glsl_transformer.ast.print.token.LineDirectiveMarker;
import io.github.douira.glsl_transformer.ast.print.token.LiteralToken;
import io.github.douira.glsl_transformer.ast.print.token.PrintToken;
import io.github.douira.glsl_transformer.ast.transform.SourceLocation;

public class LineAnnotator
extends DelegateTokenProcessor {
    public LineAnnotator(TokenProcessor delegate) {
        super(delegate);
    }

    @Override
    public void appendToken(PrintToken token) {
        if (token instanceof LineDirectiveMarker) {
            LineDirectiveMarker lineDirectiveMarker = (LineDirectiveMarker)token;
            SourceLocation location = lineDirectiveMarker.location;
            super.appendToken(new LiteralToken("#line "));
            super.appendToken(new LiteralToken(Integer.toString(location.line)));
            if (location.hasSource()) {
                super.appendToken(new LiteralToken(" "));
                super.appendToken(new LiteralToken(Integer.toString(location.source)));
            }
            super.appendToken(new LiteralToken("\n"));
        } else {
            super.appendToken(token);
        }
    }
}

