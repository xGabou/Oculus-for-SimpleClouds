/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 */
package net.irisshaders.iris.shaderpack.option;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.irisshaders.iris.shaderpack.option.OptionSet;
import net.irisshaders.iris.shaderpack.option.values.OptionValues;

public final class Profile {
    public final String name;
    public final int precedence;
    public final Map<String, String> optionValues;
    public final List<String> disabledPrograms;

    private Profile(String name, Map<String, String> optionValues, List<String> disabledPrograms) {
        this.name = name;
        this.optionValues = optionValues;
        this.precedence = optionValues.size();
        this.disabledPrograms = disabledPrograms;
    }

    public boolean matches(OptionSet options, OptionValues values) {
        for (Map.Entry<String, String> entry : this.optionValues.entrySet()) {
            String currentValue;
            boolean currentValue2;
            String option = entry.getKey();
            String value = entry.getValue();
            if (options.getBooleanOptions().containsKey((Object)option) && !Boolean.toString(currentValue2 = values.getBooleanValueOrDefault(option)).equals(value)) {
                return false;
            }
            if (!options.getStringOptions().containsKey((Object)option) || value.equals(currentValue = values.getStringValueOrDefault(option))) continue;
            return false;
        }
        return true;
    }

    public static class Builder {
        private final String name;
        private final Map<String, String> optionValues = new HashMap<String, String>();
        private final List<String> disabledPrograms = new ArrayList<String>();

        public Builder(String name) {
            this.name = name;
        }

        public Builder option(String optionId, String value) {
            this.optionValues.put(optionId, value);
            return this;
        }

        public Builder disableProgram(String programId) {
            this.disabledPrograms.add(programId);
            return this;
        }

        public Builder addAll(Profile other) {
            this.optionValues.putAll(other.optionValues);
            this.disabledPrograms.addAll(other.disabledPrograms);
            return this;
        }

        public Profile build() {
            return new Profile(this.name, (Map<String, String>)ImmutableMap.copyOf(this.optionValues), (List<String>)ImmutableList.copyOf(this.disabledPrograms));
        }
    }
}

