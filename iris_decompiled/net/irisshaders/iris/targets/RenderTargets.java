/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.mojang.blaze3d.systems.RenderSystem
 *  org.joml.Vector2i
 */
package net.irisshaders.iris.targets;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.irisshaders.iris.gl.texture.DepthBufferFormat;
import net.irisshaders.iris.gl.texture.DepthCopyStrategy;
import net.irisshaders.iris.shaderpack.properties.PackDirectives;
import net.irisshaders.iris.shaderpack.properties.PackRenderTargetDirectives;
import net.irisshaders.iris.targets.DepthTexture;
import net.irisshaders.iris.targets.RenderTarget;
import org.joml.Vector2i;

public class RenderTargets {
    private final RenderTarget[] targets;
    private final DepthTexture noTranslucents;
    private final DepthTexture noHand;
    private final GlFramebuffer depthSourceFb;
    private final GlFramebuffer noTranslucentsDestFb;
    private final GlFramebuffer noHandDestFb;
    private final List<GlFramebuffer> ownedFramebuffers;
    private final Map<Integer, PackRenderTargetDirectives.RenderTargetSettings> targetSettingsMap;
    private final PackDirectives packDirectives;
    private int currentDepthTexture;
    private DepthBufferFormat currentDepthFormat;
    private DepthCopyStrategy copyStrategy;
    private int cachedWidth;
    private int cachedHeight;
    private boolean fullClearRequired;
    private boolean translucentDepthDirty;
    private boolean handDepthDirty;
    private int cachedDepthBufferVersion;
    private boolean destroyed;

    public RenderTargets(int width, int height, int depthTexture, int depthBufferVersion, DepthBufferFormat depthFormat, Map<Integer, PackRenderTargetDirectives.RenderTargetSettings> renderTargets, PackDirectives packDirectives) {
        this.targets = new RenderTarget[renderTargets.size()];
        this.targetSettingsMap = renderTargets;
        this.packDirectives = packDirectives;
        this.currentDepthTexture = depthTexture;
        this.currentDepthFormat = depthFormat;
        this.copyStrategy = DepthCopyStrategy.fastest(this.currentDepthFormat.isCombinedStencil());
        this.cachedWidth = width;
        this.cachedHeight = height;
        this.cachedDepthBufferVersion = depthBufferVersion;
        this.ownedFramebuffers = new ArrayList<GlFramebuffer>();
        this.fullClearRequired = true;
        this.depthSourceFb = this.createFramebufferWritingToMain(new int[]{0});
        this.noTranslucents = new DepthTexture("depthtex1", width, height, this.currentDepthFormat);
        this.noHand = new DepthTexture("dephtex2", width, height, this.currentDepthFormat);
        this.noTranslucentsDestFb = this.createFramebufferWritingToMain(new int[]{0});
        this.noTranslucentsDestFb.addDepthAttachment(this.noTranslucents.getTextureId());
        this.noHandDestFb = this.createFramebufferWritingToMain(new int[]{0});
        this.noHandDestFb.addDepthAttachment(this.noHand.getTextureId());
        this.translucentDepthDirty = true;
        this.handDepthDirty = true;
    }

    public void destroy() {
        this.destroyed = true;
        for (GlFramebuffer owned : this.ownedFramebuffers) {
            owned.destroy();
        }
        for (RenderTarget target : this.targets) {
            if (target == null) continue;
            target.destroy();
        }
        this.noTranslucents.destroy();
        this.noHand.destroy();
    }

    public int getRenderTargetCount() {
        return this.targets.length;
    }

    public RenderTarget get(int index) {
        if (this.destroyed) {
            throw new IllegalStateException("Tried to use destroyed RenderTargets");
        }
        if (this.targets[index] == null) {
            return null;
        }
        return this.targets[index];
    }

    public RenderTarget getOrCreate(int index) {
        if (this.destroyed) {
            throw new IllegalStateException("Tried to use destroyed RenderTargets");
        }
        if (this.targets[index] != null) {
            return this.targets[index];
        }
        this.create(index);
        return this.targets[index];
    }

    private void create(int index) {
        PackRenderTargetDirectives.RenderTargetSettings settings = this.targetSettingsMap.get(index);
        Vector2i dimensions = this.packDirectives.getTextureScaleOverride(index, this.cachedWidth, this.cachedHeight);
        this.targets[index] = RenderTarget.builder().setDimensions(dimensions.x, dimensions.y).setName("colortex" + index).setInternalFormat(settings.getInternalFormat()).setPixelFormat(settings.getInternalFormat().getPixelFormat()).build();
    }

    public int getDepthTexture() {
        return this.currentDepthTexture;
    }

    public DepthTexture getDepthTextureNoTranslucents() {
        if (this.destroyed) {
            throw new IllegalStateException("Tried to use destroyed RenderTargets");
        }
        return this.noTranslucents;
    }

    public DepthTexture getDepthTextureNoHand() {
        return this.noHand;
    }

    public boolean resizeIfNeeded(int newDepthBufferVersion, int newDepthTextureId, int newWidth, int newHeight, DepthBufferFormat newDepthFormat, PackDirectives packDirectives) {
        boolean depthFormatChanged;
        boolean recreateDepth = false;
        if (this.cachedDepthBufferVersion != newDepthBufferVersion) {
            recreateDepth = true;
            this.currentDepthTexture = newDepthTextureId;
            this.cachedDepthBufferVersion = newDepthBufferVersion;
        }
        boolean sizeChanged = newWidth != this.cachedWidth || newHeight != this.cachedHeight;
        boolean bl = depthFormatChanged = newDepthFormat != this.currentDepthFormat;
        if (depthFormatChanged) {
            this.currentDepthFormat = newDepthFormat;
            this.copyStrategy = DepthCopyStrategy.fastest(this.currentDepthFormat.isCombinedStencil());
        }
        if (recreateDepth) {
            for (GlFramebuffer framebuffer : this.ownedFramebuffers) {
                if (framebuffer == this.noHandDestFb || framebuffer == this.noTranslucentsDestFb || !framebuffer.hasDepthAttachment()) continue;
                framebuffer.addDepthAttachment(newDepthTextureId);
            }
        }
        if (depthFormatChanged || sizeChanged) {
            this.noTranslucents.resize(newWidth, newHeight, newDepthFormat);
            this.noHand.resize(newWidth, newHeight, newDepthFormat);
            this.translucentDepthDirty = true;
            this.handDepthDirty = true;
        }
        if (sizeChanged) {
            this.cachedWidth = newWidth;
            this.cachedHeight = newHeight;
            for (int i = 0; i < this.targets.length; ++i) {
                if (this.targets[i] == null) continue;
                this.targets[i].resize(packDirectives.getTextureScaleOverride(i, newWidth, newHeight));
            }
            this.fullClearRequired = true;
        }
        return sizeChanged;
    }

    public void copyPreTranslucentDepth() {
        if (this.translucentDepthDirty) {
            this.translucentDepthDirty = false;
            RenderSystem.bindTexture((int)this.noTranslucents.getTextureId());
            this.depthSourceFb.bindAsReadBuffer();
            IrisRenderSystem.copyTexImage2D(3553, 0, this.currentDepthFormat.getGlInternalFormat(), 0, 0, this.cachedWidth, this.cachedHeight, 0);
        } else {
            this.copyStrategy.copy(this.depthSourceFb, this.getDepthTexture(), this.noTranslucentsDestFb, this.noTranslucents.getTextureId(), this.getCurrentWidth(), this.getCurrentHeight());
        }
    }

    public void copyPreHandDepth() {
        if (this.handDepthDirty) {
            this.handDepthDirty = false;
            RenderSystem.bindTexture((int)this.noHand.getTextureId());
            this.depthSourceFb.bindAsReadBuffer();
            IrisRenderSystem.copyTexImage2D(3553, 0, this.currentDepthFormat.getGlInternalFormat(), 0, 0, this.cachedWidth, this.cachedHeight, 0);
        } else {
            this.copyStrategy.copy(this.depthSourceFb, this.getDepthTexture(), this.noHandDestFb, this.noHand.getTextureId(), this.getCurrentWidth(), this.getCurrentHeight());
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

    public GlFramebuffer createClearFramebuffer(boolean alt, int[] clearBuffers) {
        ImmutableSet<Integer> stageWritesToMain = ImmutableSet.of();
        if (!alt) {
            stageWritesToMain = this.invert((ImmutableSet<Integer>)ImmutableSet.of(), clearBuffers);
        }
        return this.createColorFramebuffer(stageWritesToMain, clearBuffers);
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
        framebuffer.addDepthAttachment(this.currentDepthTexture);
        framebuffer.addColorAttachment(0, this.getOrCreate(0).getMainTexture());
        framebuffer.noDrawBuffers();
        return framebuffer;
    }

    public GlFramebuffer createDHFramebuffer(ImmutableSet<Integer> stageWritesToAlt, int[] drawBuffers) {
        if (drawBuffers.length == 0) {
            return this.createEmptyFramebuffer();
        }
        ImmutableSet<Integer> stageWritesToMain = this.invert(stageWritesToAlt, drawBuffers);
        GlFramebuffer framebuffer = this.createColorFramebuffer(stageWritesToMain, drawBuffers);
        return framebuffer;
    }

    public GlFramebuffer createGbufferFramebuffer(ImmutableSet<Integer> stageWritesToAlt, int[] drawBuffers) {
        if (drawBuffers.length == 0) {
            return this.createEmptyFramebuffer();
        }
        ImmutableSet<Integer> stageWritesToMain = this.invert(stageWritesToAlt, drawBuffers);
        GlFramebuffer framebuffer = this.createColorFramebuffer(stageWritesToMain, drawBuffers);
        framebuffer.addDepthAttachment(this.currentDepthTexture);
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
        framebuffer.addDepthAttachment(this.currentDepthTexture);
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
                framebuffer.destroy();
                this.ownedFramebuffers.remove(framebuffer);
                throw new IllegalStateException("Render target with index " + drawBuffers[i] + " is not supported, only " + this.getRenderTargetCount() + " render targets are supported.");
            }
            RenderTarget target = this.getOrCreate(drawBuffers[i]);
            int textureId = stageWritesToMain.contains((Object)drawBuffers[i]) ? target.getMainTexture() : target.getAltTexture();
            framebuffer.addColorAttachment(i, textureId);
        }
        framebuffer.drawBuffers(actualDrawBuffers);
        framebuffer.readBuffer(0);
        int status = framebuffer.getStatus();
        if (status != 36053) {
            throw new IllegalStateException("Unexpected error while creating framebuffer: Draw buffers " + Arrays.toString(actualDrawBuffers) + " Status: " + status);
        }
        return framebuffer;
    }

    public void destroyFramebuffer(GlFramebuffer framebuffer) {
        framebuffer.destroy();
        this.ownedFramebuffers.remove(framebuffer);
    }

    public int getCurrentWidth() {
        return this.cachedWidth;
    }

    public int getCurrentHeight() {
        return this.cachedHeight;
    }

    public void createIfUnsure(int index) {
        if (this.targets[index] == null) {
            this.create(index);
        }
    }
}

