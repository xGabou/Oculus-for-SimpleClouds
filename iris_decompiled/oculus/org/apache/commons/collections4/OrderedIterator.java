/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.apache.commons.collections4;

import java.util.Iterator;

public interface OrderedIterator<E>
extends Iterator<E> {
    public boolean hasPrevious();

    public E previous();
}

