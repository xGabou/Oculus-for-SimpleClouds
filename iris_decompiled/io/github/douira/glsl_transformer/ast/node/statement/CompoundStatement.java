/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.statement;

import io.github.douira.glsl_transformer.ast.node.statement.ManyStatement;
import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;
import java.util.stream.Stream;

public class CompoundStatement
extends ManyStatement {
    public CompoundStatement(Stream<Statement> statements) {
        super(statements);
    }

    @Override
    public Statement.StatementType getStatementType() {
        return Statement.StatementType.COMPOUND;
    }

    @Override
    public <R> R statementAccept(ASTVisitor<R> visitor) {
        return visitor.visitCompoundStatement(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterCompoundStatement(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitCompoundStatement(this);
    }

    @Override
    public CompoundStatement clone() {
        return new CompoundStatement(CompoundStatement.clone(this.statements));
    }

    @Override
    public CompoundStatement cloneInto(Root root) {
        return (CompoundStatement)super.cloneInto(root);
    }
}

