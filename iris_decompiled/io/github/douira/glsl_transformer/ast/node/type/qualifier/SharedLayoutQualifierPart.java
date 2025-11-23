/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.type.qualifier;

import io.github.douira.glsl_transformer.ast.node.type.qualifier.LayoutQualifierPart;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class SharedLayoutQualifierPart
extends LayoutQualifierPart {
    @Override
    public LayoutQualifierPart.LayoutQualifierType getLayoutQualifierType() {
        return LayoutQualifierPart.LayoutQualifierType.SHARED;
    }

    @Override
    public <R> R layoutQualifierPartAccept(ASTVisitor<R> visitor) {
        return visitor.visitSharedLayoutQualifierPart(this);
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
    public SharedLayoutQualifierPart clone() {
        return new SharedLayoutQualifierPart();
    }

    @Override
    public SharedLayoutQualifierPart cloneInto(Root root) {
        return (SharedLayoutQualifierPart)super.cloneInto(root);
    }
}

