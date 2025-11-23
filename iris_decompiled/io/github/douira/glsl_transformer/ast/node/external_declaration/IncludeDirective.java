/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.external_declaration;

import io.github.douira.glsl_transformer.ast.node.external_declaration.ExternalDeclaration;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class IncludeDirective
extends ExternalDeclaration {
    private String content;
    public boolean isAngleBrackets;

    public IncludeDirective(String content, boolean isAngleBrackets) {
        this.content = content;
        this.isAngleBrackets = isAngleBrackets;
    }

    public IncludeDirective(String content) {
        this(content, false);
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
        return ExternalDeclaration.ExternalDeclarationType.INCLUDE_DIRECTIVE;
    }

    @Override
    public <R> R externalDeclarationAccept(ASTVisitor<R> visitor) {
        return visitor.visitIncludeDirective(this);
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
    public IncludeDirective clone() {
        return new IncludeDirective(this.content, this.isAngleBrackets);
    }

    @Override
    public IncludeDirective cloneInto(Root root) {
        return (IncludeDirective)super.cloneInto(root);
    }
}

