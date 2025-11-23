/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.query.index;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

public interface PrefixQueryable<S extends Set<E>, E> {
    public Stream<S> prefixQuery(String var1);

    default public Stream<E> prefixQueryFlat(String prefix) {
        return this.prefixQuery(prefix).flatMap(Collection::stream);
    }
}

