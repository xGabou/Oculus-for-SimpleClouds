/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import oculus.org.antlr.v4.runtime.RuleContext;
import oculus.org.antlr.v4.runtime.atn.ATN;
import oculus.org.antlr.v4.runtime.atn.ATNConfig;
import oculus.org.antlr.v4.runtime.atn.ATNState;
import oculus.org.antlr.v4.runtime.atn.AbstractPredicateTransition;
import oculus.org.antlr.v4.runtime.atn.EmptyPredictionContext;
import oculus.org.antlr.v4.runtime.atn.NotSetTransition;
import oculus.org.antlr.v4.runtime.atn.PredictionContext;
import oculus.org.antlr.v4.runtime.atn.RuleStopState;
import oculus.org.antlr.v4.runtime.atn.RuleTransition;
import oculus.org.antlr.v4.runtime.atn.SingletonPredictionContext;
import oculus.org.antlr.v4.runtime.atn.Transition;
import oculus.org.antlr.v4.runtime.atn.WildcardTransition;
import oculus.org.antlr.v4.runtime.misc.IntervalSet;

public class LL1Analyzer {
    public static final int HIT_PRED = 0;
    public final ATN atn;

    public LL1Analyzer(ATN atn) {
        this.atn = atn;
    }

    public IntervalSet[] getDecisionLookahead(ATNState s) {
        if (s == null) {
            return null;
        }
        IntervalSet[] look = new IntervalSet[s.getNumberOfTransitions()];
        for (int alt = 0; alt < s.getNumberOfTransitions(); ++alt) {
            look[alt] = new IntervalSet(new int[0]);
            HashSet<ATNConfig> lookBusy = new HashSet<ATNConfig>();
            boolean seeThruPreds = false;
            this._LOOK(s.transition((int)alt).target, null, EmptyPredictionContext.Instance, look[alt], lookBusy, new BitSet(), seeThruPreds, false);
            if (look[alt].size() != 0 && !look[alt].contains(0)) continue;
            look[alt] = null;
        }
        return look;
    }

    public IntervalSet LOOK(ATNState s, RuleContext ctx) {
        return this.LOOK(s, null, ctx);
    }

    public IntervalSet LOOK(ATNState s, ATNState stopState, RuleContext ctx) {
        IntervalSet r = new IntervalSet(new int[0]);
        boolean seeThruPreds = true;
        PredictionContext lookContext = ctx != null ? PredictionContext.fromRuleContext(s.atn, ctx) : null;
        this._LOOK(s, stopState, lookContext, r, new HashSet<ATNConfig>(), new BitSet(), seeThruPreds, true);
        return r;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void _LOOK(ATNState s, ATNState stopState, PredictionContext ctx, IntervalSet look, Set<ATNConfig> lookBusy, BitSet calledRuleStack, boolean seeThruPreds, boolean addEOF) {
        ATNConfig c = new ATNConfig(s, 0, ctx);
        if (!lookBusy.add(c)) {
            return;
        }
        if (s == stopState) {
            if (ctx == null) {
                look.add(-2);
                return;
            }
            if (ctx.isEmpty() && addEOF) {
                look.add(-1);
                return;
            }
        }
        if (s instanceof RuleStopState) {
            if (ctx == null) {
                look.add(-2);
                return;
            }
            if (ctx.isEmpty() && addEOF) {
                look.add(-1);
                return;
            }
            if (ctx != EmptyPredictionContext.Instance) {
                boolean removed = calledRuleStack.get(s.ruleIndex);
                try {
                    calledRuleStack.clear(s.ruleIndex);
                    for (int i = 0; i < ctx.size(); ++i) {
                        ATNState returnState = this.atn.states.get(ctx.getReturnState(i));
                        this._LOOK(returnState, stopState, ctx.getParent(i), look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
                    }
                }
                finally {
                    if (removed) {
                        calledRuleStack.set(s.ruleIndex);
                    }
                }
                return;
            }
        }
        int n = s.getNumberOfTransitions();
        for (int i = 0; i < n; ++i) {
            Transition t = s.transition(i);
            if (t.getClass() == RuleTransition.class) {
                if (calledRuleStack.get(((RuleTransition)t).target.ruleIndex)) continue;
                SingletonPredictionContext newContext = SingletonPredictionContext.create(ctx, ((RuleTransition)t).followState.stateNumber);
                try {
                    calledRuleStack.set(((RuleTransition)t).target.ruleIndex);
                    this._LOOK(t.target, stopState, newContext, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
                    continue;
                }
                finally {
                    calledRuleStack.clear(((RuleTransition)t).target.ruleIndex);
                }
            }
            if (t instanceof AbstractPredicateTransition) {
                if (seeThruPreds) {
                    this._LOOK(t.target, stopState, ctx, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
                    continue;
                }
                look.add(0);
                continue;
            }
            if (t.isEpsilon()) {
                this._LOOK(t.target, stopState, ctx, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
                continue;
            }
            if (t.getClass() == WildcardTransition.class) {
                look.addAll(IntervalSet.of(1, this.atn.maxTokenType));
                continue;
            }
            IntervalSet set = t.label();
            if (set == null) continue;
            if (t instanceof NotSetTransition) {
                set = set.complement(IntervalSet.of(1, this.atn.maxTokenType));
            }
            look.addAll(set);
        }
    }
}

