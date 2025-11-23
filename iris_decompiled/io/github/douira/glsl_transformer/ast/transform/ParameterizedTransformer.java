/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.transform;

import io.github.douira.glsl_transformer.ast.transform.ParameterHolder;
import io.github.douira.glsl_transformer.ast.transform.Transformer;

public interface ParameterizedTransformer<J, V>
extends ParameterHolder<J>,
Transformer<V> {
    default public V transform(V input, J parameters) {
        return (V)this.withJobParameters(parameters, () -> this.transform(input));
    }
}

