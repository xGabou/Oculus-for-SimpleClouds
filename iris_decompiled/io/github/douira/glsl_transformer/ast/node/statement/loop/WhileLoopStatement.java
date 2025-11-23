/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.statement.loop;

import io.github.douira.glsl_transformer.ast.node.IterationConditionInitializer;
import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.node.statement.loop.ConditionLoopStatement;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class WhileLoopStatement
extends ConditionLoopStatement {
    protected IterationConditionInitializer iterationConditionInitializer;

    private WhileLoopStatement(Statement statement, Expression condition, IterationConditionInitializer iterationConditionInitializer) {
        super(statement, condition);
        this.iterationConditionInitializer = this.setup(iterationConditionInitializer, this::setIterationConditionInitializer);
    }

    public WhileLoopStatement(Expression condition, Statement statement) {
        super(statement, condition);
    }

    public WhileLoopStatement(IterationConditionInitializer iterationConditionInitializer, Statement statement) {
        super(statement, null);
        this.iterationConditionInitializer = this.setup(iterationConditionInitializer, this::setIterationConditionInitializer);
    }

    public IterationConditionInitializer getIterationConditionInitializer() {
        return this.iterationConditionInitializer;
    }

    public void setIterationConditionInitializer(IterationConditionInitializer iterationConditionInitializer) {
        this.updateParents(this.iterationConditionInitializer, iterationConditionInitializer, this::setIterationConditionInitializer);
        this.iterationConditionInitializer = iterationConditionInitializer;
    }

    @Override
    public Statement.StatementType getStatementType() {
        return Statement.StatementType.WHILE_LOOP;
    }

    @Override
    public <R> R statementAccept(ASTVisitor<R> visitor) {
        return visitor.visitWhileLoopStatement(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterWhileLoopStatement(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitWhileLoopStatement(this);
    }

    @Override
    public WhileLoopStatement clone() {
        return new WhileLoopStatement(WhileLoopStatement.clone(this.statement), WhileLoopStatement.clone(this.condition), WhileLoopStatement.clone(this.iterationConditionInitializer));
    }

    @Override
    public WhileLoopStatement cloneInto(Root root) {
        return (WhileLoopStatement)super.cloneInto(root);
    }
}

