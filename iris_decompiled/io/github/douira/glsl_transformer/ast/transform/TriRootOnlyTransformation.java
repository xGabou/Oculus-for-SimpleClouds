/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.transform;

import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.query.Root;

@FunctionalInterface
public interface TriRootOnlyTransformation<N extends ASTNode> {
    public void accept(N var1, N var2, N var3, Root var4, Root var5, Root var6);
}

