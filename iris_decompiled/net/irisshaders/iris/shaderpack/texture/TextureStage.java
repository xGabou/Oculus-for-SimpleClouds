/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.shaderpack.texture;

import java.util.Optional;

public enum TextureStage {
    SETUP,
    BEGIN,
    SHADOWCOMP,
    PREPARE,
    GBUFFERS_AND_SHADOW,
    DEFERRED,
    COMPOSITE_AND_FINAL;


    public static Optional<TextureStage> parse(String name) {
        return switch (name) {
            case "setup" -> Optional.of(SETUP);
            case "begin" -> Optional.of(BEGIN);
            case "shadowcomp" -> Optional.of(SHADOWCOMP);
            case "prepare" -> Optional.of(PREPARE);
            case "gbuffers" -> Optional.of(GBUFFERS_AND_SHADOW);
            case "deferred" -> Optional.of(DEFERRED);
            case "composite" -> Optional.of(COMPOSITE_AND_FINAL);
            default -> Optional.empty();
        };
    }
}

