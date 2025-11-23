/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.traversal;

import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ListNode;
import java.util.List;

public interface GeneralASTVisitor<R> {
    default public R startVisit(ASTNode node) {
        return this.visit(node);
    }

    public R visit(ASTNode var1);

    default public R visitData(Object data) {
        return this.defaultResult();
    }

    default public R visitData(R previousResult, Object data) {
        return this.aggregateResult(previousResult, this.visitData(data));
    }

    default public R visit(R previousResult, ASTNode node) {
        return this.aggregateResult(previousResult, this.visit(node));
    }

    default public R visitSafe(R previousResult, ASTNode node) {
        return node == null ? previousResult : this.visit(previousResult, node);
    }

    default public R visitChildren(R previousResult, List<? extends ASTNode> children) {
        if (children != null) {
            for (ASTNode aSTNode : children) {
                previousResult = this.visitSafe(previousResult, aSTNode);
            }
        }
        return previousResult;
    }

    default public R visitChildren(R previousResult, ListNode<? extends ASTNode> node) {
        return this.visitChildren(previousResult, node.getChildren());
    }

    default public R visitChildren(List<? extends ASTNode> children) {
        return this.visitChildren(this.initialResult(), children);
    }

    default public R visitChildren(ListNode<? extends ASTNode> node) {
        return this.visitChildren(node.getChildren());
    }

    default public R visitTwoChildren(ASTNode left, ASTNode right) {
        R result = this.initialResult();
        result = this.visitSafe(result, left);
        result = this.visitSafe(result, right);
        return result;
    }

    default public R visitThreeChildren(ASTNode first, ASTNode second, ASTNode third) {
        R result = this.initialResult();
        result = this.visitSafe(result, first);
        result = this.visitSafe(result, second);
        result = this.visitSafe(result, third);
        return result;
    }

    public R initialResult();

    public R superNodeTypeResult();

    public R defaultResult();

    public R aggregateResult(R var1, R var2);

    default public R aggregateResult(R aggregate, R firstResult, R secondResult) {
        return this.aggregateResult(this.aggregateResult(aggregate, firstResult), secondResult);
    }
}

