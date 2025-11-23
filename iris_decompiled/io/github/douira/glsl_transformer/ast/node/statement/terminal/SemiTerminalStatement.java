/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.statement.terminal;

import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public abstract class SemiTerminalStatement
extends Statement {
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.aggregateResult(super.accept(visitor), visitor.visitSemiTerminalStatement(this), this.statementAccept(visitor));
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterSemiTerminalStatement(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitSemiTerminalStatement(this);
    }

    @Override
    public abstract SemiTerminalStatement clone();

    @Override
    public SemiTerminalStatement cloneInto(Root root) {
        return (SemiTerminalStatement)super.cloneInto(root);
    }
}

