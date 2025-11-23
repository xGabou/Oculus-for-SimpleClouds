/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.query.index;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

public interface SuffixQueryable<S extends Set<E>, E> {
    public Stream<S> suffixQuery(String var1);

    default public Stream<E> suffixQueryFlat(String suffix) {
        return this.suffixQuery(suffix).flatMap(Collection::stream);
    }
}

