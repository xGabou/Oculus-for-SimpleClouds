/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.expression.unary;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.UnaryExpression;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class IdentityExpression
extends UnaryExpression {
    public IdentityExpression(Expression expression) {
        super(expression);
    }

    @Override
    public Expression.ExpressionType getExpressionType() {
        return Expression.ExpressionType.IDENTITY;
    }

    @Override
    public <R> R expressionAccept(ASTVisitor<R> visitor) {
        return visitor.visitIdentityExpression(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterIdentityExpression(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitIdentityExpression(this);
    }

    @Override
    public IdentityExpression clone() {
        return new IdentityExpression(IdentityExpression.clone(this.operand));
    }

    @Override
    public IdentityExpression cloneInto(Root root) {
        return (IdentityExpression)super.cloneInto(root);
    }
}

