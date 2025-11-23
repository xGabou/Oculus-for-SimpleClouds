/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import oculus.org.antlr.v4.runtime.ANTLRErrorStrategy;
import oculus.org.antlr.v4.runtime.CharStream;
import oculus.org.antlr.v4.runtime.FailedPredicateException;
import oculus.org.antlr.v4.runtime.InputMismatchException;
import oculus.org.antlr.v4.runtime.NoViableAltException;
import oculus.org.antlr.v4.runtime.Parser;
import oculus.org.antlr.v4.runtime.ParserRuleContext;
import oculus.org.antlr.v4.runtime.RecognitionException;
import oculus.org.antlr.v4.runtime.RuleContext;
import oculus.org.antlr.v4.runtime.Token;
import oculus.org.antlr.v4.runtime.TokenSource;
import oculus.org.antlr.v4.runtime.TokenStream;
import oculus.org.antlr.v4.runtime.atn.ATN;
import oculus.org.antlr.v4.runtime.atn.ATNState;
import oculus.org.antlr.v4.runtime.atn.ParserATNSimulator;
import oculus.org.antlr.v4.runtime.atn.RuleTransition;
import oculus.org.antlr.v4.runtime.misc.IntervalSet;
import oculus.org.antlr.v4.runtime.misc.Pair;

public class DefaultErrorStrategy
implements ANTLRErrorStrategy {
    protected boolean errorRecoveryMode = false;
    protected int lastErrorIndex = -1;
    protected IntervalSet lastErrorStates;
    protected ParserRuleContext nextTokensContext;
    protected int nextTokensState;

    @Override
    public void reset(Parser recognizer) {
        this.endErrorCondition(recognizer);
    }

    protected void beginErrorCondition(Parser recognizer) {
        this.errorRecoveryMode = true;
    }

    @Override
    public boolean inErrorRecoveryMode(Parser recognizer) {
        return this.errorRecoveryMode;
    }

    protected void endErrorCondition(Parser recognizer) {
        this.errorRecoveryMode = false;
        this.lastErrorStates = null;
        this.lastErrorIndex = -1;
    }

    @Override
    public void reportMatch(Parser recognizer) {
        this.endErrorCondition(recognizer);
    }

    @Override
    public void reportError(Parser recognizer, RecognitionException e) {
        if (this.inErrorRecoveryMode(recognizer)) {
            return;
        }
        this.beginErrorCondition(recognizer);
        if (e instanceof NoViableAltException) {
            this.reportNoViableAlternative(recognizer, (NoViableAltException)e);
        } else if (e instanceof InputMismatchException) {
            this.reportInputMismatch(recognizer, (InputMismatchException)e);
        } else if (e instanceof FailedPredicateException) {
            this.reportFailedPredicate(recognizer, (FailedPredicateException)e);
        } else {
            System.err.println("unknown recognition error type: " + e.getClass().getName());
            recognizer.notifyErrorListeners(e.getOffendingToken(), e.getMessage(), e);
        }
    }

    @Override
    public void recover(Parser recognizer, RecognitionException e) {
        if (this.lastErrorIndex == recognizer.getInputStream().index() && this.lastErrorStates != null && this.lastErrorStates.contains(recognizer.getState())) {
            recognizer.consume();
        }
        this.lastErrorIndex = recognizer.getInputStream().index();
        if (this.lastErrorStates == null) {
            this.lastErrorStates = new IntervalSet(new int[0]);
        }
        this.lastErrorStates.add(recognizer.getState());
        IntervalSet followSet = this.getErrorRecoverySet(recognizer);
        this.consumeUntil(recognizer, followSet);
    }

    @Override
    public void sync(Parser recognizer) throws RecognitionException {
        ATNState s = ((ParserATNSimulator)recognizer.getInterpreter()).atn.states.get(recognizer.getState());
        if (this.inErrorRecoveryMode(recognizer)) {
            return;
        }
        TokenStream tokens = recognizer.getInputStream();
        int la = tokens.LA(1);
        IntervalSet nextTokens = recognizer.getATN().nextTokens(s);
        if (nextTokens.contains(la)) {
            this.nextTokensContext = null;
            this.nextTokensState = -1;
            return;
        }
        if (nextTokens.contains(-2)) {
            if (this.nextTokensContext == null) {
                this.nextTokensContext = recognizer.getContext();
                this.nextTokensState = recognizer.getState();
            }
            return;
        }
        switch (s.getStateType()) {
            case 3: 
            case 4: 
            case 5: 
            case 10: {
                if (this.singleTokenDeletion(recognizer) != null) {
                    return;
                }
                throw new InputMismatchException(recognizer);
            }
            case 9: 
            case 11: {
                this.reportUnwantedToken(recognizer);
                IntervalSet expecting = recognizer.getExpectedTokens();
                IntervalSet whatFollowsLoopIterationOrRule = expecting.or(this.getErrorRecoverySet(recognizer));
                this.consumeUntil(recognizer, whatFollowsLoopIterationOrRule);
                break;
            }
        }
    }

    protected void reportNoViableAlternative(Parser recognizer, NoViableAltException e) {
        TokenStream tokens = recognizer.getInputStream();
        String input = tokens != null ? (e.getStartToken().getType() == -1 ? "<EOF>" : tokens.getText(e.getStartToken(), e.getOffendingToken())) : "<unknown input>";
        String msg = "no viable alternative at input " + this.escapeWSAndQuote(input);
        recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e);
    }

    protected void reportInputMismatch(Parser recognizer, InputMismatchException e) {
        String msg = "mismatched input " + this.getTokenErrorDisplay(e.getOffendingToken()) + " expecting " + e.getExpectedTokens().toString(recognizer.getVocabulary());
        recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e);
    }

    protected void reportFailedPredicate(Parser recognizer, FailedPredicateException e) {
        String ruleName = recognizer.getRuleNames()[recognizer._ctx.getRuleIndex()];
        String msg = "rule " + ruleName + " " + e.getMessage();
        recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e);
    }

    protected void reportUnwantedToken(Parser recognizer) {
        if (this.inErrorRecoveryMode(recognizer)) {
            return;
        }
        this.beginErrorCondition(recognizer);
        Token t = recognizer.getCurrentToken();
        String tokenName = this.getTokenErrorDisplay(t);
        IntervalSet expecting = this.getExpectedTokens(recognizer);
        String msg = "extraneous input " + tokenName + " expecting " + expecting.toString(recognizer.getVocabulary());
        recognizer.notifyErrorListeners(t, msg, null);
    }

    protected void reportMissingToken(Parser recognizer) {
        if (this.inErrorRecoveryMode(recognizer)) {
            return;
        }
        this.beginErrorCondition(recognizer);
        Token t = recognizer.getCurrentToken();
        IntervalSet expecting = this.getExpectedTokens(recognizer);
        String msg = "missing " + expecting.toString(recognizer.getVocabulary()) + " at " + this.getTokenErrorDisplay(t);
        recognizer.notifyErrorListeners(t, msg, null);
    }

    @Override
    public Token recoverInline(Parser recognizer) throws RecognitionException {
        Token matchedSymbol = this.singleTokenDeletion(recognizer);
        if (matchedSymbol != null) {
            recognizer.consume();
            return matchedSymbol;
        }
        if (this.singleTokenInsertion(recognizer)) {
            return this.getMissingSymbol(recognizer);
        }
        InputMismatchException e = this.nextTokensContext == null ? new InputMismatchException(recognizer) : new InputMismatchException(recognizer, this.nextTokensState, this.nextTokensContext);
        throw e;
    }

    protected boolean singleTokenInsertion(Parser recognizer) {
        int currentSymbolType = recognizer.getInputStream().LA(1);
        ATNState currentState = ((ParserATNSimulator)recognizer.getInterpreter()).atn.states.get(recognizer.getState());
        ATN atn = ((ParserATNSimulator)recognizer.getInterpreter()).atn;
        ATNState next = currentState.transition((int)0).target;
        IntervalSet expectingAtLL2 = atn.nextTokens(next, recognizer._ctx);
        if (expectingAtLL2.contains(currentSymbolType)) {
            this.reportMissingToken(recognizer);
            return true;
        }
        return false;
    }

    protected Token singleTokenDeletion(Parser recognizer) {
        int nextTokenType = recognizer.getInputStream().LA(2);
        IntervalSet expecting = this.getExpectedTokens(recognizer);
        if (expecting.contains(nextTokenType)) {
            this.reportUnwantedToken(recognizer);
            recognizer.consume();
            Token matchedSymbol = recognizer.getCurrentToken();
            this.reportMatch(recognizer);
            return matchedSymbol;
        }
        return null;
    }

    protected Token getMissingSymbol(Parser recognizer) {
        Token currentSymbol = recognizer.getCurrentToken();
        IntervalSet expecting = this.getExpectedTokens(recognizer);
        int expectedTokenType = 0;
        if (!expecting.isNil()) {
            expectedTokenType = expecting.getMinElement();
        }
        String tokenText = expectedTokenType == -1 ? "<missing EOF>" : "<missing " + recognizer.getVocabulary().getDisplayName(expectedTokenType) + ">";
        Token current = currentSymbol;
        Token lookback = recognizer.getInputStream().LT(-1);
        if (current.getType() == -1 && lookback != null) {
            current = lookback;
        }
        return recognizer.getTokenFactory().create(new Pair<TokenSource, CharStream>(current.getTokenSource(), current.getTokenSource().getInputStream()), expectedTokenType, tokenText, 0, -1, -1, current.getLine(), current.getCharPositionInLine());
    }

    protected IntervalSet getExpectedTokens(Parser recognizer) {
        return recognizer.getExpectedTokens();
    }

    protected String getTokenErrorDisplay(Token t) {
        if (t == null) {
            return "<no token>";
        }
        String s = this.getSymbolText(t);
        if (s == null) {
            s = this.getSymbolType(t) == -1 ? "<EOF>" : "<" + this.getSymbolType(t) + ">";
        }
        return this.escapeWSAndQuote(s);
    }

    protected String getSymbolText(Token symbol) {
        return symbol.getText();
    }

    protected int getSymbolType(Token symbol) {
        return symbol.getType();
    }

    protected String escapeWSAndQuote(String s) {
        s = s.replace("\n", "\\n");
        s = s.replace("\r", "\\r");
        s = s.replace("\t", "\\t");
        return "'" + s + "'";
    }

    protected IntervalSet getErrorRecoverySet(Parser recognizer) {
        ATN atn = ((ParserATNSimulator)recognizer.getInterpreter()).atn;
        RuleContext ctx = recognizer._ctx;
        IntervalSet recoverSet = new IntervalSet(new int[0]);
        while (ctx != null && ctx.invokingState >= 0) {
            ATNState invokingState = atn.states.get(ctx.invokingState);
            RuleTransition rt = (RuleTransition)invokingState.transition(0);
            IntervalSet follow = atn.nextTokens(rt.followState);
            recoverSet.addAll(follow);
            ctx = ctx.parent;
        }
        recoverSet.remove(-2);
        return recoverSet;
    }

    protected void consumeUntil(Parser recognizer, IntervalSet set) {
        int ttype = recognizer.getInputStream().LA(1);
        while (ttype != -1 && !set.contains(ttype)) {
            recognizer.consume();
            ttype = recognizer.getInputStream().LA(1);
        }
    }
}

