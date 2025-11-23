/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import java.util.Arrays;
import java.util.List;
import oculus.org.antlr.v4.runtime.Parser;
import oculus.org.antlr.v4.runtime.ParserRuleContext;
import oculus.org.antlr.v4.runtime.Recognizer;
import oculus.org.antlr.v4.runtime.misc.Interval;
import oculus.org.antlr.v4.runtime.tree.ParseTree;
import oculus.org.antlr.v4.runtime.tree.ParseTreeVisitor;
import oculus.org.antlr.v4.runtime.tree.RuleNode;
import oculus.org.antlr.v4.runtime.tree.Tree;
import oculus.org.antlr.v4.runtime.tree.Trees;

public class RuleContext
implements RuleNode {
    public RuleContext parent;
    public int invokingState = -1;

    public RuleContext() {
    }

    public RuleContext(RuleContext parent, int invokingState) {
        this.parent = parent;
        this.invokingState = invokingState;
    }

    public int depth() {
        int n = 0;
        RuleContext p = this;
        while (p != null) {
            p = p.parent;
            ++n;
        }
        return n;
    }

    public boolean isEmpty() {
        return this.invokingState == -1;
    }

    @Override
    public Interval getSourceInterval() {
        return Interval.INVALID;
    }

    @Override
    public RuleContext getRuleContext() {
        return this;
    }

    @Override
    public RuleContext getParent() {
        return this.parent;
    }

    @Override
    public RuleContext getPayload() {
        return this;
    }

    @Override
    public String getText() {
        if (this.getChildCount() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.getChildCount(); ++i) {
            builder.append(this.getChild(i).getText());
        }
        return builder.toString();
    }

    public int getRuleIndex() {
        return -1;
    }

    public int getAltNumber() {
        return 0;
    }

    public void setAltNumber(int altNumber) {
    }

    @Override
    public void setParent(RuleContext parent) {
        this.parent = parent;
    }

    @Override
    public ParseTree getChild(int i) {
        return null;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
        return visitor.visitChildren(this);
    }

    @Override
    public String toStringTree(Parser recog) {
        return Trees.toStringTree((Tree)this, recog);
    }

    public String toStringTree(List<String> ruleNames) {
        return Trees.toStringTree((Tree)this, ruleNames);
    }

    @Override
    public String toStringTree() {
        return this.toStringTree((List<String>)null);
    }

    public String toString() {
        return this.toString((List<String>)null, (RuleContext)null);
    }

    public final String toString(Recognizer<?, ?> recog) {
        return this.toString(recog, (RuleContext)ParserRuleContext.EMPTY);
    }

    public final String toString(List<String> ruleNames) {
        return this.toString(ruleNames, null);
    }

    public String toString(Recognizer<?, ?> recog, RuleContext stop) {
        String[] ruleNames = recog != null ? recog.getRuleNames() : null;
        List<String> ruleNamesList = ruleNames != null ? Arrays.asList(ruleNames) : null;
        return this.toString(ruleNamesList, stop);
    }

    public String toString(List<String> ruleNames, RuleContext stop) {
        StringBuilder buf = new StringBuilder();
        RuleContext p = this;
        buf.append("[");
        while (p != null && p != stop) {
            if (ruleNames == null) {
                if (!p.isEmpty()) {
                    buf.append(p.invokingState);
                }
            } else {
                int ruleIndex = p.getRuleIndex();
                String ruleName = ruleIndex >= 0 && ruleIndex < ruleNames.size() ? ruleNames.get(ruleIndex) : Integer.toString(ruleIndex);
                buf.append(ruleName);
            }
            if (!(p.parent == null || ruleNames == null && p.parent.isEmpty())) {
                buf.append(" ");
            }
            p = p.parent;
        }
        buf.append("]");
        return buf.toString();
    }
}

