/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.type.qualifier;

import io.github.douira.glsl_transformer.ast.node.abstract_node.InnerASTNode;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public abstract class TypeQualifierPart
extends InnerASTNode {
    public abstract QualifierType getQualifierType();

    public abstract <R> R typeQualifierPartAccept(ASTVisitor<R> var1);

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.aggregateResult(visitor.visitTypeQualifierPart(this), this.typeQualifierPartAccept(visitor));
    }

    @Override
    public void enterNode(ASTListener listener) {
        listener.enterTypeQualifierPart(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        listener.exitTypeQualifierPart(this);
    }

    @Override
    public abstract TypeQualifierPart clone();

    @Override
    public TypeQualifierPart cloneInto(Root root) {
        return (TypeQualifierPart)super.cloneInto(root);
    }

    public static enum QualifierType {
        STORAGE,
        LAYOUT,
        PRECISION,
        INTERPOLATION,
        INVARIANT,
        PRECISE;

    }
}

