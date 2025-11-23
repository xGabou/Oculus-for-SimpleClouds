/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.transform;

import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.print.ASTPrinter;
import io.github.douira.glsl_transformer.ast.query.RootSupplier;
import io.github.douira.glsl_transformer.ast.transform.ASTTransformer;
import io.github.douira.glsl_transformer.ast.transform.JobParameters;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GroupedASTTransformer<J extends JobParameters, K, M extends Map<K, String>, N extends Map<K, TranslationUnit>>
extends ASTTransformer<J, Map<K, String>> {
    private Consumer<N> transformation;
    private Supplier<N> tuMapSupplier;
    private Supplier<M> resultMapSupplier;

    public GroupedASTTransformer(Supplier<N> tuMapSupplier, Supplier<M> resultMapSupplier) {
        this.tuMapSupplier = tuMapSupplier;
        this.resultMapSupplier = resultMapSupplier;
    }

    public GroupedASTTransformer(Consumer<N> transformation, Supplier<N> tuMapSupplier, Supplier<M> resultMapSupplier) {
        this.setTransformation(transformation);
        this.tuMapSupplier = tuMapSupplier;
        this.resultMapSupplier = resultMapSupplier;
    }

    public GroupedASTTransformer(BiConsumer<N, J> transformation, Supplier<N> tuMapSupplier, Supplier<M> resultMapSupplier) {
        this.setTransformation(transformation);
        this.tuMapSupplier = tuMapSupplier;
        this.resultMapSupplier = resultMapSupplier;
    }

    public GroupedASTTransformer(Consumer<N> transformation) {
        this.transformation = transformation;
    }

    public GroupedASTTransformer() {
    }

    public void setTransformation(Consumer<N> transformation) {
        this.transformation = transformation;
    }

    public void setTransformation(BiConsumer<N, J> transformation) {
        this.transformation = trees -> transformation.accept(trees, this.getJobParameters());
    }

    public void setTuMapSupplier(Supplier<N> tuMapSupplier) {
        this.tuMapSupplier = tuMapSupplier;
    }

    public void setResultMapSupplier(Supplier<M> resultMapSupplier) {
        this.resultMapSupplier = resultMapSupplier;
    }

    @Override
    public M transform(RootSupplier rootSupplier, Map<K, String> items) {
        Map translationUnits = (Map)this.tuMapSupplier.get();
        for (Map.Entry<K, String> entry : items.entrySet()) {
            String value = entry.getValue();
            translationUnits.put(entry.getKey(), value == null ? null : this.parseTranslationUnit(rootSupplier, value));
        }
        this.transformation.accept(translationUnits);
        Map printedItems = (Map)this.resultMapSupplier.get();
        for (Map.Entry entry : translationUnits.entrySet()) {
            TranslationUnit value = (TranslationUnit)entry.getValue();
            printedItems.put(entry.getKey(), value == null ? null : ASTPrinter.print(this.getPrintType(), value));
        }
        return (M)printedItems;
    }
}

