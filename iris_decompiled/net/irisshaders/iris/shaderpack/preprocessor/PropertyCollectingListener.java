/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.anarres.cpp.DefaultPreprocessorListener
 *  org.anarres.cpp.LexerException
 *  org.anarres.cpp.Source
 */
package net.irisshaders.iris.shaderpack.preprocessor;

import org.anarres.cpp.DefaultPreprocessorListener;
import org.anarres.cpp.LexerException;
import org.anarres.cpp.Source;

public class PropertyCollectingListener
extends DefaultPreprocessorListener {
    public static final String PROPERTY_MARKER = "#warning IRIS_PASSTHROUGH ";
    private final StringBuilder builder = new StringBuilder();

    public void handleWarning(Source source, int line, int column, String msg) throws LexerException {
        if (msg.startsWith(PROPERTY_MARKER)) {
            this.builder.append(msg.replace(PROPERTY_MARKER, ""));
            this.builder.append('\n');
        } else {
            super.handleWarning(source, line, column, msg);
        }
    }

    public void handleError(Source source, int line, int column, String msg) throws LexerException {
        if (msg.contains("Unknown preprocessor directive") || msg.contains("Preprocessor directive not a word")) {
            return;
        }
        super.handleError(source, line, column, msg);
    }

    public String collectLines() {
        return this.builder.toString();
    }
}

