/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.statement.terminal;

import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.TerminalStatement;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class BreakStatement
extends TerminalStatement {
    @Override
    public Statement.StatementType getStatementType() {
        return Statement.StatementType.BREAK;
    }

    @Override
    public <R> R statementAccept(ASTVisitor<R> visitor) {
        return visitor.visitBreakStatement(this);
    }

    @Override
    public BreakStatement clone() {
        return new BreakStatement();
    }

    @Override
    public BreakStatement cloneInto(Root root) {
        return (BreakStatement)super.cloneInto(root);
    }
}

