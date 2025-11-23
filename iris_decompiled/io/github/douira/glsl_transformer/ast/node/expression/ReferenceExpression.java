/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.expression;

import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.expression.TerminalExpression;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class ReferenceExpression
extends TerminalExpression {
    protected Identifier identifier;

    public ReferenceExpression(Identifier identifier) {
        this.identifier = this.setup(identifier, this::setIdentifier);
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.updateParents(this.identifier, identifier, this::setIdentifier);
        this.identifier = identifier;
    }

    @Override
    public Expression.ExpressionType getExpressionType() {
        return Expression.ExpressionType.REFERENCE;
    }

    @Override
    public <R> R expressionAccept(ASTVisitor<R> visitor) {
        return visitor.visitReferenceExpression(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterReferenceExpression(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitReferenceExpression(this);
    }

    @Override
    public ReferenceExpression clone() {
        return new ReferenceExpression(ReferenceExpression.clone(this.identifier));
    }

    @Override
    public ReferenceExpression cloneInto(Root root) {
        return (ReferenceExpression)super.cloneInto(root);
    }
}

