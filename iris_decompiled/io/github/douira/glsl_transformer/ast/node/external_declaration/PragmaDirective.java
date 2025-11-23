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

public class PragmaDirective
extends ExternalDeclaration {
    public boolean stdGL;
    public PragmaType type;
    private String customName;
    public PragmaState state;

    private PragmaDirective(boolean stdGL, PragmaType type, String customName, PragmaState state) {
        this.stdGL = stdGL;
        this.type = type;
        this.customName = customName;
        this.state = state;
    }

    public PragmaDirective(boolean stdGL, String customPragmaName) {
        this.stdGL = stdGL;
        this.type = PragmaType.CUSTOM;
        this.customName = customPragmaName;
    }

    public PragmaDirective(boolean stdGL, PragmaType type, PragmaState state) {
        this.stdGL = stdGL;
        this.type = type;
        this.state = state;
    }

    public String getCustomName() {
        return this.customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    @Override
    public ExternalDeclaration.ExternalDeclarationType getExternalDeclarationType() {
        return ExternalDeclaration.ExternalDeclarationType.PRAGMA_DIRECTIVE;
    }

    @Override
    public <R> R externalDeclarationAccept(ASTVisitor<R> visitor) {
        return visitor.visitPragmaDirective(this);
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
    public PragmaDirective clone() {
        return new PragmaDirective(this.stdGL, this.type, this.customName, this.state);
    }

    @Override
    public PragmaDirective cloneInto(Root root) {
        return (PragmaDirective)super.cloneInto(root);
    }

    public static enum PragmaType implements TokenTyped
    {
        DEBUG(277),
        OPTIMIZE(278),
        INVARIANT(279),
        CUSTOM(314);

        public final int tokenType;

        private PragmaType(int tokenType) {
            this.tokenType = tokenType;
        }

        @Override
        public int getTokenType() {
            return this.tokenType;
        }

        public static PragmaType fromToken(Token token) {
            return (PragmaType)TypeUtil.enumFromToken((TokenTyped[])PragmaType.values(), (Token)token);
        }
    }

    public static enum PragmaState implements TokenTyped
    {
        ON(280),
        OFF(281),
        ALL(282);

        public final int tokenType;

        private PragmaState(int tokenType) {
            this.tokenType = tokenType;
        }

        @Override
        public int getTokenType() {
            return this.tokenType;
        }

        public static PragmaState fromToken(Token token) {
            return (PragmaState)TypeUtil.enumFromToken((TokenTyped[])PragmaState.values(), (Token)token);
        }
    }
}

