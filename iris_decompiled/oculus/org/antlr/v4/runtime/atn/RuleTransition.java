/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import oculus.org.antlr.v4.runtime.atn.ATNState;
import oculus.org.antlr.v4.runtime.atn.RuleStartState;
import oculus.org.antlr.v4.runtime.atn.Transition;

public final class RuleTransition
extends Transition {
    public final int ruleIndex;
    public final int precedence;
    public ATNState followState;

    @Deprecated
    public RuleTransition(RuleStartState ruleStart, int ruleIndex, ATNState followState) {
        this(ruleStart, ruleIndex, 0, followState);
    }

    public RuleTransition(RuleStartState ruleStart, int ruleIndex, int precedence, ATNState followState) {
        super(ruleStart);
        this.ruleIndex = ruleIndex;
        this.precedence = precedence;
        this.followState = followState;
    }

    @Override
    public int getSerializationType() {
        return 3;
    }

    @Override
    public boolean isEpsilon() {
        return true;
    }

    @Override
    public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
        return false;
    }
}

