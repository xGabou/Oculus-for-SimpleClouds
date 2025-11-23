/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.primitives.Ints
 *  me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexType
 *  net.minecraft.resources.ResourceLocation
 */
package net.irisshaders.iris.pipeline;

import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Ints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexType;
import net.irisshaders.iris.gl.blending.AlphaTest;
import net.irisshaders.iris.gl.blending.AlphaTests;
import net.irisshaders.iris.gl.blending.BlendModeOverride;
import net.irisshaders.iris.gl.blending.BufferBlendOverride;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.irisshaders.iris.gl.program.ProgramImages;
import net.irisshaders.iris.gl.program.ProgramSamplers;
import net.irisshaders.iris.gl.program.ProgramUniforms;
import net.irisshaders.iris.gl.state.FogMode;
import net.irisshaders.iris.gl.state.ShaderAttributeInputs;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.pipeline.transform.PatchShaderType;
import net.irisshaders.iris.pipeline.transform.ShaderPrinter;
import net.irisshaders.iris.pipeline.transform.TransformPatcher;
import net.irisshaders.iris.shaderpack.loading.ProgramId;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import net.irisshaders.iris.shaderpack.programs.ProgramSource;
import net.irisshaders.iris.targets.RenderTargets;
import net.irisshaders.iris.uniforms.CommonUniforms;
import net.irisshaders.iris.uniforms.builtin.BuiltinReplacementUniforms;
import net.irisshaders.iris.uniforms.custom.CustomUniforms;
import net.minecraft.resources.ResourceLocation;

public class SodiumTerrainPipeline {
    private static final Supplier<Optional<AlphaTest>> terrainCutoutDefault = () -> Optional.of(AlphaTests.ONE_TENTH_ALPHA);
    private static final Supplier<Optional<AlphaTest>> translucentDefault = () -> Optional.of(AlphaTest.ALWAYS);
    private static final Supplier<Optional<AlphaTest>> shadowDefault = () -> Optional.of(AlphaTests.ONE_TENTH_ALPHA);
    private static final String defaultVertex = "#version 330 core\n\nin ivec2 a_LightCoord;\nin vec4 a_Color;\nin vec2 a_TexCoord;\nin uvec4 a_PosId;\nuniform mat4 iris_ProjectionMatrix;\nuniform int fogShape;\nuniform mat4 iris_ModelViewMatrix;\nuniform vec3 u_RegionOffset;\nvec3 _vert_position;\nvec2 _vert_tex_diffuse_coord;\nivec2 _vert_tex_light_coord;\nvec4 _vert_color;\nuint _draw_id;\nuint _material_params;\nout float v_FragDistance;\n\n\nconst int FOG_SHAPE_SPHERICAL = 0;\nconst int FOG_SHAPE_CYLINDRICAL = 1;\n\nvec4 _linearFog(vec4 fragColor, float fragDistance, vec4 fogColor, float fogStart, float fogEnd) {\n    float factor = smoothstep(fogStart, fogEnd, fragDistance * fogColor.a); // alpha value of fog is used as a weight\n    vec3 blended = mix(fragColor.rgb, fogColor.rgb, factor);\n\n    return vec4(blended, fragColor.a); // alpha value of fragment cannot be modified\n}\n\nfloat getFragDistance(int fogShape, vec3 position) {\n    // Use the maximum of the horizontal and vertical distance to get cylindrical fog if fog shape is cylindrical\n    switch (fogShape) {\n        case FOG_SHAPE_SPHERICAL: return length(position);\n        case FOG_SHAPE_CYLINDRICAL: return max(length(position.xz), abs(position.y));\n        default: return length(position); // This shouldn't be possible to get, but return a sane value just in case\n    }\n}\n\nout vec4 v_ColorModulator;\nout vec2 v_TexCoord;\nout float v_MaterialMipBias;\nout float v_MaterialAlphaCutoff;\nconst uint MATERIAL_USE_MIP_OFFSET = 0u;\nconst uint MATERIAL_ALPHA_CUTOFF_OFFSET = 1u;\n\nfloat _material_mip_bias(uint material) {\n\treturn ((material >> MATERIAL_USE_MIP_OFFSET) & 1u) != 0u ? 0.0f : -4.0f;\n}\n\nconst float[4] ALPHA_CUTOFF = float[4](0.0, 0.1, 0.5, 1.0);\n\nfloat _material_alpha_cutoff(uint material) {\n    return ALPHA_CUTOFF[(material >> MATERIAL_ALPHA_CUTOFF_OFFSET) & 3u];\n}\n\nvoid _vert_init() {\n\t_vert_position = (vec3(a_PosId.xyz) * 4.8828125E-4f + -8.0f);\n\n_vert_tex_diffuse_coord = (a_TexCoord * 3.0517578E-5);\t_vert_tex_light_coord = a_LightCoord;\n\t_vert_color = a_Color;\n\t_draw_id = (a_PosId.w >> 8u) & 0xffu;\n\t_material_params = (a_PosId.w >> 0u) & 0xFFu;\n}\nuvec3 _get_relative_chunk_coord(uint pos) {\n\treturn uvec3(pos) >> uvec3(5u, 0u, 2u) & uvec3(7u, 3u, 7u);\n}\nvec3 _get_draw_translation(uint pos) {\n\treturn _get_relative_chunk_coord(pos) * vec3(16.0f);\n}\nvec4 getVertexPosition() {\n\treturn vec4(_vert_position + u_RegionOffset + _get_draw_translation(_draw_id), 1.0f);\n}\n\nuniform sampler2D lightmap; // The light map texture\n\nvec3 _sample_lightmap(ivec2 uv) {\n    return texture(lightmap, clamp(uv / 256.0, vec2(0.5 / 16.0), vec2(15.5 / 16.0))).rgb;\n}\n\n\nvoid main() {\n    _vert_init();\n\n    // Transform the chunk-local vertex position into world model space\n    vec3 translation = u_RegionOffset + _get_draw_translation(_draw_id);\n    vec3 position = _vert_position + translation;\n\n    v_FragDistance = getFragDistance(fogShape, position);\n\n    // Transform the vertex position into model-view-projection space\n    gl_Position = iris_ProjectionMatrix * iris_ModelViewMatrix * vec4(position, 1.0);\n\n    v_ColorModulator = vec4((_vert_color.rgb * _vert_color.a), 1) * vec4(_sample_lightmap(_vert_tex_light_coord), 1.0);\n    v_TexCoord = _vert_tex_diffuse_coord;\n\n    v_MaterialMipBias = _material_mip_bias(_material_params);\n    v_MaterialAlphaCutoff = _material_alpha_cutoff(_material_params);\n}\n";
    private static final String defaultFragment = "#version 330 core\n\nconst int FOG_SHAPE_SPHERICAL = 0;\nconst int FOG_SHAPE_CYLINDRICAL = 1;\n\nvec4 _linearFog(vec4 fragColor, float fragDistance, vec4 fogColor, float fogStart, float fogEnd) {\n    float factor = smoothstep(fogStart, fogEnd, fragDistance * fogColor.a); // alpha value of fog is used as a weight\n    vec3 blended = mix(fragColor.rgb, fogColor.rgb, factor);\n\n    return vec4(blended, fragColor.a); // alpha value of fragment cannot be modified\n}\n\nin vec4 v_ColorModulator; // The interpolated vertex color\nin vec2 v_TexCoord; // The interpolated block texture coordinates\n\nin float v_FragDistance; // The fragment's distance from the camera\n\nin float v_MaterialMipBias;\nin float v_MaterialAlphaCutoff;\n\nuniform sampler2D gtexture; // The block atlas texture\n\nuniform vec4 iris_FogColor; // The color of the shader fog\nuniform float iris_FogStart; // The starting position of the shader fog\nuniform float iris_FogEnd; // The ending position of the shader fog\n\nout vec4 out_FragColor; // The output fragment for the color framebuffer\n\nvoid main() {\n    vec4 diffuseColor = texture(gtexture, v_TexCoord, v_MaterialMipBias);\n\n    if (diffuseColor.a < 0.1) {\n        discard;\n    }\n\n    // Modulate the color (used by ambient occlusion and per-vertex colouring)\n    diffuseColor.rgb *= v_ColorModulator.rgb;\n\n    out_FragColor = _linearFog(diffuseColor, v_FragDistance, iris_FogColor, iris_FogStart, iris_FogEnd);\n}\n";
    private final WorldRenderingPipeline parent;
    private final CustomUniforms customUniforms;
    private final IntFunction<ProgramSamplers> createTerrainSamplers;
    private final IntFunction<ProgramSamplers> createShadowSamplers;
    private final IntFunction<ProgramImages> createTerrainImages;
    private final IntFunction<ProgramImages> createShadowImages;
    Optional<String> terrainSolidVertex;
    Optional<String> terrainSolidGeometry;
    Optional<String> terrainSolidTessControl;
    Optional<String> terrainSolidTessEval;
    Optional<String> terrainSolidFragment;
    GlFramebuffer terrainSolidFramebuffer;
    BlendModeOverride terrainSolidBlendOverride;
    List<BufferBlendOverride> terrainSolidBufferOverrides;
    Optional<String> terrainCutoutVertex;
    Optional<String> terrainCutoutGeometry;
    Optional<String> terrainCutoutTessControl;
    Optional<String> terrainCutoutTessEval;
    Optional<String> terrainCutoutFragment;
    GlFramebuffer terrainCutoutFramebuffer;
    BlendModeOverride terrainCutoutBlendOverride;
    List<BufferBlendOverride> terrainCutoutBufferOverrides;
    Optional<AlphaTest> terrainCutoutAlpha;
    Optional<String> translucentVertex;
    Optional<String> translucentGeometry;
    Optional<String> translucentTessControl;
    Optional<String> translucentTessEval;
    Optional<String> translucentFragment;
    GlFramebuffer translucentFramebuffer;
    BlendModeOverride translucentBlendOverride;
    List<BufferBlendOverride> translucentBufferOverrides;
    Optional<AlphaTest> translucentAlpha;
    Optional<String> shadowVertex;
    Optional<String> shadowGeometry;
    Optional<String> shadowTessControl;
    Optional<String> shadowTessEval;
    Optional<String> shadowFragment;
    Optional<String> shadowCutoutFragment;
    GlFramebuffer shadowFramebuffer;
    BlendModeOverride shadowBlendOverride = BlendModeOverride.OFF;
    List<BufferBlendOverride> shadowBufferOverrides;
    Optional<AlphaTest> shadowAlpha;
    ProgramSet programSet;

    public SodiumTerrainPipeline(WorldRenderingPipeline parent, ProgramSet programSet, IntFunction<ProgramSamplers> createTerrainSamplers, IntFunction<ProgramSamplers> createShadowSamplers, IntFunction<ProgramImages> createTerrainImages, IntFunction<ProgramImages> createShadowImages, RenderTargets targets, ImmutableSet<Integer> flippedAfterPrepare, ImmutableSet<Integer> flippedAfterTranslucent, GlFramebuffer shadowFramebuffer, CustomUniforms customUniforms) {
        this.parent = Objects.requireNonNull(parent);
        this.customUniforms = customUniforms;
        Optional<ProgramSource> terrainSolidSource = SodiumTerrainPipeline.first(programSet.getGbuffersTerrainSolid(), programSet.getGbuffersTerrain(), programSet.getGbuffersTexturedLit(), programSet.getGbuffersTextured(), programSet.getGbuffersBasic());
        Optional<ProgramSource> terrainCutoutSource = SodiumTerrainPipeline.first(programSet.getGbuffersTerrainCutout(), programSet.getGbuffersTerrain(), programSet.getGbuffersTexturedLit(), programSet.getGbuffersTextured(), programSet.getGbuffersBasic());
        Optional<ProgramSource> translucentSource = SodiumTerrainPipeline.first(programSet.getGbuffersWater(), terrainCutoutSource);
        this.programSet = programSet;
        this.shadowFramebuffer = shadowFramebuffer;
        terrainSolidSource.ifPresent(sources -> {
            this.terrainSolidFramebuffer = targets.createGbufferFramebuffer(flippedAfterPrepare, sources.getDirectives().getDrawBuffers());
        });
        terrainCutoutSource.ifPresent(sources -> {
            this.terrainCutoutFramebuffer = targets.createGbufferFramebuffer(flippedAfterPrepare, sources.getDirectives().getDrawBuffers());
        });
        translucentSource.ifPresent(sources -> {
            this.translucentFramebuffer = targets.createGbufferFramebuffer(flippedAfterTranslucent, sources.getDirectives().getDrawBuffers());
        });
        if (this.terrainSolidFramebuffer == null) {
            this.terrainSolidFramebuffer = targets.createGbufferFramebuffer(flippedAfterPrepare, new int[]{0});
        }
        if (this.terrainCutoutFramebuffer == null) {
            this.terrainCutoutFramebuffer = targets.createGbufferFramebuffer(flippedAfterPrepare, new int[]{0});
        }
        if (this.translucentFramebuffer == null) {
            this.translucentFramebuffer = targets.createGbufferFramebuffer(flippedAfterTranslucent, new int[]{0});
        }
        this.createTerrainSamplers = createTerrainSamplers;
        this.createShadowSamplers = createShadowSamplers;
        this.createTerrainImages = createTerrainImages;
        this.createShadowImages = createShadowImages;
    }

    @SafeVarargs
    private static <T> Optional<T> first(Optional<T> ... candidates) {
        for (Optional<T> candidate : candidates) {
            if (!candidate.isPresent()) continue;
            return candidate;
        }
        return Optional.empty();
    }

    public static String parseSodiumImport(String shader) {
        Pattern IMPORT_PATTERN = Pattern.compile("#import <(?<namespace>.*):(?<path>.*)>");
        Matcher matcher = IMPORT_PATTERN.matcher(shader);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Malformed import statement (expected format: " + IMPORT_PATTERN + ")");
        }
        String namespace = matcher.group("namespace");
        String path = matcher.group("path");
        ResourceLocation identifier = new ResourceLocation(namespace, path);
        return "";
    }

    public void patchShaders(ChunkVertexType vertexType) {
        ShaderAttributeInputs inputs = new ShaderAttributeInputs(true, true, false, true, true);
        Optional terrainSolidSource = SodiumTerrainPipeline.first(this.programSet.getGbuffersTerrainSolid(), this.programSet.getGbuffersTerrain(), this.programSet.getGbuffersTexturedLit(), this.programSet.getGbuffersTextured(), this.programSet.getGbuffersBasic());
        Optional terrainCutoutSource = SodiumTerrainPipeline.first(this.programSet.getGbuffersTerrainCutout(), this.programSet.getGbuffersTerrain(), this.programSet.getGbuffersTexturedLit(), this.programSet.getGbuffersTextured(), this.programSet.getGbuffersBasic());
        Optional translucentSource = SodiumTerrainPipeline.first(this.programSet.getGbuffersWater(), terrainCutoutSource);
        terrainSolidSource.ifPresentOrElse(sources -> {
            this.terrainSolidBlendOverride = sources.getDirectives().getBlendModeOverride().orElse(ProgramId.Terrain.getBlendModeOverride());
            this.terrainSolidBufferOverrides = new ArrayList<BufferBlendOverride>();
            sources.getDirectives().getBufferBlendOverrides().forEach(information -> {
                int index = Ints.indexOf((int[])sources.getDirectives().getDrawBuffers(), (int)information.index());
                if (index > -1) {
                    this.terrainSolidBufferOverrides.add(new BufferBlendOverride(index, information.blendMode()));
                }
            });
            Map<PatchShaderType, String> transformed = TransformPatcher.patchSodium(sources.getName(), sources.getVertexSource().orElse(null), sources.getGeometrySource().orElse(null), sources.getTessControlSource().orElse(null), sources.getTessEvalSource().orElse(null), sources.getFragmentSource().orElse(null), AlphaTest.ALWAYS, inputs, this.parent.getTextureMap());
            this.terrainSolidVertex = Optional.ofNullable(transformed.get((Object)PatchShaderType.VERTEX));
            this.terrainSolidGeometry = Optional.ofNullable(transformed.get((Object)PatchShaderType.GEOMETRY));
            this.terrainSolidTessControl = Optional.ofNullable(transformed.get((Object)PatchShaderType.TESS_CONTROL));
            this.terrainSolidTessEval = Optional.ofNullable(transformed.get((Object)PatchShaderType.TESS_EVAL));
            this.terrainSolidFragment = Optional.ofNullable(transformed.get((Object)PatchShaderType.FRAGMENT));
            ShaderPrinter.printProgram(sources.getName() + "_sodium_solid").addSources(transformed).print();
        }, () -> {
            this.terrainSolidBlendOverride = null;
            this.terrainSolidBufferOverrides = Collections.emptyList();
            this.terrainSolidVertex = Optional.of(defaultVertex);
            this.terrainSolidGeometry = Optional.empty();
            this.terrainSolidTessControl = Optional.empty();
            this.terrainSolidTessEval = Optional.empty();
            this.terrainSolidFragment = Optional.of(defaultFragment);
        });
        terrainCutoutSource.ifPresentOrElse(sources -> {
            this.terrainCutoutBlendOverride = sources.getDirectives().getBlendModeOverride().orElse(ProgramId.Terrain.getBlendModeOverride());
            this.terrainCutoutBufferOverrides = new ArrayList<BufferBlendOverride>();
            sources.getDirectives().getBufferBlendOverrides().forEach(information -> {
                int index = Ints.indexOf((int[])sources.getDirectives().getDrawBuffers(), (int)information.index());
                if (index > -1) {
                    this.terrainCutoutBufferOverrides.add(new BufferBlendOverride(index, information.blendMode()));
                }
            });
            this.terrainCutoutAlpha = sources.getDirectives().getAlphaTestOverride().or(terrainCutoutDefault);
            Map<PatchShaderType, String> transformed = TransformPatcher.patchSodium(sources.getName(), sources.getVertexSource().orElse(null), sources.getGeometrySource().orElse(null), sources.getTessControlSource().orElse(null), sources.getTessEvalSource().orElse(null), sources.getFragmentSource().orElse(null), this.terrainCutoutAlpha.orElse(AlphaTests.ONE_TENTH_ALPHA), inputs, this.parent.getTextureMap());
            this.terrainCutoutVertex = Optional.ofNullable(transformed.get((Object)PatchShaderType.VERTEX));
            this.terrainCutoutGeometry = Optional.ofNullable(transformed.get((Object)PatchShaderType.GEOMETRY));
            this.terrainCutoutTessControl = Optional.ofNullable(transformed.get((Object)PatchShaderType.TESS_CONTROL));
            this.terrainCutoutTessEval = Optional.ofNullable(transformed.get((Object)PatchShaderType.TESS_EVAL));
            this.terrainCutoutFragment = Optional.ofNullable(transformed.get((Object)PatchShaderType.FRAGMENT));
            ShaderPrinter.printProgram(sources.getName() + "_sodium_cutout").addSources(transformed).print();
        }, () -> {
            this.terrainCutoutBlendOverride = null;
            this.terrainCutoutBufferOverrides = Collections.emptyList();
            this.terrainCutoutAlpha = terrainCutoutDefault.get();
            this.terrainCutoutVertex = Optional.of(defaultVertex);
            this.terrainCutoutGeometry = Optional.empty();
            this.terrainCutoutTessControl = Optional.empty();
            this.terrainCutoutTessEval = Optional.empty();
            this.terrainCutoutFragment = Optional.of(defaultFragment);
        });
        translucentSource.ifPresentOrElse(sources -> {
            this.translucentBlendOverride = sources.getDirectives().getBlendModeOverride().orElse(ProgramId.Water.getBlendModeOverride());
            this.translucentBufferOverrides = new ArrayList<BufferBlendOverride>();
            sources.getDirectives().getBufferBlendOverrides().forEach(information -> {
                int index = Ints.indexOf((int[])sources.getDirectives().getDrawBuffers(), (int)information.index());
                if (index > -1) {
                    this.translucentBufferOverrides.add(new BufferBlendOverride(index, information.blendMode()));
                }
            });
            this.translucentAlpha = sources.getDirectives().getAlphaTestOverride().or(translucentDefault);
            Map<PatchShaderType, String> transformed = TransformPatcher.patchSodium(sources.getName(), sources.getVertexSource().orElse(null), sources.getGeometrySource().orElse(null), sources.getTessControlSource().orElse(null), sources.getTessEvalSource().orElse(null), sources.getFragmentSource().orElse(null), this.translucentAlpha.orElse(AlphaTest.ALWAYS), inputs, this.parent.getTextureMap());
            this.translucentVertex = Optional.ofNullable(transformed.get((Object)PatchShaderType.VERTEX));
            this.translucentGeometry = Optional.ofNullable(transformed.get((Object)PatchShaderType.GEOMETRY));
            this.translucentTessControl = Optional.ofNullable(transformed.get((Object)PatchShaderType.TESS_CONTROL));
            this.translucentTessEval = Optional.ofNullable(transformed.get((Object)PatchShaderType.TESS_EVAL));
            this.translucentFragment = Optional.ofNullable(transformed.get((Object)PatchShaderType.FRAGMENT));
            ShaderPrinter.printProgram(sources.getName() + "_sodium").addSources(transformed).print();
        }, () -> {
            this.translucentBlendOverride = null;
            this.translucentBufferOverrides = Collections.emptyList();
            this.translucentAlpha = translucentDefault.get();
            this.translucentVertex = Optional.of(defaultVertex);
            this.translucentGeometry = Optional.empty();
            this.translucentTessControl = Optional.empty();
            this.translucentTessEval = Optional.empty();
            this.translucentFragment = Optional.of(defaultFragment);
        });
        this.programSet.getShadow().ifPresentOrElse(sources -> {
            this.shadowBlendOverride = sources.getDirectives().getBlendModeOverride().orElse(ProgramId.Shadow.getBlendModeOverride());
            this.shadowBufferOverrides = new ArrayList<BufferBlendOverride>();
            sources.getDirectives().getBufferBlendOverrides().forEach(information -> {
                int index = Ints.indexOf((int[])sources.getDirectives().getDrawBuffers(), (int)information.index());
                if (index > -1) {
                    this.shadowBufferOverrides.add(new BufferBlendOverride(index, information.blendMode()));
                }
            });
            this.shadowAlpha = sources.getDirectives().getAlphaTestOverride().or(shadowDefault);
            Map<PatchShaderType, String> transformed = TransformPatcher.patchSodium(sources.getName(), sources.getVertexSource().orElse(null), sources.getGeometrySource().orElse(null), sources.getTessControlSource().orElse(null), sources.getTessEvalSource().orElse(null), sources.getFragmentSource().orElse(null), AlphaTest.ALWAYS, inputs, this.parent.getTextureMap());
            Map<PatchShaderType, String> transformedCutout = TransformPatcher.patchSodium(sources.getName(), sources.getVertexSource().orElse(null), sources.getGeometrySource().orElse(null), sources.getTessControlSource().orElse(null), sources.getTessEvalSource().orElse(null), sources.getFragmentSource().orElse(null), this.shadowAlpha.get(), inputs, this.parent.getTextureMap());
            this.shadowVertex = Optional.ofNullable(transformed.get((Object)PatchShaderType.VERTEX));
            this.shadowGeometry = Optional.ofNullable(transformed.get((Object)PatchShaderType.GEOMETRY));
            this.shadowTessControl = Optional.ofNullable(transformed.get((Object)PatchShaderType.TESS_CONTROL));
            this.shadowTessEval = Optional.ofNullable(transformed.get((Object)PatchShaderType.TESS_EVAL));
            this.shadowCutoutFragment = Optional.ofNullable(transformedCutout.get((Object)PatchShaderType.FRAGMENT));
            this.shadowFragment = Optional.ofNullable(transformed.get((Object)PatchShaderType.FRAGMENT));
            ShaderPrinter.printProgram(sources.getName() + "_sodium").addSources(transformed).setName(sources.getName() + "_sodium_cutout").addSource(PatchShaderType.FRAGMENT, this.shadowCutoutFragment.orElse(null)).print();
        }, () -> {
            this.shadowBlendOverride = null;
            this.shadowBufferOverrides = Collections.emptyList();
            this.shadowAlpha = shadowDefault.get();
            this.shadowVertex = Optional.empty();
            this.shadowGeometry = Optional.empty();
            this.shadowTessControl = Optional.empty();
            this.shadowTessEval = Optional.empty();
            this.shadowCutoutFragment = Optional.empty();
            this.shadowFragment = Optional.empty();
        });
    }

    public Optional<String> getTerrainSolidVertexShaderSource() {
        return this.terrainSolidVertex;
    }

    public Optional<String> getTerrainSolidGeometryShaderSource() {
        return this.terrainSolidGeometry;
    }

    public Optional<String> getTerrainSolidTessControlShaderSource() {
        return this.terrainSolidTessControl;
    }

    public Optional<String> getTerrainSolidTessEvalShaderSource() {
        return this.terrainSolidTessEval;
    }

    public Optional<String> getTerrainSolidFragmentShaderSource() {
        return this.terrainSolidFragment;
    }

    public Optional<String> getTerrainCutoutVertexShaderSource() {
        return this.terrainCutoutVertex;
    }

    public Optional<String> getTerrainCutoutGeometryShaderSource() {
        return this.terrainCutoutGeometry;
    }

    public Optional<String> getTerrainCutoutTessControlShaderSource() {
        return this.terrainCutoutTessControl;
    }

    public Optional<String> getTerrainCutoutTessEvalShaderSource() {
        return this.terrainCutoutTessEval;
    }

    public Optional<String> getTerrainCutoutFragmentShaderSource() {
        return this.terrainCutoutFragment;
    }

    public GlFramebuffer getTerrainSolidFramebuffer() {
        return this.terrainSolidFramebuffer;
    }

    public GlFramebuffer getTerrainCutoutFramebuffer() {
        return this.terrainCutoutFramebuffer;
    }

    public BlendModeOverride getTerrainSolidBlendOverride() {
        return this.terrainSolidBlendOverride;
    }

    public List<BufferBlendOverride> getTerrainSolidBufferOverrides() {
        return this.terrainSolidBufferOverrides;
    }

    public BlendModeOverride getTerrainCutoutBlendOverride() {
        return this.terrainCutoutBlendOverride;
    }

    public List<BufferBlendOverride> getTerrainCutoutBufferOverrides() {
        return this.terrainCutoutBufferOverrides;
    }

    public Optional<AlphaTest> getTerrainCutoutAlpha() {
        return this.terrainCutoutAlpha;
    }

    public Optional<String> getTranslucentVertexShaderSource() {
        return this.translucentVertex;
    }

    public Optional<String> getTranslucentGeometryShaderSource() {
        return this.translucentGeometry;
    }

    public Optional<String> getTranslucentTessControlShaderSource() {
        return this.translucentTessControl;
    }

    public Optional<String> getTranslucentTessEvalShaderSource() {
        return this.translucentTessEval;
    }

    public Optional<String> getTranslucentFragmentShaderSource() {
        return this.translucentFragment;
    }

    public GlFramebuffer getTranslucentFramebuffer() {
        return this.translucentFramebuffer;
    }

    public BlendModeOverride getTranslucentBlendOverride() {
        return this.translucentBlendOverride;
    }

    public List<BufferBlendOverride> getTranslucentBufferOverrides() {
        return this.translucentBufferOverrides;
    }

    public Optional<AlphaTest> getTranslucentAlpha() {
        return this.translucentAlpha;
    }

    public Optional<String> getShadowVertexShaderSource() {
        return this.shadowVertex;
    }

    public Optional<String> getShadowGeometryShaderSource() {
        return this.shadowGeometry;
    }

    public Optional<String> getShadowTessControlShaderSource() {
        return this.shadowTessControl;
    }

    public Optional<String> getShadowTessEvalShaderSource() {
        return this.shadowTessEval;
    }

    public Optional<String> getShadowFragmentShaderSource() {
        return this.shadowFragment;
    }

    public Optional<String> getShadowCutoutFragmentShaderSource() {
        return this.shadowCutoutFragment;
    }

    public GlFramebuffer getShadowFramebuffer() {
        return this.shadowFramebuffer;
    }

    public BlendModeOverride getShadowBlendOverride() {
        return this.shadowBlendOverride;
    }

    public List<BufferBlendOverride> getShadowBufferOverrides() {
        return this.shadowBufferOverrides;
    }

    public Optional<AlphaTest> getShadowAlpha() {
        return this.shadowAlpha;
    }

    public ProgramUniforms.Builder initUniforms(int programId) {
        ProgramUniforms.Builder uniforms = ProgramUniforms.builder("<sodium shaders>", programId);
        CommonUniforms.addDynamicUniforms(uniforms, FogMode.PER_VERTEX);
        this.customUniforms.assignTo(uniforms);
        BuiltinReplacementUniforms.addBuiltinReplacementUniforms(uniforms);
        return uniforms;
    }

    public boolean hasShadowPass() {
        return this.createShadowSamplers != null;
    }

    public ProgramSamplers initTerrainSamplers(int programId) {
        return this.createTerrainSamplers.apply(programId);
    }

    public ProgramSamplers initShadowSamplers(int programId) {
        return this.createShadowSamplers.apply(programId);
    }

    public ProgramImages initTerrainImages(int programId) {
        return this.createTerrainImages.apply(programId);
    }

    public ProgramImages initShadowImages(int programId) {
        return this.createShadowImages.apply(programId);
    }

    public CustomUniforms getCustomUniforms() {
        return this.customUniforms;
    }
}

