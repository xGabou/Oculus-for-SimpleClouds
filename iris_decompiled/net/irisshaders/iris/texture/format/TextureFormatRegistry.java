/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.texture.format;

import java.util.HashMap;
import java.util.Map;
import net.irisshaders.iris.texture.format.LabPBRTextureFormat;
import net.irisshaders.iris.texture.format.TextureFormat;
import org.jetbrains.annotations.Nullable;

public class TextureFormatRegistry {
    public static final TextureFormatRegistry INSTANCE = new TextureFormatRegistry();
    private final Map<String, TextureFormat.Factory> factoryMap = new HashMap<String, TextureFormat.Factory>();

    public void register(String name, TextureFormat.Factory factory) {
        this.factoryMap.put(name, factory);
    }

    @Nullable
    public TextureFormat.Factory getFactory(String name) {
        return this.factoryMap.get(name);
    }

    static {
        INSTANCE.register("lab-pbr", LabPBRTextureFormat::new);
    }
}

