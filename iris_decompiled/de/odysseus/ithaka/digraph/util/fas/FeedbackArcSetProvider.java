/*
 * Decompiled with CFR 0.152.
 */
package de.odysseus.ithaka.digraph.util.fas;

import de.odysseus.ithaka.digraph.Digraph;
import de.odysseus.ithaka.digraph.EdgeWeights;
import de.odysseus.ithaka.digraph.util.fas.FeedbackArcSet;
import de.odysseus.ithaka.digraph.util.fas.FeedbackArcSetPolicy;

public interface FeedbackArcSetProvider {
    public <V> FeedbackArcSet<V> getFeedbackArcSet(Digraph<V> var1, EdgeWeights<? super V> var2, FeedbackArcSetPolicy var3);
}

