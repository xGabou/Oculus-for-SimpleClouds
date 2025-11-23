/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import oculus.org.antlr.v4.runtime.RuleContext;
import oculus.org.antlr.v4.runtime.Token;
import oculus.org.antlr.v4.runtime.TokenSource;
import oculus.org.antlr.v4.runtime.TokenStream;
import oculus.org.antlr.v4.runtime.WritableToken;
import oculus.org.antlr.v4.runtime.misc.Interval;

public class BufferedTokenStream
implements TokenStream {
    protected TokenSource tokenSource;
    protected List<Token> tokens = new ArrayList<Token>(100);
    protected int p = -1;
    protected boolean fetchedEOF;

    public BufferedTokenStream(TokenSource tokenSource) {
        if (tokenSource == null) {
            throw new NullPointerException("tokenSource cannot be null");
        }
        this.tokenSource = tokenSource;
    }

    @Override
    public TokenSource getTokenSource() {
        return this.tokenSource;
    }

    @Override
    public int index() {
        return this.p;
    }

    @Override
    public int mark() {
        return 0;
    }

    @Override
    public void release(int marker) {
    }

    @Deprecated
    public void reset() {
        this.seek(0);
    }

    @Override
    public void seek(int index) {
        this.lazyInit();
        this.p = this.adjustSeekIndex(index);
    }

    @Override
    public int size() {
        return this.tokens.size();
    }

    @Override
    public void consume() {
        boolean skipEofCheck = this.p >= 0 ? (this.fetchedEOF ? this.p < this.tokens.size() - 1 : this.p < this.tokens.size()) : false;
        if (!skipEofCheck && this.LA(1) == -1) {
            throw new IllegalStateException("cannot consume EOF");
        }
        if (this.sync(this.p + 1)) {
            this.p = this.adjustSeekIndex(this.p + 1);
        }
    }

    protected boolean sync(int i) {
        assert (i >= 0);
        int n = i - this.tokens.size() + 1;
        if (n > 0) {
            int fetched = this.fetch(n);
            return fetched >= n;
        }
        return true;
    }

    protected int fetch(int n) {
        if (this.fetchedEOF) {
            return 0;
        }
        for (int i = 0; i < n; ++i) {
            Token t = this.tokenSource.nextToken();
            if (t instanceof WritableToken) {
                ((WritableToken)t).setTokenIndex(this.tokens.size());
            }
            this.tokens.add(t);
            if (t.getType() != -1) continue;
            this.fetchedEOF = true;
            return i + 1;
        }
        return n;
    }

    @Override
    public Token get(int i) {
        if (i < 0 || i >= this.tokens.size()) {
            throw new IndexOutOfBoundsException("token index " + i + " out of range 0.." + (this.tokens.size() - 1));
        }
        return this.tokens.get(i);
    }

    public List<Token> get(int start, int stop) {
        Token t;
        if (start < 0 || stop < 0) {
            return null;
        }
        this.lazyInit();
        ArrayList<Token> subset = new ArrayList<Token>();
        if (stop >= this.tokens.size()) {
            stop = this.tokens.size() - 1;
        }
        for (int i = start; i <= stop && (t = this.tokens.get(i)).getType() != -1; ++i) {
            subset.add(t);
        }
        return subset;
    }

    @Override
    public int LA(int i) {
        return this.LT(i).getType();
    }

    protected Token LB(int k) {
        if (this.p - k < 0) {
            return null;
        }
        return this.tokens.get(this.p - k);
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
        int i = this.p + k - 1;
        this.sync(i);
        if (i >= this.tokens.size()) {
            return this.tokens.get(this.tokens.size() - 1);
        }
        return this.tokens.get(i);
    }

    protected int adjustSeekIndex(int i) {
        return i;
    }

    protected final void lazyInit() {
        if (this.p == -1) {
            this.setup();
        }
    }

    protected void setup() {
        this.sync(0);
        this.p = this.adjustSeekIndex(0);
    }

    public void setTokenSource(TokenSource tokenSource) {
        this.tokenSource = tokenSource;
        this.tokens.clear();
        this.p = -1;
        this.fetchedEOF = false;
    }

    public List<Token> getTokens() {
        return this.tokens;
    }

    public List<Token> getTokens(int start, int stop) {
        return this.getTokens(start, stop, null);
    }

    public List<Token> getTokens(int start, int stop, Set<Integer> types) {
        this.lazyInit();
        if (start < 0 || stop >= this.tokens.size() || stop < 0 || start >= this.tokens.size()) {
            throw new IndexOutOfBoundsException("start " + start + " or stop " + stop + " not in 0.." + (this.tokens.size() - 1));
        }
        if (start > stop) {
            return null;
        }
        ArrayList<Token> filteredTokens = new ArrayList<Token>();
        for (int i = start; i <= stop; ++i) {
            Token t = this.tokens.get(i);
            if (types != null && !types.contains(t.getType())) continue;
            filteredTokens.add(t);
        }
        if (filteredTokens.isEmpty()) {
            filteredTokens = null;
        }
        return filteredTokens;
    }

    public List<Token> getTokens(int start, int stop, int ttype) {
        HashSet<Integer> s = new HashSet<Integer>(ttype);
        s.add(ttype);
        return this.getTokens(start, stop, s);
    }

    protected int nextTokenOnChannel(int i, int channel) {
        this.sync(i);
        if (i >= this.size()) {
            return this.size() - 1;
        }
        Token token = this.tokens.get(i);
        while (token.getChannel() != channel) {
            if (token.getType() == -1) {
                return i;
            }
            this.sync(++i);
            token = this.tokens.get(i);
        }
        return i;
    }

    protected int previousTokenOnChannel(int i, int channel) {
        this.sync(i);
        if (i >= this.size()) {
            return this.size() - 1;
        }
        while (i >= 0) {
            Token token = this.tokens.get(i);
            if (token.getType() == -1 || token.getChannel() == channel) {
                return i;
            }
            --i;
        }
        return i;
    }

    public List<Token> getHiddenTokensToRight(int tokenIndex, int channel) {
        this.lazyInit();
        if (tokenIndex < 0 || tokenIndex >= this.tokens.size()) {
            throw new IndexOutOfBoundsException(tokenIndex + " not in 0.." + (this.tokens.size() - 1));
        }
        int nextOnChannel = this.nextTokenOnChannel(tokenIndex + 1, 0);
        int from = tokenIndex + 1;
        int to = nextOnChannel == -1 ? this.size() - 1 : nextOnChannel;
        return this.filterForChannel(from, to, channel);
    }

    public List<Token> getHiddenTokensToRight(int tokenIndex) {
        return this.getHiddenTokensToRight(tokenIndex, -1);
    }

    public List<Token> getHiddenTokensToLeft(int tokenIndex, int channel) {
        this.lazyInit();
        if (tokenIndex < 0 || tokenIndex >= this.tokens.size()) {
            throw new IndexOutOfBoundsException(tokenIndex + " not in 0.." + (this.tokens.size() - 1));
        }
        if (tokenIndex == 0) {
            return null;
        }
        int prevOnChannel = this.previousTokenOnChannel(tokenIndex - 1, 0);
        if (prevOnChannel == tokenIndex - 1) {
            return null;
        }
        int from = prevOnChannel + 1;
        int to = tokenIndex - 1;
        return this.filterForChannel(from, to, channel);
    }

    public List<Token> getHiddenTokensToLeft(int tokenIndex) {
        return this.getHiddenTokensToLeft(tokenIndex, -1);
    }

    protected List<Token> filterForChannel(int from, int to, int channel) {
        ArrayList<Token> hidden = new ArrayList<Token>();
        for (int i = from; i <= to; ++i) {
            Token t = this.tokens.get(i);
            if (channel == -1) {
                if (t.getChannel() == 0) continue;
                hidden.add(t);
                continue;
            }
            if (t.getChannel() != channel) continue;
            hidden.add(t);
        }
        if (hidden.size() == 0) {
            return null;
        }
        return hidden;
    }

    @Override
    public String getSourceName() {
        return this.tokenSource.getSourceName();
    }

    @Override
    public String getText() {
        return this.getText(Interval.of(0, this.size() - 1));
    }

    @Override
    public String getText(Interval interval) {
        Token t;
        int start = interval.a;
        int stop = interval.b;
        if (start < 0 || stop < 0) {
            return "";
        }
        this.sync(stop);
        if (stop >= this.tokens.size()) {
            stop = this.tokens.size() - 1;
        }
        StringBuilder buf = new StringBuilder();
        for (int i = start; i <= stop && (t = this.tokens.get(i)).getType() != -1; ++i) {
            buf.append(t.getText());
        }
        return buf.toString();
    }

    @Override
    public String getText(RuleContext ctx) {
        return this.getText(ctx.getSourceInterval());
    }

    @Override
    public String getText(Token start, Token stop) {
        if (start != null && stop != null) {
            return this.getText(Interval.of(start.getTokenIndex(), stop.getTokenIndex()));
        }
        return "";
    }

    public void fill() {
        int fetched;
        this.lazyInit();
        int blockSize = 1000;
        while ((fetched = this.fetch(1000)) >= 1000) {
        }
    }
}

