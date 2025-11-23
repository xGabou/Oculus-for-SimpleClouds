/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.statement.terminal;

import io.github.douira.glsl_transformer.ast.node.declaration.Declaration;
import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.SemiTerminalStatement;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class DeclarationStatement
extends SemiTerminalStatement {
    protected Declaration declaration;

    public DeclarationStatement(Declaration declaration) {
        this.declaration = this.setup(declaration, this::setDeclaration);
    }

    public Declaration getDeclaration() {
        return this.declaration;
    }

    public void setDeclaration(Declaration declaration) {
        this.updateParents(this.declaration, declaration, this::setDeclaration);
        this.declaration = declaration;
    }

    @Override
    public Statement.StatementType getStatementType() {
        return Statement.StatementType.DECLARATION;
    }

    @Override
    public <R> R statementAccept(ASTVisitor<R> visitor) {
        return visitor.visitDeclarationStatement(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterDeclarationStatement(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitDeclarationStatement(this);
    }

    @Override
    public DeclarationStatement clone() {
        return new DeclarationStatement(DeclarationStatement.clone(this.declaration));
    }

    @Override
    public DeclarationStatement cloneInto(Root root) {
        return (DeclarationStatement)super.cloneInto(root);
    }
}

