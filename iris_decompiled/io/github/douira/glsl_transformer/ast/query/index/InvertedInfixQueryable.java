/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.query.index;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

public interface InvertedInfixQueryable<S extends Set<E>, E> {
    public Stream<S> invertedInfixQuery(String var1, String var2);

    default public Stream<E> invertedInfixQueryFlat(String prefix, String suffix) {
        return this.invertedInfixQuery(prefix, suffix).flatMap(Collection::stream);
    }
}

