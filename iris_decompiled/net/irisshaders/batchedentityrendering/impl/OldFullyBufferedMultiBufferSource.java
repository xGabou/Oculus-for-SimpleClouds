/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexSorting
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 */
package net.irisshaders.batchedentityrendering.impl;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexSorting;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.irisshaders.batchedentityrendering.impl.BlendingStateHolder;
import net.irisshaders.batchedentityrendering.impl.TransparencyType;
import net.irisshaders.batchedentityrendering.impl.WrappableRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class OldFullyBufferedMultiBufferSource
extends MultiBufferSource.BufferSource {
    private final Map<RenderType, BufferBuilder> bufferBuilders = new HashMap<RenderType, BufferBuilder>();
    private final Object2IntMap<RenderType> unused = new Object2IntOpenHashMap();
    private final Set<BufferBuilder> activeBuffers = new HashSet<BufferBuilder>();
    private final Set<RenderType> typesThisFrame = new HashSet<RenderType>();
    private final List<RenderType> typesInOrder = new ArrayList<RenderType>();
    private boolean flushed = false;

    public OldFullyBufferedMultiBufferSource() {
        super(new BufferBuilder(0), Collections.emptyMap());
    }

    private TransparencyType getTransparencyType(RenderType type) {
        while (type instanceof WrappableRenderType) {
            type = ((WrappableRenderType)type).unwrap();
        }
        if (type instanceof BlendingStateHolder) {
            return ((BlendingStateHolder)type).getTransparencyType();
        }
        return TransparencyType.GENERAL_TRANSPARENT;
    }

    public VertexConsumer m_6299_(RenderType renderType) {
        this.flushed = false;
        BufferBuilder buffer = this.bufferBuilders.computeIfAbsent(renderType, type -> new BufferBuilder(type.m_110507_()));
        if (this.activeBuffers.add(buffer)) {
            buffer.m_166779_(renderType.m_173186_(), renderType.m_110508_());
        }
        if (this.typesThisFrame.add(renderType)) {
            this.typesInOrder.add(renderType);
        }
        this.unused.removeInt((Object)renderType);
        return buffer;
    }

    public void m_109911_() {
        if (this.flushed) {
            return;
        }
        ArrayList removedTypes = new ArrayList();
        this.unused.forEach((unusedType, unusedCount) -> {
            if (unusedCount < 10) {
                return;
            }
            BufferBuilder buffer = this.bufferBuilders.remove(unusedType);
            removedTypes.add(unusedType);
            if (this.activeBuffers.contains(buffer)) {
                throw new IllegalStateException("A buffer was simultaneously marked as inactive and as active, something is very wrong...");
            }
        });
        for (RenderType removed : removedTypes) {
            this.unused.removeInt((Object)removed);
        }
        this.typesInOrder.sort(Comparator.comparing(this::getTransparencyType));
        for (RenderType type : this.typesInOrder) {
            this.drawInternal(type);
        }
        this.typesInOrder.clear();
        this.typesThisFrame.clear();
        this.flushed = true;
    }

    public void m_109912_(RenderType type) {
    }

    private void drawInternal(RenderType type) {
        BufferBuilder buffer = this.bufferBuilders.get(type);
        if (buffer == null) {
            return;
        }
        if (this.activeBuffers.remove(buffer)) {
            type.m_276775_(buffer, VertexSorting.f_276450_);
            buffer.m_85729_();
        } else {
            int unusedCount = this.unused.getOrDefault((Object)type, 0);
            this.unused.put((Object)type, ++unusedCount);
        }
    }
}

