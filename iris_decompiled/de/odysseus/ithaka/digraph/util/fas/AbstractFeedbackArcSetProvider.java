/*
 * Decompiled with CFR 0.152.
 */
package de.odysseus.ithaka.digraph.util.fas;

import de.odysseus.ithaka.digraph.Digraph;
import de.odysseus.ithaka.digraph.Digraphs;
import de.odysseus.ithaka.digraph.EdgeWeights;
import de.odysseus.ithaka.digraph.MapDigraph;
import de.odysseus.ithaka.digraph.util.fas.FeedbackArcSet;
import de.odysseus.ithaka.digraph.util.fas.FeedbackArcSetPolicy;
import de.odysseus.ithaka.digraph.util.fas.FeedbackArcSetProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class AbstractFeedbackArcSetProvider
implements FeedbackArcSetProvider {
    private final ExecutorService executor;

    protected AbstractFeedbackArcSetProvider() {
        this.executor = null;
    }

    protected AbstractFeedbackArcSetProvider(int numberOfThreads) {
        this.executor = numberOfThreads > 0 ? Executors.newFixedThreadPool(numberOfThreads) : null;
    }

    protected <V> Digraph<V> mfas(Digraph<V> digraph, EdgeWeights<? super V> weights) {
        return null;
    }

    protected abstract <V> Digraph<V> lfas(Digraph<V> var1, EdgeWeights<? super V> var2);

    private <V> FeedbackArcSet<V> fas(Digraph<V> digraph, EdgeWeights<? super V> weights, FeedbackArcSetPolicy policy) {
        EdgeWeights filteredWeights = weights;
        if (policy == FeedbackArcSetPolicy.MIN_SIZE) {
            final EdgeWeights origWeights = weights;
            final int delta = this.totalWeight(digraph, origWeights);
            filteredWeights = new EdgeWeights<V>(){

                @Override
                public OptionalInt get(V source, V target) {
                    OptionalInt original = origWeights.get(source, target);
                    if (original.isPresent()) {
                        return OptionalInt.of(original.getAsInt() + delta);
                    }
                    return OptionalInt.empty();
                }
            };
        }
        Digraph<? super V> result = this.mfas(digraph, filteredWeights);
        boolean exact = true;
        if (result == null) {
            result = this.lfas(digraph, filteredWeights);
            exact = false;
        }
        return new FeedbackArcSet<V>(result, this.totalWeight(result, weights), policy, exact);
    }

    protected <V> int totalWeight(Digraph<V> digraph, EdgeWeights<? super V> weights) {
        int weight = 0;
        for (V source : digraph.vertices()) {
            for (V target : digraph.targets(source)) {
                weight += weights.get(source, target).getAsInt();
            }
        }
        return weight;
    }

    private <V> List<FeedbackArcSet<V>> executeAll(List<FeedbackTask<V>> tasks) {
        ArrayList<FeedbackArcSet<V>> result = new ArrayList<FeedbackArcSet<V>>();
        if (this.executor == null) {
            for (FeedbackTask<V> task : tasks) {
                result.add((FeedbackArcSet<V>)task.call());
            }
        } else {
            try {
                for (Future<V> future : this.executor.invokeAll(tasks)) {
                    result.add((FeedbackArcSet)future.get());
                }
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        }
        return result;
    }

    @Override
    public <V> FeedbackArcSet<V> getFeedbackArcSet(Digraph<V> digraph, EdgeWeights<? super V> weights, FeedbackArcSetPolicy policy) {
        if (Digraphs.isTriviallyAcyclic(digraph)) {
            return FeedbackArcSet.empty(policy);
        }
        List<Set<V>> components = Digraphs.scc(digraph);
        if (components.size() == digraph.getVertexCount()) {
            return FeedbackArcSet.empty(policy);
        }
        if (components.size() == 1) {
            return this.fas(digraph, weights, policy);
        }
        ArrayList<FeedbackTask<V>> tasks = new ArrayList<FeedbackTask<V>>();
        for (Set<V> component : components) {
            if (component.size() <= 1) continue;
            tasks.add(new FeedbackTask<V>(digraph, weights, policy, component));
        }
        List<FeedbackArcSet<V>> feedbacks = this.executeAll(tasks);
        if (feedbacks == null) {
            return null;
        }
        int weight = 0;
        boolean exact = true;
        MapDigraph result = new MapDigraph();
        for (FeedbackArcSet feedback : feedbacks) {
            for (Object source : feedback.vertices()) {
                for (Object target : feedback.targets(source)) {
                    result.put(source, target, digraph.get(source, target).getAsInt());
                }
            }
            exact &= feedback.isExact();
            weight += feedback.getWeight();
        }
        return new FeedbackArcSet(result, weight, policy, exact);
    }

    class FeedbackTask<V>
    implements Callable<FeedbackArcSet<V>> {
        final Digraph<V> digraph;
        final EdgeWeights<? super V> weights;
        final FeedbackArcSetPolicy policy;
        final Set<V> scc;

        FeedbackTask(Digraph<V> digraph, EdgeWeights<? super V> weights, FeedbackArcSetPolicy policy, Set<V> scc) {
            this.digraph = digraph;
            this.weights = weights;
            this.policy = policy;
            this.scc = scc;
        }

        @Override
        public FeedbackArcSet<V> call() {
            return AbstractFeedbackArcSetProvider.this.fas(this.digraph.subgraph(this.scc), this.weights, this.policy);
        }
    }
}

