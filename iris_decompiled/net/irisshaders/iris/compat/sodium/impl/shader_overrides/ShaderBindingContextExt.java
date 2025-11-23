/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniform
 *  me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniformBlock
 */
package net.irisshaders.iris.compat.sodium.impl.shader_overrides;

import java.util.function.IntFunction;
import me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniform;
import me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniformBlock;

public interface ShaderBindingContextExt {
    public <U extends GlUniform<?>> U bindUniformIfPresent(String var1, IntFunction<U> var2);

    public GlUniformBlock bindUniformBlockIfPresent(String var1, int var2);
}

