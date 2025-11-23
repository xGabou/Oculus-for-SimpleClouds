/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.expression.unary;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.UnaryExpression;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class DecrementPrefixExpression
extends UnaryExpression {
    public DecrementPrefixExpression(Expression expression) {
        super(expression);
    }

    @Override
    public Expression.ExpressionType getExpressionType() {
        return Expression.ExpressionType.DECREMENT_PREFIX;
    }

    @Override
    public <R> R expressionAccept(ASTVisitor<R> visitor) {
        return visitor.visitDecrementPrefixExpression(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterDecrementPrefixExpression(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitDecrementPrefixExpression(this);
    }

    @Override
    public DecrementPrefixExpression clone() {
        return new DecrementPrefixExpression(DecrementPrefixExpression.clone(this.operand));
    }

    @Override
    public DecrementPrefixExpression cloneInto(Root root) {
        return (DecrementPrefixExpression)super.cloneInto(root);
    }
}

