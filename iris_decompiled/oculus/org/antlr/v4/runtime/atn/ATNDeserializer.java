/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import oculus.org.antlr.v4.runtime.atn.ATN;
import oculus.org.antlr.v4.runtime.atn.ATNDeserializationOptions;
import oculus.org.antlr.v4.runtime.atn.ATNState;
import oculus.org.antlr.v4.runtime.atn.ATNType;
import oculus.org.antlr.v4.runtime.atn.ActionTransition;
import oculus.org.antlr.v4.runtime.atn.AtomTransition;
import oculus.org.antlr.v4.runtime.atn.BasicBlockStartState;
import oculus.org.antlr.v4.runtime.atn.BasicState;
import oculus.org.antlr.v4.runtime.atn.BlockEndState;
import oculus.org.antlr.v4.runtime.atn.BlockStartState;
import oculus.org.antlr.v4.runtime.atn.DecisionState;
import oculus.org.antlr.v4.runtime.atn.EpsilonTransition;
import oculus.org.antlr.v4.runtime.atn.LexerAction;
import oculus.org.antlr.v4.runtime.atn.LexerActionType;
import oculus.org.antlr.v4.runtime.atn.LexerChannelAction;
import oculus.org.antlr.v4.runtime.atn.LexerCustomAction;
import oculus.org.antlr.v4.runtime.atn.LexerModeAction;
import oculus.org.antlr.v4.runtime.atn.LexerMoreAction;
import oculus.org.antlr.v4.runtime.atn.LexerPopModeAction;
import oculus.org.antlr.v4.runtime.atn.LexerPushModeAction;
import oculus.org.antlr.v4.runtime.atn.LexerSkipAction;
import oculus.org.antlr.v4.runtime.atn.LexerTypeAction;
import oculus.org.antlr.v4.runtime.atn.LoopEndState;
import oculus.org.antlr.v4.runtime.atn.NotSetTransition;
import oculus.org.antlr.v4.runtime.atn.PlusBlockStartState;
import oculus.org.antlr.v4.runtime.atn.PlusLoopbackState;
import oculus.org.antlr.v4.runtime.atn.PrecedencePredicateTransition;
import oculus.org.antlr.v4.runtime.atn.PredicateTransition;
import oculus.org.antlr.v4.runtime.atn.RangeTransition;
import oculus.org.antlr.v4.runtime.atn.RuleStartState;
import oculus.org.antlr.v4.runtime.atn.RuleStopState;
import oculus.org.antlr.v4.runtime.atn.RuleTransition;
import oculus.org.antlr.v4.runtime.atn.SetTransition;
import oculus.org.antlr.v4.runtime.atn.StarBlockStartState;
import oculus.org.antlr.v4.runtime.atn.StarLoopEntryState;
import oculus.org.antlr.v4.runtime.atn.StarLoopbackState;
import oculus.org.antlr.v4.runtime.atn.TokensStartState;
import oculus.org.antlr.v4.runtime.atn.Transition;
import oculus.org.antlr.v4.runtime.atn.WildcardTransition;
import oculus.org.antlr.v4.runtime.misc.IntegerList;
import oculus.org.antlr.v4.runtime.misc.IntervalSet;
import oculus.org.antlr.v4.runtime.misc.Pair;

public class ATNDeserializer {
    public static final int SERIALIZED_VERSION = 4;
    private final ATNDeserializationOptions deserializationOptions;

    public ATNDeserializer() {
        this(ATNDeserializationOptions.getDefaultOptions());
    }

    public ATNDeserializer(ATNDeserializationOptions deserializationOptions) {
        if (deserializationOptions == null) {
            deserializationOptions = ATNDeserializationOptions.getDefaultOptions();
        }
        this.deserializationOptions = deserializationOptions;
    }

    public ATN deserialize(char[] data) {
        return this.deserialize(ATNDeserializer.decodeIntsEncodedAs16BitWords(data));
    }

    /*
     * WARNING - void declaration
     */
    public ATN deserialize(int[] data) {
        int i;
        void var11_18;
        int version;
        int p = 0;
        if ((version = data[p++]) != SERIALIZED_VERSION) {
            String reason = String.format(Locale.getDefault(), "Could not deserialize ATN with version %d (expected %d).", version, SERIALIZED_VERSION);
            throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
        }
        ATNType grammarType = ATNType.values()[data[p++]];
        int maxTokenType = data[p++];
        ATN atn = new ATN(grammarType, maxTokenType);
        ArrayList<Pair<LoopEndState, Integer>> loopBackStateNumbers = new ArrayList<Pair<LoopEndState, Integer>>();
        ArrayList<Pair<BlockStartState, Integer>> endStateNumbers = new ArrayList<Pair<BlockStartState, Integer>>();
        int nstates = data[p++];
        for (int i2 = 0; i2 < nstates; ++i2) {
            int n;
            if ((n = data[p++]) == 0) {
                atn.addState(null);
                continue;
            }
            int ruleIndex = data[p++];
            ATNState s = this.stateFactory(n, ruleIndex);
            if (n == 12) {
                int loopBackStateNumber = data[p++];
                loopBackStateNumbers.add(new Pair<LoopEndState, Integer>((LoopEndState)s, loopBackStateNumber));
            } else if (s instanceof BlockStartState) {
                int endStateNumber = data[p++];
                endStateNumbers.add(new Pair<BlockStartState, Integer>((BlockStartState)s, endStateNumber));
            }
            atn.addState(s);
        }
        for (Pair pair : loopBackStateNumbers) {
            ((LoopEndState)pair.a).loopBackState = atn.states.get((Integer)pair.b);
        }
        for (Pair pair : endStateNumbers) {
            ((BlockStartState)pair.a).endState = (BlockEndState)atn.states.get((Integer)pair.b);
        }
        int numNonGreedyStates = data[p++];
        boolean bl = false;
        while (var11_18 < numNonGreedyStates) {
            int stateNumber = data[p++];
            ((DecisionState)atn.states.get((int)stateNumber)).nonGreedy = true;
            ++var11_18;
        }
        int n = data[p++];
        for (int i4 = 0; i4 < n; ++i4) {
            int stateNumber = data[p++];
            ((RuleStartState)atn.states.get((int)stateNumber)).isLeftRecursiveRule = true;
        }
        int nrules = data[p++];
        if (atn.grammarType == ATNType.LEXER) {
            atn.ruleToTokenType = new int[nrules];
        }
        atn.ruleToStartState = new RuleStartState[nrules];
        for (int i5 = 0; i5 < nrules; ++i5) {
            int tokenType;
            RuleStartState startState;
            int s = data[p++];
            atn.ruleToStartState[i5] = startState = (RuleStartState)atn.states.get(s);
            if (atn.grammarType != ATNType.LEXER) continue;
            atn.ruleToTokenType[i5] = tokenType = data[p++];
        }
        atn.ruleToStopState = new RuleStopState[nrules];
        for (ATNState state : atn.states) {
            RuleStopState stopState;
            if (!(state instanceof RuleStopState)) continue;
            atn.ruleToStopState[state.ruleIndex] = stopState = (RuleStopState)state;
            atn.ruleToStartState[state.ruleIndex].stopState = stopState;
        }
        int nmodes = data[p++];
        for (int i6 = 0; i6 < nmodes; ++i6) {
            int s = data[p++];
            atn.modeToStartState.add((TokensStartState)atn.states.get(s));
        }
        ArrayList<IntervalSet> sets = new ArrayList<IntervalSet>();
        p = this.deserializeSets(data, p, sets);
        int nedges = data[p++];
        for (int i7 = 0; i7 < nedges; ++i7) {
            int src = data[p];
            int trg = data[p + 1];
            int ttype = data[p + 2];
            int arg1 = data[p + 3];
            int arg2 = data[p + 4];
            int arg3 = data[p + 5];
            Transition trans = this.edgeFactory(atn, ttype, src, trg, arg1, arg2, arg3, sets);
            ATNState srcState = atn.states.get(src);
            srcState.addTransition(trans);
            p += 6;
        }
        for (ATNState state : atn.states) {
            for (int i8 = 0; i8 < state.getNumberOfTransitions(); ++i8) {
                Transition t = state.transition(i8);
                if (!(t instanceof RuleTransition)) continue;
                RuleTransition ruleTransition = (RuleTransition)t;
                int outermostPrecedenceReturn = -1;
                if (atn.ruleToStartState[ruleTransition.target.ruleIndex].isLeftRecursiveRule && ruleTransition.precedence == 0) {
                    outermostPrecedenceReturn = ruleTransition.target.ruleIndex;
                }
                EpsilonTransition returnTransition = new EpsilonTransition(ruleTransition.followState, outermostPrecedenceReturn);
                atn.ruleToStopState[ruleTransition.target.ruleIndex].addTransition(returnTransition);
            }
        }
        for (ATNState state : atn.states) {
            ATNState target;
            ATNState loopbackState;
            if (state instanceof BlockStartState) {
                if (((BlockStartState)state).endState == null) {
                    throw new IllegalStateException();
                }
                if (((BlockStartState)state).endState.startState != null) {
                    throw new IllegalStateException();
                }
                ((BlockStartState)state).endState.startState = (BlockStartState)state;
            }
            if (state instanceof PlusLoopbackState) {
                loopbackState = (PlusLoopbackState)state;
                for (int i9 = 0; i9 < loopbackState.getNumberOfTransitions(); ++i9) {
                    target = loopbackState.transition((int)i9).target;
                    if (!(target instanceof PlusBlockStartState)) continue;
                    ((PlusBlockStartState)target).loopBackState = loopbackState;
                }
                continue;
            }
            if (!(state instanceof StarLoopbackState)) continue;
            loopbackState = (StarLoopbackState)state;
            for (int i2 = 0; i2 < loopbackState.getNumberOfTransitions(); ++i2) {
                target = loopbackState.transition((int)i2).target;
                if (!(target instanceof StarLoopEntryState)) continue;
                ((StarLoopEntryState)target).loopBackState = loopbackState;
            }
        }
        int ndecisions = data[p++];
        for (i = 1; i <= ndecisions; ++i) {
            int s = data[p++];
            DecisionState decState = (DecisionState)atn.states.get(s);
            atn.decisionToState.add(decState);
            decState.decision = i - 1;
        }
        if (atn.grammarType == ATNType.LEXER) {
            atn.lexerActions = new LexerAction[data[p++]];
            for (i = 0; i < atn.lexerActions.length; ++i) {
                LexerAction lexerAction;
                LexerActionType actionType = LexerActionType.values()[data[p++]];
                int data1 = data[p++];
                int data2 = data[p++];
                atn.lexerActions[i] = lexerAction = this.lexerActionFactory(actionType, data1, data2);
            }
        }
        this.markPrecedenceDecisions(atn);
        if (this.deserializationOptions.isVerifyATN()) {
            this.verifyATN(atn);
        }
        if (this.deserializationOptions.isGenerateRuleBypassTransitions() && atn.grammarType == ATNType.PARSER) {
            atn.ruleToTokenType = new int[atn.ruleToStartState.length];
            for (i = 0; i < atn.ruleToStartState.length; ++i) {
                atn.ruleToTokenType[i] = atn.maxTokenType + i + 1;
            }
            for (i = 0; i < atn.ruleToStartState.length; ++i) {
                ATNState endState;
                BasicBlockStartState bypassStart = new BasicBlockStartState();
                bypassStart.ruleIndex = i;
                atn.addState(bypassStart);
                BlockEndState bypassStop = new BlockEndState();
                bypassStop.ruleIndex = i;
                atn.addState(bypassStop);
                bypassStart.endState = bypassStop;
                atn.defineDecisionState(bypassStart);
                bypassStop.startState = bypassStart;
                Transition excludeTransition = null;
                if (atn.ruleToStartState[i].isLeftRecursiveRule) {
                    endState = null;
                    for (ATNState state : atn.states) {
                        ATNState maybeLoopEndState;
                        if (state.ruleIndex != i || !(state instanceof StarLoopEntryState) || !((maybeLoopEndState = state.transition((int)(state.getNumberOfTransitions() - 1)).target) instanceof LoopEndState) || !maybeLoopEndState.epsilonOnlyTransitions || !(maybeLoopEndState.transition((int)0).target instanceof RuleStopState)) continue;
                        endState = state;
                        break;
                    }
                    if (endState == null) {
                        throw new UnsupportedOperationException("Couldn't identify final state of the precedence rule prefix section.");
                    }
                    excludeTransition = ((StarLoopEntryState)endState).loopBackState.transition(0);
                } else {
                    endState = atn.ruleToStopState[i];
                }
                for (ATNState state : atn.states) {
                    for (Transition transition : state.transitions) {
                        if (transition == excludeTransition || transition.target != endState) continue;
                        transition.target = bypassStop;
                    }
                }
                while (atn.ruleToStartState[i].getNumberOfTransitions() > 0) {
                    Transition transition = atn.ruleToStartState[i].removeTransition(atn.ruleToStartState[i].getNumberOfTransitions() - 1);
                    bypassStart.addTransition(transition);
                }
                atn.ruleToStartState[i].addTransition(new EpsilonTransition(bypassStart));
                bypassStop.addTransition(new EpsilonTransition(endState));
                BasicState matchState = new BasicState();
                atn.addState(matchState);
                matchState.addTransition(new AtomTransition(bypassStop, atn.ruleToTokenType[i]));
                bypassStart.addTransition(new EpsilonTransition(matchState));
            }
            if (this.deserializationOptions.isVerifyATN()) {
                this.verifyATN(atn);
            }
        }
        return atn;
    }

    private int deserializeSets(int[] data, int p, List<IntervalSet> sets) {
        int nsets = data[p++];
        for (int i = 0; i < nsets; ++i) {
            boolean containsEof;
            int nintervals = data[p];
            IntervalSet set = new IntervalSet(new int[0]);
            sets.add(set);
            int n = ++p;
            ++p;
            boolean bl = containsEof = data[n] != 0;
            if (containsEof) {
                set.add(-1);
            }
            for (int j = 0; j < nintervals; ++j) {
                int a = data[p++];
                int b = data[p++];
                set.add(a, b);
            }
        }
        return p;
    }

    protected void markPrecedenceDecisions(ATN atn) {
        for (ATNState state : atn.states) {
            ATNState maybeLoopEndState;
            if (!(state instanceof StarLoopEntryState) || !atn.ruleToStartState[state.ruleIndex].isLeftRecursiveRule || !((maybeLoopEndState = state.transition((int)(state.getNumberOfTransitions() - 1)).target) instanceof LoopEndState) || !maybeLoopEndState.epsilonOnlyTransitions || !(maybeLoopEndState.transition((int)0).target instanceof RuleStopState)) continue;
            ((StarLoopEntryState)state).isPrecedenceDecision = true;
        }
    }

    protected void verifyATN(ATN atn) {
        for (ATNState state : atn.states) {
            if (state == null) continue;
            this.checkCondition(state.onlyHasEpsilonTransitions() || state.getNumberOfTransitions() <= 1);
            if (state instanceof PlusBlockStartState) {
                this.checkCondition(((PlusBlockStartState)state).loopBackState != null);
            }
            if (state instanceof StarLoopEntryState) {
                StarLoopEntryState starLoopEntryState = (StarLoopEntryState)state;
                this.checkCondition(starLoopEntryState.loopBackState != null);
                this.checkCondition(starLoopEntryState.getNumberOfTransitions() == 2);
                if (starLoopEntryState.transition((int)0).target instanceof StarBlockStartState) {
                    this.checkCondition(starLoopEntryState.transition((int)1).target instanceof LoopEndState);
                    this.checkCondition(!starLoopEntryState.nonGreedy);
                } else if (starLoopEntryState.transition((int)0).target instanceof LoopEndState) {
                    this.checkCondition(starLoopEntryState.transition((int)1).target instanceof StarBlockStartState);
                    this.checkCondition(starLoopEntryState.nonGreedy);
                } else {
                    throw new IllegalStateException();
                }
            }
            if (state instanceof StarLoopbackState) {
                this.checkCondition(state.getNumberOfTransitions() == 1);
                this.checkCondition(state.transition((int)0).target instanceof StarLoopEntryState);
            }
            if (state instanceof LoopEndState) {
                this.checkCondition(((LoopEndState)state).loopBackState != null);
            }
            if (state instanceof RuleStartState) {
                this.checkCondition(((RuleStartState)state).stopState != null);
            }
            if (state instanceof BlockStartState) {
                this.checkCondition(((BlockStartState)state).endState != null);
            }
            if (state instanceof BlockEndState) {
                this.checkCondition(((BlockEndState)state).startState != null);
            }
            if (state instanceof DecisionState) {
                DecisionState decisionState = (DecisionState)state;
                this.checkCondition(decisionState.getNumberOfTransitions() <= 1 || decisionState.decision >= 0);
                continue;
            }
            this.checkCondition(state.getNumberOfTransitions() <= 1 || state instanceof RuleStopState);
        }
    }

    protected void checkCondition(boolean condition) {
        this.checkCondition(condition, null);
    }

    protected void checkCondition(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    protected static int toInt(char c) {
        return c;
    }

    protected static int toInt32(char[] data, int offset) {
        return data[offset] | data[offset + 1] << 16;
    }

    protected static int toInt32(int[] data, int offset) {
        return data[offset] | data[offset + 1] << 16;
    }

    protected Transition edgeFactory(ATN atn, int type, int src, int trg, int arg1, int arg2, int arg3, List<IntervalSet> sets) {
        ATNState target = atn.states.get(trg);
        switch (type) {
            case 1: {
                return new EpsilonTransition(target);
            }
            case 2: {
                if (arg3 != 0) {
                    return new RangeTransition(target, -1, arg2);
                }
                return new RangeTransition(target, arg1, arg2);
            }
            case 3: {
                RuleTransition rt = new RuleTransition((RuleStartState)atn.states.get(arg1), arg2, arg3, target);
                return rt;
            }
            case 4: {
                PredicateTransition pt = new PredicateTransition(target, arg1, arg2, arg3 != 0);
                return pt;
            }
            case 10: {
                return new PrecedencePredicateTransition(target, arg1);
            }
            case 5: {
                if (arg3 != 0) {
                    return new AtomTransition(target, -1);
                }
                return new AtomTransition(target, arg1);
            }
            case 6: {
                ActionTransition a = new ActionTransition(target, arg1, arg2, arg3 != 0);
                return a;
            }
            case 7: {
                return new SetTransition(target, sets.get(arg1));
            }
            case 8: {
                return new NotSetTransition(target, sets.get(arg1));
            }
            case 9: {
                return new WildcardTransition(target);
            }
        }
        throw new IllegalArgumentException("The specified transition type is not valid.");
    }

    protected ATNState stateFactory(int type, int ruleIndex) {
        ATNState s;
        switch (type) {
            case 0: {
                return null;
            }
            case 1: {
                s = new BasicState();
                break;
            }
            case 2: {
                s = new RuleStartState();
                break;
            }
            case 3: {
                s = new BasicBlockStartState();
                break;
            }
            case 4: {
                s = new PlusBlockStartState();
                break;
            }
            case 5: {
                s = new StarBlockStartState();
                break;
            }
            case 6: {
                s = new TokensStartState();
                break;
            }
            case 7: {
                s = new RuleStopState();
                break;
            }
            case 8: {
                s = new BlockEndState();
                break;
            }
            case 9: {
                s = new StarLoopbackState();
                break;
            }
            case 10: {
                s = new StarLoopEntryState();
                break;
            }
            case 11: {
                s = new PlusLoopbackState();
                break;
            }
            case 12: {
                s = new LoopEndState();
                break;
            }
            default: {
                String message = String.format(Locale.getDefault(), "The specified state type %d is not valid.", type);
                throw new IllegalArgumentException(message);
            }
        }
        s.ruleIndex = ruleIndex;
        return s;
    }

    protected LexerAction lexerActionFactory(LexerActionType type, int data1, int data2) {
        switch (type) {
            case CHANNEL: {
                return new LexerChannelAction(data1);
            }
            case CUSTOM: {
                return new LexerCustomAction(data1, data2);
            }
            case MODE: {
                return new LexerModeAction(data1);
            }
            case MORE: {
                return LexerMoreAction.INSTANCE;
            }
            case POP_MODE: {
                return LexerPopModeAction.INSTANCE;
            }
            case PUSH_MODE: {
                return new LexerPushModeAction(data1);
            }
            case SKIP: {
                return LexerSkipAction.INSTANCE;
            }
            case TYPE: {
                return new LexerTypeAction(data1);
            }
        }
        throw new IllegalArgumentException(String.format(Locale.getDefault(), "The specified lexer action type %s is not valid.", new Object[]{type}));
    }

    public static IntegerList encodeIntsWith16BitWords(IntegerList data) {
        IntegerList data16 = new IntegerList((int)((double)data.size() * 1.5));
        for (int i = 0; i < data.size(); ++i) {
            int v = data.get(i);
            if (v == -1) {
                data16.add(65535);
                data16.add(65535);
                continue;
            }
            if (v <= Short.MAX_VALUE) {
                data16.add(v);
                continue;
            }
            if (v >= Integer.MAX_VALUE) {
                throw new UnsupportedOperationException("Serialized ATN data element[" + i + "] = " + v + " doesn't fit in 31 bits");
            }
            data16.add((v &= Integer.MAX_VALUE) >> 16 | 0x8000);
            data16.add(v & 0xFFFF);
        }
        return data16;
    }

    public static int[] decodeIntsEncodedAs16BitWords(char[] data16) {
        return ATNDeserializer.decodeIntsEncodedAs16BitWords(data16, false);
    }

    public static int[] decodeIntsEncodedAs16BitWords(char[] data16, boolean trimToSize) {
        int[] data = new int[data16.length];
        int i = 0;
        int i2 = 0;
        while (i < data16.length) {
            int v;
            if (((v = data16[i++]) & 0x8000) == 0) {
                data[i2++] = v;
                continue;
            }
            char vnext = data16[i++];
            if (v == 65535 && vnext == '\uffff') {
                data[i2++] = -1;
                continue;
            }
            data[i2++] = (v & Short.MAX_VALUE) << 16 | vnext & 0xFFFF;
        }
        if (trimToSize) {
            return Arrays.copyOf(data, i2);
        }
        return data;
    }
}

