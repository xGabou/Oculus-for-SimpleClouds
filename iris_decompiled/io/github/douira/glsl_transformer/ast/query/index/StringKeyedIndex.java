/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.query.index;

import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.node.expression.ReferenceExpression;
import io.github.douira.glsl_transformer.ast.query.index.Index;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class StringKeyedIndex<V, N extends ASTNode, S extends Set<V>, I extends Map<String, S>>
implements Index<N> {
    public final I index;
    public final Supplier<S> setFactory;

    public StringKeyedIndex(I index, Supplier<S> setFactory) {
        this.index = index;
        this.setFactory = setFactory;
    }

    protected abstract N getNode(V var1);

    public Set<V> get(String key) {
        Set result = (Set)this.index.get(key);
        return result == null ? Collections.emptySet() : result;
    }

    public Stream<V> getStream(String key) {
        Set result = (Set)this.index.get(key);
        return result == null ? Stream.empty() : result.stream();
    }

    public <M extends ASTNode> Stream<M> getAncestors(String key, Class<M> ancestorType) {
        return this.getStream(key).map(value -> ((ASTNode)this.getNode(value)).getAncestor(ancestorType)).filter(Objects::nonNull);
    }

    public Stream<ReferenceExpression> getReferenceExpressions(String key) {
        return this.getStream(key).map(value -> this.getNode(value).getAncestor(ReferenceExpression.class)).filter(Objects::nonNull);
    }

    public ReferenceExpression getOneReferenceExpression(String key) {
        return this.getReferenceExpressions(key).findFirst().orElse(null);
    }

    public V getOne(String key) {
        Iterator iterator = ((Set)this.index.get(key)).iterator();
        return iterator.hasNext() ? (V)iterator.next() : null;
    }

    public V getUnique(String key) {
        int resultSize;
        Set set = (Set)this.index.get(key);
        int n = resultSize = set == null ? 0 : set.size();
        if (resultSize != 1) {
            throw new IllegalStateException("Expected exactly one result for key " + key + ", but got " + resultSize);
        }
        return (V)set.iterator().next();
    }

    public boolean has(String key) {
        Set result = (Set)this.index.get(key);
        return result != null && !result.isEmpty();
    }
}

