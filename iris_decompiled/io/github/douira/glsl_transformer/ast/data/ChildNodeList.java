/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.data;

import io.github.douira.glsl_transformer.ast.data.ProxyArrayList;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.node.abstract_node.InnerASTNode;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ChildNodeList<Child extends ASTNode>
extends ProxyArrayList<Child> {
    private InnerASTNode parent;

    public ChildNodeList(InnerASTNode parent) {
        this.parent = parent;
    }

    public ChildNodeList(int initialCapacity, InnerASTNode parent) {
        super(initialCapacity);
        this.parent = parent;
    }

    protected ChildNodeList(Collection<? extends Child> c, InnerASTNode parent) {
        super(c, false);
        this.parent = parent;
        for (ASTNode child : this) {
            if (child == null) continue;
            parent.setup(child, ChildNodeList.makeChildReplacer(this, child));
        }
    }

    @Override
    protected void notifyAddition(Child added) {
        ((ASTNode)added).setParent(this.parent, ChildNodeList.makeChildReplacer(this, added));
    }

    @Override
    protected void notifyRemoval(Child removed) {
        ((ASTNode)removed).detachParent();
    }

    public boolean isNullEmpty() {
        if (this.isEmpty()) {
            return true;
        }
        for (ASTNode child : this) {
            if (child == null) continue;
            return false;
        }
        return true;
    }

    protected static <Child extends ASTNode> Consumer<Child> makeChildReplacer(ChildNodeList<Child> list, Child child) {
        return newNode -> {
            if (newNode == child) {
                return;
            }
            list.parent.updateParents(child, newNode, ChildNodeList.makeChildReplacer(list, newNode));
            if (newNode == null) {
                list.remove(child);
            } else {
                list.set(list.indexOf(child), newNode);
            }
        };
    }

    public static <Child extends ASTNode> ChildNodeList<Child> collect(Stream<Child> stream, InnerASTNode parent) {
        if (stream == null) {
            return null;
        }
        return stream.collect(() -> new ChildNodeList(parent), (list, child) -> {
            if (child != null) {
                parent.setup(child, null);
            }
            list.add(child);
        }, ProxyArrayList::addAll);
    }

    public Stream<Child> getClonedStream() {
        return this.stream().map(node -> ASTNode.clone(node));
    }
}

