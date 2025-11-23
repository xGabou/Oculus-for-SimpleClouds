/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.expression;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public abstract class TernaryExpression
extends Expression {
    protected Expression first;
    protected Expression second;
    protected Expression third;

    public TernaryExpression(Expression first, Expression second, Expression third) {
        this.first = this.setup(first, this::setFirst);
        this.second = this.setup(second, this::setSecond);
        this.third = this.setup(third, this::setThird);
    }

    public Expression getFirst() {
        return this.first;
    }

    public void setFirst(Expression first) {
        this.updateParents(this.first, first, this::setFirst);
        this.first = first;
    }

    public Expression getSecond() {
        return this.second;
    }

    public void setSecond(Expression second) {
        this.updateParents(this.second, second, this::setSecond);
        this.second = second;
    }

    public Expression getThird() {
        return this.third;
    }

    public void setThird(Expression third) {
        this.updateParents(this.third, third, this::setThird);
        this.third = third;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.aggregateResult(super.accept(visitor), visitor.visitTernaryExpression(this), this.expressionAccept(visitor));
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterTernaryExpression(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitTernaryExpression(this);
    }

    @Override
    public abstract TernaryExpression clone();

    @Override
    public TernaryExpression cloneInto(Root root) {
        return (TernaryExpression)super.cloneInto(root);
    }
}

