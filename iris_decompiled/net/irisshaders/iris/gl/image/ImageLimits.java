/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.gl.image;

import net.irisshaders.iris.gl.IrisRenderSystem;

public class ImageLimits {
    private static ImageLimits instance;
    private final int maxImageUnits = IrisRenderSystem.getMaxImageUnits();

    private ImageLimits() {
    }

    public static ImageLimits get() {
        if (instance == null) {
            instance = new ImageLimits();
        }
        return instance;
    }

    public int getMaxImageUnits() {
        return this.maxImageUnits;
    }
}

