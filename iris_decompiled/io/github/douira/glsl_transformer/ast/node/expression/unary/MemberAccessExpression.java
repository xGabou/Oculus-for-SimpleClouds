/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.expression.unary;

import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.UnaryExpression;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class MemberAccessExpression
extends UnaryExpression {
    protected Identifier member;

    public MemberAccessExpression(Expression expression, Identifier member) {
        super(expression);
        this.member = this.setup(member, this::setMember);
    }

    public Identifier getMember() {
        return this.member;
    }

    public void setMember(Identifier member) {
        this.updateParents(this.member, member, this::setMember);
        this.member = member;
    }

    @Override
    public Expression.ExpressionType getExpressionType() {
        return Expression.ExpressionType.MEMBER_ACCESS;
    }

    @Override
    public <R> R expressionAccept(ASTVisitor<R> visitor) {
        return visitor.visitMemberAccessExpression(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterMemberAccessExpression(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitMemberAccessExpression(this);
    }

    @Override
    public MemberAccessExpression clone() {
        return new MemberAccessExpression(MemberAccessExpression.clone(this.operand), MemberAccessExpression.clone(this.member));
    }

    @Override
    public MemberAccessExpression cloneInto(Root root) {
        return (MemberAccessExpression)super.cloneInto(root);
    }
}

