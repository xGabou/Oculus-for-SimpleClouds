/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.transform;

import io.github.douira.glsl_transformer.GLSLParser;
import io.github.douira.glsl_transformer.GLSLParserBaseVisitor;
import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.IterationConditionInitializer;
import io.github.douira.glsl_transformer.ast.node.Profile;
import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.node.Version;
import io.github.douira.glsl_transformer.ast.node.VersionStatement;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
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
import io.github.douira.glsl_transformer.ast.node.expression.ReferenceExpression;
import io.github.douira.glsl_transformer.ast.node.expression.SequenceExpression;
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
import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.node.statement.loop.DoWhileLoopStatement;
import io.github.douira.glsl_transformer.ast.node.statement.loop.ForLoopStatement;
import io.github.douira.glsl_transformer.ast.node.statement.loop.WhileLoopStatement;
import io.github.douira.glsl_transformer.ast.node.statement.selection.SelectionStatement;
import io.github.douira.glsl_transformer.ast.node.statement.selection.SwitchStatement;
import io.github.douira.glsl_transformer.ast.node.statement.terminal.BreakStatement;
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
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.transform.SourceLocation;
import io.github.douira.glsl_transformer.util.Type;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import oculus.org.antlr.v4.runtime.BufferedTokenStream;
import oculus.org.antlr.v4.runtime.ParserRuleContext;
import oculus.org.antlr.v4.runtime.Token;
import oculus.org.antlr.v4.runtime.tree.ParseTree;
import oculus.org.antlr.v4.runtime.tree.TerminalNode;

public class ASTBuilder
extends GLSLParserBaseVisitor<ASTNode> {
    private static Deque<SourceLocation> sourceLocations = new ArrayDeque<SourceLocation>(4);
    private static SourceLocation lastSourceLocation = null;
    private static BufferedTokenStream tokenStream = null;
    private static Pattern lineDirective = Pattern.compile("#line[\\t\\r\\u000C ]+(\\d+)(?:[\\t\\r\\u000C ]+(\\d+))?.*", 32);
    private static final Pattern intExtractor = Pattern.compile("(0x|0|)(.*?)(?:us|ul|u|s|l)?$", 2);
    private static final Pattern floatExtractor = Pattern.compile("(.*?)(?:f|hf|lf)?$", 2);

    public static void setTokenStream(BufferedTokenStream tokenStream) {
        ASTBuilder.tokenStream = tokenStream;
    }

    public static void unsetTokenStream() {
        tokenStream = null;
    }

    public static ASTNode build(Root rootInstance, ParseTree ctx) {
        return rootInstance.indexNodes(() -> ASTBuilder.buildInternal(ctx));
    }

    public static <T extends ParseTree, N extends ASTNode> N build(Root rootInstance, T ctx, BiFunction<ASTBuilder, T, N> visitMethod) {
        return (N)rootInstance.indexNodes(() -> ASTBuilder.buildInternal(ctx, visitMethod));
    }

    public static ASTNode buildSubtree(Root rootInstance, ParseTree ctx) {
        return rootInstance.indexNodes(() -> ASTBuilder.buildInternal(ctx));
    }

    public static <T extends ParseTree, N extends ASTNode> N buildSubtree(Root rootInstance, T ctx, BiFunction<ASTBuilder, T, N> visitMethod) {
        return (N)rootInstance.indexNodes(() -> ASTBuilder.buildInternal(ctx, visitMethod));
    }

    private static ASTNode buildInternal(ParseTree ctx) {
        sourceLocations.clear();
        lastSourceLocation = null;
        return (ASTNode)new ASTBuilder().visit(ctx);
    }

    private static <T extends ParseTree, N extends ASTNode> N buildInternal(T ctx, BiFunction<ASTBuilder, T, N> visitMethod) {
        return (N)((ASTNode)visitMethod.apply(new ASTBuilder(), (ASTBuilder)((Object)ctx)));
    }

    private static <N, R> R applySafe(N ctx, Function<N, R> visitMethod) {
        return ctx == null ? null : (R)visitMethod.apply(ctx);
    }

    public static SourceLocation takeSourceLocation() {
        SourceLocation location = sourceLocations.isEmpty() ? null : sourceLocations.pop();
        return location == SourceLocation.PLACEHOLDER ? null : location;
    }

    private static Identifier makeIdentifier(Token name) {
        if (name == null) {
            return null;
        }
        if (name.getType() != 266) {
            throw new IllegalStateException("Expected identifier, got: " + name.getText());
        }
        Identifier result = new Identifier(name);
        return result;
    }

    private static void readLineDirective(ParserRuleContext ctx) {
        if (tokenStream == null) {
            return;
        }
        SourceLocation newSourceLocation = SourceLocation.PLACEHOLDER;
        List<Token> tokens = tokenStream.getHiddenTokensToLeft(ctx.start.getTokenIndex(), 4);
        if (tokens != null) {
            for (Token token : tokens) {
                Matcher matcher;
                if (token.getType() != 264 || !(matcher = lineDirective.matcher(token.getText())).matches()) continue;
                int line = Integer.parseInt(matcher.group(1));
                if (matcher.group(2) != null) {
                    int source = Integer.parseInt(matcher.group(2));
                    lastSourceLocation = SourceLocation.fromPrevious(lastSourceLocation, line, source);
                } else {
                    lastSourceLocation = SourceLocation.fromPrevious(lastSourceLocation, line);
                }
                newSourceLocation = lastSourceLocation;
            }
        }
        sourceLocations.push(newSourceLocation);
    }

    @Override
    public TranslationUnit visitTranslationUnit(GLSLParser.TranslationUnitContext ctx) {
        VersionStatement versionStatement = this.visitVersionStatement(ctx.versionStatement());
        Stream<ExternalDeclaration> externalDeclarations = ctx.externalDeclaration().stream().map(this::visitExternalDeclaration);
        return versionStatement == null ? new TranslationUnit(externalDeclarations) : new TranslationUnit(versionStatement, externalDeclarations);
    }

    @Override
    public VersionStatement visitVersionStatement(GLSLParser.VersionStatementContext ctx) {
        if (ctx == null) {
            return null;
        }
        return new VersionStatement(ASTBuilder.applySafe(ctx.version, Version::fromToken), ASTBuilder.applySafe(ctx.profile, Profile::fromToken));
    }

    @Override
    public EmptyDeclaration visitEmptyDeclaration(GLSLParser.EmptyDeclarationContext ctx) {
        return new EmptyDeclaration();
    }

    @Override
    public PragmaDirective visitPragmaDirective(GLSLParser.PragmaDirectiveContext ctx) {
        boolean stdGL = ctx.stdGL != null;
        PragmaDirective.PragmaType type = PragmaDirective.PragmaType.fromToken(ctx.type);
        return type == PragmaDirective.PragmaType.CUSTOM ? new PragmaDirective(stdGL, ctx.type.getText()) : new PragmaDirective(stdGL, type, PragmaDirective.PragmaState.fromToken(ctx.state));
    }

    @Override
    public ExtensionDirective visitExtensionDirective(GLSLParser.ExtensionDirectiveContext ctx) {
        String extensionName = ctx.extensionName.getText();
        return new ExtensionDirective(extensionName, ASTBuilder.applySafe(ctx.extensionBehavior, ExtensionDirective.ExtensionBehavior::fromToken));
    }

    @Override
    public CustomDirective visitCustomDirective(GLSLParser.CustomDirectiveContext ctx) {
        return new CustomDirective(ASTBuilder.applySafe(ctx.content, Token::getText));
    }

    @Override
    public IncludeDirective visitIncludeDirective(GLSLParser.IncludeDirectiveContext ctx) {
        return new IncludeDirective(ASTBuilder.applySafe(ctx.content, Token::getText), ctx.angleStart != null);
    }

    @Override
    public LayoutDefaults visitLayoutDefaults(GLSLParser.LayoutDefaultsContext ctx) {
        return new LayoutDefaults(this.visitLayoutQualifier(ctx.layoutQualifier()), LayoutDefaults.LayoutMode.fromToken(ctx.layoutMode));
    }

    @Override
    public ConditionExpression visitConditionalExpression(GLSLParser.ConditionalExpressionContext ctx) {
        return new ConditionExpression(this.visitExpression(ctx.condition), this.visitExpression(ctx.trueAlternative), this.visitExpression(ctx.falseAlternative));
    }

    @Override
    public FunctionCallExpression visitFunctionCallExpression(GLSLParser.FunctionCallExpressionContext ctx) {
        TerminalNode functionIdentifier = ctx.IDENTIFIER();
        Identifier functionName = null;
        TypeSpecifier functionType = null;
        if (functionIdentifier != null) {
            functionName = this.visitIdentifier(functionIdentifier);
        } else {
            functionType = this.visitTypeSpecifier(ctx.typeSpecifier());
        }
        Stream<Expression> parameters = ctx.parameters.stream().map(this::visitExpression);
        return functionName != null ? new FunctionCallExpression(functionName, parameters) : new FunctionCallExpression(functionType, parameters);
    }

    @Override
    public GroupingExpression visitGroupingExpression(GLSLParser.GroupingExpressionContext ctx) {
        GroupingExpression grouping;
        Expression expression = this.visitExpression(ctx.value);
        return expression instanceof GroupingExpression ? (grouping = (GroupingExpression)expression) : new GroupingExpression(expression);
    }

    @Override
    public MemberAccessExpression visitMemberAccessExpression(GLSLParser.MemberAccessExpressionContext ctx) {
        return new MemberAccessExpression(this.visitExpression(ctx.operand), new Identifier(ctx.member));
    }

    @Override
    public LengthAccessExpression visitLengthAccessExpression(GLSLParser.LengthAccessExpressionContext ctx) {
        return new LengthAccessExpression(this.visitExpression(ctx.operand));
    }

    @Override
    public UnaryExpression visitPostfixExpression(GLSLParser.PostfixExpressionContext ctx) {
        Expression operand = this.visitExpression(ctx.operand);
        switch (ctx.op.getType()) {
            case 217: {
                return new IncrementPostfixExpression(operand);
            }
            case 218: {
                return new DecrementPostfixExpression(operand);
            }
        }
        throw new IllegalArgumentException("Unknown postfix operator: " + ctx.op.getText());
    }

    @Override
    public UnaryExpression visitPrefixExpression(GLSLParser.PrefixExpressionContext ctx) {
        Expression operand = this.visitExpression(ctx.operand);
        switch (ctx.op.getType()) {
            case 217: {
                return new IncrementPrefixExpression(operand);
            }
            case 218: {
                return new DecrementPrefixExpression(operand);
            }
            case 248: {
                return new IdentityExpression(operand);
            }
            case 249: {
                return new NegationExpression(operand);
            }
            case 250: {
                return new BooleanNotExpression(operand);
            }
            case 251: {
                return new BitwiseNotExpression(operand);
            }
        }
        throw new IllegalStateException("Unexpected prefix operator type" + ctx.op.getText());
    }

    @Override
    public LiteralExpression visitLiteralExpression(GLSLParser.LiteralExpressionContext ctx) {
        Token content = ctx.getStart();
        Type literalType = Type.ofLiteralTokenType(content.getType());
        String tokenContent = content.getText();
        Type.NumberType numberType = literalType.getNumberType();
        switch (numberType) {
            case BOOLEAN: {
                return new LiteralExpression(tokenContent.equals("true"));
            }
            case SIGNED_INTEGER: 
            case UNSIGNED_INTEGER: {
                LiteralExpression.IntegerFormat format;
                int radix;
                Matcher intMatcher = intExtractor.matcher(tokenContent);
                intMatcher.matches();
                String prefix = intMatcher.group(1);
                tokenContent = intMatcher.group(2);
                if (tokenContent.isEmpty()) {
                    if (!prefix.equals("0")) {
                        throw new IllegalStateException("Unexpected prefix: " + prefix);
                    }
                    return new LiteralExpression(literalType, 0L);
                }
                if (prefix.equals("0x")) {
                    radix = 16;
                    format = LiteralExpression.IntegerFormat.HEXADECIMAL;
                } else if (prefix.equals("0")) {
                    radix = 8;
                    format = LiteralExpression.IntegerFormat.OCTAL;
                } else {
                    radix = 10;
                    format = LiteralExpression.IntegerFormat.DECIMAL;
                }
                long value = Long.parseUnsignedLong(tokenContent, radix);
                if (numberType == Type.NumberType.SIGNED_INTEGER && (value < 0L || value > Long.MAX_VALUE)) {
                    throw new IllegalStateException("Integer literal too large: " + tokenContent);
                }
                return new LiteralExpression(literalType, value, format);
            }
            case FLOATING_POINT: {
                Matcher floatMatcher = floatExtractor.matcher(tokenContent);
                floatMatcher.matches();
                tokenContent = floatMatcher.group(1);
                return new LiteralExpression(literalType, Double.parseDouble(tokenContent));
            }
        }
        throw new IllegalArgumentException("Unsupported literal type: " + literalType);
    }

    @Override
    public BinaryExpression visitAdditiveExpression(GLSLParser.AdditiveExpressionContext ctx) {
        Expression left = this.visitExpression(ctx.left);
        Expression right = this.visitExpression(ctx.right);
        switch (ctx.op.getType()) {
            case 248: {
                return new AdditionExpression(left, right);
            }
            case 249: {
                return new SubtractionExpression(left, right);
            }
        }
        throw new IllegalArgumentException("Unknown additive operator: " + ctx.op.getText());
    }

    @Override
    public ArrayAccessExpression visitArrayAccessExpression(GLSLParser.ArrayAccessExpressionContext ctx) {
        return new ArrayAccessExpression(this.visitExpression(ctx.left), this.visitExpression(ctx.right));
    }

    @Override
    public BinaryExpression visitAssignmentExpression(GLSLParser.AssignmentExpressionContext ctx) {
        Expression left = this.visitExpression(ctx.left);
        Expression right = this.visitExpression(ctx.right);
        switch (ctx.op.getType()) {
            case 261: {
                return new AssignmentExpression(left, right);
            }
            case 229: {
                return new MultiplicationAssignmentExpression(left, right);
            }
            case 230: {
                return new DivisionAssignmentExpression(left, right);
            }
            case 231: {
                return new ModuloAssignmentExpression(left, right);
            }
            case 232: {
                return new AdditionAssignmentExpression(left, right);
            }
            case 233: {
                return new SubtractionAssignmentExpression(left, right);
            }
            case 236: {
                return new BitwiseAndAssignmentExpression(left, right);
            }
            case 237: {
                return new BitwiseXorAssignmentExpression(left, right);
            }
            case 238: {
                return new BitwiseOrAssignmentExpression(left, right);
            }
            case 234: {
                return new LeftShiftAssignmentExpression(left, right);
            }
            case 235: {
                return new RightShiftAssignmentExpression(left, right);
            }
        }
        throw new IllegalArgumentException("Unknown assignment operator: " + ctx.op.getText());
    }

    @Override
    public BitwiseAndExpression visitBitwiseAndExpression(GLSLParser.BitwiseAndExpressionContext ctx) {
        return new BitwiseAndExpression(this.visitExpression(ctx.left), this.visitExpression(ctx.right));
    }

    @Override
    public BitwiseXorExpression visitBitwiseExclusiveOrExpression(GLSLParser.BitwiseExclusiveOrExpressionContext ctx) {
        return new BitwiseXorExpression(this.visitExpression(ctx.left), this.visitExpression(ctx.right));
    }

    @Override
    public BitwiseOrExpression visitBitwiseInclusiveOrExpression(GLSLParser.BitwiseInclusiveOrExpressionContext ctx) {
        return new BitwiseOrExpression(this.visitExpression(ctx.left), this.visitExpression(ctx.right));
    }

    @Override
    public BinaryExpression visitEqualityExpression(GLSLParser.EqualityExpressionContext ctx) {
        Expression left = this.visitExpression(ctx.left);
        Expression right = this.visitExpression(ctx.right);
        switch (ctx.op.getType()) {
            case 224: {
                return new EqualityExpression(left, right);
            }
            case 225: {
                return new InequalityExpression(left, right);
            }
        }
        throw new IllegalArgumentException("Unknown equality operator: " + ctx.op.getText());
    }

    @Override
    public BooleanAndExpression visitLogicalAndExpression(GLSLParser.LogicalAndExpressionContext ctx) {
        return new BooleanAndExpression(this.visitExpression(ctx.left), this.visitExpression(ctx.right));
    }

    @Override
    public BooleanXorExpression visitLogicalExclusiveOrExpression(GLSLParser.LogicalExclusiveOrExpressionContext ctx) {
        return new BooleanXorExpression(this.visitExpression(ctx.left), this.visitExpression(ctx.right));
    }

    @Override
    public BooleanOrExpression visitLogicalInclusiveOrExpression(GLSLParser.LogicalInclusiveOrExpressionContext ctx) {
        return new BooleanOrExpression(this.visitExpression(ctx.left), this.visitExpression(ctx.right));
    }

    @Override
    public BinaryExpression visitRelationalExpression(GLSLParser.RelationalExpressionContext ctx) {
        Expression left = this.visitExpression(ctx.left);
        Expression right = this.visitExpression(ctx.right);
        switch (ctx.op.getType()) {
            case 255: {
                return new LessThanExpression(left, right);
            }
            case 256: {
                return new GreaterThanExpression(left, right);
            }
            case 222: {
                return new LessThanEqualExpression(left, right);
            }
            case 223: {
                return new GreaterThanEqualExpression(left, right);
            }
        }
        throw new IllegalArgumentException("Unknown relational operator: " + ctx.op.getText());
    }

    @Override
    public BinaryExpression visitShiftExpression(GLSLParser.ShiftExpressionContext ctx) {
        Expression left = this.visitExpression(ctx.left);
        Expression right = this.visitExpression(ctx.right);
        switch (ctx.op.getType()) {
            case 220: {
                return new LeftShiftExpression(left, right);
            }
            case 221: {
                return new RightShiftExpression(left, right);
            }
        }
        throw new IllegalArgumentException("Unknown shift operator: " + ctx.op.getText());
    }

    @Override
    public BinaryExpression visitMultiplicativeExpression(GLSLParser.MultiplicativeExpressionContext ctx) {
        Expression left = this.visitExpression(ctx.left);
        Expression right = this.visitExpression(ctx.right);
        switch (ctx.op.getType()) {
            case 252: {
                return new MultiplicationExpression(left, right);
            }
            case 253: {
                return new DivisionExpression(left, right);
            }
            case 254: {
                return new ModuloExpression(left, right);
            }
        }
        throw new IllegalArgumentException("Unknown multiplicative operator: " + ctx.op.getText());
    }

    @Override
    public ReferenceExpression visitReferenceExpression(GLSLParser.ReferenceExpressionContext ctx) {
        return new ReferenceExpression(this.visitIdentifier(ctx.IDENTIFIER()));
    }

    @Override
    public CompoundStatement visitCompoundStatement(GLSLParser.CompoundStatementContext ctx) {
        return new CompoundStatement(ctx.statement().stream().map(this::visitStatement));
    }

    @Override
    public ContinueStatement visitContinueStatement(GLSLParser.ContinueStatementContext ctx) {
        return new ContinueStatement();
    }

    @Override
    public BreakStatement visitBreakStatement(GLSLParser.BreakStatementContext ctx) {
        return new BreakStatement();
    }

    @Override
    public ReturnStatement visitReturnStatement(GLSLParser.ReturnStatementContext ctx) {
        return new ReturnStatement(ASTBuilder.applySafe(ctx.expression(), this::visitExpression));
    }

    @Override
    public DiscardStatement visitDiscardStatement(GLSLParser.DiscardStatementContext ctx) {
        return new DiscardStatement();
    }

    @Override
    public ASTNode visitIgnoreIntersectionStatement(GLSLParser.IgnoreIntersectionStatementContext ctx) {
        return new IgnoreIntersectionStatement();
    }

    @Override
    public ASTNode visitTerminateRayStatement(GLSLParser.TerminateRayStatementContext ctx) {
        return new TerminateRayStatement();
    }

    @Override
    public DemoteStatement visitDemoteStatement(GLSLParser.DemoteStatementContext ctx) {
        return new DemoteStatement();
    }

    @Override
    public DeclarationStatement visitDeclarationStatement(GLSLParser.DeclarationStatementContext ctx) {
        return new DeclarationStatement(this.visitDeclaration(ctx.declaration()));
    }

    @Override
    public ExpressionStatement visitExpressionStatement(GLSLParser.ExpressionStatementContext ctx) {
        return new ExpressionStatement(this.visitExpression(ctx.expression()));
    }

    @Override
    public EmptyStatement visitEmptyStatement(GLSLParser.EmptyStatementContext ctx) {
        return new EmptyStatement();
    }

    @Override
    public SelectionStatement visitSelectionStatement(GLSLParser.SelectionStatementContext ctx) {
        return new SelectionStatement(this.visitExpression(ctx.condition), this.visitStatement(ctx.ifTrue), ASTBuilder.applySafe(ctx.ifFalse, this::visitStatement));
    }

    @Override
    public SwitchStatement visitSwitchStatement(GLSLParser.SwitchStatementContext ctx) {
        GLSLParser.CompoundStatementContext compoundStatementCtx = ctx.compoundStatement();
        ASTBuilder.readLineDirective(compoundStatementCtx);
        return new SwitchStatement(this.visitExpression(ctx.condition), this.visitCompoundStatement(compoundStatementCtx));
    }

    @Override
    public DefaultStatement visitDefaultCaseLabel(GLSLParser.DefaultCaseLabelContext ctx) {
        return new DefaultStatement();
    }

    @Override
    public CaseStatement visitValuedCaseLabel(GLSLParser.ValuedCaseLabelContext ctx) {
        return new CaseStatement(this.visitExpression(ctx.expression()));
    }

    @Override
    public ForLoopStatement visitForStatement(GLSLParser.ForStatementContext ctx) {
        Expression initExpression = null;
        Declaration initDeclaration = null;
        GLSLParser.ExpressionStatementContext initExpressionStatement = ctx.expressionStatement();
        if (initExpressionStatement != null) {
            initExpression = this.visitExpression(initExpressionStatement.expression());
        } else {
            GLSLParser.DeclarationStatementContext initDeclarationStatement = ctx.declarationStatement();
            if (initDeclarationStatement != null) {
                initDeclaration = this.visitDeclaration(initDeclarationStatement.declaration());
            }
        }
        return new ForLoopStatement(initExpression, initDeclaration, ASTBuilder.applySafe(ctx.condition, this::visitExpression), ASTBuilder.applySafe(ctx.initCondition, this::visitIterationCondition), ASTBuilder.applySafe(ctx.incrementer, this::visitExpression), this.visitStatement(ctx.statement()));
    }

    @Override
    public WhileLoopStatement visitWhileStatement(GLSLParser.WhileStatementContext ctx) {
        return ctx.condition != null ? new WhileLoopStatement(this.visitExpression(ctx.condition), this.visitStatement(ctx.loopBody)) : new WhileLoopStatement(this.visitIterationCondition(ctx.initCondition), this.visitStatement(ctx.loopBody));
    }

    @Override
    public DoWhileLoopStatement visitDoWhileStatement(GLSLParser.DoWhileStatementContext ctx) {
        return new DoWhileLoopStatement(this.visitStatement(ctx.loopBody), this.visitExpression(ctx.condition));
    }

    @Override
    public IterationConditionInitializer visitIterationCondition(GLSLParser.IterationConditionContext ctx) {
        return new IterationConditionInitializer(this.visitFullySpecifiedType(ctx.fullySpecifiedType()), new Identifier(ctx.name), this.visitInitializer(ctx.initializer()));
    }

    @Override
    public ArraySpecifier visitArraySpecifier(GLSLParser.ArraySpecifierContext ctx) {
        return new ArraySpecifier(ctx.arraySpecifierSegment().stream().map(child -> ASTBuilder.applySafe(child.expression(), this::visitExpression)));
    }

    @Override
    public FunctionDefinition visitFunctionDefinition(GLSLParser.FunctionDefinitionContext ctx) {
        GLSLParser.CompoundStatementContext compoundStatementCtx = ctx.compoundStatement();
        ASTBuilder.readLineDirective(compoundStatementCtx);
        return new FunctionDefinition(this.visitFunctionPrototype(ctx.functionPrototype()), this.visitCompoundStatement(compoundStatementCtx));
    }

    @Override
    public FunctionPrototype visitFunctionPrototype(GLSLParser.FunctionPrototypeContext ctx) {
        FullySpecifiedType returnType = this.visitFullySpecifiedType(ctx.fullySpecifiedType());
        Identifier name = this.visitIdentifier(ctx.IDENTIFIER());
        return new FunctionPrototype(returnType, name, ASTBuilder.applySafe(ctx.functionParameterList().parameters, parameters -> parameters.stream().map(this::visitParameterDeclaration)));
    }

    @Override
    public DeclarationMember visitDeclarationMember(GLSLParser.DeclarationMemberContext ctx) {
        GLSLParser.ArraySpecifierContext arraySpecifier = ctx.arraySpecifier();
        Identifier name = this.visitIdentifier(ctx.IDENTIFIER());
        GLSLParser.InitializerContext initializer = ctx.initializer();
        return new DeclarationMember(name, ASTBuilder.applySafe(arraySpecifier, this::visitArraySpecifier), ASTBuilder.applySafe(initializer, this::visitInitializer));
    }

    @Override
    public FullySpecifiedType visitFullySpecifiedType(GLSLParser.FullySpecifiedTypeContext ctx) {
        return new FullySpecifiedType(ASTBuilder.applySafe(ctx.typeQualifier(), this::visitTypeQualifier), this.visitTypeSpecifier(ctx.typeSpecifier()));
    }

    @Override
    public FunctionParameter visitParameterDeclaration(GLSLParser.ParameterDeclarationContext ctx) {
        return new FunctionParameter(this.visitFullySpecifiedType(ctx.fullySpecifiedType()), ASTBuilder.makeIdentifier(ctx.parameterName), ASTBuilder.applySafe(ctx.arraySpecifier(), this::visitArraySpecifier));
    }

    @Override
    public FunctionDeclaration visitFunctionDeclaration(GLSLParser.FunctionDeclarationContext ctx) {
        return new FunctionDeclaration(this.visitFunctionPrototype(ctx.functionPrototype()));
    }

    @Override
    public TypeAndInitDeclaration visitTypeAndInitDeclaration(GLSLParser.TypeAndInitDeclarationContext ctx) {
        return new TypeAndInitDeclaration(this.visitFullySpecifiedType(ctx.fullySpecifiedType()), ctx.declarationMembers.stream().map(this::visitDeclarationMember));
    }

    @Override
    public PrecisionDeclaration visitPrecisionDeclaration(GLSLParser.PrecisionDeclarationContext ctx) {
        return new PrecisionDeclaration(this.visitPrecisionQualifier(ctx.precisionQualifier()), this.visitTypeSpecifier(ctx.typeSpecifier()));
    }

    @Override
    public InterfaceBlockDeclaration visitInterfaceBlockDeclaration(GLSLParser.InterfaceBlockDeclarationContext ctx) {
        TypeQualifier typeQualifier = this.visitTypeQualifier(ctx.typeQualifier());
        Identifier name = new Identifier(ctx.blockName);
        StructBody structBody = this.visitStructBody(ctx.structBody());
        if (ctx.variableName != null) {
            Identifier variableName = new Identifier(ctx.variableName);
            GLSLParser.ArraySpecifierContext arraySpecifierContext = ctx.arraySpecifier();
            if (arraySpecifierContext != null) {
                ArraySpecifier arraySpecifier = this.visitArraySpecifier(arraySpecifierContext);
                return new InterfaceBlockDeclaration(typeQualifier, name, structBody, variableName, arraySpecifier);
            }
            return new InterfaceBlockDeclaration(typeQualifier, name, structBody, variableName);
        }
        return new InterfaceBlockDeclaration(typeQualifier, name, structBody);
    }

    @Override
    public VariableDeclaration visitVariableDeclaration(GLSLParser.VariableDeclarationContext ctx) {
        return new VariableDeclaration(this.visitTypeQualifier(ctx.typeQualifier()), ctx.variableNames.stream().map(ASTBuilder::makeIdentifier));
    }

    @Override
    public Initializer visitInitializer(GLSLParser.InitializerContext ctx) {
        GLSLParser.FiniteExpressionContext expressionContext = ctx.finiteExpression();
        if (expressionContext != null) {
            return new ExpressionInitializer(this.visitExpression(expressionContext));
        }
        List<GLSLParser.InitializerContext> initializers = ctx.initializers;
        return initializers == null ? new NestedInitializer() : new NestedInitializer(initializers.stream().map(this::visitInitializer));
    }

    @Override
    public NamedLayoutQualifierPart visitNamedLayoutQualifier(GLSLParser.NamedLayoutQualifierContext ctx) {
        return new NamedLayoutQualifierPart(new Identifier(ctx.getStart()), ASTBuilder.applySafe(ctx.expression(), this::visitExpression));
    }

    @Override
    public SharedLayoutQualifierPart visitSharedLayoutQualifier(GLSLParser.SharedLayoutQualifierContext ctx) {
        return new SharedLayoutQualifierPart();
    }

    public LayoutQualifierPart visitLayoutQualifierPart(GLSLParser.LayoutQualifierIdContext ctx) {
        return (LayoutQualifierPart)this.visit(ctx);
    }

    @Override
    public LayoutQualifier visitLayoutQualifier(GLSLParser.LayoutQualifierContext ctx) {
        LinkedList<LayoutQualifierPart> parts = new LinkedList<LayoutQualifierPart>();
        for (GLSLParser.LayoutQualifierIdContext partContext : ctx.layoutQualifiers) {
            LayoutQualifierPart part = this.visitLayoutQualifierPart(partContext);
            if (part instanceof NamedLayoutQualifierPart) {
                NamedLayoutQualifierPart named = (NamedLayoutQualifierPart)part;
                Expression expression = named.getExpression();
                if (expression instanceof SequenceExpression) {
                    SequenceExpression sequence = (SequenceExpression)expression;
                    Iterator expressions = sequence.getExpressions().iterator();
                    parts.add(new NamedLayoutQualifierPart(named.getName(), (Expression)expressions.next()));
                    while (expressions.hasNext()) {
                        Expression expression2 = (Expression)expressions.next();
                        if (expression2 instanceof AssignmentExpression) {
                            AssignmentExpression assignment = (AssignmentExpression)expression2;
                            Expression left = assignment.getLeft();
                            if (left instanceof ReferenceExpression) {
                                ReferenceExpression ref = (ReferenceExpression)left;
                                parts.add(new NamedLayoutQualifierPart(ref.getIdentifier(), assignment.getRight()));
                                continue;
                            }
                            throw new IllegalArgumentException("Unexpected left hand side in assignment expression of layout qualifier sequence: " + left);
                        }
                        if (expression2 instanceof ReferenceExpression) {
                            ReferenceExpression reference = (ReferenceExpression)expression2;
                            Identifier id = reference.getIdentifier();
                            if (id.equals("shared")) {
                                parts.add(new SharedLayoutQualifierPart());
                                continue;
                            }
                            parts.add(new NamedLayoutQualifierPart(id));
                            continue;
                        }
                        throw new IllegalArgumentException("Unexpected expression in sequence expression in layout qualifier");
                    }
                    continue;
                }
                parts.add(named);
                continue;
            }
            parts.add(part);
        }
        return new LayoutQualifier(parts.stream());
    }

    @Override
    public PreciseQualifier visitPreciseQualifier(GLSLParser.PreciseQualifierContext ctx) {
        return new PreciseQualifier();
    }

    @Override
    public InvariantQualifier visitInvariantQualifier(GLSLParser.InvariantQualifierContext ctx) {
        return new InvariantQualifier();
    }

    @Override
    public InterpolationQualifier visitInterpolationQualifier(GLSLParser.InterpolationQualifierContext ctx) {
        return new InterpolationQualifier(InterpolationQualifier.InterpolationType.fromToken(ctx.getStart()));
    }

    @Override
    public PrecisionQualifier visitPrecisionQualifier(GLSLParser.PrecisionQualifierContext ctx) {
        return new PrecisionQualifier(PrecisionQualifier.PrecisionLevel.fromToken(ctx.getStart()));
    }

    @Override
    public ASTNode visitStorageQualifier(GLSLParser.StorageQualifierContext ctx) {
        return ctx.typeNames.isEmpty() ? new StorageQualifier(StorageQualifier.StorageType.fromToken(ctx.getStart())) : new StorageQualifier(ctx.typeNames.stream().map(ASTBuilder::makeIdentifier));
    }

    @Override
    public StructBody visitStructBody(GLSLParser.StructBodyContext ctx) {
        return new StructBody(ctx.structMember().stream().map(this::visitStructMember));
    }

    @Override
    public StructMember visitStructMember(GLSLParser.StructMemberContext ctx) {
        return new StructMember(this.visitFullySpecifiedType(ctx.fullySpecifiedType()), ctx.structDeclarators.stream().map(this::visitStructDeclarator));
    }

    @Override
    public StructDeclarator visitStructDeclarator(GLSLParser.StructDeclaratorContext ctx) {
        return new StructDeclarator(new Identifier(ctx.getStart()), ASTBuilder.applySafe(ctx.arraySpecifier(), this::visitArraySpecifier));
    }

    @Override
    public TypeSpecifier visitTypeSpecifier(GLSLParser.TypeSpecifierContext ctx) {
        ArraySpecifier arraySpecifier = ASTBuilder.applySafe(ctx.arraySpecifier(), this::visitArraySpecifier);
        GLSLParser.BuiltinTypeSpecifierFixedContext builtinTypeFixed = ctx.builtinTypeSpecifierFixed();
        if (builtinTypeFixed != null) {
            BuiltinFixedTypeSpecifier.BuiltinType type = BuiltinFixedTypeSpecifier.BuiltinType.fromToken(builtinTypeFixed.getStart());
            return new BuiltinFixedTypeSpecifier(type, arraySpecifier);
        }
        GLSLParser.BuiltinTypeSpecifierParseableContext builtinNumericType = ctx.builtinTypeSpecifierParseable();
        if (builtinNumericType != null) {
            Type type = Type.fromToken(builtinNumericType.getStart());
            return new BuiltinNumericTypeSpecifier(type, arraySpecifier);
        }
        GLSLParser.StructSpecifierContext structSpecifierContext = ctx.structSpecifier();
        if (structSpecifierContext != null) {
            return new StructSpecifier(ASTBuilder.applySafe(structSpecifierContext.IDENTIFIER(), this::visitIdentifier), this.visitStructBody(structSpecifierContext.structBody()), arraySpecifier);
        }
        Identifier identifier = this.visitIdentifier(ctx.IDENTIFIER());
        return new TypeReference(identifier, arraySpecifier);
    }

    @Override
    public TypeQualifier visitTypeQualifier(GLSLParser.TypeQualifierContext ctx) {
        return new TypeQualifier(ctx.children.stream().map(child -> (TypeQualifierPart)this.visit((ParseTree)child)));
    }

    @Override
    public Expression visitExpression(GLSLParser.ExpressionContext ctx) {
        return ctx.items.size() == 1 ? this.visitExpression(ctx.items.get(0)) : new SequenceExpression(ctx.items.stream().map(this::visitExpression));
    }

    public Expression visitExpression(GLSLParser.FiniteExpressionContext ctx) {
        return (Expression)this.visit(ctx);
    }

    @Override
    public Statement visitStatement(GLSLParser.StatementContext ctx) {
        ASTBuilder.readLineDirective(ctx);
        return (Statement)super.visitStatement(ctx);
    }

    @Override
    public ExternalDeclaration visitExternalDeclaration(GLSLParser.ExternalDeclarationContext ctx) {
        ASTBuilder.readLineDirective(ctx);
        ASTNode result = (ASTNode)super.visitExternalDeclaration(ctx);
        if (result instanceof Declaration) {
            Declaration declaration = (Declaration)result;
            return new DeclarationExternalDeclaration(declaration);
        }
        return (ExternalDeclaration)result;
    }

    public Declaration visitDeclaration(GLSLParser.DeclarationContext ctx) {
        return (Declaration)this.visit(ctx);
    }

    @Override
    public ASTNode visitTerminal(TerminalNode node) {
        throw new AssertionError((Object)("visitTerminal should never be called instead of allowing the normal visitation to reach terminal nodes automatically. Content of this node: " + node.getText()));
    }

    public Identifier visitIdentifier(TerminalNode identifier) {
        return ASTBuilder.makeIdentifier(identifier.getSymbol());
    }
}

