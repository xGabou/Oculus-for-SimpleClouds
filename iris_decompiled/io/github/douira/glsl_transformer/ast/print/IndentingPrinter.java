/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.print;

import io.github.douira.glsl_transformer.ast.print.DelegateTokenProcessor;
import io.github.douira.glsl_transformer.ast.print.SimplePrinter;
import io.github.douira.glsl_transformer.ast.print.TokenProcessor;
import io.github.douira.glsl_transformer.ast.print.token.IndentMarker;
import io.github.douira.glsl_transformer.ast.print.token.Marker;
import io.github.douira.glsl_transformer.ast.print.token.PrintToken;

public class IndentingPrinter
extends DelegateTokenProcessor {
    private final char indent;
    private final int indentMultiplier;
    private int indentLevel = 0;
    private boolean indentationPrinted = false;

    public IndentingPrinter(TokenProcessor delegate, char indent, int indentMultiplier) {
        super(delegate);
        this.indent = indent;
        this.indentMultiplier = indentMultiplier;
    }

    public IndentingPrinter(TokenProcessor delegate) {
        this(delegate, '\t', 1);
    }

    public IndentingPrinter() {
        this(new SimplePrinter());
    }

    @Override
    public void appendToken(PrintToken token) {
        if (token instanceof IndentMarker) {
            IndentMarker indentMarker = (IndentMarker)token;
            this.indentLevel += indentMarker.indentDelta;
        } else if (!(token instanceof Marker)) {
            boolean isNewline = token.endsWithNewline();
            if (!this.indentationPrinted && !isNewline) {
                this.indentationPrinted = true;
                if (this.indentLevel > 0) {
                    int repeat = this.indentLevel * this.indentMultiplier;
                    for (int i = 0; i < repeat; ++i) {
                        this.appendDirectly(this.indent);
                    }
                }
            }
            if (isNewline) {
                this.indentationPrinted = false;
            }
            super.appendToken(token);
        }
    }
}

