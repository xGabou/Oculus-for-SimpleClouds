/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import oculus.org.antlr.v4.runtime.NoViableAltException;
import oculus.org.antlr.v4.runtime.Parser;
import oculus.org.antlr.v4.runtime.ParserRuleContext;
import oculus.org.antlr.v4.runtime.RuleContext;
import oculus.org.antlr.v4.runtime.TokenStream;
import oculus.org.antlr.v4.runtime.Vocabulary;
import oculus.org.antlr.v4.runtime.VocabularyImpl;
import oculus.org.antlr.v4.runtime.atn.ATN;
import oculus.org.antlr.v4.runtime.atn.ATNConfig;
import oculus.org.antlr.v4.runtime.atn.ATNConfigSet;
import oculus.org.antlr.v4.runtime.atn.ATNSimulator;
import oculus.org.antlr.v4.runtime.atn.ATNState;
import oculus.org.antlr.v4.runtime.atn.ActionTransition;
import oculus.org.antlr.v4.runtime.atn.AtomTransition;
import oculus.org.antlr.v4.runtime.atn.BlockEndState;
import oculus.org.antlr.v4.runtime.atn.BlockStartState;
import oculus.org.antlr.v4.runtime.atn.DecisionState;
import oculus.org.antlr.v4.runtime.atn.EmptyPredictionContext;
import oculus.org.antlr.v4.runtime.atn.EpsilonTransition;
import oculus.org.antlr.v4.runtime.atn.NotSetTransition;
import oculus.org.antlr.v4.runtime.atn.PrecedencePredicateTransition;
import oculus.org.antlr.v4.runtime.atn.PredicateTransition;
import oculus.org.antlr.v4.runtime.atn.PredictionContext;
import oculus.org.antlr.v4.runtime.atn.PredictionContextCache;
import oculus.org.antlr.v4.runtime.atn.PredictionMode;
import oculus.org.antlr.v4.runtime.atn.RuleStopState;
import oculus.org.antlr.v4.runtime.atn.RuleTransition;
import oculus.org.antlr.v4.runtime.atn.SemanticContext;
import oculus.org.antlr.v4.runtime.atn.SetTransition;
import oculus.org.antlr.v4.runtime.atn.SingletonPredictionContext;
import oculus.org.antlr.v4.runtime.atn.StarLoopEntryState;
import oculus.org.antlr.v4.runtime.atn.Transition;
import oculus.org.antlr.v4.runtime.dfa.DFA;
import oculus.org.antlr.v4.runtime.dfa.DFAState;
import oculus.org.antlr.v4.runtime.misc.DoubleKeyMap;
import oculus.org.antlr.v4.runtime.misc.IntervalSet;
import oculus.org.antlr.v4.runtime.misc.Pair;

public class ParserATNSimulator
extends ATNSimulator {
    public static final boolean debug = false;
    public static final boolean debug_list_atn_decisions = false;
    public static final boolean dfa_debug = false;
    public static final boolean retry_debug = false;
    public static final boolean TURN_OFF_LR_LOOP_ENTRY_BRANCH_OPT = Boolean.parseBoolean(ParserATNSimulator.getSafeEnv("TURN_OFF_LR_LOOP_ENTRY_BRANCH_OPT"));
    protected final Parser parser;
    public final DFA[] decisionToDFA;
    private PredictionMode mode = PredictionMode.LL;
    protected DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext> mergeCache;
    protected TokenStream _input;
    protected int _startIndex;
    protected ParserRuleContext _outerContext;
    protected DFA _dfa;

    public ParserATNSimulator(ATN atn, DFA[] decisionToDFA, PredictionContextCache sharedContextCache) {
        this(null, atn, decisionToDFA, sharedContextCache);
    }

    public ParserATNSimulator(Parser parser, ATN atn, DFA[] decisionToDFA, PredictionContextCache sharedContextCache) {
        super(atn, sharedContextCache);
        this.parser = parser;
        this.decisionToDFA = decisionToDFA;
    }

    @Override
    public void reset() {
    }

    @Override
    public void clearDFA() {
        for (int d = 0; d < this.decisionToDFA.length; ++d) {
            this.decisionToDFA[d] = new DFA(this.atn.getDecisionState(d), d);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int adaptivePredict(TokenStream input, int decision, ParserRuleContext outerContext) {
        DFA dfa;
        this._input = input;
        this._startIndex = input.index();
        this._outerContext = outerContext;
        this._dfa = dfa = this.decisionToDFA[decision];
        int m = input.mark();
        int index = this._startIndex;
        try {
            int alt;
            DFAState s0 = dfa.isPrecedenceDfa() ? dfa.getPrecedenceStartState(this.parser.getPrecedence()) : dfa.s0;
            if (s0 == null) {
                if (outerContext == null) {
                    outerContext = ParserRuleContext.EMPTY;
                }
                boolean fullCtx = false;
                ATNConfigSet s0_closure = this.computeStartState(dfa.atnStartState, ParserRuleContext.EMPTY, fullCtx);
                if (dfa.isPrecedenceDfa()) {
                    dfa.s0.configs = s0_closure;
                    s0_closure = this.applyPrecedenceFilter(s0_closure);
                    s0 = this.addDFAState(dfa, new DFAState(s0_closure));
                    dfa.setPrecedenceStartState(this.parser.getPrecedence(), s0);
                } else {
                    dfa.s0 = s0 = this.addDFAState(dfa, new DFAState(s0_closure));
                }
            }
            int n = alt = this.execATN(dfa, s0, input, index, outerContext);
            return n;
        }
        finally {
            this.mergeCache = null;
            this._dfa = null;
            input.seek(index);
            input.release(m);
        }
    }

    protected int execATN(DFA dfa, DFAState s0, TokenStream input, int startIndex, ParserRuleContext outerContext) {
        DFAState previousD = s0;
        int t = input.LA(1);
        while (true) {
            DFAState D;
            if ((D = this.getExistingTargetState(previousD, t)) == null) {
                D = this.computeTargetState(dfa, previousD, t);
            }
            if (D == ERROR) {
                NoViableAltException e = this.noViableAlt(input, outerContext, previousD.configs, startIndex);
                input.seek(startIndex);
                int alt = this.getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(previousD.configs, outerContext);
                if (alt != 0) {
                    return alt;
                }
                throw e;
            }
            if (D.requiresFullContext && this.mode != PredictionMode.SLL) {
                BitSet conflictingAlts = D.configs.conflictingAlts;
                if (D.predicates != null) {
                    int conflictIndex = input.index();
                    if (conflictIndex != startIndex) {
                        input.seek(startIndex);
                    }
                    if ((conflictingAlts = this.evalSemanticContext(D.predicates, outerContext, true)).cardinality() == 1) {
                        return conflictingAlts.nextSetBit(0);
                    }
                    if (conflictIndex != startIndex) {
                        input.seek(conflictIndex);
                    }
                }
                boolean fullCtx = true;
                ATNConfigSet s0_closure = this.computeStartState(dfa.atnStartState, outerContext, fullCtx);
                this.reportAttemptingFullContext(dfa, conflictingAlts, D.configs, startIndex, input.index());
                int alt = this.execATNWithFullContext(dfa, D, s0_closure, input, startIndex, outerContext);
                return alt;
            }
            if (D.isAcceptState) {
                if (D.predicates == null) {
                    return D.prediction;
                }
                int stopIndex = input.index();
                input.seek(startIndex);
                BitSet alts = this.evalSemanticContext(D.predicates, outerContext, true);
                switch (alts.cardinality()) {
                    case 0: {
                        throw this.noViableAlt(input, outerContext, D.configs, startIndex);
                    }
                    case 1: {
                        return alts.nextSetBit(0);
                    }
                }
                this.reportAmbiguity(dfa, D, startIndex, stopIndex, false, alts, D.configs);
                return alts.nextSetBit(0);
            }
            previousD = D;
            if (t == -1) continue;
            input.consume();
            t = input.LA(1);
        }
    }

    protected DFAState getExistingTargetState(DFAState previousD, int t) {
        DFAState[] edges = previousD.edges;
        if (edges == null || t + 1 < 0 || t + 1 >= edges.length) {
            return null;
        }
        return edges[t + 1];
    }

    protected DFAState computeTargetState(DFA dfa, DFAState previousD, int t) {
        ATNConfigSet reach = this.computeReachSet(previousD.configs, t, false);
        if (reach == null) {
            this.addDFAEdge(dfa, previousD, t, ERROR);
            return ERROR;
        }
        DFAState D = new DFAState(reach);
        int predictedAlt = ParserATNSimulator.getUniqueAlt(reach);
        if (predictedAlt != 0) {
            D.isAcceptState = true;
            D.configs.uniqueAlt = predictedAlt;
            D.prediction = predictedAlt;
        } else if (PredictionMode.hasSLLConflictTerminatingPrediction(this.mode, reach)) {
            D.configs.conflictingAlts = this.getConflictingAlts(reach);
            D.requiresFullContext = true;
            D.isAcceptState = true;
            D.prediction = D.configs.conflictingAlts.nextSetBit(0);
        }
        if (D.isAcceptState && D.configs.hasSemanticContext) {
            this.predicateDFAState(D, this.atn.getDecisionState(dfa.decision));
            if (D.predicates != null) {
                D.prediction = 0;
            }
        }
        D = this.addDFAEdge(dfa, previousD, t, D);
        return D;
    }

    protected void predicateDFAState(DFAState dfaState, DecisionState decisionState) {
        int nalts = decisionState.getNumberOfTransitions();
        BitSet altsToCollectPredsFrom = this.getConflictingAltsOrUniqueAlt(dfaState.configs);
        SemanticContext[] altToPred = this.getPredsForAmbigAlts(altsToCollectPredsFrom, dfaState.configs, nalts);
        if (altToPred != null) {
            dfaState.predicates = this.getPredicatePredictions(altsToCollectPredsFrom, altToPred);
            dfaState.prediction = 0;
        } else {
            dfaState.prediction = altsToCollectPredsFrom.nextSetBit(0);
        }
    }

    protected int execATNWithFullContext(DFA dfa, DFAState D, ATNConfigSet s0, TokenStream input, int startIndex, ParserRuleContext outerContext) {
        int predictedAlt;
        boolean fullCtx = true;
        boolean foundExactAmbig = false;
        ATNConfigSet reach = null;
        ATNConfigSet previous = s0;
        input.seek(startIndex);
        int t = input.LA(1);
        while (true) {
            if ((reach = this.computeReachSet(previous, t, fullCtx)) == null) {
                NoViableAltException e = this.noViableAlt(input, outerContext, previous, startIndex);
                input.seek(startIndex);
                int alt = this.getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(previous, outerContext);
                if (alt != 0) {
                    return alt;
                }
                throw e;
            }
            Collection<BitSet> altSubSets = PredictionMode.getConflictingAltSubsets(reach);
            reach.uniqueAlt = ParserATNSimulator.getUniqueAlt(reach);
            if (reach.uniqueAlt != 0) {
                predictedAlt = reach.uniqueAlt;
                break;
            }
            if (this.mode != PredictionMode.LL_EXACT_AMBIG_DETECTION) {
                predictedAlt = PredictionMode.resolvesToJustOneViableAlt(altSubSets);
                if (predictedAlt != 0) {
                    break;
                }
            } else if (PredictionMode.allSubsetsConflict(altSubSets) && PredictionMode.allSubsetsEqual(altSubSets)) {
                foundExactAmbig = true;
                predictedAlt = PredictionMode.getSingleViableAlt(altSubSets);
                break;
            }
            previous = reach;
            if (t == -1) continue;
            input.consume();
            t = input.LA(1);
        }
        if (reach.uniqueAlt != 0) {
            this.reportContextSensitivity(dfa, predictedAlt, reach, startIndex, input.index());
            return predictedAlt;
        }
        this.reportAmbiguity(dfa, D, startIndex, input.index(), foundExactAmbig, reach.getAlts(), reach);
        return predictedAlt;
    }

    protected ATNConfigSet computeReachSet(ATNConfigSet closure, int t, boolean fullCtx) {
        if (this.mergeCache == null) {
            this.mergeCache = new DoubleKeyMap();
        }
        ATNConfigSet intermediate = new ATNConfigSet(fullCtx);
        ArrayList<ATNConfig> skippedStopStates = null;
        for (ATNConfig c : closure) {
            if (c.state instanceof RuleStopState) {
                assert (c.context.isEmpty());
                if (!fullCtx && t != -1) continue;
                if (skippedStopStates == null) {
                    skippedStopStates = new ArrayList<ATNConfig>();
                }
                skippedStopStates.add(c);
                continue;
            }
            int n = c.state.getNumberOfTransitions();
            for (int ti = 0; ti < n; ++ti) {
                Transition trans = c.state.transition(ti);
                ATNState target = this.getReachableTarget(trans, t);
                if (target == null) continue;
                intermediate.add(new ATNConfig(c, target), this.mergeCache);
            }
        }
        ATNConfigSet reach = null;
        if (skippedStopStates == null && t != -1) {
            if (intermediate.size() == 1) {
                reach = intermediate;
            } else if (ParserATNSimulator.getUniqueAlt(intermediate) != 0) {
                reach = intermediate;
            }
        }
        if (reach == null) {
            reach = new ATNConfigSet(fullCtx);
            HashSet<ATNConfig> closureBusy = new HashSet<ATNConfig>();
            boolean treatEofAsEpsilon = t == -1;
            for (ATNConfig c : intermediate) {
                this.closure(c, reach, closureBusy, false, fullCtx, treatEofAsEpsilon);
            }
        }
        if (t == -1) {
            reach = this.removeAllConfigsNotInRuleStopState(reach, reach == intermediate);
        }
        if (!(skippedStopStates == null || fullCtx && PredictionMode.hasConfigInRuleStopState(reach))) {
            assert (!skippedStopStates.isEmpty());
            for (ATNConfig c : skippedStopStates) {
                reach.add(c, this.mergeCache);
            }
        }
        if (reach.isEmpty()) {
            return null;
        }
        return reach;
    }

    protected ATNConfigSet removeAllConfigsNotInRuleStopState(ATNConfigSet configs, boolean lookToEndOfRule) {
        if (PredictionMode.allConfigsInRuleStopStates(configs)) {
            return configs;
        }
        ATNConfigSet result = new ATNConfigSet(configs.fullCtx);
        for (ATNConfig config : configs) {
            IntervalSet nextTokens;
            if (config.state instanceof RuleStopState) {
                result.add(config, this.mergeCache);
                continue;
            }
            if (!lookToEndOfRule || !config.state.onlyHasEpsilonTransitions() || !(nextTokens = this.atn.nextTokens(config.state)).contains(-2)) continue;
            RuleStopState endOfRuleState = this.atn.ruleToStopState[config.state.ruleIndex];
            result.add(new ATNConfig(config, endOfRuleState), this.mergeCache);
        }
        return result;
    }

    protected ATNConfigSet computeStartState(ATNState p, RuleContext ctx, boolean fullCtx) {
        PredictionContext initialContext = PredictionContext.fromRuleContext(this.atn, ctx);
        ATNConfigSet configs = new ATNConfigSet(fullCtx);
        for (int i = 0; i < p.getNumberOfTransitions(); ++i) {
            ATNState target = p.transition((int)i).target;
            ATNConfig c = new ATNConfig(target, i + 1, initialContext);
            HashSet<ATNConfig> closureBusy = new HashSet<ATNConfig>();
            this.closure(c, configs, closureBusy, true, fullCtx, false);
        }
        return configs;
    }

    protected ATNConfigSet applyPrecedenceFilter(ATNConfigSet configs) {
        HashMap<Integer, PredictionContext> statesFromAlt1 = new HashMap<Integer, PredictionContext>();
        ATNConfigSet configSet = new ATNConfigSet(configs.fullCtx);
        for (ATNConfig config : configs) {
            SemanticContext updatedContext;
            if (config.alt != 1 || (updatedContext = config.semanticContext.evalPrecedence(this.parser, this._outerContext)) == null) continue;
            statesFromAlt1.put(config.state.stateNumber, config.context);
            if (updatedContext != config.semanticContext) {
                configSet.add(new ATNConfig(config, updatedContext), this.mergeCache);
                continue;
            }
            configSet.add(config, this.mergeCache);
        }
        for (ATNConfig config : configs) {
            PredictionContext context;
            if (config.alt == 1 || !config.isPrecedenceFilterSuppressed() && (context = (PredictionContext)statesFromAlt1.get(config.state.stateNumber)) != null && context.equals(config.context)) continue;
            configSet.add(config, this.mergeCache);
        }
        return configSet;
    }

    protected ATNState getReachableTarget(Transition trans, int ttype) {
        if (trans.matches(ttype, 0, this.atn.maxTokenType)) {
            return trans.target;
        }
        return null;
    }

    protected SemanticContext[] getPredsForAmbigAlts(BitSet ambigAlts, ATNConfigSet configs, int nalts) {
        SemanticContext[] altToPred = new SemanticContext[nalts + 1];
        for (ATNConfig c : configs) {
            if (!ambigAlts.get(c.alt)) continue;
            altToPred[c.alt] = SemanticContext.or(altToPred[c.alt], c.semanticContext);
        }
        int nPredAlts = 0;
        for (int i = 1; i <= nalts; ++i) {
            if (altToPred[i] == null) {
                altToPred[i] = SemanticContext.Empty.Instance;
                continue;
            }
            if (altToPred[i] == SemanticContext.Empty.Instance) continue;
            ++nPredAlts;
        }
        if (nPredAlts == 0) {
            altToPred = null;
        }
        return altToPred;
    }

    protected DFAState.PredPrediction[] getPredicatePredictions(BitSet ambigAlts, SemanticContext[] altToPred) {
        ArrayList<DFAState.PredPrediction> pairs = new ArrayList<DFAState.PredPrediction>();
        boolean containsPredicate = false;
        for (int i = 1; i < altToPred.length; ++i) {
            SemanticContext pred = altToPred[i];
            assert (pred != null);
            if (ambigAlts != null && ambigAlts.get(i)) {
                pairs.add(new DFAState.PredPrediction(pred, i));
            }
            if (pred == SemanticContext.Empty.Instance) continue;
            containsPredicate = true;
        }
        if (!containsPredicate) {
            return null;
        }
        return pairs.toArray(new DFAState.PredPrediction[0]);
    }

    protected int getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(ATNConfigSet configs, ParserRuleContext outerContext) {
        Pair<ATNConfigSet, ATNConfigSet> sets = this.splitAccordingToSemanticValidity(configs, outerContext);
        ATNConfigSet semValidConfigs = (ATNConfigSet)sets.a;
        ATNConfigSet semInvalidConfigs = (ATNConfigSet)sets.b;
        int alt = this.getAltThatFinishedDecisionEntryRule(semValidConfigs);
        if (alt != 0) {
            return alt;
        }
        if (semInvalidConfigs.size() > 0 && (alt = this.getAltThatFinishedDecisionEntryRule(semInvalidConfigs)) != 0) {
            return alt;
        }
        return 0;
    }

    protected int getAltThatFinishedDecisionEntryRule(ATNConfigSet configs) {
        IntervalSet alts = new IntervalSet(new int[0]);
        for (ATNConfig c : configs) {
            if (c.getOuterContextDepth() <= 0 && (!(c.state instanceof RuleStopState) || !c.context.hasEmptyPath())) continue;
            alts.add(c.alt);
        }
        if (alts.size() == 0) {
            return 0;
        }
        return alts.getMinElement();
    }

    protected Pair<ATNConfigSet, ATNConfigSet> splitAccordingToSemanticValidity(ATNConfigSet configs, ParserRuleContext outerContext) {
        ATNConfigSet succeeded = new ATNConfigSet(configs.fullCtx);
        ATNConfigSet failed = new ATNConfigSet(configs.fullCtx);
        for (ATNConfig c : configs) {
            if (c.semanticContext != SemanticContext.Empty.Instance) {
                boolean predicateEvaluationResult = this.evalSemanticContext(c.semanticContext, outerContext, c.alt, configs.fullCtx);
                if (predicateEvaluationResult) {
                    succeeded.add(c);
                    continue;
                }
                failed.add(c);
                continue;
            }
            succeeded.add(c);
        }
        return new Pair<ATNConfigSet, ATNConfigSet>(succeeded, failed);
    }

    protected BitSet evalSemanticContext(DFAState.PredPrediction[] predPredictions, ParserRuleContext outerContext, boolean complete) {
        BitSet predictions = new BitSet();
        for (DFAState.PredPrediction pair : predPredictions) {
            if (pair.pred == SemanticContext.Empty.Instance) {
                predictions.set(pair.alt);
                if (complete) continue;
                break;
            }
            boolean fullCtx = false;
            boolean predicateEvaluationResult = this.evalSemanticContext(pair.pred, outerContext, pair.alt, fullCtx);
            if (!predicateEvaluationResult) continue;
            predictions.set(pair.alt);
            if (!complete) break;
        }
        return predictions;
    }

    protected boolean evalSemanticContext(SemanticContext pred, ParserRuleContext parserCallStack, int alt, boolean fullCtx) {
        return pred.eval(this.parser, parserCallStack);
    }

    protected void closure(ATNConfig config, ATNConfigSet configs, Set<ATNConfig> closureBusy, boolean collectPredicates, boolean fullCtx, boolean treatEofAsEpsilon) {
        boolean initialDepth = false;
        this.closureCheckingStopState(config, configs, closureBusy, collectPredicates, fullCtx, 0, treatEofAsEpsilon);
        assert (!fullCtx || !configs.dipsIntoOuterContext);
    }

    protected void closureCheckingStopState(ATNConfig config, ATNConfigSet configs, Set<ATNConfig> closureBusy, boolean collectPredicates, boolean fullCtx, int depth, boolean treatEofAsEpsilon) {
        if (config.state instanceof RuleStopState) {
            if (!config.context.isEmpty()) {
                for (int i = 0; i < config.context.size(); ++i) {
                    if (config.context.getReturnState(i) == Integer.MAX_VALUE) {
                        if (fullCtx) {
                            configs.add(new ATNConfig(config, config.state, (PredictionContext)EmptyPredictionContext.Instance), this.mergeCache);
                            continue;
                        }
                        this.closure_(config, configs, closureBusy, collectPredicates, fullCtx, depth, treatEofAsEpsilon);
                        continue;
                    }
                    ATNState returnState = this.atn.states.get(config.context.getReturnState(i));
                    PredictionContext newContext = config.context.getParent(i);
                    ATNConfig c = new ATNConfig(returnState, config.alt, newContext, config.semanticContext);
                    c.reachesIntoOuterContext = config.reachesIntoOuterContext;
                    assert (depth > Integer.MIN_VALUE);
                    this.closureCheckingStopState(c, configs, closureBusy, collectPredicates, fullCtx, depth - 1, treatEofAsEpsilon);
                }
                return;
            }
            if (fullCtx) {
                configs.add(config, this.mergeCache);
                return;
            }
        }
        this.closure_(config, configs, closureBusy, collectPredicates, fullCtx, depth, treatEofAsEpsilon);
    }

    protected void closure_(ATNConfig config, ATNConfigSet configs, Set<ATNConfig> closureBusy, boolean collectPredicates, boolean fullCtx, int depth, boolean treatEofAsEpsilon) {
        ATNState p = config.state;
        if (!p.onlyHasEpsilonTransitions()) {
            configs.add(config, this.mergeCache);
        }
        for (int i = 0; i < p.getNumberOfTransitions(); ++i) {
            boolean continueCollecting;
            Transition t;
            ATNConfig c;
            if (i == 0 && this.canDropLoopEntryEdgeInLeftRecursiveRule(config) || (c = this.getEpsilonTarget(config, t, continueCollecting = !((t = p.transition(i)) instanceof ActionTransition) && collectPredicates, depth == 0, fullCtx, treatEofAsEpsilon)) == null) continue;
            int newDepth = depth;
            if (config.state instanceof RuleStopState) {
                int outermostPrecedenceReturn;
                assert (!fullCtx);
                if (this._dfa != null && this._dfa.isPrecedenceDfa() && (outermostPrecedenceReturn = ((EpsilonTransition)t).outermostPrecedenceReturn()) == this._dfa.atnStartState.ruleIndex) {
                    c.setPrecedenceFilterSuppressed(true);
                }
                ++c.reachesIntoOuterContext;
                if (!closureBusy.add(c)) continue;
                configs.dipsIntoOuterContext = true;
                assert (newDepth > Integer.MIN_VALUE);
                --newDepth;
            } else {
                if (!t.isEpsilon() && !closureBusy.add(c)) continue;
                if (t instanceof RuleTransition && newDepth >= 0) {
                    ++newDepth;
                }
            }
            this.closureCheckingStopState(c, configs, closureBusy, continueCollecting, fullCtx, newDepth, treatEofAsEpsilon);
        }
    }

    protected boolean canDropLoopEntryEdgeInLeftRecursiveRule(ATNConfig config) {
        if (TURN_OFF_LR_LOOP_ENTRY_BRANCH_OPT) {
            return false;
        }
        ATNState p = config.state;
        if (p.getStateType() != 10 || !((StarLoopEntryState)p).isPrecedenceDecision || config.context.isEmpty() || config.context.hasEmptyPath()) {
            return false;
        }
        int numCtxs = config.context.size();
        for (int i = 0; i < numCtxs; ++i) {
            ATNState returnState = this.atn.states.get(config.context.getReturnState(i));
            if (returnState.ruleIndex == p.ruleIndex) continue;
            return false;
        }
        BlockStartState decisionStartState = (BlockStartState)p.transition((int)0).target;
        int blockEndStateNum = decisionStartState.endState.stateNumber;
        BlockEndState blockEndState = (BlockEndState)this.atn.states.get(blockEndStateNum);
        for (int i = 0; i < numCtxs; ++i) {
            int returnStateNumber = config.context.getReturnState(i);
            ATNState returnState = this.atn.states.get(returnStateNumber);
            if (returnState.getNumberOfTransitions() != 1 || !returnState.transition(0).isEpsilon()) {
                return false;
            }
            ATNState returnStateTarget = returnState.transition((int)0).target;
            if (returnState.getStateType() == 8 && returnStateTarget == p || returnState == blockEndState || returnStateTarget == blockEndState || returnStateTarget.getStateType() == 8 && returnStateTarget.getNumberOfTransitions() == 1 && returnStateTarget.transition(0).isEpsilon() && returnStateTarget.transition((int)0).target == p) continue;
            return false;
        }
        return true;
    }

    public String getRuleName(int index) {
        if (this.parser != null && index >= 0) {
            return this.parser.getRuleNames()[index];
        }
        return "<rule " + index + ">";
    }

    protected ATNConfig getEpsilonTarget(ATNConfig config, Transition t, boolean collectPredicates, boolean inContext, boolean fullCtx, boolean treatEofAsEpsilon) {
        switch (t.getSerializationType()) {
            case 3: {
                return this.ruleTransition(config, (RuleTransition)t);
            }
            case 10: {
                return this.precedenceTransition(config, (PrecedencePredicateTransition)t, collectPredicates, inContext, fullCtx);
            }
            case 4: {
                return this.predTransition(config, (PredicateTransition)t, collectPredicates, inContext, fullCtx);
            }
            case 6: {
                return this.actionTransition(config, (ActionTransition)t);
            }
            case 1: {
                return new ATNConfig(config, t.target);
            }
            case 2: 
            case 5: 
            case 7: {
                if (treatEofAsEpsilon && t.matches(-1, 0, 1)) {
                    return new ATNConfig(config, t.target);
                }
                return null;
            }
        }
        return null;
    }

    protected ATNConfig actionTransition(ATNConfig config, ActionTransition t) {
        return new ATNConfig(config, t.target);
    }

    public ATNConfig precedenceTransition(ATNConfig config, PrecedencePredicateTransition pt, boolean collectPredicates, boolean inContext, boolean fullCtx) {
        ATNConfig c = null;
        if (collectPredicates && inContext) {
            if (fullCtx) {
                int currentPosition = this._input.index();
                this._input.seek(this._startIndex);
                boolean predSucceeds = this.evalSemanticContext(pt.getPredicate(), this._outerContext, config.alt, fullCtx);
                this._input.seek(currentPosition);
                if (predSucceeds) {
                    c = new ATNConfig(config, pt.target);
                }
            } else {
                SemanticContext newSemCtx = SemanticContext.and(config.semanticContext, pt.getPredicate());
                c = new ATNConfig(config, pt.target, newSemCtx);
            }
        } else {
            c = new ATNConfig(config, pt.target);
        }
        return c;
    }

    protected ATNConfig predTransition(ATNConfig config, PredicateTransition pt, boolean collectPredicates, boolean inContext, boolean fullCtx) {
        ATNConfig c = null;
        if (collectPredicates && (!pt.isCtxDependent || pt.isCtxDependent && inContext)) {
            if (fullCtx) {
                int currentPosition = this._input.index();
                this._input.seek(this._startIndex);
                boolean predSucceeds = this.evalSemanticContext(pt.getPredicate(), this._outerContext, config.alt, fullCtx);
                this._input.seek(currentPosition);
                if (predSucceeds) {
                    c = new ATNConfig(config, pt.target);
                }
            } else {
                SemanticContext newSemCtx = SemanticContext.and(config.semanticContext, pt.getPredicate());
                c = new ATNConfig(config, pt.target, newSemCtx);
            }
        } else {
            c = new ATNConfig(config, pt.target);
        }
        return c;
    }

    protected ATNConfig ruleTransition(ATNConfig config, RuleTransition t) {
        ATNState returnState = t.followState;
        SingletonPredictionContext newContext = SingletonPredictionContext.create(config.context, returnState.stateNumber);
        return new ATNConfig(config, t.target, (PredictionContext)newContext);
    }

    protected BitSet getConflictingAlts(ATNConfigSet configs) {
        Collection<BitSet> altsets = PredictionMode.getConflictingAltSubsets(configs);
        return PredictionMode.getAlts(altsets);
    }

    protected BitSet getConflictingAltsOrUniqueAlt(ATNConfigSet configs) {
        BitSet conflictingAlts;
        if (configs.uniqueAlt != 0) {
            conflictingAlts = new BitSet();
            conflictingAlts.set(configs.uniqueAlt);
        } else {
            conflictingAlts = configs.conflictingAlts;
        }
        return conflictingAlts;
    }

    public String getTokenName(int t) {
        if (t == -1) {
            return "EOF";
        }
        Vocabulary vocabulary = this.parser != null ? this.parser.getVocabulary() : VocabularyImpl.EMPTY_VOCABULARY;
        String displayName = vocabulary.getDisplayName(t);
        if (displayName.equals(Integer.toString(t))) {
            return displayName;
        }
        return displayName + "<" + t + ">";
    }

    public String getLookaheadName(TokenStream input) {
        return this.getTokenName(input.LA(1));
    }

    public void dumpDeadEndConfigs(NoViableAltException nvae) {
        System.err.println("dead end configs: ");
        for (ATNConfig c : nvae.getDeadEndConfigs()) {
            String trans = "no edges";
            if (c.state.getNumberOfTransitions() > 0) {
                Transition t = c.state.transition(0);
                if (t instanceof AtomTransition) {
                    AtomTransition at = (AtomTransition)t;
                    trans = "Atom " + this.getTokenName(at.label);
                } else if (t instanceof SetTransition) {
                    SetTransition st = (SetTransition)t;
                    boolean not = st instanceof NotSetTransition;
                    trans = (not ? "~" : "") + "Set " + st.set.toString();
                }
            }
            System.err.println(c.toString(this.parser, true) + ":" + trans);
        }
    }

    protected NoViableAltException noViableAlt(TokenStream input, ParserRuleContext outerContext, ATNConfigSet configs, int startIndex) {
        return new NoViableAltException(this.parser, input, input.get(startIndex), input.LT(1), configs, outerContext);
    }

    protected static int getUniqueAlt(ATNConfigSet configs) {
        int alt = 0;
        for (ATNConfig c : configs) {
            if (alt == 0) {
                alt = c.alt;
                continue;
            }
            if (c.alt == alt) continue;
            return 0;
        }
        return alt;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected DFAState addDFAEdge(DFA dfa, DFAState from, int t, DFAState to) {
        if (to == null) {
            return null;
        }
        to = this.addDFAState(dfa, to);
        if (from == null || t < -1 || t > this.atn.maxTokenType) {
            return to;
        }
        DFAState dFAState = from;
        synchronized (dFAState) {
            if (from.edges == null) {
                from.edges = new DFAState[this.atn.maxTokenType + 1 + 1];
            }
            from.edges[t + 1] = to;
        }
        return to;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected DFAState addDFAState(DFA dfa, DFAState D) {
        if (D == ERROR) {
            return D;
        }
        Map<DFAState, DFAState> map = dfa.states;
        synchronized (map) {
            DFAState existing = dfa.states.get(D);
            if (existing != null) {
                return existing;
            }
            D.stateNumber = dfa.states.size();
            if (!D.configs.isReadonly()) {
                D.configs.optimizeConfigs(this);
                D.configs.setReadonly(true);
            }
            dfa.states.put(D, D);
            return D;
        }
    }

    protected void reportAttemptingFullContext(DFA dfa, BitSet conflictingAlts, ATNConfigSet configs, int startIndex, int stopIndex) {
        if (this.parser != null) {
            this.parser.getErrorListenerDispatch().reportAttemptingFullContext(this.parser, dfa, startIndex, stopIndex, conflictingAlts, configs);
        }
    }

    protected void reportContextSensitivity(DFA dfa, int prediction, ATNConfigSet configs, int startIndex, int stopIndex) {
        if (this.parser != null) {
            this.parser.getErrorListenerDispatch().reportContextSensitivity(this.parser, dfa, startIndex, stopIndex, prediction, configs);
        }
    }

    protected void reportAmbiguity(DFA dfa, DFAState D, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
        if (this.parser != null) {
            this.parser.getErrorListenerDispatch().reportAmbiguity(this.parser, dfa, startIndex, stopIndex, exact, ambigAlts, configs);
        }
    }

    public final void setPredictionMode(PredictionMode mode) {
        this.mode = mode;
    }

    public final PredictionMode getPredictionMode() {
        return this.mode;
    }

    public Parser getParser() {
        return this.parser;
    }

    public static String getSafeEnv(final String envName) {
        try {
            return AccessController.doPrivileged(new PrivilegedAction<String>(){

                @Override
                public String run() {
                    return System.getenv(envName);
                }
            });
        }
        catch (SecurityException securityException) {
            return null;
        }
    }
}

