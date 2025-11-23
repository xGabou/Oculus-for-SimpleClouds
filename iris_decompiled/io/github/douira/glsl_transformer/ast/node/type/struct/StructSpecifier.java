/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.type.struct;

import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.ArraySpecifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.TypeSpecifier;
import io.github.douira.glsl_transformer.ast.node.type.struct.StructBody;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class StructSpecifier
extends TypeSpecifier {
    protected Identifier name;
    protected StructBody structBody;

    public StructSpecifier(StructBody structBody) {
        this.structBody = this.setup(structBody, this::setStructBody);
    }

    public StructSpecifier(StructBody structBody, ArraySpecifier arraySpecifier) {
        super(arraySpecifier);
        this.structBody = this.setup(structBody, this::setStructBody);
    }

    public StructSpecifier(Identifier name, StructBody structBody) {
        this.name = this.setup(name, this::setName);
        this.structBody = this.setup(structBody, this::setStructBody);
    }

    public StructSpecifier(Identifier name, StructBody structBody, ArraySpecifier arraySpecifier) {
        super(arraySpecifier);
        this.name = this.setup(name, this::setName);
        this.structBody = this.setup(structBody, this::setStructBody);
    }

    public Identifier getName() {
        return this.name;
    }

    public void setName(Identifier name) {
        this.updateParents(this.name, name, this::setName);
        this.name = name;
    }

    public StructBody getStructBody() {
        return this.structBody;
    }

    public void setStructBody(StructBody structBody) {
        this.updateParents(this.structBody, structBody, this::setStructBody);
        this.structBody = structBody;
    }

    @Override
    public TypeSpecifier.SpecifierType getSpecifierType() {
        return TypeSpecifier.SpecifierType.STRUCT;
    }

    @Override
    public <R> R typeSpecifierAccept(ASTVisitor<R> visitor) {
        return visitor.visitStructSpecifier(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterStructSpecifier(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitStructSpecifier(this);
    }

    @Override
    public StructSpecifier clone() {
        return new StructSpecifier(StructSpecifier.clone(this.name), StructSpecifier.clone(this.structBody), StructSpecifier.clone(this.arraySpecifier));
    }

    @Override
    public StructSpecifier cloneInto(Root root) {
        return (StructSpecifier)super.cloneInto(root);
    }
}

