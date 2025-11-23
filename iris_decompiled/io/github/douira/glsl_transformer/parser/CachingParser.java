/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.parser;

import io.github.douira.glsl_transformer.ast.data.TypedTreeCache;
import io.github.douira.glsl_transformer.parser.EnhancedParser;
import io.github.douira.glsl_transformer.parser.ParseShape;
import io.github.douira.glsl_transformer.token_filter.TokenFilter;
import oculus.org.antlr.v4.runtime.BufferedTokenStream;
import oculus.org.antlr.v4.runtime.ParserRuleContext;

public class CachingParser
extends EnhancedParser {
    private TypedTreeCache<CacheContents> parseCache;

    public CachingParser(boolean throwParseErrors, int cacheSize) {
        super(throwParseErrors);
        this.parseCache = new TypedTreeCache(cacheSize);
    }

    public CachingParser(int cacheSize) {
        this.parseCache = new TypedTreeCache(cacheSize);
    }

    public CachingParser(boolean throwParseErrors) {
        super(throwParseErrors);
        this.parseCache = new TypedTreeCache();
    }

    public CachingParser() {
        this.parseCache = new TypedTreeCache();
    }

    public void setParseCacheSizeAndClear(int size) {
        this.parseCache = new TypedTreeCache(size);
    }

    @Override
    public <C extends ParserRuleContext> C parse(String str, ParseShape<C, ?> parseShape) {
        return this.parse(str, null, parseShape);
    }

    @Override
    public <C extends ParserRuleContext> C parse(String str, ParserRuleContext parent, ParseShape<C, ?> parseShape) {
        CacheContents result = this.parseCache.cachedGet(str, parseShape.ruleType, () -> {
            Object node = ((EnhancedParser)this).parse(str, parent, parseShape.parseMethod);
            return new CacheContents((ParserRuleContext)node, this.getTokenStream());
        });
        if (result != null) {
            this.tokenStream = result.tokenStream;
            return (C)result.parseTree;
        }
        return null;
    }

    @Override
    public void setTokenFilter(TokenFilter<?> tokenFilter) {
        super.setTokenFilter(tokenFilter);
        this.parseCache.clear();
    }

    record CacheContents(ParserRuleContext parseTree, BufferedTokenStream tokenStream) {
    }
}

