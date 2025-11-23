/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.tree.xpath;

import java.util.Collection;
import oculus.org.antlr.v4.runtime.tree.ParseTree;
import oculus.org.antlr.v4.runtime.tree.Trees;
import oculus.org.antlr.v4.runtime.tree.xpath.XPathElement;

public class XPathRuleAnywhereElement
extends XPathElement {
    protected int ruleIndex;

    public XPathRuleAnywhereElement(String ruleName, int ruleIndex) {
        super(ruleName);
        this.ruleIndex = ruleIndex;
    }

    @Override
    public Collection<ParseTree> evaluate(ParseTree t) {
        return Trees.findAllRuleNodes(t, this.ruleIndex);
    }
}

