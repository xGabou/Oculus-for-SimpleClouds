/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.UnmodifiableIterator
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  net.minecraft.client.Minecraft
 */
package net.irisshaders.iris.shadows;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.irisshaders.iris.features.FeatureFlags;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.buffer.ShaderStorageBufferHolder;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.irisshaders.iris.gl.framebuffer.ViewportData;
import net.irisshaders.iris.gl.image.GlImage;
import net.irisshaders.iris.gl.program.ComputeProgram;
import net.irisshaders.iris.gl.program.Program;
import net.irisshaders.iris.gl.program.ProgramBuilder;
import net.irisshaders.iris.gl.program.ProgramSamplers;
import net.irisshaders.iris.gl.program.ProgramUniforms;
import net.irisshaders.iris.gl.state.FogMode;
import net.irisshaders.iris.gl.texture.TextureAccess;
import net.irisshaders.iris.pathways.FullScreenQuadRenderer;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.pipeline.transform.PatchShaderType;
import net.irisshaders.iris.pipeline.transform.ShaderPrinter;
import net.irisshaders.iris.pipeline.transform.TransformPatcher;
import net.irisshaders.iris.samplers.IrisImages;
import net.irisshaders.iris.samplers.IrisSamplers;
import net.irisshaders.iris.shaderpack.FilledIndirectPointer;
import net.irisshaders.iris.shaderpack.programs.ComputeSource;
import net.irisshaders.iris.shaderpack.programs.ProgramSource;
import net.irisshaders.iris.shaderpack.properties.PackDirectives;
import net.irisshaders.iris.shaderpack.properties.PackRenderTargetDirectives;
import net.irisshaders.iris.shaderpack.properties.ProgramDirectives;
import net.irisshaders.iris.shaderpack.texture.TextureStage;
import net.irisshaders.iris.shadows.ShadowRenderTargets;
import net.irisshaders.iris.uniforms.CommonUniforms;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import net.irisshaders.iris.uniforms.custom.CustomUniforms;
import net.minecraft.client.Minecraft;

public class ShadowCompositeRenderer {
    private final ShadowRenderTargets renderTargets;
    private final ImmutableList<Pass> passes;
    private final TextureAccess noiseTexture;
    private final FrameUpdateNotifier updateNotifier;
    private final Object2ObjectMap<String, TextureAccess> customTextureIds;
    private final ImmutableSet<Integer> flippedAtLeastOnceFinal;
    private final CustomUniforms customUniforms;
    private final Object2ObjectMap<String, TextureAccess> irisCustomTextures;
    private final WorldRenderingPipeline pipeline;
    private final Set<GlImage> irisCustomImages;

    public ShadowCompositeRenderer(WorldRenderingPipeline pipeline, PackDirectives packDirectives, ProgramSource[] sources, ComputeSource[][] computes, ShadowRenderTargets renderTargets, ShaderStorageBufferHolder holder, TextureAccess noiseTexture, FrameUpdateNotifier updateNotifier, Object2ObjectMap<String, TextureAccess> customTextureIds, Set<GlImage> customImages, ImmutableMap<Integer, Boolean> explicitPreFlips, Object2ObjectMap<String, TextureAccess> irisCustomTextures, CustomUniforms customUniforms) {
        this.pipeline = pipeline;
        this.noiseTexture = noiseTexture;
        this.updateNotifier = updateNotifier;
        this.renderTargets = renderTargets;
        this.customTextureIds = customTextureIds;
        this.irisCustomTextures = irisCustomTextures;
        this.irisCustomImages = customImages;
        this.customUniforms = customUniforms;
        PackRenderTargetDirectives renderTargetDirectives = packDirectives.getRenderTargetDirectives();
        Map<Integer, PackRenderTargetDirectives.RenderTargetSettings> renderTargetSettings = renderTargetDirectives.getRenderTargetSettings();
        ImmutableList.Builder passes = ImmutableList.builder();
        ImmutableSet.Builder flippedAtLeastOnce = new ImmutableSet.Builder();
        explicitPreFlips.forEach((buffer, shouldFlip) -> {
            if (shouldFlip.booleanValue()) {
                renderTargets.flip((int)buffer);
            }
        });
        int sourcesLength = sources.length;
        for (int i = 0; i < sourcesLength; ++i) {
            int[] nArray;
            Pass pass;
            ProgramSource source = sources[i];
            ImmutableSet<Integer> flipped = renderTargets.snapshot();
            ImmutableSet flippedAtLeastOnceSnapshot = flippedAtLeastOnce.build();
            if (source == null || !source.isValid()) {
                if (computes[i] == null) continue;
                pass = new ComputeOnlyPass();
                ((ComputeOnlyPass)pass).computes = this.createComputes(computes[i], flipped, (ImmutableSet<Integer>)flippedAtLeastOnceSnapshot, renderTargets, holder);
                passes.add((Object)pass);
                continue;
            }
            pass = new Pass();
            ProgramDirectives directives = source.getDirectives();
            pass.program = this.createProgram(source, flipped, (ImmutableSet<Integer>)flippedAtLeastOnceSnapshot, renderTargets);
            pass.computes = this.createComputes(computes[i], flipped, (ImmutableSet<Integer>)flippedAtLeastOnceSnapshot, renderTargets, holder);
            if (source.getDirectives().hasUnknownDrawBuffers()) {
                int[] nArray2 = new int[2];
                nArray2[0] = 0;
                nArray = nArray2;
                nArray2[1] = 1;
            } else {
                nArray = source.getDirectives().getDrawBuffers();
            }
            int[] drawBuffers = nArray;
            GlFramebuffer framebuffer = renderTargets.createColorFramebuffer(flipped, drawBuffers);
            pass.stageReadsFromAlt = flipped;
            pass.framebuffer = framebuffer;
            pass.viewportScale = directives.getViewportScale();
            pass.mipmappedBuffers = directives.getMipmappedBuffers();
            pass.flippedAtLeastOnce = flippedAtLeastOnceSnapshot;
            passes.add((Object)pass);
            ImmutableMap<Integer, Boolean> explicitFlips = directives.getExplicitFlips();
            for (int buffer2 : drawBuffers) {
                if (explicitFlips.get((Object)buffer2) == Boolean.FALSE) continue;
                renderTargets.flip(buffer2);
                flippedAtLeastOnce.add((Object)buffer2);
            }
            explicitFlips.forEach((buffer, shouldFlip) -> {
                if (shouldFlip.booleanValue()) {
                    renderTargets.flip((int)buffer);
                    flippedAtLeastOnce.add(buffer);
                }
            });
        }
        this.passes = passes.build();
        this.flippedAtLeastOnceFinal = flippedAtLeastOnce.build();
        GlStateManager._glBindFramebuffer((int)36008, (int)0);
    }

    private static void setupMipmapping(net.irisshaders.iris.targets.RenderTarget target, boolean readFromAlt) {
        int texture = readFromAlt ? target.getAltTexture() : target.getMainTexture();
        IrisRenderSystem.generateMipmaps(texture, 3553);
        IrisRenderSystem.texParameteri(texture, 3553, 10241, target.getInternalFormat().getPixelFormat().isInteger() ? 9984 : 9987);
    }

    private static void resetRenderTarget(net.irisshaders.iris.targets.RenderTarget target) {
        int filter = 9729;
        if (target.getInternalFormat().getPixelFormat().isInteger()) {
            filter = 9728;
        }
        IrisRenderSystem.texParameteri(target.getMainTexture(), 3553, 10241, filter);
        IrisRenderSystem.texParameteri(target.getAltTexture(), 3553, 10241, filter);
    }

    public ImmutableSet<Integer> getFlippedAtLeastOnceFinal() {
        return this.flippedAtLeastOnceFinal;
    }

    public void renderAll() {
        RenderSystem.disableBlend();
        FullScreenQuadRenderer.INSTANCE.begin();
        for (Pass renderPass : this.passes) {
            boolean ranCompute = false;
            for (ComputeProgram computeProgram : renderPass.computes) {
                if (computeProgram == null) continue;
                ranCompute = true;
                computeProgram.use();
                this.customUniforms.push(computeProgram);
                RenderTarget main = Minecraft.m_91087_().m_91385_();
                computeProgram.dispatch(main.f_83915_, main.f_83916_);
            }
            if (ranCompute) {
                IrisRenderSystem.memoryBarrier(8232);
            }
            Program.unbind();
            if (renderPass instanceof ComputeOnlyPass) continue;
            if (!renderPass.mipmappedBuffers.isEmpty()) {
                RenderSystem.activeTexture((int)33984);
                UnmodifiableIterator unmodifiableIterator = renderPass.mipmappedBuffers.iterator();
                while (unmodifiableIterator.hasNext()) {
                    int index = (Integer)unmodifiableIterator.next();
                    ShadowCompositeRenderer.setupMipmapping(this.renderTargets.get(index), renderPass.stageReadsFromAlt.contains((Object)index));
                }
            }
            float scaledWidth = (float)this.renderTargets.getResolution() * renderPass.viewportScale.scale();
            float scaledHeight = (float)this.renderTargets.getResolution() * renderPass.viewportScale.scale();
            int beginWidth = (int)((float)this.renderTargets.getResolution() * renderPass.viewportScale.viewportX());
            int beginHeight = (int)((float)this.renderTargets.getResolution() * renderPass.viewportScale.viewportY());
            RenderSystem.viewport((int)beginWidth, (int)beginHeight, (int)((int)scaledWidth), (int)((int)scaledHeight));
            renderPass.framebuffer.bind();
            renderPass.program.use();
            this.customUniforms.push(renderPass.program);
            FullScreenQuadRenderer.INSTANCE.renderQuad();
        }
        FullScreenQuadRenderer.INSTANCE.end();
        ProgramUniforms.clearActiveUniforms();
        GlStateManager._glUseProgram((int)0);
        for (int i = 0; i < this.renderTargets.getRenderTargetCount(); ++i) {
            if (this.renderTargets.get(i) == null) continue;
            ShadowCompositeRenderer.resetRenderTarget(this.renderTargets.get(i));
        }
        RenderSystem.activeTexture((int)33984);
    }

    private Program createProgram(ProgramSource source, ImmutableSet<Integer> flipped, ImmutableSet<Integer> flippedAtLeastOnceSnapshot, ShadowRenderTargets targets) {
        ProgramBuilder builder;
        Map<PatchShaderType, String> transformed = TransformPatcher.patchComposite(source.getName(), source.getVertexSource().orElseThrow(NullPointerException::new), source.getGeometrySource().orElse(null), source.getFragmentSource().orElseThrow(NullPointerException::new), TextureStage.SHADOWCOMP, this.pipeline.getTextureMap());
        String vertex = transformed.get((Object)PatchShaderType.VERTEX);
        String geometry = transformed.get((Object)PatchShaderType.GEOMETRY);
        String fragment = transformed.get((Object)PatchShaderType.FRAGMENT);
        ShaderPrinter.printProgram(source.getName()).addSources(transformed).print();
        Objects.requireNonNull(flipped);
        try {
            builder = ProgramBuilder.begin(source.getName(), vertex, geometry, fragment, IrisSamplers.COMPOSITE_RESERVED_TEXTURE_UNITS);
        }
        catch (RuntimeException e) {
            throw new RuntimeException("Shader compilation failed for shadow composite " + source.getName() + "!", e);
        }
        ProgramSamplers.CustomTextureSamplerInterceptor customTextureSamplerInterceptor = ProgramSamplers.customTextureSamplerInterceptor(builder, this.customTextureIds, flippedAtLeastOnceSnapshot);
        CommonUniforms.addDynamicUniforms(builder, FogMode.OFF);
        this.customUniforms.assignTo(builder);
        IrisSamplers.addNoiseSampler(customTextureSamplerInterceptor, this.noiseTexture);
        IrisSamplers.addCustomTextures(customTextureSamplerInterceptor, this.irisCustomTextures);
        IrisSamplers.addShadowSamplers(customTextureSamplerInterceptor, targets, flipped, this.pipeline.hasFeature(FeatureFlags.SEPARATE_HARDWARE_SAMPLERS));
        IrisImages.addShadowColorImages(builder, targets, flipped);
        IrisImages.addCustomImages(builder, this.irisCustomImages);
        IrisSamplers.addCustomImages(builder, this.irisCustomImages);
        Program build = builder.build();
        this.customUniforms.mapholderToPass(builder, build);
        return build;
    }

    private ComputeProgram[] createComputes(ComputeSource[] sources, ImmutableSet<Integer> flipped, ImmutableSet<Integer> flippedAtLeastOnceSnapshot, ShadowRenderTargets targets, ShaderStorageBufferHolder holder) {
        ComputeProgram[] programs = new ComputeProgram[sources.length];
        for (int i = 0; i < programs.length; ++i) {
            ProgramBuilder builder;
            ComputeSource source = sources[i];
            if (source == null || !source.getSource().isPresent()) continue;
            Objects.requireNonNull(flipped);
            try {
                String transformed = TransformPatcher.patchCompute(source.getName(), source.getSource().orElse(null), TextureStage.SHADOWCOMP, this.pipeline.getTextureMap());
                ShaderPrinter.printProgram(source.getName()).addSource(PatchShaderType.COMPUTE, transformed).print();
                builder = ProgramBuilder.beginCompute(source.getName(), transformed, IrisSamplers.COMPOSITE_RESERVED_TEXTURE_UNITS);
            }
            catch (RuntimeException e) {
                throw new RuntimeException("Shader compilation failed for shadowcomp compute " + source.getName() + "!", e);
            }
            ProgramSamplers.CustomTextureSamplerInterceptor customTextureSamplerInterceptor = ProgramSamplers.customTextureSamplerInterceptor(builder, this.customTextureIds, flippedAtLeastOnceSnapshot);
            CommonUniforms.addDynamicUniforms(builder, FogMode.OFF);
            this.customUniforms.assignTo(builder);
            IrisSamplers.addNoiseSampler(customTextureSamplerInterceptor, this.noiseTexture);
            IrisSamplers.addCustomTextures(customTextureSamplerInterceptor, this.irisCustomTextures);
            IrisSamplers.addShadowSamplers(customTextureSamplerInterceptor, targets, flipped, this.pipeline.hasFeature(FeatureFlags.SEPARATE_HARDWARE_SAMPLERS));
            IrisImages.addShadowColorImages(builder, targets, flipped);
            IrisImages.addCustomImages(builder, this.irisCustomImages);
            IrisSamplers.addCustomImages(builder, this.irisCustomImages);
            programs[i] = builder.buildCompute();
            this.customUniforms.mapholderToPass(builder, programs[i]);
            programs[i].setWorkGroupInfo(source.getWorkGroupRelative(), source.getWorkGroups(), FilledIndirectPointer.basedOff(holder, source.getIndirectPointer()));
        }
        return programs;
    }

    public void destroy() {
        for (Pass renderPass : this.passes) {
            renderPass.destroy();
        }
    }

    private static class ComputeOnlyPass
    extends Pass {
        private ComputeOnlyPass() {
        }

        @Override
        protected void destroy() {
            for (ComputeProgram compute : this.computes) {
                if (compute == null) continue;
                compute.destroy();
            }
        }
    }

    private static class Pass {
        Program program;
        GlFramebuffer framebuffer;
        ImmutableSet<Integer> flippedAtLeastOnce;
        ImmutableSet<Integer> stageReadsFromAlt;
        ImmutableSet<Integer> mipmappedBuffers;
        ViewportData viewportScale;
        ComputeProgram[] computes;

        private Pass() {
        }

        protected void destroy() {
            this.program.destroy();
            for (ComputeProgram compute : this.computes) {
                if (compute == null) continue;
                compute.destroy();
            }
        }
    }
}

