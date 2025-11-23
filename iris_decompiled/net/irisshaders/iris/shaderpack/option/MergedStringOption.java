/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.shaderpack.option;

import com.google.common.collect.ImmutableSet;
import net.irisshaders.iris.shaderpack.option.OptionLocation;
import net.irisshaders.iris.shaderpack.option.StringOption;
import org.jetbrains.annotations.Nullable;

public class MergedStringOption {
    private final StringOption option;
    private final ImmutableSet<OptionLocation> locations;

    MergedStringOption(StringOption option, ImmutableSet<OptionLocation> locations) {
        this.option = option;
        this.locations = locations;
    }

    public MergedStringOption(OptionLocation location, StringOption option) {
        this.option = option;
        this.locations = ImmutableSet.of((Object)((Object)location));
    }

    @Nullable
    public MergedStringOption merge(MergedStringOption other) {
        if (!this.option.getDefaultValue().equals(other.option.getDefaultValue())) {
            return null;
        }
        StringOption option = this.option.getComment().isPresent() ? this.option : other.option;
        ImmutableSet.Builder mergedLocations = ImmutableSet.builder();
        mergedLocations.addAll(this.locations);
        mergedLocations.addAll(other.locations);
        return new MergedStringOption(option, (ImmutableSet<OptionLocation>)mergedLocations.build());
    }

    public StringOption getOption() {
        return this.option;
    }

    public ImmutableSet<OptionLocation> getLocations() {
        return this.locations;
    }
}

