/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.query.index;

import io.github.douira.glsl_transformer.ast.query.index.ExternalDeclarationIndex;
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

public class PrefixExternalDeclarationIndex<S extends Set<ExternalDeclarationIndex.DeclarationEntry>, I extends PatriciaTrie<S>>
extends ExternalDeclarationIndex<S, I>
implements PrefixQueryable<S, ExternalDeclarationIndex.DeclarationEntry> {
    public PrefixExternalDeclarationIndex(I index, Supplier<S> setFactory) {
        super(index, setFactory);
    }

    public SortedMap<String, S> prefixMap(String key) {
        return ((PatriciaTrie)this.index).prefixMap(key);
    }

    @Override
    public Stream<S> prefixQuery(String key) {
        return ((PatriciaTrie)this.index).prefixMap(key).values().stream();
    }

    public static PrefixExternalDeclarationIndex<HashSet<ExternalDeclarationIndex.DeclarationEntry>, PrefixTrie<HashSet<ExternalDeclarationIndex.DeclarationEntry>, ExternalDeclarationIndex.DeclarationEntry>> withPrefix() {
        return new PrefixExternalDeclarationIndex<HashSet<ExternalDeclarationIndex.DeclarationEntry>, PrefixTrie<HashSet<ExternalDeclarationIndex.DeclarationEntry>, ExternalDeclarationIndex.DeclarationEntry>>(new PrefixTrie(), HashSet::new);
    }

    public static PrefixExternalDeclarationIndex<HashSet<ExternalDeclarationIndex.DeclarationEntry>, PrefixSuffixTrie<HashSet<ExternalDeclarationIndex.DeclarationEntry>, ExternalDeclarationIndex.DeclarationEntry>> withPrefixSuffix() {
        return new PrefixExternalDeclarationIndex<HashSet<ExternalDeclarationIndex.DeclarationEntry>, PrefixSuffixTrie<HashSet<ExternalDeclarationIndex.DeclarationEntry>, ExternalDeclarationIndex.DeclarationEntry>>(new PrefixSuffixTrie(), HashSet::new);
    }

    public static PrefixExternalDeclarationIndex<HashSet<ExternalDeclarationIndex.DeclarationEntry>, PermutermTrie<HashSet<ExternalDeclarationIndex.DeclarationEntry>, ExternalDeclarationIndex.DeclarationEntry>> withPermuterm() {
        return new PrefixExternalDeclarationIndex<HashSet<ExternalDeclarationIndex.DeclarationEntry>, PermutermTrie<HashSet<ExternalDeclarationIndex.DeclarationEntry>, ExternalDeclarationIndex.DeclarationEntry>>(new PermutermTrie(), HashSet::new);
    }

    public static <R extends Set<ExternalDeclarationIndex.DeclarationEntry>> PrefixExternalDeclarationIndex<R, PrefixTrie<R, ExternalDeclarationIndex.DeclarationEntry>> withPrefix(Supplier<R> setFactory) {
        return new PrefixExternalDeclarationIndex(new PrefixTrie(), setFactory);
    }

    public static <R extends Set<ExternalDeclarationIndex.DeclarationEntry>> PrefixExternalDeclarationIndex<R, PrefixSuffixTrie<R, ExternalDeclarationIndex.DeclarationEntry>> withPrefixSuffix(Supplier<R> setFactory) {
        return new PrefixExternalDeclarationIndex(new PrefixSuffixTrie(), setFactory);
    }

    public static <R extends Set<ExternalDeclarationIndex.DeclarationEntry>> PrefixExternalDeclarationIndex<R, PermutermTrie<R, ExternalDeclarationIndex.DeclarationEntry>> withPermuterm(Supplier<R> setFactory) {
        return new PrefixExternalDeclarationIndex(new PermutermTrie(), setFactory);
    }
}

