/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.tree.xpath;

import java.util.ArrayList;
import java.util.Collection;
import oculus.org.antlr.v4.runtime.ParserRuleContext;
import oculus.org.antlr.v4.runtime.tree.ParseTree;
import oculus.org.antlr.v4.runtime.tree.Tree;
import oculus.org.antlr.v4.runtime.tree.Trees;
import oculus.org.antlr.v4.runtime.tree.xpath.XPathElement;

public class XPathRuleElement
extends XPathElement {
    protected int ruleIndex;

    public XPathRuleElement(String ruleName, int ruleIndex) {
        super(ruleName);
        this.ruleIndex = ruleIndex;
    }

    @Override
    public Collection<ParseTree> evaluate(ParseTree t) {
        ArrayList<ParseTree> nodes = new ArrayList<ParseTree>();
        for (Tree c : Trees.getChildren(t)) {
            ParserRuleContext ctx;
            if (!(c instanceof ParserRuleContext) || ((ctx = (ParserRuleContext)c).getRuleIndex() != this.ruleIndex || this.invert) && (ctx.getRuleIndex() == this.ruleIndex || !this.invert)) continue;
            nodes.add(ctx);
        }
        return nodes;
    }
}

