/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  me.jellysquid.mods.sodium.client.gl.buffer.GlBuffer
 *  me.jellysquid.mods.sodium.client.gl.buffer.GlMutableBuffer
 *  me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniform
 *  me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniformBlock
 *  me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniformFloat3v
 *  me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniformMatrix4f
 *  me.jellysquid.mods.sodium.client.render.chunk.shader.ChunkShaderInterface
 *  me.jellysquid.mods.sodium.client.render.chunk.shader.ChunkShaderOptions
 *  me.jellysquid.mods.sodium.client.render.chunk.shader.ShaderBindingContext
 *  me.jellysquid.mods.sodium.client.util.TextureUtil
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package net.irisshaders.iris.compat.sodium.impl.shader_overrides;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.function.IntFunction;
import me.jellysquid.mods.sodium.client.gl.buffer.GlBuffer;
import me.jellysquid.mods.sodium.client.gl.buffer.GlMutableBuffer;
import me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniform;
import me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniformBlock;
import me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniformFloat3v;
import me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniformMatrix4f;
import me.jellysquid.mods.sodium.client.render.chunk.shader.ChunkShaderInterface;
import me.jellysquid.mods.sodium.client.render.chunk.shader.ChunkShaderOptions;
import me.jellysquid.mods.sodium.client.render.chunk.shader.ShaderBindingContext;
import me.jellysquid.mods.sodium.client.util.TextureUtil;
import net.irisshaders.iris.compat.sodium.impl.shader_overrides.GlUniformMatrix3f;
import net.irisshaders.iris.compat.sodium.impl.shader_overrides.IrisShaderFogComponent;
import net.irisshaders.iris.compat.sodium.impl.shader_overrides.ShaderBindingContextExt;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.blending.BlendModeOverride;
import net.irisshaders.iris.gl.blending.BufferBlendOverride;
import net.irisshaders.iris.gl.program.ProgramImages;
import net.irisshaders.iris.gl.program.ProgramSamplers;
import net.irisshaders.iris.gl.program.ProgramUniforms;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.pipeline.SodiumTerrainPipeline;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.uniforms.custom.CustomUniforms;
import net.irisshaders.iris.vertices.ImmediateState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class IrisChunkShaderInterface
extends ChunkShaderInterface {
    @Nullable
    private final GlUniformMatrix4f uniformModelViewMatrix;
    @Nullable
    private final GlUniformMatrix4f uniformModelViewMatrixInverse;
    @Nullable
    private final GlUniformMatrix4f uniformProjectionMatrix;
    @Nullable
    private final GlUniformMatrix4f uniformProjectionMatrixInverse;
    @Nullable
    private final GlUniformFloat3v uniformRegionOffset;
    @Nullable
    private final GlUniformMatrix3f uniformNormalMatrix;
    @Nullable
    private final GlUniformBlock uniformBlockDrawParameters;
    private final BlendModeOverride blendModeOverride;
    private final IrisShaderFogComponent fogShaderComponent;
    private final float alpha;
    private final ProgramUniforms irisProgramUniforms;
    private final ProgramSamplers irisProgramSamplers;
    private final ProgramImages irisProgramImages;
    private final List<BufferBlendOverride> bufferBlendOverrides;
    private final boolean hasOverrides;
    private final boolean isTess;
    private final CustomUniforms customUniforms;

    public IrisChunkShaderInterface(int handle, final ShaderBindingContextExt contextExt, SodiumTerrainPipeline pipeline, ChunkShaderOptions options, boolean isTess, boolean isShadowPass, BlendModeOverride blendModeOverride, List<BufferBlendOverride> bufferOverrides, float alpha, CustomUniforms customUniforms) {
        super(new ShaderBindingContext(){

            public <U extends GlUniform<?>> U bindUniform(String s, IntFunction<U> intFunction) {
                return contextExt.bindUniformIfPresent(s, intFunction);
            }

            public GlUniformBlock bindUniformBlock(String s, int i) {
                return contextExt.bindUniformBlockIfPresent(s, i);
            }
        }, options);
        this.uniformModelViewMatrix = contextExt.bindUniformIfPresent("iris_ModelViewMatrix", GlUniformMatrix4f::new);
        this.uniformModelViewMatrixInverse = contextExt.bindUniformIfPresent("iris_ModelViewMatrixInverse", GlUniformMatrix4f::new);
        this.uniformProjectionMatrix = contextExt.bindUniformIfPresent("iris_ProjectionMatrix", GlUniformMatrix4f::new);
        this.uniformProjectionMatrixInverse = contextExt.bindUniformIfPresent("iris_ProjectionMatrixInverse", GlUniformMatrix4f::new);
        this.uniformRegionOffset = contextExt.bindUniformIfPresent("u_RegionOffset", GlUniformFloat3v::new);
        this.uniformNormalMatrix = contextExt.bindUniformIfPresent("iris_NormalMatrix", GlUniformMatrix3f::new);
        this.uniformBlockDrawParameters = contextExt.bindUniformBlockIfPresent("ubo_DrawParameters", 0);
        this.customUniforms = customUniforms;
        this.isTess = isTess;
        this.alpha = alpha;
        this.blendModeOverride = blendModeOverride;
        this.bufferBlendOverrides = bufferOverrides;
        this.hasOverrides = this.bufferBlendOverrides != null && !this.bufferBlendOverrides.isEmpty();
        this.fogShaderComponent = new IrisShaderFogComponent(contextExt);
        ProgramUniforms.Builder builder = pipeline.initUniforms(handle);
        customUniforms.mapholderToPass(builder, (Object)this);
        this.irisProgramUniforms = builder.buildUniforms();
        this.irisProgramSamplers = isShadowPass ? pipeline.initShadowSamplers(handle) : pipeline.initTerrainSamplers(handle);
        this.irisProgramImages = isShadowPass ? pipeline.initShadowImages(handle) : pipeline.initTerrainImages(handle);
    }

    public void setupState() {
        IrisRenderSystem.bindTextureToUnit(TextureType.TEXTURE_2D.getGlType(), 0, TextureUtil.getBlockTextureId());
        IrisRenderSystem.bindTextureToUnit(TextureType.TEXTURE_2D.getGlType(), 2, TextureUtil.getLightTextureId());
        GlStateManager._activeTexture((int)33986);
        CapturedRenderingState.INSTANCE.setCurrentAlphaTest(this.alpha);
        if (this.blendModeOverride != null) {
            this.blendModeOverride.apply();
        }
        ImmediateState.usingTessellation = this.isTess;
        if (this.hasOverrides) {
            this.bufferBlendOverrides.forEach(BufferBlendOverride::apply);
        }
        this.fogShaderComponent.setup();
        this.irisProgramUniforms.update();
        this.irisProgramSamplers.update();
        this.irisProgramImages.update();
        this.customUniforms.push((Object)this);
    }

    public void restore() {
        ImmediateState.usingTessellation = false;
        if (this.blendModeOverride != null || this.hasOverrides) {
            BlendModeOverride.restore();
        }
    }

    public void setProjectionMatrix(Matrix4fc matrix) {
        if (this.uniformProjectionMatrix != null) {
            this.uniformProjectionMatrix.set(matrix);
        }
        if (this.uniformProjectionMatrixInverse != null) {
            Matrix4f inverted = new Matrix4f(matrix);
            inverted.invert();
            this.uniformProjectionMatrixInverse.set((Matrix4fc)inverted);
        }
    }

    public void setModelViewMatrix(Matrix4fc modelView) {
        if (this.uniformModelViewMatrix != null) {
            this.uniformModelViewMatrix.set(modelView);
        }
        if (this.uniformModelViewMatrixInverse != null) {
            Matrix4f invertedMatrix = new Matrix4f(modelView);
            invertedMatrix.invert();
            this.uniformModelViewMatrixInverse.set((Matrix4fc)invertedMatrix);
            if (this.uniformNormalMatrix != null) {
                invertedMatrix.transpose();
                this.uniformNormalMatrix.set(new Matrix3f((Matrix4fc)invertedMatrix));
            }
        } else if (this.uniformNormalMatrix != null) {
            Matrix3f normalMatrix = new Matrix3f(modelView);
            normalMatrix.invert();
            normalMatrix.transpose();
            this.uniformNormalMatrix.set(normalMatrix);
        }
    }

    public void setDrawUniforms(GlMutableBuffer buffer) {
        if (this.uniformBlockDrawParameters != null) {
            this.uniformBlockDrawParameters.bindBuffer((GlBuffer)buffer);
        }
    }

    public void setRegionOffset(float x, float y, float z) {
        if (this.uniformRegionOffset != null) {
            this.uniformRegionOffset.set(x, y, z);
        }
    }
}

