/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.type.struct;

import io.github.douira.glsl_transformer.ast.data.ChildNodeList;
import io.github.douira.glsl_transformer.ast.node.abstract_node.InnerASTNode;
import io.github.douira.glsl_transformer.ast.node.type.FullySpecifiedType;
import io.github.douira.glsl_transformer.ast.node.type.struct.StructDeclarator;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;
import java.util.stream.Stream;

public class StructMember
extends InnerASTNode {
    protected FullySpecifiedType type;
    protected ChildNodeList<StructDeclarator> declarators;

    public StructMember(FullySpecifiedType type, Stream<StructDeclarator> declarators) {
        this.type = this.setup(type, this::setType);
        this.declarators = ChildNodeList.collect(declarators, this);
    }

    public FullySpecifiedType getType() {
        return this.type;
    }

    public void setType(FullySpecifiedType type) {
        this.updateParents(this.type, type, this::setType);
        this.type = type;
    }

    public ChildNodeList<StructDeclarator> getDeclarators() {
        return this.declarators;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitStructMember(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        listener.enterStructMember(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        listener.exitStructMember(this);
    }

    @Override
    public StructMember clone() {
        return new StructMember(StructMember.clone(this.type), StructMember.clone(this.declarators));
    }

    @Override
    public StructMember cloneInto(Root root) {
        return (StructMember)super.cloneInto(root);
    }
}

