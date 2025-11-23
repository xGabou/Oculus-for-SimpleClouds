/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  org.jetbrains.annotations.NotNull
 */
package net.irisshaders.iris.texture.pbr;

import net.minecraft.client.renderer.texture.AbstractTexture;
import org.jetbrains.annotations.NotNull;

public interface PBRTextureHolder {
    @NotNull
    public AbstractTexture normalTexture();

    @NotNull
    public AbstractTexture specularTexture();
}

