/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.external_declaration;

import io.github.douira.glsl_transformer.ast.data.TokenTyped;
import io.github.douira.glsl_transformer.ast.data.TypeUtil;
import io.github.douira.glsl_transformer.ast.node.external_declaration.ExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.LayoutQualifier;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;
import oculus.org.antlr.v4.runtime.Token;

public class LayoutDefaults
extends ExternalDeclaration {
    protected LayoutQualifier qualifier;
    public LayoutMode mode;

    public LayoutDefaults(LayoutQualifier qualifier, LayoutMode mode) {
        this.qualifier = this.setup(qualifier, this::setQualifier);
        this.mode = mode;
    }

    public LayoutQualifier getQualifier() {
        return this.qualifier;
    }

    public void setQualifier(LayoutQualifier qualifier) {
        this.updateParents(this.qualifier, qualifier, this::setQualifier);
        this.qualifier = qualifier;
    }

    @Override
    public ExternalDeclaration.ExternalDeclarationType getExternalDeclarationType() {
        return ExternalDeclaration.ExternalDeclarationType.LAYOUT_DEFAULTS;
    }

    @Override
    public <R> R externalDeclarationAccept(ASTVisitor<R> visitor) {
        return visitor.visitLayoutDefaults(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterLayoutDefaults(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitLayoutDefaults(this);
    }

    @Override
    public LayoutDefaults clone() {
        return new LayoutDefaults(LayoutDefaults.clone(this.qualifier), this.mode);
    }

    @Override
    public LayoutDefaults cloneInto(Root root) {
        return (LayoutDefaults)super.cloneInto(root);
    }

    public static enum LayoutMode implements TokenTyped
    {
        UNIFORM(2),
        IN(4),
        OUT(5),
        BUFFER(3);

        public final int tokenType;

        private LayoutMode(int tokenType) {
            this.tokenType = tokenType;
        }

        @Override
        public int getTokenType() {
            return this.tokenType;
        }

        public static LayoutMode fromToken(Token token) {
            return (LayoutMode)TypeUtil.enumFromToken((TokenTyped[])LayoutMode.values(), (Token)token);
        }
    }
}

