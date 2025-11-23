/*
 * Decompiled with CFR 0.152.
 */
package de.odysseus.ithaka.digraph;

import de.odysseus.ithaka.digraph.Digraph;

public interface DigraphFactory<G extends Digraph<?>> {
    public G create();
}

