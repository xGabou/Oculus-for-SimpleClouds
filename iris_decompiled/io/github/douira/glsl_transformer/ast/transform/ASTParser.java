/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.transform;

import io.github.douira.glsl_transformer.GLSLLexer;
import io.github.douira.glsl_transformer.GLSLParser;
import io.github.douira.glsl_transformer.ast.data.TypedTreeCache;
import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.external_declaration.ExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.query.RootSupplier;
import io.github.douira.glsl_transformer.ast.transform.ASTBuilder;
import io.github.douira.glsl_transformer.parser.CachingParser;
import io.github.douira.glsl_transformer.parser.EnhancedParser;
import io.github.douira.glsl_transformer.parser.ParseShape;
import io.github.douira.glsl_transformer.parser.ParserInterface;
import io.github.douira.glsl_transformer.token_filter.TokenFilter;
import java.util.ArrayList;
import java.util.List;
import oculus.org.antlr.v4.runtime.ParserRuleContext;

public class ASTParser
implements ParserInterface {
    private static ASTParser INSTANCE;
    private EnhancedParser parser = new CachingParser();
    private TypedTreeCache<ASTNode> buildCache = new TypedTreeCache();
    private ASTCacheStrategy astCacheStrategy = ASTCacheStrategy.ALL_EXCLUDING_TRANSLATION_UNIT;
    private boolean parseLineDirectives = false;

    public static ASTParser _getInternalInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ASTParser();
        }
        return INSTANCE;
    }

    public void setBuildCacheSizeAndClear(int size) {
        this.buildCache = new TypedTreeCache(size);
    }

    public void setParseCacheSizeAndClear(int size) {
        EnhancedParser enhancedParser = this.parser;
        if (enhancedParser instanceof CachingParser) {
            CachingParser cachingParser = (CachingParser)enhancedParser;
            cachingParser.setParseCacheSizeAndClear(size);
        }
    }

    public void setASTCacheStrategy(ASTCacheStrategy astCacheStrategy) {
        this.astCacheStrategy = astCacheStrategy;
    }

    public void setParsingCacheStrategy(ParsingCacheStrategy parsingCacheStrategy) {
        this.parser = parsingCacheStrategy == ParsingCacheStrategy.ALL ? new CachingParser() : new EnhancedParser();
    }

    public void setParseLineDirectives(boolean parseLineDirectives) {
        this.parseLineDirectives = parseLineDirectives;
    }

    @Override
    public GLSLLexer getLexer() {
        return this.parser.getLexer();
    }

    @Override
    public GLSLParser getParser() {
        return this.parser.getParser();
    }

    @Override
    public void setThrowParseErrors(boolean throwParseErrors) {
        this.parser.setThrowParseErrors(throwParseErrors);
    }

    @Override
    public void setParsingStrategy(EnhancedParser.ParsingStrategy parsingStrategy) {
        this.parser.setParsingStrategy(parsingStrategy);
    }

    @Override
    public void setSLLOnly() {
        this.parser.setSLLOnly();
    }

    @Override
    public void setLLOnly() {
        this.parser.setLLOnly();
    }

    @Override
    public void setTokenFilter(TokenFilter<?> setTokenFilter) {
        this.parser.setTokenFilter(setTokenFilter);
    }

    private void setBuilderTokenStream() {
        if (this.parseLineDirectives) {
            ASTBuilder.setTokenStream(this.parser.getTokenStream());
        }
    }

    private void unsetBuilderTokenStream() {
        if (this.parseLineDirectives) {
            ASTBuilder.unsetTokenStream();
        }
    }

    private <C extends ParserRuleContext, N extends ASTNode> N parseNodeCachedUncloned(String input, ParseShape<C, N> parseShape) {
        return (N)this.buildCache.cachedGet(input, parseShape.ruleType, () -> {
            try {
                this.setBuilderTokenStream();
                Object n = ASTBuilder.build(new EmptyRoot(), this.parser.parse(input, parseShape), parseShape.visitMethod);
                return n;
            }
            finally {
                this.unsetBuilderTokenStream();
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <C extends ParserRuleContext, N extends ASTNode> N parseNode(Root rootInstance, ParseShape<C, N> parseShape, String input) {
        if (this.astCacheStrategy == ASTCacheStrategy.NONE || this.astCacheStrategy == ASTCacheStrategy.ALL_EXCLUDING_TRANSLATION_UNIT && parseShape.ruleType == GLSLParser.TranslationUnitContext.class) {
            try {
                this.setBuilderTokenStream();
                Object n = ASTBuilder.buildSubtree(rootInstance, this.parser.parse(input, parseShape), parseShape.visitMethod);
                return n;
            }
            finally {
                this.unsetBuilderTokenStream();
            }
        }
        return (N)((ASTNode)this.parseNodeCachedUncloned(input, parseShape)).cloneInto(rootInstance);
    }

    public <C extends ParserRuleContext, N extends ASTNode> N parseNodeSeparate(RootSupplier rootSupplier, ParseShape<C, N> parseShape, String input) {
        return this.parseNode(rootSupplier.get(), parseShape, input);
    }

    public TranslationUnit parseTranslationUnit(Root rootInstance, String input) {
        return this.parseNode(rootInstance, ParseShape.TRANSLATION_UNIT, input);
    }

    public ExternalDeclaration parseExternalDeclaration(Root rootInstance, String input) {
        return this.parseNode(rootInstance, ParseShape.EXTERNAL_DECLARATION, input);
    }

    public Expression parseExpression(Root rootInstance, String input) {
        return this.parseNode(rootInstance, ParseShape.EXPRESSION, input);
    }

    public Statement parseStatement(Root rootInstance, String input) {
        return this.parseNode(rootInstance, ParseShape.STATEMENT, input);
    }

    public TranslationUnit parseTranslationUnit(RootSupplier rootSupplier, String input) {
        return this.parseTranslationUnit(rootSupplier.get(), input);
    }

    public ExternalDeclaration parseExternalDeclaration(RootSupplier rootSupplier, String input) {
        return this.parseExternalDeclaration(rootSupplier.get(), input);
    }

    public Expression parseExpression(RootSupplier rootSupplier, String input) {
        return this.parseExpression(rootSupplier.get(), input);
    }

    public Statement parseStatement(RootSupplier rootSupplier, String input) {
        return this.parseStatement(rootSupplier.get(), input);
    }

    public List<ExternalDeclaration> parseExternalDeclarations(Root rootInstance, String ... inputs) {
        ArrayList<ExternalDeclaration> nodes = new ArrayList<ExternalDeclaration>(inputs.length);
        for (String input : inputs) {
            nodes.add(this.parseExternalDeclaration(rootInstance, input));
        }
        return nodes;
    }

    public List<Expression> parseExpression(Root rootInstance, String ... inputs) {
        ArrayList<Expression> nodes = new ArrayList<Expression>(inputs.length);
        for (String input : inputs) {
            nodes.add(this.parseExpression(rootInstance, input));
        }
        return nodes;
    }

    public List<Statement> parseStatements(Root rootInstance, String ... inputs) {
        ArrayList<Statement> nodes = new ArrayList<Statement>(inputs.length);
        for (String input : inputs) {
            nodes.add(this.parseStatement(rootInstance, input));
        }
        return nodes;
    }

    public static enum ASTCacheStrategy {
        ALL,
        ALL_EXCLUDING_TRANSLATION_UNIT,
        NONE;

    }

    public static enum ParsingCacheStrategy {
        ALL,
        NONE;

    }

    private class EmptyRoot
    extends Root {
        public EmptyRoot() {
            super(null, null, null);
        }

        @Override
        public void registerIdentifierRename(Identifier identifier) {
        }

        @Override
        public void registerNode(ASTNode node, boolean isSubtreeRoot) {
        }

        @Override
        public void unregisterIdentifierRename(Identifier identifier) {
        }

        @Override
        public void unregisterNode(ASTNode node, boolean isSubtreeRoot) {
        }
    }
}

