/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package net.irisshaders.iris.shaderpack.include;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.shaderpack.error.RusticError;
import net.irisshaders.iris.shaderpack.include.AbsolutePackPath;
import net.irisshaders.iris.shaderpack.include.FileNode;
import net.irisshaders.iris.shaderpack.transform.line.LineTransform;

public class IncludeGraph {
    private final ImmutableMap<AbsolutePackPath, FileNode> nodes;
    private final ImmutableMap<AbsolutePackPath, RusticError> failures;

    private IncludeGraph(ImmutableMap<AbsolutePackPath, FileNode> nodes, ImmutableMap<AbsolutePackPath, RusticError> failures) {
        this.nodes = nodes;
        this.failures = failures;
    }

    public IncludeGraph(Path root, ImmutableList<AbsolutePackPath> startingPaths) {
        HashMap<AbsolutePackPath, AbsolutePackPath> cameFrom = new HashMap<AbsolutePackPath, AbsolutePackPath>();
        HashMap<AbsolutePackPath, Integer> lineNumberInclude = new HashMap<AbsolutePackPath, Integer>();
        HashMap<AbsolutePackPath, FileNode> nodes = new HashMap<AbsolutePackPath, FileNode>();
        HashMap<AbsolutePackPath, RusticError> failures = new HashMap<AbsolutePackPath, RusticError>();
        ArrayList<AbsolutePackPath> queue = new ArrayList<AbsolutePackPath>((Collection<AbsolutePackPath>)startingPaths);
        HashSet<AbsolutePackPath> seen = new HashSet<AbsolutePackPath>((Collection<AbsolutePackPath>)startingPaths);
        while (!queue.isEmpty()) {
            String source;
            AbsolutePackPath next = (AbsolutePackPath)queue.remove(queue.size() - 1);
            try {
                source = IncludeGraph.readFile(next.resolved(root));
            }
            catch (IOException e) {
                String detailMessage;
                Object topLevelMessage;
                AbsolutePackPath src = (AbsolutePackPath)cameFrom.get(next);
                if (src == null) {
                    throw new RuntimeException("unexpected error: failed to read " + next.getPathString(), e);
                }
                if (e instanceof NoSuchFileException) {
                    topLevelMessage = "failed to resolve #include directive";
                    detailMessage = "file not found";
                } else {
                    topLevelMessage = "unexpected I/O error while resolving #include directive: " + e;
                    detailMessage = "IO error";
                }
                String badLine = ((String)((FileNode)nodes.get(src)).getLines().get(((Integer)lineNumberInclude.get(next)).intValue())).trim();
                RusticError topLevelError = new RusticError("error", (String)topLevelMessage, detailMessage, src.getPathString(), (Integer)lineNumberInclude.get(next) + 1, badLine);
                failures.put(next, topLevelError);
                continue;
            }
            ImmutableList lines = ImmutableList.copyOf((Object[])source.split("\\R"));
            FileNode node = new FileNode(next, (ImmutableList<String>)lines);
            boolean selfInclude = false;
            for (Map.Entry include : node.getIncludes().entrySet()) {
                int line = (Integer)include.getKey();
                AbsolutePackPath included = (AbsolutePackPath)include.getValue();
                if (next.equals(included)) {
                    selfInclude = true;
                    failures.put(next, new RusticError("error", "trivial #include cycle detected", "file includes itself", next.getPathString(), line + 1, (String)lines.get(line)));
                    break;
                }
                if (seen.contains(included)) continue;
                queue.add(included);
                seen.add(included);
                cameFrom.put(included, next);
                lineNumberInclude.put(included, line);
            }
            if (selfInclude) continue;
            nodes.put(next, node);
        }
        this.nodes = ImmutableMap.copyOf(nodes);
        this.failures = ImmutableMap.copyOf(failures);
        this.detectCycle();
    }

    private static String readFile(Path path) throws IOException {
        return Files.readString((Path)path);
    }

    private void detectCycle() {
        ArrayList<AbsolutePackPath> cycle = new ArrayList<AbsolutePackPath>();
        HashSet<AbsolutePackPath> visited = new HashSet<AbsolutePackPath>();
        for (AbsolutePackPath start : this.nodes.keySet()) {
            if (!this.exploreForCycles(start, cycle, visited)) continue;
            AbsolutePackPath lastFilePath = null;
            StringBuilder error = new StringBuilder();
            for (AbsolutePackPath node : cycle) {
                String detailMessage;
                if (lastFilePath == null) {
                    lastFilePath = node;
                    continue;
                }
                FileNode lastFile = (FileNode)this.nodes.get((Object)lastFilePath);
                int lineNumber = -1;
                for (Map.Entry include : lastFile.getIncludes().entrySet()) {
                    if (include.getValue() != node) continue;
                    lineNumber = (Integer)include.getKey() + 1;
                }
                String badLine = (String)lastFile.getLines().get(lineNumber - 1);
                String string = detailMessage = node.equals(start) ? "final #include in cycle" : "#include involved in cycle";
                if (lastFilePath.equals(start)) {
                    error.append((Object)new RusticError("error", "#include cycle detected", detailMessage, lastFilePath.getPathString(), lineNumber, badLine));
                } else {
                    error.append("\n  = ").append((Object)new RusticError("note", "cycle involves another file", detailMessage, lastFilePath.getPathString(), lineNumber, badLine));
                }
                lastFilePath = node;
            }
            error.append("  note: #include directives are resolved before any other preprocessor directives, any form of #include guard will not work\n\n  note: other cycles may still exist, only the first detected non-trivial cycle will be reported\n");
            Iris.logger.error(error.toString());
            throw new IllegalStateException("Cycle detected in #include graph, see previous messages for details");
        }
    }

    private boolean exploreForCycles(AbsolutePackPath frontier, List<AbsolutePackPath> path, Set<AbsolutePackPath> visited) {
        if (visited.contains(frontier)) {
            path.add(frontier);
            return true;
        }
        path.add(frontier);
        visited.add(frontier);
        for (AbsolutePackPath included : ((FileNode)this.nodes.get((Object)frontier)).getIncludes().values()) {
            if (!this.nodes.containsKey((Object)included) || !this.exploreForCycles(included, path, visited)) continue;
            return true;
        }
        path.remove(path.size() - 1);
        visited.remove(frontier);
        return false;
    }

    public ImmutableMap<AbsolutePackPath, FileNode> getNodes() {
        return this.nodes;
    }

    public List<IncludeGraph> computeWeaklyConnectedComponents() {
        return Collections.singletonList(this);
    }

    public IncludeGraph map(Function<AbsolutePackPath, LineTransform> transformProvider) {
        ImmutableMap.Builder mappedNodes = ImmutableMap.builder();
        this.nodes.forEach((path, node) -> mappedNodes.put(path, (Object)node.map((LineTransform)transformProvider.apply((AbsolutePackPath)path))));
        return new IncludeGraph((ImmutableMap<AbsolutePackPath, FileNode>)mappedNodes.build(), this.failures);
    }

    public ImmutableMap<AbsolutePackPath, RusticError> getFailures() {
        return this.failures;
    }
}

