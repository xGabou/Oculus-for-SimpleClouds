/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.query.index;

import io.github.douira.glsl_transformer.ast.query.index.DuplicatorTrie;
import io.github.douira.glsl_transformer.ast.query.index.PrefixQueryable;
import io.github.douira.glsl_transformer.ast.query.index.SuffixQueryable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class PrefixSuffixTrie<S extends Set<E>, E>
extends DuplicatorTrie<S>
implements PrefixQueryable<S, E>,
SuffixQueryable<S, E> {
    private static final int removeThreshold = 200;
    private Map<String, String> reverses = new HashMap<String, String>();

    public PrefixSuffixTrie() {
    }

    public PrefixSuffixTrie(Map<? extends String, ? extends S> m) {
        super(m);
    }

    public PrefixSuffixTrie(char marker) {
        super(marker);
    }

    public PrefixSuffixTrie(Map<? extends String, ? extends S> m, char marker) {
        super(m, marker);
    }

    private String getReverse(String key) {
        if (this.reverses.containsKey(key)) {
            return this.reverses.get(key);
        }
        String reverse = new StringBuilder(key).reverse().toString();
        this.reverses.put(key, reverse);
        this.reverses.put(reverse, key);
        return reverse;
    }

    @Override
    protected void iterateKeyVariations(String key, Consumer<String> consumer) {
        consumer.accept(key);
        consumer.accept(this.marker + this.getReverse(key));
    }

    @Override
    public S remove(Object k) {
        Set previous = (Set)super.remove(k);
        if (this.reverses.size() >= 200) {
            this.reverses.remove(this.reverses.remove(k));
        }
        return (S)previous;
    }

    @Override
    public Stream<S> prefixQuery(String prefix) {
        return this.distinctPrefixQuery(this.sanitizeKey(prefix));
    }

    @Override
    public Stream<S> suffixQuery(String suffix) {
        return this.distinctPrefixQuery(this.marker + this.getReverse(this.sanitizeKey(suffix)));
    }
}

