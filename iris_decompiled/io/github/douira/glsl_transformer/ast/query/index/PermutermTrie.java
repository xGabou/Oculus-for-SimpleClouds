/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.query.index;

import io.github.douira.glsl_transformer.ast.query.index.DuplicatorTrie;
import io.github.douira.glsl_transformer.ast.query.index.InfixQueryable;
import io.github.douira.glsl_transformer.ast.query.index.InvertedInfixQueryable;
import io.github.douira.glsl_transformer.ast.query.index.PrefixQueryable;
import io.github.douira.glsl_transformer.ast.query.index.SuffixQueryable;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class PermutermTrie<S extends Set<E>, E>
extends DuplicatorTrie<S>
implements PrefixQueryable<S, E>,
SuffixQueryable<S, E>,
InfixQueryable<S, E>,
InvertedInfixQueryable<S, E> {
    public PermutermTrie() {
    }

    public PermutermTrie(Map<? extends String, ? extends S> m) {
        super(m);
    }

    public PermutermTrie(char marker) {
        super(marker);
    }

    public PermutermTrie(Map<? extends String, ? extends S> m, char marker) {
        super(m, marker);
    }

    @Override
    protected void iterateKeyVariations(String key, Consumer<String> consumer) {
        int length = key.length();
        for (int i = 0; i <= length; ++i) {
            consumer.accept(key.substring(i) + this.marker + key.substring(0, i));
        }
    }

    @Override
    protected String prepareKey(Object k) {
        return super.prepareKey(k) + this.marker;
    }

    @Override
    public Stream<S> prefixQuery(String prefix) {
        return this.distinctPrefixQuery(this.marker + this.sanitizeKey(prefix));
    }

    @Override
    public Stream<S> suffixQuery(String suffix) {
        return this.distinctPrefixQuery(this.sanitizeKey(suffix) + this.marker);
    }

    @Override
    public Stream<S> infixQuery(String infix) {
        return this.distinctPrefixQuery(this.sanitizeKey(infix));
    }

    @Override
    public Stream<S> invertedInfixQuery(String prefix, String suffix) {
        return this.distinctPrefixQuery(this.sanitizeKey(suffix) + this.marker + this.sanitizeKey(prefix));
    }
}

