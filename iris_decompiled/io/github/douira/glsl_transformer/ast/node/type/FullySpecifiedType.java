/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.type;

import io.github.douira.glsl_transformer.ast.node.abstract_node.InnerASTNode;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.TypeQualifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.TypeSpecifier;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class FullySpecifiedType
extends InnerASTNode {
    protected TypeQualifier typeQualifier;
    protected TypeSpecifier typeSpecifier;

    public FullySpecifiedType(TypeQualifier typeQualifier, TypeSpecifier typeSpecifier) {
        this.typeQualifier = this.setup(typeQualifier, this::setTypeQualifier);
        this.typeSpecifier = this.setup(typeSpecifier, this::setTypeSpecifier);
    }

    public FullySpecifiedType(TypeSpecifier typeSpecifier) {
        this.typeSpecifier = this.setup(typeSpecifier, this::setTypeSpecifier);
    }

    public TypeQualifier getTypeQualifier() {
        return this.typeQualifier;
    }

    public void setTypeQualifier(TypeQualifier typeQualifier) {
        this.updateParents(this.typeQualifier, typeQualifier, this::setTypeQualifier);
        this.typeQualifier = typeQualifier;
    }

    public TypeSpecifier getTypeSpecifier() {
        return this.typeSpecifier;
    }

    public void setTypeSpecifier(TypeSpecifier typeSpecifier) {
        this.updateParents(this.typeSpecifier, typeSpecifier, this::setTypeSpecifier);
        this.typeSpecifier = typeSpecifier;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitFullySpecifiedType(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        listener.enterFullySpecifiedType(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        listener.exitFullySpecifiedType(this);
    }

    @Override
    public FullySpecifiedType clone() {
        return new FullySpecifiedType(FullySpecifiedType.clone(this.typeQualifier), FullySpecifiedType.clone(this.typeSpecifier));
    }

    @Override
    public FullySpecifiedType cloneInto(Root root) {
        return (FullySpecifiedType)super.cloneInto(root);
    }
}

