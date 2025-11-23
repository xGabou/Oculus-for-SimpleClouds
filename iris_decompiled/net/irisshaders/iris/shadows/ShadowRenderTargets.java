/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 */
package net.irisshaders.iris.shadows;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import net.irisshaders.iris.features.FeatureFlags;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.irisshaders.iris.gl.texture.DepthBufferFormat;
import net.irisshaders.iris.gl.texture.DepthCopyStrategy;
import net.irisshaders.iris.gl.texture.InternalTextureFormat;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.shaderpack.properties.PackShadowDirectives;
import net.irisshaders.iris.targets.DepthTexture;
import net.irisshaders.iris.targets.RenderTarget;

public class ShadowRenderTargets {
    private final RenderTarget[] targets;
    private final PackShadowDirectives shadowDirectives;
    private final DepthTexture mainDepth;
    private final DepthTexture noTranslucents;
    private final GlFramebuffer depthSourceFb;
    private final GlFramebuffer noTranslucentsDestFb;
    private final boolean[] flipped;
    private final List<GlFramebuffer> ownedFramebuffers;
    private final int resolution;
    private final WorldRenderingPipeline pipeline;
    private final boolean[] hardwareFiltered;
    private final boolean[] linearFiltered;
    private final InternalTextureFormat[] formats;
    private final IntList buffersToBeCleared;
    private final int size;
    private final boolean shouldRefresh;
    private boolean fullClearRequired;
    private boolean translucentDepthDirty;

    public ShadowRenderTargets(WorldRenderingPipeline pipeline, int resolution, PackShadowDirectives shadowDirectives) {
        this.pipeline = pipeline;
        this.shadowDirectives = shadowDirectives;
        this.size = pipeline.hasFeature(FeatureFlags.HIGHER_SHADOWCOLOR) ? 8 : 2;
        this.targets = new RenderTarget[this.size];
        this.formats = new InternalTextureFormat[this.size];
        this.flipped = new boolean[this.size];
        this.hardwareFiltered = new boolean[this.size];
        this.linearFiltered = new boolean[this.size];
        this.buffersToBeCleared = new IntArrayList();
        this.mainDepth = new DepthTexture("shadowtex0", resolution, resolution, DepthBufferFormat.DEPTH);
        this.noTranslucents = new DepthTexture("shadowtex1", resolution, resolution, DepthBufferFormat.DEPTH);
        this.ownedFramebuffers = new ArrayList<GlFramebuffer>();
        this.resolution = resolution;
        for (int i = 0; i < shadowDirectives.getDepthSamplingSettings().size(); ++i) {
            this.hardwareFiltered[i] = ((PackShadowDirectives.DepthSamplingSettings)shadowDirectives.getDepthSamplingSettings().get(i)).getHardwareFiltering();
            this.linearFiltered[i] = !((PackShadowDirectives.DepthSamplingSettings)shadowDirectives.getDepthSamplingSettings().get(i)).getNearest();
        }
        this.fullClearRequired = true;
        this.depthSourceFb = this.createFramebufferWritingToMain(new int[]{0});
        this.noTranslucentsDestFb = this.createFramebufferWritingToMain(new int[]{0});
        this.noTranslucentsDestFb.addDepthAttachment(this.noTranslucents.getTextureId());
        this.translucentDepthDirty = true;
        this.shouldRefresh = false;
    }

    public void flip(int target) {
        this.flipped[target] = !this.flipped[target];
    }

    public boolean isFlipped(int target) {
        return this.flipped[target];
    }

    public void destroy() {
        for (GlFramebuffer owned : this.ownedFramebuffers) {
            owned.destroy();
        }
        for (RenderTarget target : this.targets) {
            if (target == null) continue;
            target.destroy();
        }
        this.mainDepth.destroy();
        this.noTranslucents.destroy();
    }

    public int getRenderTargetCount() {
        return this.targets.length;
    }

    public RenderTarget get(int index) {
        return this.targets[index];
    }

    public RenderTarget getOrCreate(int index) {
        if (this.targets[index] != null) {
            return this.targets[index];
        }
        this.create(index);
        return this.targets[index];
    }

    private void create(int index) {
        if (index > this.size) {
            throw new IllegalStateException("Tried to access buffer higher than allowed limit of " + this.size + "! If you're trying to use shadowcolor2-7, you need to activate it's feature flag!");
        }
        PackShadowDirectives.SamplingSettings settings = (PackShadowDirectives.SamplingSettings)this.shadowDirectives.getColorSamplingSettings().computeIfAbsent(index, i -> new PackShadowDirectives.SamplingSettings());
        this.targets[index] = RenderTarget.builder().setDimensions(this.resolution, this.resolution).setInternalFormat(settings.getFormat()).setName("shadowcolor" + index).setPixelFormat(settings.getFormat().getPixelFormat()).build();
        this.formats[index] = settings.getFormat();
        if (settings.getClear()) {
            this.buffersToBeCleared.add(index);
        }
        if (settings.getClear()) {
            this.buffersToBeCleared.add(index);
        }
        this.fullClearRequired = true;
    }

    public void createIfEmpty(int index) {
        if (this.targets[index] == null) {
            this.create(index);
        }
    }

    public int getResolution() {
        return this.resolution;
    }

    public DepthTexture getDepthTexture() {
        return this.mainDepth;
    }

    public DepthTexture getDepthTextureNoTranslucents() {
        return this.noTranslucents;
    }

    public GlFramebuffer getDepthSourceFb() {
        return this.depthSourceFb;
    }

    public void copyPreTranslucentDepth() {
        if (this.translucentDepthDirty) {
            this.translucentDepthDirty = false;
            IrisRenderSystem.blitFramebuffer(this.depthSourceFb.getId(), this.noTranslucentsDestFb.getId(), 0, 0, this.resolution, this.resolution, 0, 0, this.resolution, this.resolution, 256, 9728);
        } else {
            DepthCopyStrategy.fastest(false).copy(this.depthSourceFb, this.mainDepth.getTextureId(), this.noTranslucentsDestFb, this.noTranslucents.getTextureId(), this.resolution, this.resolution);
        }
    }

    public boolean isFullClearRequired() {
        return this.fullClearRequired;
    }

    public void onFullClear() {
        this.fullClearRequired = false;
    }

    public GlFramebuffer createFramebufferWritingToMain(int[] drawBuffers) {
        return this.createFullFramebuffer(false, drawBuffers);
    }

    public GlFramebuffer createFramebufferWritingToAlt(int[] drawBuffers) {
        return this.createFullFramebuffer(true, drawBuffers);
    }

    private ImmutableSet<Integer> invert(ImmutableSet<Integer> base, int[] relevant) {
        ImmutableSet.Builder inverted = ImmutableSet.builder();
        for (int i : relevant) {
            if (base.contains((Object)i)) continue;
            inverted.add((Object)i);
        }
        return inverted.build();
    }

    private GlFramebuffer createEmptyFramebuffer() {
        GlFramebuffer framebuffer = new GlFramebuffer();
        this.ownedFramebuffers.add(framebuffer);
        framebuffer.addDepthAttachment(this.mainDepth.getTextureId());
        framebuffer.addColorAttachment(0, this.get(0).getMainTexture());
        framebuffer.noDrawBuffers();
        return framebuffer;
    }

    public GlFramebuffer createDHFramebuffer(ImmutableSet<Integer> stageWritesToAlt, int[] drawBuffers) {
        if (drawBuffers.length == 0) {
            return this.createEmptyFramebuffer();
        }
        ImmutableSet<Integer> stageWritesToMain = this.invert(stageWritesToAlt, drawBuffers);
        GlFramebuffer framebuffer = this.createColorFramebuffer(stageWritesToMain, drawBuffers);
        framebuffer.addDepthAttachment(this.mainDepth.getTextureId());
        return framebuffer;
    }

    public GlFramebuffer createShadowFramebuffer(ImmutableSet<Integer> stageWritesToAlt, int[] drawBuffers) {
        if (drawBuffers.length == 0) {
            return this.createEmptyFramebuffer();
        }
        ImmutableSet<Integer> stageWritesToMain = this.invert(stageWritesToAlt, drawBuffers);
        GlFramebuffer framebuffer = this.createColorFramebuffer(stageWritesToMain, drawBuffers);
        framebuffer.addDepthAttachment(this.mainDepth.getTextureId());
        return framebuffer;
    }

    private GlFramebuffer createFullFramebuffer(boolean clearsAlt, int[] drawBuffers) {
        if (drawBuffers.length == 0) {
            return this.createEmptyFramebuffer();
        }
        ImmutableSet<Integer> stageWritesToMain = ImmutableSet.of();
        if (!clearsAlt) {
            stageWritesToMain = this.invert((ImmutableSet<Integer>)ImmutableSet.of(), drawBuffers);
        }
        return this.createColorFramebufferWithDepth(stageWritesToMain, drawBuffers);
    }

    public GlFramebuffer createColorFramebufferWithDepth(ImmutableSet<Integer> stageWritesToMain, int[] drawBuffers) {
        GlFramebuffer framebuffer = this.createColorFramebuffer(stageWritesToMain, drawBuffers);
        framebuffer.addDepthAttachment(this.mainDepth.getTextureId());
        return framebuffer;
    }

    public GlFramebuffer createColorFramebuffer(ImmutableSet<Integer> stageWritesToMain, int[] drawBuffers) {
        if (drawBuffers.length == 0) {
            throw new IllegalArgumentException("Framebuffer must have at least one color buffer");
        }
        GlFramebuffer framebuffer = new GlFramebuffer();
        this.ownedFramebuffers.add(framebuffer);
        int[] actualDrawBuffers = new int[drawBuffers.length];
        for (int i = 0; i < drawBuffers.length; ++i) {
            actualDrawBuffers[i] = i;
            if (drawBuffers[i] >= this.getRenderTargetCount()) {
                this.ownedFramebuffers.remove(framebuffer);
                framebuffer.destroy();
                return this.createColorFramebuffer(stageWritesToMain, new int[]{0, 1});
            }
            RenderTarget target = this.getOrCreate(drawBuffers[i]);
            int textureId = stageWritesToMain.contains((Object)drawBuffers[i]) ? target.getMainTexture() : target.getAltTexture();
            framebuffer.addColorAttachment(i, textureId);
        }
        framebuffer.drawBuffers(actualDrawBuffers);
        framebuffer.readBuffer(0);
        int status = framebuffer.getStatus();
        if (status != 36053) {
            throw new IllegalStateException("Unexpected error while creating framebuffer");
        }
        return framebuffer;
    }

    public int getColorTextureId(int i) {
        return this.isFlipped(i) ? this.get(i).getAltTexture() : this.get(i).getMainTexture();
    }

    public boolean isHardwareFiltered(int i) {
        return this.hardwareFiltered[i];
    }

    public boolean isLinearFiltered(int i) {
        return this.linearFiltered[i];
    }

    public int getNumColorTextures() {
        return this.targets.length;
    }

    public InternalTextureFormat getColorTextureFormat(int index) {
        return this.formats[index];
    }

    public ImmutableSet<Integer> snapshot() {
        ImmutableSet.Builder builder = ImmutableSet.builder();
        for (int i = 0; i < this.flipped.length; ++i) {
            if (!this.flipped[i]) continue;
            builder.add((Object)i);
        }
        return builder.build();
    }

    public IntList getBuffersToBeCleared() {
        return this.buffersToBeCleared;
    }
}

