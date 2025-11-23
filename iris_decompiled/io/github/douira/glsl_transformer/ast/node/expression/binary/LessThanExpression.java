/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.expression.binary;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.BinaryExpression;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class LessThanExpression
extends BinaryExpression {
    public LessThanExpression(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public Expression.ExpressionType getExpressionType() {
        return Expression.ExpressionType.LESS_THAN;
    }

    @Override
    public <R> R expressionAccept(ASTVisitor<R> visitor) {
        return visitor.visitLessThanExpression(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterLessThanExpression(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitLessThanExpression(this);
    }

    @Override
    public LessThanExpression clone() {
        return new LessThanExpression(LessThanExpression.clone(this.left), LessThanExpression.clone(this.right));
    }

    @Override
    public LessThanExpression cloneInto(Root root) {
        return (LessThanExpression)super.cloneInto(root);
    }
}

