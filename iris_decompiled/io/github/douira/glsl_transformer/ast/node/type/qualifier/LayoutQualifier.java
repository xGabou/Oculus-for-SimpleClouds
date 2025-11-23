/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.type.qualifier;

import io.github.douira.glsl_transformer.ast.data.ChildNodeList;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.LayoutQualifierPart;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.TypeQualifierPart;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;
import java.util.stream.Stream;

public class LayoutQualifier
extends TypeQualifierPart {
    protected ChildNodeList<LayoutQualifierPart> parts;

    public LayoutQualifier(Stream<LayoutQualifierPart> parts) {
        this.parts = ChildNodeList.collect(parts, this);
    }

    public ChildNodeList<LayoutQualifierPart> getParts() {
        return this.parts;
    }

    @Override
    public TypeQualifierPart.QualifierType getQualifierType() {
        return TypeQualifierPart.QualifierType.LAYOUT;
    }

    @Override
    public <R> R typeQualifierPartAccept(ASTVisitor<R> visitor) {
        return visitor.visitLayoutQualifier(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterLayoutQualifier(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitLayoutQualifier(this);
    }

    @Override
    public LayoutQualifier clone() {
        return new LayoutQualifier(LayoutQualifier.clone(this.parts));
    }

    @Override
    public LayoutQualifier cloneInto(Root root) {
        return (LayoutQualifier)super.cloneInto(root);
    }
}

