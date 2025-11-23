/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.tree.xpath;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import oculus.org.antlr.v4.runtime.ANTLRInputStream;
import oculus.org.antlr.v4.runtime.CommonTokenStream;
import oculus.org.antlr.v4.runtime.LexerNoViableAltException;
import oculus.org.antlr.v4.runtime.Parser;
import oculus.org.antlr.v4.runtime.ParserRuleContext;
import oculus.org.antlr.v4.runtime.Token;
import oculus.org.antlr.v4.runtime.tree.ParseTree;
import oculus.org.antlr.v4.runtime.tree.xpath.XPathElement;
import oculus.org.antlr.v4.runtime.tree.xpath.XPathLexer;
import oculus.org.antlr.v4.runtime.tree.xpath.XPathLexerErrorListener;
import oculus.org.antlr.v4.runtime.tree.xpath.XPathRuleAnywhereElement;
import oculus.org.antlr.v4.runtime.tree.xpath.XPathRuleElement;
import oculus.org.antlr.v4.runtime.tree.xpath.XPathTokenAnywhereElement;
import oculus.org.antlr.v4.runtime.tree.xpath.XPathTokenElement;
import oculus.org.antlr.v4.runtime.tree.xpath.XPathWildcardAnywhereElement;
import oculus.org.antlr.v4.runtime.tree.xpath.XPathWildcardElement;

public class XPath {
    public static final String WILDCARD = "*";
    public static final String NOT = "!";
    protected String path;
    protected XPathElement[] elements;
    protected Parser parser;

    public XPath(Parser parser, String path) {
        this.parser = parser;
        this.path = path;
        this.elements = this.split(path);
    }

    public XPathElement[] split(String path) {
        ANTLRInputStream in;
        try {
            in = new ANTLRInputStream(new StringReader(path));
        }
        catch (IOException ioe) {
            throw new IllegalArgumentException("Could not read path: " + path, ioe);
        }
        XPathLexer lexer = new XPathLexer(in){

            @Override
            public void recover(LexerNoViableAltException e) {
                throw e;
            }
        };
        lexer.removeErrorListeners();
        lexer.addErrorListener(new XPathLexerErrorListener());
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        try {
            tokenStream.fill();
        }
        catch (LexerNoViableAltException e) {
            int pos = lexer.getCharPositionInLine();
            String msg = "Invalid tokens or characters at index " + pos + " in path '" + path + "'";
            throw new IllegalArgumentException(msg, e);
        }
        List<Token> tokens = tokenStream.getTokens();
        ArrayList<XPathElement> elements = new ArrayList<XPathElement>();
        int n = tokens.size();
        int i = 0;
        block9: while (i < n) {
            Token el = tokens.get(i);
            Token next = null;
            switch (el.getType()) {
                case 3: 
                case 4: {
                    boolean invert;
                    boolean anywhere = el.getType() == 3;
                    next = tokens.get(++i);
                    boolean bl = invert = next.getType() == 6;
                    if (invert) {
                        next = tokens.get(++i);
                    }
                    XPathElement pathElement = this.getXPathElement(next, anywhere);
                    pathElement.invert = invert;
                    elements.add(pathElement);
                    ++i;
                    break;
                }
                case 1: 
                case 2: 
                case 5: {
                    elements.add(this.getXPathElement(el, false));
                    ++i;
                    break;
                }
                case -1: {
                    break block9;
                }
                default: {
                    throw new IllegalArgumentException("Unknowth path element " + el);
                }
            }
        }
        return elements.toArray(new XPathElement[0]);
    }

    protected XPathElement getXPathElement(Token wordToken, boolean anywhere) {
        if (wordToken.getType() == -1) {
            throw new IllegalArgumentException("Missing path element at end of path");
        }
        String word = wordToken.getText();
        int ttype = this.parser.getTokenType(word);
        int ruleIndex = this.parser.getRuleIndex(word);
        switch (wordToken.getType()) {
            case 5: {
                return anywhere ? new XPathWildcardAnywhereElement() : new XPathWildcardElement();
            }
            case 1: 
            case 8: {
                if (ttype == 0) {
                    throw new IllegalArgumentException(word + " at index " + wordToken.getStartIndex() + " isn't a valid token name");
                }
                return anywhere ? new XPathTokenAnywhereElement(word, ttype) : new XPathTokenElement(word, ttype);
            }
        }
        if (ruleIndex == -1) {
            throw new IllegalArgumentException(word + " at index " + wordToken.getStartIndex() + " isn't a valid rule name");
        }
        return anywhere ? new XPathRuleAnywhereElement(word, ruleIndex) : new XPathRuleElement(word, ruleIndex);
    }

    public static Collection<ParseTree> findAll(ParseTree tree, String xpath, Parser parser) {
        XPath p = new XPath(parser, xpath);
        return p.evaluate(tree);
    }

    public Collection<ParseTree> evaluate(ParseTree t) {
        ParserRuleContext dummyRoot = new ParserRuleContext();
        dummyRoot.children = Collections.singletonList(t);
        Set<ParseTree> work = Collections.singleton(dummyRoot);
        for (int i = 0; i < this.elements.length; ++i) {
            LinkedHashSet<ParseTree> next = new LinkedHashSet<ParseTree>();
            for (ParseTree node : work) {
                if (node.getChildCount() <= 0) continue;
                Collection<ParseTree> matching = this.elements[i].evaluate(node);
                next.addAll(matching);
            }
            work = next;
        }
        return work;
    }
}

