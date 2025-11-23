/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import oculus.org.antlr.v4.runtime.CommonToken;
import oculus.org.antlr.v4.runtime.Parser;
import oculus.org.antlr.v4.runtime.ParserRuleContext;
import oculus.org.antlr.v4.runtime.RuleContext;
import oculus.org.antlr.v4.runtime.Token;
import oculus.org.antlr.v4.runtime.misc.Interval;
import oculus.org.antlr.v4.runtime.misc.Predicate;
import oculus.org.antlr.v4.runtime.misc.Utils;
import oculus.org.antlr.v4.runtime.tree.ErrorNode;
import oculus.org.antlr.v4.runtime.tree.ParseTree;
import oculus.org.antlr.v4.runtime.tree.TerminalNode;
import oculus.org.antlr.v4.runtime.tree.TerminalNodeImpl;
import oculus.org.antlr.v4.runtime.tree.Tree;

public class Trees {
    public static String toStringTree(Tree t) {
        return Trees.toStringTree(t, (List<String>)null);
    }

    public static String toStringTree(Tree t, Parser recog) {
        String[] ruleNames = recog != null ? recog.getRuleNames() : null;
        List<String> ruleNamesList = ruleNames != null ? Arrays.asList(ruleNames) : null;
        return Trees.toStringTree(t, ruleNamesList);
    }

    public static String toStringTree(Tree t, List<String> ruleNames) {
        String s = Utils.escapeWhitespace(Trees.getNodeText(t, ruleNames), false);
        if (t.getChildCount() == 0) {
            return s;
        }
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        s = Utils.escapeWhitespace(Trees.getNodeText(t, ruleNames), false);
        buf.append(s);
        buf.append(' ');
        for (int i = 0; i < t.getChildCount(); ++i) {
            if (i > 0) {
                buf.append(' ');
            }
            buf.append(Trees.toStringTree(t.getChild(i), ruleNames));
        }
        buf.append(")");
        return buf.toString();
    }

    public static String getNodeText(Tree t, Parser recog) {
        String[] ruleNames = recog != null ? recog.getRuleNames() : null;
        List<String> ruleNamesList = ruleNames != null ? Arrays.asList(ruleNames) : null;
        return Trees.getNodeText(t, ruleNamesList);
    }

    public static String getNodeText(Tree t, List<String> ruleNames) {
        Object payload;
        if (ruleNames != null) {
            Token symbol;
            if (t instanceof RuleContext) {
                int ruleIndex = ((RuleContext)t).getRuleContext().getRuleIndex();
                String ruleName = ruleNames.get(ruleIndex);
                int altNumber = ((RuleContext)t).getAltNumber();
                if (altNumber != 0) {
                    return ruleName + ":" + altNumber;
                }
                return ruleName;
            }
            if (t instanceof ErrorNode) {
                return t.toString();
            }
            if (t instanceof TerminalNode && (symbol = ((TerminalNode)t).getSymbol()) != null) {
                String s = symbol.getText();
                return s;
            }
        }
        if ((payload = t.getPayload()) instanceof Token) {
            return ((Token)payload).getText();
        }
        return t.getPayload().toString();
    }

    public static List<Tree> getChildren(Tree t) {
        ArrayList<Tree> kids = new ArrayList<Tree>();
        for (int i = 0; i < t.getChildCount(); ++i) {
            kids.add(t.getChild(i));
        }
        return kids;
    }

    public static List<? extends Tree> getAncestors(Tree t) {
        if (t.getParent() == null) {
            return Collections.emptyList();
        }
        ArrayList<Tree> ancestors = new ArrayList<Tree>();
        for (t = t.getParent(); t != null; t = t.getParent()) {
            ancestors.add(0, t);
        }
        return ancestors;
    }

    public static boolean isAncestorOf(Tree t, Tree u) {
        if (t == null || u == null || t.getParent() == null) {
            return false;
        }
        for (Tree p = u.getParent(); p != null; p = p.getParent()) {
            if (t != p) continue;
            return true;
        }
        return false;
    }

    public static Collection<ParseTree> findAllTokenNodes(ParseTree t, int ttype) {
        return Trees.findAllNodes(t, ttype, true);
    }

    public static Collection<ParseTree> findAllRuleNodes(ParseTree t, int ruleIndex) {
        return Trees.findAllNodes(t, ruleIndex, false);
    }

    public static List<ParseTree> findAllNodes(ParseTree t, int index, boolean findTokens) {
        ArrayList<ParseTree> nodes = new ArrayList<ParseTree>();
        Trees._findAllNodes(t, index, findTokens, nodes);
        return nodes;
    }

    public static void _findAllNodes(ParseTree t, int index, boolean findTokens, List<? super ParseTree> nodes) {
        ParserRuleContext ctx;
        if (findTokens && t instanceof TerminalNode) {
            TerminalNode tnode = (TerminalNode)t;
            if (tnode.getSymbol().getType() == index) {
                nodes.add(t);
            }
        } else if (!findTokens && t instanceof ParserRuleContext && (ctx = (ParserRuleContext)t).getRuleIndex() == index) {
            nodes.add(t);
        }
        for (int i = 0; i < t.getChildCount(); ++i) {
            Trees._findAllNodes(t.getChild(i), index, findTokens, nodes);
        }
    }

    public static List<ParseTree> getDescendants(ParseTree t) {
        ArrayList<ParseTree> nodes = new ArrayList<ParseTree>();
        nodes.add(t);
        int n = t.getChildCount();
        for (int i = 0; i < n; ++i) {
            nodes.addAll(Trees.getDescendants(t.getChild(i)));
        }
        return nodes;
    }

    @Deprecated
    public static List<ParseTree> descendants(ParseTree t) {
        return Trees.getDescendants(t);
    }

    public static ParserRuleContext getRootOfSubtreeEnclosingRegion(ParseTree t, int startTokenIndex, int stopTokenIndex) {
        ParserRuleContext r;
        int n = t.getChildCount();
        for (int i = 0; i < n; ++i) {
            ParseTree child = t.getChild(i);
            ParserRuleContext r2 = Trees.getRootOfSubtreeEnclosingRegion(child, startTokenIndex, stopTokenIndex);
            if (r2 == null) continue;
            return r2;
        }
        if (t instanceof ParserRuleContext && startTokenIndex >= (r = (ParserRuleContext)t).getStart().getTokenIndex() && (r.getStop() == null || stopTokenIndex <= r.getStop().getTokenIndex())) {
            return r;
        }
        return null;
    }

    public static void stripChildrenOutOfRange(ParserRuleContext t, ParserRuleContext root, int startIndex, int stopIndex) {
        if (t == null) {
            return;
        }
        for (int i = 0; i < t.getChildCount(); ++i) {
            ParseTree child = t.getChild(i);
            Interval range = child.getSourceInterval();
            if (!(child instanceof ParserRuleContext) || range.b >= startIndex && range.a <= stopIndex || !Trees.isAncestorOf(child, root)) continue;
            CommonToken abbrev = new CommonToken(0, "...");
            t.children.set(i, new TerminalNodeImpl(abbrev));
        }
    }

    public static Tree findNodeSuchThat(Tree t, Predicate<Tree> pred) {
        if (pred.test(t)) {
            return t;
        }
        if (t == null) {
            return null;
        }
        int n = t.getChildCount();
        for (int i = 0; i < n; ++i) {
            Tree u = Trees.findNodeSuchThat(t.getChild(i), pred);
            if (u == null) continue;
            return u;
        }
        return null;
    }

    private Trees() {
    }
}

