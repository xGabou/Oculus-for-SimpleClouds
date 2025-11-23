/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.gl.image;

import java.util.function.IntSupplier;
import net.irisshaders.iris.gl.IrisRenderSystem;

public class ImageBinding {
    private final int imageUnit;
    private final int internalFormat;
    private final IntSupplier textureID;

    public ImageBinding(int imageUnit, int internalFormat, IntSupplier textureID) {
        this.textureID = textureID;
        this.imageUnit = imageUnit;
        this.internalFormat = internalFormat;
    }

    public void update() {
        IrisRenderSystem.bindImageTexture(this.imageUnit, this.textureID.getAsInt(), 0, true, 0, 35002, this.internalFormat);
    }
}

