/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import java.util.List;
import oculus.org.antlr.v4.runtime.CharStream;
import oculus.org.antlr.v4.runtime.CommonTokenFactory;
import oculus.org.antlr.v4.runtime.Token;
import oculus.org.antlr.v4.runtime.TokenFactory;
import oculus.org.antlr.v4.runtime.TokenSource;
import oculus.org.antlr.v4.runtime.misc.Pair;

public class ListTokenSource
implements TokenSource {
    protected final List<? extends Token> tokens;
    private final String sourceName;
    protected int i;
    protected Token eofToken;
    private TokenFactory<?> _factory = CommonTokenFactory.DEFAULT;

    public ListTokenSource(List<? extends Token> tokens) {
        this(tokens, null);
    }

    public ListTokenSource(List<? extends Token> tokens, String sourceName) {
        if (tokens == null) {
            throw new NullPointerException("tokens cannot be null");
        }
        this.tokens = tokens;
        this.sourceName = sourceName;
    }

    @Override
    public int getCharPositionInLine() {
        if (this.i < this.tokens.size()) {
            return this.tokens.get(this.i).getCharPositionInLine();
        }
        if (this.eofToken != null) {
            return this.eofToken.getCharPositionInLine();
        }
        if (this.tokens.size() > 0) {
            int lastNewLine;
            Token lastToken = this.tokens.get(this.tokens.size() - 1);
            String tokenText = lastToken.getText();
            if (tokenText != null && (lastNewLine = tokenText.lastIndexOf(10)) >= 0) {
                return tokenText.length() - lastNewLine - 1;
            }
            return lastToken.getCharPositionInLine() + lastToken.getStopIndex() - lastToken.getStartIndex() + 1;
        }
        return 0;
    }

    @Override
    public Token nextToken() {
        if (this.i >= this.tokens.size()) {
            if (this.eofToken == null) {
                int previousStop;
                int start = -1;
                if (this.tokens.size() > 0 && (previousStop = this.tokens.get(this.tokens.size() - 1).getStopIndex()) != -1) {
                    start = previousStop + 1;
                }
                int stop = Math.max(-1, start - 1);
                this.eofToken = this._factory.create(new Pair<TokenSource, CharStream>(this, this.getInputStream()), -1, "EOF", 0, start, stop, this.getLine(), this.getCharPositionInLine());
            }
            return this.eofToken;
        }
        Token t = this.tokens.get(this.i);
        if (this.i == this.tokens.size() - 1 && t.getType() == -1) {
            this.eofToken = t;
        }
        ++this.i;
        return t;
    }

    @Override
    public int getLine() {
        if (this.i < this.tokens.size()) {
            return this.tokens.get(this.i).getLine();
        }
        if (this.eofToken != null) {
            return this.eofToken.getLine();
        }
        if (this.tokens.size() > 0) {
            Token lastToken = this.tokens.get(this.tokens.size() - 1);
            int line = lastToken.getLine();
            String tokenText = lastToken.getText();
            if (tokenText != null) {
                for (int i = 0; i < tokenText.length(); ++i) {
                    if (tokenText.charAt(i) != '\n') continue;
                    ++line;
                }
            }
            return line;
        }
        return 1;
    }

    @Override
    public CharStream getInputStream() {
        if (this.i < this.tokens.size()) {
            return this.tokens.get(this.i).getInputStream();
        }
        if (this.eofToken != null) {
            return this.eofToken.getInputStream();
        }
        if (this.tokens.size() > 0) {
            return this.tokens.get(this.tokens.size() - 1).getInputStream();
        }
        return null;
    }

    @Override
    public String getSourceName() {
        if (this.sourceName != null) {
            return this.sourceName;
        }
        CharStream inputStream = this.getInputStream();
        if (inputStream != null) {
            return inputStream.getSourceName();
        }
        return "List";
    }

    @Override
    public void setTokenFactory(TokenFactory<?> factory) {
        this._factory = factory;
    }

    @Override
    public TokenFactory<?> getTokenFactory() {
        return this._factory;
    }
}

