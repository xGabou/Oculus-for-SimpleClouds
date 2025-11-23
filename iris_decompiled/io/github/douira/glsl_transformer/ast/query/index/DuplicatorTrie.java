/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.query.index;

import java.util.Map;
import java.util.SortedMap;
import java.util.function.Consumer;
import java.util.stream.Stream;
import oculus.org.apache.commons.collections4.trie.PatriciaTrie;

public abstract class DuplicatorTrie<E>
extends PatriciaTrie<E> {
    public static final char DEFAULT_MARKER = '$';
    protected final char marker;

    public DuplicatorTrie() {
        this.marker = (char)36;
    }

    public DuplicatorTrie(Map<? extends String, ? extends E> m) {
        super(m);
        this.marker = (char)36;
    }

    public DuplicatorTrie(char marker) {
        this.marker = marker;
    }

    public DuplicatorTrie(Map<? extends String, ? extends E> m, char marker) {
        super(m);
        this.marker = marker;
    }

    protected abstract void iterateKeyVariations(String var1, Consumer<String> var2);

    protected String prepareKey(Object k) {
        return this.sanitizeKey(k.toString());
    }

    protected String sanitizeKey(String key) {
        if (key.indexOf(this.marker) >= 0) {
            throw new IllegalArgumentException("Key cannot contain marker");
        }
        return key;
    }

    @Override
    public boolean containsKey(Object k) {
        return super.containsKey(this.prepareKey(k));
    }

    @Override
    public E get(Object k) {
        return (E)super.get(this.prepareKey(k));
    }

    @Override
    public SortedMap<String, E> headMap(String toKey) {
        return super.headMap(this.prepareKey(toKey));
    }

    @Override
    public String nextKey(String key) {
        return (String)super.nextKey(this.prepareKey(key));
    }

    @Override
    public SortedMap<String, E> prefixMap(String key) {
        return super.prefixMap(this.prepareKey(key));
    }

    public SortedMap<String, E> prefixMapRaw(String key) {
        return super.prefixMap(key);
    }

    @Override
    public String previousKey(String key) {
        return (String)super.previousKey(this.prepareKey(key));
    }

    @Override
    public E put(String key, E value) {
        E previous = this.get(key);
        this.iterateKeyVariations(key, k -> super.put(k, value));
        return previous;
    }

    @Override
    public E remove(Object k) {
        E previous = this.get(k);
        this.iterateKeyVariations((String)k, x$0 -> super.remove(x$0));
        return previous;
    }

    @Override
    public Map.Entry<String, E> select(String key) {
        return super.select(this.prepareKey(key));
    }

    @Override
    public String selectKey(String key) {
        return (String)super.selectKey(this.prepareKey(key));
    }

    @Override
    public SortedMap<String, E> subMap(String fromKey, String toKey) {
        return super.subMap(this.prepareKey(fromKey), this.prepareKey(toKey));
    }

    @Override
    public SortedMap<String, E> tailMap(String fromKey) {
        return super.tailMap(this.prepareKey(fromKey));
    }

    protected Stream<E> distinctPrefixQuery(String prefix) {
        return ((Stream)super.prefixMap(prefix).values().stream().unordered()).distinct();
    }

    public static final class Holder<V> {
        public V value;

        public Holder(V value) {
            this.value = value;
        }

        public V getValue() {
            return this.value;
        }
    }
}

