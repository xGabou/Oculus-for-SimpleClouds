/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.traversal;

import io.github.douira.glsl_transformer.ast.node.IterationConditionInitializer;
import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
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
import io.github.douira.glsl_transformer.ast.node.external_declaration.DeclarationExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.external_declaration.ExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.external_declaration.FunctionDefinition;
import io.github.douira.glsl_transformer.ast.node.external_declaration.LayoutDefaults;
import io.github.douira.glsl_transformer.ast.node.statement.CompoundStatement;
import io.github.douira.glsl_transformer.ast.node.statement.ManyStatement;
import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.node.statement.loop.DoWhileLoopStatement;
import io.github.douira.glsl_transformer.ast.node.statement.loop.ForLoopStatement;
import io.github.douira.glsl_transformer.ast.node.statement.loop.LoopStatement;
import io.github.douira.glsl_transformer.ast.node.statement.loop.WhileLoopStatement;
import io.github.douira.glsl_transformer.ast.node.statement.selection.SelectionStatement;
import io.github.douira.glsl_transformer.ast.node.statement.selection.SwitchStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.CaseLabelStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.CaseStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.DeclarationStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.ExpressionStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.SemiTerminalStatement;
import io.github.douira.glsl_transformer.ast.node.type.FullySpecifiedType;
import io.github.douira.glsl_transformer.ast.node.type.initializer.ExpressionInitializer;
import io.github.douira.glsl_transformer.ast.node.type.initializer.Initializer;
import io.github.douira.glsl_transformer.ast.node.type.initializer.NestedInitializer;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.LayoutQualifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.LayoutQualifierPart;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.NamedLayoutQualifierPart;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.StorageQualifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.TypeQualifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.TypeQualifierPart;
import io.github.douira.glsl_transformer.ast.node.type.specifier.ArraySpecifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.FunctionPrototype;
import io.github.douira.glsl_transformer.ast.node.type.specifier.TypeReference;
import io.github.douira.glsl_transformer.ast.node.type.specifier.TypeSpecifier;
import io.github.douira.glsl_transformer.ast.node.type.struct.StructBody;
import io.github.douira.glsl_transformer.ast.node.type.struct.StructDeclarator;
import io.github.douira.glsl_transformer.ast.node.type.struct.StructMember;
import io.github.douira.glsl_transformer.ast.node.type.struct.StructSpecifier;
import io.github.douira.glsl_transformer.ast.traversal.GeneralASTListener;

public interface ASTListener
extends GeneralASTListener {
    default public void enterTranslationUnit(TranslationUnit node) {
    }

    default public void exitTranslationUnit(TranslationUnit node) {
    }

    default public void enterExternalDeclaration(ExternalDeclaration node) {
    }

    default public void exitExternalDeclaration(ExternalDeclaration node) {
    }

    default public void enterFunctionDefinition(FunctionDefinition node) {
    }

    default public void exitFunctionDefinition(FunctionDefinition node) {
    }

    default public void enterLayoutDefaults(LayoutDefaults node) {
    }

    default public void exitLayoutDefaults(LayoutDefaults node) {
    }

    default public void enterDeclarationExternalDeclaration(DeclarationExternalDeclaration node) {
    }

    default public void exitDeclarationExternalDeclaration(DeclarationExternalDeclaration node) {
    }

    default public void enterExpression(Expression node) {
    }

    default public void exitExpression(Expression node) {
    }

    default public void enterUnaryExpression(UnaryExpression node) {
    }

    default public void exitUnaryExpression(UnaryExpression node) {
    }

    default public void enterBitwiseNotExpression(BitwiseNotExpression node) {
    }

    default public void exitBitwiseNotExpression(BitwiseNotExpression node) {
    }

    default public void enterBooleanNotExpression(BooleanNotExpression node) {
    }

    default public void exitBooleanNotExpression(BooleanNotExpression node) {
    }

    default public void enterDecrementPostfixExpression(DecrementPostfixExpression node) {
    }

    default public void exitDecrementPostfixExpression(DecrementPostfixExpression node) {
    }

    default public void enterDecrementPrefixExpression(DecrementPrefixExpression node) {
    }

    default public void exitDecrementPrefixExpression(DecrementPrefixExpression node) {
    }

    default public void enterFunctionCallExpression(FunctionCallExpression node) {
    }

    default public void exitFunctionCallExpression(FunctionCallExpression node) {
    }

    default public void enterGroupingExpression(GroupingExpression node) {
    }

    default public void exitGroupingExpression(GroupingExpression node) {
    }

    default public void enterIncrementPostfixExpression(IncrementPostfixExpression node) {
    }

    default public void exitIncrementPostfixExpression(IncrementPostfixExpression node) {
    }

    default public void enterIncrementPrefixExpression(IncrementPrefixExpression node) {
    }

    default public void exitIncrementPrefixExpression(IncrementPrefixExpression node) {
    }

    default public void enterMemberAccessExpression(MemberAccessExpression node) {
    }

    default public void exitMemberAccessExpression(MemberAccessExpression node) {
    }

    default public void enterLengthAccessExpression(LengthAccessExpression node) {
    }

    default public void exitLengthAccessExpression(LengthAccessExpression node) {
    }

    default public void enterNegationExpression(NegationExpression node) {
    }

    default public void exitNegationExpression(NegationExpression node) {
    }

    default public void enterIdentityExpression(IdentityExpression node) {
    }

    default public void exitIdentityExpression(IdentityExpression node) {
    }

    default public void enterBinaryExpression(BinaryExpression node) {
    }

    default public void exitBinaryExpression(BinaryExpression node) {
    }

    default public void enterArrayAccessExpression(ArrayAccessExpression node) {
    }

    default public void exitArrayAccessExpression(ArrayAccessExpression node) {
    }

    default public void enterMultiplicationExpression(MultiplicationExpression node) {
    }

    default public void exitMultiplicationExpression(MultiplicationExpression node) {
    }

    default public void enterDivisionExpression(DivisionExpression node) {
    }

    default public void exitDivisionExpression(DivisionExpression node) {
    }

    default public void enterModuloExpression(ModuloExpression node) {
    }

    default public void exitModuloExpression(ModuloExpression node) {
    }

    default public void enterAdditionExpression(AdditionExpression node) {
    }

    default public void exitAdditionExpression(AdditionExpression node) {
    }

    default public void enterSubtractionExpression(SubtractionExpression node) {
    }

    default public void exitSubtractionExpression(SubtractionExpression node) {
    }

    default public void enterLeftShiftExpression(LeftShiftExpression node) {
    }

    default public void exitLeftShiftExpression(LeftShiftExpression node) {
    }

    default public void enterRightShiftExpression(RightShiftExpression node) {
    }

    default public void exitRightShiftExpression(RightShiftExpression node) {
    }

    default public void enterLessThanExpression(LessThanExpression node) {
    }

    default public void exitLessThanExpression(LessThanExpression node) {
    }

    default public void enterGreaterThanExpression(GreaterThanExpression node) {
    }

    default public void exitGreaterThanExpression(GreaterThanExpression node) {
    }

    default public void enterLessThanEqualExpression(LessThanEqualExpression node) {
    }

    default public void exitLessThanEqualExpression(LessThanEqualExpression node) {
    }

    default public void enterGreaterThanEqualExpression(GreaterThanEqualExpression node) {
    }

    default public void exitGreaterThanEqualExpression(GreaterThanEqualExpression node) {
    }

    default public void enterEqualityExpression(EqualityExpression node) {
    }

    default public void exitEqualityExpression(EqualityExpression node) {
    }

    default public void enterInequalityExpression(InequalityExpression node) {
    }

    default public void exitInequalityExpression(InequalityExpression node) {
    }

    default public void enterBitwiseAndExpression(BitwiseAndExpression node) {
    }

    default public void exitBitwiseAndExpression(BitwiseAndExpression node) {
    }

    default public void enterBitwiseXorExpression(BitwiseXorExpression node) {
    }

    default public void exitBitwiseXorExpression(BitwiseXorExpression node) {
    }

    default public void enterBitwiseOrExpression(BitwiseOrExpression node) {
    }

    default public void exitBitwiseOrExpression(BitwiseOrExpression node) {
    }

    default public void enterBooleanAndExpression(BooleanAndExpression node) {
    }

    default public void exitBooleanAndExpression(BooleanAndExpression node) {
    }

    default public void enterBooleanXorExpression(BooleanXorExpression node) {
    }

    default public void exitBooleanXorExpression(BooleanXorExpression node) {
    }

    default public void enterBooleanOrExpression(BooleanOrExpression node) {
    }

    default public void exitBooleanOrExpression(BooleanOrExpression node) {
    }

    default public void enterAssignmentExpression(AssignmentExpression node) {
    }

    default public void exitAssignmentExpression(AssignmentExpression node) {
    }

    default public void enterMultiplicationAssignmentExpression(MultiplicationAssignmentExpression node) {
    }

    default public void exitMultiplicationAssignmentExpression(MultiplicationAssignmentExpression node) {
    }

    default public void enterDivisionAssignmentExpression(DivisionAssignmentExpression node) {
    }

    default public void exitDivisionAssignmentExpression(DivisionAssignmentExpression node) {
    }

    default public void enterModuloAssignmentExpression(ModuloAssignmentExpression node) {
    }

    default public void exitModuloAssignmentExpression(ModuloAssignmentExpression node) {
    }

    default public void enterAdditionAssignmentExpression(AdditionAssignmentExpression node) {
    }

    default public void exitAdditionAssignmentExpression(AdditionAssignmentExpression node) {
    }

    default public void enterSubtractionAssignmentExpression(SubtractionAssignmentExpression node) {
    }

    default public void exitSubtractionAssignmentExpression(SubtractionAssignmentExpression node) {
    }

    default public void enterLeftShiftAssignmentExpression(LeftShiftAssignmentExpression node) {
    }

    default public void exitLeftShiftAssignmentExpression(LeftShiftAssignmentExpression node) {
    }

    default public void enterRightShiftAssignmentExpression(RightShiftAssignmentExpression node) {
    }

    default public void exitRightShiftAssignmentExpression(RightShiftAssignmentExpression node) {
    }

    default public void enterBitwiseAndAssignmentExpression(BitwiseAndAssignmentExpression node) {
    }

    default public void exitBitwiseAndAssignmentExpression(BitwiseAndAssignmentExpression node) {
    }

    default public void enterBitwiseXorAssignmentExpression(BitwiseXorAssignmentExpression node) {
    }

    default public void exitBitwiseXorAssignmentExpression(BitwiseXorAssignmentExpression node) {
    }

    default public void enterBitwiseOrAssignmentExpression(BitwiseOrAssignmentExpression node) {
    }

    default public void exitBitwiseOrAssignmentExpression(BitwiseOrAssignmentExpression node) {
    }

    default public void enterTernaryExpression(TernaryExpression node) {
    }

    default public void exitTernaryExpression(TernaryExpression node) {
    }

    default public void enterConditionExpression(ConditionExpression node) {
    }

    default public void exitConditionExpression(ConditionExpression node) {
    }

    default public void enterManyExpression(ManyExpression node) {
    }

    default public void exitManyExpression(ManyExpression node) {
    }

    default public void enterSequenceExpression(SequenceExpression node) {
    }

    default public void exitSequenceExpression(SequenceExpression node) {
    }

    default public void enterReferenceExpression(ReferenceExpression node) {
    }

    default public void exitReferenceExpression(ReferenceExpression node) {
    }

    default public void enterLiteralExpression(LiteralExpression node) {
    }

    default public void exitLiteralExpression(LiteralExpression node) {
    }

    default public void enterStatement(Statement node) {
    }

    default public void exitStatement(Statement node) {
    }

    default public void enterCompoundStatement(CompoundStatement node) {
    }

    default public void exitCompoundStatement(CompoundStatement node) {
    }

    default public void enterDeclarationStatement(DeclarationStatement node) {
    }

    default public void exitDeclarationStatement(DeclarationStatement node) {
    }

    default public void enterExpressionStatement(ExpressionStatement node) {
    }

    default public void exitExpressionStatement(ExpressionStatement node) {
    }

    default public void enterSelectionStatement(SelectionStatement node) {
    }

    default public void exitSelectionStatement(SelectionStatement node) {
    }

    default public void enterSwitchStatement(SwitchStatement node) {
    }

    default public void exitSwitchStatement(SwitchStatement node) {
    }

    default public void enterCaseLabelStatement(CaseLabelStatement node) {
    }

    default public void exitCaseLabelStatement(CaseLabelStatement node) {
    }

    default public void enterForLoopStatement(ForLoopStatement node) {
    }

    default public void exitForLoopStatement(ForLoopStatement node) {
    }

    default public void enterWhileLoopStatement(WhileLoopStatement node) {
    }

    default public void exitWhileLoopStatement(WhileLoopStatement node) {
    }

    default public void enterDoWhileLoopStatement(DoWhileLoopStatement node) {
    }

    default public void exitDoWhileLoopStatement(DoWhileLoopStatement node) {
    }

    default public void enterManyStatement(ManyStatement node) {
    }

    default public void exitManyStatement(ManyStatement node) {
    }

    default public void enterLoopStatement(LoopStatement node) {
    }

    default public void exitLoopStatement(LoopStatement node) {
    }

    default public void enterSemiTerminalStatement(SemiTerminalStatement node) {
    }

    default public void exitSemiTerminalStatement(SemiTerminalStatement node) {
    }

    default public void enterCaseStatement(CaseStatement node) {
    }

    default public void exitCaseStatement(CaseStatement node) {
    }

    default public void enterDeclaration(Declaration node) {
    }

    default public void exitDeclaration(Declaration node) {
    }

    default public void enterDeclarationMember(DeclarationMember node) {
    }

    default public void exitDeclarationMember(DeclarationMember node) {
    }

    default public void enterFunctionDeclaration(FunctionDeclaration node) {
    }

    default public void exitFunctionDeclaration(FunctionDeclaration node) {
    }

    default public void enterFunctionParameter(FunctionParameter node) {
    }

    default public void exitFunctionParameter(FunctionParameter node) {
    }

    default public void enterInterfaceBlockDeclaration(InterfaceBlockDeclaration node) {
    }

    default public void exitInterfaceBlockDeclaration(InterfaceBlockDeclaration node) {
    }

    default public void enterPrecisionDeclaration(PrecisionDeclaration node) {
    }

    default public void exitPrecisionDeclaration(PrecisionDeclaration node) {
    }

    default public void enterTypeAndInitDeclaration(TypeAndInitDeclaration node) {
    }

    default public void exitTypeAndInitDeclaration(TypeAndInitDeclaration node) {
    }

    default public void enterVariableDeclaration(VariableDeclaration node) {
    }

    default public void exitVariableDeclaration(VariableDeclaration node) {
    }

    default public void enterExpressionInitializer(ExpressionInitializer node) {
    }

    default public void exitExpressionInitializer(ExpressionInitializer node) {
    }

    default public void enterInitializer(Initializer node) {
    }

    default public void exitInitializer(Initializer node) {
    }

    default public void enterNestedInitializer(NestedInitializer node) {
    }

    default public void exitNestedInitializer(NestedInitializer node) {
    }

    default public void enterLayoutQualifier(LayoutQualifier node) {
    }

    default public void exitLayoutQualifier(LayoutQualifier node) {
    }

    default public void enterLayoutQualifierPart(LayoutQualifierPart node) {
    }

    default public void exitLayoutQualifierPart(LayoutQualifierPart node) {
    }

    default public void enterNamedLayoutQualifierPart(NamedLayoutQualifierPart node) {
    }

    default public void exitNamedLayoutQualifierPart(NamedLayoutQualifierPart node) {
    }

    default public void enterStorageQualifier(StorageQualifier node) {
    }

    default public void exitStorageQualifier(StorageQualifier node) {
    }

    default public void enterTypeQualifier(TypeQualifier node) {
    }

    default public void exitTypeQualifier(TypeQualifier node) {
    }

    default public void enterTypeQualifierPart(TypeQualifierPart node) {
    }

    default public void exitTypeQualifierPart(TypeQualifierPart node) {
    }

    default public void enterArraySpecifier(ArraySpecifier node) {
    }

    default public void exitArraySpecifier(ArraySpecifier node) {
    }

    default public void enterTypeReference(TypeReference node) {
    }

    default public void exitTypeReference(TypeReference node) {
    }

    default public void enterTypeSpecifier(TypeSpecifier node) {
    }

    default public void exitTypeSpecifier(TypeSpecifier node) {
    }

    default public void enterStructBody(StructBody node) {
    }

    default public void exitStructBody(StructBody node) {
    }

    default public void enterStructDeclarator(StructDeclarator node) {
    }

    default public void exitStructDeclarator(StructDeclarator node) {
    }

    default public void enterStructMember(StructMember node) {
    }

    default public void exitStructMember(StructMember node) {
    }

    default public void enterStructSpecifier(StructSpecifier node) {
    }

    default public void exitStructSpecifier(StructSpecifier node) {
    }

    default public void enterFullySpecifiedType(FullySpecifiedType node) {
    }

    default public void exitFullySpecifiedType(FullySpecifiedType node) {
    }

    default public void enterIterationConditionInitializer(IterationConditionInitializer node) {
    }

    default public void exitIterationConditionInitializer(IterationConditionInitializer node) {
    }

    default public void enterFunctionPrototype(FunctionPrototype node) {
    }

    default public void exitFunctionPrototype(FunctionPrototype node) {
    }
}

