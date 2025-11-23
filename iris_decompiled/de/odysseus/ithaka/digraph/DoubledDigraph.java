/*
 * Decompiled with CFR 0.152.
 */
package de.odysseus.ithaka.digraph;

import de.odysseus.ithaka.digraph.Digraph;

public interface DoubledDigraph<V>
extends Digraph<V> {
    public int getInDegree(V var1);

    public Iterable<V> sources(V var1);

    @Override
    public DoubledDigraph<V> reverse();
}

