/*
 * Decompiled with CFR 0.152.
 */
package de.odysseus.ithaka.digraph;

import de.odysseus.ithaka.digraph.Digraph;
import java.util.Collection;
import java.util.OptionalInt;
import java.util.Set;

public abstract class DigraphAdapter<V>
implements Digraph<V> {
    private final Digraph<V> delegate;

    public DigraphAdapter(Digraph<V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean add(V vertex) {
        return this.delegate.add(vertex);
    }

    @Override
    public boolean contains(V source, V target) {
        return this.delegate.contains(source, target);
    }

    @Override
    public boolean contains(V vertex) {
        return this.delegate.contains(vertex);
    }

    @Override
    public OptionalInt get(V source, V target) {
        return this.delegate.get(source, target);
    }

    @Override
    public int getOutDegree(V vertex) {
        return this.delegate.getOutDegree(vertex);
    }

    @Override
    public int getEdgeCount() {
        return this.delegate.getEdgeCount();
    }

    @Override
    public int getVertexCount() {
        return this.delegate.getVertexCount();
    }

    @Override
    public int totalWeight() {
        return this.delegate.totalWeight();
    }

    @Override
    public Iterable<V> vertices() {
        return this.delegate.vertices();
    }

    @Override
    public OptionalInt put(V source, V target, int edge) {
        return this.delegate.put(source, target, edge);
    }

    @Override
    public OptionalInt remove(V source, V target) {
        return this.delegate.remove(source, target);
    }

    @Override
    public boolean remove(V vertex) {
        return this.delegate.remove(vertex);
    }

    @Override
    public void removeAll(Collection<V> vertices) {
        this.delegate.removeAll(vertices);
    }

    @Override
    public Digraph<V> reverse() {
        return this.delegate.reverse();
    }

    @Override
    public Digraph<V> subgraph(Set<V> vertices) {
        return this.delegate.subgraph(vertices);
    }

    @Override
    public boolean isAcyclic() {
        return this.delegate.isAcyclic();
    }

    @Override
    public Iterable<V> targets(V source) {
        return this.delegate.targets(source);
    }

    public String toString() {
        return this.delegate.toString();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        return this.delegate.equals(((DigraphAdapter)obj).delegate);
    }

    public int hashCode() {
        return this.delegate.hashCode();
    }
}

