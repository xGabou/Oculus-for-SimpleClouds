/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMaps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2IntFunction
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  net.minecraftforge.fml.loading.FMLPaths
 */
package net.irisshaders.iris.shaderpack;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.helpers.StringPair;
import net.irisshaders.iris.pipeline.transform.ShaderPrinter;
import net.irisshaders.iris.shaderpack.materialmap.BlockEntry;
import net.irisshaders.iris.shaderpack.materialmap.BlockRenderType;
import net.irisshaders.iris.shaderpack.materialmap.LegacyIdMap;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.irisshaders.iris.shaderpack.option.OrderBackedProperties;
import net.irisshaders.iris.shaderpack.option.ShaderPackOptions;
import net.irisshaders.iris.shaderpack.preprocessor.PropertiesPreprocessor;
import net.minecraftforge.fml.loading.FMLPaths;

public class IdMap {
    private final Object2IntMap<NamespacedId> itemIdMap;
    private final Object2IntMap<NamespacedId> entityIdMap;
    private Int2ObjectMap<List<BlockEntry>> blockPropertiesMap;
    private Map<NamespacedId, BlockRenderType> blockRenderTypeMap;

    IdMap(Path shaderPath, ShaderPackOptions shaderPackOptions, Iterable<StringPair> environmentDefines) {
        this.itemIdMap = IdMap.loadProperties(shaderPath, "item.properties", shaderPackOptions, environmentDefines).map(IdMap::parseItemIdMap).orElse(Object2IntMaps.emptyMap());
        this.entityIdMap = IdMap.loadProperties(shaderPath, "entity.properties", shaderPackOptions, environmentDefines).map(IdMap::parseEntityIdMap).orElse(Object2IntMaps.emptyMap());
        IdMap.loadProperties(shaderPath, "block.properties", shaderPackOptions, environmentDefines).ifPresent(blockProperties -> {
            this.blockPropertiesMap = IdMap.parseBlockMap(blockProperties, "block.", "block.properties");
            this.blockRenderTypeMap = IdMap.parseRenderTypeMap(blockProperties, "layer.", "block.properties");
        });
        if (this.blockPropertiesMap == null) {
            this.blockPropertiesMap = new Int2ObjectOpenHashMap();
            LegacyIdMap.addLegacyValues(this.blockPropertiesMap);
        }
        if (this.blockRenderTypeMap == null) {
            this.blockRenderTypeMap = Collections.emptyMap();
        }
    }

    private static Optional<Properties> loadProperties(Path shaderPath, String name, ShaderPackOptions shaderPackOptions, Iterable<StringPair> environmentDefines) {
        String fileContents = IdMap.readProperties(shaderPath, name);
        if (fileContents == null) {
            return Optional.empty();
        }
        String processed = PropertiesPreprocessor.preprocessSource(fileContents, shaderPackOptions, environmentDefines).replaceAll("\\\\\\n\\s*\\n", " ").replaceAll("\\S *block\\.", "\nblock.");
        StringReader propertiesReader = new StringReader(processed);
        OrderBackedProperties properties = new OrderBackedProperties();
        try {
            properties.load(propertiesReader);
        }
        catch (IOException e) {
            Iris.logger.error("Error loading " + name + " at " + shaderPath, e);
            return Optional.empty();
        }
        if (Iris.getIrisConfig().areDebugOptionsEnabled()) {
            ShaderPrinter.deleteIfClearing();
            try (OutputStream os = Files.newOutputStream(FMLPaths.GAMEDIR.get().resolve("patched_shaders").resolve(name), new OpenOption[0]);){
                ((Properties)properties).store(new OutputStreamWriter(os, StandardCharsets.UTF_8), "Patched version of properties");
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.of(properties);
    }

    private static String readProperties(Path shaderPath, String name) {
        try {
            return Files.readString((Path)shaderPath.resolve(name), (Charset)StandardCharsets.ISO_8859_1);
        }
        catch (NoSuchFileException e) {
            Iris.logger.debug("An " + name + " file was not found in the current shaderpack");
            return null;
        }
        catch (IOException e) {
            Iris.logger.error("An IOException occurred reading " + name + " from the current shaderpack", e);
            return null;
        }
    }

    private static Object2IntMap<NamespacedId> parseItemIdMap(Properties properties) {
        return IdMap.parseIdMap(properties, "item.", "item.properties");
    }

    private static Object2IntMap<NamespacedId> parseEntityIdMap(Properties properties) {
        return IdMap.parseIdMap(properties, "entity.", "entity.properties");
    }

    private static Object2IntMap<NamespacedId> parseIdMap(Properties properties, String keyPrefix, String fileName) {
        Object2IntOpenHashMap idMap = new Object2IntOpenHashMap();
        idMap.defaultReturnValue(-1);
        properties.forEach((arg_0, arg_1) -> IdMap.lambda$parseIdMap$1(keyPrefix, fileName, (Object2IntMap)idMap, arg_0, arg_1));
        return Object2IntMaps.unmodifiable((Object2IntMap)idMap);
    }

    private static Int2ObjectMap<List<BlockEntry>> parseBlockMap(Properties properties, String keyPrefix, String fileName) {
        Int2ObjectOpenHashMap entriesById = new Int2ObjectOpenHashMap();
        properties.forEach((arg_0, arg_1) -> IdMap.lambda$parseBlockMap$2(keyPrefix, fileName, (Int2ObjectMap)entriesById, arg_0, arg_1));
        return Int2ObjectMaps.unmodifiable((Int2ObjectMap)entriesById);
    }

    private static Map<NamespacedId, BlockRenderType> parseRenderTypeMap(Properties properties, String keyPrefix, String fileName) {
        HashMap<NamespacedId, BlockRenderType> overrides = new HashMap<NamespacedId, BlockRenderType>();
        properties.forEach((keyObject, valueObject) -> {
            String key = (String)keyObject;
            String value = (String)valueObject;
            if (!key.startsWith(keyPrefix)) {
                return;
            }
            String keyWithoutPrefix = key.substring(keyPrefix.length());
            BlockRenderType renderType = BlockRenderType.fromString(keyWithoutPrefix).orElse(null);
            if (renderType == null) {
                Iris.logger.warn("Failed to parse line in " + fileName + ": invalid block render type: " + key);
                return;
            }
            for (String part : value.split("\\s+")) {
                overrides.put(new NamespacedId(part), renderType);
            }
        });
        return overrides;
    }

    private static Map<NamespacedId, String> parseDimensionMap(Properties properties, String keyPrefix, String fileName) {
        Object2ObjectArrayMap overrides = new Object2ObjectArrayMap();
        properties.forEach((arg_0, arg_1) -> IdMap.lambda$parseDimensionMap$4(keyPrefix, (Map)overrides, arg_0, arg_1));
        return overrides;
    }

    public Int2ObjectMap<List<BlockEntry>> getBlockProperties() {
        return this.blockPropertiesMap;
    }

    public Object2IntFunction<NamespacedId> getItemIdMap() {
        return this.itemIdMap;
    }

    public Object2IntFunction<NamespacedId> getEntityIdMap() {
        return this.entityIdMap;
    }

    public Map<NamespacedId, BlockRenderType> getBlockRenderTypeMap() {
        return this.blockRenderTypeMap;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        IdMap idMap = (IdMap)o;
        return Objects.equals(this.itemIdMap, idMap.itemIdMap) && Objects.equals(this.entityIdMap, idMap.entityIdMap) && Objects.equals(this.blockPropertiesMap, idMap.blockPropertiesMap) && Objects.equals(this.blockRenderTypeMap, idMap.blockRenderTypeMap);
    }

    public int hashCode() {
        return Objects.hash(this.itemIdMap, this.entityIdMap, this.blockPropertiesMap, this.blockRenderTypeMap);
    }

    private static /* synthetic */ void lambda$parseDimensionMap$4(String keyPrefix, Map overrides, Object keyObject, Object valueObject) {
        String key = (String)keyObject;
        String value = (String)valueObject;
        if (!key.startsWith(keyPrefix)) {
            return;
        }
        key = key.substring(keyPrefix.length());
        for (String part : value.split("\\s+")) {
            overrides.put(new NamespacedId(part), key);
        }
    }

    private static /* synthetic */ void lambda$parseBlockMap$2(String keyPrefix, String fileName, Int2ObjectMap entriesById, Object keyObject, Object valueObject) {
        int intId;
        String key = (String)keyObject;
        String value = (String)valueObject;
        if (!key.startsWith(keyPrefix)) {
            return;
        }
        try {
            intId = Integer.parseInt(key.substring(keyPrefix.length()));
        }
        catch (NumberFormatException e) {
            Iris.logger.warn("Failed to parse line in " + fileName + ": invalid key " + key);
            return;
        }
        ArrayList<BlockEntry> entries = new ArrayList<BlockEntry>();
        for (String part : value.split("\\s+")) {
            if (part.isEmpty()) continue;
            try {
                entries.add(BlockEntry.parse(part));
            }
            catch (Exception e) {
                Iris.logger.warn("Unexpected error while parsing an entry from " + fileName + " for the key " + key + ":", e);
            }
        }
        entriesById.put(intId, Collections.unmodifiableList(entries));
    }

    private static /* synthetic */ void lambda$parseIdMap$1(String keyPrefix, String fileName, Object2IntMap idMap, Object keyObject, Object valueObject) {
        int intId;
        String key = (String)keyObject;
        String value = (String)valueObject;
        if (!key.startsWith(keyPrefix)) {
            return;
        }
        try {
            intId = Integer.parseInt(key.substring(keyPrefix.length()));
        }
        catch (NumberFormatException e) {
            Iris.logger.warn("Failed to parse line in " + fileName + ": invalid key " + key);
            return;
        }
        for (String part : value.split("\\s+")) {
            if (part.contains("=")) {
                Iris.logger.warn("Failed to parse an ResourceLocation in " + fileName + " for the key " + key + ": state properties are currently not supported: " + part);
                continue;
            }
            idMap.put((Object)new NamespacedId(part), intId);
        }
    }
}

