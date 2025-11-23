/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.preprocessor.GlslPreprocessor
 *  com.mojang.blaze3d.shaders.Program
 *  com.mojang.blaze3d.shaders.Program$Type
 *  com.mojang.blaze3d.shaders.ProgramManager
 *  com.mojang.blaze3d.shaders.Shader
 *  com.mojang.blaze3d.shaders.Uniform
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceProvider
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package net.irisshaders.iris.pipeline.programs;

import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.GLDebug;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.blending.AlphaTest;
import net.irisshaders.iris.gl.blending.BlendModeOverride;
import net.irisshaders.iris.gl.blending.BufferBlendOverride;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.irisshaders.iris.gl.image.ImageHolder;
import net.irisshaders.iris.gl.program.IrisProgramTypes;
import net.irisshaders.iris.gl.program.ProgramImages;
import net.irisshaders.iris.gl.program.ProgramSamplers;
import net.irisshaders.iris.gl.program.ProgramUniforms;
import net.irisshaders.iris.gl.sampler.SamplerHolder;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.gl.uniform.DynamicLocationalUniformHolder;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.pipeline.programs.ShaderInstanceInterface;
import net.irisshaders.iris.samplers.IrisSamplers;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.uniforms.custom.CustomUniforms;
import net.irisshaders.iris.vertices.ImmediateState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class ExtendedShader
extends ShaderInstance
implements ShaderInstanceInterface {
    private static final Matrix4f identity = new Matrix4f();
    private static ExtendedShader lastApplied;
    private final boolean intensitySwizzle;
    private final List<BufferBlendOverride> bufferBlendOverrides;
    private final boolean hasOverrides;
    private final Uniform modelViewInverse;
    private final Uniform projectionInverse;
    private final Uniform normalMatrix;
    private final CustomUniforms customUniforms;
    private final IrisRenderingPipeline parent;
    private final ProgramUniforms uniforms;
    private final ProgramSamplers samplers;
    private final ProgramImages images;
    private final GlFramebuffer writingToBeforeTranslucent;
    private final GlFramebuffer writingToAfterTranslucent;
    private final BlendModeOverride blendModeOverride;
    float alphaTest;
    boolean usesTessellation;
    Matrix4f tempMatrix4f = new Matrix4f();
    Matrix3f tempMatrix3f = new Matrix3f();
    float[] tempFloats = new float[16];
    float[] tempFloats2 = new float[9];
    private Program geometry;
    private Program tessControl;
    private Program tessEval;

    public ExtendedShader(ResourceProvider resourceFactory, String string, VertexFormat vertexFormat, boolean usesTessellation, GlFramebuffer writingToBeforeTranslucent, GlFramebuffer writingToAfterTranslucent, BlendModeOverride blendModeOverride, AlphaTest alphaTest, Consumer<DynamicLocationalUniformHolder> uniformCreator, BiConsumer<SamplerHolder, ImageHolder> samplerCreator, boolean isIntensity, IrisRenderingPipeline parent, @Nullable List<BufferBlendOverride> bufferBlendOverrides, CustomUniforms customUniforms) throws IOException {
        super(resourceFactory, string, vertexFormat);
        GLDebug.nameObject(33505, this.m_108962_().m_166618_(), string + "_vertex.vsh");
        GLDebug.nameObject(33505, this.m_108964_().m_166618_(), string + "_fragment.fsh");
        int programId = this.m_108943_();
        GLDebug.nameObject(33506, programId, string);
        ProgramUniforms.Builder uniformBuilder = ProgramUniforms.builder(string, programId);
        ProgramSamplers.Builder samplerBuilder = ProgramSamplers.builder(programId, IrisSamplers.WORLD_RESERVED_TEXTURE_UNITS);
        uniformCreator.accept(uniformBuilder);
        ProgramImages.Builder builder = ProgramImages.builder(programId);
        samplerCreator.accept(samplerBuilder, builder);
        customUniforms.mapholderToPass(uniformBuilder, this);
        this.usesTessellation = usesTessellation;
        this.uniforms = uniformBuilder.buildUniforms();
        this.customUniforms = customUniforms;
        this.samplers = samplerBuilder.build();
        this.images = builder.build();
        this.writingToBeforeTranslucent = writingToBeforeTranslucent;
        this.writingToAfterTranslucent = writingToAfterTranslucent;
        this.blendModeOverride = blendModeOverride;
        this.bufferBlendOverrides = bufferBlendOverrides;
        this.hasOverrides = bufferBlendOverrides != null && !bufferBlendOverrides.isEmpty();
        this.alphaTest = alphaTest.reference();
        this.parent = parent;
        this.modelViewInverse = this.m_173348_("ModelViewMatInverse");
        this.projectionInverse = this.m_173348_("ProjMatInverse");
        this.normalMatrix = this.m_173348_("NormalMat");
        this.intensitySwizzle = isIntensity;
    }

    public boolean isIntensitySwizzle() {
        return this.intensitySwizzle;
    }

    public void m_173362_() {
        ProgramUniforms.clearActiveUniforms();
        ProgramSamplers.clearActiveSamplers();
        lastApplied = null;
        if (this.blendModeOverride != null || this.hasOverrides) {
            BlendModeOverride.restore();
        }
        Minecraft.m_91087_().m_91385_().m_83947_(false);
    }

    public void m_173363_() {
        CapturedRenderingState.INSTANCE.setCurrentAlphaTest(this.alphaTest);
        if (lastApplied != this) {
            lastApplied = this;
            ProgramManager.m_85578_((int)this.m_108943_());
        }
        if (this.intensitySwizzle) {
            IrisRenderSystem.texParameteriv(RenderSystem.getShaderTexture((int)0), TextureType.TEXTURE_2D.getGlType(), 36422, new int[]{6403, 6403, 6403, 6403});
        }
        IrisRenderSystem.bindTextureToUnit(TextureType.TEXTURE_2D.getGlType(), 0, RenderSystem.getShaderTexture((int)0));
        IrisRenderSystem.bindTextureToUnit(TextureType.TEXTURE_2D.getGlType(), 1, RenderSystem.getShaderTexture((int)1));
        IrisRenderSystem.bindTextureToUnit(TextureType.TEXTURE_2D.getGlType(), 2, RenderSystem.getShaderTexture((int)2));
        ImmediateState.usingTessellation = this.usesTessellation;
        if (this.f_173309_ != null) {
            if (this.projectionInverse != null) {
                this.projectionInverse.m_5941_(this.tempMatrix4f.set(this.f_173309_.m_166761_()).invert().get(this.tempFloats));
            }
        } else if (this.projectionInverse != null) {
            this.projectionInverse.m_5679_(identity);
        }
        if (this.f_173308_ != null) {
            if (this.modelViewInverse != null) {
                this.modelViewInverse.m_5941_(this.tempMatrix4f.set(this.f_173308_.m_166761_()).invert().get(this.tempFloats));
            }
            if (this.normalMatrix != null) {
                this.normalMatrix.m_5941_(this.tempMatrix3f.set((Matrix4fc)this.tempMatrix4f.set(this.f_173308_.m_166761_())).invert().transpose().get(this.tempFloats2));
            }
        }
        this.uploadIfNotNull(this.projectionInverse);
        this.uploadIfNotNull(this.modelViewInverse);
        this.uploadIfNotNull(this.normalMatrix);
        List uniformList = this.f_173331_;
        for (Uniform uniform : uniformList) {
            this.uploadIfNotNull(uniform);
        }
        this.samplers.update();
        this.uniforms.update();
        this.customUniforms.push(this);
        this.images.update();
        if (this.blendModeOverride != null) {
            this.blendModeOverride.apply();
        }
        if (this.hasOverrides) {
            this.bufferBlendOverrides.forEach(BufferBlendOverride::apply);
        }
        if (this.parent.isBeforeTranslucent) {
            this.writingToBeforeTranslucent.bind();
        } else {
            this.writingToAfterTranslucent.bind();
        }
    }

    @Nullable
    public Uniform m_173348_(String name) {
        return super.m_173348_("iris_" + name);
    }

    private void uploadIfNotNull(Uniform uniform) {
        if (uniform != null) {
            uniform.m_85633_();
        }
    }

    public void m_142662_() {
        super.m_142662_();
        if (this.geometry != null) {
            this.geometry.m_166610_((Shader)this);
        }
        if (this.tessControl != null) {
            this.tessControl.m_166610_((Shader)this);
        }
        if (this.tessEval != null) {
            this.tessEval.m_166610_((Shader)this);
        }
    }

    @Override
    public void iris$createExtraShaders(ResourceProvider factory, ResourceLocation name) {
        factory.m_213713_(new ResourceLocation(name.m_135827_(), name.m_135815_() + "_geometry.gsh")).ifPresent(geometry -> {
            try {
                this.geometry = Program.m_166604_((Program.Type)IrisProgramTypes.GEOMETRY, (String)name.m_135815_(), (InputStream)geometry.m_215507_(), (String)geometry.m_215506_(), (GlslPreprocessor)new GlslPreprocessor(){

                    @Nullable
                    public String m_142138_(boolean bl, String string) {
                        return null;
                    }
                });
                GLDebug.nameObject(33505, this.geometry.m_166618_(), name.m_135815_() + "_geometry.gsh");
            }
            catch (IOException e) {
                Iris.logger.error("Failed to create shader program", e);
            }
        });
        factory.m_213713_(new ResourceLocation(name.m_135827_(), name.m_135815_() + "_tessControl.tcs")).ifPresent(tessControl -> {
            try {
                this.tessControl = Program.m_166604_((Program.Type)IrisProgramTypes.TESS_CONTROL, (String)name.m_135815_(), (InputStream)tessControl.m_215507_(), (String)tessControl.m_215506_(), (GlslPreprocessor)new GlslPreprocessor(){

                    @Nullable
                    public String m_142138_(boolean bl, String string) {
                        return null;
                    }
                });
                GLDebug.nameObject(33505, this.tessControl.m_166618_(), name.m_135815_() + "_tessControl.tcs");
            }
            catch (IOException e) {
                Iris.logger.error("Failed to create shader program", e);
            }
        });
        factory.m_213713_(new ResourceLocation(name.m_135827_(), name.m_135815_() + "_tessEval.tes")).ifPresent(tessEval -> {
            try {
                this.tessEval = Program.m_166604_((Program.Type)IrisProgramTypes.TESS_EVAL, (String)name.m_135815_(), (InputStream)tessEval.m_215507_(), (String)tessEval.m_215506_(), (GlslPreprocessor)new GlslPreprocessor(){

                    @Nullable
                    public String m_142138_(boolean bl, String string) {
                        return null;
                    }
                });
                GLDebug.nameObject(33505, this.tessEval.m_166618_(), name.m_135815_() + "_tessEval.tes");
            }
            catch (IOException e) {
                Iris.logger.error("Failed to create shader program", e);
            }
        });
    }

    public Program getGeometry() {
        return this.geometry;
    }

    public Program getTessControl() {
        return this.tessControl;
    }

    public Program getTessEval() {
        return this.tessEval;
    }

    public boolean hasActiveImages() {
        return this.images.getActiveImages() > 0;
    }

    static {
        identity.identity();
    }
}

