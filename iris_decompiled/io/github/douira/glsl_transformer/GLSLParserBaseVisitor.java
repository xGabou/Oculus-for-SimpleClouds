/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer;

import io.github.douira.glsl_transformer.GLSLParser;
import io.github.douira.glsl_transformer.GLSLParserVisitor;
import oculus.org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

public class GLSLParserBaseVisitor<T>
extends AbstractParseTreeVisitor<T>
implements GLSLParserVisitor<T> {
    @Override
    public T visitTranslationUnit(GLSLParser.TranslationUnitContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitVersionStatement(GLSLParser.VersionStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitExternalDeclaration(GLSLParser.ExternalDeclarationContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitEmptyDeclaration(GLSLParser.EmptyDeclarationContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitPragmaDirective(GLSLParser.PragmaDirectiveContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitExtensionDirective(GLSLParser.ExtensionDirectiveContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitCustomDirective(GLSLParser.CustomDirectiveContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitIncludeDirective(GLSLParser.IncludeDirectiveContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitLayoutDefaults(GLSLParser.LayoutDefaultsContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitFunctionDefinition(GLSLParser.FunctionDefinitionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitShiftExpression(GLSLParser.ShiftExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitReferenceExpression(GLSLParser.ReferenceExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitAdditiveExpression(GLSLParser.AdditiveExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitRelationalExpression(GLSLParser.RelationalExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitLogicalExclusiveOrExpression(GLSLParser.LogicalExclusiveOrExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitConditionalExpression(GLSLParser.ConditionalExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitAssignmentExpression(GLSLParser.AssignmentExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitLengthAccessExpression(GLSLParser.LengthAccessExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitMultiplicativeExpression(GLSLParser.MultiplicativeExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitGroupingExpression(GLSLParser.GroupingExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitArrayAccessExpression(GLSLParser.ArrayAccessExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitPrefixExpression(GLSLParser.PrefixExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitBitwiseInclusiveOrExpression(GLSLParser.BitwiseInclusiveOrExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitLogicalInclusiveOrExpression(GLSLParser.LogicalInclusiveOrExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitBitwiseAndExpression(GLSLParser.BitwiseAndExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitEqualityExpression(GLSLParser.EqualityExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitLogicalAndExpression(GLSLParser.LogicalAndExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitFunctionCallExpression(GLSLParser.FunctionCallExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitBitwiseExclusiveOrExpression(GLSLParser.BitwiseExclusiveOrExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitMemberAccessExpression(GLSLParser.MemberAccessExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitLiteralExpression(GLSLParser.LiteralExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitPostfixExpression(GLSLParser.PostfixExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitExpression(GLSLParser.ExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitFunctionDeclaration(GLSLParser.FunctionDeclarationContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitInterfaceBlockDeclaration(GLSLParser.InterfaceBlockDeclarationContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitVariableDeclaration(GLSLParser.VariableDeclarationContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitPrecisionDeclaration(GLSLParser.PrecisionDeclarationContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitTypeAndInitDeclaration(GLSLParser.TypeAndInitDeclarationContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitFunctionPrototype(GLSLParser.FunctionPrototypeContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitFunctionParameterList(GLSLParser.FunctionParameterListContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitParameterDeclaration(GLSLParser.ParameterDeclarationContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitAttribute(GLSLParser.AttributeContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitSingleAttribute(GLSLParser.SingleAttributeContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitDeclarationMember(GLSLParser.DeclarationMemberContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitFullySpecifiedType(GLSLParser.FullySpecifiedTypeContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitStorageQualifier(GLSLParser.StorageQualifierContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitLayoutQualifier(GLSLParser.LayoutQualifierContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitNamedLayoutQualifier(GLSLParser.NamedLayoutQualifierContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitSharedLayoutQualifier(GLSLParser.SharedLayoutQualifierContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitPrecisionQualifier(GLSLParser.PrecisionQualifierContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitInterpolationQualifier(GLSLParser.InterpolationQualifierContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitInvariantQualifier(GLSLParser.InvariantQualifierContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitPreciseQualifier(GLSLParser.PreciseQualifierContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitTypeQualifier(GLSLParser.TypeQualifierContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitTypeSpecifier(GLSLParser.TypeSpecifierContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitArraySpecifier(GLSLParser.ArraySpecifierContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitArraySpecifierSegment(GLSLParser.ArraySpecifierSegmentContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitBuiltinTypeSpecifierParseable(GLSLParser.BuiltinTypeSpecifierParseableContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitBuiltinTypeSpecifierFixed(GLSLParser.BuiltinTypeSpecifierFixedContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitStructSpecifier(GLSLParser.StructSpecifierContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitStructBody(GLSLParser.StructBodyContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitStructMember(GLSLParser.StructMemberContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitStructDeclarator(GLSLParser.StructDeclaratorContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitInitializer(GLSLParser.InitializerContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitStatement(GLSLParser.StatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitCompoundStatement(GLSLParser.CompoundStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitDeclarationStatement(GLSLParser.DeclarationStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitExpressionStatement(GLSLParser.ExpressionStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitEmptyStatement(GLSLParser.EmptyStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitSelectionStatement(GLSLParser.SelectionStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitIterationCondition(GLSLParser.IterationConditionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitSwitchStatement(GLSLParser.SwitchStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitValuedCaseLabel(GLSLParser.ValuedCaseLabelContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitDefaultCaseLabel(GLSLParser.DefaultCaseLabelContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitWhileStatement(GLSLParser.WhileStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitDoWhileStatement(GLSLParser.DoWhileStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitForStatement(GLSLParser.ForStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitContinueStatement(GLSLParser.ContinueStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitBreakStatement(GLSLParser.BreakStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitReturnStatement(GLSLParser.ReturnStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitDiscardStatement(GLSLParser.DiscardStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitIgnoreIntersectionStatement(GLSLParser.IgnoreIntersectionStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitTerminateRayStatement(GLSLParser.TerminateRayStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitDemoteStatement(GLSLParser.DemoteStatementContext ctx) {
        return this.visitChildren(ctx);
    }
}

