/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.parser;

import io.github.douira.glsl_transformer.GLSLLexer;
import io.github.douira.glsl_transformer.GLSLParser;
import io.github.douira.glsl_transformer.parser.FilterTokenSource;
import io.github.douira.glsl_transformer.parser.ParseShape;
import io.github.douira.glsl_transformer.parser.ParserInterface;
import io.github.douira.glsl_transformer.parser.ParsingException;
import io.github.douira.glsl_transformer.token_filter.TokenFilter;
import java.util.function.BiConsumer;
import java.util.function.Function;
import oculus.org.antlr.v4.runtime.BailErrorStrategy;
import oculus.org.antlr.v4.runtime.BaseErrorListener;
import oculus.org.antlr.v4.runtime.BufferedTokenStream;
import oculus.org.antlr.v4.runtime.CharStreams;
import oculus.org.antlr.v4.runtime.CommonTokenStream;
import oculus.org.antlr.v4.runtime.DefaultErrorStrategy;
import oculus.org.antlr.v4.runtime.InputMismatchException;
import oculus.org.antlr.v4.runtime.IntStream;
import oculus.org.antlr.v4.runtime.ParserRuleContext;
import oculus.org.antlr.v4.runtime.RecognitionException;
import oculus.org.antlr.v4.runtime.Recognizer;
import oculus.org.antlr.v4.runtime.Token;
import oculus.org.antlr.v4.runtime.atn.ParserATNSimulator;
import oculus.org.antlr.v4.runtime.atn.PredictionMode;
import oculus.org.antlr.v4.runtime.misc.IntervalSet;
import oculus.org.antlr.v4.runtime.misc.ParseCancellationException;

public class EnhancedParser
implements ParserInterface {
    private final GLSLLexer lexer = new GLSLLexer(null);
    private final GLSLParser parser = new GLSLParser(null);
    private boolean throwParseErrors = true;
    private ParsingStrategy parsingStrategy;
    public BiConsumer<ParseCancellationException, ParseCancellationException> internalErrorConsumer;
    protected IntStream input;
    protected BufferedTokenStream tokenStream;
    private FilterTokenSource tokenSource;

    public EnhancedParser(boolean throwParseErrors) {
        this.parser.removeErrorListeners();
        this.lexer.removeErrorListeners();
        this.parsingStrategy = ParsingStrategy.SLL_AND_LL_ON_ERROR;
        this.tokenSource = new FilterTokenSource(this.lexer);
        this.throwParseErrors = throwParseErrors;
    }

    public EnhancedParser() {
        this.parser.removeErrorListeners();
        this.lexer.removeErrorListeners();
        this.parsingStrategy = ParsingStrategy.SLL_AND_LL_ON_ERROR;
        this.tokenSource = new FilterTokenSource(this.lexer);
    }

    @Override
    public void setThrowParseErrors(boolean throwParseErrors) {
        this.throwParseErrors = throwParseErrors;
    }

    @Override
    public void setParsingStrategy(ParsingStrategy parsingStrategy) {
        this.parsingStrategy = parsingStrategy;
    }

    @Override
    public void setSLLOnly() {
        this.setParsingStrategy(ParsingStrategy.SLL_ONLY);
    }

    @Override
    public void setLLOnly() {
        this.setParsingStrategy(ParsingStrategy.LL_ONLY);
    }

    @Override
    public GLSLParser getParser() {
        return this.parser;
    }

    @Override
    public GLSLLexer getLexer() {
        return this.lexer;
    }

    public BufferedTokenStream getTokenStream() {
        return this.tokenStream;
    }

    @Override
    public void setTokenFilter(TokenFilter<?> tokenFilter) {
        this.tokenSource.setTokenFilter(tokenFilter);
    }

    public GLSLParser.TranslationUnitContext parse(String str) {
        return this.parse(str, ParseShape.TRANSLATION_UNIT);
    }

    public <C extends ParserRuleContext> C parse(String str, Function<GLSLParser, C> parseMethod) {
        return this.parse(str, (ParserRuleContext)null, parseMethod);
    }

    public <C extends ParserRuleContext> C parse(String str, ParseShape<C, ?> parseShape) {
        return this.parse(str, (ParserRuleContext)null, parseShape.parseMethod);
    }

    public <C extends ParserRuleContext> C parse(String str, ParserRuleContext parent, Function<GLSLParser, C> parseMethod) {
        return this.parse(CharStreams.fromString(str), parent, parseMethod);
    }

    public <C extends ParserRuleContext> C parse(String str, ParserRuleContext parent, ParseShape<C, ?> parseShape) {
        return this.parse(CharStreams.fromString(str), parent, parseShape.parseMethod);
    }

    private <C extends ParserRuleContext> C parse(IntStream stream, ParserRuleContext parent, Function<GLSLParser, C> parseMethod) {
        ParserRuleContext node;
        block17: {
            this.input = stream;
            this.lexer.setInputStream(this.input);
            if (this.throwParseErrors) {
                this.lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
            } else {
                this.lexer.removeErrorListener(ThrowingErrorListener.INSTANCE);
            }
            this.lexer.reset();
            this.tokenSource.resetState();
            this.tokenStream = new CommonTokenStream(this.tokenSource);
            this.parser.setTokenStream(this.tokenStream);
            try {
                if (this.parsingStrategy == ParsingStrategy.SLL_AND_LL_ON_ERROR) {
                    this.parser.removeErrorListener(ThrowingErrorListener.INSTANCE);
                    ((ParserATNSimulator)this.parser.getInterpreter()).setPredictionMode(PredictionMode.SLL);
                    this.parser.setErrorHandler(new BailErrorStrategy());
                    try {
                        node = (ParserRuleContext)parseMethod.apply(this.parser);
                        break block17;
                    }
                    catch (ParseCancellationException SLLException) {
                        this.lexer.reset();
                        this.parser.reset();
                        if (this.throwParseErrors) {
                            this.parser.addErrorListener(ThrowingErrorListener.INSTANCE);
                        } else {
                            this.parser.removeErrorListener(ThrowingErrorListener.INSTANCE);
                        }
                        this.parser.setErrorHandler(new DefaultErrorStrategy());
                        ((ParserATNSimulator)this.parser.getInterpreter()).setPredictionMode(PredictionMode.LL);
                        ParseCancellationException possibleLLException = null;
                        try {
                            node = (ParserRuleContext)parseMethod.apply(this.parser);
                            break block17;
                        }
                        catch (ParseCancellationException LLException) {
                            possibleLLException = LLException;
                            throw LLException;
                        }
                        finally {
                            if (this.internalErrorConsumer != null) {
                                this.internalErrorConsumer.accept(SLLException, possibleLLException);
                            }
                        }
                    }
                }
                ((ParserATNSimulator)this.parser.getInterpreter()).setPredictionMode(this.parsingStrategy == ParsingStrategy.SLL_ONLY ? PredictionMode.SLL : PredictionMode.LL);
                if (this.throwParseErrors) {
                    this.parser.addErrorListener(ThrowingErrorListener.INSTANCE);
                } else {
                    this.parser.removeErrorListener(ThrowingErrorListener.INSTANCE);
                }
                this.parser.setErrorHandler(new DefaultErrorStrategy());
                node = (ParserRuleContext)parseMethod.apply(this.parser);
            }
            catch (ParseCancellationException e) {
                throw this.handleParseCancellationException(e);
            }
        }
        node.setParent(parent);
        return (C)node;
    }

    private RuntimeException handleParseCancellationException(ParseCancellationException e) {
        RecognitionException recognitionException;
        Throwable throwable = e.getCause();
        if (throwable instanceof RecognitionException && (recognitionException = (RecognitionException)throwable) instanceof InputMismatchException) {
            InputMismatchException inputMismatchException = (InputMismatchException)recognitionException;
            IntervalSet expectedTokens = inputMismatchException.getExpectedTokens();
            if (expectedTokens.size() == 2 && expectedTokens.contains(243) && expectedTokens.contains(246)) {
                return new ParsingException("Missing semicolon or comma!", e);
            }
            Token offendingToken = inputMismatchException.getOffendingToken();
            if (offendingToken.getType() == -1) {
                return new ParsingException("Unexpected end of file!", e);
            }
            return new ParsingException("Unexpected token '" + inputMismatchException.getOffendingToken().getText() + "'", e);
        }
        return e;
    }

    public static enum ParsingStrategy {
        SLL_AND_LL_ON_ERROR,
        SLL_ONLY,
        LL_ONLY;

    }

    private static class ThrowingErrorListener
    extends BaseErrorListener {
        public static final ThrowingErrorListener INSTANCE = new ThrowingErrorListener();

        private ThrowingErrorListener() {
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) throws ParseCancellationException {
            throw new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg, e);
        }
    }
}

