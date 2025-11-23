/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.traversal;

import io.github.douira.glsl_transformer.ast.node.abstract_node.InnerASTNode;
import io.github.douira.glsl_transformer.ast.traversal.ContextTracker;

public interface GeneralASTListener
extends ContextTracker {
    default public void enterEveryNode(InnerASTNode node) {
    }

    default public void exitEveryNode(InnerASTNode node) {
    }

    default public void afterEnterEveryNode(InnerASTNode node) {
    }

    default public void beforeExitEveryNode(InnerASTNode node) {
    }
}

