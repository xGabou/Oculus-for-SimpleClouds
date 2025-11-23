/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.dfa;

import java.util.Arrays;
import java.util.List;
import oculus.org.antlr.v4.runtime.Vocabulary;
import oculus.org.antlr.v4.runtime.VocabularyImpl;
import oculus.org.antlr.v4.runtime.dfa.DFA;
import oculus.org.antlr.v4.runtime.dfa.DFAState;

public class DFASerializer {
    private final DFA dfa;
    private final Vocabulary vocabulary;

    @Deprecated
    public DFASerializer(DFA dfa, String[] tokenNames) {
        this(dfa, VocabularyImpl.fromTokenNames(tokenNames));
    }

    public DFASerializer(DFA dfa, Vocabulary vocabulary) {
        this.dfa = dfa;
        this.vocabulary = vocabulary;
    }

    public String toString() {
        if (this.dfa.s0 == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        List<DFAState> states = this.dfa.getStates();
        for (DFAState s : states) {
            int n = 0;
            if (s.edges != null) {
                n = s.edges.length;
            }
            for (int i = 0; i < n; ++i) {
                DFAState t = s.edges[i];
                if (t == null || t.stateNumber == Integer.MAX_VALUE) continue;
                buf.append(this.getStateString(s));
                String label = this.getEdgeLabel(i);
                buf.append("-").append(label).append("->").append(this.getStateString(t)).append('\n');
            }
        }
        String output = buf.toString();
        if (output.length() == 0) {
            return null;
        }
        return output;
    }

    protected String getEdgeLabel(int i) {
        return this.vocabulary.getDisplayName(i - 1);
    }

    protected String getStateString(DFAState s) {
        int n = s.stateNumber;
        String baseStateStr = (s.isAcceptState ? ":" : "") + "s" + n + (s.requiresFullContext ? "^" : "");
        if (s.isAcceptState) {
            if (s.predicates != null) {
                return baseStateStr + "=>" + Arrays.toString(s.predicates);
            }
            return baseStateStr + "=>" + s.prediction;
        }
        return baseStateStr;
    }
}

