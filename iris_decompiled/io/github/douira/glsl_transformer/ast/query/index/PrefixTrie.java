/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.query.index;

import io.github.douira.glsl_transformer.ast.query.index.PrefixQueryable;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import oculus.org.apache.commons.collections4.trie.PatriciaTrie;

public class PrefixTrie<S extends Set<E>, E>
extends PatriciaTrie<S>
implements PrefixQueryable<S, E> {
    public PrefixTrie() {
    }

    public PrefixTrie(Map<? extends String, ? extends S> m) {
        super(m);
    }

    @Override
    public Stream<S> prefixQuery(String prefix) {
        return this.prefixMap(prefix).values().stream();
    }
}

