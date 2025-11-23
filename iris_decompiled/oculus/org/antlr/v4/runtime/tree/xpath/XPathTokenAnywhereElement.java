/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.tree.xpath;

import java.util.Collection;
import oculus.org.antlr.v4.runtime.tree.ParseTree;
import oculus.org.antlr.v4.runtime.tree.Trees;
import oculus.org.antlr.v4.runtime.tree.xpath.XPathElement;

public class XPathTokenAnywhereElement
extends XPathElement {
    protected int tokenType;

    public XPathTokenAnywhereElement(String tokenName, int tokenType) {
        super(tokenName);
        this.tokenType = tokenType;
    }

    @Override
    public Collection<ParseTree> evaluate(ParseTree t) {
        return Trees.findAllTokenNodes(t, this.tokenType);
    }
}

