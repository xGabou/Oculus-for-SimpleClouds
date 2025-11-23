/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.gl.blending;

import net.irisshaders.iris.gl.blending.BlendMode;
import net.irisshaders.iris.gl.blending.BlendModeStorage;

public class BlendModeOverride {
    public static final BlendModeOverride OFF = new BlendModeOverride(null);
    private final BlendMode blendMode;

    public BlendModeOverride(BlendMode blendMode) {
        this.blendMode = blendMode;
    }

    public static void restore() {
        BlendModeStorage.restoreBlend();
    }

    public void apply() {
        BlendModeStorage.overrideBlend(this.blendMode);
    }
}

