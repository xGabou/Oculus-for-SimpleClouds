/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package net.irisshaders.iris.shaderpack.include;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.irisshaders.iris.shaderpack.include.AbsolutePackPath;
import net.irisshaders.iris.shaderpack.loading.ProgramArrayId;
import net.irisshaders.iris.shaderpack.loading.ProgramId;

public class ShaderPackSourceNames {
    public static final ImmutableList<String> POTENTIAL_STARTS = ShaderPackSourceNames.findPotentialStarts();

    public static boolean findPresentSources(ImmutableList.Builder<AbsolutePackPath> starts, Path packRoot, AbsolutePackPath directory, ImmutableList<String> candidates) throws IOException {
        Set found;
        Path directoryPath = directory.resolved(packRoot);
        if (!Files.exists(directoryPath, new LinkOption[0])) {
            return false;
        }
        boolean anyFound = false;
        try (Stream<Path> stream = Files.list(directoryPath);){
            found = stream.map(path -> path.getFileName().toString()).collect(Collectors.toSet());
        }
        for (String candidate : candidates) {
            if (!found.contains(candidate)) continue;
            starts.add((Object)directory.resolve(candidate));
            anyFound = true;
        }
        return anyFound;
    }

    private static ImmutableList<String> findPotentialStarts() {
        ImmutableList.Builder potentialFileNames = ImmutableList.builder();
        for (ProgramArrayId programArrayId : ProgramArrayId.values()) {
            for (int i = 0; i < programArrayId.getNumPrograms(); ++i) {
                String name = programArrayId.getSourcePrefix();
                String suffix = "";
                if (i > 0) {
                    suffix = Integer.toString(i);
                }
                ShaderPackSourceNames.addComputeStarts((ImmutableList.Builder<String>)potentialFileNames, name + suffix);
            }
        }
        for (Enum enum_ : ProgramId.values()) {
            if (enum_ == ProgramId.Final || enum_ == ProgramId.Shadow) {
                ShaderPackSourceNames.addComputeStarts((ImmutableList.Builder<String>)potentialFileNames, ((ProgramId)enum_).getSourceName());
                continue;
            }
            ShaderPackSourceNames.addStarts((ImmutableList.Builder<String>)potentialFileNames, ((ProgramId)enum_).getSourceName());
        }
        return potentialFileNames.build();
    }

    private static void addStarts(ImmutableList.Builder<String> potentialFileNames, String baseName) {
        potentialFileNames.add((Object)(baseName + ".vsh"));
        potentialFileNames.add((Object)(baseName + ".tcs"));
        potentialFileNames.add((Object)(baseName + ".tes"));
        potentialFileNames.add((Object)(baseName + ".gsh"));
        potentialFileNames.add((Object)(baseName + ".fsh"));
    }

    private static void addComputeStarts(ImmutableList.Builder<String> potentialFileNames, String baseName) {
        ShaderPackSourceNames.addStarts(potentialFileNames, baseName);
        for (int j = 0; j < 27; ++j) {
            Object suffix2;
            if (j == 0) {
                suffix2 = "";
            } else {
                char letter = (char)(97 + j - 1);
                suffix2 = "_" + letter;
            }
            potentialFileNames.add((Object)(baseName + (String)suffix2 + ".csh"));
        }
    }
}

