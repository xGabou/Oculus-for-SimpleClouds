/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.expression.unary;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.UnaryExpression;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class IncrementPrefixExpression
extends UnaryExpression {
    public IncrementPrefixExpression(Expression expression) {
        super(expression);
    }

    @Override
    public Expression.ExpressionType getExpressionType() {
        return Expression.ExpressionType.INCREMENT_PREFIX;
    }

    @Override
    public <R> R expressionAccept(ASTVisitor<R> visitor) {
        return visitor.visitIncrementPrefixExpression(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterIncrementPrefixExpression(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitIncrementPrefixExpression(this);
    }

    @Override
    public IncrementPrefixExpression clone() {
        return new IncrementPrefixExpression(IncrementPrefixExpression.clone(this.operand));
    }

    @Override
    public IncrementPrefixExpression cloneInto(Root root) {
        return (IncrementPrefixExpression)super.cloneInto(root);
    }
}

