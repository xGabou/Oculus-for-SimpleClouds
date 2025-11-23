/*
 * Decompiled with CFR 0.152.
 */
package de.odysseus.ithaka.digraph.io.tgf;

import de.odysseus.ithaka.digraph.Digraph;
import de.odysseus.ithaka.digraph.io.tgf.TgfLabelProvider;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

public class TgfExporter {
    private final String newline;

    public TgfExporter() {
        this(System.getProperty("line.separator"));
    }

    public TgfExporter(String newline) {
        this.newline = newline;
    }

    public <V> void export(TgfLabelProvider<V> provider, Digraph<V> digraph, Writer writer) throws IOException {
        HashMap<V, Integer> index = new HashMap<V, Integer>();
        int n = 0;
        for (V vertex : digraph.vertices()) {
            index.put(vertex, ++n);
            writer.write(String.valueOf(n));
            String label = provider.getVertexLabel(vertex);
            if (label != null) {
                writer.write(32);
                writer.write(label);
            }
            writer.write(this.newline);
        }
        writer.write(35);
        writer.write(this.newline);
        for (V source : digraph.vertices()) {
            for (V target : digraph.targets(source)) {
                writer.write(String.valueOf(index.get(source)));
                writer.write(32);
                writer.write(String.valueOf(index.get(target)));
                String label = provider.getEdgeLabel(digraph.get(source, target).getAsInt());
                if (label != null) {
                    writer.write(32);
                    writer.write(label);
                }
                writer.write(this.newline);
            }
        }
        writer.flush();
    }
}

