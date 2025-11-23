/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.expression.binary;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.BinaryExpression;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class LessThanEqualExpression
extends BinaryExpression {
    public LessThanEqualExpression(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public Expression.ExpressionType getExpressionType() {
        return Expression.ExpressionType.LESS_THAN_EQUAL;
    }

    @Override
    public <R> R expressionAccept(ASTVisitor<R> visitor) {
        return visitor.visitLessThanEqualExpression(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterLessThanEqualExpression(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitLessThanEqualExpression(this);
    }

    @Override
    public LessThanEqualExpression clone() {
        return new LessThanEqualExpression(LessThanEqualExpression.clone(this.left), LessThanEqualExpression.clone(this.right));
    }

    @Override
    public LessThanEqualExpression cloneInto(Root root) {
        return (LessThanEqualExpression)super.cloneInto(root);
    }
}

