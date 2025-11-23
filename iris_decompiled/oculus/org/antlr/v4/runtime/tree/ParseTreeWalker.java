/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.tree;

import oculus.org.antlr.v4.runtime.ParserRuleContext;
import oculus.org.antlr.v4.runtime.tree.ErrorNode;
import oculus.org.antlr.v4.runtime.tree.ParseTree;
import oculus.org.antlr.v4.runtime.tree.ParseTreeListener;
import oculus.org.antlr.v4.runtime.tree.RuleNode;
import oculus.org.antlr.v4.runtime.tree.TerminalNode;

public class ParseTreeWalker {
    public static final ParseTreeWalker DEFAULT = new ParseTreeWalker();

    public void walk(ParseTreeListener listener, ParseTree t) {
        if (t instanceof ErrorNode) {
            listener.visitErrorNode((ErrorNode)t);
            return;
        }
        if (t instanceof TerminalNode) {
            listener.visitTerminal((TerminalNode)t);
            return;
        }
        RuleNode r = (RuleNode)t;
        this.enterRule(listener, r);
        int n = r.getChildCount();
        for (int i = 0; i < n; ++i) {
            this.walk(listener, r.getChild(i));
        }
        this.exitRule(listener, r);
    }

    protected void enterRule(ParseTreeListener listener, RuleNode r) {
        ParserRuleContext ctx = (ParserRuleContext)r.getRuleContext();
        listener.enterEveryRule(ctx);
        ctx.enterRule(listener);
    }

    protected void exitRule(ParseTreeListener listener, RuleNode r) {
        ParserRuleContext ctx = (ParserRuleContext)r.getRuleContext();
        ctx.exitRule(listener);
        listener.exitEveryRule(ctx);
    }
}

