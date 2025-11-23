/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.query.index;

import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;

public interface Index<N extends ASTNode> {
    public void add(N var1);

    public void remove(N var1);
}

