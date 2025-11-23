/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package net.irisshaders.iris.shaderpack.materialmap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import org.jetbrains.annotations.NotNull;

public record BlockEntry(NamespacedId id, Map<String, String> propertyPredicates) {
    @NotNull
    public static BlockEntry parse(@NotNull String entry) {
        NamespacedId id;
        if (entry.isEmpty()) {
            throw new IllegalArgumentException("Called BlockEntry::parse with an empty string");
        }
        String[] splitStates = entry.split(":");
        if (splitStates.length == 1) {
            return new BlockEntry(new NamespacedId("minecraft", entry), Collections.emptyMap());
        }
        if (splitStates.length == 2 && !splitStates[1].contains("=")) {
            return new BlockEntry(new NamespacedId(splitStates[0], splitStates[1]), Collections.emptyMap());
        }
        if (splitStates[1].contains("=")) {
            statesStart = 1;
            id = new NamespacedId("minecraft", splitStates[0]);
        } else {
            statesStart = 2;
            id = new NamespacedId(splitStates[0], splitStates[1]);
        }
        HashMap<String, String> map = new HashMap<String, String>();
        for (int index = statesStart; index < splitStates.length; ++index) {
            String[] propertyParts = splitStates[index].split("=");
            if (propertyParts.length != 2) {
                Iris.logger.warn("Warning: the block ID map entry \"" + entry + "\" could not be fully parsed:");
                Iris.logger.warn("- Block state property filters must be of the form \"key=value\", but " + splitStates[index] + " is not of that form!");
                continue;
            }
            map.put(propertyParts[0], propertyParts[1]);
        }
        return new BlockEntry(id, map);
    }
}

