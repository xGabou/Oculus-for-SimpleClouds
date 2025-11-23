/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.expression;

import io.github.douira.glsl_transformer.ast.data.ChildNodeList;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ListNode;
import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;
import java.util.stream.Stream;

public abstract class ManyExpression
extends Expression
implements ListNode<Expression> {
    protected ChildNodeList<Expression> expressions;

    public ManyExpression(Stream<Expression> expressions) {
        this.expressions = ChildNodeList.collect(expressions, this);
    }

    @Override
    public ChildNodeList<Expression> getChildren() {
        return this.expressions;
    }

    public ChildNodeList<Expression> getExpressions() {
        return this.expressions;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.aggregateResult(super.accept(visitor), visitor.visitManyExpression(this), this.expressionAccept(visitor));
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterManyExpression(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitManyExpression(this);
    }

    @Override
    public abstract ManyExpression clone();

    @Override
    public ManyExpression cloneInto(Root root) {
        return (ManyExpression)super.cloneInto(root);
    }
}

