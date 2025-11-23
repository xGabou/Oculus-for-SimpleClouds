/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.parser;

import io.github.douira.glsl_transformer.GLSLParser;
import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.external_declaration.ExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.node.type.FullySpecifiedType;
import io.github.douira.glsl_transformer.ast.query.RootSupplier;
import io.github.douira.glsl_transformer.ast.transform.ASTBuilder;
import io.github.douira.glsl_transformer.ast.transform.ASTParser;
import java.util.function.BiFunction;
import java.util.function.Function;
import oculus.org.antlr.v4.runtime.ParserRuleContext;

public class ParseShape<C extends ParserRuleContext, N extends ASTNode> {
    public static final ParseShape<GLSLParser.TranslationUnitContext, TranslationUnit> TRANSLATION_UNIT = new ParseShape<GLSLParser.TranslationUnitContext, TranslationUnit>(GLSLParser.TranslationUnitContext.class, GLSLParser::translationUnit, ASTBuilder::visitTranslationUnit);
    public static final ParseShape<GLSLParser.ExternalDeclarationContext, ExternalDeclaration> EXTERNAL_DECLARATION = new ParseShape<GLSLParser.ExternalDeclarationContext, ExternalDeclaration>(GLSLParser.ExternalDeclarationContext.class, GLSLParser::externalDeclaration, ASTBuilder::visitExternalDeclaration);
    public static final ParseShape<GLSLParser.StatementContext, Statement> STATEMENT = new ParseShape<GLSLParser.StatementContext, Statement>(GLSLParser.StatementContext.class, GLSLParser::statement, ASTBuilder::visitStatement);
    public static final ParseShape<GLSLParser.ExpressionContext, Expression> EXPRESSION = new ParseShape<GLSLParser.ExpressionContext, Expression>(GLSLParser.ExpressionContext.class, GLSLParser::expression, ASTBuilder::visitExpression);
    public static final ParseShape<GLSLParser.FullySpecifiedTypeContext, FullySpecifiedType> FULLY_SPECIFIED_TYPE = new ParseShape<GLSLParser.FullySpecifiedTypeContext, FullySpecifiedType>(GLSLParser.FullySpecifiedTypeContext.class, GLSLParser::fullySpecifiedType, ASTBuilder::visitFullySpecifiedType);
    public final Class<C> ruleType;
    public final Function<GLSLParser, C> parseMethod;
    public final BiFunction<ASTBuilder, C, N> visitMethod;

    public ParseShape(Class<C> ruleType, Function<GLSLParser, C> parseMethod, BiFunction<ASTBuilder, C, N> visitMethod) {
        this.ruleType = ruleType;
        this.parseMethod = parseMethod;
        this.visitMethod = visitMethod;
    }

    public N _parseNodeSeparateInternal(String input) {
        return ASTParser._getInternalInstance().parseNodeSeparate(RootSupplier.DEFAULT, this, input);
    }
}

