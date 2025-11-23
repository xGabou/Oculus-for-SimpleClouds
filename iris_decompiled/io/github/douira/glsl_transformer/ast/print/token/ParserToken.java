/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.print.token;

import io.github.douira.glsl_transformer.GLSLLexer;
import io.github.douira.glsl_transformer.ast.print.TokenRole;
import io.github.douira.glsl_transformer.ast.print.token.PrintToken;
import io.github.douira.glsl_transformer.token_filter.TokenChannel;
import java.util.HashMap;
import java.util.Map;

public class ParserToken
extends PrintToken {
    private static final Map<Integer, String> missingTokenStrings = new HashMap<Integer, String>(){
        {
            this.put(279, "invariant");
            this.put(274, "custom");
            this.put(13, "invariant");
            this.put(239, "(");
            this.put(288, "(");
            this.put(240, ")");
            this.put(289, ")");
            this.put(263, "#\n");
            this.put(1, ":");
            this.put(287, ":");
            this.put(311, "\"");
            this.put(312, "<");
            this.put(321, "\"");
            this.put(323, ">");
            this.put(256, ">");
        }
    };
    public final int tokenType;

    public ParserToken(TokenChannel channel, TokenRole role, int tokenType) {
        super(channel, role);
        this.tokenType = tokenType;
    }

    public ParserToken(TokenRole role, int tokenType) {
        super(role);
        this.tokenType = tokenType;
    }

    public ParserToken(TokenChannel channel, int tokenType) {
        super(channel);
        this.tokenType = tokenType;
    }

    public ParserToken(int tokenType) {
        this.tokenType = tokenType;
    }

    @Override
    public String getContent() {
        String literalName = GLSLLexer.VOCABULARY.getLiteralName(this.tokenType);
        if (literalName == null) {
            String replacement = missingTokenStrings.get(this.tokenType);
            if (replacement != null) {
                return replacement;
            }
            throw new IllegalStateException("Can't create a parser token for a token type that doesn't have a defined literal name! Resolving " + this.tokenType + " failed.");
        }
        return literalName.substring(1, literalName.length() - 1);
    }
}

