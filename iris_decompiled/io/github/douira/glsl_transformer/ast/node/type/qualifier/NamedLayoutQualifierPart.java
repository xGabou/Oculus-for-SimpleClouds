/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.type.qualifier;

import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.LayoutQualifierPart;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class NamedLayoutQualifierPart
extends LayoutQualifierPart {
    protected Identifier name;
    protected Expression expression;

    public NamedLayoutQualifierPart(Identifier name, Expression expression) {
        this.name = this.setup(name, this::setName);
        this.expression = this.setup(expression, this::setExpression);
    }

    public NamedLayoutQualifierPart(Identifier name) {
        this.name = this.setup(name, this::setName);
    }

    public Identifier getName() {
        return this.name;
    }

    public void setName(Identifier name) {
        this.updateParents(this.name, name, this::setName);
        this.name = name;
    }

    public Expression getExpression() {
        return this.expression;
    }

    public void setExpression(Expression expression) {
        this.updateParents(this.expression, expression, this::setExpression);
        this.expression = expression;
    }

    @Override
    public LayoutQualifierPart.LayoutQualifierType getLayoutQualifierType() {
        return LayoutQualifierPart.LayoutQualifierType.NAMED;
    }

    @Override
    public <R> R layoutQualifierPartAccept(ASTVisitor<R> visitor) {
        return visitor.visitNamedLayoutQualifierPart(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterNamedLayoutQualifierPart(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitNamedLayoutQualifierPart(this);
    }

    @Override
    public NamedLayoutQualifierPart clone() {
        return new NamedLayoutQualifierPart(NamedLayoutQualifierPart.clone(this.name), NamedLayoutQualifierPart.clone(this.expression));
    }

    @Override
    public NamedLayoutQualifierPart cloneInto(Root root) {
        return (NamedLayoutQualifierPart)super.cloneInto(root);
    }
}

