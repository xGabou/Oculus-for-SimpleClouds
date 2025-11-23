/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.statement.selection;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.statement.CompoundStatement;
import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class SwitchStatement
extends Statement {
    protected Expression expression;
    protected CompoundStatement statement;

    public SwitchStatement(Expression expression, CompoundStatement statement) {
        this.expression = this.setup(expression, this::setExpression);
        this.statement = this.setup(statement, this::setStatement);
    }

    public Expression getExpression() {
        return this.expression;
    }

    public void setExpression(Expression expression) {
        this.updateParents(this.expression, expression, this::setExpression);
        this.expression = expression;
    }

    public CompoundStatement getStatement() {
        return this.statement;
    }

    public void setStatement(CompoundStatement statement) {
        this.updateParents(this.statement, statement, this::setStatement);
        this.statement = statement;
    }

    @Override
    public Statement.StatementType getStatementType() {
        return Statement.StatementType.SWITCH;
    }

    @Override
    public <R> R statementAccept(ASTVisitor<R> visitor) {
        return visitor.visitSwitchStatement(this);
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.aggregateResult(super.accept(visitor), this.statementAccept(visitor));
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterSwitchStatement(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitSwitchStatement(this);
    }

    @Override
    public SwitchStatement clone() {
        return new SwitchStatement(SwitchStatement.clone(this.expression), SwitchStatement.clone(this.statement));
    }

    @Override
    public SwitchStatement cloneInto(Root root) {
        return (SwitchStatement)super.cloneInto(root);
    }
}

