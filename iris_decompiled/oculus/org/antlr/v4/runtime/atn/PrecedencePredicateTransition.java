/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import oculus.org.antlr.v4.runtime.atn.ATNState;
import oculus.org.antlr.v4.runtime.atn.AbstractPredicateTransition;
import oculus.org.antlr.v4.runtime.atn.SemanticContext;

public final class PrecedencePredicateTransition
extends AbstractPredicateTransition {
    public final int precedence;

    public PrecedencePredicateTransition(ATNState target, int precedence) {
        super(target);
        this.precedence = precedence;
    }

    @Override
    public int getSerializationType() {
        return 10;
    }

    @Override
    public boolean isEpsilon() {
        return true;
    }

    @Override
    public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
        return false;
    }

    public SemanticContext.PrecedencePredicate getPredicate() {
        return new SemanticContext.PrecedencePredicate(this.precedence);
    }

    public String toString() {
        return this.precedence + " >= _p";
    }
}

