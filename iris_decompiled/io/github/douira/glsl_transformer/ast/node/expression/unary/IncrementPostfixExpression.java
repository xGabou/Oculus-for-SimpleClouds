/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.expression.unary;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.UnaryExpression;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class IncrementPostfixExpression
extends UnaryExpression {
    public IncrementPostfixExpression(Expression expression) {
        super(expression);
    }

    @Override
    public Expression.ExpressionType getExpressionType() {
        return Expression.ExpressionType.INCREMENT_POSTFIX;
    }

    @Override
    public <R> R expressionAccept(ASTVisitor<R> visitor) {
        return visitor.visitIncrementPostfixExpression(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterIncrementPostfixExpression(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitIncrementPostfixExpression(this);
    }

    @Override
    public IncrementPostfixExpression clone() {
        return new IncrementPostfixExpression(IncrementPostfixExpression.clone(this.operand));
    }

    @Override
    public IncrementPostfixExpression cloneInto(Root root) {
        return (IncrementPostfixExpression)super.cloneInto(root);
    }
}

