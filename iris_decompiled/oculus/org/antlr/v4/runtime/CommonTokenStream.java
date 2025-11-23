/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import oculus.org.antlr.v4.runtime.BufferedTokenStream;
import oculus.org.antlr.v4.runtime.Token;
import oculus.org.antlr.v4.runtime.TokenSource;

public class CommonTokenStream
extends BufferedTokenStream {
    protected int channel = 0;

    public CommonTokenStream(TokenSource tokenSource) {
        super(tokenSource);
    }

    public CommonTokenStream(TokenSource tokenSource, int channel) {
        this(tokenSource);
        this.channel = channel;
    }

    @Override
    protected int adjustSeekIndex(int i) {
        return this.nextTokenOnChannel(i, this.channel);
    }

    @Override
    protected Token LB(int k) {
        if (k == 0 || this.p - k < 0) {
            return null;
        }
        int i = this.p;
        for (int n = 1; n <= k && i > 0; ++n) {
            i = this.previousTokenOnChannel(i - 1, this.channel);
        }
        if (i < 0) {
            return null;
        }
        return (Token)this.tokens.get(i);
    }

    @Override
    public Token LT(int k) {
        this.lazyInit();
        if (k == 0) {
            return null;
        }
        if (k < 0) {
            return this.LB(-k);
        }
        int i = this.p;
        for (int n = 1; n < k; ++n) {
            if (!this.sync(i + 1)) continue;
            i = this.nextTokenOnChannel(i + 1, this.channel);
        }
        return (Token)this.tokens.get(i);
    }

    public int getNumberOfOnChannelTokens() {
        int n = 0;
        this.fill();
        for (int i = 0; i < this.tokens.size(); ++i) {
            Token t = (Token)this.tokens.get(i);
            if (t.getChannel() == this.channel) {
                ++n;
            }
            if (t.getType() == -1) break;
        }
        return n;
    }
}

