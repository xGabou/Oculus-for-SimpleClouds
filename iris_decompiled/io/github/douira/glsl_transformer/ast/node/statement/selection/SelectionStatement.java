/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.statement.selection;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.SemiTerminalStatement;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class SelectionStatement
extends SemiTerminalStatement {
    protected Expression condition;
    protected Statement ifTrue;
    protected Statement ifFalse;

    public SelectionStatement(Expression condition, Statement ifTrue, Statement ifFalse) {
        this.condition = this.setup(condition, this::setCondition);
        this.ifTrue = this.setup(ifTrue, this::setIfTrue);
        this.ifFalse = this.setup(ifFalse, this::setIfFalse);
    }

    public SelectionStatement(Expression condition, Statement ifTrue) {
        this.condition = this.setup(condition, this::setCondition);
        this.ifTrue = this.setup(ifTrue, this::setIfTrue);
    }

    public Expression getCondition() {
        return this.condition;
    }

    public void setCondition(Expression condition) {
        this.updateParents(this.condition, condition, this::setCondition);
        this.condition = condition;
    }

    public Statement getIfTrue() {
        return this.ifTrue;
    }

    public void setIfTrue(Statement ifTrue) {
        this.updateParents(this.ifTrue, ifTrue, this::setIfTrue);
        this.ifTrue = ifTrue;
    }

    public Statement getIfFalse() {
        return this.ifFalse;
    }

    public void setIfFalse(Statement ifFalse) {
        this.updateParents(this.ifFalse, ifFalse, this::setIfFalse);
        this.ifFalse = ifFalse;
    }

    public boolean hasIfFalse() {
        return this.ifFalse != null;
    }

    @Override
    public Statement.StatementType getStatementType() {
        return Statement.StatementType.SELECTION;
    }

    @Override
    public <R> R statementAccept(ASTVisitor<R> visitor) {
        return visitor.visitSelectionStatement(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterSelectionStatement(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitSelectionStatement(this);
    }

    @Override
    public SelectionStatement clone() {
        return new SelectionStatement(SelectionStatement.clone(this.condition), SelectionStatement.clone(this.ifTrue), SelectionStatement.clone(this.ifFalse));
    }

    @Override
    public SelectionStatement cloneInto(Root root) {
        return (SelectionStatement)super.cloneInto(root);
    }
}

