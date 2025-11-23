/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer;

import io.github.douira.glsl_transformer.GLSLParser;
import oculus.org.antlr.v4.runtime.tree.ParseTreeListener;

public interface GLSLParserListener
extends ParseTreeListener {
    public void enterTranslationUnit(GLSLParser.TranslationUnitContext var1);

    public void exitTranslationUnit(GLSLParser.TranslationUnitContext var1);

    public void enterVersionStatement(GLSLParser.VersionStatementContext var1);

    public void exitVersionStatement(GLSLParser.VersionStatementContext var1);

    public void enterExternalDeclaration(GLSLParser.ExternalDeclarationContext var1);

    public void exitExternalDeclaration(GLSLParser.ExternalDeclarationContext var1);

    public void enterEmptyDeclaration(GLSLParser.EmptyDeclarationContext var1);

    public void exitEmptyDeclaration(GLSLParser.EmptyDeclarationContext var1);

    public void enterPragmaDirective(GLSLParser.PragmaDirectiveContext var1);

    public void exitPragmaDirective(GLSLParser.PragmaDirectiveContext var1);

    public void enterExtensionDirective(GLSLParser.ExtensionDirectiveContext var1);

    public void exitExtensionDirective(GLSLParser.ExtensionDirectiveContext var1);

    public void enterCustomDirective(GLSLParser.CustomDirectiveContext var1);

    public void exitCustomDirective(GLSLParser.CustomDirectiveContext var1);

    public void enterIncludeDirective(GLSLParser.IncludeDirectiveContext var1);

    public void exitIncludeDirective(GLSLParser.IncludeDirectiveContext var1);

    public void enterLayoutDefaults(GLSLParser.LayoutDefaultsContext var1);

    public void exitLayoutDefaults(GLSLParser.LayoutDefaultsContext var1);

    public void enterFunctionDefinition(GLSLParser.FunctionDefinitionContext var1);

    public void exitFunctionDefinition(GLSLParser.FunctionDefinitionContext var1);

    public void enterShiftExpression(GLSLParser.ShiftExpressionContext var1);

    public void exitShiftExpression(GLSLParser.ShiftExpressionContext var1);

    public void enterReferenceExpression(GLSLParser.ReferenceExpressionContext var1);

    public void exitReferenceExpression(GLSLParser.ReferenceExpressionContext var1);

    public void enterAdditiveExpression(GLSLParser.AdditiveExpressionContext var1);

    public void exitAdditiveExpression(GLSLParser.AdditiveExpressionContext var1);

    public void enterRelationalExpression(GLSLParser.RelationalExpressionContext var1);

    public void exitRelationalExpression(GLSLParser.RelationalExpressionContext var1);

    public void enterLogicalExclusiveOrExpression(GLSLParser.LogicalExclusiveOrExpressionContext var1);

    public void exitLogicalExclusiveOrExpression(GLSLParser.LogicalExclusiveOrExpressionContext var1);

    public void enterConditionalExpression(GLSLParser.ConditionalExpressionContext var1);

    public void exitConditionalExpression(GLSLParser.ConditionalExpressionContext var1);

    public void enterAssignmentExpression(GLSLParser.AssignmentExpressionContext var1);

    public void exitAssignmentExpression(GLSLParser.AssignmentExpressionContext var1);

    public void enterLengthAccessExpression(GLSLParser.LengthAccessExpressionContext var1);

    public void exitLengthAccessExpression(GLSLParser.LengthAccessExpressionContext var1);

    public void enterMultiplicativeExpression(GLSLParser.MultiplicativeExpressionContext var1);

    public void exitMultiplicativeExpression(GLSLParser.MultiplicativeExpressionContext var1);

    public void enterGroupingExpression(GLSLParser.GroupingExpressionContext var1);

    public void exitGroupingExpression(GLSLParser.GroupingExpressionContext var1);

    public void enterArrayAccessExpression(GLSLParser.ArrayAccessExpressionContext var1);

    public void exitArrayAccessExpression(GLSLParser.ArrayAccessExpressionContext var1);

    public void enterPrefixExpression(GLSLParser.PrefixExpressionContext var1);

    public void exitPrefixExpression(GLSLParser.PrefixExpressionContext var1);

    public void enterBitwiseInclusiveOrExpression(GLSLParser.BitwiseInclusiveOrExpressionContext var1);

    public void exitBitwiseInclusiveOrExpression(GLSLParser.BitwiseInclusiveOrExpressionContext var1);

    public void enterLogicalInclusiveOrExpression(GLSLParser.LogicalInclusiveOrExpressionContext var1);

    public void exitLogicalInclusiveOrExpression(GLSLParser.LogicalInclusiveOrExpressionContext var1);

    public void enterBitwiseAndExpression(GLSLParser.BitwiseAndExpressionContext var1);

    public void exitBitwiseAndExpression(GLSLParser.BitwiseAndExpressionContext var1);

    public void enterEqualityExpression(GLSLParser.EqualityExpressionContext var1);

    public void exitEqualityExpression(GLSLParser.EqualityExpressionContext var1);

    public void enterLogicalAndExpression(GLSLParser.LogicalAndExpressionContext var1);

    public void exitLogicalAndExpression(GLSLParser.LogicalAndExpressionContext var1);

    public void enterFunctionCallExpression(GLSLParser.FunctionCallExpressionContext var1);

    public void exitFunctionCallExpression(GLSLParser.FunctionCallExpressionContext var1);

    public void enterBitwiseExclusiveOrExpression(GLSLParser.BitwiseExclusiveOrExpressionContext var1);

    public void exitBitwiseExclusiveOrExpression(GLSLParser.BitwiseExclusiveOrExpressionContext var1);

    public void enterMemberAccessExpression(GLSLParser.MemberAccessExpressionContext var1);

    public void exitMemberAccessExpression(GLSLParser.MemberAccessExpressionContext var1);

    public void enterLiteralExpression(GLSLParser.LiteralExpressionContext var1);

    public void exitLiteralExpression(GLSLParser.LiteralExpressionContext var1);

    public void enterPostfixExpression(GLSLParser.PostfixExpressionContext var1);

    public void exitPostfixExpression(GLSLParser.PostfixExpressionContext var1);

    public void enterExpression(GLSLParser.ExpressionContext var1);

    public void exitExpression(GLSLParser.ExpressionContext var1);

    public void enterFunctionDeclaration(GLSLParser.FunctionDeclarationContext var1);

    public void exitFunctionDeclaration(GLSLParser.FunctionDeclarationContext var1);

    public void enterInterfaceBlockDeclaration(GLSLParser.InterfaceBlockDeclarationContext var1);

    public void exitInterfaceBlockDeclaration(GLSLParser.InterfaceBlockDeclarationContext var1);

    public void enterVariableDeclaration(GLSLParser.VariableDeclarationContext var1);

    public void exitVariableDeclaration(GLSLParser.VariableDeclarationContext var1);

    public void enterPrecisionDeclaration(GLSLParser.PrecisionDeclarationContext var1);

    public void exitPrecisionDeclaration(GLSLParser.PrecisionDeclarationContext var1);

    public void enterTypeAndInitDeclaration(GLSLParser.TypeAndInitDeclarationContext var1);

    public void exitTypeAndInitDeclaration(GLSLParser.TypeAndInitDeclarationContext var1);

    public void enterFunctionPrototype(GLSLParser.FunctionPrototypeContext var1);

    public void exitFunctionPrototype(GLSLParser.FunctionPrototypeContext var1);

    public void enterFunctionParameterList(GLSLParser.FunctionParameterListContext var1);

    public void exitFunctionParameterList(GLSLParser.FunctionParameterListContext var1);

    public void enterParameterDeclaration(GLSLParser.ParameterDeclarationContext var1);

    public void exitParameterDeclaration(GLSLParser.ParameterDeclarationContext var1);

    public void enterAttribute(GLSLParser.AttributeContext var1);

    public void exitAttribute(GLSLParser.AttributeContext var1);

    public void enterSingleAttribute(GLSLParser.SingleAttributeContext var1);

    public void exitSingleAttribute(GLSLParser.SingleAttributeContext var1);

    public void enterDeclarationMember(GLSLParser.DeclarationMemberContext var1);

    public void exitDeclarationMember(GLSLParser.DeclarationMemberContext var1);

    public void enterFullySpecifiedType(GLSLParser.FullySpecifiedTypeContext var1);

    public void exitFullySpecifiedType(GLSLParser.FullySpecifiedTypeContext var1);

    public void enterStorageQualifier(GLSLParser.StorageQualifierContext var1);

    public void exitStorageQualifier(GLSLParser.StorageQualifierContext var1);

    public void enterLayoutQualifier(GLSLParser.LayoutQualifierContext var1);

    public void exitLayoutQualifier(GLSLParser.LayoutQualifierContext var1);

    public void enterNamedLayoutQualifier(GLSLParser.NamedLayoutQualifierContext var1);

    public void exitNamedLayoutQualifier(GLSLParser.NamedLayoutQualifierContext var1);

    public void enterSharedLayoutQualifier(GLSLParser.SharedLayoutQualifierContext var1);

    public void exitSharedLayoutQualifier(GLSLParser.SharedLayoutQualifierContext var1);

    public void enterPrecisionQualifier(GLSLParser.PrecisionQualifierContext var1);

    public void exitPrecisionQualifier(GLSLParser.PrecisionQualifierContext var1);

    public void enterInterpolationQualifier(GLSLParser.InterpolationQualifierContext var1);

    public void exitInterpolationQualifier(GLSLParser.InterpolationQualifierContext var1);

    public void enterInvariantQualifier(GLSLParser.InvariantQualifierContext var1);

    public void exitInvariantQualifier(GLSLParser.InvariantQualifierContext var1);

    public void enterPreciseQualifier(GLSLParser.PreciseQualifierContext var1);

    public void exitPreciseQualifier(GLSLParser.PreciseQualifierContext var1);

    public void enterTypeQualifier(GLSLParser.TypeQualifierContext var1);

    public void exitTypeQualifier(GLSLParser.TypeQualifierContext var1);

    public void enterTypeSpecifier(GLSLParser.TypeSpecifierContext var1);

    public void exitTypeSpecifier(GLSLParser.TypeSpecifierContext var1);

    public void enterArraySpecifier(GLSLParser.ArraySpecifierContext var1);

    public void exitArraySpecifier(GLSLParser.ArraySpecifierContext var1);

    public void enterArraySpecifierSegment(GLSLParser.ArraySpecifierSegmentContext var1);

    public void exitArraySpecifierSegment(GLSLParser.ArraySpecifierSegmentContext var1);

    public void enterBuiltinTypeSpecifierParseable(GLSLParser.BuiltinTypeSpecifierParseableContext var1);

    public void exitBuiltinTypeSpecifierParseable(GLSLParser.BuiltinTypeSpecifierParseableContext var1);

    public void enterBuiltinTypeSpecifierFixed(GLSLParser.BuiltinTypeSpecifierFixedContext var1);

    public void exitBuiltinTypeSpecifierFixed(GLSLParser.BuiltinTypeSpecifierFixedContext var1);

    public void enterStructSpecifier(GLSLParser.StructSpecifierContext var1);

    public void exitStructSpecifier(GLSLParser.StructSpecifierContext var1);

    public void enterStructBody(GLSLParser.StructBodyContext var1);

    public void exitStructBody(GLSLParser.StructBodyContext var1);

    public void enterStructMember(GLSLParser.StructMemberContext var1);

    public void exitStructMember(GLSLParser.StructMemberContext var1);

    public void enterStructDeclarator(GLSLParser.StructDeclaratorContext var1);

    public void exitStructDeclarator(GLSLParser.StructDeclaratorContext var1);

    public void enterInitializer(GLSLParser.InitializerContext var1);

    public void exitInitializer(GLSLParser.InitializerContext var1);

    public void enterStatement(GLSLParser.StatementContext var1);

    public void exitStatement(GLSLParser.StatementContext var1);

    public void enterCompoundStatement(GLSLParser.CompoundStatementContext var1);

    public void exitCompoundStatement(GLSLParser.CompoundStatementContext var1);

    public void enterDeclarationStatement(GLSLParser.DeclarationStatementContext var1);

    public void exitDeclarationStatement(GLSLParser.DeclarationStatementContext var1);

    public void enterExpressionStatement(GLSLParser.ExpressionStatementContext var1);

    public void exitExpressionStatement(GLSLParser.ExpressionStatementContext var1);

    public void enterEmptyStatement(GLSLParser.EmptyStatementContext var1);

    public void exitEmptyStatement(GLSLParser.EmptyStatementContext var1);

    public void enterSelectionStatement(GLSLParser.SelectionStatementContext var1);

    public void exitSelectionStatement(GLSLParser.SelectionStatementContext var1);

    public void enterIterationCondition(GLSLParser.IterationConditionContext var1);

    public void exitIterationCondition(GLSLParser.IterationConditionContext var1);

    public void enterSwitchStatement(GLSLParser.SwitchStatementContext var1);

    public void exitSwitchStatement(GLSLParser.SwitchStatementContext var1);

    public void enterValuedCaseLabel(GLSLParser.ValuedCaseLabelContext var1);

    public void exitValuedCaseLabel(GLSLParser.ValuedCaseLabelContext var1);

    public void enterDefaultCaseLabel(GLSLParser.DefaultCaseLabelContext var1);

    public void exitDefaultCaseLabel(GLSLParser.DefaultCaseLabelContext var1);

    public void enterWhileStatement(GLSLParser.WhileStatementContext var1);

    public void exitWhileStatement(GLSLParser.WhileStatementContext var1);

    public void enterDoWhileStatement(GLSLParser.DoWhileStatementContext var1);

    public void exitDoWhileStatement(GLSLParser.DoWhileStatementContext var1);

    public void enterForStatement(GLSLParser.ForStatementContext var1);

    public void exitForStatement(GLSLParser.ForStatementContext var1);

    public void enterContinueStatement(GLSLParser.ContinueStatementContext var1);

    public void exitContinueStatement(GLSLParser.ContinueStatementContext var1);

    public void enterBreakStatement(GLSLParser.BreakStatementContext var1);

    public void exitBreakStatement(GLSLParser.BreakStatementContext var1);

    public void enterReturnStatement(GLSLParser.ReturnStatementContext var1);

    public void exitReturnStatement(GLSLParser.ReturnStatementContext var1);

    public void enterDiscardStatement(GLSLParser.DiscardStatementContext var1);

    public void exitDiscardStatement(GLSLParser.DiscardStatementContext var1);

    public void enterIgnoreIntersectionStatement(GLSLParser.IgnoreIntersectionStatementContext var1);

    public void exitIgnoreIntersectionStatement(GLSLParser.IgnoreIntersectionStatementContext var1);

    public void enterTerminateRayStatement(GLSLParser.TerminateRayStatementContext var1);

    public void exitTerminateRayStatement(GLSLParser.TerminateRayStatementContext var1);

    public void enterDemoteStatement(GLSLParser.DemoteStatementContext var1);

    public void exitDemoteStatement(GLSLParser.DemoteStatementContext var1);
}

