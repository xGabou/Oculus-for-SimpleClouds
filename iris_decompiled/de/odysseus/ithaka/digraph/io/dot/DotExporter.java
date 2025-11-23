/*
 * Decompiled with CFR 0.152.
 */
package de.odysseus.ithaka.digraph.io.dot;

import de.odysseus.ithaka.digraph.Digraph;
import de.odysseus.ithaka.digraph.DigraphProvider;
import de.odysseus.ithaka.digraph.io.dot.DotAttribute;
import de.odysseus.ithaka.digraph.io.dot.DotProvider;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DotExporter {
    private final String indent;
    private final String lineSpeparator;

    public DotExporter() {
        this("  ", System.getProperty("line.separator"));
    }

    public DotExporter(String indent, String newline) {
        this.indent = indent;
        this.lineSpeparator = newline;
    }

    private void indent(Writer writer, int level) throws IOException {
        for (int i = 0; i < level; ++i) {
            writer.write(this.indent);
        }
    }

    private void writeAttributes(Writer writer, Iterator<DotAttribute> iterator) throws IOException {
        if (iterator.hasNext()) {
            boolean first = true;
            while (iterator.hasNext()) {
                if (first) {
                    writer.write(91);
                    first = false;
                } else {
                    writer.write(", ");
                }
                iterator.next().write(writer);
            }
            writer.write(93);
        }
    }

    private void writeDefaultAttributes(Writer writer, int level, String name, Iterable<DotAttribute> attributes) throws IOException {
        if (attributes != null) {
            this.indent(writer, level);
            Iterator<DotAttribute> iterator = attributes.iterator();
            if (iterator.hasNext()) {
                writer.write(name);
                this.writeAttributes(writer, iterator);
            }
            writer.write(";");
            writer.write(this.lineSpeparator);
        }
    }

    private <V> void writeNode(Writer writer, int level, V vertex, DotProvider<V, ?> provider) throws IOException {
        this.indent(writer, level);
        writer.write(provider.getNodeId(vertex));
        Iterable<DotAttribute> attributes = provider.getNodeAttributes(vertex);
        if (attributes != null) {
            this.writeAttributes(writer, attributes.iterator());
        }
        writer.write(";");
        writer.write(this.lineSpeparator);
    }

    private <V> void writeEdge(Writer writer, int level, V source, V target, int edgeWeight, DotProvider<V, ?> provider, Cluster<V, ?> sourceCluster, Cluster<V, ?> targetCluster) throws IOException {
        this.indent(writer, level);
        writer.write(provider.getNodeId(sourceCluster == null ? source : sourceCluster.sample));
        writer.write(" -> ");
        writer.write(provider.getNodeId(targetCluster == null ? target : targetCluster.sample));
        Iterable<DotAttribute> attributes = provider.getEdgeAttributes(source, target, edgeWeight);
        if (sourceCluster == null && targetCluster == null) {
            if (attributes != null) {
                this.writeAttributes(writer, attributes.iterator());
            }
        } else {
            ArrayList<DotAttribute> attributeList = new ArrayList<DotAttribute>();
            if (sourceCluster != null) {
                attributeList.add(sourceCluster.tail);
            }
            if (targetCluster != null) {
                attributeList.add(targetCluster.head);
            }
            if (attributes != null) {
                for (DotAttribute attribute : attributes) {
                    attributeList.add(attribute);
                }
            }
            this.writeAttributes(writer, attributeList.iterator());
        }
        writer.write(";");
        writer.write(this.lineSpeparator);
    }

    private <V, G extends Digraph<V>> Map<V, Cluster<V, G>> createClusters(G digraph, DotProvider<V, G> provider, DigraphProvider<? super V, G> subgraphs) {
        HashMap clusters = new HashMap();
        if (subgraphs != null) {
            for (V vertex : digraph.vertices()) {
                G subgraph = subgraphs.get(vertex);
                if (subgraph == null || subgraph.getVertexCount() <= 0) continue;
                clusters.put(vertex, new Cluster("cluster_" + provider.getNodeId(vertex), subgraph));
            }
        }
        return clusters;
    }

    public <V, G extends Digraph<V>> void export(DotProvider<V, G> provider, G digraph, DigraphProvider<V, G> subgraphs, Writer writer) throws IOException {
        writer.write("de.odysseus.ithaka.digraph G {");
        writer.write(this.lineSpeparator);
        Map<V, Cluster<V, G>> clusters = this.createClusters(digraph, provider, subgraphs);
        if (!clusters.isEmpty()) {
            this.indent(writer, 1);
            writer.write("compound=true;");
            writer.write(this.lineSpeparator);
        }
        this.writeDefaultAttributes(writer, 1, "graph", provider.getDefaultGraphAttributes(digraph));
        this.writeDefaultAttributes(writer, 1, "node", provider.getDefaultNodeAttributes(digraph));
        this.writeDefaultAttributes(writer, 1, "edge", provider.getDefaultEdgeAttributes(digraph));
        this.writeNodesAndEdges(writer, 1, provider, digraph, clusters, subgraphs);
        writer.write("}");
        writer.write(this.lineSpeparator);
        writer.flush();
    }

    private <V, G extends Digraph<V>> void writeNodesAndEdges(Writer writer, int level, DotProvider<V, G> provider, G digraph, Map<V, Cluster<V, G>> clusters, DigraphProvider<V, G> subgraphs) throws IOException {
        for (V vertex : digraph.vertices()) {
            if (clusters.containsKey(vertex)) {
                this.writeCluster(writer, level, provider, vertex, clusters.get(vertex), subgraphs);
                continue;
            }
            this.writeNode(writer, level, vertex, provider);
        }
        for (V source : digraph.vertices()) {
            for (V target : digraph.targets(source)) {
                this.writeEdge(writer, level, source, target, digraph.get(source, target).getAsInt(), provider, clusters.get(source), clusters.get(target));
            }
        }
    }

    private <V, G extends Digraph<V>> void writeCluster(Writer writer, int level, DotProvider<V, G> provider, V subgraphVertex, Cluster<V, G> cluster, DigraphProvider<V, G> subgraphs) throws IOException {
        this.indent(writer, level);
        writer.write("subgraph ");
        writer.write(cluster.id);
        writer.write(" {");
        writer.write(this.lineSpeparator);
        this.writeDefaultAttributes(writer, level + 1, "graph", provider.getSubgraphAttributes(cluster.subgraph, subgraphVertex));
        Map subclusters = this.createClusters(cluster.subgraph, provider, (DigraphProvider<? super V, G>)subgraphs);
        this.writeNodesAndEdges(writer, level + 1, provider, cluster.subgraph, subclusters, subgraphs);
        this.indent(writer, level);
        writer.write("}");
        writer.write(this.lineSpeparator);
    }

    private static class Cluster<V, G extends Digraph<V>> {
        String id;
        G subgraph;
        V sample;
        DotAttribute tail;
        DotAttribute head;

        public Cluster(String id, G subgraph) {
            this.id = id;
            this.subgraph = subgraph;
            this.sample = subgraph.vertices().iterator().next();
            this.head = new DotAttribute("lhead", id);
            this.tail = new DotAttribute("ltail", id);
        }
    }
}

