/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.transform;

import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.transform.EnumASTTransformer;
import io.github.douira.glsl_transformer.ast.transform.JobParameters;
import io.github.douira.glsl_transformer.ast.transform.TriFullTransformation;
import io.github.douira.glsl_transformer.ast.transform.TriRootOnlyTransformation;
import io.github.douira.glsl_transformer.util.TriConsumer;
import io.github.douira.glsl_transformer.util.Triple;
import java.util.EnumMap;
import java.util.function.Consumer;

public class TriASTTransformer<J extends JobParameters, E extends Enum<E>>
extends EnumASTTransformer<J, E> {
    private final E aType;
    private final E bType;
    private final E cType;
    private final Class<E> enumClass;

    public TriASTTransformer(Consumer<EnumMap<E, TranslationUnit>> transformation, Class<E> enumClass, E aType, E bType, E cType) {
        super(transformation, enumClass);
        this.aType = aType;
        this.bType = bType;
        this.cType = cType;
        this.enumClass = enumClass;
    }

    public TriASTTransformer(Class<E> enumClass, E aType, E bType, E cType) {
        super(enumClass);
        this.aType = aType;
        this.bType = bType;
        this.cType = cType;
        this.enumClass = enumClass;
    }

    public TriASTTransformer(TriConsumer<TranslationUnit, TranslationUnit, TranslationUnit> transformation, Class<E> enumClass, E aType, E bType, E cType) {
        this(enumClass, aType, bType, cType);
        this.setTransformation(transformation);
    }

    public TriASTTransformer(TriRootOnlyTransformation<TranslationUnit> transformation, Class<E> enumClass, E aType, E bType, E cType) {
        this(enumClass, aType, bType, cType);
        this.setTransformation(transformation);
    }

    public TriASTTransformer(TriFullTransformation<TranslationUnit, J> transformation, Class<E> enumClass, E aType, E bType, E cType) {
        this(enumClass, aType, bType, cType);
        this.setTransformation(transformation);
    }

    public void setTransformation(TriConsumer<TranslationUnit, TranslationUnit, TranslationUnit> transformation) {
        super.setTransformation((N map) -> transformation.accept((TranslationUnit)map.get(this.aType), (TranslationUnit)map.get(this.bType), (TranslationUnit)map.get(this.cType)));
    }

    @Override
    public void setTransformation(TriRootOnlyTransformation<TranslationUnit> transformation) {
        super.setTransformation((N map) -> {
            TranslationUnit a = (TranslationUnit)map.get(this.aType);
            TranslationUnit b = (TranslationUnit)map.get(this.bType);
            TranslationUnit c = (TranslationUnit)map.get(this.cType);
            transformation.accept(a, b, c, a == null ? null : a.getRoot(), b == null ? null : b.getRoot(), c == null ? null : c.getRoot());
        });
    }

    @Override
    public void setTransformation(TriFullTransformation<TranslationUnit, J> transformation) {
        super.setTransformation((N map) -> {
            TranslationUnit a = (TranslationUnit)map.get(this.aType);
            TranslationUnit b = (TranslationUnit)map.get(this.bType);
            TranslationUnit c = (TranslationUnit)map.get(this.cType);
            transformation.accept(a, b, c, a == null ? null : a.getRoot(), b == null ? null : b.getRoot(), c == null ? null : c.getRoot(), this.getJobParameters());
        });
    }

    @Override
    public void setEnumType(Class<E> enumClass) {
        throw new UnsupportedOperationException("The tri enum map types may not be changed.");
    }

    public EnumMap<E, String> transform(String a, String b, String c) {
        EnumMap<E, String> items = new EnumMap<E, String>(this.enumClass);
        items.put(this.aType, a);
        items.put(this.bType, b);
        items.put(this.cType, c);
        return (EnumMap)this.transform(this.getRootSupplier(), items);
    }

    public EnumMap<E, String> transform(String a, String b, String c, J parameters) {
        return this.withJobParameters(parameters, () -> this.transform(a, b, c));
    }

    @Override
    public Triple<String> transform(Triple<String> str) {
        EnumMap<E, String> result = this.transform((String)str.a, (String)str.b, (String)str.c);
        return new Triple<String>(result.get(this.aType), result.get(this.bType), result.get(this.cType));
    }

    @Override
    public Triple<String> transform(Triple<String> str, J parameters) {
        return this.withJobParameters(parameters, () -> this.transform(str));
    }
}

