/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.shaderpack.option;

import com.google.common.collect.ImmutableList;
import java.util.Objects;
import net.irisshaders.iris.shaderpack.option.BaseOption;
import net.irisshaders.iris.shaderpack.option.OptionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringOption
extends BaseOption {
    private final String defaultValue;
    private final ImmutableList<String> allowedValues;

    private StringOption(OptionType type, String name, String defaultValue) {
        super(type, name, null);
        this.defaultValue = Objects.requireNonNull(defaultValue);
        this.allowedValues = ImmutableList.of((Object)defaultValue);
    }

    private StringOption(OptionType type, String name, String comment, String defaultValue, ImmutableList<String> allowedValues) {
        super(type, name, comment);
        this.defaultValue = Objects.requireNonNull(defaultValue);
        this.allowedValues = allowedValues;
    }

    @Nullable
    public static StringOption create(OptionType type, String name, String comment, String defaultValue) {
        if (comment == null) {
            return null;
        }
        int openingBracket = ((String)comment).indexOf(91);
        if (openingBracket == -1) {
            return null;
        }
        int closingBracket = ((String)comment).indexOf(93, openingBracket);
        if (closingBracket == -1) {
            return null;
        }
        Object[] allowedValues = ((String)comment).substring(openingBracket + 1, closingBracket).split(" ");
        comment = ((String)comment).substring(0, openingBracket) + ((String)comment).substring(closingBracket + 1);
        boolean allowedValuesContainsDefaultValue = false;
        for (String string : allowedValues) {
            if (!defaultValue.equals(string)) continue;
            allowedValuesContainsDefaultValue = true;
            break;
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add(allowedValues);
        if (!allowedValuesContainsDefaultValue) {
            builder.add((Object)defaultValue);
        }
        return new StringOption(type, name, ((String)comment).trim(), defaultValue, (ImmutableList<String>)builder.build());
    }

    @NotNull
    public String getDefaultValue() {
        return this.defaultValue;
    }

    @NotNull
    public ImmutableList<String> getAllowedValues() {
        return this.allowedValues;
    }
}

