/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 */
package net.irisshaders.iris.shaderpack.include;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.irisshaders.iris.shaderpack.include.AbsolutePackPath;
import net.irisshaders.iris.shaderpack.include.FileNode;
import net.irisshaders.iris.shaderpack.include.IncludeGraph;

public class IncludeProcessor {
    private final IncludeGraph graph;
    private final Map<AbsolutePackPath, ImmutableList<String>> cache;

    public IncludeProcessor(IncludeGraph graph) {
        this.graph = graph;
        this.cache = new HashMap<AbsolutePackPath, ImmutableList<String>>();
    }

    public ImmutableList<String> getIncludedFile(AbsolutePackPath path) {
        ImmutableList<String> lines = this.cache.get(path);
        if (lines == null) {
            lines = this.process(path);
            this.cache.put(path, lines);
        }
        return lines;
    }

    private ImmutableList<String> process(AbsolutePackPath path) {
        FileNode fileNode = (FileNode)this.graph.getNodes().get((Object)path);
        if (fileNode == null) {
            return null;
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        ImmutableList<String> lines = fileNode.getLines();
        ImmutableMap<Integer, AbsolutePackPath> includes = fileNode.getIncludes();
        for (int i = 0; i < lines.size(); ++i) {
            AbsolutePackPath include = (AbsolutePackPath)includes.get((Object)i);
            if (include != null) {
                builder.addAll((Iterable)Objects.requireNonNull(this.getIncludedFile(include)));
                continue;
            }
            builder.add((Object)((String)lines.get(i)));
        }
        return builder.build();
    }
}

