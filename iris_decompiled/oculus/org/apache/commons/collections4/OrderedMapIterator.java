/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.apache.commons.collections4;

import oculus.org.apache.commons.collections4.MapIterator;
import oculus.org.apache.commons.collections4.OrderedIterator;

public interface OrderedMapIterator<K, V>
extends MapIterator<K, V>,
OrderedIterator<K> {
    @Override
    public boolean hasPrevious();

    @Override
    public K previous();
}

