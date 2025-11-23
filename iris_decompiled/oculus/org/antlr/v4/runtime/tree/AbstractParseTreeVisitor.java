/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.tree;

import oculus.org.antlr.v4.runtime.tree.ErrorNode;
import oculus.org.antlr.v4.runtime.tree.ParseTree;
import oculus.org.antlr.v4.runtime.tree.ParseTreeVisitor;
import oculus.org.antlr.v4.runtime.tree.RuleNode;
import oculus.org.antlr.v4.runtime.tree.TerminalNode;

public abstract class AbstractParseTreeVisitor<T>
implements ParseTreeVisitor<T> {
    @Override
    public T visit(ParseTree tree) {
        return tree.accept(this);
    }

    @Override
    public T visitChildren(RuleNode node) {
        T result = this.defaultResult();
        int n = node.getChildCount();
        for (int i = 0; i < n && this.shouldVisitNextChild(node, result); ++i) {
            ParseTree c = node.getChild(i);
            Object childResult = c.accept(this);
            result = this.aggregateResult(result, childResult);
        }
        return result;
    }

    @Override
    public T visitTerminal(TerminalNode node) {
        return this.defaultResult();
    }

    @Override
    public T visitErrorNode(ErrorNode node) {
        return this.defaultResult();
    }

    protected T defaultResult() {
        return null;
    }

    protected T aggregateResult(T aggregate, T nextResult) {
        return nextResult;
    }

    protected boolean shouldVisitNextChild(RuleNode node, T currentResult) {
        return true;
    }
}

