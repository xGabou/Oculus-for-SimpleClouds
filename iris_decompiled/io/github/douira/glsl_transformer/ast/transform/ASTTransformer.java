/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.transform;

import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.external_declaration.ExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.print.PrintType;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.query.RootSupplier;
import io.github.douira.glsl_transformer.ast.transform.ASTParser;
import io.github.douira.glsl_transformer.ast.transform.JobParameters;
import io.github.douira.glsl_transformer.ast.transform.ParameterizedTransformer;
import io.github.douira.glsl_transformer.token_filter.TokenFilter;
import java.util.Objects;

public abstract class ASTTransformer<J extends JobParameters, V>
extends ASTParser
implements ParameterizedTransformer<J, V> {
    private J jobParameters;
    private PrintType printType = PrintType.COMPACT;
    private RootSupplier rootSupplier = RootSupplier.DEFAULT;

    @Override
    public J getJobParameters() {
        return this.jobParameters;
    }

    @Override
    public void setJobParameters(J parameters) {
        this.jobParameters = parameters;
    }

    public PrintType getPrintType() {
        return this.printType;
    }

    public void setPrintType(PrintType printType) {
        this.printType = printType;
    }

    @Override
    public void setTokenFilter(TokenFilter<?> tokenFilter) {
        super.setTokenFilter(tokenFilter);
        tokenFilter.setJobParametersSupplier(() -> this.getJobParameters());
    }

    public RootSupplier getRootSupplier() {
        return this.rootSupplier;
    }

    public void setRootSupplier(RootSupplier rootSupplier) {
        Objects.requireNonNull(rootSupplier);
        this.rootSupplier = rootSupplier;
    }

    public Root supplyRoot() {
        return this.rootSupplier.get();
    }

    @Override
    public V transform(V input) {
        return this.transform(this.rootSupplier, input);
    }

    @Override
    public abstract V transform(RootSupplier var1, V var2);

    public TranslationUnit parseSeparateTranslationUnit(String input) {
        return this.parseTranslationUnit(this.rootSupplier, input);
    }

    public ExternalDeclaration parseSeparateExternalDeclaration(String input) {
        return this.parseExternalDeclaration(this.rootSupplier, input);
    }

    public Expression parseSeparateExpression(String input) {
        return this.parseExpression(this.rootSupplier, input);
    }

    public Statement parseSeparateStatement(String input) {
        return this.parseStatement(this.rootSupplier, input);
    }
}

