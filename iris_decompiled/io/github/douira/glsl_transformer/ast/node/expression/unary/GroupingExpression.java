/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.expression.unary;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.UnaryExpression;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class GroupingExpression
extends UnaryExpression {
    public GroupingExpression(Expression expression) {
        super(expression);
    }

    @Override
    public Expression.ExpressionType getExpressionType() {
        return Expression.ExpressionType.GROUPING;
    }

    @Override
    public <R> R expressionAccept(ASTVisitor<R> visitor) {
        return visitor.visitGroupingExpression(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterGroupingExpression(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitGroupingExpression(this);
    }

    @Override
    public GroupingExpression clone() {
        return new GroupingExpression(GroupingExpression.clone(this.operand));
    }

    @Override
    public GroupingExpression cloneInto(Root root) {
        return (GroupingExpression)super.cloneInto(root);
    }
}

