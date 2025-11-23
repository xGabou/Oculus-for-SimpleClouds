/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.query.match;

import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.query.match.HintedMatcher;
import io.github.douira.glsl_transformer.parser.ParseShape;

public class AutoHintedMatcher<N extends ASTNode>
extends HintedMatcher<N> {
    public AutoHintedMatcher(N pattern, String wildcardPrefix) {
        super(pattern, wildcardPrefix, null);
    }

    public AutoHintedMatcher(N pattern) {
        super(pattern, null);
    }

    public AutoHintedMatcher(String input, ParseShape<?, N> parseShape, String wildcardPrefix) {
        super(input, parseShape, wildcardPrefix, null);
    }

    public AutoHintedMatcher(String input, ParseShape<?, N> parseShape) {
        super(input, parseShape, null);
    }

    private void determineHint() {
        this.preparePatternItems();
        String longestHint = null;
        int hintLength = 0;
        for (Object item : this.patternItems) {
            Identifier id;
            String idContent;
            if (!(item instanceof Identifier) || (idContent = (id = (Identifier)item).getName()).length() <= hintLength || this.wildcardPrefix != null && idContent.startsWith(this.wildcardPrefix)) continue;
            longestHint = idContent;
            hintLength = idContent.length();
        }
        if (longestHint == null) {
            throw new IllegalArgumentException("The provided pattern must contain a non-wildcard identifier to use as the hint!");
        }
        this.hint = longestHint;
    }

    @Override
    public String getHint() {
        if (this.hint == null) {
            this.determineHint();
        }
        return super.getHint();
    }
}

