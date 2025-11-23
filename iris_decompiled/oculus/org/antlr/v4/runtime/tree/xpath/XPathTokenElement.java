/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.tree.xpath;

import java.util.ArrayList;
import java.util.Collection;
import oculus.org.antlr.v4.runtime.tree.ParseTree;
import oculus.org.antlr.v4.runtime.tree.TerminalNode;
import oculus.org.antlr.v4.runtime.tree.Tree;
import oculus.org.antlr.v4.runtime.tree.Trees;
import oculus.org.antlr.v4.runtime.tree.xpath.XPathElement;

public class XPathTokenElement
extends XPathElement {
    protected int tokenType;

    public XPathTokenElement(String tokenName, int tokenType) {
        super(tokenName);
        this.tokenType = tokenType;
    }

    @Override
    public Collection<ParseTree> evaluate(ParseTree t) {
        ArrayList<ParseTree> nodes = new ArrayList<ParseTree>();
        for (Tree c : Trees.getChildren(t)) {
            TerminalNode tnode;
            if (!(c instanceof TerminalNode) || ((tnode = (TerminalNode)c).getSymbol().getType() != this.tokenType || this.invert) && (tnode.getSymbol().getType() == this.tokenType || !this.invert)) continue;
            nodes.add(tnode);
        }
        return nodes;
    }
}

