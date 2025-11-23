/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.tree;

import oculus.org.antlr.v4.runtime.tree.ErrorNode;
import oculus.org.antlr.v4.runtime.tree.ParseTree;
import oculus.org.antlr.v4.runtime.tree.RuleNode;
import oculus.org.antlr.v4.runtime.tree.TerminalNode;

public interface ParseTreeVisitor<T> {
    public T visit(ParseTree var1);

    public T visitChildren(RuleNode var1);

    public T visitTerminal(TerminalNode var1);

    public T visitErrorNode(ErrorNode var1);
}

