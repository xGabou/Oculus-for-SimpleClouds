/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.statement;

import io.github.douira.glsl_transformer.ast.node.abstract_node.InnerASTNode;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.transform.ASTBuilder;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public abstract class Statement
extends InnerASTNode {
    public Statement() {
        this.sourceLocation = ASTBuilder.takeSourceLocation();
    }

    public abstract StatementType getStatementType();

    public abstract <R> R statementAccept(ASTVisitor<R> var1);

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitStatement(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        listener.enterStatement(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        listener.exitStatement(this);
    }

    @Override
    public abstract Statement clone();

    @Override
    public Statement cloneInto(Root root) {
        return (Statement)super.cloneInto(root);
    }

    public static enum StatementType {
        COMPOUND(StructureType.MANY),
        DECLARATION(StructureType.SEMI_TERMINAL),
        EXPRESSION(StructureType.SEMI_TERMINAL),
        EMPTY(StructureType.TERMINAL),
        SELECTION(StructureType.MANY),
        SWITCH(StructureType.UNARY),
        CASE(StructureType.SEMI_TERMINAL),
        DEFAULT(StructureType.TERMINAL),
        FOR_LOOP(StructureType.UNARY),
        WHILE_LOOP(StructureType.UNARY),
        DO_WHILE_LOOP(StructureType.UNARY),
        CONTINUE(StructureType.TERMINAL),
        BREAK(StructureType.TERMINAL),
        RETURN(StructureType.SEMI_TERMINAL),
        DISCARD(StructureType.TERMINAL),
        DEMOTE(StructureType.TERMINAL),
        IGNORE_INTERSECTION(StructureType.TERMINAL),
        TERMINATE_RAY(StructureType.TERMINAL);

        public final StructureType structureType;

        private StatementType(StructureType structureType) {
            this.structureType = structureType;
        }

        public static enum StructureType {
            SEMI_TERMINAL,
            TERMINAL,
            UNARY,
            MANY;

        }
    }
}

