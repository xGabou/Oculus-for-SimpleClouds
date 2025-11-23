/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.transform;

import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.print.ASTPrinter;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.query.RootSupplier;
import io.github.douira.glsl_transformer.ast.transform.ASTTransformer;
import io.github.douira.glsl_transformer.ast.transform.JobParameters;
import io.github.douira.glsl_transformer.ast.transform.ParameterizedTransformer;
import io.github.douira.glsl_transformer.util.TriConsumer;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SingleASTTransformer<J extends JobParameters>
extends ASTTransformer<J, String> {
    public static final Consumer<TranslationUnit> IDENTITY_TRANSFORMATION = tu -> {};
    private Consumer<TranslationUnit> transformation;

    public SingleASTTransformer() {
    }

    public SingleASTTransformer(Consumer<TranslationUnit> transformation) {
        this.setTransformation(transformation);
    }

    public SingleASTTransformer(BiConsumer<TranslationUnit, Root> transformation) {
        this.setTransformation(transformation);
    }

    public SingleASTTransformer(TriConsumer<TranslationUnit, Root, J> transformation) {
        this.setTransformation(transformation);
    }

    public void setTransformation(Consumer<TranslationUnit> transformation) {
        this.transformation = transformation;
    }

    public void setTransformation(BiConsumer<TranslationUnit, Root> transformation) {
        this.transformation = SingleASTTransformer.wrapTransformation(this, transformation);
    }

    public void setTransformation(TriConsumer<TranslationUnit, Root, J> transformation) {
        this.transformation = SingleASTTransformer.wrapTransformation(this, transformation);
    }

    public static <T, R> Consumer<TranslationUnit> wrapTransformation(ParameterizedTransformer<T, R> transformer, TriConsumer<TranslationUnit, Root, T> transformation) {
        return translationUnit -> transformation.accept((TranslationUnit)translationUnit, translationUnit.getRoot(), (Object)transformer.getJobParameters());
    }

    public static <R> Consumer<TranslationUnit> wrapTransformation(ParameterizedTransformer<?, R> transformer, BiConsumer<TranslationUnit, Root> transformation) {
        return translationUnit -> transformation.accept((TranslationUnit)translationUnit, translationUnit.getRoot());
    }

    @Override
    public String transform(RootSupplier rootSupplier, String str) {
        TranslationUnit translationUnit = this.parseTranslationUnit(rootSupplier, str);
        this.transformation.accept(translationUnit);
        return ASTPrinter.print(this.getPrintType(), translationUnit);
    }
}

