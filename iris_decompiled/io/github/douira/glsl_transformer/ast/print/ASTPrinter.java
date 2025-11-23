/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.print;

import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.IterationConditionInitializer;
import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.node.VersionStatement;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
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
import io.github.douira.glsl_transformer.ast.node.expression.SequenceExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.AdditionAssignmentExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.AdditionExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.ArrayAccessExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.AssignmentExpression;
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
import io.github.douira.glsl_transformer.ast.node.external_declaration.CustomDirective;
import io.github.douira.glsl_transformer.ast.node.external_declaration.EmptyDeclaration;
import io.github.douira.glsl_transformer.ast.node.external_declaration.ExtensionDirective;
import io.github.douira.glsl_transformer.ast.node.external_declaration.ExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.external_declaration.FunctionDefinition;
import io.github.douira.glsl_transformer.ast.node.external_declaration.IncludeDirective;
import io.github.douira.glsl_transformer.ast.node.external_declaration.LayoutDefaults;
import io.github.douira.glsl_transformer.ast.node.external_declaration.PragmaDirective;
import io.github.douira.glsl_transformer.ast.node.statement.CompoundStatement;
import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.node.statement.loop.DoWhileLoopStatement;
import io.github.douira.glsl_transformer.ast.node.statement.loop.ForLoopStatement;
import io.github.douira.glsl_transformer.ast.node.statement.loop.WhileLoopStatement;
import io.github.douira.glsl_transformer.ast.node.statement.selection.SelectionStatement;
import io.github.douira.glsl_transformer.ast.node.statement.selection.SwitchStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.BreakStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.CaseLabelStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.CaseStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.ContinueStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.DefaultStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.DemoteStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.DiscardStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.EmptyStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.ExpressionStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.IgnoreIntersectionStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.ReturnStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.TerminateRayStatement;
import io.github.douira.glsl_transformer.ast.node.type.FullySpecifiedType;
import io.github.douira.glsl_transformer.ast.node.type.initializer.NestedInitializer;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.InterpolationQualifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.InvariantQualifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.LayoutQualifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.NamedLayoutQualifierPart;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.PreciseQualifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.PrecisionQualifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.SharedLayoutQualifierPart;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.StorageQualifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.TypeQualifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.ArraySpecifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.BuiltinFixedTypeSpecifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.BuiltinNumericTypeSpecifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.FunctionPrototype;
import io.github.douira.glsl_transformer.ast.node.type.specifier.TypeSpecifier;
import io.github.douira.glsl_transformer.ast.node.type.struct.StructBody;
import io.github.douira.glsl_transformer.ast.node.type.struct.StructDeclarator;
import io.github.douira.glsl_transformer.ast.node.type.struct.StructMember;
import io.github.douira.glsl_transformer.ast.node.type.struct.StructSpecifier;
import io.github.douira.glsl_transformer.ast.print.ASTPrinterBase;
import io.github.douira.glsl_transformer.ast.print.PrintType;
import io.github.douira.glsl_transformer.ast.print.TokenProcessor;
import io.github.douira.glsl_transformer.ast.print.token.EOFToken;
import io.github.douira.glsl_transformer.util.Type;
import java.util.ArrayDeque;
import java.util.Deque;

public class ASTPrinter
extends ASTPrinterBase {
    private final Deque<Expression> precedenceWrapped = new ArrayDeque<Expression>();

    public ASTPrinter(TokenProcessor tokenProcessor) {
        super(tokenProcessor);
    }

    public static String printAST(TokenProcessor tokenProcessor, ASTNode node) {
        ASTPrinter printer = new ASTPrinter(tokenProcessor);
        printer.startVisit(node);
        printer.finalizePrinting();
        return printer.generateString();
    }

    public static String print(PrintType type, ASTNode node) {
        return ASTPrinter.printAST(type.getTokenProcessor(), node);
    }

    public static String printSimple(ASTNode node) {
        return ASTPrinter.print(PrintType.SIMPLE, node);
    }

    public static String printIndented(ASTNode node) {
        return ASTPrinter.print(PrintType.INDENTED, node);
    }

    public static String printCompact(ASTNode node) {
        return ASTPrinter.print(PrintType.COMPACT, node);
    }

    public static String printIndentedAnnotated(ASTNode node) {
        return ASTPrinter.print(PrintType.INDENTED_ANNOTATED, node);
    }

    @Override
    public Void startVisit(ASTNode node) {
        this.precedenceWrapped.clear();
        return (Void)super.startVisit(node);
    }

    @Override
    public Void visitTranslationUnit(TranslationUnit node) {
        this.visitSafe(node.getVersionStatement());
        this.emitLiteralSafe(node.outputOptions.getPrintHeader());
        this.visitChildren(node);
        this.emitToken(new EOFToken());
        return null;
    }

    @Override
    public Void visitVersionStatement(VersionStatement node) {
        this.emitType(265, 273);
        this.emitExtendableSpace();
        this.emitType(node.version.tokenType);
        if (node.profile != null) {
            this.emitExtendableSpace();
            this.emitType(node.profile.tokenType);
        }
        this.emitExactNewline();
        return null;
    }

    @Override
    public void enterExternalDeclaration(ExternalDeclaration node) {
        this.emitLineDirective(node.getSourceLocation());
        super.enterExternalDeclaration(node);
    }

    @Override
    public void enterStatement(Statement node) {
        this.emitLineDirective(node.getSourceLocation());
        super.visitStatement(node);
    }

    @Override
    public Void visitFunctionDefinition(FunctionDefinition node) {
        this.visit(node.getFunctionPrototype());
        this.emitBreakableSpace();
        this.visit(node.getBody());
        return null;
    }

    @Override
    public Void visitEmptyDeclaration(EmptyDeclaration node) {
        this.emitStatementEnd();
        return null;
    }

    @Override
    public Void visitPragmaDirective(PragmaDirective node) {
        this.emitType(265, 276);
        this.emitExtendableSpace();
        if (node.stdGL) {
            this.emitType(290);
            this.emitExtendableSpace();
        }
        if (node.type == PragmaDirective.PragmaType.CUSTOM) {
            this.emitLiteral(node.getCustomName());
        } else {
            this.emitType(node.type.tokenType, 288, node.state.tokenType, 289);
        }
        this.emitExactNewline();
        return null;
    }

    @Override
    public Void visitExtensionDirective(ExtensionDirective node) {
        this.emitType(265, 272);
        this.emitExtendableSpace();
        this.emitLiteral(node.getName());
        if (node.behavior != null) {
            this.emitType(287);
            this.emitExtendableSpace();
            this.emitType(node.behavior.tokenType);
        }
        this.emitExactNewline();
        return null;
    }

    @Override
    public Void visitCustomDirective(CustomDirective node) {
        TranslationUnit translationUnitParent = node.getAncestor(TranslationUnit.class);
        if (translationUnitParent != null && !translationUnitParent.outputOptions.printCustomDirectives) {
            return null;
        }
        this.emitType(265, 274);
        String content = node.getContent();
        if (content != null) {
            this.emitExtendableSpace();
            this.emitLiteral(content);
        }
        this.emitExactNewline();
        return null;
    }

    @Override
    public Void visitIncludeDirective(IncludeDirective node) {
        this.emitType(265, 275);
        this.emitExtendableSpace();
        this.emitType(node.isAngleBrackets ? 312 : 311);
        String content = node.getContent();
        if (content != null) {
            this.emitLiteral(content);
        }
        this.emitType(node.isAngleBrackets ? 323 : 321);
        this.emitExactNewline();
        return null;
    }

    @Override
    public void exitLayoutDefaults(LayoutDefaults node) {
        this.emitType(node.mode.tokenType);
        this.emitBreakableSpace();
        this.emitStatementEnd();
    }

    @Override
    public void enterExpression(Expression node) {
        ASTNode aSTNode = node.getParent();
        if (aSTNode instanceof Expression) {
            ArrayAccessExpression access;
            Expression parent = (Expression)aSTNode;
            Expression.ExpressionType parentType = parent.getExpressionType();
            Expression.ExpressionType ownType = node.getExpressionType();
            if (!(parentType == Expression.ExpressionType.GROUPING || ownType == Expression.ExpressionType.GROUPING || parentType.precedence >= ownType.precedence || parentType.operandStructure != Expression.ExpressionType.OperandStructure.UNARY && parentType.operandStructure != Expression.ExpressionType.OperandStructure.BINARY && parentType.operandStructure != Expression.ExpressionType.OperandStructure.TERNARY && parentType != Expression.ExpressionType.SEQUENCE || parent instanceof ArrayAccessExpression && (access = (ArrayAccessExpression)parent).getRight() == node)) {
                this.emitType(288);
                this.precedenceWrapped.add(node);
            }
        }
    }

    @Override
    public void exitExpression(Expression node) {
        if (this.precedenceWrapped.peekLast() == node) {
            this.emitType(289);
            this.precedenceWrapped.removeLast();
        }
    }

    @Override
    public void enterBitwiseNotExpression(BitwiseNotExpression node) {
        this.emitType(251);
    }

    @Override
    public void enterBooleanNotExpression(BooleanNotExpression node) {
        this.emitType(250);
    }

    @Override
    public void enterDecrementPrefixExpression(DecrementPrefixExpression node) {
        this.emitType(249, 249);
    }

    @Override
    public Void visitGroupingExpression(GroupingExpression node) {
        Expression operand = node.getOperand();
        if (operand.getExpressionType() == Expression.ExpressionType.GROUPING) {
            this.visit(operand);
        } else {
            this.emitType(239);
            this.visit(operand);
            this.emitType(240);
        }
        return null;
    }

    @Override
    public void enterIncrementPrefixExpression(IncrementPrefixExpression node) {
        this.emitType(248, 248);
    }

    @Override
    public Void visitNegationExpression(NegationExpression node) {
        if (node.getParent() instanceof NegationExpression) {
            this.emitBreakableSpace();
        }
        this.emitType(249);
        this.visit(node.getOperand());
        return null;
    }

    @Override
    public void enterIdentityExpression(IdentityExpression node) {
        this.emitType(248);
    }

    @Override
    public void exitDecrementPostfixExpression(DecrementPostfixExpression node) {
        this.emitType(249, 249);
    }

    @Override
    public void exitIncrementPostfixExpression(IncrementPostfixExpression node) {
        this.emitType(248, 248);
    }

    @Override
    public Void visitFunctionCallExpression(FunctionCallExpression node) {
        this.visit(node.getReference());
        this.emitType(239);
        this.visitCommaSpaced(node.getParameters());
        this.emitType(240);
        return null;
    }

    @Override
    public Void visitMemberAccessExpression(MemberAccessExpression node) {
        this.visit(node.getOperand());
        this.emitType(247);
        this.visit(node.getMember());
        return null;
    }

    @Override
    public void exitLengthAccessExpression(LengthAccessExpression node) {
        this.emitType(22);
    }

    @Override
    public Void visitConditionExpression(ConditionExpression node) {
        this.visit(node.getCondition());
        this.emitBreakableSpace();
        this.emitType(260);
        this.emitExtendableSpace();
        this.visit(node.getTrueExpression());
        this.emitBreakableSpace();
        this.emitType(1);
        this.emitExtendableSpace();
        this.visit(node.getFalseExpression());
        return null;
    }

    @Override
    public Void visitSequenceExpression(SequenceExpression node) {
        this.visitCommaSpaced(node.getExpressions());
        return null;
    }

    @Override
    public Void visitLiteralExpression(LiteralExpression node) {
        Type.NumberType numberType = node.getNumberType();
        block0 : switch (numberType) {
            case BOOLEAN: {
                this.emitLiteral(node.getBoolean() ? "true" : "false");
                break;
            }
            case SIGNED_INTEGER: 
            case UNSIGNED_INTEGER: {
                Object intString;
                int radix = node.getIntegerRadix();
                long integer = node.getInteger();
                boolean unsignedInt = numberType == Type.NumberType.UNSIGNED_INTEGER;
                Object object = intString = unsignedInt ? Long.toUnsignedString(integer, radix) : Long.toString(integer, radix);
                if (radix != 10) {
                    boolean negative = !unsignedInt && integer < 0L;
                    char sign = ((String)intString).charAt(0);
                    if (negative) {
                        intString = ((String)intString).substring(1);
                    }
                    if (radix == 16) {
                        intString = "0x" + (String)intString;
                    } else if (radix == 8) {
                        intString = "0" + (String)intString;
                    }
                    if (negative) {
                        this.emitBreakableSpace();
                        intString = sign + (String)intString;
                    }
                }
                switch (node.getType()) {
                    case INT16: {
                        this.emitLiteral((String)intString + "s");
                        break block0;
                    }
                    case UINT16: {
                        this.emitLiteral((String)intString + "us");
                        break block0;
                    }
                    case INT32: {
                        this.emitLiteral((String)intString);
                        break block0;
                    }
                    case UINT32: {
                        this.emitLiteral((String)intString + "u");
                        break block0;
                    }
                    case INT64: {
                        this.emitLiteral((String)intString + "l");
                        break block0;
                    }
                    case UINT64: {
                        this.emitLiteral((String)intString + "ul");
                        break block0;
                    }
                }
                throw new IllegalStateException("Unexpected int type: " + node.getType());
            }
            case FLOATING_POINT: {
                switch (node.getType()) {
                    case FLOAT16: {
                        this.emitLiteral(Double.toString(node.getFloating()) + "hf");
                        break block0;
                    }
                    case FLOAT32: {
                        this.emitLiteral(Double.toString(node.getFloating()) + "f");
                        break block0;
                    }
                    case FLOAT64: {
                        this.emitLiteral(Double.toString(node.getFloating()) + "lf");
                        break block0;
                    }
                }
                throw new IllegalStateException("Unexpected float type: " + node.getType());
            }
        }
        return null;
    }

    @Override
    public Void visitArrayAccessExpression(ArrayAccessExpression node) {
        this.visit(node.getLeft());
        this.emitType(244);
        this.visit(node.getRight());
        this.emitType(245);
        return null;
    }

    @Override
    public Void visitMultiplicationExpression(MultiplicationExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(252);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitDivisionExpression(DivisionExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(253);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitModuloExpression(ModuloExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(254);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitAdditionExpression(AdditionExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(248);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitSubtractionExpression(SubtractionExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(249);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitLeftShiftExpression(LeftShiftExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(220);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitRightShiftExpression(RightShiftExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(221);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitLessThanExpression(LessThanExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(255);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitGreaterThanExpression(GreaterThanExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(256);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitLessThanEqualExpression(LessThanEqualExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(222);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitGreaterThanEqualExpression(GreaterThanEqualExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(223);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitEqualityExpression(EqualityExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(224);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitInequalityExpression(InequalityExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(225);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitBitwiseAndExpression(BitwiseAndExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(257);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitBitwiseXorExpression(BitwiseXorExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(259);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitBitwiseOrExpression(BitwiseOrExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(258);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitBooleanAndExpression(BooleanAndExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(226);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitBooleanXorExpression(BooleanXorExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(227);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitBooleanOrExpression(BooleanOrExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(228);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitAssignmentExpression(AssignmentExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(261);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitMultiplicationAssignmentExpression(MultiplicationAssignmentExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(229);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitDivisionAssignmentExpression(DivisionAssignmentExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(230);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitModuloAssignmentExpression(ModuloAssignmentExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(231);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitAdditionAssignmentExpression(AdditionAssignmentExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(232);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitSubtractionAssignmentExpression(SubtractionAssignmentExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(233);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitLeftShiftAssignmentExpression(LeftShiftAssignmentExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(234);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitRightShiftAssignmentExpression(RightShiftAssignmentExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(235);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitBitwiseAndAssignmentExpression(BitwiseAndAssignmentExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(236);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitBitwiseXorAssignmentExpression(BitwiseXorAssignmentExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(237);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitBitwiseOrAssignmentExpression(BitwiseOrAssignmentExpression node) {
        this.visit(node.getLeft());
        this.emitBreakableSpace();
        this.emitType(238);
        this.emitBreakableSpace();
        this.visit(node.getRight());
        return null;
    }

    @Override
    public Void visitEmptyStatement(EmptyStatement node) {
        this.emitStatementEnd();
        return null;
    }

    @Override
    public void enterCompoundStatement(CompoundStatement node) {
        this.emitType(241);
        this.emitCommonNewline();
        this.indent();
        if (node.getParent() instanceof SwitchStatement) {
            this.indent();
        }
    }

    @Override
    public void exitCompoundStatement(CompoundStatement node) {
        this.unindent();
        if (node.getParent() instanceof SwitchStatement) {
            this.unindent();
        }
        this.emitType(242);
        this.emitCommonNewline();
    }

    @Override
    public void exitExpressionStatement(ExpressionStatement node) {
        this.emitStatementEnd();
    }

    @Override
    public Void visitSelectionStatement(SelectionStatement node) {
        this.emitType(46);
        this.emitExtendableSpace();
        this.emitType(239);
        this.visit(node.getCondition());
        this.emitType(240);
        this.emitBreakableSpace();
        this.visit(node.getIfTrue());
        if (node.hasIfFalse()) {
            this.compactCommonNewline(CompoundStatement.class);
            this.emitType(47);
            this.emitBreakableSpace();
            this.visit(node.getIfFalse());
        }
        return null;
    }

    @Override
    public Void visitSwitchStatement(SwitchStatement node) {
        this.emitType(48);
        this.emitExtendableSpace();
        this.emitType(239);
        this.visit(node.getExpression());
        this.emitType(240);
        this.emitBreakableSpace();
        this.visit(node.getStatement());
        return null;
    }

    @Override
    public void enterCaseLabelStatement(CaseLabelStatement node) {
        this.unindent();
    }

    @Override
    public void exitCaseLabelStatement(CaseLabelStatement node) {
        this.indent();
    }

    @Override
    public Void visitCaseStatement(CaseStatement node) {
        this.emitType(49);
        this.emitBreakableSpace();
        this.visit(node.getExpression());
        this.emitType(1);
        this.emitCommonNewline();
        return null;
    }

    @Override
    public Void visitDefaultStatement(DefaultStatement node) {
        this.emitType(50, 1);
        this.emitCommonNewline();
        return null;
    }

    private void visitLoopBody(Statement statement) {
        if (!(statement instanceof EmptyStatement)) {
            this.emitBreakableSpace();
        }
        this.visit(statement);
    }

    @Override
    public Void visitForLoopStatement(ForLoopStatement node) {
        this.emitType(53);
        this.emitBreakableSpace();
        this.emitType(239);
        if (this.visitSafe(node.getInitDeclaration())) {
            this.compactCommonNewline();
        } else {
            if (!this.visitSafe(node.getInitExpression())) {
                this.emitExactSpace();
            }
            this.emitType(243);
            this.emitBreakableSpace();
        }
        if (!this.visitSafe(node.getCondition())) {
            this.visitSafe(node.getIterationConditionInitializer());
        }
        this.emitType(243);
        this.emitBreakableSpace();
        this.visitSafe(node.getIncrementer());
        this.emitType(240);
        this.visitLoopBody(node.getStatement());
        return null;
    }

    @Override
    public Void visitWhileLoopStatement(WhileLoopStatement node) {
        this.emitType(51);
        this.emitBreakableSpace();
        this.emitType(239);
        if (!this.visitSafe(node.getCondition())) {
            this.visitSafe(node.getIterationConditionInitializer());
        }
        this.emitType(240);
        this.visitLoopBody(node.getStatement());
        return null;
    }

    @Override
    public Void visitDoWhileLoopStatement(DoWhileLoopStatement node) {
        this.emitType(52);
        this.visitLoopBody(node.getStatement());
        this.compactCommonNewline(CompoundStatement.class);
        this.emitType(51);
        this.emitBreakableSpace();
        this.emitType(239);
        this.visit(node.getCondition());
        this.emitType(240);
        this.emitStatementEnd();
        return null;
    }

    @Override
    public Void visitContinueStatement(ContinueStatement node) {
        this.emitType(54);
        this.emitStatementEnd();
        return null;
    }

    @Override
    public Void visitBreakStatement(BreakStatement node) {
        this.emitType(55);
        this.emitStatementEnd();
        return null;
    }

    @Override
    public Void visitReturnStatement(ReturnStatement node) {
        this.emitType(56);
        if (node.getExpression() != null) {
            this.emitBreakableSpace();
            this.visit(node.getExpression());
        }
        this.emitStatementEnd();
        return null;
    }

    @Override
    public Void visitDiscardStatement(DiscardStatement node) {
        this.emitType(57);
        this.emitStatementEnd();
        return null;
    }

    @Override
    public Void visitIgnoreIntersectionStatement(IgnoreIntersectionStatement node) {
        this.emitType(41);
        this.emitStatementEnd();
        return null;
    }

    @Override
    public Void visitTerminateRayStatement(TerminateRayStatement node) {
        this.emitType(42);
        this.emitStatementEnd();
        return null;
    }

    @Override
    public Void visitDemoteStatement(DemoteStatement node) {
        this.emitType(58);
        this.emitStatementEnd();
        return null;
    }

    @Override
    public Void visitDeclarationMember(DeclarationMember node) {
        this.visit(node.getName());
        this.visitSafe(node.getArraySpecifier());
        if (node.getInitializer() != null) {
            this.emitBreakableSpace();
            this.emitType(261);
            this.emitBreakableSpace();
            this.visit(node.getInitializer());
        }
        return null;
    }

    @Override
    public Void visitFunctionPrototype(FunctionPrototype node) {
        this.visit(node.getReturnType());
        this.emitBreakableSpace();
        this.visit(node.getName());
        this.emitType(239);
        if (node.getParameters().size() >= 7) {
            this.emitCommonNewline();
            this.indent();
            this.visitWithSeparator(node.getParameters(), () -> {
                this.emitType(246);
                this.emitCommonNewline();
            });
            this.emitCommonNewline();
            this.unindent();
        } else {
            this.visitCommaSpaced(node.getParameters());
        }
        this.emitType(240);
        return null;
    }

    @Override
    public Void visitFunctionParameter(FunctionParameter node) {
        this.visit(node.getType());
        if (node.getName() != null) {
            this.emitBreakableSpace();
            this.visit(node.getName());
            if (node.getArraySpecifier() != null) {
                this.visit(node.getArraySpecifier());
            }
        }
        return null;
    }

    @Override
    public void exitFunctionDeclaration(FunctionDeclaration node) {
        this.emitStatementEnd();
    }

    @Override
    public Void visitInterfaceBlockDeclaration(InterfaceBlockDeclaration node) {
        this.visit(node.getTypeQualifier());
        this.emitBreakableSpace();
        this.visit(node.getBlockName());
        this.emitBreakableSpace();
        this.visit(node.getStructBody());
        if (node.getVariableName() != null) {
            this.emitBreakableSpace();
            this.visit(node.getVariableName());
            this.visitSafe(node.getArraySpecifier());
        }
        this.emitStatementEnd();
        return null;
    }

    @Override
    public Void visitPrecisionDeclaration(PrecisionDeclaration node) {
        this.emitType(10);
        this.emitBreakableSpace();
        this.visit(node.getPrecisionQualifier());
        this.emitBreakableSpace();
        this.visit(node.getTypeSpecifier());
        this.emitStatementEnd();
        return null;
    }

    @Override
    public Void visitTypeAndInitDeclaration(TypeAndInitDeclaration node) {
        this.visit(node.getType());
        if (!node.getMembers().isEmpty()) {
            this.emitBreakableSpace();
            this.visitCommaSpaced(node.getMembers());
        }
        this.emitStatementEnd();
        return null;
    }

    @Override
    public Void visitVariableDeclaration(VariableDeclaration node) {
        this.visit(node.getTypeQualifier());
        if (!node.getNames().isEmpty()) {
            this.emitBreakableSpace();
            this.visitCommaSpaced(node.getNames());
        }
        this.emitStatementEnd();
        return null;
    }

    @Override
    public Void visitNestedInitializer(NestedInitializer node) {
        this.emitType(241);
        this.emitBreakableSpace();
        this.visitCommaSpaced(node.getInitializers());
        this.emitBreakableSpace();
        this.emitType(242);
        return null;
    }

    @Override
    public Void visitInterpolationQualifier(InterpolationQualifier node) {
        this.emitType(node.interpolationType.tokenType);
        return null;
    }

    @Override
    public Void visitInvariantQualifier(InvariantQualifier node) {
        this.emitType(13);
        return null;
    }

    @Override
    public Void visitLayoutQualifier(LayoutQualifier node) {
        this.emitType(21, 239);
        this.visitCommaSpaced(node.getParts());
        this.emitType(240);
        return null;
    }

    @Override
    public Void visitNamedLayoutQualifierPart(NamedLayoutQualifierPart node) {
        this.visit(node.getName());
        if (node.getExpression() != null) {
            this.emitBreakableSpace();
            this.emitType(261);
            this.emitBreakableSpace();
            this.visit(node.getExpression());
        }
        return null;
    }

    @Override
    public Void visitSharedLayoutQualifierPart(SharedLayoutQualifierPart node) {
        this.emitType(20);
        return null;
    }

    @Override
    public Void visitPreciseQualifier(PreciseQualifier node) {
        this.emitType(12);
        return null;
    }

    @Override
    public Void visitPrecisionQualifier(PrecisionQualifier node) {
        this.emitType(node.precisionLevel.tokenType);
        return null;
    }

    @Override
    public Void visitStorageQualifier(StorageQualifier node) {
        this.emitType(node.storageType.tokenType);
        if (node.getTypeNames() != null) {
            this.emitType(239);
            this.visitCommaSpaced(node.getTypeNames());
            this.emitType(240);
        }
        return null;
    }

    @Override
    public Void visitTypeQualifier(TypeQualifier node) {
        this.visitWithSeparator(node.getParts(), this::emitBreakableSpace);
        return null;
    }

    @Override
    public Void visitArraySpecifier(ArraySpecifier node) {
        for (Expression dimension : node.getDimensions()) {
            this.emitType(244);
            this.visitSafe(dimension);
            this.emitType(245);
        }
        return null;
    }

    @Override
    public void exitTypeSpecifier(TypeSpecifier node) {
        this.visitSafe(node.getArraySpecifier());
    }

    @Override
    public Void visitBuiltinFixedTypeSpecifier(BuiltinFixedTypeSpecifier node) {
        this.emitType(node.type.tokenType);
        return null;
    }

    @Override
    public Void visitBuiltinNumericTypeSpecifier(BuiltinNumericTypeSpecifier node) {
        this.emitLiteral(node.type.getMostCompactName());
        return null;
    }

    @Override
    public Void visitStructBody(StructBody node) {
        this.emitType(241);
        if (node.getMembers().size() <= 1) {
            this.emitBreakableSpace();
            this.visitWithSeparator(node.getMembers(), this::emitBreakableSpace);
            this.emitBreakableSpace();
        } else {
            this.emitCommonNewline();
            this.indent();
            this.visitWithSeparator(node.getMembers(), this::emitCommonNewline);
            this.unindent();
            this.emitCommonNewline();
        }
        this.emitType(242);
        return null;
    }

    @Override
    public Void visitStructDeclarator(StructDeclarator node) {
        this.visit(node.getName());
        this.visitSafe(node.getArraySpecifier());
        return null;
    }

    @Override
    public Void visitStructMember(StructMember node) {
        this.visit(node.getType());
        this.emitBreakableSpace();
        this.visitCommaSpaced(node.getDeclarators());
        this.emitType(243);
        return null;
    }

    @Override
    public Void visitStructSpecifier(StructSpecifier node) {
        this.emitType(45);
        this.emitBreakableSpace();
        if (node.getName() != null) {
            this.visit(node.getName());
            this.emitBreakableSpace();
        }
        this.visit(node.getStructBody());
        return null;
    }

    @Override
    public Void visitFullySpecifiedType(FullySpecifiedType node) {
        if (node.getTypeQualifier() != null) {
            this.visit(node.getTypeQualifier());
            this.emitBreakableSpace();
        }
        this.visit(node.getTypeSpecifier());
        return null;
    }

    @Override
    public Void visitIterationConditionInitializer(IterationConditionInitializer node) {
        this.visit(node.getType());
        this.emitBreakableSpace();
        this.visit(node.getName());
        this.emitBreakableSpace();
        this.emitType(261);
        this.emitBreakableSpace();
        this.visit(node.getInitializer());
        return null;
    }

    @Override
    public Void visitIdentifier(Identifier node) {
        this.emitLiteral(node.getName());
        return null;
    }
}

