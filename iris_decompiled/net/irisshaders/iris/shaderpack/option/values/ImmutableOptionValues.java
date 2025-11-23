/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.irisshaders.iris.shaderpack.option.values;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.irisshaders.iris.helpers.OptionalBoolean;
import net.irisshaders.iris.shaderpack.option.OptionSet;
import net.irisshaders.iris.shaderpack.option.values.MutableOptionValues;
import net.irisshaders.iris.shaderpack.option.values.OptionValues;

public class ImmutableOptionValues
implements OptionValues {
    private final OptionSet options;
    private final ImmutableMap<String, Boolean> booleanValues;
    private final ImmutableMap<String, String> stringValues;

    ImmutableOptionValues(OptionSet options, ImmutableMap<String, Boolean> booleanValues, ImmutableMap<String, String> stringValues) {
        this.options = options;
        this.booleanValues = booleanValues;
        this.stringValues = stringValues;
    }

    @Override
    public OptionalBoolean getBooleanValue(String name) {
        if (this.booleanValues.containsKey((Object)name)) {
            return (Boolean)this.booleanValues.get((Object)name) != false ? OptionalBoolean.TRUE : OptionalBoolean.FALSE;
        }
        return OptionalBoolean.DEFAULT;
    }

    @Override
    public Optional<String> getStringValue(String name) {
        return Optional.ofNullable((String)this.stringValues.get((Object)name));
    }

    @Override
    public int getOptionsChanged() {
        return this.stringValues.size() + this.booleanValues.size();
    }

    @Override
    public MutableOptionValues mutableCopy() {
        return new MutableOptionValues(this.options, new HashMap<String, Boolean>((Map<String, Boolean>)this.booleanValues), new HashMap<String, String>((Map<String, String>)this.stringValues));
    }

    @Override
    public ImmutableOptionValues toImmutable() {
        return this;
    }

    @Override
    public OptionSet getOptionSet() {
        return this.options;
    }
}

