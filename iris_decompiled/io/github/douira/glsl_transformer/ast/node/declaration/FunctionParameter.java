/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.declaration;

import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.abstract_node.InnerASTNode;
import io.github.douira.glsl_transformer.ast.node.type.FullySpecifiedType;
import io.github.douira.glsl_transformer.ast.node.type.specifier.ArraySpecifier;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class FunctionParameter
extends InnerASTNode {
    protected FullySpecifiedType type;
    protected Identifier name;
    protected ArraySpecifier arraySpecifier;

    public FunctionParameter(FullySpecifiedType type, Identifier name, ArraySpecifier arraySpecifier) {
        this.type = this.setup(type, this::setType);
        this.name = this.setup(name, this::setName);
        this.arraySpecifier = this.setup(arraySpecifier, this::setArraySpecifier);
    }

    public FunctionParameter(FullySpecifiedType type, Identifier name) {
        this.type = this.setup(type, this::setType);
        this.name = this.setup(name, this::setName);
    }

    public FunctionParameter(FullySpecifiedType type) {
        this.type = this.setup(type, this::setType);
    }

    public FullySpecifiedType getType() {
        return this.type;
    }

    public void setType(FullySpecifiedType type) {
        this.updateParents(this.type, type, this::setType);
        this.type = type;
    }

    public Identifier getName() {
        return this.name;
    }

    public void setName(Identifier name) {
        this.updateParents(this.name, name, this::setName);
        this.name = name;
    }

    public ArraySpecifier getArraySpecifier() {
        return this.arraySpecifier;
    }

    public void setArraySpecifier(ArraySpecifier arraySpecifier) {
        this.updateParents(this.arraySpecifier, arraySpecifier, this::setArraySpecifier);
        this.arraySpecifier = arraySpecifier;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitFunctionParameter(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        listener.enterFunctionParameter(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        listener.exitFunctionParameter(this);
    }

    @Override
    public FunctionParameter clone() {
        return new FunctionParameter(FunctionParameter.clone(this.type), FunctionParameter.clone(this.name), FunctionParameter.clone(this.arraySpecifier));
    }

    @Override
    public FunctionParameter cloneInto(Root root) {
        return (FunctionParameter)super.cloneInto(root);
    }
}

