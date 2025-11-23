/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.external_declaration;

import io.github.douira.glsl_transformer.ast.node.external_declaration.ExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.statement.CompoundStatement;
import io.github.douira.glsl_transformer.ast.node.type.specifier.FunctionPrototype;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class FunctionDefinition
extends ExternalDeclaration {
    protected FunctionPrototype functionPrototype;
    protected CompoundStatement body;

    public FunctionDefinition(FunctionPrototype functionPrototype, CompoundStatement body) {
        this.functionPrototype = this.setup(functionPrototype, this::setFunctionPrototype);
        this.body = this.setup(body, this::setBody);
    }

    public FunctionPrototype getFunctionPrototype() {
        return this.functionPrototype;
    }

    public void setFunctionPrototype(FunctionPrototype functionPrototype) {
        this.updateParents(this.functionPrototype, functionPrototype, this::setFunctionPrototype);
        this.functionPrototype = functionPrototype;
    }

    public CompoundStatement getBody() {
        return this.body;
    }

    public void setBody(CompoundStatement body) {
        this.updateParents(this.body, body, this::setBody);
        this.body = body;
    }

    @Override
    public ExternalDeclaration.ExternalDeclarationType getExternalDeclarationType() {
        return ExternalDeclaration.ExternalDeclarationType.FUNCTION_DEFINITION;
    }

    @Override
    public <R> R externalDeclarationAccept(ASTVisitor<R> visitor) {
        return visitor.visitFunctionDefinition(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterFunctionDefinition(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitFunctionDefinition(this);
    }

    @Override
    public FunctionDefinition clone() {
        return new FunctionDefinition(FunctionDefinition.clone(this.functionPrototype), FunctionDefinition.clone(this.body));
    }

    @Override
    public FunctionDefinition cloneInto(Root root) {
        return (FunctionDefinition)super.cloneInto(root);
    }
}

