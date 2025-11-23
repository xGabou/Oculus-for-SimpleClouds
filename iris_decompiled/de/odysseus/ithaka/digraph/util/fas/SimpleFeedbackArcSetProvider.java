/*
 * Decompiled with CFR 0.152.
 */
package de.odysseus.ithaka.digraph.util.fas;

import de.odysseus.ithaka.digraph.Digraph;
import de.odysseus.ithaka.digraph.DigraphFactory;
import de.odysseus.ithaka.digraph.Digraphs;
import de.odysseus.ithaka.digraph.EdgeWeights;
import de.odysseus.ithaka.digraph.MapDigraph;
import de.odysseus.ithaka.digraph.util.fas.AbstractFeedbackArcSetProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class SimpleFeedbackArcSetProvider
extends AbstractFeedbackArcSetProvider {
    public SimpleFeedbackArcSetProvider() {
    }

    public SimpleFeedbackArcSetProvider(int numberOfThreads) {
        super(numberOfThreads);
    }

    private <V> List<Digraph<V>> copies(Digraph<V> digraph, int count) {
        ArrayList<Digraph<V>> copies = new ArrayList<Digraph<V>>();
        copies.add(digraph);
        ArrayList<Integer> shuffle = new ArrayList<Integer>();
        final HashMap<V, Integer> order = new HashMap<V, Integer>();
        int index = 0;
        for (V source : digraph.vertices()) {
            order.put(source, index);
            shuffle.add(index++);
        }
        Random random = new Random(7L);
        for (int i = 0; i < count; ++i) {
            Collections.shuffle(shuffle, random);
            final ArrayList mapping = new ArrayList(shuffle);
            copies.add((Digraph<V>)Digraphs.copy(digraph, new DigraphFactory<Digraph<V>>(){

                @Override
                public Digraph<V> create() {
                    return new MapDigraph(new Comparator<V>(){

                        @Override
                        public int compare(V v1, V v2) {
                            int value1 = (Integer)mapping.get((Integer)order.get(v1));
                            int value2 = (Integer)mapping.get((Integer)order.get(v2));
                            return Integer.compare(value1, value2);
                        }
                    });
                }
            }));
        }
        return copies;
    }

    @Override
    protected <V> Digraph<V> lfas(Digraph<V> tangle, EdgeWeights<? super V> weights) {
        int minWeight = Integer.MAX_VALUE;
        int minSize = Integer.MAX_VALUE;
        ArrayList minFinished = null;
        int maxIterationsLeft = Math.max(1, 1000000 / (tangle.getVertexCount() + tangle.getEdgeCount()));
        List<Digraph<V>> copies = this.copies(tangle, Math.min(10, tangle.getVertexCount()));
        ArrayList finished = new ArrayList(tangle.getVertexCount());
        HashSet discovered = new HashSet(tangle.getVertexCount());
        for (V start : tangle.vertices()) {
            for (Digraph<V> copy : copies) {
                finished.clear();
                discovered.clear();
                Digraphs.dfs(copy, start, discovered, finished);
                assert (finished.size() == tangle.getVertexCount());
                int weight = 0;
                int size = 0;
                discovered.clear();
                for (Object source : finished) {
                    discovered.add(source);
                    for (V target : tangle.targets(source)) {
                        if (discovered.contains(target)) continue;
                        weight += weights.get(source, target).getAsInt();
                        ++size;
                    }
                    if (weight <= minWeight) continue;
                    break;
                }
                if (weight >= minWeight && (weight != minWeight || size >= minSize)) continue;
                minFinished = new ArrayList(finished);
                minWeight = weight;
                minSize = size;
            }
            if (--maxIterationsLeft != 0) continue;
            break;
        }
        Objects.requireNonNull(minFinished);
        MapDigraph feedback = MapDigraph.getDefaultDigraphFactory().create();
        discovered.clear();
        for (Object source : minFinished) {
            discovered.add(source);
            for (V target : tangle.targets(source)) {
                if (discovered.contains(target)) continue;
                feedback.put(source, target, tangle.get(source, target).getAsInt());
            }
        }
        return feedback;
    }
}

