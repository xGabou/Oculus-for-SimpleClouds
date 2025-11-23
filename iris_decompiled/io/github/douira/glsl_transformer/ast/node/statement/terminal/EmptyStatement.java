/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.statement.terminal;

import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.SemiTerminalStatement;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class EmptyStatement
extends SemiTerminalStatement {
    @Override
    public Statement.StatementType getStatementType() {
        return Statement.StatementType.EMPTY;
    }

    @Override
    public <R> R statementAccept(ASTVisitor<R> visitor) {
        return visitor.visitEmptyStatement(this);
    }

    @Override
    public EmptyStatement clone() {
        return new EmptyStatement();
    }

    @Override
    public EmptyStatement cloneInto(Root root) {
        return (EmptyStatement)super.cloneInto(root);
    }
}

