/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import oculus.org.antlr.v4.runtime.atn.ATNState;
import oculus.org.antlr.v4.runtime.atn.AbstractPredicateTransition;
import oculus.org.antlr.v4.runtime.atn.SemanticContext;

public final class PredicateTransition
extends AbstractPredicateTransition {
    public final int ruleIndex;
    public final int predIndex;
    public final boolean isCtxDependent;

    public PredicateTransition(ATNState target, int ruleIndex, int predIndex, boolean isCtxDependent) {
        super(target);
        this.ruleIndex = ruleIndex;
        this.predIndex = predIndex;
        this.isCtxDependent = isCtxDependent;
    }

    @Override
    public int getSerializationType() {
        return 4;
    }

    @Override
    public boolean isEpsilon() {
        return true;
    }

    @Override
    public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
        return false;
    }

    public SemanticContext.Predicate getPredicate() {
        return new SemanticContext.Predicate(this.ruleIndex, this.predIndex, this.isCtxDependent);
    }

    public String toString() {
        return "pred_" + this.ruleIndex + ":" + this.predIndex;
    }
}

