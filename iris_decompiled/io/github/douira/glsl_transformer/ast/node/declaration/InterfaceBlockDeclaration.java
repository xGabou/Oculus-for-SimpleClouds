/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.declaration;

import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.declaration.Declaration;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.TypeQualifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.ArraySpecifier;
import io.github.douira.glsl_transformer.ast.node.type.struct.StructBody;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class InterfaceBlockDeclaration
extends Declaration {
    protected TypeQualifier typeQualifier;
    protected Identifier blockName;
    protected StructBody structBody;
    protected Identifier variableName;
    protected ArraySpecifier arraySpecifier;

    public InterfaceBlockDeclaration(TypeQualifier typeQualifier, Identifier blockName, StructBody structBody, Identifier variableName, ArraySpecifier arraySpecifier) {
        this(typeQualifier, blockName, structBody, variableName);
        this.arraySpecifier = this.setup(arraySpecifier, this::setArraySpecifier);
    }

    public InterfaceBlockDeclaration(TypeQualifier typeQualifier, Identifier blockName, StructBody structBody, Identifier variableName) {
        this(typeQualifier, blockName, structBody);
        this.variableName = this.setup(variableName, this::setVariableName);
    }

    public InterfaceBlockDeclaration(TypeQualifier typeQualifier, Identifier blockName, StructBody structBody) {
        this.typeQualifier = this.setup(typeQualifier, this::setTypeQualifier);
        this.blockName = this.setup(blockName, this::setBlockName);
        this.structBody = this.setup(structBody, this::setStructBody);
    }

    public TypeQualifier getTypeQualifier() {
        return this.typeQualifier;
    }

    public void setTypeQualifier(TypeQualifier typeQualifier) {
        this.updateParents(this.typeQualifier, typeQualifier, this::setTypeQualifier);
        this.typeQualifier = typeQualifier;
    }

    public Identifier getBlockName() {
        return this.blockName;
    }

    public void setBlockName(Identifier blockName) {
        this.updateParents(this.blockName, blockName, this::setBlockName);
        this.blockName = blockName;
    }

    public StructBody getStructBody() {
        return this.structBody;
    }

    public void setStructBody(StructBody structBody) {
        this.updateParents(this.structBody, structBody, this::setStructBody);
        this.structBody = structBody;
    }

    public Identifier getVariableName() {
        return this.variableName;
    }

    public void setVariableName(Identifier variableName) {
        this.updateParents(this.variableName, variableName, this::setVariableName);
        this.variableName = variableName;
    }

    public ArraySpecifier getArraySpecifier() {
        return this.arraySpecifier;
    }

    public void setArraySpecifier(ArraySpecifier arraySpecifier) {
        this.updateParents(this.arraySpecifier, arraySpecifier, this::setArraySpecifier);
        this.arraySpecifier = arraySpecifier;
    }

    @Override
    public Declaration.DeclarationType getDeclarationType() {
        return Declaration.DeclarationType.INTERFACE_BLOCK;
    }

    @Override
    public <R> R declarationAccept(ASTVisitor<R> visitor) {
        return visitor.visitInterfaceBlockDeclaration(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterInterfaceBlockDeclaration(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitInterfaceBlockDeclaration(this);
    }

    @Override
    public InterfaceBlockDeclaration clone() {
        return new InterfaceBlockDeclaration(InterfaceBlockDeclaration.clone(this.typeQualifier), InterfaceBlockDeclaration.clone(this.blockName), InterfaceBlockDeclaration.clone(this.structBody), InterfaceBlockDeclaration.clone(this.variableName), InterfaceBlockDeclaration.clone(this.arraySpecifier));
    }

    @Override
    public InterfaceBlockDeclaration cloneInto(Root root) {
        return (InterfaceBlockDeclaration)super.cloneInto(root);
    }
}

