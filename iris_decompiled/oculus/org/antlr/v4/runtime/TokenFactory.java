/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import oculus.org.antlr.v4.runtime.CharStream;
import oculus.org.antlr.v4.runtime.Token;
import oculus.org.antlr.v4.runtime.TokenSource;
import oculus.org.antlr.v4.runtime.misc.Pair;

public interface TokenFactory<Symbol extends Token> {
    public Symbol create(Pair<TokenSource, CharStream> var1, int var2, String var3, int var4, int var5, int var6, int var7, int var8);

    public Symbol create(int var1, String var2);
}

