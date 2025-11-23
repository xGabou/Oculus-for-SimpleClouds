/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.shaderpack.option;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.IrisLogging;
import net.irisshaders.iris.shaderpack.option.OptionSet;
import net.irisshaders.iris.shaderpack.option.Profile;
import net.irisshaders.iris.shaderpack.option.values.OptionValues;
import org.jetbrains.annotations.Nullable;

public class ProfileSet {
    private final LinkedHashMap<String, Profile> orderedProfiles;
    private final List<Profile> sortedProfiles;

    public ProfileSet(LinkedHashMap<String, Profile> orderedProfiles) {
        ArrayList<Profile> sorted = new ArrayList<Profile>(orderedProfiles.values());
        Comparator<Profile> lowToHigh = Comparator.comparing(p -> p.precedence);
        Comparator<Profile> highToLow = lowToHigh.reversed();
        sorted.sort(highToLow);
        if (IrisLogging.ENABLE_SPAM) {
            sorted.forEach((? super T p) -> System.out.println(p.name + ":" + p.precedence));
        }
        this.sortedProfiles = sorted;
        this.orderedProfiles = orderedProfiles;
    }

    public static ProfileSet fromTree(Map<String, List<String>> tree, OptionSet optionSet) {
        LinkedHashMap<String, Profile> profiles = new LinkedHashMap<String, Profile>();
        for (String name : tree.keySet()) {
            profiles.put(name, ProfileSet.parse(name, new ArrayList<String>(), tree, optionSet));
        }
        return new ProfileSet(profiles);
    }

    private static Profile parse(String name, List<String> parents, Map<String, List<String>> tree, OptionSet optionSet) throws IllegalArgumentException {
        Profile.Builder builder = new Profile.Builder(name);
        List<String> options = tree.get(name);
        if (options == null) {
            throw new IllegalArgumentException("Profile \"" + name + "\" does not exist!");
        }
        for (String option : options) {
            if (option.startsWith("!program.")) {
                builder.disableProgram(option.substring("!program.".length()));
                continue;
            }
            if (option.startsWith("profile.")) {
                String dependency = option.substring("profile.".length());
                if (parents.contains(dependency)) {
                    throw new IllegalArgumentException("Error parsing profile \"" + name + "\", recursively included by: " + String.join((CharSequence)", ", parents));
                }
                parents.add(dependency);
                builder.addAll(ProfileSet.parse(dependency, parents, tree, optionSet));
                continue;
            }
            if (option.startsWith("!")) {
                builder.option(option.substring(1), "false");
                continue;
            }
            if (option.contains("=")) {
                int splitPoint = option.indexOf("=");
                builder.option(option.substring(0, splitPoint), option.substring(splitPoint + 1));
                continue;
            }
            if (option.contains(":")) {
                int splitPoint = option.indexOf(":");
                builder.option(option.substring(0, splitPoint), option.substring(splitPoint + 1));
                continue;
            }
            if (optionSet.isBooleanOption(option)) {
                builder.option(option, "true");
                continue;
            }
            Iris.logger.warn("Invalid pack option: " + option);
        }
        return builder.build();
    }

    public void forEach(BiConsumer<String, Profile> action) {
        this.orderedProfiles.forEach(action);
    }

    public ProfileResult scan(OptionSet options, OptionValues values) {
        if (this.sortedProfiles.isEmpty()) {
            return new ProfileResult(null, null, null);
        }
        for (int i = 0; i < this.sortedProfiles.size(); ++i) {
            Profile current = this.sortedProfiles.get(i);
            if (!current.matches(options, values)) continue;
            Profile next = this.sortedProfiles.get(Math.floorMod(i + 1, this.sortedProfiles.size()));
            Profile prev = this.sortedProfiles.get(Math.floorMod(i - 1, this.sortedProfiles.size()));
            return new ProfileResult(current, next, prev);
        }
        Profile next = this.sortedProfiles.get(0);
        Profile prev = this.sortedProfiles.get(this.sortedProfiles.size() - 1);
        return new ProfileResult(null, next, prev);
    }

    public int size() {
        return this.sortedProfiles.size();
    }

    public static class ProfileResult {
        public final Optional<Profile> current;
        public final Profile next;
        public final Profile previous;

        private ProfileResult(@Nullable Profile current, Profile next, Profile previous) {
            this.current = Optional.ofNullable(current);
            this.next = next;
            this.previous = previous;
        }
    }
}

