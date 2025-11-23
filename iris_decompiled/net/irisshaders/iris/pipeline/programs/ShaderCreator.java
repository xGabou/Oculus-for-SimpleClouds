/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.primitives.Ints
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.PackResources
 *  net.minecraft.server.packs.PathPackResources
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceProvider
 *  net.minecraftforge.fml.loading.FMLPaths
 *  org.apache.commons.io.IOUtils
 */
package net.irisshaders.iris.pipeline.programs;

import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Ints;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.irisshaders.iris.gl.blending.AlphaTest;
import net.irisshaders.iris.gl.blending.BlendModeOverride;
import net.irisshaders.iris.gl.blending.BufferBlendOverride;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.irisshaders.iris.gl.image.ImageHolder;
import net.irisshaders.iris.gl.sampler.SamplerHolder;
import net.irisshaders.iris.gl.state.FogMode;
import net.irisshaders.iris.gl.state.ShaderAttributeInputs;
import net.irisshaders.iris.gl.uniform.LocationalUniformHolder;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.pipeline.fallback.ShaderSynthesizer;
import net.irisshaders.iris.pipeline.programs.ExtendedShader;
import net.irisshaders.iris.pipeline.programs.FallbackShader;
import net.irisshaders.iris.pipeline.transform.PatchShaderType;
import net.irisshaders.iris.pipeline.transform.ShaderPrinter;
import net.irisshaders.iris.pipeline.transform.TransformPatcher;
import net.irisshaders.iris.shaderpack.loading.ProgramId;
import net.irisshaders.iris.shaderpack.programs.ProgramSource;
import net.irisshaders.iris.uniforms.CommonUniforms;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import net.irisshaders.iris.uniforms.VanillaUniforms;
import net.irisshaders.iris.uniforms.builtin.BuiltinReplacementUniforms;
import net.irisshaders.iris.uniforms.custom.CustomUniforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.IOUtils;

public class ShaderCreator {
    public static ExtendedShader create(WorldRenderingPipeline pipeline, String name, ProgramSource source, ProgramId programId, GlFramebuffer writingToBeforeTranslucent, GlFramebuffer writingToAfterTranslucent, AlphaTest fallbackAlpha, VertexFormat vertexFormat, ShaderAttributeInputs inputs, FrameUpdateNotifier updateNotifier, IrisRenderingPipeline parent, Supplier<ImmutableSet<Integer>> flipped, FogMode fogMode, boolean isIntensity, boolean isFullbright, boolean isShadowPass, boolean isLines, CustomUniforms customUniforms) throws IOException {
        AlphaTest alpha = source.getDirectives().getAlphaTestOverride().orElse(fallbackAlpha);
        BlendModeOverride blendModeOverride = source.getDirectives().getBlendModeOverride().orElse(programId.getBlendModeOverride());
        Map<PatchShaderType, String> transformed = TransformPatcher.patchVanilla(name, source.getVertexSource().orElseThrow(RuntimeException::new), source.getGeometrySource().orElse(null), source.getTessControlSource().orElse(null), source.getTessEvalSource().orElse(null), source.getFragmentSource().orElseThrow(RuntimeException::new), alpha, isLines, true, inputs, pipeline.getTextureMap());
        String vertex = transformed.get((Object)PatchShaderType.VERTEX);
        String geometry = transformed.get((Object)PatchShaderType.GEOMETRY);
        String tessControl = transformed.get((Object)PatchShaderType.TESS_CONTROL);
        String tessEval = transformed.get((Object)PatchShaderType.TESS_EVAL);
        String fragment = transformed.get((Object)PatchShaderType.FRAGMENT);
        String shaderJsonString = String.format("    {\n    \"blend\": {\n        \"func\": \"add\",\n        \"srcrgb\": \"srcalpha\",\n        \"dstrgb\": \"1-srcalpha\"\n    },\n    \"vertex\": \"%s\",\n    \"fragment\": \"%s\",\n    \"attributes\": [\n        \"Position\",\n        \"Color\",\n        \"UV0\",\n        \"UV1\",\n        \"UV2\",\n        \"Normal\"\n    ],\n    \"uniforms\": [\n        { \"name\": \"iris_TextureMat\", \"type\": \"matrix4x4\", \"count\": 16, \"values\": [ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 ] },\n        { \"name\": \"iris_ModelViewMat\", \"type\": \"matrix4x4\", \"count\": 16, \"values\": [ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 ] },\n        { \"name\": \"iris_ModelViewMatInverse\", \"type\": \"matrix4x4\", \"count\": 16, \"values\": [ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 ] },\n        { \"name\": \"iris_ProjMat\", \"type\": \"matrix4x4\", \"count\": 16, \"values\": [ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 ] },\n        { \"name\": \"iris_ProjMatInverse\", \"type\": \"matrix4x4\", \"count\": 16, \"values\": [ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 ] },\n        { \"name\": \"iris_NormalMat\", \"type\": \"matrix3x3\", \"count\": 9, \"values\": [ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0 ] },\n        { \"name\": \"iris_ChunkOffset\", \"type\": \"float\", \"count\": 3, \"values\": [ 0.0, 0.0, 0.0 ] },\n        { \"name\": \"iris_ColorModulator\", \"type\": \"float\", \"count\": 4, \"values\": [ 1.0, 1.0, 1.0, 1.0 ] },\n        { \"name\": \"iris_GlintAlpha\", \"type\": \"float\", \"count\": 1, \"values\": [ 1.0 ] },\n        { \"name\": \"iris_FogStart\", \"type\": \"float\", \"count\": 1, \"values\": [ 0.0 ] },\n        { \"name\": \"iris_FogEnd\", \"type\": \"float\", \"count\": 1, \"values\": [ 1.0 ] },\n        { \"name\": \"iris_FogColor\", \"type\": \"float\", \"count\": 4, \"values\": [ 0.0, 0.0, 0.0, 0.0 ] }\n    ]\n}", name, name);
        ShaderPrinter.printProgram(name).addSources(transformed).addJson(shaderJsonString).print();
        IrisProgramResourceFactory shaderResourceFactory = new IrisProgramResourceFactory(shaderJsonString, vertex, geometry, tessControl, tessEval, fragment);
        ArrayList<BufferBlendOverride> overrides = new ArrayList<BufferBlendOverride>();
        source.getDirectives().getBufferBlendOverrides().forEach(information -> {
            int index = Ints.indexOf((int[])source.getDirectives().getDrawBuffers(), (int)information.index());
            if (index > -1) {
                overrides.add(new BufferBlendOverride(index, information.blendMode()));
            }
        });
        return new ExtendedShader(shaderResourceFactory, name, vertexFormat, tessControl != null || tessEval != null, writingToBeforeTranslucent, writingToAfterTranslucent, blendModeOverride, alpha, uniforms -> {
            CommonUniforms.addDynamicUniforms(uniforms, FogMode.PER_VERTEX);
            customUniforms.assignTo((LocationalUniformHolder)uniforms);
            BuiltinReplacementUniforms.addBuiltinReplacementUniforms(uniforms);
            VanillaUniforms.addVanillaUniforms(uniforms);
        }, (samplerHolder, imageHolder) -> parent.addGbufferOrShadowSamplers((SamplerHolder)samplerHolder, (ImageHolder)imageHolder, flipped, isShadowPass, inputs.hasTex(), inputs.hasLight(), inputs.hasOverlay()), isIntensity, parent, overrides, customUniforms);
    }

    public static FallbackShader createFallback(String name, GlFramebuffer writingToBeforeTranslucent, GlFramebuffer writingToAfterTranslucent, AlphaTest alpha, VertexFormat vertexFormat, BlendModeOverride blendModeOverride, IrisRenderingPipeline parent, FogMode fogMode, boolean entityLighting, boolean isGlint, boolean isText, boolean intensityTex, boolean isFullbright) throws IOException {
        ShaderAttributeInputs inputs = new ShaderAttributeInputs(vertexFormat, isFullbright, false, isGlint, isText);
        boolean isLeash = vertexFormat == DefaultVertexFormat.f_85816_;
        String vertex = ShaderSynthesizer.vsh(true, inputs, fogMode, entityLighting, isLeash);
        String fragment = ShaderSynthesizer.fsh(inputs, fogMode, alpha, intensityTex, isLeash);
        String shaderJsonString = String.format("    {\n    \"blend\": {\n        \"func\": \"add\",\n        \"srcrgb\": \"srcalpha\",\n        \"dstrgb\": \"1-srcalpha\"\n    },\n    \"vertex\": \"%s\",\n    \"fragment\": \"%s\",\n    \"attributes\": [\n        \"Position\",\n        \"Color\",\n        \"UV0\",\n        \"UV1\",\n        \"UV2\",\n        \"Normal\"\n    ],\n    \"uniforms\": [\n        \t\t{ \"name\": \"TextureMat\", \"type\": \"matrix4x4\", \"count\": 16, \"values\": [ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 ] },\n        \t\t{ \"name\": \"ModelViewMat\", \"type\": \"matrix4x4\", \"count\": 16, \"values\": [ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 ] },\n        \t\t{ \"name\": \"ProjMat\", \"type\": \"matrix4x4\", \"count\": 16, \"values\": [ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 ] },\n        \t\t{ \"name\": \"ChunkOffset\", \"type\": \"float\", \"count\": 3, \"values\": [ 0.0, 0.0, 0.0 ] },\n        \t\t{ \"name\": \"ColorModulator\", \"type\": \"float\", \"count\": 4, \"values\": [ 1.0, 1.0, 1.0, 1.0 ] },\n        \t\t{ \"name\": \"GlintAlpha\", \"type\": \"float\", \"count\": 1, \"values\": [ 1.0 ] },\n        \t\t{ \"name\": \"Light0_Direction\", \"type\": \"float\", \"count\": 3, \"values\": [0.0, 0.0, 0.0] },\n        \t\t{ \"name\": \"Light1_Direction\", \"type\": \"float\", \"count\": 3, \"values\": [0.0, 0.0, 0.0] },\n        \t\t{ \"name\": \"FogStart\", \"type\": \"float\", \"count\": 1, \"values\": [ 0.0 ] },\n        \t\t{ \"name\": \"FogEnd\", \"type\": \"float\", \"count\": 1, \"values\": [ 1.0 ] },\n        \t\t{ \"name\": \"FogDensity\", \"type\": \"float\", \"count\": 1, \"values\": [ 1.0 ] },\n        \t\t{ \"name\": \"FogIsExp2\", \"type\": \"int\", \"count\": 1, \"values\": [ 0 ] },\n        \t\t{ \"name\": \"AlphaTestValue\", \"type\": \"float\", \"count\": 1, \"values\": [ 0.0 ] },\n        \t\t{ \"name\": \"LineWidth\", \"type\": \"float\", \"count\": 1, \"values\": [ 1.0 ] },\n        \t\t{ \"name\": \"ScreenSize\", \"type\": \"float\", \"count\": 2, \"values\": [ 1.0, 1.0 ] },\n        \t\t{ \"name\": \"FogColor\", \"type\": \"float\", \"count\": 4, \"values\": [ 0.0, 0.0, 0.0, 0.0 ] }\n    ]\n}", name, name);
        ShaderPrinter.printProgram(name).addSource(PatchShaderType.VERTEX, vertex).addSource(PatchShaderType.FRAGMENT, fragment).addJson(shaderJsonString).print();
        IrisProgramResourceFactory shaderResourceFactory = new IrisProgramResourceFactory(shaderJsonString, vertex, null, null, null, fragment);
        return new FallbackShader(shaderResourceFactory, name, vertexFormat, writingToBeforeTranslucent, writingToAfterTranslucent, blendModeOverride, alpha.reference(), parent);
    }

    private record IrisProgramResourceFactory(String json, String vertex, String geometry, String tessControl, String tessEval, String fragment) implements ResourceProvider
    {
        public Optional<Resource> m_213713_(ResourceLocation id) {
            String path = id.m_135815_();
            if (path.endsWith("json")) {
                return Optional.of(new StringResource(id, this.json));
            }
            if (path.endsWith("vsh")) {
                return Optional.of(new StringResource(id, this.vertex));
            }
            if (path.endsWith("gsh")) {
                if (this.geometry == null) {
                    return Optional.empty();
                }
                return Optional.of(new StringResource(id, this.geometry));
            }
            if (path.endsWith("tcs")) {
                if (this.tessControl == null) {
                    return Optional.empty();
                }
                return Optional.of(new StringResource(id, this.tessControl));
            }
            if (path.endsWith("tes")) {
                if (this.tessEval == null) {
                    return Optional.empty();
                }
                return Optional.of(new StringResource(id, this.tessEval));
            }
            if (path.endsWith("fsh")) {
                return Optional.of(new StringResource(id, this.fragment));
            }
            return Optional.empty();
        }
    }

    private static class StringResource
    extends Resource {
        private final String content;

        private StringResource(ResourceLocation id, String content) {
            super((PackResources)new PathPackResources("<iris shaderpack shaders>", FMLPaths.CONFIGDIR.get(), true), () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
            this.content = content;
        }

        public InputStream m_215507_() {
            return IOUtils.toInputStream((String)this.content, (Charset)StandardCharsets.UTF_8);
        }
    }
}

