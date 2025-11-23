/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.expression.unary;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.UnaryExpression;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class LengthAccessExpression
extends UnaryExpression {
    public LengthAccessExpression(Expression operand) {
        super(operand);
    }

    @Override
    public Expression.ExpressionType getExpressionType() {
        return Expression.ExpressionType.LENGTH_ACCESS;
    }

    @Override
    public <R> R expressionAccept(ASTVisitor<R> visitor) {
        return visitor.visitLengthAccessExpression(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterLengthAccessExpression(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitLengthAccessExpression(this);
    }

    @Override
    public LengthAccessExpression clone() {
        return new LengthAccessExpression(LengthAccessExpression.clone(this.operand));
    }

    @Override
    public LengthAccessExpression cloneInto(Root root) {
        return (LengthAccessExpression)super.cloneInto(root);
    }
}

