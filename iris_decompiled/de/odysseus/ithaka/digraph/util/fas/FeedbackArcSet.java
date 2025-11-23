/*
 * Decompiled with CFR 0.152.
 */
package de.odysseus.ithaka.digraph.util.fas;

import de.odysseus.ithaka.digraph.Digraph;
import de.odysseus.ithaka.digraph.Digraphs;
import de.odysseus.ithaka.digraph.UnmodifiableDigraph;
import de.odysseus.ithaka.digraph.util.fas.FeedbackArcSetPolicy;

public class FeedbackArcSet<V>
extends UnmodifiableDigraph<V> {
    private final FeedbackArcSetPolicy policy;
    private final boolean exact;
    private final int weight;

    public FeedbackArcSet(Digraph<V> feedback, int weight, FeedbackArcSetPolicy policy, boolean exact) {
        super(feedback);
        this.weight = weight;
        this.policy = policy;
        this.exact = exact;
    }

    public static <V> FeedbackArcSet<V> empty(FeedbackArcSetPolicy policy) {
        return new FeedbackArcSet(Digraphs.emptyDigraph(), 0, policy, true);
    }

    public boolean isExact() {
        return this.exact;
    }

    public int getWeight() {
        return this.weight;
    }

    public FeedbackArcSetPolicy getPolicy() {
        return this.policy;
    }
}

