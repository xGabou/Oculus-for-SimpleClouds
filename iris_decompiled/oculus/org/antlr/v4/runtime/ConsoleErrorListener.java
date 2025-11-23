/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import oculus.org.antlr.v4.runtime.BaseErrorListener;
import oculus.org.antlr.v4.runtime.RecognitionException;
import oculus.org.antlr.v4.runtime.Recognizer;

public class ConsoleErrorListener
extends BaseErrorListener {
    public static final ConsoleErrorListener INSTANCE = new ConsoleErrorListener();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        System.err.println("line " + line + ":" + charPositionInLine + " " + msg);
    }
}

