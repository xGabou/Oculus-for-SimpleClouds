/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.type.initializer;

import io.github.douira.glsl_transformer.ast.data.ChildNodeList;
import io.github.douira.glsl_transformer.ast.node.type.initializer.Initializer;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;
import java.util.stream.Stream;

public class NestedInitializer
extends Initializer {
    protected ChildNodeList<Initializer> initializers;

    public NestedInitializer(Stream<Initializer> initializers) {
        this.initializers = ChildNodeList.collect(initializers, this);
    }

    public NestedInitializer() {
        this.initializers = new ChildNodeList(this);
    }

    public ChildNodeList<Initializer> getInitializers() {
        return this.initializers;
    }

    @Override
    public Initializer.InitializerType getInitializerType() {
        return Initializer.InitializerType.NESTED;
    }

    @Override
    public <R> R initializerAccept(ASTVisitor<R> visitor) {
        return visitor.visitNestedInitializer(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterNestedInitializer(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitNestedInitializer(this);
    }

    @Override
    public NestedInitializer clone() {
        return new NestedInitializer(NestedInitializer.clone(this.initializers));
    }

    @Override
    public NestedInitializer cloneInto(Root root) {
        return (NestedInitializer)super.cloneInto(root);
    }
}

