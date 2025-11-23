/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public abstract class ProxyArrayList<V>
extends ArrayList<V> {
    private Set<V> elements = null;

    public ProxyArrayList() {
    }

    public ProxyArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public ProxyArrayList(Collection<? extends V> c) {
        this(c, true);
    }

    public ProxyArrayList(Collection<? extends V> c, boolean notifyInitial) {
        super(c);
        if (notifyInitial) {
            this.notifyAdditionSafe(c);
        }
    }

    protected abstract void notifyAddition(V var1);

    protected abstract void notifyRemoval(V var1);

    void notifyAdditionSafe(V added) {
        if (added != null) {
            this.notifyAddition(added);
            if (this.elements != null) {
                this.elements.add(added);
            }
        }
    }

    void notifyAdditionSafe(Collection<? extends V> collection) {
        for (V element : collection) {
            this.notifyAdditionSafe(element);
        }
    }

    private void notifyRemovalSafe(V removed) {
        if (removed != null) {
            this.notifyRemoval(removed);
            if (this.elements != null) {
                this.elements.remove(removed);
            }
        }
    }

    private void notifyRemovalSafe(Collection<? extends V> collection) {
        for (V element : collection) {
            this.notifyRemovalSafe(element);
        }
    }

    private Set<V> getElements() {
        if (this.elements == null) {
            this.elements = new HashSet<V>(this);
        }
        return this.elements;
    }

    @Override
    public V set(int index, V element) {
        V prev = super.set(index, element);
        if (prev != element) {
            this.notifyRemovalSafe(prev);
            this.notifyAdditionSafe(element);
        }
        return prev;
    }

    @Override
    public boolean add(V e) {
        boolean result = super.add(e);
        this.notifyAdditionSafe(e);
        return result;
    }

    @Override
    public void add(int index, V element) {
        super.add(index, element);
        this.notifyAdditionSafe(element);
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        boolean result = super.addAll(c);
        if (result) {
            this.notifyAdditionSafe(c);
        }
        return result;
    }

    @Override
    public boolean addAll(int index, Collection<? extends V> c) {
        boolean result = super.addAll(index, c);
        if (result) {
            this.notifyAdditionSafe(c);
        }
        return result;
    }

    @Override
    public V remove(int index) {
        Object removed = super.remove(index);
        this.notifyRemovalSafe(removed);
        return (V)removed;
    }

    @Override
    public boolean remove(Object o) {
        boolean result = super.remove(o);
        if (result) {
            this.notifyRemovalSafe(o);
        }
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean result = super.removeAll(c);
        if (result) {
            for (Object element : this) {
                if (!this.getElements().contains(element)) continue;
                this.notifyRemovalSafe(element);
            }
        }
        return result;
    }

    @Override
    public void replaceAll(UnaryOperator<V> operator) {
        for (int i = 0; i < this.size(); ++i) {
            this.set(i, (V)operator.apply(this.get(i)));
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean result = super.retainAll(c);
        if (result) {
            for (Object element : this) {
                if (this.getElements().contains(element)) continue;
                this.notifyRemovalSafe(element);
            }
        }
        return result;
    }

    @Override
    public boolean removeIf(Predicate<? super V> filter) {
        boolean result = super.removeIf(filter);
        if (result) {
            for (Object element : this) {
                if (!filter.test(element)) continue;
                this.notifyRemovalSafe(element);
            }
        }
        return result;
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        for (int i = fromIndex; i < toIndex; ++i) {
            this.notifyRemovalSafe(this.get(i));
        }
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public void clear() {
        if (this.elements != null) {
            this.elements.clear();
        }
        this.notifyRemovalSafe(this);
        super.clear();
    }

    @Override
    public ProxyArrayList<V> clone() {
        ProxyArrayList result = (ProxyArrayList)super.clone();
        result.elements = null;
        return result;
    }
}

