/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.abstract_node;

import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;

public abstract class InnerASTNode
extends ASTNode {
    public abstract void enterNode(ASTListener var1);

    public abstract void exitNode(ASTListener var1);

    @Override
    public abstract InnerASTNode clone();

    @Override
    public InnerASTNode cloneInto(Root root) {
        return (InnerASTNode)super.cloneInto(root);
    }
}

