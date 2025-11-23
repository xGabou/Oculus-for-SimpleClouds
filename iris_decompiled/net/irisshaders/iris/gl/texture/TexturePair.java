/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.gl.texture;

import java.util.function.IntSupplier;
import net.irisshaders.iris.gl.texture.TextureType;

public record TexturePair(TextureType type, IntSupplier id) {
}

