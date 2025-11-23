/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.tree.xpath;

import java.util.ArrayList;
import java.util.Collection;
import oculus.org.antlr.v4.runtime.tree.ParseTree;
import oculus.org.antlr.v4.runtime.tree.Tree;
import oculus.org.antlr.v4.runtime.tree.Trees;
import oculus.org.antlr.v4.runtime.tree.xpath.XPathElement;

public class XPathWildcardElement
extends XPathElement {
    public XPathWildcardElement() {
        super("*");
    }

    @Override
    public Collection<ParseTree> evaluate(ParseTree t) {
        if (this.invert) {
            return new ArrayList<ParseTree>();
        }
        ArrayList<ParseTree> kids = new ArrayList<ParseTree>();
        for (Tree c : Trees.getChildren(t)) {
            kids.add((ParseTree)c);
        }
        return kids;
    }
}

