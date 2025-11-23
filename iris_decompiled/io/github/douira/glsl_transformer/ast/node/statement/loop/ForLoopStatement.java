/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.statement.loop;

import io.github.douira.glsl_transformer.ast.node.IterationConditionInitializer;
import io.github.douira.glsl_transformer.ast.node.declaration.Declaration;
import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.node.statement.loop.LoopStatement;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class ForLoopStatement
extends LoopStatement {
    protected Expression initExpression;
    protected Declaration initDeclaration;
    protected Expression condition;
    protected IterationConditionInitializer iterationConditionInitializer;
    protected Expression incrementer;

    public ForLoopStatement(Statement statement, Expression initExpression, Expression condition, Expression incrementer) {
        super(statement);
        this.initExpression = this.setup(initExpression, this::setInitExpression);
        this.condition = this.setup(condition, this::setCondition);
        this.incrementer = this.setup(incrementer, this::setIncrementer);
    }

    public ForLoopStatement(Statement statement, Declaration initDeclaration, Expression condition, Expression incrementer) {
        super(statement);
        this.initDeclaration = this.setup(initDeclaration, this::setInitDeclaration);
        this.condition = this.setup(condition, this::setCondition);
        this.incrementer = this.setup(incrementer, this::setIncrementer);
    }

    public ForLoopStatement(Statement statement, Expression initExpression, IterationConditionInitializer iterationConditionInitializer, Expression incrementer) {
        super(statement);
        this.initExpression = this.setup(initExpression, this::setInitExpression);
        this.iterationConditionInitializer = this.setup(iterationConditionInitializer, this::setIterationConditionInitializer);
        this.incrementer = this.setup(incrementer, this::setIncrementer);
    }

    public ForLoopStatement(Statement statement, Declaration initDeclaration, IterationConditionInitializer iterationConditionInitializer, Expression incrementer) {
        super(statement);
        this.initDeclaration = this.setup(initDeclaration, this::setInitDeclaration);
        this.iterationConditionInitializer = this.setup(iterationConditionInitializer, this::setIterationConditionInitializer);
        this.incrementer = this.setup(incrementer, this::setIncrementer);
    }

    public ForLoopStatement(Statement statement) {
        super(statement);
    }

    public ForLoopStatement(Expression initExpression, Declaration initDeclaration, Expression condition, IterationConditionInitializer iterationConditionInitializer, Expression incrementer, Statement statement) {
        super(statement);
        this.initExpression = this.setup(initExpression, this::setInitExpression);
        this.initDeclaration = this.setup(initDeclaration, this::setInitDeclaration);
        this.condition = this.setup(condition, this::setCondition);
        this.iterationConditionInitializer = this.setup(iterationConditionInitializer, this::setIterationConditionInitializer);
        this.incrementer = this.setup(incrementer, this::setIncrementer);
    }

    public Expression getInitExpression() {
        return this.initExpression;
    }

    public void setInitExpression(Expression initExpression) {
        this.updateParents(this.initExpression, initExpression, this::setInitExpression);
        this.initExpression = initExpression;
    }

    public Declaration getInitDeclaration() {
        return this.initDeclaration;
    }

    public void setInitDeclaration(Declaration initDeclaration) {
        this.updateParents(this.initDeclaration, initDeclaration, this::setInitDeclaration);
        this.initDeclaration = initDeclaration;
    }

    public Expression getCondition() {
        return this.condition;
    }

    public void setCondition(Expression condition) {
        this.updateParents(this.condition, condition, this::setCondition);
        this.condition = condition;
    }

    public IterationConditionInitializer getIterationConditionInitializer() {
        return this.iterationConditionInitializer;
    }

    public void setIterationConditionInitializer(IterationConditionInitializer iterationConditionInitializer) {
        this.updateParents(this.iterationConditionInitializer, iterationConditionInitializer, this::setIterationConditionInitializer);
        this.iterationConditionInitializer = iterationConditionInitializer;
    }

    public Expression getIncrementer() {
        return this.incrementer;
    }

    public void setIncrementer(Expression incrementer) {
        this.updateParents(this.incrementer, incrementer, this::setIncrementer);
        this.incrementer = incrementer;
    }

    @Override
    public Statement.StatementType getStatementType() {
        return Statement.StatementType.FOR_LOOP;
    }

    @Override
    public <R> R statementAccept(ASTVisitor<R> visitor) {
        return visitor.visitForLoopStatement(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterForLoopStatement(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitForLoopStatement(this);
    }

    @Override
    public ForLoopStatement clone() {
        return new ForLoopStatement(ForLoopStatement.clone(this.initExpression), ForLoopStatement.clone(this.initDeclaration), ForLoopStatement.clone(this.condition), ForLoopStatement.clone(this.iterationConditionInitializer), ForLoopStatement.clone(this.incrementer), ForLoopStatement.clone(this.statement));
    }

    @Override
    public ForLoopStatement cloneInto(Root root) {
        return (ForLoopStatement)super.cloneInto(root);
    }
}

