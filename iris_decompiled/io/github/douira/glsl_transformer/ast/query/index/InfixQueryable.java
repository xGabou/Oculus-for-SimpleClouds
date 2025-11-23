/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.query.index;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

public interface InfixQueryable<S extends Set<E>, E> {
    public Stream<S> infixQuery(String var1);

    default public Stream<E> infixQueryFlat(String infix) {
        return this.infixQuery(infix).flatMap(Collection::stream);
    }
}

