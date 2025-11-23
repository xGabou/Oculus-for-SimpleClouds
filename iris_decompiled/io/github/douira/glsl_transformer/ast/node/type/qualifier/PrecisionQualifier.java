/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.type.qualifier;

import io.github.douira.glsl_transformer.ast.data.TokenTyped;
import io.github.douira.glsl_transformer.ast.data.TypeUtil;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.TypeQualifierPart;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;
import oculus.org.antlr.v4.runtime.Token;

public class PrecisionQualifier
extends TypeQualifierPart {
    public PrecisionLevel precisionLevel;

    public PrecisionQualifier(PrecisionLevel storageType) {
        this.precisionLevel = storageType;
    }

    @Override
    public TypeQualifierPart.QualifierType getQualifierType() {
        return TypeQualifierPart.QualifierType.PRECISION;
    }

    @Override
    public <R> R typeQualifierPartAccept(ASTVisitor<R> visitor) {
        return visitor.visitPrecisionQualifier(this);
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
    public PrecisionQualifier clone() {
        return new PrecisionQualifier(this.precisionLevel);
    }

    @Override
    public PrecisionQualifier cloneInto(Root root) {
        return (PrecisionQualifier)super.cloneInto(root);
    }

    public static enum PrecisionLevel implements TokenTyped
    {
        HIGH(7),
        MEDIUM(8),
        LOW(9);

        public final int tokenType;

        private PrecisionLevel(int tokenType) {
            this.tokenType = tokenType;
        }

        @Override
        public int getTokenType() {
            return this.tokenType;
        }

        public static PrecisionLevel fromToken(Token token) {
            return (PrecisionLevel)TypeUtil.enumFromToken((TokenTyped[])PrecisionLevel.values(), (Token)token);
        }
    }
}

