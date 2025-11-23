/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.shaderpack.properties;

import java.util.Optional;
import net.irisshaders.iris.Iris;

public enum ParticleRenderingSettings {
    BEFORE,
    MIXED,
    AFTER;


    public static Optional<ParticleRenderingSettings> fromString(String name) {
        try {
            return Optional.of(ParticleRenderingSettings.valueOf(name));
        }
        catch (IllegalArgumentException e) {
            Iris.logger.warn("Invalid particle rendering settings! " + name);
            return Optional.empty();
        }
    }
}

