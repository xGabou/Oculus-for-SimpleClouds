/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.type.initializer;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.type.initializer.Initializer;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class ExpressionInitializer
extends Initializer {
    protected Expression expression;

    public ExpressionInitializer(Expression expression) {
        this.expression = this.setup(expression, this::setExpression);
    }

    public Expression getExpression() {
        return this.expression;
    }

    public void setExpression(Expression expression) {
        this.updateParents(this.expression, expression, this::setExpression);
        this.expression = expression;
    }

    @Override
    public Initializer.InitializerType getInitializerType() {
        return Initializer.InitializerType.EXPRESSION;
    }

    @Override
    public <R> R initializerAccept(ASTVisitor<R> visitor) {
        return visitor.visitExpressionInitializer(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterExpressionInitializer(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitExpressionInitializer(this);
    }

    @Override
    public ExpressionInitializer clone() {
        return new ExpressionInitializer(ExpressionInitializer.clone(this.expression));
    }

    @Override
    public ExpressionInitializer cloneInto(Root root) {
        return (ExpressionInitializer)super.cloneInto(root);
    }
}

