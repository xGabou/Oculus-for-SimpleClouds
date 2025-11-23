/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.type.initializer;

import io.github.douira.glsl_transformer.ast.node.abstract_node.InnerASTNode;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public abstract class Initializer
extends InnerASTNode {
    public abstract InitializerType getInitializerType();

    public abstract <R> R initializerAccept(ASTVisitor<R> var1);

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.aggregateResult(visitor.visitInitializer(this), this.initializerAccept(visitor));
    }

    @Override
    public void enterNode(ASTListener listener) {
        listener.enterInitializer(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        listener.exitInitializer(this);
    }

    @Override
    public abstract Initializer clone();

    @Override
    public Initializer cloneInto(Root root) {
        return (Initializer)super.cloneInto(root);
    }

    public static enum InitializerType {
        EXPRESSION,
        NESTED;

    }
}

