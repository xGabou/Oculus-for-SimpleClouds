/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.tree.xpath;

import oculus.org.antlr.v4.runtime.CharStream;
import oculus.org.antlr.v4.runtime.CommonToken;
import oculus.org.antlr.v4.runtime.Lexer;
import oculus.org.antlr.v4.runtime.LexerNoViableAltException;
import oculus.org.antlr.v4.runtime.Token;
import oculus.org.antlr.v4.runtime.Vocabulary;
import oculus.org.antlr.v4.runtime.VocabularyImpl;
import oculus.org.antlr.v4.runtime.atn.ATN;
import oculus.org.antlr.v4.runtime.misc.Interval;

public class XPathLexer
extends Lexer {
    public static final int TOKEN_REF = 1;
    public static final int RULE_REF = 2;
    public static final int ANYWHERE = 3;
    public static final int ROOT = 4;
    public static final int WILDCARD = 5;
    public static final int BANG = 6;
    public static final int ID = 7;
    public static final int STRING = 8;
    public static final String[] modeNames = new String[]{"DEFAULT_MODE"};
    public static final String[] ruleNames = new String[]{"ANYWHERE", "ROOT", "WILDCARD", "BANG", "ID", "NameChar", "NameStartChar", "STRING"};
    private static final String[] _LITERAL_NAMES = new String[]{null, null, null, "'//'", "'/'", "'*'", "'!'"};
    private static final String[] _SYMBOLIC_NAMES = new String[]{null, "TOKEN_REF", "RULE_REF", "ANYWHERE", "ROOT", "WILDCARD", "BANG", "ID", "STRING"};
    public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
    @Deprecated
    public static final String[] tokenNames = new String[_SYMBOLIC_NAMES.length];
    protected int line = 1;
    protected int charPositionInLine = 0;

    @Override
    public String getGrammarFileName() {
        return "XPathLexer.g4";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String[] getModeNames() {
        return modeNames;
    }

    @Override
    @Deprecated
    public String[] getTokenNames() {
        return tokenNames;
    }

    @Override
    public Vocabulary getVocabulary() {
        return VOCABULARY;
    }

    @Override
    public ATN getATN() {
        return null;
    }

    public XPathLexer(CharStream input) {
        super(input);
    }

    @Override
    public Token nextToken() {
        this._tokenStartCharIndex = this._input.index();
        CommonToken t = null;
        block7: while (t == null) {
            switch (this._input.LA(1)) {
                case 47: {
                    this.consume();
                    if (this._input.LA(1) == 47) {
                        this.consume();
                        t = new CommonToken(3, "//");
                        continue block7;
                    }
                    t = new CommonToken(4, "/");
                    continue block7;
                }
                case 42: {
                    this.consume();
                    t = new CommonToken(5, "*");
                    continue block7;
                }
                case 33: {
                    this.consume();
                    t = new CommonToken(6, "!");
                    continue block7;
                }
                case 39: {
                    String s = this.matchString();
                    t = new CommonToken(8, s);
                    continue block7;
                }
                case -1: {
                    return new CommonToken(-1, "<EOF>");
                }
            }
            if (this.isNameStartChar(this._input.LA(1))) {
                String id = this.matchID();
                if (Character.isUpperCase(id.charAt(0))) {
                    t = new CommonToken(1, id);
                    continue;
                }
                t = new CommonToken(2, id);
                continue;
            }
            throw new LexerNoViableAltException(this, this._input, this._tokenStartCharIndex, null);
        }
        t.setStartIndex(this._tokenStartCharIndex);
        t.setCharPositionInLine(this._tokenStartCharIndex);
        t.setLine(this.line);
        return t;
    }

    public void consume() {
        int curChar = this._input.LA(1);
        if (curChar == 10) {
            ++this.line;
            this.charPositionInLine = 0;
        } else {
            ++this.charPositionInLine;
        }
        this._input.consume();
    }

    @Override
    public int getCharPositionInLine() {
        return this.charPositionInLine;
    }

    public String matchID() {
        int start = this._input.index();
        this.consume();
        while (this.isNameChar(this._input.LA(1))) {
            this.consume();
        }
        return this._input.getText(Interval.of(start, this._input.index() - 1));
    }

    public String matchString() {
        int start = this._input.index();
        this.consume();
        while (this._input.LA(1) != 39) {
            this.consume();
        }
        this.consume();
        return this._input.getText(Interval.of(start, this._input.index() - 1));
    }

    public boolean isNameChar(int c) {
        return Character.isUnicodeIdentifierPart(c);
    }

    public boolean isNameStartChar(int c) {
        return Character.isUnicodeIdentifierStart(c);
    }

    static {
        for (int i = 0; i < tokenNames.length; ++i) {
            XPathLexer.tokenNames[i] = VOCABULARY.getLiteralName(i);
            if (tokenNames[i] == null) {
                XPathLexer.tokenNames[i] = VOCABULARY.getSymbolicName(i);
            }
            if (tokenNames[i] != null) continue;
            XPathLexer.tokenNames[i] = "<INVALID>";
        }
    }
}

