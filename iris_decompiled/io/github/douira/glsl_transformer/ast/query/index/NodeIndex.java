/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.query.index;

import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.query.index.Index;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class NodeIndex<S extends Set<ASTNode>>
implements Index<ASTNode> {
    public final Map<Class<ASTNode>, S> index = new HashMap<Class<ASTNode>, S>();
    public final Supplier<S> setFactory;

    public NodeIndex(Supplier<S> setFactory) {
        this.setFactory = setFactory;
    }

    public static NodeIndex<HashSet<ASTNode>> withUnordered() {
        return new NodeIndex<HashSet<ASTNode>>(HashSet::new);
    }

    public static NodeIndex<LinkedHashSet<ASTNode>> withOrdered() {
        return new NodeIndex<LinkedHashSet<ASTNode>>(LinkedHashSet::new);
    }

    @Override
    public void add(ASTNode node) {
        Class<?> nodeClass = node.getClass();
        Set set = (Set)this.index.get(nodeClass);
        if (set == null) {
            set = (Set)this.setFactory.get();
            this.index.put(nodeClass, set);
        }
        set.add(node);
    }

    @Override
    public void remove(ASTNode node) {
        Set set = (Set)this.index.get(node.getClass());
        if (set == null) {
            return;
        }
        set.remove(node);
    }

    public <N extends ASTNode> Set<N> get(Class<N> type) {
        Set result = (Set)this.index.get(type);
        return result == null ? Collections.emptySet() : result;
    }

    public <N extends ASTNode> Stream<N> getStream(Class<N> type) {
        Set result = (Set)this.index.get(type);
        return result == null ? Stream.empty() : result.stream();
    }

    public <N extends ASTNode> N getOne(Class<N> type) {
        Set result = (Set)this.index.get(type);
        if (result == null) {
            return null;
        }
        Iterator iterator = result.iterator();
        return (N)(iterator.hasNext() ? (ASTNode)iterator.next() : null);
    }

    public <N extends ASTNode> N getUnique(Class<N> type) {
        int resultCount;
        Set result = (Set)this.index.get(type);
        int n = resultCount = result == null ? 0 : result.size();
        if (resultCount != 1) {
            throw new IllegalStateException("Expected exactly one node of type " + type + " but found " + resultCount);
        }
        return (N)((ASTNode)result.iterator().next());
    }

    public boolean has(Class<? extends ASTNode> type) {
        Set result = (Set)this.index.get(type);
        return result != null && !result.isEmpty();
    }

    public <N extends ASTNode> Set<N> get(N node) {
        return this.get((N)((Object)node.getClass()));
    }

    public <N extends ASTNode> N getOne(N node) {
        return (N)this.getOne((N)((Object)node.getClass()));
    }

    public <N extends ASTNode> N getUnique(N node) {
        return (N)this.getUnique((N)((Object)node.getClass()));
    }

    public boolean has(ASTNode node) {
        return this.has(node.getClass());
    }

    public boolean hasExact(ASTNode node) {
        Set<ASTNode> set = this.get(node);
        return set != null && set.contains(node);
    }
}

