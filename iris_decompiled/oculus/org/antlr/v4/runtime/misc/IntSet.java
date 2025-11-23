/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.misc;

import java.util.List;

public interface IntSet {
    public void add(int var1);

    public IntSet addAll(IntSet var1);

    public IntSet and(IntSet var1);

    public IntSet complement(IntSet var1);

    public IntSet or(IntSet var1);

    public IntSet subtract(IntSet var1);

    public int size();

    public boolean isNil();

    public boolean equals(Object var1);

    public boolean contains(int var1);

    public void remove(int var1);

    public List<Integer> toList();

    public String toString();
}

