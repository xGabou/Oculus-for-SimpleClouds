/*
 * Decompiled with CFR 0.152.
 */
package de.odysseus.ithaka.digraph;

import de.odysseus.ithaka.digraph.Digraph;
import de.odysseus.ithaka.digraph.DoubledDigraph;
import java.util.Collection;
import java.util.Collections;
import java.util.OptionalInt;
import java.util.Set;

class EmptyDigraph<V>
implements DoubledDigraph<V> {
    EmptyDigraph() {
    }

    @Override
    public boolean add(Object vertex) {
        throw new UnsupportedOperationException("Empty de.odysseus.ithaka.digraph cannot have vertices!");
    }

    @Override
    public boolean contains(Object source, Object target) {
        return false;
    }

    @Override
    public boolean contains(Object vertex) {
        return false;
    }

    @Override
    public OptionalInt get(Object source, Object target) {
        return OptionalInt.empty();
    }

    @Override
    public int getInDegree(Object vertex) {
        return 0;
    }

    @Override
    public int getOutDegree(Object vertex) {
        return 0;
    }

    @Override
    public int getEdgeCount() {
        return 0;
    }

    @Override
    public int getVertexCount() {
        return 0;
    }

    @Override
    public int totalWeight() {
        return 0;
    }

    @Override
    public Iterable<V> vertices() {
        return Collections.emptyList();
    }

    @Override
    public OptionalInt put(V source, V target, int edgeWeight) {
        throw new UnsupportedOperationException("Empty de.odysseus.ithaka.digraph cannot have edges!");
    }

    @Override
    public OptionalInt remove(V source, V target) {
        return OptionalInt.empty();
    }

    @Override
    public boolean remove(Object vertex) {
        return false;
    }

    @Override
    public void removeAll(Collection<V> vertices) {
    }

    @Override
    public DoubledDigraph<V> reverse() {
        return this;
    }

    @Override
    public Digraph<V> subgraph(Set<V> vertices) {
        return this;
    }

    @Override
    public Iterable<V> sources(Object target) {
        return Collections.emptyList();
    }

    @Override
    public Iterable<V> targets(Object source) {
        return Collections.emptyList();
    }

    @Override
    public boolean isAcyclic() {
        return true;
    }
}

