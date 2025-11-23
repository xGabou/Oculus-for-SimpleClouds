/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.declaration;

import io.github.douira.glsl_transformer.ast.node.declaration.Declaration;
import io.github.douira.glsl_transformer.ast.node.type.specifier.FunctionPrototype;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class FunctionDeclaration
extends Declaration {
    protected FunctionPrototype functionPrototype;

    public FunctionDeclaration(FunctionPrototype functionPrototype) {
        this.functionPrototype = this.setup(functionPrototype, this::setFunctionPrototype);
    }

    public FunctionPrototype getFunctionPrototype() {
        return this.functionPrototype;
    }

    public void setFunctionPrototype(FunctionPrototype functionPrototype) {
        this.updateParents(this.functionPrototype, functionPrototype, this::setFunctionPrototype);
        this.functionPrototype = functionPrototype;
    }

    @Override
    public Declaration.DeclarationType getDeclarationType() {
        return Declaration.DeclarationType.FUNCTION;
    }

    @Override
    public <R> R declarationAccept(ASTVisitor<R> visitor) {
        return visitor.visitFunctionDeclaration(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterFunctionDeclaration(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitFunctionDeclaration(this);
    }

    @Override
    public FunctionDeclaration clone() {
        return new FunctionDeclaration(FunctionDeclaration.clone(this.functionPrototype));
    }

    @Override
    public FunctionDeclaration cloneInto(Root root) {
        return (FunctionDeclaration)super.cloneInto(root);
    }
}

