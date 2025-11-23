/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.irisshaders.iris.shaderpack.option;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.shaderpack.option.BooleanOption;
import net.irisshaders.iris.shaderpack.option.MergedBooleanOption;
import net.irisshaders.iris.shaderpack.option.MergedStringOption;
import net.irisshaders.iris.shaderpack.option.OptionLocation;
import net.irisshaders.iris.shaderpack.option.StringOption;

public class OptionSet {
    private final ImmutableMap<String, MergedBooleanOption> booleanOptions;
    private final ImmutableMap<String, MergedStringOption> stringOptions;

    private OptionSet(Builder builder) {
        this.booleanOptions = ImmutableMap.copyOf(builder.booleanOptions);
        this.stringOptions = ImmutableMap.copyOf(builder.stringOptions);
    }

    public static Builder builder() {
        return new Builder();
    }

    public ImmutableMap<String, MergedBooleanOption> getBooleanOptions() {
        return this.booleanOptions;
    }

    public ImmutableMap<String, MergedStringOption> getStringOptions() {
        return this.stringOptions;
    }

    public boolean isBooleanOption(String name) {
        return this.booleanOptions.containsKey((Object)name);
    }

    public static class Builder {
        private final Map<String, MergedBooleanOption> booleanOptions = new HashMap<String, MergedBooleanOption>();
        private final Map<String, MergedStringOption> stringOptions = new HashMap<String, MergedStringOption>();

        public void addAll(OptionSet other) {
            if (this.booleanOptions.isEmpty()) {
                this.booleanOptions.putAll((Map<String, MergedBooleanOption>)other.booleanOptions);
            } else {
                other.booleanOptions.values().forEach(this::addBooleanOption);
            }
            if (this.stringOptions.isEmpty()) {
                this.stringOptions.putAll((Map<String, MergedStringOption>)other.stringOptions);
            } else {
                other.stringOptions.values().forEach(this::addStringOption);
            }
        }

        public void addBooleanOption(OptionLocation location, BooleanOption option) {
            this.addBooleanOption(new MergedBooleanOption(location, option));
        }

        public void addBooleanOption(MergedBooleanOption proposed) {
            MergedBooleanOption merged;
            BooleanOption option = proposed.getOption();
            MergedBooleanOption existing = this.booleanOptions.get(option.getName());
            if (existing != null) {
                merged = existing.merge(proposed);
                if (merged == null) {
                    Iris.logger.warn("Ignoring ambiguous boolean option " + option.getName());
                    this.booleanOptions.remove(option.getName());
                    return;
                }
            } else {
                merged = proposed;
            }
            this.booleanOptions.put(option.getName(), merged);
        }

        public void addStringOption(OptionLocation location, StringOption option) {
            this.addStringOption(new MergedStringOption(location, option));
        }

        public void addStringOption(MergedStringOption proposed) {
            MergedStringOption merged;
            StringOption option = proposed.getOption();
            MergedStringOption existing = this.stringOptions.get(option.getName());
            if (existing != null) {
                merged = existing.merge(proposed);
                if (merged == null) {
                    Iris.logger.warn("Ignoring ambiguous string option " + option.getName());
                    this.stringOptions.remove(option.getName());
                    return;
                }
            } else {
                merged = proposed;
            }
            this.stringOptions.put(option.getName(), merged);
        }

        public OptionSet build() {
            return new OptionSet(this);
        }
    }
}

