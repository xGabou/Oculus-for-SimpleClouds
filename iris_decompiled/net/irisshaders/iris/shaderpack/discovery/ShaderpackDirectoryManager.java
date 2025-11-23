/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.shaderpack.discovery;

import java.io.IOException;
import java.net.URI;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.irisshaders.iris.Iris;

public class ShaderpackDirectoryManager {
    private final Path root;

    public ShaderpackDirectoryManager(Path root) {
        this.root = root;
    }

    private static String removeFormatting(String formatted) {
        char[] original = formatted.toCharArray();
        char[] cleaned = new char[original.length];
        int c = 0;
        for (int i = 0; i < original.length; ++i) {
            if (original[i] == '\u00a7') {
                ++i;
                continue;
            }
            cleaned[c++] = original[i];
        }
        return new String(cleaned, 0, c);
    }

    public void copyPackIntoDirectory(String name, Path source) throws IOException {
        Path target = Iris.getShaderpacksDirectory().resolve(name);
        Files.copy(source, target, new CopyOption[0]);
        if (Files.isDirectory(source, new LinkOption[0])) {
            try (Stream<Path> stream = Files.walk(source, new FileVisitOption[0]);){
                for (Path p2 : stream.filter(x$0 -> Files.isDirectory(x$0, new LinkOption[0])).toList()) {
                    Path folder = source.relativize(p2);
                    if (Files.exists(folder, new LinkOption[0])) continue;
                    Files.createDirectory(target.resolve(folder), new FileAttribute[0]);
                }
            }
            stream = Files.walk(source, new FileVisitOption[0]);
            try {
                for (Path p2 : stream.filter(p -> !Files.isDirectory(p, new LinkOption[0])).collect(Collectors.toSet())) {
                    Path file = source.relativize(p2);
                    Files.copy(p2, target.resolve(file), new CopyOption[0]);
                }
            }
            finally {
                if (stream != null) {
                    stream.close();
                }
            }
        }
    }

    public List<String> enumerate() throws IOException {
        boolean debug = Iris.getIrisConfig().areDebugOptionsEnabled();
        Comparator baseComparator = String.CASE_INSENSITIVE_ORDER.thenComparing(Comparator.naturalOrder());
        Comparator comparator = (a, b) -> {
            if (debug) {
                if (Files.isDirectory(a, new LinkOption[0])) {
                    if (!Files.isDirectory(b, new LinkOption[0])) {
                        return -1;
                    }
                } else if (Files.isDirectory(b, new LinkOption[0]) && !Files.isDirectory(a, new LinkOption[0])) {
                    return 1;
                }
            }
            return baseComparator.compare(ShaderpackDirectoryManager.removeFormatting(a.getFileName().toString()), ShaderpackDirectoryManager.removeFormatting(b.getFileName().toString()));
        };
        try (Stream<Path> list = Files.list(this.root);){
            List<String> list2 = list.filter(Iris::isValidToShowPack).sorted(comparator).map(path -> path.getFileName().toString()).collect(Collectors.toList());
            return list2;
        }
    }

    public URI getDirectoryUri() {
        return this.root.toUri();
    }
}

