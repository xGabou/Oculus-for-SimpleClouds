/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node;

import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.abstract_node.InnerASTNode;
import io.github.douira.glsl_transformer.ast.node.type.FullySpecifiedType;
import io.github.douira.glsl_transformer.ast.node.type.initializer.Initializer;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class IterationConditionInitializer
extends InnerASTNode {
    protected FullySpecifiedType type;
    protected Identifier name;
    protected Initializer initializer;

    public IterationConditionInitializer(FullySpecifiedType type, Identifier name, Initializer initializer) {
        this.type = this.setup(type, this::setType);
        this.name = this.setup(name, this::setName);
        this.initializer = this.setup(initializer, this::setInitializer);
    }

    public FullySpecifiedType getType() {
        return this.type;
    }

    public void setType(FullySpecifiedType type) {
        this.updateParents(this.type, type, this::setType);
        this.type = type;
    }

    public Identifier getName() {
        return this.name;
    }

    public void setName(Identifier name) {
        this.updateParents(this.name, name, this::setName);
        this.name = name;
    }

    public Initializer getInitializer() {
        return this.initializer;
    }

    public void setInitializer(Initializer initializer) {
        this.updateParents(this.initializer, initializer, this::setInitializer);
        this.initializer = initializer;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitIterationConditionInitializer(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        listener.enterIterationConditionInitializer(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        listener.exitIterationConditionInitializer(this);
    }

    @Override
    public IterationConditionInitializer clone() {
        return new IterationConditionInitializer(IterationConditionInitializer.clone(this.type), IterationConditionInitializer.clone(this.name), IterationConditionInitializer.clone(this.initializer));
    }

    @Override
    public IterationConditionInitializer cloneInto(Root root) {
        return (IterationConditionInitializer)super.cloneInto(root);
    }
}

