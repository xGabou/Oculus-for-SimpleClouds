/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import oculus.org.antlr.v4.runtime.IntStream;
import oculus.org.antlr.v4.runtime.ParserRuleContext;
import oculus.org.antlr.v4.runtime.Recognizer;
import oculus.org.antlr.v4.runtime.RuleContext;
import oculus.org.antlr.v4.runtime.Token;
import oculus.org.antlr.v4.runtime.misc.IntervalSet;

public class RecognitionException
extends RuntimeException {
    private final Recognizer<?, ?> recognizer;
    private final RuleContext ctx;
    private final IntStream input;
    private Token offendingToken;
    private int offendingState = -1;

    public RecognitionException(Recognizer<?, ?> recognizer, IntStream input, ParserRuleContext ctx) {
        this.recognizer = recognizer;
        this.input = input;
        this.ctx = ctx;
        if (recognizer != null) {
            this.offendingState = recognizer.getState();
        }
    }

    public RecognitionException(String message, Recognizer<?, ?> recognizer, IntStream input, ParserRuleContext ctx) {
        super(message);
        this.recognizer = recognizer;
        this.input = input;
        this.ctx = ctx;
        if (recognizer != null) {
            this.offendingState = recognizer.getState();
        }
    }

    public int getOffendingState() {
        return this.offendingState;
    }

    protected final void setOffendingState(int offendingState) {
        this.offendingState = offendingState;
    }

    public IntervalSet getExpectedTokens() {
        if (this.recognizer != null) {
            return this.recognizer.getATN().getExpectedTokens(this.offendingState, this.ctx);
        }
        return null;
    }

    public RuleContext getCtx() {
        return this.ctx;
    }

    public IntStream getInputStream() {
        return this.input;
    }

    public Token getOffendingToken() {
        return this.offendingToken;
    }

    protected final void setOffendingToken(Token offendingToken) {
        this.offendingToken = offendingToken;
    }

    public Recognizer<?, ?> getRecognizer() {
        return this.recognizer;
    }
}

