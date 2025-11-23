/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.print.token;

import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.print.ASTPrinterBase;
import io.github.douira.glsl_transformer.ast.print.TokenRole;
import io.github.douira.glsl_transformer.ast.print.token.PrintToken;
import java.util.function.Function;

public class ReplaceToken
extends PrintToken {
    private PrintToken replacement;
    private Function<PrintToken, Boolean> condition;

    public ReplaceToken(PrintToken replacement, Function<PrintToken, Boolean> condition) {
        this.replacement = replacement;
        this.condition = condition;
    }

    public ReplaceToken(PrintToken replacement, String match, Function<ASTNode, Boolean> condition) {
        this.replacement = replacement;
        this.condition = token -> match.equals(token.getContent()) && (Boolean)condition.apply(token.getSource()) != false;
    }

    @Override
    public String getContent() {
        return null;
    }

    public void replace(PrintToken other, ASTPrinterBase printer) {
        if (other.getRole() == TokenRole.COMMON_FORMATTING && this.condition.apply(other).booleanValue()) {
            printer.replaceToken(this.replacement);
        }
    }

    public static ReplaceToken fromMatch(PrintToken replacement, String match) {
        return new ReplaceToken(replacement, token -> match.equals(token.getContent()));
    }

    public static ReplaceToken fromNodeCondition(PrintToken replacement, Function<ASTNode, Boolean> condition) {
        return new ReplaceToken(replacement, node -> (Boolean)condition.apply(node.getSource()));
    }

    public static ReplaceToken fromMatchAndNodeCondition(PrintToken replacement, String match, Function<ASTNode, Boolean> condition) {
        return new ReplaceToken(replacement, token -> match.equals(token.getContent()) && (Boolean)condition.apply(token.getSource()) != false);
    }
}

