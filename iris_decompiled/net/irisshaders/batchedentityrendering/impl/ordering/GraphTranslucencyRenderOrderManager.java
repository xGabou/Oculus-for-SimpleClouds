/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderType
 */
package net.irisshaders.batchedentityrendering.impl.ordering;

import de.odysseus.ithaka.digraph.Digraph;
import de.odysseus.ithaka.digraph.Digraphs;
import de.odysseus.ithaka.digraph.MapDigraph;
import de.odysseus.ithaka.digraph.util.fas.FeedbackArcSet;
import de.odysseus.ithaka.digraph.util.fas.FeedbackArcSetPolicy;
import de.odysseus.ithaka.digraph.util.fas.FeedbackArcSetProvider;
import de.odysseus.ithaka.digraph.util.fas.SimpleFeedbackArcSetProvider;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import net.irisshaders.batchedentityrendering.impl.BlendingStateHolder;
import net.irisshaders.batchedentityrendering.impl.TransparencyType;
import net.irisshaders.batchedentityrendering.impl.WrappableRenderType;
import net.irisshaders.batchedentityrendering.impl.ordering.RenderOrderManager;
import net.minecraft.client.renderer.RenderType;

public class GraphTranslucencyRenderOrderManager
implements RenderOrderManager {
    private final FeedbackArcSetProvider feedbackArcSetProvider = new SimpleFeedbackArcSetProvider();
    private final EnumMap<TransparencyType, Digraph<RenderType>> types = new EnumMap(TransparencyType.class);
    private final EnumMap<TransparencyType, RenderType> currentTypes = new EnumMap(TransparencyType.class);
    private boolean inGroup = false;

    public GraphTranslucencyRenderOrderManager() {
        for (TransparencyType type : TransparencyType.values()) {
            this.types.put(type, new MapDigraph());
        }
    }

    private static TransparencyType getTransparencyType(RenderType type) {
        while (type instanceof WrappableRenderType) {
            type = ((WrappableRenderType)type).unwrap();
        }
        if (type instanceof BlendingStateHolder) {
            return ((BlendingStateHolder)type).getTransparencyType();
        }
        return TransparencyType.GENERAL_TRANSPARENT;
    }

    @Override
    public void begin(RenderType renderType) {
        TransparencyType transparencyType = GraphTranslucencyRenderOrderManager.getTransparencyType(renderType);
        Digraph<RenderType> graph = this.types.get((Object)transparencyType);
        graph.add(renderType);
        if (this.inGroup) {
            RenderType previous = this.currentTypes.put(transparencyType, renderType);
            if (previous == null) {
                return;
            }
            int weight = graph.get(previous, renderType).orElse(0);
            graph.put(previous, renderType, ++weight);
        }
    }

    @Override
    public void startGroup() {
        if (this.inGroup) {
            throw new IllegalStateException("Already in a group");
        }
        this.currentTypes.clear();
        this.inGroup = true;
    }

    @Override
    public boolean maybeStartGroup() {
        if (this.inGroup) {
            return false;
        }
        this.currentTypes.clear();
        this.inGroup = true;
        return true;
    }

    @Override
    public void endGroup() {
        if (!this.inGroup) {
            throw new IllegalStateException("Not in a group");
        }
        this.currentTypes.clear();
        this.inGroup = false;
    }

    @Override
    public void reset() {
        this.types.clear();
        for (TransparencyType type : TransparencyType.values()) {
            this.types.put(type, new MapDigraph());
        }
    }

    @Override
    public void resetType(TransparencyType type) {
        this.types.put(type, new MapDigraph());
    }

    @Override
    public List<RenderType> getRenderOrder() {
        int layerCount = 0;
        for (Digraph<RenderType> graph : this.types.values()) {
            layerCount += graph.getVertexCount();
        }
        ArrayList<RenderType> allLayers = new ArrayList<RenderType>(layerCount);
        for (Digraph<RenderType> graph : this.types.values()) {
            FeedbackArcSet<RenderType> arcSet = this.feedbackArcSetProvider.getFeedbackArcSet(graph, graph, FeedbackArcSetPolicy.MIN_WEIGHT);
            if (arcSet.getEdgeCount() > 0) {
                for (RenderType source : arcSet.vertices()) {
                    for (RenderType target : arcSet.targets(source)) {
                        graph.remove(source, target);
                    }
                }
            }
            allLayers.addAll(Digraphs.toposort(graph, false));
        }
        return allLayers;
    }
}

