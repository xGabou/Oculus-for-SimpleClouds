/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.expression.binary;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.BinaryExpression;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class LeftShiftExpression
extends BinaryExpression {
    public LeftShiftExpression(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public Expression.ExpressionType getExpressionType() {
        return Expression.ExpressionType.SHIFT_LEFT;
    }

    @Override
    public <R> R expressionAccept(ASTVisitor<R> visitor) {
        return visitor.visitLeftShiftExpression(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterLeftShiftExpression(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitLeftShiftExpression(this);
    }

    @Override
    public LeftShiftExpression clone() {
        return new LeftShiftExpression(LeftShiftExpression.clone(this.left), LeftShiftExpression.clone(this.right));
    }

    @Override
    public LeftShiftExpression cloneInto(Root root) {
        return (LeftShiftExpression)super.cloneInto(root);
    }
}

