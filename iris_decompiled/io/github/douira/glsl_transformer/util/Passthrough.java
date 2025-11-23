/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.util;

import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
public interface Passthrough<V>
extends Function<V, V> {
    @Override
    public V apply(V var1);

    public static <V> Passthrough<V> of(Function<V, V> f) {
        return f::apply;
    }

    public static <V> Passthrough<V> of(Consumer<V> f) {
        return v -> {
            f.accept(v);
            return v;
        };
    }
}

