/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.query;

import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.query.index.ExternalDeclarationIndex;
import io.github.douira.glsl_transformer.ast.query.index.IdentifierIndex;
import io.github.douira.glsl_transformer.ast.query.index.NodeIndex;
import io.github.douira.glsl_transformer.ast.query.index.PrefixExternalDeclarationIndex;
import io.github.douira.glsl_transformer.ast.query.index.PrefixIdentifierIndex;
import java.util.function.Supplier;

public class RootSupplier
implements Supplier<Root> {
    public static final RootSupplier EXACT_UNORDERED = new RootSupplier(NodeIndex::withUnordered, IdentifierIndex::withOnlyExact);
    public static final RootSupplier PREFIX_UNORDERED = new RootSupplier(NodeIndex::withUnordered, PrefixIdentifierIndex::withPrefix);
    public static final RootSupplier EXACT_UNORDERED_ED_EXACT = new RootSupplier(NodeIndex::withUnordered, IdentifierIndex::withOnlyExact, ExternalDeclarationIndex::withOnlyExact);
    public static final RootSupplier PREFIX_UNORDERED_ED_EXACT = new RootSupplier(NodeIndex::withUnordered, PrefixIdentifierIndex::withPrefix, ExternalDeclarationIndex::withOnlyExact);
    public static final RootSupplier PREFIX_UNORDERED_ED_PREFIX = new RootSupplier(NodeIndex::withUnordered, PrefixIdentifierIndex::withPrefix, PrefixExternalDeclarationIndex::withPrefix);
    public static final RootSupplier EMPTY = new RootSupplier(RootSupplier.supplier(null), RootSupplier.supplier(null));
    public static final RootSupplier ONLY_NODE_INDEX = new RootSupplier(NodeIndex::withUnordered, RootSupplier.supplier(null));
    public static final RootSupplier ONLY_IDENTIFIER_INDEX = new RootSupplier(RootSupplier.supplier(null), IdentifierIndex::withOnlyExact);
    public static final RootSupplier DEFAULT = EXACT_UNORDERED;
    private final Supplier<NodeIndex<?>> nodeIndexSupplier;
    private final Supplier<IdentifierIndex<?, ?>> identifierIndexSupplier;
    private final Supplier<ExternalDeclarationIndex<?, ?>> externalDeclarationIndexSupplier;

    private static final <V> Supplier<V> supplier(V value) {
        return () -> value;
    }

    public static Root supplyDefault() {
        return DEFAULT.get();
    }

    public RootSupplier(Supplier<NodeIndex<?>> nodeIndexSupplier, Supplier<IdentifierIndex<?, ?>> identifierIndexSupplier, Supplier<ExternalDeclarationIndex<?, ?>> externalDeclarationIndexSupplier) {
        this.nodeIndexSupplier = nodeIndexSupplier;
        this.identifierIndexSupplier = identifierIndexSupplier;
        this.externalDeclarationIndexSupplier = externalDeclarationIndexSupplier;
    }

    public RootSupplier(Supplier<NodeIndex<?>> nodeIndexSupplier, Supplier<IdentifierIndex<?, ?>> identifierIndexSupplier) {
        this(nodeIndexSupplier, identifierIndexSupplier, RootSupplier.supplier(null));
    }

    @Override
    public Root get() {
        return new Root(this.nodeIndexSupplier.get(), this.identifierIndexSupplier.get(), this.externalDeclarationIndexSupplier.get());
    }

    public RootSupplier setNodeIndex(Supplier<NodeIndex<?>> nodeIndexSupplier) {
        return new RootSupplier(nodeIndexSupplier, this.identifierIndexSupplier);
    }

    public RootSupplier setIdentifierIndex(Supplier<IdentifierIndex<?, ?>> identifierIndexSupplier) {
        return new RootSupplier(this.nodeIndexSupplier, identifierIndexSupplier);
    }
}

