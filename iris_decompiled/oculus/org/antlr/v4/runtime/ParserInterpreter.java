/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import oculus.org.antlr.v4.runtime.CharStream;
import oculus.org.antlr.v4.runtime.FailedPredicateException;
import oculus.org.antlr.v4.runtime.InputMismatchException;
import oculus.org.antlr.v4.runtime.InterpreterRuleContext;
import oculus.org.antlr.v4.runtime.Parser;
import oculus.org.antlr.v4.runtime.ParserRuleContext;
import oculus.org.antlr.v4.runtime.RecognitionException;
import oculus.org.antlr.v4.runtime.Token;
import oculus.org.antlr.v4.runtime.TokenSource;
import oculus.org.antlr.v4.runtime.TokenStream;
import oculus.org.antlr.v4.runtime.Vocabulary;
import oculus.org.antlr.v4.runtime.VocabularyImpl;
import oculus.org.antlr.v4.runtime.atn.ATN;
import oculus.org.antlr.v4.runtime.atn.ATNState;
import oculus.org.antlr.v4.runtime.atn.ActionTransition;
import oculus.org.antlr.v4.runtime.atn.AtomTransition;
import oculus.org.antlr.v4.runtime.atn.DecisionState;
import oculus.org.antlr.v4.runtime.atn.LoopEndState;
import oculus.org.antlr.v4.runtime.atn.ParserATNSimulator;
import oculus.org.antlr.v4.runtime.atn.PrecedencePredicateTransition;
import oculus.org.antlr.v4.runtime.atn.PredicateTransition;
import oculus.org.antlr.v4.runtime.atn.PredictionContextCache;
import oculus.org.antlr.v4.runtime.atn.RuleStartState;
import oculus.org.antlr.v4.runtime.atn.RuleTransition;
import oculus.org.antlr.v4.runtime.atn.StarLoopEntryState;
import oculus.org.antlr.v4.runtime.atn.Transition;
import oculus.org.antlr.v4.runtime.dfa.DFA;
import oculus.org.antlr.v4.runtime.misc.Pair;

public class ParserInterpreter
extends Parser {
    protected final String grammarFileName;
    protected final ATN atn;
    protected final DFA[] decisionToDFA;
    protected final PredictionContextCache sharedContextCache = new PredictionContextCache();
    @Deprecated
    protected final String[] tokenNames;
    protected final String[] ruleNames;
    private final Vocabulary vocabulary;
    protected final Deque<Pair<ParserRuleContext, Integer>> _parentContextStack = new ArrayDeque<Pair<ParserRuleContext, Integer>>();
    protected int overrideDecision = -1;
    protected int overrideDecisionInputIndex = -1;
    protected int overrideDecisionAlt = -1;
    protected boolean overrideDecisionReached = false;
    protected InterpreterRuleContext overrideDecisionRoot = null;
    protected InterpreterRuleContext rootContext;

    @Deprecated
    public ParserInterpreter(String grammarFileName, Collection<String> tokenNames, Collection<String> ruleNames, ATN atn, TokenStream input) {
        this(grammarFileName, VocabularyImpl.fromTokenNames(tokenNames.toArray(new String[0])), ruleNames, atn, input);
    }

    public ParserInterpreter(String grammarFileName, Vocabulary vocabulary, Collection<String> ruleNames, ATN atn, TokenStream input) {
        super(input);
        this.grammarFileName = grammarFileName;
        this.atn = atn;
        this.tokenNames = new String[atn.maxTokenType];
        for (int i = 0; i < this.tokenNames.length; ++i) {
            this.tokenNames[i] = vocabulary.getDisplayName(i);
        }
        this.ruleNames = ruleNames.toArray(new String[0]);
        this.vocabulary = vocabulary;
        int numberOfDecisions = atn.getNumberOfDecisions();
        this.decisionToDFA = new DFA[numberOfDecisions];
        for (int i = 0; i < numberOfDecisions; ++i) {
            DecisionState decisionState = atn.getDecisionState(i);
            this.decisionToDFA[i] = new DFA(decisionState, i);
        }
        this.setInterpreter(new ParserATNSimulator(this, atn, this.decisionToDFA, this.sharedContextCache));
    }

    @Override
    public void reset() {
        super.reset();
        this.overrideDecisionReached = false;
        this.overrideDecisionRoot = null;
    }

    @Override
    public ATN getATN() {
        return this.atn;
    }

    @Override
    @Deprecated
    public String[] getTokenNames() {
        return this.tokenNames;
    }

    @Override
    public Vocabulary getVocabulary() {
        return this.vocabulary;
    }

    @Override
    public String[] getRuleNames() {
        return this.ruleNames;
    }

    @Override
    public String getGrammarFileName() {
        return this.grammarFileName;
    }

    public ParserRuleContext parse(int startRuleIndex) {
        RuleStartState startRuleStartState = this.atn.ruleToStartState[startRuleIndex];
        this.rootContext = this.createInterpreterRuleContext(null, -1, startRuleIndex);
        if (startRuleStartState.isLeftRecursiveRule) {
            this.enterRecursionRule(this.rootContext, startRuleStartState.stateNumber, startRuleIndex, 0);
        } else {
            this.enterRule(this.rootContext, startRuleStartState.stateNumber, startRuleIndex);
        }
        block5: while (true) {
            ATNState p = this.getATNState();
            switch (p.getStateType()) {
                case 7: {
                    if (this._ctx.isEmpty()) {
                        if (startRuleStartState.isLeftRecursiveRule) {
                            ParserRuleContext result = this._ctx;
                            Pair<ParserRuleContext, Integer> parentContext = this._parentContextStack.pop();
                            this.unrollRecursionContexts((ParserRuleContext)parentContext.a);
                            return result;
                        }
                        this.exitRule();
                        return this.rootContext;
                    }
                    this.visitRuleStopState(p);
                    continue block5;
                }
            }
            try {
                this.visitState(p);
                continue;
            }
            catch (RecognitionException e) {
                this.setState(this.atn.ruleToStopState[p.ruleIndex].stateNumber);
                this.getContext().exception = e;
                this.getErrorHandler().reportError(this, e);
                this.recover(e);
                continue;
            }
            break;
        }
    }

    @Override
    public void enterRecursionRule(ParserRuleContext localctx, int state, int ruleIndex, int precedence) {
        Pair<ParserRuleContext, Integer> pair = new Pair<ParserRuleContext, Integer>(this._ctx, localctx.invokingState);
        this._parentContextStack.push(pair);
        super.enterRecursionRule(localctx, state, ruleIndex, precedence);
    }

    protected ATNState getATNState() {
        return this.atn.states.get(this.getState());
    }

    protected void visitState(ATNState p) {
        int predictedAlt = 1;
        if (p instanceof DecisionState) {
            predictedAlt = this.visitDecisionState((DecisionState)p);
        }
        Transition transition = p.transition(predictedAlt - 1);
        switch (transition.getSerializationType()) {
            case 1: {
                if (p.getStateType() != 10 || !((StarLoopEntryState)p).isPrecedenceDecision || transition.target instanceof LoopEndState) break;
                InterpreterRuleContext localctx = this.createInterpreterRuleContext((ParserRuleContext)this._parentContextStack.peek().a, (Integer)this._parentContextStack.peek().b, this._ctx.getRuleIndex());
                this.pushNewRecursionContext(localctx, this.atn.ruleToStartState[p.ruleIndex].stateNumber, this._ctx.getRuleIndex());
                break;
            }
            case 5: {
                this.match(((AtomTransition)transition).label);
                break;
            }
            case 2: 
            case 7: 
            case 8: {
                if (!transition.matches(this._input.LA(1), 1, 65535)) {
                    this.recoverInline();
                }
                this.matchWildcard();
                break;
            }
            case 9: {
                this.matchWildcard();
                break;
            }
            case 3: {
                RuleStartState ruleStartState = (RuleStartState)transition.target;
                int ruleIndex = ruleStartState.ruleIndex;
                InterpreterRuleContext newctx = this.createInterpreterRuleContext(this._ctx, p.stateNumber, ruleIndex);
                if (ruleStartState.isLeftRecursiveRule) {
                    this.enterRecursionRule(newctx, ruleStartState.stateNumber, ruleIndex, ((RuleTransition)transition).precedence);
                    break;
                }
                this.enterRule(newctx, transition.target.stateNumber, ruleIndex);
                break;
            }
            case 4: {
                PredicateTransition predicateTransition = (PredicateTransition)transition;
                if (this.sempred(this._ctx, predicateTransition.ruleIndex, predicateTransition.predIndex)) break;
                throw new FailedPredicateException(this);
            }
            case 6: {
                ActionTransition actionTransition = (ActionTransition)transition;
                this.action(this._ctx, actionTransition.ruleIndex, actionTransition.actionIndex);
                break;
            }
            case 10: {
                if (this.precpred(this._ctx, ((PrecedencePredicateTransition)transition).precedence)) break;
                throw new FailedPredicateException(this, String.format("precpred(_ctx, %d)", ((PrecedencePredicateTransition)transition).precedence));
            }
            default: {
                throw new UnsupportedOperationException("Unrecognized ATN transition type.");
            }
        }
        this.setState(transition.target.stateNumber);
    }

    protected int visitDecisionState(DecisionState p) {
        int predictedAlt = 1;
        if (p.getNumberOfTransitions() > 1) {
            this.getErrorHandler().sync(this);
            int decision = p.decision;
            if (decision == this.overrideDecision && this._input.index() == this.overrideDecisionInputIndex && !this.overrideDecisionReached) {
                predictedAlt = this.overrideDecisionAlt;
                this.overrideDecisionReached = true;
            } else {
                predictedAlt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, decision, this._ctx);
            }
        }
        return predictedAlt;
    }

    protected InterpreterRuleContext createInterpreterRuleContext(ParserRuleContext parent, int invokingStateNumber, int ruleIndex) {
        return new InterpreterRuleContext(parent, invokingStateNumber, ruleIndex);
    }

    protected void visitRuleStopState(ATNState p) {
        RuleStartState ruleStartState = this.atn.ruleToStartState[p.ruleIndex];
        if (ruleStartState.isLeftRecursiveRule) {
            Pair<ParserRuleContext, Integer> parentContext = this._parentContextStack.pop();
            this.unrollRecursionContexts((ParserRuleContext)parentContext.a);
            this.setState((Integer)parentContext.b);
        } else {
            this.exitRule();
        }
        RuleTransition ruleTransition = (RuleTransition)this.atn.states.get(this.getState()).transition(0);
        this.setState(ruleTransition.followState.stateNumber);
    }

    public void addDecisionOverride(int decision, int tokenIndex, int forcedAlt) {
        this.overrideDecision = decision;
        this.overrideDecisionInputIndex = tokenIndex;
        this.overrideDecisionAlt = forcedAlt;
    }

    public InterpreterRuleContext getOverrideDecisionRoot() {
        return this.overrideDecisionRoot;
    }

    protected void recover(RecognitionException e) {
        int i = this._input.index();
        this.getErrorHandler().recover(this, e);
        if (this._input.index() == i) {
            if (e instanceof InputMismatchException) {
                InputMismatchException ime = (InputMismatchException)e;
                Token tok = e.getOffendingToken();
                int expectedTokenType = 0;
                if (!ime.getExpectedTokens().isNil()) {
                    expectedTokenType = ime.getExpectedTokens().getMinElement();
                }
                Object errToken = this.getTokenFactory().create(new Pair<TokenSource, CharStream>(tok.getTokenSource(), tok.getTokenSource().getInputStream()), expectedTokenType, tok.getText(), 0, -1, -1, tok.getLine(), tok.getCharPositionInLine());
                this._ctx.addErrorNode(this.createErrorNode(this._ctx, (Token)errToken));
            } else {
                Token tok = e.getOffendingToken();
                Object errToken = this.getTokenFactory().create(new Pair<TokenSource, CharStream>(tok.getTokenSource(), tok.getTokenSource().getInputStream()), 0, tok.getText(), 0, -1, -1, tok.getLine(), tok.getCharPositionInLine());
                this._ctx.addErrorNode(this.createErrorNode(this._ctx, (Token)errToken));
            }
        }
    }

    protected Token recoverInline() {
        return this._errHandler.recoverInline(this);
    }

    public InterpreterRuleContext getRootContext() {
        return this.rootContext;
    }
}

