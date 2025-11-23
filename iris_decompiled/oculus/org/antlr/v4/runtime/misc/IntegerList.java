/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.misc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class IntegerList {
    private static final int[] EMPTY_DATA = new int[0];
    private static final int INITIAL_SIZE = 4;
    private static final int MAX_ARRAY_SIZE = 0x7FFFFFF7;
    private int[] _data;
    private int _size;

    public IntegerList() {
        this._data = EMPTY_DATA;
    }

    public IntegerList(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException();
        }
        this._data = capacity == 0 ? EMPTY_DATA : new int[capacity];
    }

    public IntegerList(IntegerList list) {
        this._data = (int[])list._data.clone();
        this._size = list._size;
    }

    public IntegerList(Collection<Integer> list) {
        this(list.size());
        for (Integer value : list) {
            this.add(value);
        }
    }

    public final void add(int value) {
        if (this._data.length == this._size) {
            this.ensureCapacity(this._size + 1);
        }
        this._data[this._size] = value;
        ++this._size;
    }

    public final void addAll(int[] array) {
        this.ensureCapacity(this._size + array.length);
        System.arraycopy(array, 0, this._data, this._size, array.length);
        this._size += array.length;
    }

    public final void addAll(IntegerList list) {
        this.ensureCapacity(this._size + list._size);
        System.arraycopy(list._data, 0, this._data, this._size, list._size);
        this._size += list._size;
    }

    public final void addAll(Collection<Integer> list) {
        this.ensureCapacity(this._size + list.size());
        int current = 0;
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            int x;
            this._data[this._size + current] = x = iterator.next().intValue();
            ++current;
        }
        this._size += list.size();
    }

    public final int get(int index) {
        if (index < 0 || index >= this._size) {
            throw new IndexOutOfBoundsException();
        }
        return this._data[index];
    }

    public final boolean contains(int value) {
        for (int i = 0; i < this._size; ++i) {
            if (this._data[i] != value) continue;
            return true;
        }
        return false;
    }

    public final int set(int index, int value) {
        if (index < 0 || index >= this._size) {
            throw new IndexOutOfBoundsException();
        }
        int previous = this._data[index];
        this._data[index] = value;
        return previous;
    }

    public final int removeAt(int index) {
        int value = this.get(index);
        System.arraycopy(this._data, index + 1, this._data, index, this._size - index - 1);
        this._data[this._size - 1] = 0;
        --this._size;
        return value;
    }

    public final void removeRange(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex < 0 || fromIndex > this._size || toIndex > this._size) {
            throw new IndexOutOfBoundsException();
        }
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException();
        }
        System.arraycopy(this._data, toIndex, this._data, fromIndex, this._size - toIndex);
        Arrays.fill(this._data, this._size - (toIndex - fromIndex), this._size, 0);
        this._size -= toIndex - fromIndex;
    }

    public final boolean isEmpty() {
        return this._size == 0;
    }

    public final int size() {
        return this._size;
    }

    public final void trimToSize() {
        if (this._data.length == this._size) {
            return;
        }
        this._data = Arrays.copyOf(this._data, this._size);
    }

    public final void clear() {
        Arrays.fill(this._data, 0, this._size, 0);
        this._size = 0;
    }

    public final int[] toArray() {
        if (this._size == 0) {
            return EMPTY_DATA;
        }
        return Arrays.copyOf(this._data, this._size);
    }

    public final void sort() {
        Arrays.sort(this._data, 0, this._size);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof IntegerList)) {
            return false;
        }
        IntegerList other = (IntegerList)o;
        if (this._size != other._size) {
            return false;
        }
        for (int i = 0; i < this._size; ++i) {
            if (this._data[i] == other._data[i]) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        for (int i = 0; i < this._size; ++i) {
            hashCode = 31 * hashCode + this._data[i];
        }
        return hashCode;
    }

    public String toString() {
        return Arrays.toString(this.toArray());
    }

    public final int binarySearch(int key) {
        return Arrays.binarySearch(this._data, 0, this._size, key);
    }

    public final int binarySearch(int fromIndex, int toIndex, int key) {
        if (fromIndex < 0 || toIndex < 0 || fromIndex > this._size || toIndex > this._size) {
            throw new IndexOutOfBoundsException();
        }
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException();
        }
        return Arrays.binarySearch(this._data, fromIndex, toIndex, key);
    }

    private void ensureCapacity(int capacity) {
        if (capacity < 0 || capacity > 0x7FFFFFF7) {
            throw new OutOfMemoryError();
        }
        int newLength = this._data.length == 0 ? 4 : this._data.length;
        while (newLength < capacity) {
            if ((newLength *= 2) >= 0 && newLength <= 0x7FFFFFF7) continue;
            newLength = 0x7FFFFFF7;
        }
        this._data = Arrays.copyOf(this._data, newLength);
    }

    public final char[] toCharArray() {
        char[] resultArray = new char[this._size];
        int resultIdx = 0;
        boolean calculatedPreciseResultSize = false;
        for (int i = 0; i < this._size; ++i) {
            int codePoint = this._data[i];
            if (!calculatedPreciseResultSize && Character.isSupplementaryCodePoint(codePoint)) {
                resultArray = Arrays.copyOf(resultArray, this.charArraySize());
                calculatedPreciseResultSize = true;
            }
            int charsWritten = Character.toChars(codePoint, resultArray, resultIdx);
            resultIdx += charsWritten;
        }
        return resultArray;
    }

    private int charArraySize() {
        int result = 0;
        for (int i = 0; i < this._size; ++i) {
            result += Character.charCount(this._data[i]);
        }
        return result;
    }
}

