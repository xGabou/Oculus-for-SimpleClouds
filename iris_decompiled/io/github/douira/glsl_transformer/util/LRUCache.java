/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class LRUCache<K, V>
extends LinkedHashMap<K, V> {
    private final int maxSize;

    public LRUCache(int maxSize, float loadFactor) {
        super((int)Math.ceil((float)maxSize / loadFactor) + 1, loadFactor, true);
        this.maxSize = maxSize;
    }

    public LRUCache(int maxSize) {
        this(maxSize, 0.75f);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size() > this.maxSize;
    }

    public V cachedGet(K key, Supplier<V> supplier) {
        Object value = this.get(key);
        if (value == null) {
            value = supplier.get();
            this.put(key, value);
        }
        return value;
    }

    public V cachedGetHydrateHit(K key, Supplier<V> supplier, Function<V, V> hydrator) {
        Object value = this.get(key);
        if (value == null) {
            value = supplier.get();
            this.put(key, value);
        } else {
            value = hydrator.apply(value);
        }
        return value;
    }
}

