/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.expression;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.expression.TernaryExpression;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class ConditionExpression
extends TernaryExpression {
    public ConditionExpression(Expression condition, Expression trueExpression, Expression falseExpression) {
        super(condition, trueExpression, falseExpression);
    }

    public Expression getCondition() {
        return this.first;
    }

    public Expression getTrueExpression() {
        return this.second;
    }

    public Expression getFalseExpression() {
        return this.third;
    }

    @Override
    public Expression.ExpressionType getExpressionType() {
        return Expression.ExpressionType.CONDITION;
    }

    @Override
    public <R> R expressionAccept(ASTVisitor<R> visitor) {
        return visitor.visitConditionExpression(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterConditionExpression(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitConditionExpression(this);
    }

    @Override
    public ConditionExpression clone() {
        return new ConditionExpression(ConditionExpression.clone(this.first), ConditionExpression.clone(this.second), ConditionExpression.clone(this.third));
    }

    @Override
    public ConditionExpression cloneInto(Root root) {
        return (ConditionExpression)super.cloneInto(root);
    }
}

