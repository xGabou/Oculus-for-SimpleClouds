/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import oculus.org.antlr.v4.runtime.atn.ATNState;
import oculus.org.antlr.v4.runtime.atn.Transition;

public final class ActionTransition
extends Transition {
    public final int ruleIndex;
    public final int actionIndex;
    public final boolean isCtxDependent;

    public ActionTransition(ATNState target, int ruleIndex) {
        this(target, ruleIndex, -1, false);
    }

    public ActionTransition(ATNState target, int ruleIndex, int actionIndex, boolean isCtxDependent) {
        super(target);
        this.ruleIndex = ruleIndex;
        this.actionIndex = actionIndex;
        this.isCtxDependent = isCtxDependent;
    }

    @Override
    public int getSerializationType() {
        return 6;
    }

    @Override
    public boolean isEpsilon() {
        return true;
    }

    @Override
    public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
        return false;
    }

    public String toString() {
        return "action_" + this.ruleIndex + ":" + this.actionIndex;
    }
}

