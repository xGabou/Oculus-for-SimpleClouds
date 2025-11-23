/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.type.qualifier;

import io.github.douira.glsl_transformer.ast.node.type.qualifier.TypeQualifierPart;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class InvariantQualifier
extends TypeQualifierPart {
    @Override
    public TypeQualifierPart.QualifierType getQualifierType() {
        return TypeQualifierPart.QualifierType.INVARIANT;
    }

    @Override
    public <R> R typeQualifierPartAccept(ASTVisitor<R> visitor) {
        return visitor.visitInvariantQualifier(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
    }

    @Override
    public InvariantQualifier clone() {
        return new InvariantQualifier();
    }

    @Override
    public InvariantQualifier cloneInto(Root root) {
        return (InvariantQualifier)super.cloneInto(root);
    }
}

