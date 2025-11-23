/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.statement.loop;

import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public abstract class LoopStatement
extends Statement {
    protected Statement statement;

    public LoopStatement(Statement statement) {
        this.statement = this.setup(statement, this::setStatement);
    }

    public Statement getStatement() {
        return this.statement;
    }

    public void setStatement(Statement statement) {
        this.updateParents(this.statement, statement, this::setStatement);
        this.statement = statement;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.aggregateResult(super.accept(visitor), visitor.visitLoopStatement(this), this.statementAccept(visitor));
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterLoopStatement(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitLoopStatement(this);
    }

    @Override
    public abstract LoopStatement clone();

    @Override
    public LoopStatement cloneInto(Root root) {
        return (LoopStatement)super.cloneInto(root);
    }
}

