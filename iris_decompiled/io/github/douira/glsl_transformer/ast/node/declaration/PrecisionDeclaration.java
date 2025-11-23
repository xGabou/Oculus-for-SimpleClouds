/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.declaration;

import io.github.douira.glsl_transformer.ast.node.declaration.Declaration;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.PrecisionQualifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.TypeSpecifier;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class PrecisionDeclaration
extends Declaration {
    protected PrecisionQualifier precisionQualifier;
    protected TypeSpecifier typeSpecifier;

    public PrecisionDeclaration(PrecisionQualifier precisionQualifier, TypeSpecifier typeSpecifier) {
        this.precisionQualifier = this.setup(precisionQualifier, this::setPrecisionQualifier);
        this.typeSpecifier = this.setup(typeSpecifier, this::setTypeSpecifier);
    }

    public PrecisionQualifier getPrecisionQualifier() {
        return this.precisionQualifier;
    }

    public void setPrecisionQualifier(PrecisionQualifier precisionQualifier) {
        this.updateParents(this.precisionQualifier, precisionQualifier, this::setPrecisionQualifier);
        this.precisionQualifier = precisionQualifier;
    }

    public TypeSpecifier getTypeSpecifier() {
        return this.typeSpecifier;
    }

    public void setTypeSpecifier(TypeSpecifier typeSpecifier) {
        this.updateParents(this.typeSpecifier, typeSpecifier, this::setTypeSpecifier);
        this.typeSpecifier = typeSpecifier;
    }

    @Override
    public Declaration.DeclarationType getDeclarationType() {
        return Declaration.DeclarationType.PRECISION;
    }

    @Override
    public <R> R declarationAccept(ASTVisitor<R> visitor) {
        return visitor.visitPrecisionDeclaration(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterPrecisionDeclaration(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitPrecisionDeclaration(this);
    }

    @Override
    public PrecisionDeclaration clone() {
        return new PrecisionDeclaration(PrecisionDeclaration.clone(this.precisionQualifier), PrecisionDeclaration.clone(this.typeSpecifier));
    }

    @Override
    public PrecisionDeclaration cloneInto(Root root) {
        return (PrecisionDeclaration)super.cloneInto(root);
    }
}

