/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.shaderpack.materialmap;

import java.util.Optional;

public enum BlockRenderType {
    SOLID,
    CUTOUT,
    CUTOUT_MIPPED,
    TRANSLUCENT;


    public static Optional<BlockRenderType> fromString(String name) {
        return switch (name) {
            case "solid" -> Optional.of(SOLID);
            case "cutout" -> Optional.of(CUTOUT);
            case "cutout_mipped" -> Optional.of(CUTOUT_MIPPED);
            case "translucent" -> Optional.of(TRANSLUCENT);
            default -> Optional.empty();
        };
    }
}

