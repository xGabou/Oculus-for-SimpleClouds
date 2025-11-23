/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import oculus.org.antlr.v4.runtime.CharStream;
import oculus.org.antlr.v4.runtime.TokenSource;

public interface Token {
    public static final int INVALID_TYPE = 0;
    public static final int EPSILON = -2;
    public static final int MIN_USER_TOKEN_TYPE = 1;
    public static final int EOF = -1;
    public static final int DEFAULT_CHANNEL = 0;
    public static final int HIDDEN_CHANNEL = 1;
    public static final int MIN_USER_CHANNEL_VALUE = 2;

    public String getText();

    public int getType();

    public int getLine();

    public int getCharPositionInLine();

    public int getChannel();

    public int getTokenIndex();

    public int getStartIndex();

    public int getStopIndex();

    public TokenSource getTokenSource();

    public CharStream getInputStream();
}

