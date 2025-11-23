/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import oculus.org.antlr.v4.runtime.CharStream;
import oculus.org.antlr.v4.runtime.CommonToken;
import oculus.org.antlr.v4.runtime.TokenFactory;
import oculus.org.antlr.v4.runtime.TokenSource;
import oculus.org.antlr.v4.runtime.misc.Interval;
import oculus.org.antlr.v4.runtime.misc.Pair;

public class CommonTokenFactory
implements TokenFactory<CommonToken> {
    public static final TokenFactory<CommonToken> DEFAULT = new CommonTokenFactory();
    protected final boolean copyText;

    public CommonTokenFactory(boolean copyText) {
        this.copyText = copyText;
    }

    public CommonTokenFactory() {
        this(false);
    }

    @Override
    public CommonToken create(Pair<TokenSource, CharStream> source, int type, String text, int channel, int start, int stop, int line, int charPositionInLine) {
        CommonToken t = new CommonToken(source, type, channel, start, stop);
        t.setLine(line);
        t.setCharPositionInLine(charPositionInLine);
        if (text != null) {
            t.setText(text);
        } else if (this.copyText && source.b != null) {
            t.setText(((CharStream)source.b).getText(Interval.of(start, stop)));
        }
        return t;
    }

    @Override
    public CommonToken create(int type, String text) {
        return new CommonToken(type, text);
    }
}

