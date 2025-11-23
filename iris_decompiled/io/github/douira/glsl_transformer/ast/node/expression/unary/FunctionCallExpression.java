/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.expression.unary;

import io.github.douira.glsl_transformer.ast.data.ChildNodeList;
import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.expression.TerminalExpression;
import io.github.douira.glsl_transformer.ast.node.type.specifier.TypeSpecifier;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;
import java.util.stream.Stream;

public class FunctionCallExpression
extends TerminalExpression {
    protected Identifier functionName;
    protected TypeSpecifier functionSpecifier;
    protected FunctionReferenceType referenceType;
    protected ChildNodeList<Expression> parameters;

    private FunctionCallExpression(Identifier functionName, TypeSpecifier functionSpecifier, FunctionReferenceType referenceType, Stream<Expression> parameters) {
        this.functionName = this.setup(functionName, this::setFunctionName);
        this.functionSpecifier = this.setup(functionSpecifier, this::setFunctionSpecifier);
        this.referenceType = referenceType;
        this.parameters = ChildNodeList.collect(parameters, this);
    }

    public FunctionCallExpression(Identifier functionName) {
        this.functionName = this.setup(functionName, this::setFunctionName);
        this.referenceType = FunctionReferenceType.NAME;
        this.parameters = new ChildNodeList(this);
    }

    public FunctionCallExpression(TypeSpecifier functionSpecifier) {
        this.functionSpecifier = this.setup(functionSpecifier, this::setFunctionSpecifier);
        this.referenceType = FunctionReferenceType.TYPE_SPECIFIER;
        this.parameters = new ChildNodeList(this);
    }

    public FunctionCallExpression(Identifier functionName, Stream<Expression> parameters) {
        this(functionName);
        this.parameters = ChildNodeList.collect(parameters, this);
    }

    public FunctionCallExpression(TypeSpecifier functionSpecifier, Stream<Expression> parameters) {
        this(functionSpecifier);
        this.parameters = ChildNodeList.collect(parameters, this);
    }

    public Identifier getFunctionName() {
        return this.functionName;
    }

    protected void setFunctionName(Identifier functionName) {
        this.updateParents(this.functionName, functionName, this::setFunctionName);
        this.functionName = functionName;
    }

    public void useFunctionName(Identifier functionName) {
        this.setFunctionName(functionName);
        this.referenceType = FunctionReferenceType.NAME;
        this.setFunctionSpecifier(null);
    }

    public TypeSpecifier getFunctionSpecifier() {
        return this.functionSpecifier;
    }

    protected void setFunctionSpecifier(TypeSpecifier functionSpecifier) {
        this.updateParents(this.functionSpecifier, functionSpecifier, this::setFunctionSpecifier);
        this.functionSpecifier = functionSpecifier;
    }

    public void useFunctionSpecifier(TypeSpecifier functionSpecifier) {
        this.setFunctionSpecifier(functionSpecifier);
        this.referenceType = FunctionReferenceType.TYPE_SPECIFIER;
        this.setFunctionName(null);
    }

    public FunctionReferenceType getReferenceType() {
        return this.referenceType;
    }

    public ASTNode getReference() {
        return this.referenceType == FunctionReferenceType.NAME ? this.functionName : this.functionSpecifier;
    }

    public ChildNodeList<Expression> getParameters() {
        return this.parameters;
    }

    @Override
    public Expression.ExpressionType getExpressionType() {
        return Expression.ExpressionType.FUNCTION_CALL;
    }

    @Override
    public <R> R expressionAccept(ASTVisitor<R> visitor) {
        return visitor.visitFunctionCallExpression(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterFunctionCallExpression(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitFunctionCallExpression(this);
    }

    @Override
    public FunctionCallExpression clone() {
        return new FunctionCallExpression(FunctionCallExpression.clone(this.functionName), FunctionCallExpression.clone(this.functionSpecifier), this.referenceType, FunctionCallExpression.clone(this.parameters));
    }

    @Override
    public FunctionCallExpression cloneInto(Root root) {
        return (FunctionCallExpression)super.cloneInto(root);
    }

    public static enum FunctionReferenceType {
        NAME,
        TYPE_SPECIFIER;

    }
}

