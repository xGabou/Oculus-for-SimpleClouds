/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.traversal;

import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTWalker;

public class ASTListenerVisitor<R>
extends ASTWalker<R>
implements ASTListener {
    public ASTListenerVisitor() {
        this.listener = this;
    }

    public static <R> R walkAndListen(ASTNode node) {
        return new ASTListenerVisitor<R>().visit(node);
    }

    @Override
    public void enterContext(ASTNode node) {
    }
}

