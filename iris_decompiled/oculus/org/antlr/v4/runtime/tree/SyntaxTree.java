/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.tree;

import oculus.org.antlr.v4.runtime.misc.Interval;
import oculus.org.antlr.v4.runtime.tree.Tree;

public interface SyntaxTree
extends Tree {
    public Interval getSourceInterval();
}

