/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.util;

import java.util.Objects;
import java.util.function.Supplier;

public class ConfigUtil {
    public static <R> R withDefault(R setValue, Supplier<R> defaultGenerator) {
        return setValue == null ? ConfigUtil.withDefault(setValue, defaultGenerator.get()) : setValue;
    }

    public static <R> R withDefault(Supplier<R> setValueSupplier, R defaultValue) {
        return (R)(setValueSupplier == null ? ConfigUtil.withDefault(null, defaultValue) : setValueSupplier.get());
    }

    public static <R> R withDefault(R setValue, R defaultValue) {
        if (setValue == null) {
            Objects.requireNonNull(defaultValue, "Generated default value is null!");
            return defaultValue;
        }
        return setValue;
    }
}

