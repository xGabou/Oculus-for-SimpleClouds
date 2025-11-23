/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.apache.commons.collections4;

import oculus.org.apache.commons.collections4.Trie;
import oculus.org.apache.commons.collections4.trie.UnmodifiableTrie;

public class TrieUtils {
    private TrieUtils() {
    }

    public static <K, V> Trie<K, V> unmodifiableTrie(Trie<K, ? extends V> trie) {
        return UnmodifiableTrie.unmodifiableTrie(trie);
    }
}

