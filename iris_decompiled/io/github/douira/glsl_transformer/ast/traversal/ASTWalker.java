/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.traversal;

import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.node.abstract_node.InnerASTNode;
import io.github.douira.glsl_transformer.ast.traversal.ASTBaseVisitor;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;

public class ASTWalker<R>
extends ASTBaseVisitor<R> {
    protected ASTListener listener;

    protected ASTWalker() {
    }

    private ASTWalker(ASTListener listener) {
        this.listener = listener;
    }

    public static <R> R walk(ASTListener listener, ASTNode node) {
        return new ASTWalker<R>(listener).startVisit(node);
    }

    @Override
    public R visit(ASTNode node) {
        if (node instanceof InnerASTNode) {
            InnerASTNode innerNode = (InnerASTNode)node;
            ASTNode previousContext = this.context;
            this.setContext(node);
            this.enterNode(this.listener, innerNode);
            Object result = this.visitRaw(node);
            this.exitNode(this.listener, innerNode);
            this.enterContext(previousContext);
            return result;
        }
        return super.visit(node);
    }

    protected void enterNode(ASTListener listener, InnerASTNode node) {
        listener.enterEveryNode(node);
        node.enterNode(listener);
        listener.afterEnterEveryNode(node);
    }

    protected void exitNode(ASTListener listener, InnerASTNode node) {
        listener.beforeExitEveryNode(node);
        node.exitNode(listener);
        listener.exitEveryNode(node);
    }

    @Override
    public void enterContext(ASTNode node) {
        this.listener.enterContext(node);
    }
}

