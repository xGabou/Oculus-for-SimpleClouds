/*
 * Decompiled with CFR 0.152.
 */
package de.odysseus.ithaka.digraph;

import de.odysseus.ithaka.digraph.Digraph;
import de.odysseus.ithaka.digraph.DigraphFactory;
import de.odysseus.ithaka.digraph.DoubledDigraph;
import de.odysseus.ithaka.digraph.EmptyDigraph;
import de.odysseus.ithaka.digraph.UnmodifiableDigraph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.Stack;

public class Digraphs {
    public static <V> DoubledDigraph<V> emptyDigraph() {
        return new EmptyDigraph();
    }

    public static <V> Digraph<V> unmodifiableDigraph(Digraph<V> digraph) {
        return new UnmodifiableDigraph<V>(digraph);
    }

    public static <V> List<V> toposort(Digraph<V> digraph, boolean descending) {
        ArrayList finished = new ArrayList();
        HashSet discovered = new HashSet(digraph.getVertexCount());
        for (V vertex : digraph.vertices()) {
            if (discovered.contains(vertex)) continue;
            Digraphs.dfs(digraph, vertex, discovered, finished);
        }
        if (!descending) {
            Collections.reverse(finished);
        }
        return finished;
    }

    public static <V> Set<V> closure(Digraph<V> digraph, V source) {
        HashSet closure = new HashSet();
        Digraphs.dfs(digraph, source, closure, closure);
        return closure;
    }

    public static <V> boolean isTriviallyAcyclic(Digraph<V> digraph) {
        return digraph.getVertexCount() < 2;
    }

    public static <V> boolean isAcyclic(Digraph<V> digraph) {
        if (Digraphs.isTriviallyAcyclic(digraph)) {
            return true;
        }
        int n = digraph.getVertexCount();
        if (digraph.getEdgeCount() > n * (n - 1) / 2) {
            return false;
        }
        return Digraphs.scc(digraph).size() == n;
    }

    public static <V> boolean isEquivalent(Digraph<V> first, Digraph<V> second, boolean compareEdges) {
        if (first == second) {
            return true;
        }
        if (first.getEdgeCount() != second.getEdgeCount() || first.getVertexCount() != second.getVertexCount()) {
            return false;
        }
        for (V source : first.vertices()) {
            if (!second.contains(source)) {
                return false;
            }
            for (V target : first.targets(source)) {
                int edge2;
                int edge1;
                OptionalInt secondEdge = second.get(source, target);
                if (!secondEdge.isPresent()) {
                    return false;
                }
                if (!compareEdges || (edge1 = first.get(source, target).getAsInt()) == (edge2 = secondEdge.getAsInt())) continue;
                return false;
            }
        }
        return true;
    }

    public static <V> boolean isStronglyConnected(Digraph<V> digraph) {
        int n = digraph.getVertexCount();
        if (n < 2) {
            return true;
        }
        return Digraphs.scc(digraph).size() == 1;
    }

    public static <V> boolean isReachable(Digraph<V> digraph, V source, V target) {
        return digraph.contains(source, target) || Digraphs.closure(digraph, source).contains(target);
    }

    public static <V> void dfs(Digraph<V> digraph, V source, Set<? super V> discovered, Collection<? super V> finished) {
        if (discovered.add(source)) {
            for (V target : digraph.targets(source)) {
                Digraphs.dfs(digraph, target, discovered, finished);
            }
            finished.add(source);
        }
    }

    public static <V> void dfs2(Digraph<V> digraph, V source, Set<? super V> discovered, Collection<? super V> finished) {
        Digraphs.dfs2(digraph, digraph.reverse(), source, discovered, finished);
    }

    private static <V> void dfs2(Digraph<V> forward, Digraph<V> backward, V source, Set<? super V> discovered, Collection<? super V> finished) {
        if (discovered.add(source)) {
            for (V target : forward.targets(source)) {
                Digraphs.dfs2(forward, backward, target, discovered, finished);
            }
            for (V target : backward.targets(source)) {
                Digraphs.dfs2(forward, backward, target, discovered, finished);
            }
            finished.add(source);
        }
    }

    public static <V> List<Set<V>> scc(Digraph<V> digraph) {
        ArrayList<Set<V>> components = new ArrayList<Set<V>>();
        Digraph<V> reverse = digraph.reverse();
        Stack stack = new Stack();
        HashSet discovered = new HashSet();
        for (V vertex : digraph.vertices()) {
            Digraphs.dfs(digraph, vertex, discovered, stack);
        }
        discovered = new HashSet();
        while (!stack.isEmpty()) {
            Object vertex = stack.pop();
            if (discovered.contains(vertex)) continue;
            HashSet component = new HashSet();
            Digraphs.dfs(reverse, vertex, discovered, component);
            components.add(component);
        }
        return components;
    }

    public static <V> List<Set<V>> wcc(Digraph<V> digraph) {
        ArrayList<Set<V>> components = new ArrayList<Set<V>>();
        Digraph<V> reverse = digraph.reverse();
        HashSet discovered = new HashSet();
        for (V vertex : digraph.vertices()) {
            if (discovered.contains(vertex)) continue;
            HashSet component = new HashSet();
            Digraphs.dfs2(digraph, reverse, vertex, discovered, component);
            components.add(component);
        }
        return components;
    }

    public static <V, G extends Digraph<V>> G reverse(Digraph<V> digraph, DigraphFactory<? extends G> factory) {
        G reverse = factory.create();
        for (V source : digraph.vertices()) {
            reverse.add(source);
            for (V target : digraph.targets(source)) {
                reverse.put(target, source, digraph.get(source, target).getAsInt());
            }
        }
        return reverse;
    }

    public static <V, G extends Digraph<V>> G copy(Digraph<V> digraph, DigraphFactory<? extends G> factory) {
        G result = factory.create();
        for (V source : digraph.vertices()) {
            result.add(source);
            for (V target : digraph.targets(source)) {
                result.put(source, target, digraph.get(source, target).getAsInt());
            }
        }
        return result;
    }

    public static <V, G extends Digraph<V>> G subgraph(Digraph<V> digraph, Set<V> vertices, DigraphFactory<? extends G> factory) {
        G subgraph = factory.create();
        for (V v : vertices) {
            if (!digraph.contains(v)) continue;
            subgraph.add(v);
            for (V w : digraph.targets(v)) {
                if (!vertices.contains(w)) continue;
                subgraph.put(v, w, digraph.get(v, w).getAsInt());
            }
        }
        return subgraph;
    }
}

