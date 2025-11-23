/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import java.util.ArrayList;
import java.util.List;
import oculus.org.antlr.v4.runtime.atn.DecisionInfo;
import oculus.org.antlr.v4.runtime.atn.ProfilingATNSimulator;
import oculus.org.antlr.v4.runtime.dfa.DFA;

public class ParseInfo {
    protected final ProfilingATNSimulator atnSimulator;

    public ParseInfo(ProfilingATNSimulator atnSimulator) {
        this.atnSimulator = atnSimulator;
    }

    public DecisionInfo[] getDecisionInfo() {
        return this.atnSimulator.getDecisionInfo();
    }

    public List<Integer> getLLDecisions() {
        DecisionInfo[] decisions = this.atnSimulator.getDecisionInfo();
        ArrayList<Integer> LL = new ArrayList<Integer>();
        for (int i = 0; i < decisions.length; ++i) {
            long fallBack = decisions[i].LL_Fallback;
            if (fallBack <= 0L) continue;
            LL.add(i);
        }
        return LL;
    }

    public long getTotalTimeInPrediction() {
        DecisionInfo[] decisions = this.atnSimulator.getDecisionInfo();
        long t = 0L;
        for (int i = 0; i < decisions.length; ++i) {
            t += decisions[i].timeInPrediction;
        }
        return t;
    }

    public long getTotalSLLLookaheadOps() {
        DecisionInfo[] decisions = this.atnSimulator.getDecisionInfo();
        long k = 0L;
        for (int i = 0; i < decisions.length; ++i) {
            k += decisions[i].SLL_TotalLook;
        }
        return k;
    }

    public long getTotalLLLookaheadOps() {
        DecisionInfo[] decisions = this.atnSimulator.getDecisionInfo();
        long k = 0L;
        for (int i = 0; i < decisions.length; ++i) {
            k += decisions[i].LL_TotalLook;
        }
        return k;
    }

    public long getTotalSLLATNLookaheadOps() {
        DecisionInfo[] decisions = this.atnSimulator.getDecisionInfo();
        long k = 0L;
        for (int i = 0; i < decisions.length; ++i) {
            k += decisions[i].SLL_ATNTransitions;
        }
        return k;
    }

    public long getTotalLLATNLookaheadOps() {
        DecisionInfo[] decisions = this.atnSimulator.getDecisionInfo();
        long k = 0L;
        for (int i = 0; i < decisions.length; ++i) {
            k += decisions[i].LL_ATNTransitions;
        }
        return k;
    }

    public long getTotalATNLookaheadOps() {
        DecisionInfo[] decisions = this.atnSimulator.getDecisionInfo();
        long k = 0L;
        for (int i = 0; i < decisions.length; ++i) {
            k += decisions[i].SLL_ATNTransitions;
            k += decisions[i].LL_ATNTransitions;
        }
        return k;
    }

    public int getDFASize() {
        int n = 0;
        DFA[] decisionToDFA = this.atnSimulator.decisionToDFA;
        for (int i = 0; i < decisionToDFA.length; ++i) {
            n += this.getDFASize(i);
        }
        return n;
    }

    public int getDFASize(int decision) {
        DFA decisionToDFA = this.atnSimulator.decisionToDFA[decision];
        return decisionToDFA.states.size();
    }
}

