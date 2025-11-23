/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.shaderpack.option.values;

import java.util.Optional;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.helpers.OptionalBoolean;
import net.irisshaders.iris.shaderpack.option.MergedBooleanOption;
import net.irisshaders.iris.shaderpack.option.MergedStringOption;
import net.irisshaders.iris.shaderpack.option.OptionSet;
import net.irisshaders.iris.shaderpack.option.values.ImmutableOptionValues;
import net.irisshaders.iris.shaderpack.option.values.MutableOptionValues;

public interface OptionValues {
    public OptionalBoolean getBooleanValue(String var1);

    public Optional<String> getStringValue(String var1);

    default public boolean getBooleanValueOrDefault(String name) {
        return this.getBooleanValue(name).orElseGet(() -> {
            if (!this.getOptionSet().getBooleanOptions().containsKey((Object)name)) {
                Iris.logger.warn("Tried to get boolean value for unknown option: " + name + ", defaulting to true!");
                return true;
            }
            return ((MergedBooleanOption)this.getOptionSet().getBooleanOptions().get((Object)name)).getOption().getDefaultValue();
        });
    }

    default public String getStringValueOrDefault(String name) {
        return this.getStringValue(name).orElseGet(() -> ((MergedStringOption)this.getOptionSet().getStringOptions().get((Object)name)).getOption().getDefaultValue());
    }

    public int getOptionsChanged();

    public MutableOptionValues mutableCopy();

    public ImmutableOptionValues toImmutable();

    public OptionSet getOptionSet();
}

