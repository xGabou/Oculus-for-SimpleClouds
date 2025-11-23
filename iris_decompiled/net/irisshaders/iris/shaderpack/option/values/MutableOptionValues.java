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
import net.irisshaders.iris.shaderpack.option.values.ImmutableOptionValues;
import net.irisshaders.iris.shaderpack.option.values.OptionValues;

public class MutableOptionValues
implements OptionValues {
    private final OptionSet options;
    private final Map<String, Boolean> booleanValues;
    private final Map<String, String> stringValues;

    MutableOptionValues(OptionSet options, Map<String, Boolean> booleanValues, Map<String, String> stringValues) {
        HashMap<String, String> values = new HashMap<String, String>();
        booleanValues.forEach((k, v) -> values.put((String)k, Boolean.toString(v)));
        values.putAll(stringValues);
        this.options = options;
        this.booleanValues = new HashMap<String, Boolean>();
        this.stringValues = new HashMap<String, String>();
        this.addAll(values);
    }

    public MutableOptionValues(OptionSet options, Map<String, String> values) {
        this.options = options;
        this.booleanValues = new HashMap<String, Boolean>();
        this.stringValues = new HashMap<String, String>();
        this.addAll(values);
    }

    public OptionSet getOptions() {
        return this.options;
    }

    public Map<String, Boolean> getBooleanValues() {
        return this.booleanValues;
    }

    public Map<String, String> getStringValues() {
        return this.stringValues;
    }

    public void addAll(Map<String, String> values) {
        this.options.getBooleanOptions().forEach((name, option) -> {
            String value = (String)values.get(name);
            if (value == null) {
                return;
            }
            OptionalBoolean booleanValue = value.equals("false") ? OptionalBoolean.FALSE : (value.equals("true") ? OptionalBoolean.TRUE : OptionalBoolean.DEFAULT);
            boolean actualValue = booleanValue.orElse(option.getOption().getDefaultValue());
            if (actualValue == option.getOption().getDefaultValue()) {
                this.booleanValues.remove(name);
                return;
            }
            this.booleanValues.put((String)name, actualValue);
        });
        this.options.getStringOptions().forEach((name, option) -> {
            String value = (String)values.get(name);
            if (value == null) {
                return;
            }
            if (value.equals(option.getOption().getDefaultValue())) {
                this.stringValues.remove(name);
                return;
            }
            this.stringValues.put((String)name, value);
        });
    }

    @Override
    public OptionalBoolean getBooleanValue(String name) {
        if (this.booleanValues.containsKey(name)) {
            return this.booleanValues.get(name) != false ? OptionalBoolean.TRUE : OptionalBoolean.FALSE;
        }
        return OptionalBoolean.DEFAULT;
    }

    @Override
    public Optional<String> getStringValue(String name) {
        return Optional.ofNullable(this.stringValues.get(name));
    }

    @Override
    public int getOptionsChanged() {
        return this.stringValues.size() + this.booleanValues.size();
    }

    @Override
    public MutableOptionValues mutableCopy() {
        return new MutableOptionValues(this.options, new HashMap<String, Boolean>(this.booleanValues), new HashMap<String, String>(this.stringValues));
    }

    @Override
    public ImmutableOptionValues toImmutable() {
        return new ImmutableOptionValues(this.options, (ImmutableMap<String, Boolean>)ImmutableMap.copyOf(this.booleanValues), (ImmutableMap<String, String>)ImmutableMap.copyOf(this.stringValues));
    }

    @Override
    public OptionSet getOptionSet() {
        return this.options;
    }
}

