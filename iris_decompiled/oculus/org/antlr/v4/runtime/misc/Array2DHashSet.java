/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.misc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import oculus.org.antlr.v4.runtime.misc.AbstractEqualityComparator;
import oculus.org.antlr.v4.runtime.misc.MurmurHash;
import oculus.org.antlr.v4.runtime.misc.ObjectEqualityComparator;

public class Array2DHashSet<T>
implements Set<T> {
    public static final int INITAL_CAPACITY = 16;
    public static final int INITAL_BUCKET_CAPACITY = 8;
    public static final double LOAD_FACTOR = 0.75;
    protected final AbstractEqualityComparator<? super T> comparator;
    protected T[][] buckets;
    protected int n = 0;
    protected int currentPrime = 1;
    protected int threshold;
    protected final int initialCapacity;
    protected final int initialBucketCapacity;

    public Array2DHashSet() {
        this(null, 16, 8);
    }

    public Array2DHashSet(AbstractEqualityComparator<? super T> comparator) {
        this(comparator, 16, 8);
    }

    public Array2DHashSet(AbstractEqualityComparator<? super T> comparator, int initialCapacity, int initialBucketCapacity) {
        if (comparator == null) {
            comparator = ObjectEqualityComparator.INSTANCE;
        }
        this.comparator = comparator;
        this.initialCapacity = initialCapacity;
        this.initialBucketCapacity = initialBucketCapacity;
        this.buckets = this.createBuckets(initialCapacity);
        this.threshold = (int)Math.floor((double)initialCapacity * 0.75);
    }

    public final T getOrAdd(T o) {
        if (this.n > this.threshold) {
            this.expand();
        }
        return this.getOrAddImpl(o);
    }

    protected T getOrAddImpl(T o) {
        int b = this.getBucket(o);
        T[] bucket = this.buckets[b];
        if (bucket == null) {
            bucket = this.createBucket(this.initialBucketCapacity);
            bucket[0] = o;
            this.buckets[b] = bucket;
            ++this.n;
            return o;
        }
        for (int i = 0; i < bucket.length; ++i) {
            T existing = bucket[i];
            if (existing == null) {
                bucket[i] = o;
                ++this.n;
                return o;
            }
            if (!this.comparator.equals(existing, o)) continue;
            return existing;
        }
        int oldLength = bucket.length;
        bucket = Arrays.copyOf(bucket, bucket.length * 2);
        this.buckets[b] = bucket;
        bucket[oldLength] = o;
        ++this.n;
        return o;
    }

    public T get(T o) {
        if (o == null) {
            return o;
        }
        int b = this.getBucket(o);
        T[] bucket = this.buckets[b];
        if (bucket == null) {
            return null;
        }
        for (T e : bucket) {
            if (e == null) {
                return null;
            }
            if (!this.comparator.equals(e, o)) continue;
            return e;
        }
        return null;
    }

    protected final int getBucket(T o) {
        int hash = this.comparator.hashCode(o);
        int b = hash & this.buckets.length - 1;
        return b;
    }

    @Override
    public int hashCode() {
        int hash = MurmurHash.initialize();
        for (T[] bucket : this.buckets) {
            if (bucket == null) continue;
            for (T o : bucket) {
                if (o == null) break;
                hash = MurmurHash.update(hash, this.comparator.hashCode(o));
            }
        }
        hash = MurmurHash.finish(hash, this.size());
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Array2DHashSet)) {
            return false;
        }
        Array2DHashSet other = (Array2DHashSet)o;
        if (other.size() != this.size()) {
            return false;
        }
        boolean same = this.containsAll(other);
        return same;
    }

    protected void expand() {
        T[][] old = this.buckets;
        this.currentPrime += 4;
        int newCapacity = this.buckets.length * 2;
        T[][] newTable = this.createBuckets(newCapacity);
        int[] newBucketLengths = new int[newTable.length];
        this.buckets = newTable;
        this.threshold = (int)((double)newCapacity * 0.75);
        int oldSize = this.size();
        for (T[] bucket : old) {
            if (bucket == null) continue;
            for (T o : bucket) {
                T[] newBucket;
                if (o == null) break;
                int b = this.getBucket(o);
                int bucketLength = newBucketLengths[b];
                if (bucketLength == 0) {
                    newBucket = this.createBucket(this.initialBucketCapacity);
                    newTable[b] = newBucket;
                } else {
                    newBucket = newTable[b];
                    if (bucketLength == newBucket.length) {
                        newBucket = Arrays.copyOf(newBucket, newBucket.length * 2);
                        newTable[b] = newBucket;
                    }
                }
                newBucket[bucketLength] = o;
                int n = b;
                newBucketLengths[n] = newBucketLengths[n] + 1;
            }
        }
        assert (this.n == oldSize);
    }

    @Override
    public final boolean add(T t) {
        T existing = this.getOrAdd(t);
        return existing == t;
    }

    @Override
    public final int size() {
        return this.n;
    }

    @Override
    public final boolean isEmpty() {
        return this.n == 0;
    }

    @Override
    public final boolean contains(Object o) {
        return this.containsFast(this.asElementType(o));
    }

    public boolean containsFast(T obj) {
        if (obj == null) {
            return false;
        }
        return this.get(obj) != null;
    }

    @Override
    public Iterator<T> iterator() {
        return new SetIterator(this.toArray());
    }

    @Override
    public T[] toArray() {
        T[] a = this.createBucket(this.size());
        int i = 0;
        for (T[] bucket : this.buckets) {
            if (bucket == null) continue;
            for (T o : bucket) {
                if (o == null) break;
                a[i++] = o;
            }
        }
        return a;
    }

    @Override
    public <U> U[] toArray(U[] a) {
        if (a.length < this.size()) {
            a = Arrays.copyOf(a, this.size());
        }
        int i = 0;
        for (T[] bucket : this.buckets) {
            if (bucket == null) continue;
            for (T o : bucket) {
                if (o == null) break;
                T targetElement = o;
                a[i++] = targetElement;
            }
        }
        return a;
    }

    @Override
    public final boolean remove(Object o) {
        return this.removeFast(this.asElementType(o));
    }

    public boolean removeFast(T obj) {
        if (obj == null) {
            return false;
        }
        int b = this.getBucket(obj);
        T[] bucket = this.buckets[b];
        if (bucket == null) {
            return false;
        }
        for (int i = 0; i < bucket.length; ++i) {
            T e = bucket[i];
            if (e == null) {
                return false;
            }
            if (!this.comparator.equals(e, obj)) continue;
            System.arraycopy(bucket, i + 1, bucket, i, bucket.length - i - 1);
            bucket[bucket.length - 1] = null;
            --this.n;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        if (collection instanceof Array2DHashSet) {
            Array2DHashSet s = (Array2DHashSet)collection;
            for (T[] bucket : s.buckets) {
                if (bucket == null) continue;
                for (T o : bucket) {
                    if (o == null) break;
                    if (this.containsFast(this.asElementType(o))) continue;
                    return false;
                }
            }
        } else {
            for (Object o : collection) {
                if (this.containsFast(this.asElementType(o))) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean changed = false;
        for (T o : c) {
            T existing = this.getOrAdd(o);
            if (existing == o) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        int newsize = 0;
        for (T[] bucket : this.buckets) {
            int i;
            if (bucket == null) continue;
            int j = 0;
            for (i = 0; i < bucket.length && bucket[i] != null; ++i) {
                if (!c.contains(bucket[i])) continue;
                if (i != j) {
                    bucket[j] = bucket[i];
                }
                ++j;
                ++newsize;
            }
            newsize += j;
            while (j < i) {
                bucket[j] = null;
                ++j;
            }
        }
        boolean changed = newsize != this.n;
        this.n = newsize;
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (Object o : c) {
            changed |= this.removeFast(this.asElementType(o));
        }
        return changed;
    }

    @Override
    public void clear() {
        this.n = 0;
        this.buckets = this.createBuckets(this.initialCapacity);
        this.threshold = (int)Math.floor((double)this.initialCapacity * 0.75);
    }

    public String toString() {
        if (this.size() == 0) {
            return "{}";
        }
        StringBuilder buf = new StringBuilder();
        buf.append('{');
        boolean first = true;
        for (T[] bucket : this.buckets) {
            if (bucket == null) continue;
            for (T o : bucket) {
                if (o == null) break;
                if (first) {
                    first = false;
                } else {
                    buf.append(", ");
                }
                buf.append(o.toString());
            }
        }
        buf.append('}');
        return buf.toString();
    }

    public String toTableString() {
        StringBuilder buf = new StringBuilder();
        for (T[] bucket : this.buckets) {
            if (bucket == null) {
                buf.append("null\n");
                continue;
            }
            buf.append('[');
            boolean first = true;
            for (T o : bucket) {
                if (first) {
                    first = false;
                } else {
                    buf.append(" ");
                }
                if (o == null) {
                    buf.append("_");
                    continue;
                }
                buf.append(o.toString());
            }
            buf.append("]\n");
        }
        return buf.toString();
    }

    protected T asElementType(Object o) {
        return (T)o;
    }

    protected T[][] createBuckets(int capacity) {
        return new Object[capacity][];
    }

    protected T[] createBucket(int capacity) {
        return new Object[capacity];
    }

    protected class SetIterator
    implements Iterator<T> {
        final T[] data;
        int nextIndex = 0;
        boolean removed = true;

        public SetIterator(T[] data) {
            this.data = data;
        }

        @Override
        public boolean hasNext() {
            return this.nextIndex < this.data.length;
        }

        @Override
        public T next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.removed = false;
            return this.data[this.nextIndex++];
        }

        @Override
        public void remove() {
            if (this.removed) {
                throw new IllegalStateException();
            }
            Array2DHashSet.this.remove(this.data[this.nextIndex - 1]);
            this.removed = true;
        }
    }
}

