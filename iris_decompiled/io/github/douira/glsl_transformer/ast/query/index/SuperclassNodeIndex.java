/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.query.index;

import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.node.abstract_node.InnerASTNode;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ListASTNode;
import io.github.douira.glsl_transformer.ast.query.index.NodeIndex;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SuperclassNodeIndex<S extends Set<ASTNode>>
extends NodeIndex<S> {
    public SuperclassNodeIndex(Supplier<S> setFactory) {
        super(setFactory);
    }

    public static SuperclassNodeIndex<HashSet<ASTNode>> withUnordered() {
        return new SuperclassNodeIndex<HashSet<ASTNode>>(HashSet::new);
    }

    public static SuperclassNodeIndex<LinkedHashSet<ASTNode>> withOrdered() {
        return new SuperclassNodeIndex<LinkedHashSet<ASTNode>>(LinkedHashSet::new);
    }

    protected void iterateClasses(ASTNode node, BiConsumer<Class<? extends ASTNode>, ASTNode> consumer) {
        for (Class<?> nodeClass = node.getClass(); nodeClass != null && nodeClass != InnerASTNode.class && nodeClass != ASTNode.class && nodeClass != ListASTNode.class; nodeClass = nodeClass.getSuperclass()) {
            consumer.accept(nodeClass, node);
        }
    }

    @Override
    public void add(ASTNode node) {
        this.iterateClasses(node, (nodeClass, toAdd) -> {
            Set set = (Set)this.index.get(nodeClass);
            if (set == null) {
                set = (Set)this.setFactory.get();
                this.index.put(nodeClass, set);
            }
            set.add(toAdd);
        });
    }

    @Override
    public void remove(ASTNode node) {
        this.iterateClasses(node, (nodeClass, toAdd) -> {
            Set set = (Set)this.index.get(nodeClass);
            if (set == null) {
                return;
            }
            set.remove(toAdd);
        });
    }
}

