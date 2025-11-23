/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.gl.image;

import java.util.function.IntSupplier;
import net.irisshaders.iris.gl.texture.InternalTextureFormat;

public interface ImageHolder {
    public boolean hasImage(String var1);

    public void addTextureImage(IntSupplier var1, InternalTextureFormat var2, String var3);
}

