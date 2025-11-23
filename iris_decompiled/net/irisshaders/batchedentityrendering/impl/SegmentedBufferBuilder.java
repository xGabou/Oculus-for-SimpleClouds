/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 */
package net.irisshaders.batchedentityrendering.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.irisshaders.batchedentityrendering.impl.BlendingStateHolder;
import net.irisshaders.batchedentityrendering.impl.BufferBuilderExt;
import net.irisshaders.batchedentityrendering.impl.BufferSegment;
import net.irisshaders.batchedentityrendering.impl.MemoryTrackingBuffer;
import net.irisshaders.batchedentityrendering.impl.RenderTypeUtil;
import net.irisshaders.batchedentityrendering.impl.TransparencyType;
import net.irisshaders.batchedentityrendering.mixin.RenderTypeAccessor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class SegmentedBufferBuilder
implements MultiBufferSource,
MemoryTrackingBuffer {
    private final BufferBuilder buffer = new BufferBuilder(524288);
    private final List<BufferSegment> buffers = new ArrayList<BufferSegment>();
    private RenderType currentType = null;

    private static boolean shouldSortOnUpload(RenderType type) {
        return ((RenderTypeAccessor)type).shouldSortOnUpload();
    }

    public VertexConsumer m_6299_(RenderType renderType) {
        if (!Objects.equals(this.currentType, renderType)) {
            if (this.currentType != null) {
                if (SegmentedBufferBuilder.shouldSortOnUpload(this.currentType)) {
                    this.buffer.m_277127_(RenderSystem.getVertexSorting());
                }
                this.buffers.add(new BufferSegment(Objects.requireNonNull(this.buffer.m_231175_()), this.currentType));
            }
            this.buffer.m_166779_(renderType.m_173186_(), renderType.m_110508_());
            this.currentType = renderType;
        }
        if (RenderTypeUtil.isTriangleStripDrawMode(this.currentType)) {
            ((BufferBuilderExt)this.buffer).splitStrip();
        }
        return this.buffer;
    }

    public List<BufferSegment> getSegments() {
        if (this.currentType == null) {
            return Collections.emptyList();
        }
        if (SegmentedBufferBuilder.shouldSortOnUpload(this.currentType)) {
            this.buffer.m_277127_(RenderSystem.getVertexSorting());
        }
        this.buffers.add(new BufferSegment(Objects.requireNonNull(this.buffer.m_231175_()), this.currentType));
        this.currentType = null;
        ArrayList<BufferSegment> finalSegments = new ArrayList<BufferSegment>(this.buffers);
        this.buffers.clear();
        return finalSegments;
    }

    public List<BufferSegment> getSegmentsForType(TransparencyType transparencyType) {
        if (this.currentType == null) {
            return Collections.emptyList();
        }
        if (((BlendingStateHolder)this.currentType).getTransparencyType() == transparencyType) {
            if (SegmentedBufferBuilder.shouldSortOnUpload(this.currentType)) {
                this.buffer.m_277127_(RenderSystem.getVertexSorting());
            }
            this.buffers.add(new BufferSegment(Objects.requireNonNull(this.buffer.m_231175_()), this.currentType));
            this.currentType = null;
        }
        List finalSegments = this.buffers.stream().filter(segment -> ((BlendingStateHolder)segment.type()).getTransparencyType() == transparencyType).toList();
        this.buffers.removeAll(finalSegments);
        return finalSegments;
    }

    @Override
    public int getAllocatedSize() {
        return ((MemoryTrackingBuffer)this.buffer).getAllocatedSize();
    }

    @Override
    public int getUsedSize() {
        return ((MemoryTrackingBuffer)this.buffer).getUsedSize();
    }

    @Override
    public void freeAndDeleteBuffer() {
        ((MemoryTrackingBuffer)this.buffer).freeAndDeleteBuffer();
    }
}

