/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.gl.shader.GlProgram
 */
package net.irisshaders.iris.compat.sodium.impl.shader_overrides;

import me.jellysquid.mods.sodium.client.gl.shader.GlProgram;
import net.irisshaders.iris.compat.sodium.impl.shader_overrides.IrisChunkShaderInterface;

public interface ShaderChunkRendererExt {
    public GlProgram<IrisChunkShaderInterface> iris$getOverride();
}

