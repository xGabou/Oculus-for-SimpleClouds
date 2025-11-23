/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.shaderpack.option;

import java.util.Optional;
import net.irisshaders.iris.shaderpack.option.OptionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseOption {
    @NotNull
    private final OptionType type;
    @NotNull
    private final String name;
    @Nullable
    private final String comment;

    BaseOption(@NotNull OptionType type, @NotNull String name, @Nullable String comment) {
        this.type = type;
        this.name = name;
        this.comment = comment == null || comment.isEmpty() ? null : comment;
    }

    @NotNull
    public OptionType getType() {
        return this.type;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public Optional<String> getComment() {
        return Optional.ofNullable(this.comment);
    }
}

