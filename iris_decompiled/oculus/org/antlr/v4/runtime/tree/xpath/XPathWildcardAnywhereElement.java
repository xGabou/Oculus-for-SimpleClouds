/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.tree.xpath;

import java.util.ArrayList;
import java.util.Collection;
import oculus.org.antlr.v4.runtime.tree.ParseTree;
import oculus.org.antlr.v4.runtime.tree.Trees;
import oculus.org.antlr.v4.runtime.tree.xpath.XPathElement;

public class XPathWildcardAnywhereElement
extends XPathElement {
    public XPathWildcardAnywhereElement() {
        super("*");
    }

    @Override
    public Collection<ParseTree> evaluate(ParseTree t) {
        if (this.invert) {
            return new ArrayList<ParseTree>();
        }
        return Trees.getDescendants(t);
    }
}

