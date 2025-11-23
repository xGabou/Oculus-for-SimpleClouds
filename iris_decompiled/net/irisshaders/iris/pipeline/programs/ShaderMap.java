/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.ShaderInstance
 */
package net.irisshaders.iris.pipeline.programs;

import java.util.function.Function;
import net.irisshaders.iris.pipeline.programs.ShaderKey;
import net.minecraft.client.renderer.ShaderInstance;

public class ShaderMap {
    private final ShaderInstance[] shaders;

    public ShaderMap(Function<ShaderKey, ShaderInstance> factory) {
        ShaderKey[] ids = ShaderKey.values();
        this.shaders = new ShaderInstance[ids.length];
        for (int i = 0; i < ids.length; ++i) {
            this.shaders[i] = factory.apply(ids[i]);
        }
    }

    public ShaderInstance getShader(ShaderKey id) {
        return this.shaders[id.ordinal()];
    }
}

