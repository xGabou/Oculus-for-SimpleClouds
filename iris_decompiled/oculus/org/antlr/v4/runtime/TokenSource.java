/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import oculus.org.antlr.v4.runtime.CharStream;
import oculus.org.antlr.v4.runtime.Token;
import oculus.org.antlr.v4.runtime.TokenFactory;

public interface TokenSource {
    public Token nextToken();

    public int getLine();

    public int getCharPositionInLine();

    public CharStream getInputStream();

    public String getSourceName();

    public void setTokenFactory(TokenFactory<?> var1);

    public TokenFactory<?> getTokenFactory();
}

