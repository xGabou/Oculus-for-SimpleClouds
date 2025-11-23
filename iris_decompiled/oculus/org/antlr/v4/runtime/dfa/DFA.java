/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.dfa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oculus.org.antlr.v4.runtime.Vocabulary;
import oculus.org.antlr.v4.runtime.VocabularyImpl;
import oculus.org.antlr.v4.runtime.atn.ATNConfigSet;
import oculus.org.antlr.v4.runtime.atn.DecisionState;
import oculus.org.antlr.v4.runtime.atn.StarLoopEntryState;
import oculus.org.antlr.v4.runtime.dfa.DFASerializer;
import oculus.org.antlr.v4.runtime.dfa.DFAState;
import oculus.org.antlr.v4.runtime.dfa.LexerDFASerializer;

public class DFA {
    public final Map<DFAState, DFAState> states = new HashMap<DFAState, DFAState>();
    public volatile DFAState s0;
    public final int decision;
    public final DecisionState atnStartState;
    private final boolean precedenceDfa;

    public DFA(DecisionState atnStartState) {
        this(atnStartState, 0);
    }

    public DFA(DecisionState atnStartState, int decision) {
        this.atnStartState = atnStartState;
        this.decision = decision;
        boolean precedenceDfa = false;
        if (atnStartState instanceof StarLoopEntryState && ((StarLoopEntryState)atnStartState).isPrecedenceDecision) {
            precedenceDfa = true;
            DFAState precedenceState = new DFAState(new ATNConfigSet());
            precedenceState.edges = new DFAState[0];
            precedenceState.isAcceptState = false;
            precedenceState.requiresFullContext = false;
            this.s0 = precedenceState;
        }
        this.precedenceDfa = precedenceDfa;
    }

    public final boolean isPrecedenceDfa() {
        return this.precedenceDfa;
    }

    public final DFAState getPrecedenceStartState(int precedence) {
        if (!this.isPrecedenceDfa()) {
            throw new IllegalStateException("Only precedence DFAs may contain a precedence start state.");
        }
        if (precedence < 0 || precedence >= this.s0.edges.length) {
            return null;
        }
        return this.s0.edges[precedence];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void setPrecedenceStartState(int precedence, DFAState startState) {
        if (!this.isPrecedenceDfa()) {
            throw new IllegalStateException("Only precedence DFAs may contain a precedence start state.");
        }
        if (precedence < 0) {
            return;
        }
        DFAState dFAState = this.s0;
        synchronized (dFAState) {
            if (precedence >= this.s0.edges.length) {
                this.s0.edges = Arrays.copyOf(this.s0.edges, precedence + 1);
            }
            this.s0.edges[precedence] = startState;
        }
    }

    @Deprecated
    public final void setPrecedenceDfa(boolean precedenceDfa) {
        if (precedenceDfa != this.isPrecedenceDfa()) {
            throw new UnsupportedOperationException("The precedenceDfa field cannot change after a DFA is constructed.");
        }
    }

    public List<DFAState> getStates() {
        ArrayList<DFAState> result = new ArrayList<DFAState>(this.states.keySet());
        Collections.sort(result, new Comparator<DFAState>(){

            @Override
            public int compare(DFAState o1, DFAState o2) {
                return o1.stateNumber - o2.stateNumber;
            }
        });
        return result;
    }

    public String toString() {
        return this.toString(VocabularyImpl.EMPTY_VOCABULARY);
    }

    @Deprecated
    public String toString(String[] tokenNames) {
        if (this.s0 == null) {
            return "";
        }
        DFASerializer serializer = new DFASerializer(this, tokenNames);
        return serializer.toString();
    }

    public String toString(Vocabulary vocabulary) {
        if (this.s0 == null) {
            return "";
        }
        DFASerializer serializer = new DFASerializer(this, vocabulary);
        return serializer.toString();
    }

    public String toLexerString() {
        if (this.s0 == null) {
            return "";
        }
        LexerDFASerializer serializer = new LexerDFASerializer(this);
        return serializer.toString();
    }
}

