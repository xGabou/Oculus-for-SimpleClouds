/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.tree.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import oculus.org.antlr.v4.runtime.ANTLRInputStream;
import oculus.org.antlr.v4.runtime.BailErrorStrategy;
import oculus.org.antlr.v4.runtime.CommonTokenStream;
import oculus.org.antlr.v4.runtime.Lexer;
import oculus.org.antlr.v4.runtime.ListTokenSource;
import oculus.org.antlr.v4.runtime.Parser;
import oculus.org.antlr.v4.runtime.ParserInterpreter;
import oculus.org.antlr.v4.runtime.ParserRuleContext;
import oculus.org.antlr.v4.runtime.RecognitionException;
import oculus.org.antlr.v4.runtime.Token;
import oculus.org.antlr.v4.runtime.TokenStream;
import oculus.org.antlr.v4.runtime.misc.MultiMap;
import oculus.org.antlr.v4.runtime.misc.ParseCancellationException;
import oculus.org.antlr.v4.runtime.tree.ParseTree;
import oculus.org.antlr.v4.runtime.tree.RuleNode;
import oculus.org.antlr.v4.runtime.tree.TerminalNode;
import oculus.org.antlr.v4.runtime.tree.pattern.Chunk;
import oculus.org.antlr.v4.runtime.tree.pattern.ParseTreeMatch;
import oculus.org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import oculus.org.antlr.v4.runtime.tree.pattern.RuleTagToken;
import oculus.org.antlr.v4.runtime.tree.pattern.TagChunk;
import oculus.org.antlr.v4.runtime.tree.pattern.TextChunk;
import oculus.org.antlr.v4.runtime.tree.pattern.TokenTagToken;

public class ParseTreePatternMatcher {
    private final Lexer lexer;
    private final Parser parser;
    protected String start = "<";
    protected String stop = ">";
    protected String escape = "\\";

    public ParseTreePatternMatcher(Lexer lexer, Parser parser) {
        this.lexer = lexer;
        this.parser = parser;
    }

    public void setDelimiters(String start, String stop, String escapeLeft) {
        if (start == null || start.isEmpty()) {
            throw new IllegalArgumentException("start cannot be null or empty");
        }
        if (stop == null || stop.isEmpty()) {
            throw new IllegalArgumentException("stop cannot be null or empty");
        }
        this.start = start;
        this.stop = stop;
        this.escape = escapeLeft;
    }

    public boolean matches(ParseTree tree, String pattern, int patternRuleIndex) {
        ParseTreePattern p = this.compile(pattern, patternRuleIndex);
        return this.matches(tree, p);
    }

    public boolean matches(ParseTree tree, ParseTreePattern pattern) {
        MultiMap<String, ParseTree> labels = new MultiMap<String, ParseTree>();
        ParseTree mismatchedNode = this.matchImpl(tree, pattern.getPatternTree(), labels);
        return mismatchedNode == null;
    }

    public ParseTreeMatch match(ParseTree tree, String pattern, int patternRuleIndex) {
        ParseTreePattern p = this.compile(pattern, patternRuleIndex);
        return this.match(tree, p);
    }

    public ParseTreeMatch match(ParseTree tree, ParseTreePattern pattern) {
        MultiMap<String, ParseTree> labels = new MultiMap<String, ParseTree>();
        ParseTree mismatchedNode = this.matchImpl(tree, pattern.getPatternTree(), labels);
        return new ParseTreeMatch(tree, pattern, labels, mismatchedNode);
    }

    public ParseTreePattern compile(String pattern, int patternRuleIndex) {
        List<? extends Token> tokenList = this.tokenize(pattern);
        ListTokenSource tokenSrc = new ListTokenSource(tokenList);
        CommonTokenStream tokens = new CommonTokenStream(tokenSrc);
        ParserInterpreter parserInterp = new ParserInterpreter(this.parser.getGrammarFileName(), this.parser.getVocabulary(), Arrays.asList(this.parser.getRuleNames()), this.parser.getATNWithBypassAlts(), (TokenStream)tokens);
        ParserRuleContext tree = null;
        try {
            parserInterp.setErrorHandler(new BailErrorStrategy());
            tree = parserInterp.parse(patternRuleIndex);
        }
        catch (ParseCancellationException e) {
            throw (RecognitionException)e.getCause();
        }
        catch (RecognitionException re) {
            throw re;
        }
        catch (Exception e) {
            throw new CannotInvokeStartRule(e);
        }
        if (tokens.LA(1) != -1) {
            throw new StartRuleDoesNotConsumeFullPattern();
        }
        return new ParseTreePattern(this, pattern, patternRuleIndex, tree);
    }

    public Lexer getLexer() {
        return this.lexer;
    }

    public Parser getParser() {
        return this.parser;
    }

    protected ParseTree matchImpl(ParseTree tree, ParseTree patternTree, MultiMap<String, ParseTree> labels) {
        if (tree == null) {
            throw new IllegalArgumentException("tree cannot be null");
        }
        if (patternTree == null) {
            throw new IllegalArgumentException("patternTree cannot be null");
        }
        if (tree instanceof TerminalNode && patternTree instanceof TerminalNode) {
            TerminalNode t1 = (TerminalNode)tree;
            TerminalNode t2 = (TerminalNode)patternTree;
            TerminalNode mismatchedNode = null;
            if (t1.getSymbol().getType() == t2.getSymbol().getType()) {
                if (t2.getSymbol() instanceof TokenTagToken) {
                    TokenTagToken tokenTagToken = (TokenTagToken)t2.getSymbol();
                    labels.map(tokenTagToken.getTokenName(), tree);
                    if (tokenTagToken.getLabel() != null) {
                        labels.map(tokenTagToken.getLabel(), tree);
                    }
                } else if (!t1.getText().equals(t2.getText()) && mismatchedNode == null) {
                    mismatchedNode = t1;
                }
            } else if (mismatchedNode == null) {
                mismatchedNode = t1;
            }
            return mismatchedNode;
        }
        if (tree instanceof ParserRuleContext && patternTree instanceof ParserRuleContext) {
            ParserRuleContext r1 = (ParserRuleContext)tree;
            ParserRuleContext r2 = (ParserRuleContext)patternTree;
            ParserRuleContext mismatchedNode = null;
            RuleTagToken ruleTagToken = this.getRuleTagToken(r2);
            if (ruleTagToken != null) {
                Object m = null;
                if (r1.getRuleContext().getRuleIndex() == r2.getRuleContext().getRuleIndex()) {
                    labels.map(ruleTagToken.getRuleName(), tree);
                    if (ruleTagToken.getLabel() != null) {
                        labels.map(ruleTagToken.getLabel(), tree);
                    }
                } else if (mismatchedNode == null) {
                    mismatchedNode = r1;
                }
                return mismatchedNode;
            }
            if (r1.getChildCount() != r2.getChildCount()) {
                if (mismatchedNode == null) {
                    mismatchedNode = r1;
                }
                return mismatchedNode;
            }
            int n = r1.getChildCount();
            for (int i = 0; i < n; ++i) {
                ParseTree childMatch = this.matchImpl(r1.getChild(i), patternTree.getChild(i), labels);
                if (childMatch == null) continue;
                return childMatch;
            }
            return mismatchedNode;
        }
        return tree;
    }

    protected RuleTagToken getRuleTagToken(ParseTree t) {
        TerminalNode c;
        RuleNode r;
        if (t instanceof RuleNode && (r = (RuleNode)t).getChildCount() == 1 && r.getChild(0) instanceof TerminalNode && (c = (TerminalNode)r.getChild(0)).getSymbol() instanceof RuleTagToken) {
            return (RuleTagToken)c.getSymbol();
        }
        return null;
    }

    public List<? extends Token> tokenize(String pattern) {
        List<Chunk> chunks = this.split(pattern);
        ArrayList<Token> tokens = new ArrayList<Token>();
        for (Chunk chunk : chunks) {
            Token t;
            if (chunk instanceof TagChunk) {
                TagChunk tagChunk = (TagChunk)chunk;
                if (Character.isUpperCase(tagChunk.getTag().charAt(0))) {
                    Integer ttype = this.parser.getTokenType(tagChunk.getTag());
                    if (ttype == 0) {
                        throw new IllegalArgumentException("Unknown token " + tagChunk.getTag() + " in pattern: " + pattern);
                    }
                    t = new TokenTagToken(tagChunk.getTag(), ttype, tagChunk.getLabel());
                    tokens.add(t);
                    continue;
                }
                if (Character.isLowerCase(tagChunk.getTag().charAt(0))) {
                    int ruleIndex = this.parser.getRuleIndex(tagChunk.getTag());
                    if (ruleIndex == -1) {
                        throw new IllegalArgumentException("Unknown rule " + tagChunk.getTag() + " in pattern: " + pattern);
                    }
                    int ruleImaginaryTokenType = this.parser.getATNWithBypassAlts().ruleToTokenType[ruleIndex];
                    tokens.add(new RuleTagToken(tagChunk.getTag(), ruleImaginaryTokenType, tagChunk.getLabel()));
                    continue;
                }
                throw new IllegalArgumentException("invalid tag: " + tagChunk.getTag() + " in pattern: " + pattern);
            }
            TextChunk textChunk = (TextChunk)chunk;
            ANTLRInputStream in = new ANTLRInputStream(textChunk.getText());
            this.lexer.setInputStream(in);
            t = this.lexer.nextToken();
            while (t.getType() != -1) {
                tokens.add(t);
                t = this.lexer.nextToken();
            }
        }
        return tokens;
    }

    public List<Chunk> split(String pattern) {
        int afterLastTag;
        int i;
        int p = 0;
        int n = pattern.length();
        ArrayList<Chunk> chunks = new ArrayList<Chunk>();
        StringBuilder buf = new StringBuilder();
        ArrayList<Integer> starts = new ArrayList<Integer>();
        ArrayList<Integer> stops = new ArrayList<Integer>();
        while (p < n) {
            if (p == pattern.indexOf(this.escape + this.start, p)) {
                p += this.escape.length() + this.start.length();
                continue;
            }
            if (p == pattern.indexOf(this.escape + this.stop, p)) {
                p += this.escape.length() + this.stop.length();
                continue;
            }
            if (p == pattern.indexOf(this.start, p)) {
                starts.add(p);
                p += this.start.length();
                continue;
            }
            if (p == pattern.indexOf(this.stop, p)) {
                stops.add(p);
                p += this.stop.length();
                continue;
            }
            ++p;
        }
        if (starts.size() > stops.size()) {
            throw new IllegalArgumentException("unterminated tag in pattern: " + pattern);
        }
        if (starts.size() < stops.size()) {
            throw new IllegalArgumentException("missing start tag in pattern: " + pattern);
        }
        int ntags = starts.size();
        for (i = 0; i < ntags; ++i) {
            if ((Integer)starts.get(i) < (Integer)stops.get(i)) continue;
            throw new IllegalArgumentException("tag delimiters out of order in pattern: " + pattern);
        }
        if (ntags == 0) {
            String text = pattern.substring(0, n);
            chunks.add(new TextChunk(text));
        }
        if (ntags > 0 && (Integer)starts.get(0) > 0) {
            String text = pattern.substring(0, (Integer)starts.get(0));
            chunks.add(new TextChunk(text));
        }
        for (i = 0; i < ntags; ++i) {
            String tag;
            String ruleOrToken = tag = pattern.substring((Integer)starts.get(i) + this.start.length(), (Integer)stops.get(i));
            String label = null;
            int colon = tag.indexOf(58);
            if (colon >= 0) {
                label = tag.substring(0, colon);
                ruleOrToken = tag.substring(colon + 1, tag.length());
            }
            chunks.add(new TagChunk(label, ruleOrToken));
            if (i + 1 >= ntags) continue;
            String text = pattern.substring((Integer)stops.get(i) + this.stop.length(), (Integer)starts.get(i + 1));
            chunks.add(new TextChunk(text));
        }
        if (ntags > 0 && (afterLastTag = (Integer)stops.get(ntags - 1) + this.stop.length()) < n) {
            String text = pattern.substring(afterLastTag, n);
            chunks.add(new TextChunk(text));
        }
        for (i = 0; i < chunks.size(); ++i) {
            TextChunk tc;
            String unescaped;
            Chunk c = (Chunk)chunks.get(i);
            if (!(c instanceof TextChunk) || (unescaped = (tc = (TextChunk)c).getText().replace(this.escape, "")).length() >= tc.getText().length()) continue;
            chunks.set(i, new TextChunk(unescaped));
        }
        return chunks;
    }

    public static class StartRuleDoesNotConsumeFullPattern
    extends RuntimeException {
    }

    public static class CannotInvokeStartRule
    extends RuntimeException {
        public CannotInvokeStartRule(Throwable e) {
            super(e);
        }
    }
}

