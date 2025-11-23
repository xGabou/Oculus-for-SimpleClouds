/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.type.qualifier;

import io.github.douira.glsl_transformer.ast.node.abstract_node.InnerASTNode;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public abstract class LayoutQualifierPart
extends InnerASTNode {
    public abstract LayoutQualifierType getLayoutQualifierType();

    public abstract <R> R layoutQualifierPartAccept(ASTVisitor<R> var1);

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.aggregateResult(visitor.visitLayoutQualifierPart(this), this.layoutQualifierPartAccept(visitor));
    }

    @Override
    public void enterNode(ASTListener listener) {
        listener.enterLayoutQualifierPart(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        listener.exitLayoutQualifierPart(this);
    }

    @Override
    public abstract LayoutQualifierPart clone();

    @Override
    public LayoutQualifierPart cloneInto(Root root) {
        return (LayoutQualifierPart)super.cloneInto(root);
    }

    public static enum LayoutQualifierType {
        NAMED,
        SHARED;

    }
}

