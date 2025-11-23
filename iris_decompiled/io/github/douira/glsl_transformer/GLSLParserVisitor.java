/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer;

import io.github.douira.glsl_transformer.GLSLParser;
import oculus.org.antlr.v4.runtime.tree.ParseTreeVisitor;

public interface GLSLParserVisitor<T>
extends ParseTreeVisitor<T> {
    public T visitTranslationUnit(GLSLParser.TranslationUnitContext var1);

    public T visitVersionStatement(GLSLParser.VersionStatementContext var1);

    public T visitExternalDeclaration(GLSLParser.ExternalDeclarationContext var1);

    public T visitEmptyDeclaration(GLSLParser.EmptyDeclarationContext var1);

    public T visitPragmaDirective(GLSLParser.PragmaDirectiveContext var1);

    public T visitExtensionDirective(GLSLParser.ExtensionDirectiveContext var1);

    public T visitCustomDirective(GLSLParser.CustomDirectiveContext var1);

    public T visitIncludeDirective(GLSLParser.IncludeDirectiveContext var1);

    public T visitLayoutDefaults(GLSLParser.LayoutDefaultsContext var1);

    public T visitFunctionDefinition(GLSLParser.FunctionDefinitionContext var1);

    public T visitShiftExpression(GLSLParser.ShiftExpressionContext var1);

    public T visitReferenceExpression(GLSLParser.ReferenceExpressionContext var1);

    public T visitAdditiveExpression(GLSLParser.AdditiveExpressionContext var1);

    public T visitRelationalExpression(GLSLParser.RelationalExpressionContext var1);

    public T visitLogicalExclusiveOrExpression(GLSLParser.LogicalExclusiveOrExpressionContext var1);

    public T visitConditionalExpression(GLSLParser.ConditionalExpressionContext var1);

    public T visitAssignmentExpression(GLSLParser.AssignmentExpressionContext var1);

    public T visitLengthAccessExpression(GLSLParser.LengthAccessExpressionContext var1);

    public T visitMultiplicativeExpression(GLSLParser.MultiplicativeExpressionContext var1);

    public T visitGroupingExpression(GLSLParser.GroupingExpressionContext var1);

    public T visitArrayAccessExpression(GLSLParser.ArrayAccessExpressionContext var1);

    public T visitPrefixExpression(GLSLParser.PrefixExpressionContext var1);

    public T visitBitwiseInclusiveOrExpression(GLSLParser.BitwiseInclusiveOrExpressionContext var1);

    public T visitLogicalInclusiveOrExpression(GLSLParser.LogicalInclusiveOrExpressionContext var1);

    public T visitBitwiseAndExpression(GLSLParser.BitwiseAndExpressionContext var1);

    public T visitEqualityExpression(GLSLParser.EqualityExpressionContext var1);

    public T visitLogicalAndExpression(GLSLParser.LogicalAndExpressionContext var1);

    public T visitFunctionCallExpression(GLSLParser.FunctionCallExpressionContext var1);

    public T visitBitwiseExclusiveOrExpression(GLSLParser.BitwiseExclusiveOrExpressionContext var1);

    public T visitMemberAccessExpression(GLSLParser.MemberAccessExpressionContext var1);

    public T visitLiteralExpression(GLSLParser.LiteralExpressionContext var1);

    public T visitPostfixExpression(GLSLParser.PostfixExpressionContext var1);

    public T visitExpression(GLSLParser.ExpressionContext var1);

    public T visitFunctionDeclaration(GLSLParser.FunctionDeclarationContext var1);

    public T visitInterfaceBlockDeclaration(GLSLParser.InterfaceBlockDeclarationContext var1);

    public T visitVariableDeclaration(GLSLParser.VariableDeclarationContext var1);

    public T visitPrecisionDeclaration(GLSLParser.PrecisionDeclarationContext var1);

    public T visitTypeAndInitDeclaration(GLSLParser.TypeAndInitDeclarationContext var1);

    public T visitFunctionPrototype(GLSLParser.FunctionPrototypeContext var1);

    public T visitFunctionParameterList(GLSLParser.FunctionParameterListContext var1);

    public T visitParameterDeclaration(GLSLParser.ParameterDeclarationContext var1);

    public T visitAttribute(GLSLParser.AttributeContext var1);

    public T visitSingleAttribute(GLSLParser.SingleAttributeContext var1);

    public T visitDeclarationMember(GLSLParser.DeclarationMemberContext var1);

    public T visitFullySpecifiedType(GLSLParser.FullySpecifiedTypeContext var1);

    public T visitStorageQualifier(GLSLParser.StorageQualifierContext var1);

    public T visitLayoutQualifier(GLSLParser.LayoutQualifierContext var1);

    public T visitNamedLayoutQualifier(GLSLParser.NamedLayoutQualifierContext var1);

    public T visitSharedLayoutQualifier(GLSLParser.SharedLayoutQualifierContext var1);

    public T visitPrecisionQualifier(GLSLParser.PrecisionQualifierContext var1);

    public T visitInterpolationQualifier(GLSLParser.InterpolationQualifierContext var1);

    public T visitInvariantQualifier(GLSLParser.InvariantQualifierContext var1);

    public T visitPreciseQualifier(GLSLParser.PreciseQualifierContext var1);

    public T visitTypeQualifier(GLSLParser.TypeQualifierContext var1);

    public T visitTypeSpecifier(GLSLParser.TypeSpecifierContext var1);

    public T visitArraySpecifier(GLSLParser.ArraySpecifierContext var1);

    public T visitArraySpecifierSegment(GLSLParser.ArraySpecifierSegmentContext var1);

    public T visitBuiltinTypeSpecifierParseable(GLSLParser.BuiltinTypeSpecifierParseableContext var1);

    public T visitBuiltinTypeSpecifierFixed(GLSLParser.BuiltinTypeSpecifierFixedContext var1);

    public T visitStructSpecifier(GLSLParser.StructSpecifierContext var1);

    public T visitStructBody(GLSLParser.StructBodyContext var1);

    public T visitStructMember(GLSLParser.StructMemberContext var1);

    public T visitStructDeclarator(GLSLParser.StructDeclaratorContext var1);

    public T visitInitializer(GLSLParser.InitializerContext var1);

    public T visitStatement(GLSLParser.StatementContext var1);

    public T visitCompoundStatement(GLSLParser.CompoundStatementContext var1);

    public T visitDeclarationStatement(GLSLParser.DeclarationStatementContext var1);

    public T visitExpressionStatement(GLSLParser.ExpressionStatementContext var1);

    public T visitEmptyStatement(GLSLParser.EmptyStatementContext var1);

    public T visitSelectionStatement(GLSLParser.SelectionStatementContext var1);

    public T visitIterationCondition(GLSLParser.IterationConditionContext var1);

    public T visitSwitchStatement(GLSLParser.SwitchStatementContext var1);

    public T visitValuedCaseLabel(GLSLParser.ValuedCaseLabelContext var1);

    public T visitDefaultCaseLabel(GLSLParser.DefaultCaseLabelContext var1);

    public T visitWhileStatement(GLSLParser.WhileStatementContext var1);

    public T visitDoWhileStatement(GLSLParser.DoWhileStatementContext var1);

    public T visitForStatement(GLSLParser.ForStatementContext var1);

    public T visitContinueStatement(GLSLParser.ContinueStatementContext var1);

    public T visitBreakStatement(GLSLParser.BreakStatementContext var1);

    public T visitReturnStatement(GLSLParser.ReturnStatementContext var1);

    public T visitDiscardStatement(GLSLParser.DiscardStatementContext var1);

    public T visitIgnoreIntersectionStatement(GLSLParser.IgnoreIntersectionStatementContext var1);

    public T visitTerminateRayStatement(GLSLParser.TerminateRayStatementContext var1);

    public T visitDemoteStatement(GLSLParser.DemoteStatementContext var1);
}

