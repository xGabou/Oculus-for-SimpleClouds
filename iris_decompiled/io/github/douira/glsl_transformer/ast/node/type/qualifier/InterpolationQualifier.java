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

public class InterpolationQualifier
extends TypeQualifierPart {
    public InterpolationType interpolationType;

    public InterpolationQualifier(InterpolationType interpolationType) {
        this.interpolationType = interpolationType;
    }

    @Override
    public TypeQualifierPart.QualifierType getQualifierType() {
        return TypeQualifierPart.QualifierType.INTERPOLATION;
    }

    @Override
    public <R> R typeQualifierPartAccept(ASTVisitor<R> visitor) {
        return visitor.visitInterpolationQualifier(this);
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
    public InterpolationQualifier clone() {
        return new InterpolationQualifier(this.interpolationType);
    }

    @Override
    public InterpolationQualifier cloneInto(Root root) {
        return (InterpolationQualifier)super.cloneInto(root);
    }

    public static enum InterpolationType implements TokenTyped
    {
        SMOOTH(14),
        FLAT(15),
        NOPERSPECTIVE(23);

        public final int tokenType;

        private InterpolationType(int tokenType) {
            this.tokenType = tokenType;
        }

        @Override
        public int getTokenType() {
            return this.tokenType;
        }

        public static InterpolationType fromToken(Token token) {
            return (InterpolationType)TypeUtil.enumFromToken((TokenTyped[])InterpolationType.values(), (Token)token);
        }
    }
}

