/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.util.profiling.ProfilerFiller
 */
package net.irisshaders.batchedentityrendering.impl;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.irisshaders.batchedentityrendering.impl.BlendingStateHolder;
import net.irisshaders.batchedentityrendering.impl.BufferSegment;
import net.irisshaders.batchedentityrendering.impl.BufferSegmentRenderer;
import net.irisshaders.batchedentityrendering.impl.Groupable;
import net.irisshaders.batchedentityrendering.impl.MemoryTrackingBuffer;
import net.irisshaders.batchedentityrendering.impl.SegmentedBufferBuilder;
import net.irisshaders.batchedentityrendering.impl.TransparencyType;
import net.irisshaders.batchedentityrendering.impl.ordering.GraphTranslucencyRenderOrderManager;
import net.irisshaders.batchedentityrendering.impl.ordering.RenderOrderManager;
import net.irisshaders.iris.layer.WrappingMultiBufferSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.profiling.ProfilerFiller;

public class FullyBufferedMultiBufferSource
extends MultiBufferSource.BufferSource
implements MemoryTrackingBuffer,
Groupable,
WrappingMultiBufferSource {
    private static final int NUM_BUFFERS = 32;
    private final RenderOrderManager renderOrderManager;
    private final SegmentedBufferBuilder[] builders;
    private final LinkedHashMap<RenderType, Integer> affinities;
    private final BufferSegmentRenderer segmentRenderer;
    private final UnflushableWrapper unflushableWrapper;
    private final List<Function<RenderType, RenderType>> wrappingFunctionStack;
    private final Map<RenderType, List<BufferSegment>> typeToSegment = new HashMap<RenderType, List<BufferSegment>>();
    private int drawCalls;
    private int renderTypes;
    private Function<RenderType, RenderType> wrappingFunction = null;
    private boolean isReady;
    private List<RenderType> renderOrder = new ArrayList<RenderType>();

    public FullyBufferedMultiBufferSource() {
        super(new BufferBuilder(0), Collections.emptyMap());
        this.renderOrderManager = new GraphTranslucencyRenderOrderManager();
        this.builders = new SegmentedBufferBuilder[32];
        for (int i = 0; i < this.builders.length; ++i) {
            this.builders[i] = new SegmentedBufferBuilder();
        }
        this.affinities = new LinkedHashMap(32, 0.75f, true);
        this.drawCalls = 0;
        this.segmentRenderer = new BufferSegmentRenderer();
        this.unflushableWrapper = new UnflushableWrapper(this);
        this.wrappingFunctionStack = new ArrayList<Function<RenderType, RenderType>>();
    }

    public VertexConsumer m_6299_(RenderType renderType) {
        this.removeReady();
        if (this.wrappingFunction != null) {
            renderType = this.wrappingFunction.apply(renderType);
        }
        this.renderOrderManager.begin(renderType);
        Integer affinity = this.affinities.get(renderType);
        if (affinity == null) {
            if (this.affinities.size() < this.builders.length) {
                affinity = this.affinities.size();
            } else {
                Iterator<Map.Entry<RenderType, Integer>> iterator = this.affinities.entrySet().iterator();
                Map.Entry<RenderType, Integer> evicted = iterator.next();
                iterator.remove();
                this.affinities.remove(evicted.getKey());
                affinity = evicted.getValue();
            }
            this.affinities.put(renderType, affinity);
        }
        return this.builders[affinity].m_6299_(renderType);
    }

    private void removeReady() {
        this.isReady = false;
        this.typeToSegment.clear();
        this.renderOrder.clear();
    }

    public void readyUp() {
        this.isReady = true;
        ProfilerFiller profiler = Minecraft.m_91087_().m_91307_();
        profiler.m_6180_("collect");
        for (SegmentedBufferBuilder builder : this.builders) {
            List<BufferSegment> segments = builder.getSegments();
            for (BufferSegment segment : segments) {
                this.typeToSegment.computeIfAbsent(segment.type(), type -> new ArrayList()).add(segment);
            }
        }
        profiler.m_6182_("resolve ordering");
        this.renderOrder = this.renderOrderManager.getRenderOrder();
        this.renderOrderManager.reset();
        this.affinities.clear();
        profiler.m_7238_();
    }

    public void m_109911_() {
        ProfilerFiller profiler = Minecraft.m_91087_().m_91307_();
        if (!this.isReady) {
            this.readyUp();
        }
        profiler.m_6180_("draw buffers");
        for (RenderType type : this.renderOrder) {
            type.m_110185_();
            ++this.renderTypes;
            for (BufferSegment segment : this.typeToSegment.getOrDefault(type, Collections.emptyList())) {
                this.segmentRenderer.drawInner(segment);
                ++this.drawCalls;
            }
            type.m_110188_();
        }
        profiler.m_6182_("reset");
        this.removeReady();
        profiler.m_7238_();
    }

    public void endBatchWithType(TransparencyType transparencyType) {
        ProfilerFiller profiler = Minecraft.m_91087_().m_91307_();
        if (!this.isReady) {
            this.readyUp();
        }
        profiler.m_6180_("draw buffers");
        ArrayList<RenderType> types = new ArrayList<RenderType>();
        for (RenderType type : this.renderOrder) {
            if (((BlendingStateHolder)type).getTransparencyType() != transparencyType) continue;
            types.add(type);
            type.m_110185_();
            ++this.renderTypes;
            for (BufferSegment segment : this.typeToSegment.getOrDefault(type, Collections.emptyList())) {
                this.segmentRenderer.drawInner(segment);
                ++this.drawCalls;
            }
            this.typeToSegment.remove(type);
            type.m_110188_();
        }
        profiler.m_6182_("reset type " + transparencyType);
        this.renderOrder.removeAll(types);
        profiler.m_7238_();
    }

    public int getDrawCalls() {
        return this.drawCalls;
    }

    public int getRenderTypes() {
        return this.renderTypes;
    }

    public void resetDrawCalls() {
        this.drawCalls = 0;
        this.renderTypes = 0;
    }

    public void m_109912_(RenderType type) {
    }

    public MultiBufferSource.BufferSource getUnflushableWrapper() {
        return this.unflushableWrapper;
    }

    @Override
    public int getAllocatedSize() {
        int size = 0;
        for (SegmentedBufferBuilder builder : this.builders) {
            size += builder.getAllocatedSize();
        }
        return size;
    }

    @Override
    public int getUsedSize() {
        int size = 0;
        for (SegmentedBufferBuilder builder : this.builders) {
            size += builder.getUsedSize();
        }
        return size;
    }

    @Override
    public void freeAndDeleteBuffer() {
        for (SegmentedBufferBuilder builder : this.builders) {
            builder.freeAndDeleteBuffer();
        }
    }

    @Override
    public void startGroup() {
        this.renderOrderManager.startGroup();
    }

    @Override
    public boolean maybeStartGroup() {
        return this.renderOrderManager.maybeStartGroup();
    }

    @Override
    public void endGroup() {
        this.renderOrderManager.endGroup();
    }

    @Override
    public void pushWrappingFunction(Function<RenderType, RenderType> wrappingFunction) {
        if (this.wrappingFunction != null) {
            this.wrappingFunctionStack.add(this.wrappingFunction);
        }
        this.wrappingFunction = wrappingFunction;
    }

    @Override
    public void popWrappingFunction() {
        this.wrappingFunction = this.wrappingFunctionStack.isEmpty() ? null : this.wrappingFunctionStack.remove(this.wrappingFunctionStack.size() - 1);
    }

    @Override
    public void assertWrapStackEmpty() {
        if (!this.wrappingFunctionStack.isEmpty() || this.wrappingFunction != null) {
            throw new IllegalStateException("Wrapping function stack not empty!");
        }
    }

    private static class UnflushableWrapper
    extends MultiBufferSource.BufferSource
    implements Groupable {
        private final FullyBufferedMultiBufferSource wrapped;

        UnflushableWrapper(FullyBufferedMultiBufferSource wrapped) {
            super(new BufferBuilder(0), Collections.emptyMap());
            this.wrapped = wrapped;
        }

        public VertexConsumer m_6299_(RenderType renderType) {
            return this.wrapped.m_6299_(renderType);
        }

        public void m_109911_() {
        }

        public void m_109912_(RenderType type) {
        }

        @Override
        public void startGroup() {
            this.wrapped.startGroup();
        }

        @Override
        public boolean maybeStartGroup() {
            return this.wrapped.maybeStartGroup();
        }

        @Override
        public void endGroup() {
            this.wrapped.endGroup();
        }
    }
}

