/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.traversal;

import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.IterationConditionInitializer;
import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.node.VersionStatement;
import io.github.douira.glsl_transformer.ast.node.declaration.Declaration;
import io.github.douira.glsl_transformer.ast.node.declaration.DeclarationMember;
import io.github.douira.glsl_transformer.ast.node.declaration.FunctionDeclaration;
import io.github.douira.glsl_transformer.ast.node.declaration.FunctionParameter;
import io.github.douira.glsl_transformer.ast.node.declaration.InterfaceBlockDeclaration;
import io.github.douira.glsl_transformer.ast.node.declaration.PrecisionDeclaration;
import io.github.douira.glsl_transformer.ast.node.declaration.TypeAndInitDeclaration;
import io.github.douira.glsl_transformer.ast.node.declaration.VariableDeclaration;
import io.github.douira.glsl_transformer.ast.node.expression.ConditionExpression;
import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.expression.LiteralExpression;
import io.github.douira.glsl_transformer.ast.node.expression.ManyExpression;
import io.github.douira.glsl_transformer.ast.node.expression.ReferenceExpression;
import io.github.douira.glsl_transformer.ast.node.expression.SequenceExpression;
import io.github.douira.glsl_transformer.ast.node.expression.TerminalExpression;
import io.github.douira.glsl_transformer.ast.node.expression.TernaryExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.AdditionAssignmentExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.AdditionExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.ArrayAccessExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.AssignmentExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.BinaryExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.BitwiseAndAssignmentExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.BitwiseAndExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.BitwiseOrAssignmentExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.BitwiseOrExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.BitwiseXorAssignmentExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.BitwiseXorExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.BooleanAndExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.BooleanOrExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.BooleanXorExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.DivisionAssignmentExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.DivisionExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.EqualityExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.GreaterThanEqualExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.GreaterThanExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.InequalityExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.LeftShiftAssignmentExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.LeftShiftExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.LessThanEqualExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.LessThanExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.ModuloAssignmentExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.ModuloExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.MultiplicationAssignmentExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.MultiplicationExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.RightShiftAssignmentExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.RightShiftExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.SubtractionAssignmentExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.SubtractionExpression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.BitwiseNotExpression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.BooleanNotExpression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.DecrementPostfixExpression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.DecrementPrefixExpression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.FunctionCallExpression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.GroupingExpression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.IdentityExpression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.IncrementPostfixExpression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.IncrementPrefixExpression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.LengthAccessExpression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.MemberAccessExpression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.NegationExpression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.UnaryExpression;
import io.github.douira.glsl_transformer.ast.node.external_declaration.CustomDirective;
import io.github.douira.glsl_transformer.ast.node.external_declaration.DeclarationExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.external_declaration.EmptyDeclaration;
import io.github.douira.glsl_transformer.ast.node.external_declaration.ExtensionDirective;
import io.github.douira.glsl_transformer.ast.node.external_declaration.ExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.external_declaration.FunctionDefinition;
import io.github.douira.glsl_transformer.ast.node.external_declaration.IncludeDirective;
import io.github.douira.glsl_transformer.ast.node.external_declaration.LayoutDefaults;
import io.github.douira.glsl_transformer.ast.node.external_declaration.PragmaDirective;
import io.github.douira.glsl_transformer.ast.node.statement.CompoundStatement;
import io.github.douira.glsl_transformer.ast.node.statement.ManyStatement;
import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.node.statement.loop.DoWhileLoopStatement;
import io.github.douira.glsl_transformer.ast.node.statement.loop.ForLoopStatement;
import io.github.douira.glsl_transformer.ast.node.statement.loop.LoopStatement;
import io.github.douira.glsl_transformer.ast.node.statement.loop.WhileLoopStatement;
import io.github.douira.glsl_transformer.ast.node.statement.selection.SelectionStatement;
import io.github.douira.glsl_transformer.ast.node.statement.selection.SwitchStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.BreakStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.CaseLabelStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.CaseStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.ContinueStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.DeclarationStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.DefaultStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.DemoteStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.DiscardStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.EmptyStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.ExpressionStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.IgnoreIntersectionStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.ReturnStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.SemiTerminalStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.TerminalStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.TerminateRayStatement;
import io.github.douira.glsl_transformer.ast.node.type.FullySpecifiedType;
import io.github.douira.glsl_transformer.ast.node.type.initializer.ExpressionInitializer;
import io.github.douira.glsl_transformer.ast.node.type.initializer.Initializer;
import io.github.douira.glsl_transformer.ast.node.type.initializer.NestedInitializer;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.InterpolationQualifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.InvariantQualifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.LayoutQualifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.LayoutQualifierPart;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.NamedLayoutQualifierPart;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.PreciseQualifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.PrecisionQualifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.SharedLayoutQualifierPart;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.StorageQualifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.TypeQualifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.TypeQualifierPart;
import io.github.douira.glsl_transformer.ast.node.type.specifier.ArraySpecifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.BuiltinFixedTypeSpecifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.BuiltinNumericTypeSpecifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.FunctionPrototype;
import io.github.douira.glsl_transformer.ast.node.type.specifier.TypeReference;
import io.github.douira.glsl_transformer.ast.node.type.specifier.TypeSpecifier;
import io.github.douira.glsl_transformer.ast.node.type.struct.StructBody;
import io.github.douira.glsl_transformer.ast.node.type.struct.StructDeclarator;
import io.github.douira.glsl_transformer.ast.node.type.struct.StructMember;
import io.github.douira.glsl_transformer.ast.node.type.struct.StructSpecifier;
import io.github.douira.glsl_transformer.ast.traversal.GeneralASTVisitor;

public interface ASTVisitor<R>
extends GeneralASTVisitor<R> {
    default public R visitTranslationUnit(TranslationUnit node) {
        Object result = this.initialResult();
        result = this.visitSafe(result, node.getVersionStatement());
        this.visitChildren(result, node);
        return result;
    }

    default public R visitVersionStatement(VersionStatement node) {
        return this.visitData(node.profile);
    }

    default public R visitExternalDeclaration(ExternalDeclaration node) {
        return this.superNodeTypeResult();
    }

    default public R visitFunctionDefinition(FunctionDefinition node) {
        return this.visitTwoChildren(node.getFunctionPrototype(), node.getBody());
    }

    default public R visitEmptyDeclaration(EmptyDeclaration node) {
        return this.defaultResult();
    }

    default public R visitPragmaDirective(PragmaDirective node) {
        Object result = this.visitData(node.stdGL);
        result = this.visitData(result, node.type);
        result = this.visitData(result, node.getCustomName());
        return this.visitData(result, node.state);
    }

    default public R visitExtensionDirective(ExtensionDirective node) {
        Object result = this.visitData(this.superNodeTypeResult(), node.getName());
        return this.visitData(result, node.behavior);
    }

    default public R visitCustomDirective(CustomDirective node) {
        return this.visitData(node.getContent());
    }

    default public R visitIncludeDirective(IncludeDirective node) {
        return this.visitData(node.getContent());
    }

    default public R visitDeclarationExternalDeclaration(DeclarationExternalDeclaration node) {
        return this.visit(node.getDeclaration());
    }

    default public R visitLayoutDefaults(LayoutDefaults node) {
        Object result = this.visit(node.getQualifier());
        result = this.aggregateResult(result, this.visitData(node.mode));
        return result;
    }

    default public R visitExpression(Expression node) {
        return this.superNodeTypeResult();
    }

    default public R visitUnaryExpression(UnaryExpression node) {
        return this.superNodeTypeResult();
    }

    default public R visitBitwiseNotExpression(BitwiseNotExpression node) {
        return this.visit(node.getOperand());
    }

    default public R visitBooleanNotExpression(BooleanNotExpression node) {
        return this.visit(node.getOperand());
    }

    default public R visitDecrementPostfixExpression(DecrementPostfixExpression node) {
        return this.visit(node.getOperand());
    }

    default public R visitDecrementPrefixExpression(DecrementPrefixExpression node) {
        return this.visit(node.getOperand());
    }

    default public R visitFunctionCallExpression(FunctionCallExpression node) {
        Object result = this.visitTwoChildren(node.getFunctionName(), node.getFunctionSpecifier());
        return this.visitChildren(result, node.getParameters());
    }

    default public R visitGroupingExpression(GroupingExpression node) {
        return this.visit(node.getOperand());
    }

    default public R visitIncrementPostfixExpression(IncrementPostfixExpression node) {
        return this.visit(node.getOperand());
    }

    default public R visitIncrementPrefixExpression(IncrementPrefixExpression node) {
        return this.visit(node.getOperand());
    }

    default public R visitMemberAccessExpression(MemberAccessExpression node) {
        return this.visitTwoChildren(node.getOperand(), node.getMember());
    }

    default public R visitLengthAccessExpression(LengthAccessExpression node) {
        return this.visit(node.getOperand());
    }

    default public R visitNegationExpression(NegationExpression node) {
        return this.visit(node.getOperand());
    }

    default public R visitIdentityExpression(IdentityExpression node) {
        return this.visit(node.getOperand());
    }

    default public R visitBinaryExpression(BinaryExpression node) {
        return this.superNodeTypeResult();
    }

    default public R visitArrayAccessExpression(ArrayAccessExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitMultiplicationExpression(MultiplicationExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitDivisionExpression(DivisionExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitModuloExpression(ModuloExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitAdditionExpression(AdditionExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitSubtractionExpression(SubtractionExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitLeftShiftExpression(LeftShiftExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitRightShiftExpression(RightShiftExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitLessThanExpression(LessThanExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitGreaterThanExpression(GreaterThanExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitLessThanEqualExpression(LessThanEqualExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitGreaterThanEqualExpression(GreaterThanEqualExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitEqualityExpression(EqualityExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitInequalityExpression(InequalityExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitBitwiseAndExpression(BitwiseAndExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitBitwiseXorExpression(BitwiseXorExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitBitwiseOrExpression(BitwiseOrExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitBooleanAndExpression(BooleanAndExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitBooleanXorExpression(BooleanXorExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitBooleanOrExpression(BooleanOrExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitAssignmentExpression(AssignmentExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitMultiplicationAssignmentExpression(MultiplicationAssignmentExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitDivisionAssignmentExpression(DivisionAssignmentExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitModuloAssignmentExpression(ModuloAssignmentExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitAdditionAssignmentExpression(AdditionAssignmentExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitSubtractionAssignmentExpression(SubtractionAssignmentExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitLeftShiftAssignmentExpression(LeftShiftAssignmentExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitRightShiftAssignmentExpression(RightShiftAssignmentExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitBitwiseAndAssignmentExpression(BitwiseAndAssignmentExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitBitwiseXorAssignmentExpression(BitwiseXorAssignmentExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitBitwiseOrAssignmentExpression(BitwiseOrAssignmentExpression node) {
        return this.visitTwoChildren(node.getLeft(), node.getRight());
    }

    default public R visitTernaryExpression(TernaryExpression node) {
        return this.superNodeTypeResult();
    }

    default public R visitConditionExpression(ConditionExpression node) {
        return this.visitThreeChildren(node.getCondition(), node.getTrueExpression(), node.getFalseExpression());
    }

    default public R visitManyExpression(ManyExpression node) {
        return this.superNodeTypeResult();
    }

    default public R visitSequenceExpression(SequenceExpression node) {
        return this.visitChildren(node);
    }

    default public R visitTerminalExpression(TerminalExpression node) {
        return this.superNodeTypeResult();
    }

    default public R visitReferenceExpression(ReferenceExpression node) {
        return this.visit(node.getIdentifier());
    }

    default public R visitLiteralExpression(LiteralExpression node) {
        Object result = this.visitData(node.getType());
        result = this.visitData(result, node.getNumber());
        return node.isInteger() ? this.visitData(result, (Object)node.getIntegerFormat()) : result;
    }

    default public R visitStatement(Statement node) {
        return this.superNodeTypeResult();
    }

    default public R visitEmptyStatement(EmptyStatement node) {
        return this.defaultResult();
    }

    default public R visitCompoundStatement(CompoundStatement node) {
        return this.visitChildren(node);
    }

    default public R visitDeclarationStatement(DeclarationStatement node) {
        return this.visit(node.getDeclaration());
    }

    default public R visitExpressionStatement(ExpressionStatement node) {
        return this.visit(node.getExpression());
    }

    default public R visitSelectionStatement(SelectionStatement node) {
        return this.visitThreeChildren(node.getCondition(), node.getIfTrue(), node.getIfFalse());
    }

    default public R visitSwitchStatement(SwitchStatement node) {
        return this.visitTwoChildren(node.getExpression(), node.getStatement());
    }

    default public R visitCaseStatement(CaseStatement node) {
        return this.visit(node.getExpression());
    }

    default public R visitDefaultStatement(DefaultStatement node) {
        return this.defaultResult();
    }

    default public R visitCaseLabelStatement(CaseLabelStatement node) {
        return this.superNodeTypeResult();
    }

    default public R visitForLoopStatement(ForLoopStatement node) {
        Object result = this.initialResult();
        result = this.visitSafe(result, node.getInitExpression());
        result = this.visitSafe(result, node.getInitDeclaration());
        result = this.visitSafe(result, node.getCondition());
        result = this.visitSafe(result, node.getIterationConditionInitializer());
        result = this.visitSafe(result, node.getIncrementer());
        return this.visit(result, node.getStatement());
    }

    default public R visitWhileLoopStatement(WhileLoopStatement node) {
        return this.visitTwoChildren(node.getCondition(), node.getStatement());
    }

    default public R visitDoWhileLoopStatement(DoWhileLoopStatement node) {
        return this.visitTwoChildren(node.getStatement(), node.getCondition());
    }

    default public R visitContinueStatement(ContinueStatement node) {
        return this.defaultResult();
    }

    default public R visitBreakStatement(BreakStatement node) {
        return this.defaultResult();
    }

    default public R visitReturnStatement(ReturnStatement node) {
        return this.visitSafe(this.initialResult(), node.getExpression());
    }

    default public R visitDiscardStatement(DiscardStatement node) {
        return this.defaultResult();
    }

    default public R visitIgnoreIntersectionStatement(IgnoreIntersectionStatement node) {
        return this.defaultResult();
    }

    default public R visitTerminateRayStatement(TerminateRayStatement node) {
        return this.defaultResult();
    }

    default public R visitDemoteStatement(DemoteStatement node) {
        return this.defaultResult();
    }

    default public R visitManyStatement(ManyStatement node) {
        return this.superNodeTypeResult();
    }

    default public R visitLoopStatement(LoopStatement node) {
        return this.superNodeTypeResult();
    }

    default public R visitTerminalStatement(TerminalStatement node) {
        return this.superNodeTypeResult();
    }

    default public R visitSemiTerminalStatement(SemiTerminalStatement node) {
        return this.superNodeTypeResult();
    }

    default public R visitDeclaration(Declaration node) {
        return this.superNodeTypeResult();
    }

    default public R visitDeclarationMember(DeclarationMember node) {
        return this.visitThreeChildren(node.getName(), node.getArraySpecifier(), node.getInitializer());
    }

    default public R visitFunctionDeclaration(FunctionDeclaration node) {
        return this.visit(node.getFunctionPrototype());
    }

    default public R visitFunctionParameter(FunctionParameter node) {
        return this.visitThreeChildren(node.getType(), node.getName(), node.getArraySpecifier());
    }

    default public R visitInterfaceBlockDeclaration(InterfaceBlockDeclaration node) {
        Object result = this.visit(node.getTypeQualifier());
        result = this.visit(result, node.getBlockName());
        result = this.visit(result, node.getStructBody());
        result = this.visitSafe(result, node.getVariableName());
        return this.visitSafe(result, node.getArraySpecifier());
    }

    default public R visitPrecisionDeclaration(PrecisionDeclaration node) {
        return this.visitTwoChildren(node.getPrecisionQualifier(), node.getTypeSpecifier());
    }

    default public R visitTypeAndInitDeclaration(TypeAndInitDeclaration node) {
        return this.visitChildren(this.visit(node.getType()), node.getMembers());
    }

    default public R visitVariableDeclaration(VariableDeclaration node) {
        return this.visitChildren(this.visit(node.getTypeQualifier()), node.getNames());
    }

    default public R visitExpressionInitializer(ExpressionInitializer node) {
        return this.visit(node.getExpression());
    }

    default public R visitInitializer(Initializer node) {
        return this.superNodeTypeResult();
    }

    default public R visitNestedInitializer(NestedInitializer node) {
        return this.visitChildren(node.getInitializers());
    }

    default public R visitInterpolationQualifier(InterpolationQualifier node) {
        return this.visitData(node.interpolationType);
    }

    default public R visitInvariantQualifier(InvariantQualifier node) {
        return this.defaultResult();
    }

    default public R visitLayoutQualifier(LayoutQualifier node) {
        return this.visitChildren(node.getParts());
    }

    default public R visitLayoutQualifierPart(LayoutQualifierPart node) {
        return this.superNodeTypeResult();
    }

    default public R visitNamedLayoutQualifierPart(NamedLayoutQualifierPart node) {
        return this.visitTwoChildren(node.getName(), node.getExpression());
    }

    default public R visitPreciseQualifier(PreciseQualifier node) {
        return this.defaultResult();
    }

    default public R visitPrecisionQualifier(PrecisionQualifier node) {
        return this.visitData(node.precisionLevel);
    }

    default public R visitSharedLayoutQualifierPart(SharedLayoutQualifierPart node) {
        return this.defaultResult();
    }

    default public R visitStorageQualifier(StorageQualifier node) {
        Object result = this.visitChildren(node.getTypeNames());
        return this.visitData(result, node.storageType);
    }

    default public R visitTypeQualifier(TypeQualifier node) {
        return this.visitChildren(node);
    }

    default public R visitTypeQualifierPart(TypeQualifierPart node) {
        return this.superNodeTypeResult();
    }

    default public R visitArraySpecifier(ArraySpecifier node) {
        return this.visitChildren(node);
    }

    default public R visitBuiltinFixedTypeSpecifier(BuiltinFixedTypeSpecifier node) {
        return this.visitData(node.type);
    }

    default public R visitBuiltinNumericTypeSpecifier(BuiltinNumericTypeSpecifier node) {
        return this.visitData(node.type);
    }

    default public R visitTypeReference(TypeReference node) {
        return this.visit(node.getReference());
    }

    default public R visitTypeSpecifier(TypeSpecifier node) {
        return this.superNodeTypeResult();
    }

    default public R visitStructBody(StructBody node) {
        return this.visitChildren(node);
    }

    default public R visitStructDeclarator(StructDeclarator node) {
        return this.visitTwoChildren(node.getName(), node.getArraySpecifier());
    }

    default public R visitStructMember(StructMember node) {
        return this.visitChildren(this.visit(node.getType()), node.getDeclarators());
    }

    default public R visitStructSpecifier(StructSpecifier node) {
        return this.visitTwoChildren(node.getName(), node.getStructBody());
    }

    default public R visitFullySpecifiedType(FullySpecifiedType node) {
        return this.visitTwoChildren(node.getTypeQualifier(), node.getTypeSpecifier());
    }

    default public R visitIterationConditionInitializer(IterationConditionInitializer node) {
        return this.visitThreeChildren(node.getType(), node.getName(), node.getInitializer());
    }

    default public R visitFunctionPrototype(FunctionPrototype node) {
        return this.visitChildren(this.visitTwoChildren(node.getReturnType(), node.getName()), node);
    }

    default public R visitIdentifier(Identifier node) {
        return this.visitData(node.getName());
    }
}

