/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.declaration;

import io.github.douira.glsl_transformer.ast.data.ChildNodeList;
import io.github.douira.glsl_transformer.ast.node.declaration.Declaration;
import io.github.douira.glsl_transformer.ast.node.declaration.DeclarationMember;
import io.github.douira.glsl_transformer.ast.node.type.FullySpecifiedType;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;
import java.util.stream.Stream;

public class TypeAndInitDeclaration
extends Declaration {
    protected FullySpecifiedType type;
    protected ChildNodeList<DeclarationMember> members;

    public TypeAndInitDeclaration(FullySpecifiedType type, Stream<DeclarationMember> members) {
        this.type = this.setup(type, this::setType);
        this.members = ChildNodeList.collect(members, this);
    }

    public TypeAndInitDeclaration(FullySpecifiedType type) {
        this.type = this.setup(type, this::setType);
        this.members = new ChildNodeList(this);
    }

    public FullySpecifiedType getType() {
        return this.type;
    }

    public void setType(FullySpecifiedType type) {
        this.updateParents(this.type, type, this::setType);
        this.type = type;
    }

    public ChildNodeList<DeclarationMember> getMembers() {
        return this.members;
    }

    @Override
    public Declaration.DeclarationType getDeclarationType() {
        return Declaration.DeclarationType.TYPE_AND_INIT;
    }

    @Override
    public <R> R declarationAccept(ASTVisitor<R> visitor) {
        return visitor.visitTypeAndInitDeclaration(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterTypeAndInitDeclaration(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitTypeAndInitDeclaration(this);
    }

    @Override
    public TypeAndInitDeclaration clone() {
        return new TypeAndInitDeclaration(TypeAndInitDeclaration.clone(this.type), TypeAndInitDeclaration.clone(this.members));
    }

    @Override
    public TypeAndInitDeclaration cloneInto(Root root) {
        return (TypeAndInitDeclaration)super.cloneInto(root);
    }
}

