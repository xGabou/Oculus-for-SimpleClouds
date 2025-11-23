/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.traversal;

import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;
import io.github.douira.glsl_transformer.ast.traversal.ContextTracker;

public abstract class ASTBaseVisitor<R>
implements ASTVisitor<R>,
ContextTracker {
    protected ASTNode context;

    @Override
    public R startVisit(ASTNode node) {
        this.context = node;
        return ASTVisitor.super.startVisit(node);
    }

    @Override
    public R visit(ASTNode node) {
        ASTNode previousContext = this.context;
        this.setContext(node);
        R result = this.visitRaw(node);
        this.enterContext(previousContext);
        return result;
    }

    protected void setContext(ASTNode node) {
        this.context = node;
        this.enterContext(node);
    }

    protected R visitRaw(ASTNode node) {
        return node.accept(this);
    }

    @Override
    public R initialResult() {
        return this.defaultResult();
    }

    @Override
    public R superNodeTypeResult() {
        return this.defaultResult();
    }

    @Override
    public R defaultResult() {
        return null;
    }

    @Override
    public R aggregateResult(R aggregate, R nextResult) {
        return nextResult;
    }
}

