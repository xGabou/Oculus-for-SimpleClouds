/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.expression.binary;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public abstract class BinaryExpression
extends Expression {
    protected Expression left;
    protected Expression right;

    public BinaryExpression(Expression left, Expression right) {
        this.left = this.setup(left, this::setLeft);
        this.right = this.setup(right, this::setRight);
    }

    public Expression getLeft() {
        return this.left;
    }

    public void setLeft(Expression left) {
        this.updateParents(this.left, left, this::setLeft);
        this.left = left;
    }

    public Expression getRight() {
        return this.right;
    }

    public void setRight(Expression right) {
        this.updateParents(this.right, right, this::setRight);
        this.right = right;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.aggregateResult(super.accept(visitor), visitor.visitBinaryExpression(this), this.expressionAccept(visitor));
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterBinaryExpression(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitBinaryExpression(this);
    }

    @Override
    public abstract BinaryExpression clone();

    @Override
    public BinaryExpression cloneInto(Root root) {
        return (BinaryExpression)super.cloneInto(root);
    }
}

