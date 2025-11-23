/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Ints
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiGenericObjectShaderProgram
 *  com.seibel.distanthorizons.api.interfaces.render.IDhApiRenderableBoxGroup
 *  com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam
 *  com.seibel.distanthorizons.api.objects.math.DhApiMat4f
 *  com.seibel.distanthorizons.api.objects.math.DhApiVec3d
 *  com.seibel.distanthorizons.api.objects.math.DhApiVec3f
 *  com.seibel.distanthorizons.api.objects.math.DhApiVec3i
 *  com.seibel.distanthorizons.api.objects.render.DhApiRenderableBox
 *  com.seibel.distanthorizons.api.objects.render.DhApiRenderableBoxGroupShading
 *  net.minecraft.client.Minecraft
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.lwjgl.opengl.GL32
 *  org.lwjgl.opengl.GL32C
 *  org.lwjgl.opengl.GL43C
 *  org.lwjgl.system.MemoryStack
 */
package net.irisshaders.iris.compat.dh;

import com.google.common.primitives.Ints;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiGenericObjectShaderProgram;
import com.seibel.distanthorizons.api.interfaces.render.IDhApiRenderableBoxGroup;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;
import com.seibel.distanthorizons.api.objects.math.DhApiMat4f;
import com.seibel.distanthorizons.api.objects.math.DhApiVec3d;
import com.seibel.distanthorizons.api.objects.math.DhApiVec3f;
import com.seibel.distanthorizons.api.objects.math.DhApiVec3i;
import com.seibel.distanthorizons.api.objects.render.DhApiRenderableBox;
import com.seibel.distanthorizons.api.objects.render.DhApiRenderableBoxGroupShading;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Map;
import net.irisshaders.iris.Iris;
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
import org.lwjgl.opengl.GL32C;
import org.lwjgl.opengl.GL43C;
import org.lwjgl.system.MemoryStack;

public class IrisGenericRenderProgram
implements IDhApiGenericObjectShaderProgram {
    public final int modelViewUniform;
    public final int modelViewInverseUniform;
    public final int projectionUniform;
    public final int projectionInverseUniform;
    public final int normalMatrix3fUniform;
    private final int id = GL43C.glCreateProgram();
    private final ProgramUniforms uniforms;
    private final CustomUniforms customUniforms;
    private final ProgramSamplers samplers;
    private final ProgramImages images;
    private final BlendModeOverride blend;
    private final BufferBlendOverride[] bufferBlendOverrides;
    private final int instancedShaderOffsetChunkUniform;
    private final int instancedShaderOffsetSubChunkUniform;
    private final int instancedShaderCameraChunkPosUniform;
    private final int instancedShaderCameraSubChunkPosUniform;
    private final int instancedShaderProjectionModelViewMatrixUniform;
    private final int va;
    private final int uBlockLight;
    private final int uSkyLight;

    private IrisGenericRenderProgram(String name, boolean isShadowPass, boolean translucent, BlendModeOverride override, BufferBlendOverride[] bufferBlendOverrides, String vertex, String tessControl, String tessEval, String geometry, String fragment, CustomUniforms customUniforms, IrisRenderingPipeline pipeline) {
        GL32.glBindAttribLocation((int)this.id, (int)0, (CharSequence)"vPosition");
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
        this.va = GlStateManager._glGenVertexArrays();
        GlStateManager._glBindVertexArray((int)this.va);
        GL32.glVertexAttribPointer((int)0, (int)3, (int)5126, (boolean)false, (int)0, (long)0L);
        GL32.glEnableVertexAttribArray((int)0);
        this.projectionUniform = this.tryGetUniformLocation2("iris_ProjectionMatrix");
        this.projectionInverseUniform = this.tryGetUniformLocation2("iris_ProjectionMatrixInverse");
        this.modelViewUniform = this.tryGetUniformLocation2("iris_ModelViewMatrix");
        this.modelViewInverseUniform = this.tryGetUniformLocation2("iris_ModelViewMatrixInverse");
        this.normalMatrix3fUniform = this.tryGetUniformLocation2("iris_NormalMatrix");
        this.instancedShaderOffsetChunkUniform = this.tryGetUniformLocation2("uOffsetChunk");
        this.instancedShaderOffsetSubChunkUniform = this.tryGetUniformLocation2("uOffsetSubChunk");
        this.instancedShaderCameraChunkPosUniform = this.tryGetUniformLocation2("uCameraPosChunk");
        this.instancedShaderCameraSubChunkPosUniform = this.tryGetUniformLocation2("uCameraPosSubChunk");
        this.instancedShaderProjectionModelViewMatrixUniform = this.tryGetUniformLocation2("uProjectionMvm");
        this.uBlockLight = this.tryGetUniformLocation2("uBlockLight");
        this.uSkyLight = this.tryGetUniformLocation2("uSkyLight");
    }

    public static IrisGenericRenderProgram createProgram(String name, boolean isShadowPass, boolean translucent, ProgramSource source, CustomUniforms uniforms, IrisRenderingPipeline pipeline) {
        Map<PatchShaderType, String> transformed = TransformPatcher.patchDHGeneric(name, source.getVertexSource().orElseThrow(RuntimeException::new), source.getTessControlSource().orElse(null), source.getTessEvalSource().orElse(null), source.getGeometrySource().orElse(null), source.getFragmentSource().orElseThrow(RuntimeException::new), pipeline.getTextureMap());
        String vertex = transformed.get((Object)PatchShaderType.VERTEX);
        String tessControl = transformed.get((Object)PatchShaderType.TESS_CONTROL);
        String tessEval = transformed.get((Object)PatchShaderType.TESS_EVAL);
        String geometry = transformed.get((Object)PatchShaderType.GEOMETRY);
        String fragment = transformed.get((Object)PatchShaderType.FRAGMENT);
        ShaderPrinter.printProgram(name + "_g").addSources(transformed).setName("dh_" + name + "_g").print();
        ArrayList bufferOverrides = new ArrayList();
        source.getDirectives().getBufferBlendOverrides().forEach(information -> {
            int index = Ints.indexOf((int[])source.getDirectives().getDrawBuffers(), (int)information.index());
            if (index > -1) {
                bufferOverrides.add(new BufferBlendOverride(index, information.blendMode()));
            }
        });
        return new IrisGenericRenderProgram(name, isShadowPass, translucent, source.getDirectives().getBlendModeOverride().orElse(null), (BufferBlendOverride[])bufferOverrides.toArray(BufferBlendOverride[]::new), vertex, tessControl, tessEval, geometry, fragment, uniforms, pipeline);
    }

    private static int getChunkPosFromDouble(double value) {
        return (int)Math.floor(value / 16.0);
    }

    private static float getSubChunkPosFromDouble(double value) {
        double chunkPos = Math.floor(value / 16.0);
        return (float)(value - chunkPos * 16.0);
    }

    public int tryGetUniformLocation2(CharSequence name) {
        return GL32.glGetUniformLocation((int)this.id, (CharSequence)name);
    }

    public void setUniform(int index, Matrix4f matrix) {
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

    public void bind(DhApiRenderParam renderParam) {
        GlStateManager._glBindVertexArray((int)this.va);
        GL32C.glUseProgram((int)this.id);
        if (this.blend != null) {
            this.blend.apply();
        }
        for (BufferBlendOverride override : this.bufferBlendOverrides) {
            override.apply();
        }
        this.setUniform(this.modelViewUniform, this.toJOML(renderParam.dhModelViewMatrix));
        this.setUniform(this.modelViewInverseUniform, this.toJOML(renderParam.dhModelViewMatrix).invert());
        this.setUniform(this.projectionUniform, this.toJOML(renderParam.dhProjectionMatrix));
        this.setUniform(this.projectionInverseUniform, this.toJOML(renderParam.dhModelViewMatrix).invert());
        this.setUniform(this.normalMatrix3fUniform, this.toJOML(renderParam.dhModelViewMatrix).invert().transpose3x3(new Matrix3f()));
        Minecraft.m_91087_().f_91063_.m_109154_().m_109896_();
        IrisRenderSystem.bindTextureToUnit(TextureType.TEXTURE_2D.getGlType(), 2, RenderSystem.getShaderTexture((int)2));
        this.setUniform(this.instancedShaderProjectionModelViewMatrixUniform, this.toJOML(renderParam.dhProjectionMatrix).mul((Matrix4fc)this.toJOML(renderParam.dhModelViewMatrix)));
        this.samplers.update();
        this.uniforms.update();
        this.customUniforms.push(this);
        this.images.update();
    }

    public void unbind() {
        GlStateManager._glBindVertexArray((int)0);
        GL43C.glUseProgram((int)0);
        ProgramUniforms.clearActiveUniforms();
        ProgramSamplers.clearActiveSamplers();
        BlendModeOverride.restore();
    }

    public void bindVertexBuffer(int i) {
        GL32.glBindBuffer((int)34962, (int)i);
        GL32.glVertexAttribPointer((int)0, (int)3, (int)5126, (boolean)false, (int)12, (long)0L);
    }

    public boolean overrideThisFrame() {
        return Iris.getPipelineManager().getPipelineNullable() instanceof IrisRenderingPipeline;
    }

    public int getId() {
        return this.id;
    }

    public void free() {
        GL43C.glDeleteProgram((int)this.id);
    }

    public void fillIndirectUniformData(DhApiRenderParam dhApiRenderParam, DhApiRenderableBoxGroupShading dhApiRenderableBoxGroupShading, IDhApiRenderableBoxGroup boxGroup, DhApiVec3d camPos) {
        this.bind(dhApiRenderParam);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc((int)515);
        this.setUniform(this.instancedShaderOffsetChunkUniform, new DhApiVec3i(IrisGenericRenderProgram.getChunkPosFromDouble(boxGroup.getOriginBlockPos().x), IrisGenericRenderProgram.getChunkPosFromDouble(boxGroup.getOriginBlockPos().y), IrisGenericRenderProgram.getChunkPosFromDouble(boxGroup.getOriginBlockPos().z)));
        this.setUniform(this.instancedShaderOffsetSubChunkUniform, new DhApiVec3f(IrisGenericRenderProgram.getSubChunkPosFromDouble(boxGroup.getOriginBlockPos().x), IrisGenericRenderProgram.getSubChunkPosFromDouble(boxGroup.getOriginBlockPos().y), IrisGenericRenderProgram.getSubChunkPosFromDouble(boxGroup.getOriginBlockPos().z)));
        this.setUniform(this.instancedShaderCameraChunkPosUniform, new DhApiVec3i(IrisGenericRenderProgram.getChunkPosFromDouble(camPos.x), IrisGenericRenderProgram.getChunkPosFromDouble(camPos.y), IrisGenericRenderProgram.getChunkPosFromDouble(camPos.z)));
        this.setUniform(this.instancedShaderCameraSubChunkPosUniform, new DhApiVec3f(IrisGenericRenderProgram.getSubChunkPosFromDouble(camPos.x), IrisGenericRenderProgram.getSubChunkPosFromDouble(camPos.y), IrisGenericRenderProgram.getSubChunkPosFromDouble(camPos.z)));
        this.setUniform(this.uBlockLight, boxGroup.getBlockLight());
        this.setUniform(this.uSkyLight, boxGroup.getSkyLight());
    }

    public void fillSharedDirectUniformData(DhApiRenderParam dhApiRenderParam, DhApiRenderableBoxGroupShading dhApiRenderableBoxGroupShading, IDhApiRenderableBoxGroup iDhApiRenderableBoxGroup, DhApiVec3d dhApiVec3d) {
        throw new IllegalStateException("Only indirect is supported with Iris.");
    }

    public void fillDirectUniformData(DhApiRenderParam dhApiRenderParam, IDhApiRenderableBoxGroup iDhApiRenderableBoxGroup, DhApiRenderableBox dhApiRenderableBox, DhApiVec3d dhApiVec3d) {
        throw new IllegalStateException("Only indirect is supported with Iris.");
    }

    private Matrix4f toJOML(DhApiMat4f mat4f) {
        return new Matrix4f().setTransposed(mat4f.getValuesAsArray());
    }

    private void setUniform(int index, int value) {
        GL43C.glUniform1i((int)index, (int)value);
    }

    private void setUniform(int index, float value) {
        GL43C.glUniform1f((int)index, (float)value);
    }

    private void setUniform(int index, DhApiVec3f pos) {
        GL43C.glUniform3f((int)index, (float)pos.x, (float)pos.y, (float)pos.z);
    }

    private void setUniform(int index, DhApiVec3i pos) {
        GL43C.glUniform3i((int)index, (int)pos.x, (int)pos.y, (int)pos.z);
    }
}

