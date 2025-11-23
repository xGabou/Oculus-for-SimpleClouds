/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.external_declaration;

import io.github.douira.glsl_transformer.ast.data.TokenTyped;
import io.github.douira.glsl_transformer.ast.data.TypeUtil;
import io.github.douira.glsl_transformer.ast.node.external_declaration.ExternalDeclaration;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;
import oculus.org.antlr.v4.runtime.Token;

public class ExtensionDirective
extends ExternalDeclaration {
    private String name;
    public ExtensionBehavior behavior;

    public ExtensionDirective(String name, ExtensionBehavior behavior) {
        this.name = name;
        this.behavior = behavior;
    }

    public ExtensionDirective(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.getRoot().unregisterFastRename(this);
        this.name = name;
        this.getRoot().registerFastRename(this);
    }

    @Override
    public ExternalDeclaration.ExternalDeclarationType getExternalDeclarationType() {
        return ExternalDeclaration.ExternalDeclarationType.EXTENSION_DIRECTIVE;
    }

    @Override
    public <R> R externalDeclarationAccept(ASTVisitor<R> visitor) {
        return visitor.visitExtensionDirective(this);
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
    public ExtensionDirective clone() {
        return new ExtensionDirective(this.name, this.behavior);
    }

    @Override
    public ExtensionDirective cloneInto(Root root) {
        return (ExtensionDirective)super.cloneInto(root);
    }

    public static enum ExtensionBehavior implements TokenTyped
    {
        DEBUG(283),
        ENABLE(284),
        WARN(285),
        DISABLE(286);

        public final int tokenType;

        private ExtensionBehavior(int tokenType) {
            this.tokenType = tokenType;
        }

        @Override
        public int getTokenType() {
            return this.tokenType;
        }

        public static ExtensionBehavior fromToken(Token token) {
            return (ExtensionBehavior)TypeUtil.enumFromToken((TokenTyped[])ExtensionBehavior.values(), (Token)token);
        }
    }
}

