/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package net.irisshaders.batchedentityrendering.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import java.util.Map;
import net.irisshaders.batchedentityrendering.impl.MemoryTrackingBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={MultiBufferSource.BufferSource.class})
public class MixinBufferSource
implements MemoryTrackingBuffer {
    @Shadow
    @Final
    protected BufferBuilder f_109904_;
    @Shadow
    @Final
    protected Map<RenderType, BufferBuilder> f_109905_;

    @Override
    public int getAllocatedSize() {
        int allocatedSize = ((MemoryTrackingBuffer)this.f_109904_).getAllocatedSize();
        for (BufferBuilder builder : this.f_109905_.values()) {
            allocatedSize += ((MemoryTrackingBuffer)builder).getAllocatedSize();
        }
        return allocatedSize;
    }

    @Override
    public int getUsedSize() {
        int allocatedSize = ((MemoryTrackingBuffer)this.f_109904_).getUsedSize();
        for (BufferBuilder builder : this.f_109905_.values()) {
            allocatedSize += ((MemoryTrackingBuffer)builder).getUsedSize();
        }
        return allocatedSize;
    }

    @Override
    public void freeAndDeleteBuffer() {
        ((MemoryTrackingBuffer)this.f_109904_).freeAndDeleteBuffer();
        for (BufferBuilder builder : this.f_109905_.values()) {
            ((MemoryTrackingBuffer)builder).freeAndDeleteBuffer();
        }
    }
}

