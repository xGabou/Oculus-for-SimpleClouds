/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import oculus.org.antlr.v4.runtime.atn.ATN;
import oculus.org.antlr.v4.runtime.atn.ATNDeserializer;
import oculus.org.antlr.v4.runtime.atn.ATNState;
import oculus.org.antlr.v4.runtime.atn.ATNType;
import oculus.org.antlr.v4.runtime.atn.ActionTransition;
import oculus.org.antlr.v4.runtime.atn.AtomTransition;
import oculus.org.antlr.v4.runtime.atn.BlockStartState;
import oculus.org.antlr.v4.runtime.atn.DecisionState;
import oculus.org.antlr.v4.runtime.atn.LexerAction;
import oculus.org.antlr.v4.runtime.atn.LexerChannelAction;
import oculus.org.antlr.v4.runtime.atn.LexerCustomAction;
import oculus.org.antlr.v4.runtime.atn.LexerModeAction;
import oculus.org.antlr.v4.runtime.atn.LexerPushModeAction;
import oculus.org.antlr.v4.runtime.atn.LexerTypeAction;
import oculus.org.antlr.v4.runtime.atn.LoopEndState;
import oculus.org.antlr.v4.runtime.atn.PrecedencePredicateTransition;
import oculus.org.antlr.v4.runtime.atn.PredicateTransition;
import oculus.org.antlr.v4.runtime.atn.RangeTransition;
import oculus.org.antlr.v4.runtime.atn.RuleStartState;
import oculus.org.antlr.v4.runtime.atn.RuleTransition;
import oculus.org.antlr.v4.runtime.atn.SetTransition;
import oculus.org.antlr.v4.runtime.atn.Transition;
import oculus.org.antlr.v4.runtime.misc.IntegerList;
import oculus.org.antlr.v4.runtime.misc.Interval;
import oculus.org.antlr.v4.runtime.misc.IntervalSet;

public class ATNSerializer {
    public ATN atn;
    private final IntegerList data = new IntegerList();
    private final Map<IntervalSet, Boolean> sets = new LinkedHashMap<IntervalSet, Boolean>();
    private final IntegerList nonGreedyStates = new IntegerList();
    private final IntegerList precedenceStates = new IntegerList();

    public ATNSerializer(ATN atn) {
        assert (atn.grammarType != null);
        this.atn = atn;
    }

    public IntegerList serialize() {
        this.addPreamble();
        int nedges = this.addEdges();
        this.addNonGreedyStates();
        this.addPrecedenceStates();
        this.addRuleStatesAndLexerTokenTypes();
        this.addModeStartStates();
        Map<IntervalSet, Integer> setIndices = null;
        setIndices = this.addSets();
        this.addEdges(nedges, setIndices);
        this.addDecisionStartStates();
        this.addLexerActions();
        return this.data;
    }

    private void addPreamble() {
        this.data.add(ATNDeserializer.SERIALIZED_VERSION);
        this.data.add(this.atn.grammarType.ordinal());
        this.data.add(this.atn.maxTokenType);
    }

    private void addLexerActions() {
        if (this.atn.grammarType == ATNType.LEXER) {
            this.data.add(this.atn.lexerActions.length);
            block10: for (LexerAction action : this.atn.lexerActions) {
                this.data.add(action.getActionType().ordinal());
                switch (action.getActionType()) {
                    case CHANNEL: {
                        int channel = ((LexerChannelAction)action).getChannel();
                        this.data.add(channel);
                        this.data.add(0);
                        continue block10;
                    }
                    case CUSTOM: {
                        int ruleIndex = ((LexerCustomAction)action).getRuleIndex();
                        int actionIndex = ((LexerCustomAction)action).getActionIndex();
                        this.data.add(ruleIndex);
                        this.data.add(actionIndex);
                        continue block10;
                    }
                    case MODE: {
                        int mode = ((LexerModeAction)action).getMode();
                        this.data.add(mode);
                        this.data.add(0);
                        continue block10;
                    }
                    case MORE: {
                        this.data.add(0);
                        this.data.add(0);
                        continue block10;
                    }
                    case POP_MODE: {
                        this.data.add(0);
                        this.data.add(0);
                        continue block10;
                    }
                    case PUSH_MODE: {
                        int mode = ((LexerPushModeAction)action).getMode();
                        this.data.add(mode);
                        this.data.add(0);
                        continue block10;
                    }
                    case SKIP: {
                        this.data.add(0);
                        this.data.add(0);
                        continue block10;
                    }
                    case TYPE: {
                        int type = ((LexerTypeAction)action).getType();
                        this.data.add(type);
                        this.data.add(0);
                        continue block10;
                    }
                    default: {
                        String message = String.format(Locale.getDefault(), "The specified lexer action type %s is not valid.", new Object[]{action.getActionType()});
                        throw new IllegalArgumentException(message);
                    }
                }
            }
        }
    }

    private void addDecisionStartStates() {
        int ndecisions = this.atn.decisionToState.size();
        this.data.add(ndecisions);
        for (DecisionState decStartState : this.atn.decisionToState) {
            this.data.add(decStartState.stateNumber);
        }
    }

    private void addEdges(int nedges, Map<IntervalSet, Integer> setIndices) {
        this.data.add(nedges);
        for (ATNState s : this.atn.states) {
            if (s == null || s.getStateType() == 7) continue;
            for (int i = 0; i < s.getNumberOfTransitions(); ++i) {
                Transition t = s.transition(i);
                if (this.atn.states.get(t.target.stateNumber) == null) {
                    throw new IllegalStateException("Cannot serialize a transition to a removed state.");
                }
                int src = s.stateNumber;
                int trg = t.target.stateNumber;
                int edgeType = Transition.serializationTypes.get(t.getClass());
                int arg1 = 0;
                int arg2 = 0;
                int arg3 = 0;
                switch (edgeType) {
                    case 3: {
                        trg = ((RuleTransition)t).followState.stateNumber;
                        arg1 = ((RuleTransition)t).target.stateNumber;
                        arg2 = ((RuleTransition)t).ruleIndex;
                        arg3 = ((RuleTransition)t).precedence;
                        break;
                    }
                    case 10: {
                        PrecedencePredicateTransition ppt = (PrecedencePredicateTransition)t;
                        arg1 = ppt.precedence;
                        break;
                    }
                    case 4: {
                        PredicateTransition pt = (PredicateTransition)t;
                        arg1 = pt.ruleIndex;
                        arg2 = pt.predIndex;
                        arg3 = pt.isCtxDependent ? 1 : 0;
                        break;
                    }
                    case 2: {
                        arg1 = ((RangeTransition)t).from;
                        arg2 = ((RangeTransition)t).to;
                        if (arg1 != -1) break;
                        arg1 = 0;
                        arg3 = 1;
                        break;
                    }
                    case 5: {
                        arg1 = ((AtomTransition)t).label;
                        if (arg1 != -1) break;
                        arg1 = 0;
                        arg3 = 1;
                        break;
                    }
                    case 6: {
                        ActionTransition at = (ActionTransition)t;
                        arg1 = at.ruleIndex;
                        arg2 = at.actionIndex;
                        arg3 = at.isCtxDependent ? 1 : 0;
                        break;
                    }
                    case 7: {
                        arg1 = setIndices.get(((SetTransition)t).set);
                        break;
                    }
                    case 8: {
                        arg1 = setIndices.get(((SetTransition)t).set);
                        break;
                    }
                }
                this.data.add(src);
                this.data.add(trg);
                this.data.add(edgeType);
                this.data.add(arg1);
                this.data.add(arg2);
                this.data.add(arg3);
            }
        }
    }

    private Map<IntervalSet, Integer> addSets() {
        ATNSerializer.serializeSets(this.data, this.sets.keySet());
        HashMap<IntervalSet, Integer> setIndices = new HashMap<IntervalSet, Integer>();
        int setIndex = 0;
        for (IntervalSet s : this.sets.keySet()) {
            setIndices.put(s, setIndex++);
        }
        return setIndices;
    }

    private void addModeStartStates() {
        int nmodes = this.atn.modeToStartState.size();
        this.data.add(nmodes);
        if (nmodes > 0) {
            for (ATNState aTNState : this.atn.modeToStartState) {
                this.data.add(aTNState.stateNumber);
            }
        }
    }

    private void addRuleStatesAndLexerTokenTypes() {
        int nrules = this.atn.ruleToStartState.length;
        this.data.add(nrules);
        for (int r = 0; r < nrules; ++r) {
            RuleStartState ruleStartState = this.atn.ruleToStartState[r];
            this.data.add(ruleStartState.stateNumber);
            if (this.atn.grammarType != ATNType.LEXER) continue;
            assert (this.atn.ruleToTokenType[r] >= 0);
            this.data.add(this.atn.ruleToTokenType[r]);
        }
    }

    private void addPrecedenceStates() {
        this.data.add(this.precedenceStates.size());
        for (int i = 0; i < this.precedenceStates.size(); ++i) {
            this.data.add(this.precedenceStates.get(i));
        }
    }

    private void addNonGreedyStates() {
        this.data.add(this.nonGreedyStates.size());
        for (int i = 0; i < this.nonGreedyStates.size(); ++i) {
            this.data.add(this.nonGreedyStates.get(i));
        }
    }

    private int addEdges() {
        int nedges = 0;
        this.data.add(this.atn.states.size());
        for (ATNState s : this.atn.states) {
            if (s == null) {
                this.data.add(0);
                continue;
            }
            int stateType = s.getStateType();
            if (s instanceof DecisionState && ((DecisionState)s).nonGreedy) {
                this.nonGreedyStates.add(s.stateNumber);
            }
            if (s instanceof RuleStartState && ((RuleStartState)s).isLeftRecursiveRule) {
                this.precedenceStates.add(s.stateNumber);
            }
            this.data.add(stateType);
            this.data.add(s.ruleIndex);
            if (s.getStateType() == 12) {
                this.data.add(((LoopEndState)s).loopBackState.stateNumber);
            } else if (s instanceof BlockStartState) {
                this.data.add(((BlockStartState)s).endState.stateNumber);
            }
            if (s.getStateType() != 7) {
                nedges += s.getNumberOfTransitions();
            }
            for (int i = 0; i < s.getNumberOfTransitions(); ++i) {
                Transition t = s.transition(i);
                int edgeType = Transition.serializationTypes.get(t.getClass());
                if (edgeType != 7 && edgeType != 8) continue;
                SetTransition st = (SetTransition)t;
                this.sets.put(st.set, true);
            }
        }
        return nedges;
    }

    private static void serializeSets(IntegerList data, Collection<IntervalSet> sets) {
        int nSets = sets.size();
        data.add(nSets);
        for (IntervalSet set : sets) {
            boolean containsEof = set.contains(-1);
            if (containsEof && set.getIntervals().get((int)0).b == -1) {
                data.add(set.getIntervals().size() - 1);
            } else {
                data.add(set.getIntervals().size());
            }
            data.add(containsEof ? 1 : 0);
            for (Interval I : set.getIntervals()) {
                if (I.a == -1) {
                    if (I.b == -1) continue;
                    data.add(0);
                } else {
                    data.add(I.a);
                }
                data.add(I.b);
            }
        }
    }

    public static IntegerList getSerialized(ATN atn) {
        return new ATNSerializer(atn).serialize();
    }
}

