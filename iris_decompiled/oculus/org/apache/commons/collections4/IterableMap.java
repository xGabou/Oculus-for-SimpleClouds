/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.apache.commons.collections4;

import java.util.Map;
import oculus.org.apache.commons.collections4.IterableGet;
import oculus.org.apache.commons.collections4.Put;

public interface IterableMap<K, V>
extends Map<K, V>,
Put<K, V>,
IterableGet<K, V> {
}

