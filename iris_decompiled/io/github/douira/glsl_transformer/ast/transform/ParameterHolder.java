/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.transform;

import java.util.function.Supplier;

public interface ParameterHolder<J> {
    public J getJobParameters();

    public void setJobParameters(J var1);

    default public <R> R withJobParameters(J parameters, Supplier<R> run) {
        this.setJobParameters(parameters);
        R value = run.get();
        this.setJobParameters(null);
        return value;
    }
}

