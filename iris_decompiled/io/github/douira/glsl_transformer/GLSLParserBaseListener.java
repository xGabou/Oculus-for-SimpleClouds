/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer;

import io.github.douira.glsl_transformer.GLSLParser;
import io.github.douira.glsl_transformer.GLSLParserListener;
import oculus.org.antlr.v4.runtime.ParserRuleContext;
import oculus.org.antlr.v4.runtime.tree.ErrorNode;
import oculus.org.antlr.v4.runtime.tree.TerminalNode;

public class GLSLParserBaseListener
implements GLSLParserListener {
    @Override
    public void enterTranslationUnit(GLSLParser.TranslationUnitContext ctx) {
    }

    @Override
    public void exitTranslationUnit(GLSLParser.TranslationUnitContext ctx) {
    }

    @Override
    public void enterVersionStatement(GLSLParser.VersionStatementContext ctx) {
    }

    @Override
    public void exitVersionStatement(GLSLParser.VersionStatementContext ctx) {
    }

    @Override
    public void enterExternalDeclaration(GLSLParser.ExternalDeclarationContext ctx) {
    }

    @Override
    public void exitExternalDeclaration(GLSLParser.ExternalDeclarationContext ctx) {
    }

    @Override
    public void enterEmptyDeclaration(GLSLParser.EmptyDeclarationContext ctx) {
    }

    @Override
    public void exitEmptyDeclaration(GLSLParser.EmptyDeclarationContext ctx) {
    }

    @Override
    public void enterPragmaDirective(GLSLParser.PragmaDirectiveContext ctx) {
    }

    @Override
    public void exitPragmaDirective(GLSLParser.PragmaDirectiveContext ctx) {
    }

    @Override
    public void enterExtensionDirective(GLSLParser.ExtensionDirectiveContext ctx) {
    }

    @Override
    public void exitExtensionDirective(GLSLParser.ExtensionDirectiveContext ctx) {
    }

    @Override
    public void enterCustomDirective(GLSLParser.CustomDirectiveContext ctx) {
    }

    @Override
    public void exitCustomDirective(GLSLParser.CustomDirectiveContext ctx) {
    }

    @Override
    public void enterIncludeDirective(GLSLParser.IncludeDirectiveContext ctx) {
    }

    @Override
    public void exitIncludeDirective(GLSLParser.IncludeDirectiveContext ctx) {
    }

    @Override
    public void enterLayoutDefaults(GLSLParser.LayoutDefaultsContext ctx) {
    }

    @Override
    public void exitLayoutDefaults(GLSLParser.LayoutDefaultsContext ctx) {
    }

    @Override
    public void enterFunctionDefinition(GLSLParser.FunctionDefinitionContext ctx) {
    }

    @Override
    public void exitFunctionDefinition(GLSLParser.FunctionDefinitionContext ctx) {
    }

    @Override
    public void enterShiftExpression(GLSLParser.ShiftExpressionContext ctx) {
    }

    @Override
    public void exitShiftExpression(GLSLParser.ShiftExpressionContext ctx) {
    }

    @Override
    public void enterReferenceExpression(GLSLParser.ReferenceExpressionContext ctx) {
    }

    @Override
    public void exitReferenceExpression(GLSLParser.ReferenceExpressionContext ctx) {
    }

    @Override
    public void enterAdditiveExpression(GLSLParser.AdditiveExpressionContext ctx) {
    }

    @Override
    public void exitAdditiveExpression(GLSLParser.AdditiveExpressionContext ctx) {
    }

    @Override
    public void enterRelationalExpression(GLSLParser.RelationalExpressionContext ctx) {
    }

    @Override
    public void exitRelationalExpression(GLSLParser.RelationalExpressionContext ctx) {
    }

    @Override
    public void enterLogicalExclusiveOrExpression(GLSLParser.LogicalExclusiveOrExpressionContext ctx) {
    }

    @Override
    public void exitLogicalExclusiveOrExpression(GLSLParser.LogicalExclusiveOrExpressionContext ctx) {
    }

    @Override
    public void enterConditionalExpression(GLSLParser.ConditionalExpressionContext ctx) {
    }

    @Override
    public void exitConditionalExpression(GLSLParser.ConditionalExpressionContext ctx) {
    }

    @Override
    public void enterAssignmentExpression(GLSLParser.AssignmentExpressionContext ctx) {
    }

    @Override
    public void exitAssignmentExpression(GLSLParser.AssignmentExpressionContext ctx) {
    }

    @Override
    public void enterLengthAccessExpression(GLSLParser.LengthAccessExpressionContext ctx) {
    }

    @Override
    public void exitLengthAccessExpression(GLSLParser.LengthAccessExpressionContext ctx) {
    }

    @Override
    public void enterMultiplicativeExpression(GLSLParser.MultiplicativeExpressionContext ctx) {
    }

    @Override
    public void exitMultiplicativeExpression(GLSLParser.MultiplicativeExpressionContext ctx) {
    }

    @Override
    public void enterGroupingExpression(GLSLParser.GroupingExpressionContext ctx) {
    }

    @Override
    public void exitGroupingExpression(GLSLParser.GroupingExpressionContext ctx) {
    }

    @Override
    public void enterArrayAccessExpression(GLSLParser.ArrayAccessExpressionContext ctx) {
    }

    @Override
    public void exitArrayAccessExpression(GLSLParser.ArrayAccessExpressionContext ctx) {
    }

    @Override
    public void enterPrefixExpression(GLSLParser.PrefixExpressionContext ctx) {
    }

    @Override
    public void exitPrefixExpression(GLSLParser.PrefixExpressionContext ctx) {
    }

    @Override
    public void enterBitwiseInclusiveOrExpression(GLSLParser.BitwiseInclusiveOrExpressionContext ctx) {
    }

    @Override
    public void exitBitwiseInclusiveOrExpression(GLSLParser.BitwiseInclusiveOrExpressionContext ctx) {
    }

    @Override
    public void enterLogicalInclusiveOrExpression(GLSLParser.LogicalInclusiveOrExpressionContext ctx) {
    }

    @Override
    public void exitLogicalInclusiveOrExpression(GLSLParser.LogicalInclusiveOrExpressionContext ctx) {
    }

    @Override
    public void enterBitwiseAndExpression(GLSLParser.BitwiseAndExpressionContext ctx) {
    }

    @Override
    public void exitBitwiseAndExpression(GLSLParser.BitwiseAndExpressionContext ctx) {
    }

    @Override
    public void enterEqualityExpression(GLSLParser.EqualityExpressionContext ctx) {
    }

    @Override
    public void exitEqualityExpression(GLSLParser.EqualityExpressionContext ctx) {
    }

    @Override
    public void enterLogicalAndExpression(GLSLParser.LogicalAndExpressionContext ctx) {
    }

    @Override
    public void exitLogicalAndExpression(GLSLParser.LogicalAndExpressionContext ctx) {
    }

    @Override
    public void enterFunctionCallExpression(GLSLParser.FunctionCallExpressionContext ctx) {
    }

    @Override
    public void exitFunctionCallExpression(GLSLParser.FunctionCallExpressionContext ctx) {
    }

    @Override
    public void enterBitwiseExclusiveOrExpression(GLSLParser.BitwiseExclusiveOrExpressionContext ctx) {
    }

    @Override
    public void exitBitwiseExclusiveOrExpression(GLSLParser.BitwiseExclusiveOrExpressionContext ctx) {
    }

    @Override
    public void enterMemberAccessExpression(GLSLParser.MemberAccessExpressionContext ctx) {
    }

    @Override
    public void exitMemberAccessExpression(GLSLParser.MemberAccessExpressionContext ctx) {
    }

    @Override
    public void enterLiteralExpression(GLSLParser.LiteralExpressionContext ctx) {
    }

    @Override
    public void exitLiteralExpression(GLSLParser.LiteralExpressionContext ctx) {
    }

    @Override
    public void enterPostfixExpression(GLSLParser.PostfixExpressionContext ctx) {
    }

    @Override
    public void exitPostfixExpression(GLSLParser.PostfixExpressionContext ctx) {
    }

    @Override
    public void enterExpression(GLSLParser.ExpressionContext ctx) {
    }

    @Override
    public void exitExpression(GLSLParser.ExpressionContext ctx) {
    }

    @Override
    public void enterFunctionDeclaration(GLSLParser.FunctionDeclarationContext ctx) {
    }

    @Override
    public void exitFunctionDeclaration(GLSLParser.FunctionDeclarationContext ctx) {
    }

    @Override
    public void enterInterfaceBlockDeclaration(GLSLParser.InterfaceBlockDeclarationContext ctx) {
    }

    @Override
    public void exitInterfaceBlockDeclaration(GLSLParser.InterfaceBlockDeclarationContext ctx) {
    }

    @Override
    public void enterVariableDeclaration(GLSLParser.VariableDeclarationContext ctx) {
    }

    @Override
    public void exitVariableDeclaration(GLSLParser.VariableDeclarationContext ctx) {
    }

    @Override
    public void enterPrecisionDeclaration(GLSLParser.PrecisionDeclarationContext ctx) {
    }

    @Override
    public void exitPrecisionDeclaration(GLSLParser.PrecisionDeclarationContext ctx) {
    }

    @Override
    public void enterTypeAndInitDeclaration(GLSLParser.TypeAndInitDeclarationContext ctx) {
    }

    @Override
    public void exitTypeAndInitDeclaration(GLSLParser.TypeAndInitDeclarationContext ctx) {
    }

    @Override
    public void enterFunctionPrototype(GLSLParser.FunctionPrototypeContext ctx) {
    }

    @Override
    public void exitFunctionPrototype(GLSLParser.FunctionPrototypeContext ctx) {
    }

    @Override
    public void enterFunctionParameterList(GLSLParser.FunctionParameterListContext ctx) {
    }

    @Override
    public void exitFunctionParameterList(GLSLParser.FunctionParameterListContext ctx) {
    }

    @Override
    public void enterParameterDeclaration(GLSLParser.ParameterDeclarationContext ctx) {
    }

    @Override
    public void exitParameterDeclaration(GLSLParser.ParameterDeclarationContext ctx) {
    }

    @Override
    public void enterAttribute(GLSLParser.AttributeContext ctx) {
    }

    @Override
    public void exitAttribute(GLSLParser.AttributeContext ctx) {
    }

    @Override
    public void enterSingleAttribute(GLSLParser.SingleAttributeContext ctx) {
    }

    @Override
    public void exitSingleAttribute(GLSLParser.SingleAttributeContext ctx) {
    }

    @Override
    public void enterDeclarationMember(GLSLParser.DeclarationMemberContext ctx) {
    }

    @Override
    public void exitDeclarationMember(GLSLParser.DeclarationMemberContext ctx) {
    }

    @Override
    public void enterFullySpecifiedType(GLSLParser.FullySpecifiedTypeContext ctx) {
    }

    @Override
    public void exitFullySpecifiedType(GLSLParser.FullySpecifiedTypeContext ctx) {
    }

    @Override
    public void enterStorageQualifier(GLSLParser.StorageQualifierContext ctx) {
    }

    @Override
    public void exitStorageQualifier(GLSLParser.StorageQualifierContext ctx) {
    }

    @Override
    public void enterLayoutQualifier(GLSLParser.LayoutQualifierContext ctx) {
    }

    @Override
    public void exitLayoutQualifier(GLSLParser.LayoutQualifierContext ctx) {
    }

    @Override
    public void enterNamedLayoutQualifier(GLSLParser.NamedLayoutQualifierContext ctx) {
    }

    @Override
    public void exitNamedLayoutQualifier(GLSLParser.NamedLayoutQualifierContext ctx) {
    }

    @Override
    public void enterSharedLayoutQualifier(GLSLParser.SharedLayoutQualifierContext ctx) {
    }

    @Override
    public void exitSharedLayoutQualifier(GLSLParser.SharedLayoutQualifierContext ctx) {
    }

    @Override
    public void enterPrecisionQualifier(GLSLParser.PrecisionQualifierContext ctx) {
    }

    @Override
    public void exitPrecisionQualifier(GLSLParser.PrecisionQualifierContext ctx) {
    }

    @Override
    public void enterInterpolationQualifier(GLSLParser.InterpolationQualifierContext ctx) {
    }

    @Override
    public void exitInterpolationQualifier(GLSLParser.InterpolationQualifierContext ctx) {
    }

    @Override
    public void enterInvariantQualifier(GLSLParser.InvariantQualifierContext ctx) {
    }

    @Override
    public void exitInvariantQualifier(GLSLParser.InvariantQualifierContext ctx) {
    }

    @Override
    public void enterPreciseQualifier(GLSLParser.PreciseQualifierContext ctx) {
    }

    @Override
    public void exitPreciseQualifier(GLSLParser.PreciseQualifierContext ctx) {
    }

    @Override
    public void enterTypeQualifier(GLSLParser.TypeQualifierContext ctx) {
    }

    @Override
    public void exitTypeQualifier(GLSLParser.TypeQualifierContext ctx) {
    }

    @Override
    public void enterTypeSpecifier(GLSLParser.TypeSpecifierContext ctx) {
    }

    @Override
    public void exitTypeSpecifier(GLSLParser.TypeSpecifierContext ctx) {
    }

    @Override
    public void enterArraySpecifier(GLSLParser.ArraySpecifierContext ctx) {
    }

    @Override
    public void exitArraySpecifier(GLSLParser.ArraySpecifierContext ctx) {
    }

    @Override
    public void enterArraySpecifierSegment(GLSLParser.ArraySpecifierSegmentContext ctx) {
    }

    @Override
    public void exitArraySpecifierSegment(GLSLParser.ArraySpecifierSegmentContext ctx) {
    }

    @Override
    public void enterBuiltinTypeSpecifierParseable(GLSLParser.BuiltinTypeSpecifierParseableContext ctx) {
    }

    @Override
    public void exitBuiltinTypeSpecifierParseable(GLSLParser.BuiltinTypeSpecifierParseableContext ctx) {
    }

    @Override
    public void enterBuiltinTypeSpecifierFixed(GLSLParser.BuiltinTypeSpecifierFixedContext ctx) {
    }

    @Override
    public void exitBuiltinTypeSpecifierFixed(GLSLParser.BuiltinTypeSpecifierFixedContext ctx) {
    }

    @Override
    public void enterStructSpecifier(GLSLParser.StructSpecifierContext ctx) {
    }

    @Override
    public void exitStructSpecifier(GLSLParser.StructSpecifierContext ctx) {
    }

    @Override
    public void enterStructBody(GLSLParser.StructBodyContext ctx) {
    }

    @Override
    public void exitStructBody(GLSLParser.StructBodyContext ctx) {
    }

    @Override
    public void enterStructMember(GLSLParser.StructMemberContext ctx) {
    }

    @Override
    public void exitStructMember(GLSLParser.StructMemberContext ctx) {
    }

    @Override
    public void enterStructDeclarator(GLSLParser.StructDeclaratorContext ctx) {
    }

    @Override
    public void exitStructDeclarator(GLSLParser.StructDeclaratorContext ctx) {
    }

    @Override
    public void enterInitializer(GLSLParser.InitializerContext ctx) {
    }

    @Override
    public void exitInitializer(GLSLParser.InitializerContext ctx) {
    }

    @Override
    public void enterStatement(GLSLParser.StatementContext ctx) {
    }

    @Override
    public void exitStatement(GLSLParser.StatementContext ctx) {
    }

    @Override
    public void enterCompoundStatement(GLSLParser.CompoundStatementContext ctx) {
    }

    @Override
    public void exitCompoundStatement(GLSLParser.CompoundStatementContext ctx) {
    }

    @Override
    public void enterDeclarationStatement(GLSLParser.DeclarationStatementContext ctx) {
    }

    @Override
    public void exitDeclarationStatement(GLSLParser.DeclarationStatementContext ctx) {
    }

    @Override
    public void enterExpressionStatement(GLSLParser.ExpressionStatementContext ctx) {
    }

    @Override
    public void exitExpressionStatement(GLSLParser.ExpressionStatementContext ctx) {
    }

    @Override
    public void enterEmptyStatement(GLSLParser.EmptyStatementContext ctx) {
    }

    @Override
    public void exitEmptyStatement(GLSLParser.EmptyStatementContext ctx) {
    }

    @Override
    public void enterSelectionStatement(GLSLParser.SelectionStatementContext ctx) {
    }

    @Override
    public void exitSelectionStatement(GLSLParser.SelectionStatementContext ctx) {
    }

    @Override
    public void enterIterationCondition(GLSLParser.IterationConditionContext ctx) {
    }

    @Override
    public void exitIterationCondition(GLSLParser.IterationConditionContext ctx) {
    }

    @Override
    public void enterSwitchStatement(GLSLParser.SwitchStatementContext ctx) {
    }

    @Override
    public void exitSwitchStatement(GLSLParser.SwitchStatementContext ctx) {
    }

    @Override
    public void enterValuedCaseLabel(GLSLParser.ValuedCaseLabelContext ctx) {
    }

    @Override
    public void exitValuedCaseLabel(GLSLParser.ValuedCaseLabelContext ctx) {
    }

    @Override
    public void enterDefaultCaseLabel(GLSLParser.DefaultCaseLabelContext ctx) {
    }

    @Override
    public void exitDefaultCaseLabel(GLSLParser.DefaultCaseLabelContext ctx) {
    }

    @Override
    public void enterWhileStatement(GLSLParser.WhileStatementContext ctx) {
    }

    @Override
    public void exitWhileStatement(GLSLParser.WhileStatementContext ctx) {
    }

    @Override
    public void enterDoWhileStatement(GLSLParser.DoWhileStatementContext ctx) {
    }

    @Override
    public void exitDoWhileStatement(GLSLParser.DoWhileStatementContext ctx) {
    }

    @Override
    public void enterForStatement(GLSLParser.ForStatementContext ctx) {
    }

    @Override
    public void exitForStatement(GLSLParser.ForStatementContext ctx) {
    }

    @Override
    public void enterContinueStatement(GLSLParser.ContinueStatementContext ctx) {
    }

    @Override
    public void exitContinueStatement(GLSLParser.ContinueStatementContext ctx) {
    }

    @Override
    public void enterBreakStatement(GLSLParser.BreakStatementContext ctx) {
    }

    @Override
    public void exitBreakStatement(GLSLParser.BreakStatementContext ctx) {
    }

    @Override
    public void enterReturnStatement(GLSLParser.ReturnStatementContext ctx) {
    }

    @Override
    public void exitReturnStatement(GLSLParser.ReturnStatementContext ctx) {
    }

    @Override
    public void enterDiscardStatement(GLSLParser.DiscardStatementContext ctx) {
    }

    @Override
    public void exitDiscardStatement(GLSLParser.DiscardStatementContext ctx) {
    }

    @Override
    public void enterIgnoreIntersectionStatement(GLSLParser.IgnoreIntersectionStatementContext ctx) {
    }

    @Override
    public void exitIgnoreIntersectionStatement(GLSLParser.IgnoreIntersectionStatementContext ctx) {
    }

    @Override
    public void enterTerminateRayStatement(GLSLParser.TerminateRayStatementContext ctx) {
    }

    @Override
    public void exitTerminateRayStatement(GLSLParser.TerminateRayStatementContext ctx) {
    }

    @Override
    public void enterDemoteStatement(GLSLParser.DemoteStatementContext ctx) {
    }

    @Override
    public void exitDemoteStatement(GLSLParser.DemoteStatementContext ctx) {
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
    }

    @Override
    public void visitTerminal(TerminalNode node) {
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
    }
}

