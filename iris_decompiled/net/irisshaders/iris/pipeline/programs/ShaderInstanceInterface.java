/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceProvider
 */
package net.irisshaders.iris.pipeline.programs;

import java.io.IOException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

public interface ShaderInstanceInterface {
    public void iris$createExtraShaders(ResourceProvider var1, ResourceLocation var2) throws IOException;
}

