/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package net.irisshaders.iris.shaderpack.include;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import net.irisshaders.iris.shaderpack.include.AbsolutePackPath;
import net.irisshaders.iris.shaderpack.transform.line.LineTransform;

public class FileNode {
    private final AbsolutePackPath path;
    private final ImmutableList<String> lines;
    private final ImmutableMap<Integer, AbsolutePackPath> includes;

    private FileNode(AbsolutePackPath path, ImmutableList<String> lines, ImmutableMap<Integer, AbsolutePackPath> includes) {
        this.path = path;
        this.lines = lines;
        this.includes = includes;
    }

    public FileNode(AbsolutePackPath path, ImmutableList<String> lines) {
        this.path = path;
        this.lines = lines;
        AbsolutePackPath currentDirectory = path.parent().orElseThrow(() -> new IllegalArgumentException("Not a valid shader file name: " + path));
        this.includes = FileNode.findIncludes(currentDirectory, lines);
    }

    private static ImmutableMap<Integer, AbsolutePackPath> findIncludes(AbsolutePackPath currentDirectory, ImmutableList<String> lines) {
        ImmutableMap.Builder foundIncludes = ImmutableMap.builder();
        for (int i = 0; i < lines.size(); ++i) {
            String line = ((String)lines.get(i)).trim();
            if (!line.startsWith("#include")) continue;
            String target = line.substring("#include ".length()).trim();
            if (target.startsWith("\"")) {
                target = target.substring(1);
            }
            if (target.endsWith("\"")) {
                target = target.substring(0, target.length() - 1);
            }
            foundIncludes.put((Object)i, (Object)currentDirectory.resolve(target));
        }
        return foundIncludes.build();
    }

    public AbsolutePackPath getPath() {
        return this.path;
    }

    public ImmutableList<String> getLines() {
        return this.lines;
    }

    public ImmutableMap<Integer, AbsolutePackPath> getIncludes() {
        return this.includes;
    }

    public FileNode map(LineTransform transform) {
        ImmutableList.Builder newLines = ImmutableList.builder();
        int index = 0;
        for (String line : this.lines) {
            String transformedLine = transform.transform(index, line);
            if (this.includes.containsKey((Object)index) && !Objects.equals(line, transformedLine)) {
                throw new IllegalStateException("Attempted to modify an #include line in LineTransform.");
            }
            newLines.add((Object)transformedLine);
            ++index;
        }
        return new FileNode(this.path, (ImmutableList<String>)newLines.build(), this.includes);
    }
}

