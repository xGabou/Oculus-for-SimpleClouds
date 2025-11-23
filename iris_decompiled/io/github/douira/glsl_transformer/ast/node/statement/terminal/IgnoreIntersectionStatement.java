/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.statement.terminal;

import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.TerminalStatement;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class IgnoreIntersectionStatement
extends TerminalStatement {
    @Override
    public Statement.StatementType getStatementType() {
        return Statement.StatementType.IGNORE_INTERSECTION;
    }

    @Override
    public <R> R statementAccept(ASTVisitor<R> visitor) {
        return visitor.visitIgnoreIntersectionStatement(this);
    }

    @Override
    public IgnoreIntersectionStatement clone() {
        return new IgnoreIntersectionStatement();
    }

    @Override
    public IgnoreIntersectionStatement cloneInto(Root root) {
        return (IgnoreIntersectionStatement)super.cloneInto(root);
    }
}

