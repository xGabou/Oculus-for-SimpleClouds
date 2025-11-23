/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.server.packs.resources.ResourceManager
 *  org.jetbrains.annotations.NotNull
 */
package net.irisshaders.iris.texture.pbr.loader;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

public interface PBRTextureLoader<T extends AbstractTexture> {
    public void load(T var1, ResourceManager var2, PBRTextureConsumer var3);

    public static interface PBRTextureConsumer {
        public void acceptNormalTexture(@NotNull AbstractTexture var1);

        public void acceptSpecularTexture(@NotNull AbstractTexture var1);
    }
}

