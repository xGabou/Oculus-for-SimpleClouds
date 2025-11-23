/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.expression;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.expression.ManyExpression;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;
import java.util.stream.Stream;

public class SequenceExpression
extends ManyExpression {
    public SequenceExpression(Stream<Expression> expressions) {
        super(expressions);
    }

    @Override
    public Expression.ExpressionType getExpressionType() {
        return Expression.ExpressionType.SEQUENCE;
    }

    @Override
    public <R> R expressionAccept(ASTVisitor<R> visitor) {
        return visitor.visitSequenceExpression(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterSequenceExpression(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitSequenceExpression(this);
    }

    @Override
    public SequenceExpression clone() {
        return new SequenceExpression(SequenceExpression.clone(this.expressions));
    }

    @Override
    public SequenceExpression cloneInto(Root root) {
        return (SequenceExpression)super.cloneInto(root);
    }
}

