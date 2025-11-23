/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.pipeline;

import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.pipeline.programs.ShaderMap;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;

public interface ShaderRenderingPipeline
extends WorldRenderingPipeline {
    public ShaderMap getShaderMap();

    @Override
    public FrameUpdateNotifier getFrameUpdateNotifier();

    public boolean shouldOverrideShaders();
}

