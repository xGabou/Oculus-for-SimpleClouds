/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.transform;

import java.util.function.Supplier;

public class TransformationException
extends RuntimeException {
    public TransformationException(String message) {
        super(message);
    }

    public TransformationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransformationException(Throwable cause) {
        super(cause);
    }

    public TransformationException() {
    }

    public static Runnable thrower(String message) {
        return () -> {
            throw new TransformationException(message);
        };
    }

    public static Supplier<TransformationException> supplier(String message) {
        return () -> new TransformationException(message);
    }
}

