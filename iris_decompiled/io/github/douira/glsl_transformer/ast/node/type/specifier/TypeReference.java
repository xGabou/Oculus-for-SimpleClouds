/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.type.specifier;

import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.ArraySpecifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.TypeSpecifier;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class TypeReference
extends TypeSpecifier {
    protected Identifier reference;

    public TypeReference(Identifier reference) {
        this.reference = this.setup(reference, this::setReference);
    }

    public TypeReference(Identifier reference, ArraySpecifier arraySpecifier) {
        super(arraySpecifier);
        this.reference = this.setup(reference, this::setReference);
    }

    public Identifier getReference() {
        return this.reference;
    }

    public void setReference(Identifier reference) {
        this.updateParents(this.reference, reference, this::setReference);
        this.reference = reference;
    }

    @Override
    public TypeSpecifier.SpecifierType getSpecifierType() {
        return TypeSpecifier.SpecifierType.REFERENCE;
    }

    @Override
    public <R> R typeSpecifierAccept(ASTVisitor<R> visitor) {
        return visitor.visitTypeReference(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterTypeReference(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitTypeReference(this);
    }

    @Override
    public TypeReference clone() {
        return new TypeReference(TypeReference.clone(this.reference), TypeReference.clone(this.arraySpecifier));
    }

    @Override
    public TypeReference cloneInto(Root root) {
        return (TypeReference)super.cloneInto(root);
    }
}

