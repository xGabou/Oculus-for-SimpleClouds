/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.loading.FMLPaths
 *  org.apache.commons.io.FilenameUtils
 */
package net.irisshaders.iris.pipeline.transform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pipeline.transform.PatchShaderType;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.FilenameUtils;

public class ShaderPrinter {
    private static final Path debugOutDir = FMLPaths.GAMEDIR.get().resolve("patched_shaders");
    private static boolean outputLocationCleared = false;
    private static int programCounter = 0;

    public static void resetPrintState() {
        outputLocationCleared = false;
        programCounter = 0;
    }

    public static void deleteIfClearing() {
        if (!outputLocationCleared) {
            try {
                if (Files.exists(debugOutDir, new LinkOption[0])) {
                    try (Stream<Path> stream = Files.list(debugOutDir);){
                        stream.forEach(path -> {
                            try {
                                Files.delete(path);
                            }
                            catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                }
                Files.createDirectories(debugOutDir, new FileAttribute[0]);
            }
            catch (IOException e) {
                Iris.logger.warn("Failed to initialize debug patched shader source location", e);
            }
            outputLocationCleared = true;
        }
    }

    public static ProgramPrintBuilder printProgram(String name) {
        return new ProgramPrintBuilder(name);
    }

    public static class ProgramPrintBuilder {
        private final boolean isActive = Iris.getIrisConfig().areDebugOptionsEnabled();
        private final String prefix = this.isActive ? String.format("%03d_", ++programCounter) : null;
        private final List<String> sources = this.isActive ? new ArrayList(PatchShaderType.values().length * 2) : null;
        private String name;
        private boolean done = false;

        public ProgramPrintBuilder(String name) {
            this.setName(name);
        }

        public ProgramPrintBuilder setName(String name) {
            this.name = name;
            return this;
        }

        private void addItem(String extension, String content) {
            if (content != null && this.sources != null) {
                this.sources.add(this.prefix + this.name + extension);
                this.sources.add(content);
            }
        }

        public ProgramPrintBuilder addSource(PatchShaderType type, String source) {
            if (this.sources == null) {
                return this;
            }
            this.addItem(type.extension, source);
            return this;
        }

        public ProgramPrintBuilder addSources(Map<PatchShaderType, String> sources) {
            if (sources == null) {
                return this;
            }
            for (Map.Entry<PatchShaderType, String> entry : sources.entrySet()) {
                this.addSource(entry.getKey(), entry.getValue());
            }
            return this;
        }

        public ProgramPrintBuilder addJson(String json) {
            if (this.sources == null) {
                return this;
            }
            this.addItem(".json", json);
            return this;
        }

        public void print() {
            if (this.done) {
                return;
            }
            this.done = true;
            if (this.isActive) {
                if (!outputLocationCleared) {
                    try {
                        if (Files.exists(debugOutDir, new LinkOption[0])) {
                            try (Stream<Path> stream = Files.list(debugOutDir).filter(s -> !FilenameUtils.getExtension((String)s.toString()).contains("properties"));){
                                stream.forEach(path -> {
                                    try {
                                        Files.delete(path);
                                    }
                                    catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                            }
                        }
                        Files.createDirectories(debugOutDir, new FileAttribute[0]);
                    }
                    catch (IOException e) {
                        Iris.logger.warn("Failed to initialize debug patched shader source location", e);
                    }
                    outputLocationCleared = true;
                }
                try {
                    for (int i = 0; i < this.sources.size(); i += 2) {
                        Files.writeString((Path)debugOutDir.resolve(this.sources.get(i)), (CharSequence)this.sources.get(i + 1), (OpenOption[])new OpenOption[0]);
                    }
                }
                catch (IOException e) {
                    Iris.logger.warn("Failed to write debug patched shader source", e);
                }
            }
        }
    }
}

