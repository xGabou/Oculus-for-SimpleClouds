/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.statement.terminal;

import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.CaseLabelStatement;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class DefaultStatement
extends CaseLabelStatement {
    @Override
    public CaseLabelStatement.CaseLabelType getCaseLabelType() {
        return CaseLabelStatement.CaseLabelType.DEFAULT;
    }

    @Override
    public Statement.StatementType getStatementType() {
        return Statement.StatementType.DEFAULT;
    }

    @Override
    public <R> R statementAccept(ASTVisitor<R> visitor) {
        return visitor.visitDefaultStatement(this);
    }

    @Override
    public DefaultStatement clone() {
        return new DefaultStatement();
    }

    @Override
    public DefaultStatement cloneInto(Root root) {
        return (DefaultStatement)super.cloneInto(root);
    }
}

