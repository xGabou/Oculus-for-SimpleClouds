/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.expression.unary;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public abstract class UnaryExpression
extends Expression {
    protected Expression operand;

    public UnaryExpression(Expression operand) {
        this.operand = this.setup(operand, this::setOperand);
    }

    public Expression getOperand() {
        return this.operand;
    }

    public void setOperand(Expression operand) {
        this.updateParents(this.operand, operand, this::setOperand);
        this.operand = operand;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.aggregateResult(super.accept(visitor), visitor.visitUnaryExpression(this), this.expressionAccept(visitor));
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterUnaryExpression(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitUnaryExpression(this);
    }

    @Override
    public abstract UnaryExpression clone();

    @Override
    public UnaryExpression cloneInto(Root root) {
        return (UnaryExpression)super.cloneInto(root);
    }
}

