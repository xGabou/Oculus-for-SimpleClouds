/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.tree;

import oculus.org.antlr.v4.runtime.Token;
import oculus.org.antlr.v4.runtime.tree.ParseTree;

public interface TerminalNode
extends ParseTree {
    public Token getSymbol();
}

