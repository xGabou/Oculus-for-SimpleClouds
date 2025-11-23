/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.gl.GlObject
 *  me.jellysquid.mods.sodium.client.gl.shader.GlProgram
 *  me.jellysquid.mods.sodium.client.gl.shader.GlProgram$Builder
 *  me.jellysquid.mods.sodium.client.gl.shader.GlShader
 *  me.jellysquid.mods.sodium.client.gl.shader.ShaderType
 *  me.jellysquid.mods.sodium.client.render.chunk.shader.ChunkFogMode
 *  me.jellysquid.mods.sodium.client.render.chunk.shader.ChunkShaderOptions
 *  me.jellysquid.mods.sodium.client.render.chunk.terrain.TerrainRenderPass
 *  me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexType
 *  net.minecraft.client.Minecraft
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.compat.sodium.impl.shader_overrides;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import me.jellysquid.mods.sodium.client.gl.GlObject;
import me.jellysquid.mods.sodium.client.gl.shader.GlProgram;
import me.jellysquid.mods.sodium.client.gl.shader.GlShader;
import me.jellysquid.mods.sodium.client.gl.shader.ShaderType;
import me.jellysquid.mods.sodium.client.render.chunk.shader.ChunkFogMode;
import me.jellysquid.mods.sodium.client.render.chunk.shader.ChunkShaderOptions;
import me.jellysquid.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexType;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.compat.sodium.impl.shader_overrides.IrisChunkShaderInterface;
import net.irisshaders.iris.compat.sodium.impl.shader_overrides.IrisShaderTypes;
import net.irisshaders.iris.compat.sodium.impl.shader_overrides.IrisTerrainPass;
import net.irisshaders.iris.compat.sodium.impl.shader_overrides.ShaderBindingContextExt;
import net.irisshaders.iris.gl.GLDebug;
import net.irisshaders.iris.gl.blending.AlphaTest;
import net.irisshaders.iris.gl.blending.AlphaTests;
import net.irisshaders.iris.gl.blending.BlendModeOverride;
import net.irisshaders.iris.gl.blending.BufferBlendOverride;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.irisshaders.iris.pipeline.SodiumTerrainPipeline;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class IrisChunkProgramOverrides {
    private final EnumMap<IrisTerrainPass, GlProgram<IrisChunkShaderInterface>> programs = new EnumMap(IrisTerrainPass.class);
    private boolean shadersCreated = false;
    private int versionCounterForSodiumShaderReload = -1;

    private GlShader createVertexShader(IrisTerrainPass pass, SodiumTerrainPipeline pipeline) {
        Optional<String> irisVertexShader;
        if (pass == IrisTerrainPass.SHADOW || pass == IrisTerrainPass.SHADOW_CUTOUT) {
            irisVertexShader = pipeline.getShadowVertexShaderSource();
        } else if (pass == IrisTerrainPass.GBUFFER_SOLID) {
            irisVertexShader = pipeline.getTerrainSolidVertexShaderSource();
        } else if (pass == IrisTerrainPass.GBUFFER_CUTOUT) {
            irisVertexShader = pipeline.getTerrainCutoutVertexShaderSource();
        } else if (pass == IrisTerrainPass.GBUFFER_TRANSLUCENT) {
            irisVertexShader = pipeline.getTranslucentVertexShaderSource();
        } else {
            throw new IllegalArgumentException("Unknown pass type " + pass);
        }
        String source = irisVertexShader.orElse(null);
        if (source == null) {
            return null;
        }
        return new GlShader(ShaderType.VERTEX, new ResourceLocation("iris", "sodium-terrain-" + pass.toString().toLowerCase(Locale.ROOT) + ".vsh"), source);
    }

    private GlShader createGeometryShader(IrisTerrainPass pass, SodiumTerrainPipeline pipeline) {
        Optional<String> irisGeometryShader;
        if (pass == IrisTerrainPass.SHADOW || pass == IrisTerrainPass.SHADOW_CUTOUT) {
            irisGeometryShader = pipeline.getShadowGeometryShaderSource();
        } else if (pass == IrisTerrainPass.GBUFFER_SOLID) {
            irisGeometryShader = pipeline.getTerrainSolidGeometryShaderSource();
        } else if (pass == IrisTerrainPass.GBUFFER_CUTOUT) {
            irisGeometryShader = pipeline.getTerrainCutoutGeometryShaderSource();
        } else if (pass == IrisTerrainPass.GBUFFER_TRANSLUCENT) {
            irisGeometryShader = pipeline.getTranslucentGeometryShaderSource();
        } else {
            throw new IllegalArgumentException("Unknown pass type " + pass);
        }
        String source = irisGeometryShader.orElse(null);
        if (source == null) {
            return null;
        }
        return new GlShader(IrisShaderTypes.GEOMETRY, new ResourceLocation("iris", "sodium-terrain-" + pass.toString().toLowerCase(Locale.ROOT) + ".gsh"), source);
    }

    private GlShader createTessControlShader(IrisTerrainPass pass, SodiumTerrainPipeline pipeline) {
        Optional<String> irisTessControlShader;
        if (pass == IrisTerrainPass.SHADOW || pass == IrisTerrainPass.SHADOW_CUTOUT) {
            irisTessControlShader = pipeline.getShadowTessControlShaderSource();
        } else if (pass == IrisTerrainPass.GBUFFER_SOLID) {
            irisTessControlShader = pipeline.getTerrainSolidTessControlShaderSource();
        } else if (pass == IrisTerrainPass.GBUFFER_CUTOUT) {
            irisTessControlShader = pipeline.getTerrainCutoutTessControlShaderSource();
        } else if (pass == IrisTerrainPass.GBUFFER_TRANSLUCENT) {
            irisTessControlShader = pipeline.getTranslucentTessControlShaderSource();
        } else {
            throw new IllegalArgumentException("Unknown pass type " + pass);
        }
        String source = irisTessControlShader.orElse(null);
        if (source == null) {
            return null;
        }
        return new GlShader(IrisShaderTypes.TESS_CONTROL, new ResourceLocation("iris", "sodium-terrain-" + pass.toString().toLowerCase(Locale.ROOT) + ".tcs"), source);
    }

    private GlShader createTessEvalShader(IrisTerrainPass pass, SodiumTerrainPipeline pipeline) {
        Optional<String> irisTessEvalShader;
        if (pass == IrisTerrainPass.SHADOW || pass == IrisTerrainPass.SHADOW_CUTOUT) {
            irisTessEvalShader = pipeline.getShadowTessEvalShaderSource();
        } else if (pass == IrisTerrainPass.GBUFFER_SOLID) {
            irisTessEvalShader = pipeline.getTerrainSolidTessEvalShaderSource();
        } else if (pass == IrisTerrainPass.GBUFFER_CUTOUT) {
            irisTessEvalShader = pipeline.getTerrainCutoutTessEvalShaderSource();
        } else if (pass == IrisTerrainPass.GBUFFER_TRANSLUCENT) {
            irisTessEvalShader = pipeline.getTranslucentTessEvalShaderSource();
        } else {
            throw new IllegalArgumentException("Unknown pass type " + pass);
        }
        String source = irisTessEvalShader.orElse(null);
        if (source == null) {
            return null;
        }
        return new GlShader(IrisShaderTypes.TESS_EVAL, new ResourceLocation("iris", "sodium-terrain-" + pass.toString().toLowerCase(Locale.ROOT) + ".tes"), source);
    }

    private GlShader createFragmentShader(IrisTerrainPass pass, SodiumTerrainPipeline pipeline) {
        Optional<String> irisFragmentShader;
        if (pass == IrisTerrainPass.SHADOW) {
            irisFragmentShader = pipeline.getShadowFragmentShaderSource();
        } else if (pass == IrisTerrainPass.SHADOW_CUTOUT) {
            irisFragmentShader = pipeline.getShadowCutoutFragmentShaderSource();
        } else if (pass == IrisTerrainPass.GBUFFER_SOLID) {
            irisFragmentShader = pipeline.getTerrainSolidFragmentShaderSource();
        } else if (pass == IrisTerrainPass.GBUFFER_CUTOUT) {
            irisFragmentShader = pipeline.getTerrainCutoutFragmentShaderSource();
        } else if (pass == IrisTerrainPass.GBUFFER_TRANSLUCENT) {
            irisFragmentShader = pipeline.getTranslucentFragmentShaderSource();
        } else {
            throw new IllegalArgumentException("Unknown pass type " + pass);
        }
        String source = irisFragmentShader.orElse(null);
        if (source == null) {
            return null;
        }
        return new GlShader(ShaderType.FRAGMENT, new ResourceLocation("iris", "sodium-terrain-" + pass.toString().toLowerCase(Locale.ROOT) + ".fsh"), source);
    }

    private BlendModeOverride getBlendOverride(IrisTerrainPass pass, SodiumTerrainPipeline pipeline) {
        if (pass == IrisTerrainPass.SHADOW || pass == IrisTerrainPass.SHADOW_CUTOUT) {
            return pipeline.getShadowBlendOverride();
        }
        if (pass == IrisTerrainPass.GBUFFER_SOLID) {
            return pipeline.getTerrainSolidBlendOverride();
        }
        if (pass == IrisTerrainPass.GBUFFER_CUTOUT) {
            return pipeline.getTerrainCutoutBlendOverride();
        }
        if (pass == IrisTerrainPass.GBUFFER_TRANSLUCENT) {
            return pipeline.getTranslucentBlendOverride();
        }
        throw new IllegalArgumentException("Unknown pass type " + pass);
    }

    private List<BufferBlendOverride> getBufferBlendOverride(IrisTerrainPass pass, SodiumTerrainPipeline pipeline) {
        if (pass == IrisTerrainPass.SHADOW || pass == IrisTerrainPass.SHADOW_CUTOUT) {
            return pipeline.getShadowBufferOverrides();
        }
        if (pass == IrisTerrainPass.GBUFFER_SOLID) {
            return pipeline.getTerrainSolidBufferOverrides();
        }
        if (pass == IrisTerrainPass.GBUFFER_CUTOUT) {
            return pipeline.getTerrainCutoutBufferOverrides();
        }
        if (pass == IrisTerrainPass.GBUFFER_TRANSLUCENT) {
            return pipeline.getTranslucentBufferOverrides();
        }
        throw new IllegalArgumentException("Unknown pass type " + pass);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    private GlProgram<IrisChunkShaderInterface> createShader(IrisTerrainPass pass, SodiumTerrainPipeline pipeline, ChunkVertexType vertexType) {
        GlShader vertShader = this.createVertexShader(pass, pipeline);
        GlShader geomShader = this.createGeometryShader(pass, pipeline);
        GlShader tessCShader = this.createTessControlShader(pass, pipeline);
        GlShader tessEShader = this.createTessEvalShader(pass, pipeline);
        GlShader fragShader = this.createFragmentShader(pass, pipeline);
        BlendModeOverride blendOverride = this.getBlendOverride(pass, pipeline);
        List<BufferBlendOverride> bufferOverrides = this.getBufferBlendOverride(pass, pipeline);
        float alpha = this.getAlphaReference(pass, pipeline);
        if (vertShader == null || fragShader == null) {
            if (vertShader != null) {
                vertShader.delete();
            }
            if (geomShader != null) {
                geomShader.delete();
            }
            if (tessCShader != null) {
                tessCShader.delete();
            }
            if (tessEShader != null) {
                tessEShader.delete();
            }
            if (fragShader != null) {
                fragShader.delete();
            }
            return null;
        }
        try {
            GlProgram.Builder builder = GlProgram.builder((ResourceLocation)new ResourceLocation("sodium", "chunk_shader_for_" + pass.getName()));
            if (geomShader != null) {
                builder.attachShader(geomShader);
            }
            if (tessCShader != null) {
                builder.attachShader(tessCShader);
            }
            if (tessEShader != null) {
                builder.attachShader(tessEShader);
            }
            GlProgram glProgram = builder.attachShader(vertShader).attachShader(fragShader).bindAttribute("a_PosId", 1).bindAttribute("a_Color", 2).bindAttribute("a_TexCoord", 3).bindAttribute("a_LightCoord", 4).bindAttribute("mc_Entity", 11).bindAttribute("mc_midTexCoord", 12).bindAttribute("at_tangent", 13).bindAttribute("iris_Normal", 10).bindAttribute("at_midBlock", 14).link(shader -> {
                int handle = ((GlObject)shader).handle();
                ShaderBindingContextExt contextExt = (ShaderBindingContextExt)shader;
                GLDebug.nameObject(33506, handle, "sodium-terrain-" + pass.toString().toLowerCase(Locale.ROOT));
                return new IrisChunkShaderInterface(handle, contextExt, pipeline, new ChunkShaderOptions(ChunkFogMode.SMOOTH, pass.toTerrainPass(), vertexType), tessCShader != null || tessEShader != null, pass == IrisTerrainPass.SHADOW || pass == IrisTerrainPass.SHADOW_CUTOUT, blendOverride, bufferOverrides, alpha, pipeline.getCustomUniforms());
            });
            return glProgram;
        }
        finally {
            vertShader.delete();
            if (geomShader != null) {
                geomShader.delete();
            }
            if (tessCShader != null) {
                tessCShader.delete();
            }
            if (tessEShader != null) {
                tessEShader.delete();
            }
            fragShader.delete();
        }
    }

    private float getAlphaReference(IrisTerrainPass pass, SodiumTerrainPipeline pipeline) {
        if (pass == IrisTerrainPass.SHADOW || pass == IrisTerrainPass.SHADOW_CUTOUT) {
            return pipeline.getShadowAlpha().orElse(AlphaTests.ONE_TENTH_ALPHA).reference();
        }
        if (pass == IrisTerrainPass.GBUFFER_SOLID) {
            return AlphaTest.ALWAYS.reference();
        }
        if (pass == IrisTerrainPass.GBUFFER_CUTOUT) {
            return pipeline.getTerrainCutoutAlpha().orElse(AlphaTests.ONE_TENTH_ALPHA).reference();
        }
        if (pass == IrisTerrainPass.GBUFFER_TRANSLUCENT) {
            return pipeline.getTranslucentAlpha().orElse(AlphaTest.ALWAYS).reference();
        }
        throw new IllegalArgumentException("Unknown pass type " + pass);
    }

    private SodiumTerrainPipeline getSodiumTerrainPipeline() {
        WorldRenderingPipeline worldRenderingPipeline = Iris.getPipelineManager().getPipelineNullable();
        if (worldRenderingPipeline != null) {
            return worldRenderingPipeline.getSodiumTerrainPipeline();
        }
        return null;
    }

    public void createShaders(SodiumTerrainPipeline pipeline, ChunkVertexType vertexType) {
        if (pipeline != null) {
            pipeline.patchShaders(vertexType);
            for (IrisTerrainPass pass : IrisTerrainPass.values()) {
                if (pass.isShadow() && !pipeline.hasShadowPass()) {
                    this.programs.put(pass, null);
                    continue;
                }
                this.programs.put(pass, this.createShader(pass, pipeline, vertexType));
            }
        } else {
            for (GlProgram<IrisChunkShaderInterface> program : this.programs.values()) {
                if (program == null) continue;
                program.delete();
            }
            this.programs.clear();
        }
        this.shadersCreated = true;
    }

    @Nullable
    public GlProgram<IrisChunkShaderInterface> getProgramOverride(TerrainRenderPass pass, ChunkVertexType vertexType) {
        if (this.versionCounterForSodiumShaderReload != Iris.getPipelineManager().getVersionCounterForSodiumShaderReload()) {
            this.versionCounterForSodiumShaderReload = Iris.getPipelineManager().getVersionCounterForSodiumShaderReload();
            this.deleteShaders();
        }
        WorldRenderingPipeline worldRenderingPipeline = Iris.getPipelineManager().getPipelineNullable();
        SodiumTerrainPipeline sodiumTerrainPipeline = null;
        if (worldRenderingPipeline != null) {
            sodiumTerrainPipeline = worldRenderingPipeline.getSodiumTerrainPipeline();
        }
        if (!this.shadersCreated) {
            this.createShaders(sodiumTerrainPipeline, vertexType);
        }
        if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
            if (sodiumTerrainPipeline != null && !sodiumTerrainPipeline.hasShadowPass()) {
                throw new IllegalStateException("Shadow program requested, but the pack does not have a shadow pass?");
            }
            if (pass.supportsFragmentDiscard()) {
                return this.programs.get((Object)IrisTerrainPass.SHADOW_CUTOUT);
            }
            return this.programs.get((Object)IrisTerrainPass.SHADOW);
        }
        if (pass.supportsFragmentDiscard()) {
            return this.programs.get((Object)IrisTerrainPass.GBUFFER_CUTOUT);
        }
        if (pass.isReverseOrder()) {
            return this.programs.get((Object)IrisTerrainPass.GBUFFER_TRANSLUCENT);
        }
        return this.programs.get((Object)IrisTerrainPass.GBUFFER_SOLID);
    }

    public void bindFramebuffer(TerrainRenderPass pass) {
        GlFramebuffer framebuffer;
        SodiumTerrainPipeline pipeline = this.getSodiumTerrainPipeline();
        boolean isShadowPass = ShadowRenderingState.areShadowsCurrentlyBeingRendered();
        if (pipeline != null && (framebuffer = isShadowPass ? pipeline.getShadowFramebuffer() : (pass.isReverseOrder() ? pipeline.getTranslucentFramebuffer() : pipeline.getTerrainSolidFramebuffer())) != null) {
            framebuffer.bind();
        }
    }

    public void unbindFramebuffer() {
        SodiumTerrainPipeline pipeline = this.getSodiumTerrainPipeline();
        if (pipeline != null) {
            Minecraft.m_91087_().m_91385_().m_83947_(false);
        }
    }

    public void deleteShaders() {
        for (GlProgram<IrisChunkShaderInterface> program : this.programs.values()) {
            if (program == null) continue;
            program.delete();
        }
        this.programs.clear();
        this.shadersCreated = false;
    }
}

