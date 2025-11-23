/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Ints
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.seibel.distanthorizons.api.DhApi$Delayed
 *  com.seibel.distanthorizons.api.objects.math.DhApiVec3f
 *  net.minecraft.client.Minecraft
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.lwjgl.opengl.GL32
 *  org.lwjgl.opengl.GL43C
 *  org.lwjgl.system.MemoryStack
 */
package net.irisshaders.iris.compat.dh;

import com.google.common.primitives.Ints;
import com.mojang.blaze3d.systems.RenderSystem;
import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.objects.math.DhApiVec3f;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Map;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.blending.BlendModeOverride;
import net.irisshaders.iris.gl.blending.BufferBlendOverride;
import net.irisshaders.iris.gl.program.ProgramImages;
import net.irisshaders.iris.gl.program.ProgramSamplers;
import net.irisshaders.iris.gl.program.ProgramUniforms;
import net.irisshaders.iris.gl.shader.GlShader;
import net.irisshaders.iris.gl.shader.ShaderType;
import net.irisshaders.iris.gl.state.FogMode;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.pipeline.transform.PatchShaderType;
import net.irisshaders.iris.pipeline.transform.ShaderPrinter;
import net.irisshaders.iris.pipeline.transform.TransformPatcher;
import net.irisshaders.iris.samplers.IrisSamplers;
import net.irisshaders.iris.shaderpack.programs.ProgramSource;
import net.irisshaders.iris.uniforms.CommonUniforms;
import net.irisshaders.iris.uniforms.builtin.BuiltinReplacementUniforms;
import net.irisshaders.iris.uniforms.custom.CustomUniforms;
import net.minecraft.client.Minecraft;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL43C;
import org.lwjgl.system.MemoryStack;

public class IrisLodRenderProgram {
    public final int modelOffsetUniform;
    public final int worldYOffsetUniform;
    public final int mircoOffsetUniform;
    public final int modelViewUniform;
    public final int modelViewInverseUniform;
    public final int projectionUniform;
    public final int projectionInverseUniform;
    public final int normalMatrix3fUniform;
    public final int clipDistanceUniform;
    private final int id = GL43C.glCreateProgram();
    private final ProgramUniforms uniforms;
    private final CustomUniforms customUniforms;
    private final ProgramSamplers samplers;
    private final ProgramImages images;
    private final BlendModeOverride blend;
    private final BufferBlendOverride[] bufferBlendOverrides;

    private IrisLodRenderProgram(String name, boolean isShadowPass, boolean translucent, BlendModeOverride override, BufferBlendOverride[] bufferBlendOverrides, String vertex, String tessControl, String tessEval, String geometry, String fragment, CustomUniforms customUniforms, IrisRenderingPipeline pipeline) {
        GL32.glBindAttribLocation((int)this.id, (int)0, (CharSequence)"vPosition");
        GL32.glBindAttribLocation((int)this.id, (int)1, (CharSequence)"iris_color");
        GL32.glBindAttribLocation((int)this.id, (int)2, (CharSequence)"irisExtra");
        this.bufferBlendOverrides = bufferBlendOverrides;
        GlShader vert = new GlShader(ShaderType.VERTEX, name + ".vsh", vertex);
        GL43C.glAttachShader((int)this.id, (int)vert.getHandle());
        GlShader tessCont = null;
        if (tessControl != null) {
            tessCont = new GlShader(ShaderType.TESSELATION_CONTROL, name + ".tcs", tessControl);
            GL43C.glAttachShader((int)this.id, (int)tessCont.getHandle());
        }
        GlShader tessE = null;
        if (tessEval != null) {
            tessE = new GlShader(ShaderType.TESSELATION_EVAL, name + ".tes", tessEval);
            GL43C.glAttachShader((int)this.id, (int)tessE.getHandle());
        }
        GlShader geom = null;
        if (geometry != null) {
            geom = new GlShader(ShaderType.GEOMETRY, name + ".gsh", geometry);
            GL43C.glAttachShader((int)this.id, (int)geom.getHandle());
        }
        GlShader frag = new GlShader(ShaderType.FRAGMENT, name + ".fsh", fragment);
        GL43C.glAttachShader((int)this.id, (int)frag.getHandle());
        GL32.glLinkProgram((int)this.id);
        int status = GL32.glGetProgrami((int)this.id, (int)35714);
        if (status != 1) {
            String message = "Shader link error in Iris DH program! Details: " + GL32.glGetProgramInfoLog((int)this.id);
            this.free();
            throw new RuntimeException(message);
        }
        GL32.glUseProgram((int)this.id);
        vert.destroy();
        frag.destroy();
        if (tessCont != null) {
            tessCont.destroy();
        }
        if (tessE != null) {
            tessE.destroy();
        }
        if (geom != null) {
            geom.destroy();
        }
        this.blend = override;
        ProgramUniforms.Builder uniformBuilder = ProgramUniforms.builder(name, this.id);
        ProgramSamplers.Builder samplerBuilder = ProgramSamplers.builder(this.id, IrisSamplers.WORLD_RESERVED_TEXTURE_UNITS);
        CommonUniforms.addDynamicUniforms(uniformBuilder, FogMode.PER_VERTEX);
        customUniforms.assignTo(uniformBuilder);
        BuiltinReplacementUniforms.addBuiltinReplacementUniforms(uniformBuilder);
        ProgramImages.Builder builder = ProgramImages.builder(this.id);
        pipeline.addGbufferOrShadowSamplers(samplerBuilder, builder, isShadowPass ? pipeline::getFlippedBeforeShadow : () -> translucent ? pipeline.getFlippedAfterTranslucent() : pipeline.getFlippedAfterPrepare(), isShadowPass, false, true, false);
        customUniforms.mapholderToPass(uniformBuilder, this);
        this.uniforms = uniformBuilder.buildUniforms();
        this.customUniforms = customUniforms;
        this.samplers = samplerBuilder.build();
        this.images = builder.build();
        this.modelOffsetUniform = this.tryGetUniformLocation2("modelOffset");
        this.worldYOffsetUniform = this.tryGetUniformLocation2("worldYOffset");
        this.mircoOffsetUniform = this.tryGetUniformLocation2("mircoOffset");
        this.projectionUniform = this.tryGetUniformLocation2("iris_ProjectionMatrix");
        this.projectionInverseUniform = this.tryGetUniformLocation2("iris_ProjectionMatrixInverse");
        this.modelViewUniform = this.tryGetUniformLocation2("iris_ModelViewMatrix");
        this.modelViewInverseUniform = this.tryGetUniformLocation2("iris_ModelViewMatrixInverse");
        this.normalMatrix3fUniform = this.tryGetUniformLocation2("iris_NormalMatrix");
        this.clipDistanceUniform = this.tryGetUniformLocation2("clipDistance");
    }

    public static IrisLodRenderProgram createProgram(String name, boolean isShadowPass, boolean translucent, ProgramSource source, CustomUniforms uniforms, IrisRenderingPipeline pipeline) {
        Map<PatchShaderType, String> transformed = TransformPatcher.patchDHTerrain(name, source.getVertexSource().orElseThrow(RuntimeException::new), source.getTessControlSource().orElse(null), source.getTessEvalSource().orElse(null), source.getGeometrySource().orElse(null), source.getFragmentSource().orElseThrow(RuntimeException::new), pipeline.getTextureMap());
        String vertex = transformed.get((Object)PatchShaderType.VERTEX);
        String tessControl = transformed.get((Object)PatchShaderType.TESS_CONTROL);
        String tessEval = transformed.get((Object)PatchShaderType.TESS_EVAL);
        String geometry = transformed.get((Object)PatchShaderType.GEOMETRY);
        String fragment = transformed.get((Object)PatchShaderType.FRAGMENT);
        ShaderPrinter.printProgram(name).addSources(transformed).setName("dh_" + name).print();
        ArrayList bufferOverrides = new ArrayList();
        source.getDirectives().getBufferBlendOverrides().forEach(information -> {
            int index = Ints.indexOf((int[])source.getDirectives().getDrawBuffers(), (int)information.index());
            if (index > -1) {
                bufferOverrides.add(new BufferBlendOverride(index, information.blendMode()));
            }
        });
        return new IrisLodRenderProgram(name, isShadowPass, translucent, source.getDirectives().getBlendModeOverride().orElse(null), (BufferBlendOverride[])bufferOverrides.toArray(BufferBlendOverride[]::new), vertex, tessControl, tessEval, geometry, fragment, uniforms, pipeline);
    }

    public int tryGetUniformLocation2(CharSequence name) {
        return GL32.glGetUniformLocation((int)this.id, (CharSequence)name);
    }

    public void setUniform(int index, Matrix4fc matrix) {
        if (index == -1 || matrix == null) {
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            FloatBuffer buffer = stack.callocFloat(16);
            matrix.get(buffer);
            buffer.rewind();
            RenderSystem.glUniformMatrix4((int)index, (boolean)false, (FloatBuffer)buffer);
        }
    }

    public void setUniform(int index, Matrix3f matrix) {
        if (index == -1) {
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            FloatBuffer buffer = stack.callocFloat(9);
            matrix.get(buffer);
            buffer.rewind();
            RenderSystem.glUniformMatrix3((int)index, (boolean)false, (FloatBuffer)buffer);
        }
    }

    public void bind() {
        GL43C.glUseProgram((int)this.id);
        if (this.blend != null) {
            this.blend.apply();
        }
        for (BufferBlendOverride override : this.bufferBlendOverrides) {
            override.apply();
        }
    }

    public void unbind() {
        GL43C.glUseProgram((int)0);
        ProgramUniforms.clearActiveUniforms();
        ProgramSamplers.clearActiveSamplers();
        BlendModeOverride.restore();
    }

    public void free() {
        GL43C.glDeleteProgram((int)this.id);
    }

    public void fillUniformData(Matrix4fc projection, Matrix4fc modelView, int worldYOffset, float partialTicks) {
        GL43C.glUseProgram((int)this.id);
        Minecraft.m_91087_().f_91063_.m_109154_().m_109896_();
        IrisRenderSystem.bindTextureToUnit(TextureType.TEXTURE_2D.getGlType(), 2, RenderSystem.getShaderTexture((int)2));
        this.setUniform(this.modelViewUniform, modelView);
        this.setUniform(this.modelViewInverseUniform, (Matrix4fc)modelView.invert(new Matrix4f()));
        this.setUniform(this.projectionUniform, projection);
        this.setUniform(this.projectionInverseUniform, (Matrix4fc)projection.invert(new Matrix4f()));
        this.setUniform(this.normalMatrix3fUniform, new Matrix4f(modelView).invert().transpose3x3(new Matrix3f()));
        this.setUniform(this.mircoOffsetUniform, 0.01f);
        if (this.worldYOffsetUniform != -1) {
            this.setUniform(this.worldYOffsetUniform, worldYOffset);
        }
        float dhNearClipDistance = DhApi.Delayed.renderProxy.getNearClipPlaneDistanceInBlocks(partialTicks);
        this.setUniform(this.clipDistanceUniform, dhNearClipDistance);
        this.samplers.update();
        this.uniforms.update();
        this.customUniforms.push(this);
        this.images.update();
    }

    private void setUniform(int index, float value) {
        GL43C.glUniform1f((int)index, (float)value);
    }

    public void setModelPos(DhApiVec3f modelPos) {
        this.setUniform(this.modelOffsetUniform, modelPos);
    }

    private void setUniform(int index, DhApiVec3f pos) {
        GL43C.glUniform3f((int)index, (float)pos.x, (float)pos.y, (float)pos.z);
    }
}

