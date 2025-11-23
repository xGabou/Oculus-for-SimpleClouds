/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.query.index;

import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.query.index.IdentifierIndex;
import io.github.douira.glsl_transformer.ast.query.index.PermutermTrie;
import io.github.douira.glsl_transformer.ast.query.index.PrefixQueryable;
import io.github.douira.glsl_transformer.ast.query.index.PrefixSuffixTrie;
import io.github.douira.glsl_transformer.ast.query.index.PrefixTrie;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.function.Supplier;
import java.util.stream.Stream;
import oculus.org.apache.commons.collections4.trie.PatriciaTrie;

public class PrefixIdentifierIndex<S extends Set<Identifier>, I extends PatriciaTrie<S>>
extends IdentifierIndex<S, I>
implements PrefixQueryable<S, Identifier> {
    public PrefixIdentifierIndex(I index, Supplier<S> setFactory) {
        super(index, setFactory);
    }

    public SortedMap<String, S> prefixMap(String key) {
        return ((PatriciaTrie)this.index).prefixMap(key);
    }

    @Override
    public Stream<S> prefixQuery(String key) {
        return ((PatriciaTrie)this.index).prefixMap(key).values().stream();
    }

    public static PrefixIdentifierIndex<HashSet<Identifier>, PrefixTrie<HashSet<Identifier>, Identifier>> withPrefix() {
        return new PrefixIdentifierIndex<HashSet<Identifier>, PrefixTrie<HashSet<Identifier>, Identifier>>(new PrefixTrie(), HashSet::new);
    }

    public static PrefixIdentifierIndex<HashSet<Identifier>, PrefixSuffixTrie<HashSet<Identifier>, Identifier>> withPrefixSuffix() {
        return new PrefixIdentifierIndex<HashSet<Identifier>, PrefixSuffixTrie<HashSet<Identifier>, Identifier>>(new PrefixSuffixTrie(), HashSet::new);
    }

    public static PrefixIdentifierIndex<HashSet<Identifier>, PermutermTrie<HashSet<Identifier>, Identifier>> withPermuterm() {
        return new PrefixIdentifierIndex<HashSet<Identifier>, PermutermTrie<HashSet<Identifier>, Identifier>>(new PermutermTrie(), HashSet::new);
    }

    public static <R extends Set<Identifier>> PrefixIdentifierIndex<R, PrefixTrie<R, Identifier>> withPrefix(Supplier<R> setFactory) {
        return new PrefixIdentifierIndex(new PrefixTrie(), setFactory);
    }

    public static <R extends Set<Identifier>> PrefixIdentifierIndex<R, PrefixSuffixTrie<R, Identifier>> withPrefixSuffix(Supplier<R> setFactory) {
        return new PrefixIdentifierIndex(new PrefixSuffixTrie(), setFactory);
    }

    public static <R extends Set<Identifier>> PrefixIdentifierIndex<R, PermutermTrie<R, Identifier>> withPermuterm(Supplier<R> setFactory) {
        return new PrefixIdentifierIndex(new PermutermTrie(), setFactory);
    }
}

