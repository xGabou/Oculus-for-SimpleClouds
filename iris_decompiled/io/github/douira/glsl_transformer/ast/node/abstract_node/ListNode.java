/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.abstract_node;

import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import java.util.List;

public interface ListNode<Child extends ASTNode> {
    public List<Child> getChildren();
}

