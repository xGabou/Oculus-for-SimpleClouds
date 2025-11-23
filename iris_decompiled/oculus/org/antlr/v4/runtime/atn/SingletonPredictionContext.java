/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import oculus.org.antlr.v4.runtime.atn.EmptyPredictionContext;
import oculus.org.antlr.v4.runtime.atn.PredictionContext;

public class SingletonPredictionContext
extends PredictionContext {
    public final PredictionContext parent;
    public final int returnState;

    SingletonPredictionContext(PredictionContext parent, int returnState) {
        super(parent != null ? SingletonPredictionContext.calculateHashCode(parent, returnState) : SingletonPredictionContext.calculateEmptyHashCode());
        assert (returnState != -1);
        this.parent = parent;
        this.returnState = returnState;
    }

    public static SingletonPredictionContext create(PredictionContext parent, int returnState) {
        if (returnState == Integer.MAX_VALUE && parent == null) {
            return EmptyPredictionContext.Instance;
        }
        return new SingletonPredictionContext(parent, returnState);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public PredictionContext getParent(int index) {
        assert (index == 0);
        return this.parent;
    }

    @Override
    public int getReturnState(int index) {
        assert (index == 0);
        return this.returnState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SingletonPredictionContext)) {
            return false;
        }
        if (this.hashCode() != o.hashCode()) {
            return false;
        }
        SingletonPredictionContext s = (SingletonPredictionContext)o;
        return this.returnState == s.returnState && this.parent != null && this.parent.equals(s.parent);
    }

    public String toString() {
        String up;
        String string = up = this.parent != null ? this.parent.toString() : "";
        if (up.length() == 0) {
            if (this.returnState == Integer.MAX_VALUE) {
                return "$";
            }
            return String.valueOf(this.returnState);
        }
        return String.valueOf(this.returnState) + " " + up;
    }
}

