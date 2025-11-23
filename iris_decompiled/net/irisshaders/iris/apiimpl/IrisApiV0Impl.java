/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.Screen
 */
package net.irisshaders.iris.apiimpl;

import java.nio.ByteBuffer;
import java.util.function.IntFunction;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.api.v0.IrisApiConfig;
import net.irisshaders.iris.api.v0.IrisTextVertexSink;
import net.irisshaders.iris.apiimpl.IrisApiV0ConfigImpl;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.irisshaders.iris.pipeline.VanillaRenderingPipeline;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.irisshaders.iris.vertices.IrisTextVertexSinkImpl;
import net.minecraft.client.gui.screens.Screen;

public class IrisApiV0Impl
implements IrisApi {
    public static final IrisApiV0Impl INSTANCE = new IrisApiV0Impl();
    private static final IrisApiV0ConfigImpl CONFIG = new IrisApiV0ConfigImpl();

    @Override
    public int getMinorApiRevision() {
        return 2;
    }

    @Override
    public boolean isShaderPackInUse() {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline == null) {
            return false;
        }
        return !(pipeline instanceof VanillaRenderingPipeline);
    }

    @Override
    public boolean isRenderingShadowPass() {
        return ShadowRenderingState.areShadowsCurrentlyBeingRendered();
    }

    @Override
    public Object openMainIrisScreenObj(Object parent) {
        return new ShaderPackScreen((Screen)parent);
    }

    @Override
    public String getMainScreenLanguageKey() {
        return "options.iris.shaderPackSelection";
    }

    @Override
    public IrisApiConfig getConfig() {
        return CONFIG;
    }

    @Override
    public IrisTextVertexSink createTextVertexSink(int maxQuadCount, IntFunction<ByteBuffer> bufferProvider) {
        return new IrisTextVertexSinkImpl(maxQuadCount, bufferProvider);
    }

    @Override
    public float getSunPathRotation() {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline == null) {
            return 0.0f;
        }
        return pipeline.getSunPathRotation();
    }
}

