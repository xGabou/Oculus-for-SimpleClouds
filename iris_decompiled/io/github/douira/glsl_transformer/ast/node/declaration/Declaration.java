/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.declaration;

import io.github.douira.glsl_transformer.ast.node.abstract_node.InnerASTNode;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public abstract class Declaration
extends InnerASTNode {
    public abstract DeclarationType getDeclarationType();

    public abstract <R> R declarationAccept(ASTVisitor<R> var1);

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.aggregateResult(visitor.visitDeclaration(this), this.declarationAccept(visitor));
    }

    @Override
    public void enterNode(ASTListener listener) {
        listener.enterDeclaration(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        listener.exitDeclaration(this);
    }

    @Override
    public abstract Declaration clone();

    @Override
    public Declaration cloneInto(Root root) {
        return (Declaration)super.cloneInto(root);
    }

    public static enum DeclarationType {
        FUNCTION,
        TYPE_AND_INIT,
        PRECISION,
        INTERFACE_BLOCK,
        VARIABLE;

    }
}

