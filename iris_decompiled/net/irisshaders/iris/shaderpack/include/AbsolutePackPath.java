/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.shaderpack.include;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class AbsolutePackPath {
    private final String path;

    private AbsolutePackPath(String absolute) {
        this.path = absolute;
    }

    public static AbsolutePackPath fromAbsolutePath(String absolutePath) {
        return new AbsolutePackPath(AbsolutePackPath.normalizeAbsolutePath(absolutePath));
    }

    private static String normalizeAbsolutePath(String path) {
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Not an absolute path: " + path);
        }
        String[] segments = path.split(Pattern.quote("/"));
        ArrayList<String> parsedSegments = new ArrayList<String>();
        for (String segment : segments) {
            if (segment.isEmpty() || segment.equals(".")) continue;
            if (segment.equals("..")) {
                if (parsedSegments.isEmpty()) continue;
                parsedSegments.remove(parsedSegments.size() - 1);
                continue;
            }
            parsedSegments.add(segment);
        }
        if (parsedSegments.isEmpty()) {
            return "/";
        }
        StringBuilder normalized = new StringBuilder();
        for (String segment : parsedSegments) {
            normalized.append('/');
            normalized.append(segment);
        }
        return normalized.toString();
    }

    public Optional<AbsolutePackPath> parent() {
        if (this.path.equals("/")) {
            return Optional.empty();
        }
        int lastSlash = this.path.lastIndexOf(47);
        return Optional.of(new AbsolutePackPath(this.path.substring(0, lastSlash)));
    }

    public AbsolutePackPath resolve(String path) {
        if (path.startsWith("/")) {
            return AbsolutePackPath.fromAbsolutePath(path);
        }
        String merged = !this.path.endsWith("/") & !path.startsWith("/") ? this.path + "/" + path : this.path + path;
        return AbsolutePackPath.fromAbsolutePath(merged);
    }

    public Path resolved(Path root) {
        if (this.path.equals("/")) {
            return root;
        }
        return root.resolve(this.path.substring(1));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbsolutePackPath that = (AbsolutePackPath)o;
        return Objects.equals(this.path, that.path);
    }

    public int hashCode() {
        return Objects.hash(this.path);
    }

    public String toString() {
        return "AbsolutePackPath {" + this.getPathString() + "}";
    }

    public String getPathString() {
        return this.path;
    }
}

