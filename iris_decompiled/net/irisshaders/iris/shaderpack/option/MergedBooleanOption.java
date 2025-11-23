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
import net.irisshaders.iris.shaderpack.option.BooleanOption;
import net.irisshaders.iris.shaderpack.option.OptionLocation;
import org.jetbrains.annotations.Nullable;

public class MergedBooleanOption {
    private final BooleanOption option;
    private final ImmutableSet<OptionLocation> locations;

    MergedBooleanOption(BooleanOption option, ImmutableSet<OptionLocation> locations) {
        this.option = option;
        this.locations = locations;
    }

    public MergedBooleanOption(OptionLocation location, BooleanOption option) {
        this.option = option;
        this.locations = ImmutableSet.of((Object)((Object)location));
    }

    @Nullable
    public MergedBooleanOption merge(MergedBooleanOption other) {
        if (this.option.getDefaultValue() != other.option.getDefaultValue()) {
            return null;
        }
        BooleanOption option = this.option.getComment().isPresent() ? this.option : other.option;
        ImmutableSet.Builder mergedLocations = ImmutableSet.builder();
        mergedLocations.addAll(this.locations);
        mergedLocations.addAll(other.locations);
        return new MergedBooleanOption(option, (ImmutableSet<OptionLocation>)mergedLocations.build());
    }

    public BooleanOption getOption() {
        return this.option;
    }

    public ImmutableSet<OptionLocation> getLocations() {
        return this.locations;
    }
}

