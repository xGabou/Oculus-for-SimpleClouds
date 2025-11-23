/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.expression;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public abstract class TerminalExpression
extends Expression {
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.aggregateResult(super.accept(visitor), visitor.visitTerminalExpression(this), this.expressionAccept(visitor));
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
    }

    @Override
    public abstract TerminalExpression clone();

    @Override
    public TerminalExpression cloneInto(Root root) {
        return (TerminalExpression)super.cloneInto(root);
    }
}

