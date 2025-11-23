/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.parser;

import io.github.douira.glsl_transformer.GLSLLexer;
import io.github.douira.glsl_transformer.GLSLParser;
import io.github.douira.glsl_transformer.parser.EnhancedParser;
import io.github.douira.glsl_transformer.token_filter.TokenFilter;

public interface ParserInterface {
    public GLSLLexer getLexer();

    public GLSLParser getParser();

    public void setThrowParseErrors(boolean var1);

    public void setParsingStrategy(EnhancedParser.ParsingStrategy var1);

    public void setSLLOnly();

    public void setLLOnly();

    public void setTokenFilter(TokenFilter<?> var1);
}

