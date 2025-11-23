/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import oculus.org.antlr.v4.runtime.atn.ATNState;
import oculus.org.antlr.v4.runtime.atn.Transition;
import oculus.org.antlr.v4.runtime.misc.IntervalSet;

public final class AtomTransition
extends Transition {
    public final int label;

    public AtomTransition(ATNState target, int label) {
        super(target);
        this.label = label;
    }

    @Override
    public int getSerializationType() {
        return 5;
    }

    @Override
    public IntervalSet label() {
        return IntervalSet.of(this.label);
    }

    @Override
    public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
        return this.label == symbol;
    }

    public String toString() {
        return String.valueOf(this.label);
    }
}

