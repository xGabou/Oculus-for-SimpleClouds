/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.UnmodifiableIterator
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  net.minecraft.client.Minecraft
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.pipeline;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import net.irisshaders.iris.features.FeatureFlags;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.buffer.ShaderStorageBufferHolder;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
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
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import net.irisshaders.iris.shaderpack.programs.ProgramSource;
import net.irisshaders.iris.shaderpack.properties.PackRenderTargetDirectives;
import net.irisshaders.iris.shaderpack.properties.ProgramDirectives;
import net.irisshaders.iris.shaderpack.texture.TextureStage;
import net.irisshaders.iris.shadows.ShadowRenderTargets;
import net.irisshaders.iris.targets.Blaze3dRenderTargetExt;
import net.irisshaders.iris.targets.RenderTargets;
import net.irisshaders.iris.uniforms.CommonUniforms;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import net.irisshaders.iris.uniforms.custom.CustomUniforms;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

public class FinalPassRenderer {
    private final RenderTargets renderTargets;
    @Nullable
    private final Pass finalPass;
    private final ImmutableList<SwapPass> swapPasses;
    private final GlFramebuffer baseline;
    private final GlFramebuffer colorHolder;
    private final Object2ObjectMap<String, TextureAccess> irisCustomTextures;
    private final Set<GlImage> customImages;
    private final TextureAccess noiseTexture;
    private final FrameUpdateNotifier updateNotifier;
    private final CenterDepthSampler centerDepthSampler;
    private final Object2ObjectMap<String, TextureAccess> customTextureIds;
    private final CustomUniforms customUniforms;
    private final WorldRenderingPipeline pipeline;
    private int lastColorTextureId;
    private int lastColorTextureVersion;

    public FinalPassRenderer(WorldRenderingPipeline pipeline, ProgramSet pack, RenderTargets renderTargets, TextureAccess noiseTexture, ShaderStorageBufferHolder holder, FrameUpdateNotifier updateNotifier, ImmutableSet<Integer> flippedBuffers, CenterDepthSampler centerDepthSampler, Supplier<ShadowRenderTargets> shadowTargetsSupplier, Object2ObjectMap<String, TextureAccess> customTextureIds, Object2ObjectMap<String, TextureAccess> irisCustomTextures, Set<GlImage> customImages, ImmutableSet<Integer> flippedAtLeastOnce, CustomUniforms customUniforms) {
        this.pipeline = pipeline;
        this.updateNotifier = updateNotifier;
        this.centerDepthSampler = centerDepthSampler;
        this.customTextureIds = customTextureIds;
        this.irisCustomTextures = irisCustomTextures;
        this.customImages = customImages;
        PackRenderTargetDirectives renderTargetDirectives = pack.getPackDirectives().getRenderTargetDirectives();
        Map<Integer, PackRenderTargetDirectives.RenderTargetSettings> renderTargetSettings = renderTargetDirectives.getRenderTargetSettings();
        this.noiseTexture = noiseTexture;
        this.renderTargets = renderTargets;
        this.customUniforms = customUniforms;
        this.finalPass = pack.getCompositeFinal().map(source -> {
            Pass pass = new Pass();
            ProgramDirectives directives = source.getDirectives();
            pass.program = this.createProgram((ProgramSource)source, flippedBuffers, flippedAtLeastOnce, shadowTargetsSupplier);
            pass.computes = this.createComputes(pack.getFinalCompute(), flippedBuffers, flippedAtLeastOnce, shadowTargetsSupplier, holder);
            pass.stageReadsFromAlt = flippedBuffers;
            pass.mipmappedBuffers = directives.getMipmappedBuffers();
            return pass;
        }).orElse(null);
        IntList buffersToBeCleared = pack.getPackDirectives().getRenderTargetDirectives().getBuffersToBeCleared();
        this.baseline = renderTargets.createGbufferFramebuffer(flippedBuffers, new int[]{0});
        this.colorHolder = new GlFramebuffer();
        this.lastColorTextureId = Minecraft.m_91087_().m_91385_().m_83975_();
        this.lastColorTextureVersion = ((Blaze3dRenderTargetExt)Minecraft.m_91087_().m_91385_()).iris$getColorBufferVersion();
        this.colorHolder.addColorAttachment(0, this.lastColorTextureId);
        ImmutableList.Builder swapPasses = ImmutableList.builder();
        flippedBuffers.forEach(i -> {
            int target = i;
            if (buffersToBeCleared.contains(target)) {
                return;
            }
            SwapPass swap = new SwapPass();
            net.irisshaders.iris.targets.RenderTarget target1 = renderTargets.getOrCreate(target);
            swap.target = target;
            swap.width = target1.getWidth();
            swap.height = target1.getHeight();
            swap.from = renderTargets.createColorFramebuffer((ImmutableSet<Integer>)ImmutableSet.of(), new int[]{target});
            swap.targetTexture = renderTargets.get(target).getMainTexture();
            swapPasses.add((Object)swap);
        });
        this.swapPasses = swapPasses.build();
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

    private static void resetRenderTarget(net.irisshaders.iris.targets.RenderTarget target) {
        if (target == null) {
            return;
        }
        int filter = 9729;
        if (target.getInternalFormat().getPixelFormat().isInteger()) {
            filter = 9728;
        }
        IrisRenderSystem.texParameteri(target.getMainTexture(), 3553, 10241, filter);
        IrisRenderSystem.texParameteri(target.getAltTexture(), 3553, 10241, filter);
        RenderSystem.bindTexture((int)0);
    }

    public void renderFinalPass() {
        RenderSystem.disableBlend();
        RenderSystem.depthMask((boolean)false);
        RenderTarget main = Minecraft.m_91087_().m_91385_();
        int baseWidth = main.f_83915_;
        int baseHeight = main.f_83916_;
        if (((Blaze3dRenderTargetExt)main).iris$getColorBufferVersion() != this.lastColorTextureVersion || main.m_83975_() != this.lastColorTextureId) {
            this.lastColorTextureVersion = ((Blaze3dRenderTargetExt)main).iris$getColorBufferVersion();
            this.lastColorTextureId = main.m_83975_();
            this.colorHolder.addColorAttachment(0, this.lastColorTextureId);
        }
        if (this.finalPass != null) {
            this.colorHolder.bind();
            FullScreenQuadRenderer.INSTANCE.begin();
            for (ComputeProgram computeProgram : this.finalPass.computes) {
                if (computeProgram == null) continue;
                computeProgram.use();
                this.customUniforms.push(computeProgram);
                computeProgram.dispatch(baseWidth, baseHeight);
            }
            IrisRenderSystem.memoryBarrier(8232);
            if (!this.finalPass.mipmappedBuffers.isEmpty()) {
                RenderSystem.activeTexture((int)33984);
                UnmodifiableIterator unmodifiableIterator = this.finalPass.mipmappedBuffers.iterator();
                while (unmodifiableIterator.hasNext()) {
                    int index = (Integer)unmodifiableIterator.next();
                    FinalPassRenderer.setupMipmapping(this.renderTargets.get(index), this.finalPass.stageReadsFromAlt.contains((Object)index));
                }
            }
            this.finalPass.program.use();
            this.customUniforms.push(this.finalPass.program);
            FullScreenQuadRenderer.INSTANCE.renderQuad();
            FullScreenQuadRenderer.INSTANCE.end();
        } else {
            this.baseline.bindAsReadBuffer();
            IrisRenderSystem.copyTexSubImage2D(main.m_83975_(), 3553, 0, 0, 0, 0, 0, baseWidth, baseHeight);
        }
        RenderSystem.activeTexture((int)33984);
        for (int i = 0; i < this.renderTargets.getRenderTargetCount(); ++i) {
            FinalPassRenderer.resetRenderTarget(this.renderTargets.get(i));
        }
        for (SwapPass swapPass : this.swapPasses) {
            swapPass.from.bind();
            RenderSystem.bindTexture((int)swapPass.targetTexture);
            GlStateManager._glCopyTexSubImage2D((int)3553, (int)0, (int)0, (int)0, (int)0, (int)0, (int)swapPass.width, (int)swapPass.height);
        }
        main.m_83947_(true);
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

    public void recalculateSwapPassSize() {
        for (SwapPass swapPass : this.swapPasses) {
            net.irisshaders.iris.targets.RenderTarget target = this.renderTargets.get(swapPass.target);
            this.renderTargets.destroyFramebuffer(swapPass.from);
            swapPass.from = this.renderTargets.createColorFramebuffer((ImmutableSet<Integer>)ImmutableSet.of(), new int[]{swapPass.target});
            swapPass.width = target.getWidth();
            swapPass.height = target.getHeight();
            swapPass.targetTexture = target.getMainTexture();
        }
    }

    private Program createProgram(ProgramSource source, ImmutableSet<Integer> flipped, ImmutableSet<Integer> flippedAtLeastOnceSnapshot, Supplier<ShadowRenderTargets> shadowTargetsSupplier) {
        ProgramBuilder builder;
        Map<PatchShaderType, String> transformed = TransformPatcher.patchComposite(source.getName(), source.getVertexSource().orElseThrow(NullPointerException::new), source.getGeometrySource().orElse(null), source.getFragmentSource().orElseThrow(NullPointerException::new), TextureStage.COMPOSITE_AND_FINAL, this.pipeline.getTextureMap());
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
            throw new RuntimeException("Shader compilation failed for final!", e);
        }
        CommonUniforms.addDynamicUniforms(builder, FogMode.OFF);
        this.customUniforms.assignTo(builder);
        ProgramSamplers.CustomTextureSamplerInterceptor customTextureSamplerInterceptor = ProgramSamplers.customTextureSamplerInterceptor(builder, this.customTextureIds, flippedAtLeastOnceSnapshot);
        IrisSamplers.addRenderTargetSamplers(customTextureSamplerInterceptor, () -> flipped, this.renderTargets, true, this.pipeline);
        IrisSamplers.addCustomImages(customTextureSamplerInterceptor, this.customImages);
        IrisImages.addRenderTargetImages(builder, () -> flipped, this.renderTargets);
        IrisImages.addCustomImages(builder, this.customImages);
        IrisSamplers.addCustomTextures(builder, this.irisCustomTextures);
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
                String transformed = TransformPatcher.patchCompute(source.getName(), source.getSource().orElse(null), TextureStage.COMPOSITE_AND_FINAL, this.pipeline.getTextureMap());
                ShaderPrinter.printProgram(source.getName()).addSource(PatchShaderType.COMPUTE, transformed).print();
                builder = ProgramBuilder.beginCompute(source.getName(), transformed, IrisSamplers.COMPOSITE_RESERVED_TEXTURE_UNITS);
            }
            catch (ShaderCompileException e) {
                throw e;
            }
            catch (RuntimeException e) {
                throw new RuntimeException("Shader compilation failed for final compute " + source.getName() + "!", e);
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
        if (this.finalPass != null) {
            this.finalPass.destroy();
        }
        this.colorHolder.destroy();
    }

    private static final class Pass {
        Program program;
        ComputeProgram[] computes;
        ImmutableSet<Integer> stageReadsFromAlt;
        ImmutableSet<Integer> mipmappedBuffers;

        private Pass() {
        }

        private void destroy() {
            this.program.destroy();
        }
    }

    private static final class SwapPass {
        public int target;
        public int width;
        public int height;
        GlFramebuffer from;
        int targetTexture;

        private SwapPass() {
        }
    }
}

