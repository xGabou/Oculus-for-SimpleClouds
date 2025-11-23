/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.SimpleTexture
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.texture.pbr.loader;

import java.util.HashMap;
import java.util.Map;
import net.irisshaders.iris.texture.pbr.loader.AtlasPBRLoader;
import net.irisshaders.iris.texture.pbr.loader.PBRTextureLoader;
import net.irisshaders.iris.texture.pbr.loader.SimplePBRLoader;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.jetbrains.annotations.Nullable;

public class PBRTextureLoaderRegistry {
    public static final PBRTextureLoaderRegistry INSTANCE = new PBRTextureLoaderRegistry();
    private final Map<Class<?>, PBRTextureLoader<?>> loaderMap = new HashMap();

    public <T extends AbstractTexture> void register(Class<? extends T> clazz, PBRTextureLoader<T> loader) {
        this.loaderMap.put(clazz, loader);
    }

    @Nullable
    public <T extends AbstractTexture> PBRTextureLoader<T> getLoader(Class<? extends T> clazz) {
        return this.loaderMap.get(clazz);
    }

    static {
        INSTANCE.register(SimpleTexture.class, new SimplePBRLoader());
        INSTANCE.register(TextureAtlas.class, new AtlasPBRLoader());
    }
}

