/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.statement.terminal;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.CaseLabelStatement;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class CaseStatement
extends CaseLabelStatement {
    protected Expression expression;

    public CaseStatement(Expression expression) {
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
    public CaseLabelStatement.CaseLabelType getCaseLabelType() {
        return CaseLabelStatement.CaseLabelType.CASE;
    }

    @Override
    public Statement.StatementType getStatementType() {
        return Statement.StatementType.CASE;
    }

    @Override
    public <R> R statementAccept(ASTVisitor<R> visitor) {
        return visitor.visitCaseStatement(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterCaseStatement(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitCaseStatement(this);
    }

    @Override
    public CaseStatement clone() {
        return new CaseStatement(CaseStatement.clone(this.expression));
    }

    @Override
    public CaseStatement cloneInto(Root root) {
        return (CaseStatement)super.cloneInto(root);
    }
}

