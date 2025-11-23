/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.tree;

import oculus.org.antlr.v4.runtime.Parser;
import oculus.org.antlr.v4.runtime.RuleContext;
import oculus.org.antlr.v4.runtime.Token;
import oculus.org.antlr.v4.runtime.misc.Interval;
import oculus.org.antlr.v4.runtime.tree.ParseTree;
import oculus.org.antlr.v4.runtime.tree.ParseTreeVisitor;
import oculus.org.antlr.v4.runtime.tree.TerminalNode;

public class TerminalNodeImpl
implements TerminalNode {
    public Token symbol;
    public ParseTree parent;

    public TerminalNodeImpl(Token symbol) {
        this.symbol = symbol;
    }

    @Override
    public ParseTree getChild(int i) {
        return null;
    }

    @Override
    public Token getSymbol() {
        return this.symbol;
    }

    @Override
    public ParseTree getParent() {
        return this.parent;
    }

    @Override
    public void setParent(RuleContext parent) {
        this.parent = parent;
    }

    @Override
    public Token getPayload() {
        return this.symbol;
    }

    @Override
    public Interval getSourceInterval() {
        if (this.symbol == null) {
            return Interval.INVALID;
        }
        int tokenIndex = this.symbol.getTokenIndex();
        return new Interval(tokenIndex, tokenIndex);
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
        return visitor.visitTerminal(this);
    }

    @Override
    public String getText() {
        return this.symbol.getText();
    }

    @Override
    public String toStringTree(Parser parser) {
        return this.toString();
    }

    public String toString() {
        if (this.symbol.getType() == -1) {
            return "<EOF>";
        }
        return this.symbol.getText();
    }

    @Override
    public String toStringTree() {
        return this.toString();
    }
}

