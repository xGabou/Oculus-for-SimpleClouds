/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.tree;

import oculus.org.antlr.v4.runtime.Token;
import oculus.org.antlr.v4.runtime.tree.ErrorNode;
import oculus.org.antlr.v4.runtime.tree.ParseTreeVisitor;
import oculus.org.antlr.v4.runtime.tree.TerminalNodeImpl;

public class ErrorNodeImpl
extends TerminalNodeImpl
implements ErrorNode {
    public ErrorNodeImpl(Token token) {
        super(token);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
        return visitor.visitErrorNode(this);
    }
}

