/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package net.irisshaders.iris.shaderpack;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;
import net.irisshaders.iris.Iris;

public class LanguageMap {
    private final Map<String, Map<String, String>> translationMaps = new HashMap<String, Map<String, String>>();

    public LanguageMap(Path root) throws IOException {
        if (!Files.exists(root, new LinkOption[0])) {
            return;
        }
        try (Stream<Path> stream = Files.list(root);){
            stream.filter(path -> !Files.isDirectory(path, new LinkOption[0])).forEach(path -> {
                String currentFileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
                if (!currentFileName.endsWith(".lang")) {
                    return;
                }
                String currentLangCode = currentFileName.substring(0, currentFileName.lastIndexOf("."));
                Properties properties = new Properties();
                try (InputStreamReader isr = new InputStreamReader(Files.newInputStream(path, new OpenOption[0]), StandardCharsets.UTF_8);){
                    properties.load(isr);
                }
                catch (IOException e) {
                    Iris.logger.error("Failed to parse shader pack language file " + path, e);
                }
                ImmutableMap.Builder builder = ImmutableMap.builder();
                properties.forEach((key, value) -> builder.put((Object)key.toString(), (Object)value.toString()));
                this.translationMaps.put(currentLangCode, (Map<String, String>)builder.build());
            });
        }
    }

    public Set<String> getLanguages() {
        return Collections.unmodifiableSet(this.translationMaps.keySet());
    }

    public Map<String, String> getTranslations(String language) {
        return this.translationMaps.get(language);
    }
}

