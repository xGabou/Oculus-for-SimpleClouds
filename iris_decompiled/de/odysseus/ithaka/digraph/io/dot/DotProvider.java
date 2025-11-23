/*
 * Decompiled with CFR 0.152.
 */
package de.odysseus.ithaka.digraph.io.dot;

import de.odysseus.ithaka.digraph.Digraph;
import de.odysseus.ithaka.digraph.io.dot.DotAttribute;

public interface DotProvider<V, G extends Digraph<? extends V>> {
    public Iterable<DotAttribute> getDefaultGraphAttributes(G var1);

    public Iterable<DotAttribute> getDefaultNodeAttributes(G var1);

    public Iterable<DotAttribute> getDefaultEdgeAttributes(G var1);

    public String getNodeId(V var1);

    public Iterable<DotAttribute> getNodeAttributes(V var1);

    public Iterable<DotAttribute> getEdgeAttributes(V var1, V var2, int var3);

    public Iterable<DotAttribute> getSubgraphAttributes(G var1, V var2);
}

