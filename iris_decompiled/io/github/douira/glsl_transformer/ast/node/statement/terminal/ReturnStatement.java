/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.statement.terminal;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.SemiTerminalStatement;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class ReturnStatement
extends SemiTerminalStatement {
    protected Expression expression;

    public ReturnStatement() {
    }

    public ReturnStatement(Expression expression) {
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
    public Statement.StatementType getStatementType() {
        return Statement.StatementType.RETURN;
    }

    @Override
    public <R> R statementAccept(ASTVisitor<R> visitor) {
        return visitor.visitReturnStatement(this);
    }

    @Override
    public ReturnStatement clone() {
        return new ReturnStatement(ReturnStatement.clone(this.expression));
    }

    @Override
    public ReturnStatement cloneInto(Root root) {
        return (ReturnStatement)super.cloneInto(root);
    }
}

