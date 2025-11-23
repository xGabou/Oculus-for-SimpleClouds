/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.type.specifier;

import io.github.douira.glsl_transformer.ast.node.abstract_node.InnerASTNode;
import io.github.douira.glsl_transformer.ast.node.type.specifier.ArraySpecifier;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public abstract class TypeSpecifier
extends InnerASTNode {
    protected ArraySpecifier arraySpecifier;

    public abstract SpecifierType getSpecifierType();

    public TypeSpecifier() {
    }

    public TypeSpecifier(ArraySpecifier arraySpecifier) {
        this.arraySpecifier = this.setup(arraySpecifier, this::setArraySpecifier);
    }

    public ArraySpecifier getArraySpecifier() {
        return this.arraySpecifier;
    }

    public void setArraySpecifier(ArraySpecifier arraySpecifier) {
        this.updateParents(this.arraySpecifier, arraySpecifier, this::setArraySpecifier);
        this.arraySpecifier = arraySpecifier;
    }

    public abstract <R> R typeSpecifierAccept(ASTVisitor<R> var1);

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.aggregateResult(visitor.visitTypeSpecifier(this), this.typeSpecifierAccept(visitor));
    }

    @Override
    public void enterNode(ASTListener listener) {
        listener.enterTypeSpecifier(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        listener.exitTypeSpecifier(this);
    }

    @Override
    public abstract TypeSpecifier clone();

    @Override
    public TypeSpecifier cloneInto(Root root) {
        return (TypeSpecifier)super.cloneInto(root);
    }

    public static enum SpecifierType {
        BUILTIN_NUMERIC,
        BULTIN_FIXED,
        STRUCT,
        REFERENCE;

    }
}

