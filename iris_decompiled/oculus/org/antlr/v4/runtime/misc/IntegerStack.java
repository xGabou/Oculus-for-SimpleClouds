/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.misc;

import oculus.org.antlr.v4.runtime.misc.IntegerList;

public class IntegerStack
extends IntegerList {
    public IntegerStack() {
    }

    public IntegerStack(int capacity) {
        super(capacity);
    }

    public IntegerStack(IntegerStack list) {
        super(list);
    }

    public final void push(int value) {
        this.add(value);
    }

    public final int pop() {
        return this.removeAt(this.size() - 1);
    }

    public final int peek() {
        return this.get(this.size() - 1);
    }
}

