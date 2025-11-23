/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package net.irisshaders.iris.helpers;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public record StringPair(String key, String value) {
    public StringPair(@NotNull String key, @NotNull String value) {
        this.key = Objects.requireNonNull(key);
        this.value = Objects.requireNonNull(value);
    }
}

