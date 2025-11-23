/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.statement;

import io.github.douira.glsl_transformer.ast.data.ChildNodeList;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ListNode;
import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;
import java.util.stream.Stream;

public abstract class ManyStatement
extends Statement
implements ListNode<Statement> {
    protected ChildNodeList<Statement> statements;

    public ManyStatement(Stream<Statement> statements) {
        this.statements = ChildNodeList.collect(statements, this);
    }

    @Override
    public ChildNodeList<Statement> getChildren() {
        return this.statements;
    }

    public ChildNodeList<Statement> getStatements() {
        return this.statements;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.aggregateResult(super.accept(visitor), visitor.visitManyStatement(this), this.statementAccept(visitor));
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterManyStatement(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitManyStatement(this);
    }

    @Override
    public abstract ManyStatement clone();

    @Override
    public ManyStatement cloneInto(Root root) {
        return (ManyStatement)super.cloneInto(root);
    }
}

