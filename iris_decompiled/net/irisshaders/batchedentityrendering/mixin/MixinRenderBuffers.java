/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.ChunkBufferBuilderPack
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.OutlineBufferSource
 *  net.minecraft.client.renderer.RenderBuffers
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.batchedentityrendering.mixin;

import net.irisshaders.batchedentityrendering.impl.DrawCallTrackingRenderBuffers;
import net.irisshaders.batchedentityrendering.impl.FullyBufferedMultiBufferSource;
import net.irisshaders.batchedentityrendering.impl.MemoryTrackingBuffer;
import net.irisshaders.batchedentityrendering.impl.MemoryTrackingRenderBuffers;
import net.irisshaders.batchedentityrendering.impl.RenderBuffersExt;
import net.irisshaders.batchedentityrendering.mixin.BufferSourceAccessor;
import net.irisshaders.batchedentityrendering.mixin.ChunkBufferBuilderPackAccessor;
import net.irisshaders.batchedentityrendering.mixin.OutlineBufferSourceAccessor;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={RenderBuffers.class})
public class MixinRenderBuffers
implements RenderBuffersExt,
MemoryTrackingRenderBuffers,
DrawCallTrackingRenderBuffers {
    @Unique
    private final FullyBufferedMultiBufferSource buffered = new FullyBufferedMultiBufferSource();
    @Unique
    private final OutlineBufferSource outlineBufferSource = new OutlineBufferSource((MultiBufferSource.BufferSource)this.buffered);
    @Unique
    private int begins = 0;
    @Unique
    private int maxBegins = 0;
    @Shadow
    @Final
    private MultiBufferSource.BufferSource f_110094_;
    @Shadow
    @Final
    private MultiBufferSource.BufferSource f_110095_;
    @Shadow
    @Final
    private ChunkBufferBuilderPack f_110092_;

    @Inject(method={"bufferSource"}, at={@At(value="HEAD")}, cancellable=true)
    private void batchedentityrendering$replaceBufferSource(CallbackInfoReturnable<MultiBufferSource.BufferSource> cir) {
        if (this.begins == 0) {
            return;
        }
        cir.setReturnValue((Object)this.buffered);
    }

    @Inject(method={"crumblingBufferSource"}, at={@At(value="HEAD")}, cancellable=true)
    private void batchedentityrendering$replaceCrumblingBufferSource(CallbackInfoReturnable<MultiBufferSource.BufferSource> cir) {
        if (this.begins == 0) {
            return;
        }
        cir.setReturnValue((Object)this.buffered.getUnflushableWrapper());
    }

    @Inject(method={"outlineBufferSource"}, at={@At(value="HEAD")}, cancellable=true)
    private void batchedentityrendering$replaceOutlineBufferSource(CallbackInfoReturnable<OutlineBufferSource> provider) {
        if (this.begins == 0) {
            return;
        }
        provider.setReturnValue((Object)this.outlineBufferSource);
    }

    @Override
    public void beginLevelRendering() {
        if (this.begins == 0) {
            this.buffered.assertWrapStackEmpty();
        }
        ++this.begins;
        this.maxBegins = Math.max(this.begins, this.maxBegins);
    }

    @Override
    public void endLevelRendering() {
        --this.begins;
        if (this.begins == 0) {
            this.buffered.assertWrapStackEmpty();
        }
    }

    @Override
    public int getEntityBufferAllocatedSize() {
        return this.buffered.getAllocatedSize();
    }

    @Override
    public int getMiscBufferAllocatedSize() {
        return ((MemoryTrackingBuffer)this.f_110094_).getAllocatedSize();
    }

    @Override
    public int getMaxBegins() {
        return this.maxBegins;
    }

    @Override
    public void freeAndDeleteBuffers() {
        this.buffered.freeAndDeleteBuffer();
        ((ChunkBufferBuilderPackAccessor)this.f_110092_).getBuilders().values().forEach(bufferBuilder -> ((MemoryTrackingBuffer)bufferBuilder).freeAndDeleteBuffer());
        ((BufferSourceAccessor)this.f_110094_).getFixedBuffers().forEach((renderType, bufferBuilder) -> ((MemoryTrackingBuffer)bufferBuilder).freeAndDeleteBuffer());
        ((BufferSourceAccessor)this.f_110094_).getFixedBuffers().clear();
        ((MemoryTrackingBuffer)((OutlineBufferSourceAccessor)this.outlineBufferSource).getOutlineBufferSource()).freeAndDeleteBuffer();
    }

    @Override
    public int getDrawCalls() {
        return this.buffered.getDrawCalls();
    }

    @Override
    public int getRenderTypes() {
        return this.buffered.getRenderTypes();
    }

    @Override
    public void resetDrawCounts() {
        this.buffered.resetDrawCalls();
    }
}

