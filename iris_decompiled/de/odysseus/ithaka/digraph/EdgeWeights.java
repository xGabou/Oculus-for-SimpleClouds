/*
 * Decompiled with CFR 0.152.
 */
package de.odysseus.ithaka.digraph;

import java.util.OptionalInt;

public interface EdgeWeights<V> {
    public static final OptionalInt UNIT_WEIGHT = OptionalInt.of(1);
    public static final EdgeWeights<Object> UNIT_WEIGHTS = (source, target) -> UNIT_WEIGHT;

    public OptionalInt get(V var1, V var2);
}

