/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.expression.unary;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.UnaryExpression;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class BooleanNotExpression
extends UnaryExpression {
    public BooleanNotExpression(Expression expression) {
        super(expression);
    }

    @Override
    public Expression.ExpressionType getExpressionType() {
        return Expression.ExpressionType.BOOLEAN_NOT;
    }

    @Override
    public <R> R expressionAccept(ASTVisitor<R> visitor) {
        return visitor.visitBooleanNotExpression(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterBooleanNotExpression(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitBooleanNotExpression(this);
    }

    @Override
    public BooleanNotExpression clone() {
        return new BooleanNotExpression(BooleanNotExpression.clone(this.operand));
    }

    @Override
    public BooleanNotExpression cloneInto(Root root) {
        return (BooleanNotExpression)super.cloneInto(root);
    }
}

