/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import java.util.Arrays;
import oculus.org.antlr.v4.runtime.atn.PredictionContext;
import oculus.org.antlr.v4.runtime.atn.SingletonPredictionContext;

public class ArrayPredictionContext
extends PredictionContext {
    public final PredictionContext[] parents;
    public final int[] returnStates;

    public ArrayPredictionContext(SingletonPredictionContext a) {
        this(new PredictionContext[]{a.parent}, new int[]{a.returnState});
    }

    public ArrayPredictionContext(PredictionContext[] parents, int[] returnStates) {
        super(ArrayPredictionContext.calculateHashCode(parents, returnStates));
        assert (parents != null && parents.length > 0);
        assert (returnStates != null && returnStates.length > 0);
        this.parents = parents;
        this.returnStates = returnStates;
    }

    @Override
    public boolean isEmpty() {
        return this.returnStates[0] == Integer.MAX_VALUE;
    }

    @Override
    public int size() {
        return this.returnStates.length;
    }

    @Override
    public PredictionContext getParent(int index) {
        return this.parents[index];
    }

    @Override
    public int getReturnState(int index) {
        return this.returnStates[index];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArrayPredictionContext)) {
            return false;
        }
        if (this.hashCode() != o.hashCode()) {
            return false;
        }
        ArrayPredictionContext a = (ArrayPredictionContext)o;
        return Arrays.equals(this.returnStates, a.returnStates) && Arrays.equals(this.parents, a.parents);
    }

    public String toString() {
        if (this.isEmpty()) {
            return "[]";
        }
        StringBuilder buf = new StringBuilder();
        buf.append("[");
        for (int i = 0; i < this.returnStates.length; ++i) {
            if (i > 0) {
                buf.append(", ");
            }
            if (this.returnStates[i] == Integer.MAX_VALUE) {
                buf.append("$");
                continue;
            }
            buf.append(this.returnStates[i]);
            if (this.parents[i] != null) {
                buf.append(' ');
                buf.append(this.parents[i].toString());
                continue;
            }
            buf.append("null");
        }
        buf.append("]");
        return buf.toString();
    }
}

