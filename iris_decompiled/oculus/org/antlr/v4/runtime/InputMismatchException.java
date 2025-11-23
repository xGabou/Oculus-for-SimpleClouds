/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import oculus.org.antlr.v4.runtime.Parser;
import oculus.org.antlr.v4.runtime.ParserRuleContext;
import oculus.org.antlr.v4.runtime.RecognitionException;

public class InputMismatchException
extends RecognitionException {
    public InputMismatchException(Parser recognizer) {
        super(recognizer, recognizer.getInputStream(), recognizer._ctx);
        this.setOffendingToken(recognizer.getCurrentToken());
    }

    public InputMismatchException(Parser recognizer, int state, ParserRuleContext ctx) {
        super(recognizer, recognizer.getInputStream(), ctx);
        this.setOffendingState(state);
        this.setOffendingToken(recognizer.getCurrentToken());
    }
}

