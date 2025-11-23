/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.traversal;

import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ListNode;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public abstract class ASTVoidVisitor
implements ASTVisitor<Void> {
    public void visitVoid(ASTNode node) {
    }

    public void visitVoidData(Object data) {
    }

    @Override
    public Void visitData(Object data) {
        this.visitVoidData(data);
        return null;
    }

    @Override
    public Void visitData(Void previousResult, Object data) {
        this.visitData(data);
        return null;
    }

    @Override
    public Void visit(ASTNode node) {
        this.visitVoid(node);
        node.accept(this);
        return null;
    }

    @Override
    public Void visit(Void previousResult, ASTNode node) {
        this.visit(node);
        return null;
    }

    @Override
    public Void visitSafe(Void previousResult, ASTNode node) {
        if (node != null) {
            this.visit(node);
        }
        return null;
    }

    @Override
    public Void visitChildren(Void previousResult, ListNode<? extends ASTNode> node) {
        for (ASTNode aSTNode : node.getChildren()) {
            if (aSTNode == null) continue;
            this.visit(aSTNode);
        }
        return null;
    }

    @Override
    public Void aggregateResult(Void aggregate, Void nextResult) {
        return null;
    }

    @Override
    public Void aggregateResult(Void aggregate, Void firstResult, Void secondResult) {
        return null;
    }

    @Override
    public Void defaultResult() {
        return null;
    }

    @Override
    public Void initialResult() {
        return null;
    }

    @Override
    public Void superNodeTypeResult() {
        return null;
    }
}

