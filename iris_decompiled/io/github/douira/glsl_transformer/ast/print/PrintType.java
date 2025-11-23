/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.print;

import io.github.douira.glsl_transformer.ast.print.CompactPrinter;
import io.github.douira.glsl_transformer.ast.print.IndentingPrinter;
import io.github.douira.glsl_transformer.ast.print.LineAnnotator;
import io.github.douira.glsl_transformer.ast.print.SimplePrinter;
import io.github.douira.glsl_transformer.ast.print.TokenProcessor;
import java.util.function.Supplier;

public enum PrintType {
    SIMPLE(SimplePrinter::new),
    INDENTED(IndentingPrinter::new),
    COMPACT(CompactPrinter::new),
    SIMPLE_ANNOTATED(() -> new LineAnnotator(new SimplePrinter())),
    INDENTED_ANNOTATED(() -> new LineAnnotator(new IndentingPrinter())),
    COMPACT_ANNOTATED(() -> new LineAnnotator(new CompactPrinter()));

    private final Supplier<TokenProcessor> printerSupplier;

    private PrintType(Supplier<TokenProcessor> printerSupplier) {
        this.printerSupplier = printerSupplier;
    }

    public TokenProcessor getTokenProcessor() {
        return this.printerSupplier.get();
    }
}

