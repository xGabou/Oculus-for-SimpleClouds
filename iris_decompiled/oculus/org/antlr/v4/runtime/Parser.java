/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import oculus.org.antlr.v4.runtime.ANTLRErrorListener;
import oculus.org.antlr.v4.runtime.ANTLRErrorStrategy;
import oculus.org.antlr.v4.runtime.DefaultErrorStrategy;
import oculus.org.antlr.v4.runtime.IntStream;
import oculus.org.antlr.v4.runtime.Lexer;
import oculus.org.antlr.v4.runtime.ParserRuleContext;
import oculus.org.antlr.v4.runtime.RecognitionException;
import oculus.org.antlr.v4.runtime.Recognizer;
import oculus.org.antlr.v4.runtime.RuleContext;
import oculus.org.antlr.v4.runtime.Token;
import oculus.org.antlr.v4.runtime.TokenFactory;
import oculus.org.antlr.v4.runtime.TokenSource;
import oculus.org.antlr.v4.runtime.TokenStream;
import oculus.org.antlr.v4.runtime.atn.ATN;
import oculus.org.antlr.v4.runtime.atn.ATNDeserializationOptions;
import oculus.org.antlr.v4.runtime.atn.ATNDeserializer;
import oculus.org.antlr.v4.runtime.atn.ATNSimulator;
import oculus.org.antlr.v4.runtime.atn.ATNState;
import oculus.org.antlr.v4.runtime.atn.ParseInfo;
import oculus.org.antlr.v4.runtime.atn.ParserATNSimulator;
import oculus.org.antlr.v4.runtime.atn.PredictionMode;
import oculus.org.antlr.v4.runtime.atn.ProfilingATNSimulator;
import oculus.org.antlr.v4.runtime.atn.RuleTransition;
import oculus.org.antlr.v4.runtime.dfa.DFA;
import oculus.org.antlr.v4.runtime.misc.IntegerStack;
import oculus.org.antlr.v4.runtime.misc.IntervalSet;
import oculus.org.antlr.v4.runtime.tree.ErrorNode;
import oculus.org.antlr.v4.runtime.tree.ErrorNodeImpl;
import oculus.org.antlr.v4.runtime.tree.ParseTreeListener;
import oculus.org.antlr.v4.runtime.tree.TerminalNode;
import oculus.org.antlr.v4.runtime.tree.TerminalNodeImpl;
import oculus.org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import oculus.org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher;

public abstract class Parser
extends Recognizer<Token, ParserATNSimulator> {
    private ATN bypassAltsAtnCache;
    protected ANTLRErrorStrategy _errHandler = new DefaultErrorStrategy();
    protected TokenStream _input;
    protected final IntegerStack _precedenceStack = new IntegerStack();
    protected ParserRuleContext _ctx;
    protected boolean _buildParseTrees;
    private TraceListener _tracer;
    protected List<ParseTreeListener> _parseListeners;
    protected int _syntaxErrors;
    protected boolean matchedEOF;

    public Parser(TokenStream input) {
        this._precedenceStack.push(0);
        this._buildParseTrees = true;
        this.setInputStream(input);
    }

    public void reset() {
        if (this.getInputStream() != null) {
            this.getInputStream().seek(0);
        }
        this._errHandler.reset(this);
        this._ctx = null;
        this._syntaxErrors = 0;
        this.matchedEOF = false;
        this.setTrace(false);
        this._precedenceStack.clear();
        this._precedenceStack.push(0);
        Object interpreter = this.getInterpreter();
        if (interpreter != null) {
            ((ATNSimulator)interpreter).reset();
        }
    }

    public Token match(int ttype) throws RecognitionException {
        Token t = this.getCurrentToken();
        if (t.getType() == ttype) {
            if (ttype == -1) {
                this.matchedEOF = true;
            }
            this._errHandler.reportMatch(this);
            this.consume();
        } else {
            t = this._errHandler.recoverInline(this);
            if (this._buildParseTrees && t.getTokenIndex() == -1) {
                this._ctx.addErrorNode(this.createErrorNode(this._ctx, t));
            }
        }
        return t;
    }

    public Token matchWildcard() throws RecognitionException {
        Token t = this.getCurrentToken();
        if (t.getType() > 0) {
            this._errHandler.reportMatch(this);
            this.consume();
        } else {
            t = this._errHandler.recoverInline(this);
            if (this._buildParseTrees && t.getTokenIndex() == -1) {
                this._ctx.addErrorNode(this.createErrorNode(this._ctx, t));
            }
        }
        return t;
    }

    public void setBuildParseTree(boolean buildParseTrees) {
        this._buildParseTrees = buildParseTrees;
    }

    public boolean getBuildParseTree() {
        return this._buildParseTrees;
    }

    public void setTrimParseTree(boolean trimParseTrees) {
        if (trimParseTrees) {
            if (this.getTrimParseTree()) {
                return;
            }
            this.addParseListener(TrimToSizeListener.INSTANCE);
        } else {
            this.removeParseListener(TrimToSizeListener.INSTANCE);
        }
    }

    public boolean getTrimParseTree() {
        return this.getParseListeners().contains(TrimToSizeListener.INSTANCE);
    }

    public List<ParseTreeListener> getParseListeners() {
        List<ParseTreeListener> listeners = this._parseListeners;
        if (listeners == null) {
            return Collections.emptyList();
        }
        return listeners;
    }

    public void addParseListener(ParseTreeListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener");
        }
        if (this._parseListeners == null) {
            this._parseListeners = new ArrayList<ParseTreeListener>();
        }
        this._parseListeners.add(listener);
    }

    public void removeParseListener(ParseTreeListener listener) {
        if (this._parseListeners != null && this._parseListeners.remove(listener) && this._parseListeners.isEmpty()) {
            this._parseListeners = null;
        }
    }

    public void removeParseListeners() {
        this._parseListeners = null;
    }

    protected void triggerEnterRuleEvent() {
        for (ParseTreeListener listener : this._parseListeners) {
            listener.enterEveryRule(this._ctx);
            this._ctx.enterRule(listener);
        }
    }

    protected void triggerExitRuleEvent() {
        for (int i = this._parseListeners.size() - 1; i >= 0; --i) {
            ParseTreeListener listener = this._parseListeners.get(i);
            this._ctx.exitRule(listener);
            listener.exitEveryRule(this._ctx);
        }
    }

    public int getNumberOfSyntaxErrors() {
        return this._syntaxErrors;
    }

    @Override
    public TokenFactory<?> getTokenFactory() {
        return this._input.getTokenSource().getTokenFactory();
    }

    @Override
    public void setTokenFactory(TokenFactory<?> factory) {
        this._input.getTokenSource().setTokenFactory(factory);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ATN getATNWithBypassAlts() {
        String serializedAtn = this.getSerializedATN();
        if (serializedAtn == null) {
            throw new UnsupportedOperationException("The current parser does not support an ATN with bypass alternatives.");
        }
        Parser parser = this;
        synchronized (parser) {
            if (this.bypassAltsAtnCache != null) {
                return this.bypassAltsAtnCache;
            }
            ATNDeserializationOptions deserializationOptions = new ATNDeserializationOptions();
            deserializationOptions.setGenerateRuleBypassTransitions(true);
            this.bypassAltsAtnCache = new ATNDeserializer(deserializationOptions).deserialize(serializedAtn.toCharArray());
            return this.bypassAltsAtnCache;
        }
    }

    public ParseTreePattern compileParseTreePattern(String pattern, int patternRuleIndex) {
        TokenSource tokenSource;
        if (this.getTokenStream() != null && (tokenSource = this.getTokenStream().getTokenSource()) instanceof Lexer) {
            Lexer lexer = (Lexer)tokenSource;
            return this.compileParseTreePattern(pattern, patternRuleIndex, lexer);
        }
        throw new UnsupportedOperationException("Parser can't discover a lexer to use");
    }

    public ParseTreePattern compileParseTreePattern(String pattern, int patternRuleIndex, Lexer lexer) {
        ParseTreePatternMatcher m = new ParseTreePatternMatcher(lexer, this);
        return m.compile(pattern, patternRuleIndex);
    }

    public ANTLRErrorStrategy getErrorHandler() {
        return this._errHandler;
    }

    public void setErrorHandler(ANTLRErrorStrategy handler) {
        this._errHandler = handler;
    }

    @Override
    public TokenStream getInputStream() {
        return this.getTokenStream();
    }

    @Override
    public final void setInputStream(IntStream input) {
        this.setTokenStream((TokenStream)input);
    }

    public TokenStream getTokenStream() {
        return this._input;
    }

    public void setTokenStream(TokenStream input) {
        this._input = null;
        this.reset();
        this._input = input;
    }

    public Token getCurrentToken() {
        return this._input.LT(1);
    }

    public final void notifyErrorListeners(String msg) {
        this.notifyErrorListeners(this.getCurrentToken(), msg, null);
    }

    public void notifyErrorListeners(Token offendingToken, String msg, RecognitionException e) {
        ++this._syntaxErrors;
        int line = -1;
        int charPositionInLine = -1;
        line = offendingToken.getLine();
        charPositionInLine = offendingToken.getCharPositionInLine();
        ANTLRErrorListener listener = this.getErrorListenerDispatch();
        listener.syntaxError(this, offendingToken, line, charPositionInLine, msg, e);
    }

    public Token consume() {
        Token o;
        block4: {
            block5: {
                boolean hasListener;
                o = this.getCurrentToken();
                if (o.getType() != -1) {
                    this.getInputStream().consume();
                }
                boolean bl = hasListener = this._parseListeners != null && !this._parseListeners.isEmpty();
                if (!this._buildParseTrees && !hasListener) break block4;
                if (!this._errHandler.inErrorRecoveryMode(this)) break block5;
                ErrorNode node = this._ctx.addErrorNode(this.createErrorNode(this._ctx, o));
                if (this._parseListeners == null) break block4;
                for (ParseTreeListener listener : this._parseListeners) {
                    listener.visitErrorNode(node);
                }
                break block4;
            }
            TerminalNode node = this._ctx.addChild(this.createTerminalNode(this._ctx, o));
            if (this._parseListeners != null) {
                for (ParseTreeListener listener : this._parseListeners) {
                    listener.visitTerminal(node);
                }
            }
        }
        return o;
    }

    public TerminalNode createTerminalNode(ParserRuleContext parent, Token t) {
        return new TerminalNodeImpl(t);
    }

    public ErrorNode createErrorNode(ParserRuleContext parent, Token t) {
        return new ErrorNodeImpl(t);
    }

    protected void addContextToParseTree() {
        ParserRuleContext parent = (ParserRuleContext)this._ctx.parent;
        if (parent != null) {
            parent.addChild(this._ctx);
        }
    }

    public void enterRule(ParserRuleContext localctx, int state, int ruleIndex) {
        this.setState(state);
        this._ctx = localctx;
        this._ctx.start = this._input.LT(1);
        if (this._buildParseTrees) {
            this.addContextToParseTree();
        }
        if (this._parseListeners != null) {
            this.triggerEnterRuleEvent();
        }
    }

    public void exitRule() {
        this._ctx.stop = this.matchedEOF ? this._input.LT(1) : this._input.LT(-1);
        if (this._parseListeners != null) {
            this.triggerExitRuleEvent();
        }
        this.setState(this._ctx.invokingState);
        this._ctx = (ParserRuleContext)this._ctx.parent;
    }

    public void enterOuterAlt(ParserRuleContext localctx, int altNum) {
        ParserRuleContext parent;
        localctx.setAltNumber(altNum);
        if (this._buildParseTrees && this._ctx != localctx && (parent = (ParserRuleContext)this._ctx.parent) != null) {
            parent.removeLastChild();
            parent.addChild(localctx);
        }
        this._ctx = localctx;
    }

    public final int getPrecedence() {
        if (this._precedenceStack.isEmpty()) {
            return -1;
        }
        return this._precedenceStack.peek();
    }

    @Deprecated
    public void enterRecursionRule(ParserRuleContext localctx, int ruleIndex) {
        this.enterRecursionRule(localctx, this.getATN().ruleToStartState[ruleIndex].stateNumber, ruleIndex, 0);
    }

    public void enterRecursionRule(ParserRuleContext localctx, int state, int ruleIndex, int precedence) {
        this.setState(state);
        this._precedenceStack.push(precedence);
        this._ctx = localctx;
        this._ctx.start = this._input.LT(1);
        if (this._parseListeners != null) {
            this.triggerEnterRuleEvent();
        }
    }

    public void pushNewRecursionContext(ParserRuleContext localctx, int state, int ruleIndex) {
        ParserRuleContext previous = this._ctx;
        previous.parent = localctx;
        previous.invokingState = state;
        previous.stop = this._input.LT(-1);
        this._ctx = localctx;
        this._ctx.start = previous.start;
        if (this._buildParseTrees) {
            this._ctx.addChild(previous);
        }
        if (this._parseListeners != null) {
            this.triggerEnterRuleEvent();
        }
    }

    public void unrollRecursionContexts(ParserRuleContext _parentctx) {
        this._precedenceStack.pop();
        this._ctx.stop = this._input.LT(-1);
        ParserRuleContext retctx = this._ctx;
        if (this._parseListeners != null) {
            while (this._ctx != _parentctx) {
                this.triggerExitRuleEvent();
                this._ctx = (ParserRuleContext)this._ctx.parent;
            }
        } else {
            this._ctx = _parentctx;
        }
        retctx.parent = _parentctx;
        if (this._buildParseTrees && _parentctx != null) {
            _parentctx.addChild(retctx);
        }
    }

    public ParserRuleContext getInvokingContext(int ruleIndex) {
        ParserRuleContext p = this._ctx;
        while (p != null) {
            if (p.getRuleIndex() == ruleIndex) {
                return p;
            }
            p = (ParserRuleContext)p.parent;
        }
        return null;
    }

    public ParserRuleContext getContext() {
        return this._ctx;
    }

    public void setContext(ParserRuleContext ctx) {
        this._ctx = ctx;
    }

    @Override
    public boolean precpred(RuleContext localctx, int precedence) {
        return precedence >= this._precedenceStack.peek();
    }

    public boolean inContext(String context) {
        return false;
    }

    public boolean isExpectedToken(int symbol) {
        ATN atn = ((ParserATNSimulator)this.getInterpreter()).atn;
        ParserRuleContext ctx = this._ctx;
        ATNState s = atn.states.get(this.getState());
        IntervalSet following = atn.nextTokens(s);
        if (following.contains(symbol)) {
            return true;
        }
        if (!following.contains(-2)) {
            return false;
        }
        while (ctx != null && ctx.invokingState >= 0 && following.contains(-2)) {
            ATNState invokingState = atn.states.get(ctx.invokingState);
            RuleTransition rt = (RuleTransition)invokingState.transition(0);
            following = atn.nextTokens(rt.followState);
            if (following.contains(symbol)) {
                return true;
            }
            ctx = (ParserRuleContext)ctx.parent;
        }
        return following.contains(-2) && symbol == -1;
    }

    public boolean isMatchedEOF() {
        return this.matchedEOF;
    }

    public IntervalSet getExpectedTokens() {
        return this.getATN().getExpectedTokens(this.getState(), this.getContext());
    }

    public IntervalSet getExpectedTokensWithinCurrentRule() {
        ATN atn = ((ParserATNSimulator)this.getInterpreter()).atn;
        ATNState s = atn.states.get(this.getState());
        return atn.nextTokens(s);
    }

    public int getRuleIndex(String ruleName) {
        Integer ruleIndex = this.getRuleIndexMap().get(ruleName);
        if (ruleIndex != null) {
            return ruleIndex;
        }
        return -1;
    }

    public ParserRuleContext getRuleContext() {
        return this._ctx;
    }

    public List<String> getRuleInvocationStack() {
        return this.getRuleInvocationStack(this._ctx);
    }

    public List<String> getRuleInvocationStack(RuleContext p) {
        String[] ruleNames = this.getRuleNames();
        ArrayList<String> stack = new ArrayList<String>();
        while (p != null) {
            int ruleIndex = p.getRuleIndex();
            if (ruleIndex < 0) {
                stack.add("n/a");
            } else {
                stack.add(ruleNames[ruleIndex]);
            }
            p = p.parent;
        }
        return stack;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<String> getDFAStrings() {
        DFA[] dFAArray = ((ParserATNSimulator)this._interp).decisionToDFA;
        synchronized (((ParserATNSimulator)this._interp).decisionToDFA) {
            ArrayList<String> s = new ArrayList<String>();
            for (int d = 0; d < ((ParserATNSimulator)this._interp).decisionToDFA.length; ++d) {
                DFA dfa = ((ParserATNSimulator)this._interp).decisionToDFA[d];
                s.add(dfa.toString(this.getVocabulary()));
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return s;
        }
    }

    public void dumpDFA() {
        this.dumpDFA(System.out);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void dumpDFA(PrintStream dumpStream) {
        DFA[] dFAArray = ((ParserATNSimulator)this._interp).decisionToDFA;
        synchronized (((ParserATNSimulator)this._interp).decisionToDFA) {
            boolean seenOne = false;
            for (int d = 0; d < ((ParserATNSimulator)this._interp).decisionToDFA.length; ++d) {
                DFA dfa = ((ParserATNSimulator)this._interp).decisionToDFA[d];
                if (dfa.states.isEmpty()) continue;
                if (seenOne) {
                    dumpStream.println();
                }
                dumpStream.println("Decision " + dfa.decision + ":");
                dumpStream.print(dfa.toString(this.getVocabulary()));
                seenOne = true;
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return;
        }
    }

    public String getSourceName() {
        return this._input.getSourceName();
    }

    @Override
    public ParseInfo getParseInfo() {
        ParserATNSimulator interp = (ParserATNSimulator)this.getInterpreter();
        if (interp instanceof ProfilingATNSimulator) {
            return new ParseInfo((ProfilingATNSimulator)interp);
        }
        return null;
    }

    public void setProfile(boolean profile) {
        ParserATNSimulator interp = (ParserATNSimulator)this.getInterpreter();
        PredictionMode saveMode = interp.getPredictionMode();
        if (profile) {
            if (!(interp instanceof ProfilingATNSimulator)) {
                this.setInterpreter(new ProfilingATNSimulator(this));
            }
        } else if (interp instanceof ProfilingATNSimulator) {
            ParserATNSimulator sim = new ParserATNSimulator(this, this.getATN(), interp.decisionToDFA, interp.getSharedContextCache());
            this.setInterpreter(sim);
        }
        ((ParserATNSimulator)this.getInterpreter()).setPredictionMode(saveMode);
    }

    public void setTrace(boolean trace) {
        if (!trace) {
            this.removeParseListener(this._tracer);
            this._tracer = null;
        } else {
            if (this._tracer != null) {
                this.removeParseListener(this._tracer);
            } else {
                this._tracer = new TraceListener();
            }
            this.addParseListener(this._tracer);
        }
    }

    public boolean isTrace() {
        return this._tracer != null;
    }

    public static class TrimToSizeListener
    implements ParseTreeListener {
        public static final TrimToSizeListener INSTANCE = new TrimToSizeListener();

        @Override
        public void enterEveryRule(ParserRuleContext ctx) {
        }

        @Override
        public void visitTerminal(TerminalNode node) {
        }

        @Override
        public void visitErrorNode(ErrorNode node) {
        }

        @Override
        public void exitEveryRule(ParserRuleContext ctx) {
            if (ctx.children instanceof ArrayList) {
                ((ArrayList)ctx.children).trimToSize();
            }
        }
    }

    public class TraceListener
    implements ParseTreeListener {
        @Override
        public void enterEveryRule(ParserRuleContext ctx) {
            System.out.println("enter   " + Parser.this.getRuleNames()[ctx.getRuleIndex()] + ", LT(1)=" + Parser.this._input.LT(1).getText());
        }

        @Override
        public void visitTerminal(TerminalNode node) {
            System.out.println("consume " + node.getSymbol() + " rule " + Parser.this.getRuleNames()[Parser.this._ctx.getRuleIndex()]);
        }

        @Override
        public void visitErrorNode(ErrorNode node) {
        }

        @Override
        public void exitEveryRule(ParserRuleContext ctx) {
            System.out.println("exit    " + Parser.this.getRuleNames()[ctx.getRuleIndex()] + ", LT(1)=" + Parser.this._input.LT(1).getText());
        }
    }
}

