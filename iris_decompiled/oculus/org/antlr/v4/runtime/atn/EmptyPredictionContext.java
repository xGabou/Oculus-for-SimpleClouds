/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import oculus.org.antlr.v4.runtime.atn.PredictionContext;
import oculus.org.antlr.v4.runtime.atn.SingletonPredictionContext;

public class EmptyPredictionContext
extends SingletonPredictionContext {
    public static final EmptyPredictionContext Instance = new EmptyPredictionContext();

    private EmptyPredictionContext() {
        super(null, Integer.MAX_VALUE);
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public PredictionContext getParent(int index) {
        return null;
    }

    @Override
    public int getReturnState(int index) {
        return this.returnState;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public String toString() {
        return "$";
    }
}

