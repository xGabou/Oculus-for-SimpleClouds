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
package net.irisshaders.iris.pipeline;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import net.irisshaders.iris.features.FeatureFlags;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.blending.BlendModeOverride;
import net.irisshaders.iris.gl.buffer.ShaderStorageBufferHolder;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.irisshaders.iris.gl.framebuffer.ViewportData;
import net.irisshaders.iris.gl.image.GlImage;
import net.irisshaders.iris.gl.program.ComputeProgram;
import net.irisshaders.iris.gl.program.Program;
import net.irisshaders.iris.gl.program.ProgramBuilder;
import net.irisshaders.iris.gl.program.ProgramSamplers;
import net.irisshaders.iris.gl.program.ProgramUniforms;
import net.irisshaders.iris.gl.sampler.SamplerLimits;
import net.irisshaders.iris.gl.shader.ShaderCompileException;
import net.irisshaders.iris.gl.state.FogMode;
import net.irisshaders.iris.gl.texture.TextureAccess;
import net.irisshaders.iris.mixin.GlStateManagerAccessor;
import net.irisshaders.iris.pathways.CenterDepthSampler;
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
import net.irisshaders.iris.targets.BufferFlipper;
import net.irisshaders.iris.targets.RenderTargets;
import net.irisshaders.iris.uniforms.CommonUniforms;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import net.irisshaders.iris.uniforms.custom.CustomUniforms;
import net.minecraft.client.Minecraft;

public class CompositeRenderer {
    private final RenderTargets renderTargets;
    private final ImmutableList<Pass> passes;
    private final TextureAccess noiseTexture;
    private final FrameUpdateNotifier updateNotifier;
    private final CenterDepthSampler centerDepthSampler;
    private final Object2ObjectMap<String, TextureAccess> customTextureIds;
    private final ImmutableSet<Integer> flippedAtLeastOnceFinal;
    private final CustomUniforms customUniforms;
    private final Object2ObjectMap<String, TextureAccess> irisCustomTextures;
    private final Set<GlImage> customImages;
    private final TextureStage textureStage;
    private final WorldRenderingPipeline pipeline;

    public CompositeRenderer(WorldRenderingPipeline pipeline, PackDirectives packDirectives, ProgramSource[] sources, ComputeSource[][] computes, RenderTargets renderTargets, ShaderStorageBufferHolder holder, TextureAccess noiseTexture, FrameUpdateNotifier updateNotifier, CenterDepthSampler centerDepthSampler, BufferFlipper bufferFlipper, Supplier<ShadowRenderTargets> shadowTargetsSupplier, TextureStage textureStage, Object2ObjectMap<String, TextureAccess> customTextureIds, Object2ObjectMap<String, TextureAccess> irisCustomTextures, Set<GlImage> customImages, ImmutableMap<Integer, Boolean> explicitPreFlips, CustomUniforms customUniforms) {
        this.pipeline = pipeline;
        this.noiseTexture = noiseTexture;
        this.updateNotifier = updateNotifier;
        this.centerDepthSampler = centerDepthSampler;
        this.renderTargets = renderTargets;
        this.customTextureIds = customTextureIds;
        this.customUniforms = customUniforms;
        this.irisCustomTextures = irisCustomTextures;
        this.customImages = customImages;
        this.textureStage = textureStage;
        PackRenderTargetDirectives renderTargetDirectives = packDirectives.getRenderTargetDirectives();
        Map<Integer, PackRenderTargetDirectives.RenderTargetSettings> renderTargetSettings = renderTargetDirectives.getRenderTargetSettings();
        ImmutableList.Builder passes = ImmutableList.builder();
        ImmutableSet.Builder flippedAtLeastOnce = new ImmutableSet.Builder();
        explicitPreFlips.forEach((buffer, shouldFlip) -> {
            if (shouldFlip.booleanValue()) {
                bufferFlipper.flip((int)buffer);
            }
        });
        for (int i = 0; i < sources.length; ++i) {
            Pass pass;
            ProgramSource source = sources[i];
            ImmutableSet<Integer> flipped = bufferFlipper.snapshot();
            ImmutableSet flippedAtLeastOnceSnapshot = flippedAtLeastOnce.build();
            if (source == null || !source.isValid()) {
                if (computes[i] == null) continue;
                pass = new ComputeOnlyPass();
                ((ComputeOnlyPass)pass).computes = this.createComputes(computes[i], flipped, (ImmutableSet<Integer>)flippedAtLeastOnceSnapshot, shadowTargetsSupplier, holder);
                passes.add((Object)pass);
                continue;
            }
            pass = new Pass();
            ProgramDirectives directives = source.getDirectives();
            pass.program = this.createProgram(source, flipped, (ImmutableSet<Integer>)flippedAtLeastOnceSnapshot, shadowTargetsSupplier);
            pass.blendModeOverride = source.getDirectives().getBlendModeOverride().orElse(null);
            pass.computes = this.createComputes(computes[i], flipped, (ImmutableSet<Integer>)flippedAtLeastOnceSnapshot, shadowTargetsSupplier, holder);
            int[] drawBuffers = directives.getDrawBuffers();
            int passWidth = 0;
            int passHeight = 0;
            ImmutableMap<Integer, Boolean> explicitFlips = directives.getExplicitFlips();
            GlFramebuffer framebuffer = renderTargets.createColorFramebuffer(flipped, drawBuffers);
            for (int buffer2 : drawBuffers) {
                net.irisshaders.iris.targets.RenderTarget target = renderTargets.get(buffer2);
                if (passWidth > 0 && passWidth != target.getWidth() || passHeight > 0 && passHeight != target.getHeight()) {
                    throw new IllegalStateException("Pass sizes must match for drawbuffers " + Arrays.toString(drawBuffers) + "\nOriginal width: " + passWidth + " New width: " + target.getWidth() + " Original height: " + passHeight + " New height: " + target.getHeight());
                }
                passWidth = target.getWidth();
                passHeight = target.getHeight();
                if (explicitFlips.get((Object)buffer2) == Boolean.FALSE) continue;
                bufferFlipper.flip(buffer2);
                flippedAtLeastOnce.add((Object)buffer2);
            }
            explicitFlips.forEach((buffer, shouldFlip) -> {
                if (shouldFlip.booleanValue()) {
                    bufferFlipper.flip((int)buffer);
                    flippedAtLeastOnce.add(buffer);
                }
            });
            pass.drawBuffers = directives.getDrawBuffers();
            pass.viewWidth = passWidth;
            pass.viewHeight = passHeight;
            pass.stageReadsFromAlt = flipped;
            pass.framebuffer = framebuffer;
            pass.viewportScale = directives.getViewportScale();
            pass.mipmappedBuffers = directives.getMipmappedBuffers();
            pass.flippedAtLeastOnce = flippedAtLeastOnceSnapshot;
            passes.add((Object)pass);
        }
        this.passes = passes.build();
        this.flippedAtLeastOnceFinal = flippedAtLeastOnce.build();
        GlStateManager._glBindFramebuffer((int)36008, (int)0);
    }

    private static void setupMipmapping(net.irisshaders.iris.targets.RenderTarget target, boolean readFromAlt) {
        if (target == null) {
            return;
        }
        int texture = readFromAlt ? target.getAltTexture() : target.getMainTexture();
        IrisRenderSystem.generateMipmaps(texture, 3553);
        int filter = 9987;
        if (target.getInternalFormat().getPixelFormat().isInteger()) {
            filter = 9984;
        }
        IrisRenderSystem.texParameteri(texture, 3553, 10241, filter);
    }

    public ImmutableSet<Integer> getFlippedAtLeastOnceFinal() {
        return this.flippedAtLeastOnceFinal;
    }

    public void recalculateSizes() {
        for (Pass pass : this.passes) {
            if (pass instanceof ComputeOnlyPass) continue;
            int passWidth = 0;
            int passHeight = 0;
            for (int buffer : pass.drawBuffers) {
                net.irisshaders.iris.targets.RenderTarget target = this.renderTargets.get(buffer);
                if (passWidth > 0 && passWidth != target.getWidth() || passHeight > 0 && passHeight != target.getHeight()) {
                    throw new IllegalStateException("Pass widths must match");
                }
                passWidth = target.getWidth();
                passHeight = target.getHeight();
            }
            this.renderTargets.destroyFramebuffer(pass.framebuffer);
            pass.framebuffer = this.renderTargets.createColorFramebuffer(pass.stageReadsFromAlt, pass.drawBuffers);
            pass.viewWidth = passWidth;
            pass.viewHeight = passHeight;
        }
    }

    public void renderAll() {
        RenderSystem.disableBlend();
        FullScreenQuadRenderer.INSTANCE.begin();
        RenderTarget main = Minecraft.m_91087_().m_91385_();
        for (Pass renderPass : this.passes) {
            boolean ranCompute = false;
            for (ComputeProgram computeProgram : renderPass.computes) {
                if (computeProgram == null) continue;
                ranCompute = true;
                computeProgram.use();
                this.customUniforms.push(computeProgram);
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
                    CompositeRenderer.setupMipmapping(this.renderTargets.get(index), renderPass.stageReadsFromAlt.contains((Object)index));
                }
            }
            float scaledWidth = (float)renderPass.viewWidth * renderPass.viewportScale.scale();
            float scaledHeight = (float)renderPass.viewHeight * renderPass.viewportScale.scale();
            int beginWidth = (int)((float)renderPass.viewWidth * renderPass.viewportScale.viewportX());
            int beginHeight = (int)((float)renderPass.viewHeight * renderPass.viewportScale.viewportY());
            RenderSystem.viewport((int)beginWidth, (int)beginHeight, (int)((int)scaledWidth), (int)((int)scaledHeight));
            renderPass.framebuffer.bind();
            renderPass.program.use();
            if (renderPass.blendModeOverride != null) {
                renderPass.blendModeOverride.apply();
            } else {
                RenderSystem.disableBlend();
            }
            this.customUniforms.push(renderPass.program);
            FullScreenQuadRenderer.INSTANCE.renderQuad();
            BlendModeOverride.restore();
        }
        FullScreenQuadRenderer.INSTANCE.end();
        Minecraft.m_91087_().m_91385_().m_83947_(true);
        ProgramUniforms.clearActiveUniforms();
        ProgramSamplers.clearActiveSamplers();
        GlStateManager._glUseProgram((int)0);
        for (int i = 0; i < SamplerLimits.get().getMaxTextureUnits(); ++i) {
            if (GlStateManagerAccessor.getTEXTURES()[i].f_84801_ == 0) continue;
            RenderSystem.activeTexture((int)(33984 + i));
            RenderSystem.bindTexture((int)0);
        }
        RenderSystem.activeTexture((int)33984);
    }

    private Program createProgram(ProgramSource source, ImmutableSet<Integer> flipped, ImmutableSet<Integer> flippedAtLeastOnceSnapshot, Supplier<ShadowRenderTargets> shadowTargetsSupplier) {
        ProgramBuilder builder;
        Map<PatchShaderType, String> transformed = TransformPatcher.patchComposite(source.getName(), source.getVertexSource().orElseThrow(NullPointerException::new), source.getGeometrySource().orElse(null), source.getFragmentSource().orElseThrow(NullPointerException::new), this.textureStage, this.pipeline.getTextureMap());
        String vertex = transformed.get((Object)PatchShaderType.VERTEX);
        String geometry = transformed.get((Object)PatchShaderType.GEOMETRY);
        String fragment = transformed.get((Object)PatchShaderType.FRAGMENT);
        ShaderPrinter.printProgram(source.getName()).addSources(transformed).print();
        Objects.requireNonNull(flipped);
        try {
            builder = ProgramBuilder.begin(source.getName(), vertex, geometry, fragment, IrisSamplers.COMPOSITE_RESERVED_TEXTURE_UNITS);
        }
        catch (ShaderCompileException e) {
            throw e;
        }
        catch (RuntimeException e) {
            throw new RuntimeException("Shader compilation failed for " + source.getName() + "!", e);
        }
        CommonUniforms.addDynamicUniforms(builder, FogMode.OFF);
        this.customUniforms.assignTo(builder);
        ProgramSamplers.CustomTextureSamplerInterceptor customTextureSamplerInterceptor = ProgramSamplers.customTextureSamplerInterceptor(builder, this.customTextureIds, flippedAtLeastOnceSnapshot);
        IrisSamplers.addRenderTargetSamplers(customTextureSamplerInterceptor, () -> flipped, this.renderTargets, true, this.pipeline);
        IrisSamplers.addCustomTextures(builder, this.irisCustomTextures);
        IrisSamplers.addCustomImages(customTextureSamplerInterceptor, this.customImages);
        IrisImages.addRenderTargetImages(builder, () -> flipped, this.renderTargets);
        IrisImages.addCustomImages(builder, this.customImages);
        IrisSamplers.addNoiseSampler(customTextureSamplerInterceptor, this.noiseTexture);
        IrisSamplers.addCompositeSamplers(customTextureSamplerInterceptor, this.renderTargets);
        if (IrisSamplers.hasShadowSamplers(customTextureSamplerInterceptor)) {
            IrisSamplers.addShadowSamplers(customTextureSamplerInterceptor, shadowTargetsSupplier.get(), null, this.pipeline.hasFeature(FeatureFlags.SEPARATE_HARDWARE_SAMPLERS));
            IrisImages.addShadowColorImages(builder, shadowTargetsSupplier.get(), null);
        }
        this.centerDepthSampler.setUsage(builder.addDynamicSampler(this.centerDepthSampler::getCenterDepthTexture, "iris_centerDepthSmooth"));
        Program build = builder.build();
        this.customUniforms.mapholderToPass(builder, build);
        return build;
    }

    private ComputeProgram[] createComputes(ComputeSource[] compute, ImmutableSet<Integer> flipped, ImmutableSet<Integer> flippedAtLeastOnceSnapshot, Supplier<ShadowRenderTargets> shadowTargetsSupplier, ShaderStorageBufferHolder holder) {
        ComputeProgram[] programs = new ComputeProgram[compute.length];
        for (int i = 0; i < programs.length; ++i) {
            ProgramBuilder builder;
            ComputeSource source = compute[i];
            if (source == null || !source.getSource().isPresent()) continue;
            Objects.requireNonNull(flipped);
            try {
                String transformed = TransformPatcher.patchCompute(source.getName(), source.getSource().orElse(null), this.textureStage, this.pipeline.getTextureMap());
                ShaderPrinter.printProgram(source.getName()).addSource(PatchShaderType.COMPUTE, transformed).print();
                builder = ProgramBuilder.beginCompute(source.getName(), transformed, IrisSamplers.COMPOSITE_RESERVED_TEXTURE_UNITS);
            }
            catch (ShaderCompileException e) {
                throw e;
            }
            catch (RuntimeException e) {
                throw new RuntimeException("Shader compilation failed for compute " + source.getName() + "!", e);
            }
            ProgramSamplers.CustomTextureSamplerInterceptor customTextureSamplerInterceptor = ProgramSamplers.customTextureSamplerInterceptor(builder, this.customTextureIds, flippedAtLeastOnceSnapshot);
            CommonUniforms.addDynamicUniforms(builder, FogMode.OFF);
            this.customUniforms.assignTo(builder);
            IrisSamplers.addRenderTargetSamplers(customTextureSamplerInterceptor, () -> flipped, this.renderTargets, true, this.pipeline);
            IrisSamplers.addCustomTextures(builder, this.irisCustomTextures);
            IrisSamplers.addCustomImages(customTextureSamplerInterceptor, this.customImages);
            IrisImages.addRenderTargetImages(builder, () -> flipped, this.renderTargets);
            IrisImages.addCustomImages(builder, this.customImages);
            IrisSamplers.addNoiseSampler(customTextureSamplerInterceptor, this.noiseTexture);
            IrisSamplers.addCompositeSamplers(customTextureSamplerInterceptor, this.renderTargets);
            if (IrisSamplers.hasShadowSamplers(customTextureSamplerInterceptor)) {
                IrisSamplers.addShadowSamplers(customTextureSamplerInterceptor, shadowTargetsSupplier.get(), null, this.pipeline.hasFeature(FeatureFlags.SEPARATE_HARDWARE_SAMPLERS));
                IrisImages.addShadowColorImages(builder, shadowTargetsSupplier.get(), null);
            }
            this.centerDepthSampler.setUsage(builder.addDynamicSampler(this.centerDepthSampler::getCenterDepthTexture, "iris_centerDepthSmooth"));
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
        int[] drawBuffers;
        int viewWidth;
        int viewHeight;
        Program program;
        BlendModeOverride blendModeOverride;
        ComputeProgram[] computes;
        GlFramebuffer framebuffer;
        ImmutableSet<Integer> flippedAtLeastOnce;
        ImmutableSet<Integer> stageReadsFromAlt;
        ImmutableSet<Integer> mipmappedBuffers;
        ViewportData viewportScale;

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

