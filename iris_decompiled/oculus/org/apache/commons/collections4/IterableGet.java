/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.apache.commons.collections4;

import oculus.org.apache.commons.collections4.Get;
import oculus.org.apache.commons.collections4.MapIterator;

public interface IterableGet<K, V>
extends Get<K, V> {
    public MapIterator<K, V> mapIterator();
}

