/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.external_declaration;

import io.github.douira.glsl_transformer.ast.node.external_declaration.ExternalDeclaration;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class CustomDirective
extends ExternalDeclaration {
    private String content;

    public CustomDirective(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.getRoot().unregisterFastRename(this);
        this.content = content;
        this.getRoot().registerFastRename(this);
    }

    @Override
    public ExternalDeclaration.ExternalDeclarationType getExternalDeclarationType() {
        return ExternalDeclaration.ExternalDeclarationType.CUSTOM_DIRECTIVE;
    }

    @Override
    public <R> R externalDeclarationAccept(ASTVisitor<R> visitor) {
        return visitor.visitCustomDirective(this);
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
    public CustomDirective clone() {
        return new CustomDirective(this.content);
    }

    @Override
    public CustomDirective cloneInto(Root root) {
        return (CustomDirective)super.cloneInto(root);
    }
}

