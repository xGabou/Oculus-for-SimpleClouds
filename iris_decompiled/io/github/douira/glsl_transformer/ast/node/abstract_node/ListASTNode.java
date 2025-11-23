/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.abstract_node;

import io.github.douira.glsl_transformer.ast.data.ChildNodeList;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.node.abstract_node.InnerASTNode;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ListNode;
import io.github.douira.glsl_transformer.ast.query.Root;
import java.util.stream.Stream;

public abstract class ListASTNode<Child extends ASTNode>
extends InnerASTNode
implements ListNode<Child> {
    private ChildNodeList<Child> children;

    public ListASTNode(Stream<Child> children) {
        this.children = ChildNodeList.collect(children, this);
    }

    @Override
    public ChildNodeList<Child> getChildren() {
        return this.children;
    }

    @Override
    public ListASTNode<Child> cloneInto(Root root) {
        return (ListASTNode)super.cloneInto(root);
    }

    public Stream<Child> getClonedChildren() {
        return this.children.getClonedStream();
    }
}

