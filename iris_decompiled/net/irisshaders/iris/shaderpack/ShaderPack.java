/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.gson.Gson
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.stream.JsonReader
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  org.apache.commons.lang3.SystemUtils
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.shaderpack;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.features.FeatureFlags;
import net.irisshaders.iris.gl.texture.TextureDefinition;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.gui.FeatureMissingErrorScreen;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.irisshaders.iris.helpers.StringPair;
import net.irisshaders.iris.pathways.colorspace.ColorSpace;
import net.irisshaders.iris.shaderpack.DimensionId;
import net.irisshaders.iris.shaderpack.IdMap;
import net.irisshaders.iris.shaderpack.ImageInformation;
import net.irisshaders.iris.shaderpack.IrisDefines;
import net.irisshaders.iris.shaderpack.LanguageMap;
import net.irisshaders.iris.shaderpack.include.AbsolutePackPath;
import net.irisshaders.iris.shaderpack.include.IncludeGraph;
import net.irisshaders.iris.shaderpack.include.IncludeProcessor;
import net.irisshaders.iris.shaderpack.include.ShaderPackSourceNames;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.irisshaders.iris.shaderpack.option.OrderBackedProperties;
import net.irisshaders.iris.shaderpack.option.ProfileSet;
import net.irisshaders.iris.shaderpack.option.ShaderPackOptions;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuContainer;
import net.irisshaders.iris.shaderpack.option.values.MutableOptionValues;
import net.irisshaders.iris.shaderpack.preprocessor.JcppProcessor;
import net.irisshaders.iris.shaderpack.preprocessor.PropertiesPreprocessor;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import net.irisshaders.iris.shaderpack.programs.ProgramSetInterface;
import net.irisshaders.iris.shaderpack.properties.ShaderProperties;
import net.irisshaders.iris.shaderpack.texture.CustomTextureData;
import net.irisshaders.iris.shaderpack.texture.TextureFilteringData;
import net.irisshaders.iris.shaderpack.texture.TextureStage;
import net.irisshaders.iris.uniforms.custom.CustomUniforms;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.Nullable;

public class ShaderPack {
    private static final Gson GSON = new Gson();
    public final CustomUniforms.Builder customUniforms;
    private final ProgramSet base;
    private final Map<NamespacedId, ProgramSetInterface> overrides;
    private final IdMap idMap;
    private final LanguageMap languageMap;
    private final EnumMap<TextureStage, Object2ObjectMap<String, CustomTextureData>> customTextureDataMap = new EnumMap(TextureStage.class);
    private final Object2ObjectMap<String, CustomTextureData> irisCustomTextureDataMap = new Object2ObjectOpenHashMap();
    private final CustomTextureData customNoiseTexture;
    private final ShaderPackOptions shaderPackOptions;
    private final OptionMenuContainer menuContainer;
    private final ProfileSet.ProfileResult profile;
    private final String profileInfo;
    private final List<ImageInformation> irisCustomImages;
    private final Set<FeatureFlags> activeFeatures;
    private final Function<AbsolutePackPath, String> sourceProvider;
    private final ShaderProperties shaderProperties;
    private final List<String> dimensionIds;
    private Map<NamespacedId, String> dimensionMap;

    public ShaderPack(Path root, ImmutableList<StringPair> environmentDefines) throws IOException, IllegalStateException {
        this(root, Collections.emptyMap(), environmentDefines);
    }

    public ShaderPack(Path root, Map<String, String> changedConfigs, ImmutableList<StringPair> environmentDefines) throws IOException, IllegalStateException {
        List list;
        int i;
        Objects.requireNonNull(root);
        ArrayList<StringPair> envDefines1 = new ArrayList<StringPair>((Collection<StringPair>)environmentDefines);
        envDefines1.addAll((Collection<StringPair>)IrisDefines.createIrisReplacements());
        environmentDefines = ImmutableList.copyOf(envDefines1);
        ImmutableList.Builder starts = ImmutableList.builder();
        ImmutableList<String> potentialFileNames = ShaderPackSourceNames.POTENTIAL_STARTS;
        ShaderPackSourceNames.findPresentSources((ImmutableList.Builder<AbsolutePackPath>)starts, root, AbsolutePackPath.fromAbsolutePath("/"), potentialFileNames);
        this.dimensionIds = new ArrayList<String>();
        boolean[] hasDimensionIds = new boolean[]{false};
        List dimensionIdCreator = ShaderPack.loadProperties(root, "dimension.properties", (Iterable<StringPair>)environmentDefines).map(dimensionProperties -> {
            hasDimensionIds[0] = !dimensionProperties.isEmpty();
            this.dimensionMap = ShaderPack.parseDimensionMap(dimensionProperties, "dimension.", "dimension.properties");
            return this.parseDimensionIds((Properties)dimensionProperties, "dimension.");
        }).orElse(new ArrayList());
        if (!hasDimensionIds[0]) {
            this.dimensionMap = new Object2ObjectArrayMap();
            if (Files.exists(root.resolve("world0"), new LinkOption[0])) {
                dimensionIdCreator.add("world0");
                this.dimensionMap.putIfAbsent(DimensionId.OVERWORLD, "world0");
                this.dimensionMap.putIfAbsent(new NamespacedId("*", "*"), "world0");
            }
            if (Files.exists(root.resolve("world-1"), new LinkOption[0])) {
                dimensionIdCreator.add("world-1");
                this.dimensionMap.putIfAbsent(DimensionId.NETHER, "world-1");
            }
            if (Files.exists(root.resolve("world1"), new LinkOption[0])) {
                dimensionIdCreator.add("world1");
                this.dimensionMap.putIfAbsent(DimensionId.END, "world1");
            }
        }
        for (String id : dimensionIdCreator) {
            if (!ShaderPackSourceNames.findPresentSources((ImmutableList.Builder<AbsolutePackPath>)starts, root, AbsolutePackPath.fromAbsolutePath("/" + id), potentialFileNames)) continue;
            this.dimensionIds.add(id);
        }
        IncludeGraph graph = new IncludeGraph(root, (ImmutableList<AbsolutePackPath>)starts.build());
        if (!graph.getFailures().isEmpty()) {
            graph.getFailures().forEach((path, error) -> Iris.logger.error("{}", error.toString()));
            throw new IOException("Failed to resolve some #include directives, see previous messages for details");
        }
        this.languageMap = new LanguageMap(root.resolve("lang"));
        this.shaderPackOptions = new ShaderPackOptions(graph, changedConfigs);
        graph = this.shaderPackOptions.getIncludes();
        ArrayList<StringPair> finalEnvironmentDefines = new ArrayList<StringPair>(List.copyOf((Collection)environmentDefines));
        for (FeatureFlags featureFlags : FeatureFlags.values()) {
            if (!featureFlags.isUsable()) continue;
            if (featureFlags == FeatureFlags.TESSELLATION_SHADERS) {
                finalEnvironmentDefines.add(new StringPair("IRIS_FEATURE_TESSELATION_SHADERS", ""));
            }
            finalEnvironmentDefines.add(new StringPair("IRIS_FEATURE_" + featureFlags.name(), ""));
        }
        this.shaderProperties = ShaderPack.loadProperties(root, "shaders.properties").map(source -> new ShaderProperties((String)source, this.shaderPackOptions, (Iterable<StringPair>)finalEnvironmentDefines)).orElseGet(ShaderProperties::empty);
        this.activeFeatures = new HashSet<FeatureFlags>();
        for (i = 0; i < this.shaderProperties.getRequiredFeatureFlags().size(); ++i) {
            this.activeFeatures.add(FeatureFlags.getValue(this.shaderProperties.getRequiredFeatureFlags().get(i)));
        }
        for (i = 0; i < this.shaderProperties.getOptionalFeatureFlags().size(); ++i) {
            this.activeFeatures.add(FeatureFlags.getValue(this.shaderProperties.getOptionalFeatureFlags().get(i)));
        }
        if (!this.activeFeatures.contains((Object)FeatureFlags.SSBO) && !this.shaderProperties.getBufferObjects().isEmpty()) {
            throw new IllegalStateException("An SSBO is being used, but the feature flag for SSBO's hasn't been set! Please set either a requirement or check for the SSBO feature using \"iris.features.required/optional = ssbo\".");
        }
        if (!this.activeFeatures.contains((Object)FeatureFlags.CUSTOM_IMAGES) && !this.shaderProperties.getIrisCustomImages().isEmpty()) {
            throw new IllegalStateException("Custom images are being used, but the feature flag for custom images hasn't been set! Please set either a requirement or check for custom images' feature flag using \"iris.features.required/optional = CUSTOM_IMAGES\".");
        }
        List<FeatureFlags> invalidFlagList = this.shaderProperties.getRequiredFeatureFlags().stream().filter(FeatureFlags::isInvalid).map(FeatureFlags::getValue).collect(Collectors.toList());
        List invalidFeatureFlags = invalidFlagList.stream().map(FeatureFlags::getHumanReadableName).toList();
        if (!invalidFeatureFlags.isEmpty()) {
            if (Minecraft.m_91087_().f_91080_ instanceof ShaderPackScreen) {
                MutableComponent component = Component.m_237110_((String)"iris.unsupported.pack.description", (Object[])new Object[]{FeatureFlags.getInvalidStatus(invalidFlagList), invalidFeatureFlags.stream().collect(Collectors.joining(", ", ": ", "."))});
                if (SystemUtils.IS_OS_MAC) {
                    component = component.m_7220_((Component)Component.m_237115_((String)"iris.unsupported.pack.macos"));
                }
                Minecraft.m_91087_().m_91152_((Screen)new FeatureMissingErrorScreen(Minecraft.m_91087_().f_91080_, (Component)Component.m_237115_((String)"iris.unsupported.pack"), (Component)component));
            }
            IrisApi.getInstance().getConfig().setShadersEnabledAndApply(false);
        }
        ArrayList<StringPair> newEnvDefines = new ArrayList<StringPair>((Collection<StringPair>)environmentDefines);
        if (this.shaderProperties.supportsColorCorrection().orElse(false)) {
            for (ColorSpace space : ColorSpace.values()) {
                newEnvDefines.add(new StringPair("COLOR_SPACE_" + space.name(), String.valueOf(space.ordinal())));
            }
        }
        if (!(list = this.shaderProperties.getOptionalFeatureFlags().stream().filter(flag -> !FeatureFlags.isInvalid(flag)).toList()).isEmpty()) {
            list.forEach(flag -> Iris.logger.warn("Found flag " + flag));
            list.forEach(flag -> newEnvDefines.add(new StringPair("IRIS_FEATURE_" + flag, "")));
        }
        environmentDefines = ImmutableList.copyOf(newEnvDefines);
        ProfileSet profiles = ProfileSet.fromTree(this.shaderProperties.getProfiles(), this.shaderPackOptions.getOptionSet());
        this.profile = profiles.scan(this.shaderPackOptions.getOptionSet(), this.shaderPackOptions.getOptionValues());
        ArrayList disabledPrograms = new ArrayList();
        this.profile.current.ifPresent(profile -> disabledPrograms.addAll(profile.disabledPrograms));
        this.shaderProperties.getConditionallyEnabledPrograms().forEach((program, shaderOption) -> {
            if ("true".equals(shaderOption)) {
                return;
            }
            if ("false".equals(shaderOption) || !this.shaderPackOptions.getOptionValues().getBooleanValueOrDefault((String)shaderOption)) {
                disabledPrograms.add(program);
            }
        });
        this.menuContainer = new OptionMenuContainer(this.shaderProperties, this.shaderPackOptions, profiles);
        String profileName = this.getCurrentProfileName();
        MutableOptionValues profileOptions = new MutableOptionValues(this.shaderPackOptions.getOptionSet(), this.profile.current.map(p -> p.optionValues).orElse(new HashMap()));
        int userOptionsChanged = this.shaderPackOptions.getOptionValues().getOptionsChanged() - profileOptions.getOptionsChanged();
        this.profileInfo = "Profile: " + profileName + " (+" + userOptionsChanged + " option" + (userOptionsChanged == 1 ? "" : "s") + " changed by user)";
        Iris.logger.info(this.profileInfo);
        IncludeProcessor includeProcessor = new IncludeProcessor(graph);
        ImmutableList finalEnvironmentDefines1 = environmentDefines;
        this.sourceProvider = arg_0 -> ShaderPack.lambda$new$9(disabledPrograms, includeProcessor, (Iterable)finalEnvironmentDefines1, arg_0);
        this.base = new ProgramSet(AbsolutePackPath.fromAbsolutePath("/" + this.dimensionMap.getOrDefault(new NamespacedId("*", "*"), "")), this.sourceProvider, this.shaderProperties, this);
        this.overrides = new HashMap<NamespacedId, ProgramSetInterface>();
        this.idMap = new IdMap(root, this.shaderPackOptions, (Iterable<StringPair>)environmentDefines);
        this.customNoiseTexture = this.shaderProperties.getNoiseTexturePath().map(path -> {
            try {
                return this.readTexture(root, new TextureDefinition.PNGDefinition((String)path));
            }
            catch (IOException e) {
                Iris.logger.error("Unable to read the custom noise texture at " + path, e);
                return null;
            }
        }).orElse(null);
        this.shaderProperties.getCustomTextures().forEach((textureStage, customTexturePropertiesMap) -> {
            Object2ObjectOpenHashMap innerCustomTextureDataMap = new Object2ObjectOpenHashMap();
            customTexturePropertiesMap.forEach((arg_0, arg_1) -> this.lambda$new$11((Object2ObjectMap)innerCustomTextureDataMap, root, arg_0, arg_1));
            this.customTextureDataMap.put((TextureStage)((Object)((Object)textureStage)), (Object2ObjectMap<String, CustomTextureData>)innerCustomTextureDataMap);
        });
        this.irisCustomImages = this.shaderProperties.getIrisCustomImages();
        this.customUniforms = this.shaderProperties.getCustomUniforms();
        this.shaderProperties.getIrisCustomTextures().forEach((name, texture) -> {
            try {
                this.irisCustomTextureDataMap.put(name, (Object)this.readTexture(root, (TextureDefinition)texture));
            }
            catch (IOException e) {
                Iris.logger.error("Unable to read the custom texture at " + texture.getName(), e);
            }
        });
    }

    private static Optional<Properties> loadProperties(Path shaderPath, String name, Iterable<StringPair> environmentDefines) {
        String fileContents = ShaderPack.readProperties(shaderPath, name);
        if (fileContents == null) {
            return Optional.empty();
        }
        String processed = PropertiesPreprocessor.preprocessSource(fileContents, environmentDefines);
        StringReader propertiesReader = new StringReader(processed);
        OrderBackedProperties properties = new OrderBackedProperties();
        try {
            properties.load(propertiesReader);
        }
        catch (IOException e) {
            Iris.logger.error("Error loading " + name + " at " + shaderPath, e);
            return Optional.empty();
        }
        return Optional.of(properties);
    }

    private static Map<NamespacedId, String> parseDimensionMap(Properties properties, String keyPrefix, String fileName) {
        Object2ObjectArrayMap overrides = new Object2ObjectArrayMap();
        properties.forEach((arg_0, arg_1) -> ShaderPack.lambda$parseDimensionMap$14(keyPrefix, (Map)overrides, arg_0, arg_1));
        return overrides;
    }

    @Nullable
    private static ProgramSet loadOverrides(boolean has, AbsolutePackPath path, Function<AbsolutePackPath, String> sourceProvider, ShaderProperties shaderProperties, ShaderPack pack) {
        if (has) {
            return new ProgramSet(path, sourceProvider, shaderProperties, pack);
        }
        return null;
    }

    private static Optional<String> loadProperties(Path shaderPath, String name) {
        String fileContents = ShaderPack.readProperties(shaderPath, name);
        if (fileContents == null) {
            return Optional.empty();
        }
        return Optional.of(fileContents);
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

    private List<String> parseDimensionIds(Properties dimensionProperties, String keyPrefix) {
        ArrayList<String> names = new ArrayList<String>();
        dimensionProperties.forEach((keyObject, value) -> {
            String key = (String)keyObject;
            if (!key.startsWith(keyPrefix)) {
                return;
            }
            key = key.substring(keyPrefix.length());
            names.add(key);
        });
        return names;
    }

    private String getCurrentProfileName() {
        return this.profile.current.map(p -> p.name).orElse("Custom");
    }

    public String getProfileInfo() {
        return this.profileInfo;
    }

    public CustomTextureData readTexture(Path root, TextureDefinition definition) throws IOException {
        CustomTextureData customTextureData;
        String path = definition.getName();
        if (path.contains(":")) {
            String[] parts = path.split(":");
            if (parts.length > 2) {
                Iris.logger.warn("Resource location " + path + " contained more than two parts?");
            }
            customTextureData = parts[0].equals("minecraft") && (parts[1].equals("dynamic/lightmap_1") || parts[1].equals("dynamic/light_map_1")) ? new CustomTextureData.LightmapMarker() : new CustomTextureData.ResourceData(parts[0], parts[1]);
        } else {
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            boolean blur = definition instanceof TextureDefinition.RawDefinition;
            boolean clamp = definition instanceof TextureDefinition.RawDefinition;
            String mcMetaPath = path + ".mcmeta";
            Path mcMetaResolvedPath = root.resolve(mcMetaPath);
            if (Files.exists(mcMetaResolvedPath, new LinkOption[0])) {
                try {
                    JsonObject meta = this.loadMcMeta(mcMetaResolvedPath);
                    if (meta.get("texture") != null) {
                        if (meta.get("texture").getAsJsonObject().get("blur") != null) {
                            blur = meta.get("texture").getAsJsonObject().get("blur").getAsBoolean();
                        }
                        if (meta.get("texture").getAsJsonObject().get("clamp") != null) {
                            clamp = meta.get("texture").getAsJsonObject().get("clamp").getAsBoolean();
                        }
                    }
                }
                catch (IOException e) {
                    Iris.logger.error("Unable to read the custom texture mcmeta at " + mcMetaPath + ", ignoring: " + e);
                }
            }
            byte[] content = Files.readAllBytes(root.resolve(path));
            if (definition instanceof TextureDefinition.PNGDefinition) {
                customTextureData = new CustomTextureData.PngData(new TextureFilteringData(blur, clamp), content);
            } else if (definition instanceof TextureDefinition.RawDefinition) {
                TextureDefinition.RawDefinition rawDefinition = (TextureDefinition.RawDefinition)definition;
                customTextureData = switch (rawDefinition.getTarget()) {
                    case TextureType.TEXTURE_1D -> new CustomTextureData.RawData1D(content, new TextureFilteringData(blur, clamp), rawDefinition.getInternalFormat(), rawDefinition.getFormat(), rawDefinition.getPixelType(), rawDefinition.getSizeX());
                    case TextureType.TEXTURE_2D -> new CustomTextureData.RawData2D(content, new TextureFilteringData(blur, clamp), rawDefinition.getInternalFormat(), rawDefinition.getFormat(), rawDefinition.getPixelType(), rawDefinition.getSizeX(), rawDefinition.getSizeY());
                    case TextureType.TEXTURE_3D -> new CustomTextureData.RawData3D(content, new TextureFilteringData(blur, clamp), rawDefinition.getInternalFormat(), rawDefinition.getFormat(), rawDefinition.getPixelType(), rawDefinition.getSizeX(), rawDefinition.getSizeY(), rawDefinition.getSizeZ());
                    case TextureType.TEXTURE_RECTANGLE -> new CustomTextureData.RawDataRect(content, new TextureFilteringData(blur, clamp), rawDefinition.getInternalFormat(), rawDefinition.getFormat(), rawDefinition.getPixelType(), rawDefinition.getSizeX(), rawDefinition.getSizeY());
                    default -> throw new IllegalStateException("Unknown texture type: " + rawDefinition.getTarget());
                };
            } else {
                customTextureData = null;
            }
        }
        return customTextureData;
    }

    private JsonObject loadMcMeta(Path mcMetaPath) throws IOException, JsonParseException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(mcMetaPath, new OpenOption[0]), StandardCharsets.UTF_8));){
            JsonReader jsonReader = new JsonReader((Reader)reader);
            JsonObject jsonObject = (JsonObject)GSON.getAdapter(JsonObject.class).read(jsonReader);
            return jsonObject;
        }
    }

    public ProgramSet getProgramSet(NamespacedId dimension) {
        ProgramSetInterface overrides = this.overrides.computeIfAbsent(dimension, dim -> {
            if (this.dimensionMap.containsKey(dimension)) {
                String name = this.dimensionMap.get(dimension);
                if (this.dimensionIds.contains(name)) {
                    return new ProgramSet(AbsolutePackPath.fromAbsolutePath("/" + name), this.sourceProvider, this.shaderProperties, this);
                }
                Iris.logger.error("Attempted to load dimension folder " + name + " for dimension " + dimension + ", but it does not exist!");
                return ProgramSetInterface.Empty.INSTANCE;
            }
            return ProgramSetInterface.Empty.INSTANCE;
        });
        if (overrides instanceof ProgramSet) {
            return (ProgramSet)overrides;
        }
        return this.base;
    }

    public IdMap getIdMap() {
        return this.idMap;
    }

    public EnumMap<TextureStage, Object2ObjectMap<String, CustomTextureData>> getCustomTextureDataMap() {
        return this.customTextureDataMap;
    }

    public List<ImageInformation> getIrisCustomImages() {
        return this.irisCustomImages;
    }

    public Object2ObjectMap<String, CustomTextureData> getIrisCustomTextureDataMap() {
        return this.irisCustomTextureDataMap;
    }

    public Optional<CustomTextureData> getCustomNoiseTexture() {
        return Optional.ofNullable(this.customNoiseTexture);
    }

    public LanguageMap getLanguageMap() {
        return this.languageMap;
    }

    public ShaderPackOptions getShaderPackOptions() {
        return this.shaderPackOptions;
    }

    public OptionMenuContainer getMenuContainer() {
        return this.menuContainer;
    }

    public boolean hasFeature(FeatureFlags feature) {
        return this.activeFeatures.contains((Object)feature);
    }

    private static /* synthetic */ void lambda$parseDimensionMap$14(String keyPrefix, Map overrides, Object keyObject, Object valueObject) {
        String key = (String)keyObject;
        String value = (String)valueObject;
        if (!key.startsWith(keyPrefix)) {
            return;
        }
        key = key.substring(keyPrefix.length());
        for (String part : value.split("\\s+")) {
            if (part.equals("*")) {
                overrides.put(new NamespacedId("*", "*"), key);
            }
            overrides.put(new NamespacedId(part), key);
        }
    }

    private /* synthetic */ void lambda$new$11(Object2ObjectMap innerCustomTextureDataMap, Path root, String samplerName, TextureDefinition path) {
        try {
            innerCustomTextureDataMap.put((Object)samplerName, (Object)this.readTexture(root, path));
        }
        catch (IOException e) {
            Iris.logger.error("Unable to read the custom texture at " + path, e);
        }
    }

    private static /* synthetic */ String lambda$new$9(List disabledPrograms, IncludeProcessor includeProcessor, Iterable finalEnvironmentDefines1, AbsolutePackPath path) {
        String pathString;
        String programString = pathString.substring((pathString = path.getPathString()).indexOf("/") == 0 ? 1 : 0, pathString.lastIndexOf("."));
        if (disabledPrograms.contains(programString)) {
            return null;
        }
        ImmutableList<String> lines = includeProcessor.getIncludedFile(path);
        if (lines == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            builder.append(line);
            builder.append('\n');
        }
        String source = builder.toString();
        source = JcppProcessor.glslPreprocessSource(source, finalEnvironmentDefines1);
        return source;
    }
}

