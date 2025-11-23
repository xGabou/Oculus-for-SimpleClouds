/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraftforge.fml.loading.FMLPaths
 */
package net.irisshaders.iris.shaderpack.properties;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.blending.AlphaTest;
import net.irisshaders.iris.gl.blending.AlphaTestFunction;
import net.irisshaders.iris.gl.blending.BlendMode;
import net.irisshaders.iris.gl.blending.BlendModeFunction;
import net.irisshaders.iris.gl.blending.BlendModeOverride;
import net.irisshaders.iris.gl.blending.BufferBlendInformation;
import net.irisshaders.iris.gl.buffer.ShaderStorageInfo;
import net.irisshaders.iris.gl.framebuffer.ViewportData;
import net.irisshaders.iris.gl.texture.InternalTextureFormat;
import net.irisshaders.iris.gl.texture.PixelFormat;
import net.irisshaders.iris.gl.texture.PixelType;
import net.irisshaders.iris.gl.texture.TextureDefinition;
import net.irisshaders.iris.gl.texture.TextureScaleOverride;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.helpers.OptionalBoolean;
import net.irisshaders.iris.helpers.StringPair;
import net.irisshaders.iris.helpers.Tri;
import net.irisshaders.iris.shaderpack.ImageInformation;
import net.irisshaders.iris.shaderpack.option.OrderBackedProperties;
import net.irisshaders.iris.shaderpack.option.ShaderPackOptions;
import net.irisshaders.iris.shaderpack.preprocessor.PropertiesPreprocessor;
import net.irisshaders.iris.shaderpack.properties.CloudSetting;
import net.irisshaders.iris.shaderpack.properties.IndirectPointer;
import net.irisshaders.iris.shaderpack.properties.PackRenderTargetDirectives;
import net.irisshaders.iris.shaderpack.properties.ParticleRenderingSettings;
import net.irisshaders.iris.shaderpack.properties.ShadowCullState;
import net.irisshaders.iris.shaderpack.texture.TextureStage;
import net.irisshaders.iris.uniforms.custom.CustomUniforms;
import net.minecraftforge.fml.loading.FMLPaths;

public class ShaderProperties {
    private final Map<String, List<String>> profiles = new LinkedHashMap<String, List<String>>();
    private final Map<String, List<String>> subScreenOptions = new HashMap<String, List<String>>();
    private final Map<String, Integer> subScreenColumnCount = new HashMap<String, Integer>();
    private final Object2ObjectMap<String, AlphaTest> alphaTestOverrides = new Object2ObjectOpenHashMap();
    private final Object2ObjectMap<String, ViewportData> viewportScaleOverrides = new Object2ObjectOpenHashMap();
    private final Object2ObjectMap<String, TextureScaleOverride> textureScaleOverrides = new Object2ObjectOpenHashMap();
    private final Object2ObjectMap<String, BlendModeOverride> blendModeOverrides = new Object2ObjectOpenHashMap();
    private final Object2ObjectMap<String, IndirectPointer> indirectPointers = new Object2ObjectOpenHashMap();
    private final Object2ObjectMap<String, ArrayList<BufferBlendInformation>> bufferBlendOverrides = new Object2ObjectOpenHashMap();
    private final EnumMap<TextureStage, Object2ObjectMap<String, TextureDefinition>> customTextures = new EnumMap(TextureStage.class);
    private final Object2ObjectMap<Tri<String, TextureType, TextureStage>, String> customTexturePatching = new Object2ObjectOpenHashMap();
    private final Object2ObjectMap<String, TextureDefinition> irisCustomTextures = new Object2ObjectOpenHashMap();
    private final List<ImageInformation> irisCustomImages = new ArrayList<ImageInformation>();
    private final Int2ObjectArrayMap<ShaderStorageInfo> bufferObjects = new Int2ObjectArrayMap();
    private final Object2ObjectMap<String, Object2BooleanMap<String>> explicitFlips = new Object2ObjectOpenHashMap();
    private final Object2ObjectMap<String, String> conditionallyEnabledPrograms = new Object2ObjectOpenHashMap();
    CustomUniforms.Builder customUniforms = new CustomUniforms.Builder();
    private int customTexAmount;
    private CloudSetting cloudSetting = CloudSetting.DEFAULT;
    private CloudSetting dhCloudSetting = CloudSetting.DEFAULT;
    private OptionalBoolean oldHandLight = OptionalBoolean.DEFAULT;
    private OptionalBoolean dynamicHandLight = OptionalBoolean.DEFAULT;
    private OptionalBoolean supportsColorCorrection = OptionalBoolean.DEFAULT;
    private OptionalBoolean oldLighting = OptionalBoolean.DEFAULT;
    private OptionalBoolean shadowTerrain = OptionalBoolean.DEFAULT;
    private OptionalBoolean shadowTranslucent = OptionalBoolean.DEFAULT;
    private OptionalBoolean shadowEntities = OptionalBoolean.DEFAULT;
    private OptionalBoolean shadowPlayer = OptionalBoolean.DEFAULT;
    private OptionalBoolean shadowBlockEntities = OptionalBoolean.DEFAULT;
    private OptionalBoolean shadowLightBlockEntities = OptionalBoolean.DEFAULT;
    private OptionalBoolean underwaterOverlay = OptionalBoolean.DEFAULT;
    private OptionalBoolean sun = OptionalBoolean.DEFAULT;
    private OptionalBoolean moon = OptionalBoolean.DEFAULT;
    private OptionalBoolean vignette = OptionalBoolean.DEFAULT;
    private OptionalBoolean backFaceSolid = OptionalBoolean.DEFAULT;
    private OptionalBoolean backFaceCutout = OptionalBoolean.DEFAULT;
    private OptionalBoolean backFaceCutoutMipped = OptionalBoolean.DEFAULT;
    private OptionalBoolean backFaceTranslucent = OptionalBoolean.DEFAULT;
    private OptionalBoolean rainDepth = OptionalBoolean.DEFAULT;
    private OptionalBoolean concurrentCompute = OptionalBoolean.DEFAULT;
    private OptionalBoolean beaconBeamDepth = OptionalBoolean.DEFAULT;
    private OptionalBoolean separateAo = OptionalBoolean.DEFAULT;
    private OptionalBoolean voxelizeLightBlocks = OptionalBoolean.DEFAULT;
    private OptionalBoolean separateEntityDraws = OptionalBoolean.DEFAULT;
    private OptionalBoolean frustumCulling = OptionalBoolean.DEFAULT;
    private OptionalBoolean occlusionCulling = OptionalBoolean.DEFAULT;
    private ShadowCullState shadowCulling = ShadowCullState.DEFAULT;
    private OptionalBoolean shadowEnabled = OptionalBoolean.DEFAULT;
    private OptionalBoolean dhShadowEnabled = OptionalBoolean.DEFAULT;
    private Optional<ParticleRenderingSettings> particleRenderingSettings = Optional.empty();
    private OptionalBoolean prepareBeforeShadow = OptionalBoolean.DEFAULT;
    private List<String> sliderOptions = new ArrayList<String>();
    private List<String> mainScreenOptions = null;
    private Integer mainScreenColumnCount = null;
    private String noiseTexturePath = null;
    private List<String> requiredFeatureFlags = new ArrayList<String>();
    private List<String> optionalFeatureFlags = new ArrayList<String>();

    private ShaderProperties() {
    }

    public ShaderProperties(String contents, ShaderPackOptions shaderPackOptions, Iterable<StringPair> environmentDefines) {
        String preprocessedContents = PropertiesPreprocessor.preprocessSource(contents, shaderPackOptions, environmentDefines);
        if (Iris.getIrisConfig().areDebugOptionsEnabled()) {
            try {
                Files.writeString((Path)FMLPaths.GAMEDIR.get().resolve("preprocessed.properties"), (CharSequence)preprocessedContents, (OpenOption[])new OpenOption[0]);
                Files.writeString((Path)FMLPaths.GAMEDIR.get().resolve("original.properties"), (CharSequence)contents, (OpenOption[])new OpenOption[0]);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        OrderBackedProperties preprocessed = new OrderBackedProperties();
        OrderBackedProperties original = new OrderBackedProperties();
        try {
            preprocessed.load(new StringReader(preprocessedContents));
            original.load(new StringReader(contents));
        }
        catch (IOException e) {
            Iris.logger.error("Error loading shaders.properties!", e);
        }
        ((Hashtable)preprocessed).forEach((keyObject, valueObject) -> {
            String key = (String)keyObject;
            String value = (String)valueObject;
            if ("texture.noise".equals(key)) {
                this.noiseTexturePath = value;
                return;
            }
            if ("clouds".equals(key)) {
                if ("off".equals(value)) {
                    this.cloudSetting = CloudSetting.OFF;
                } else if ("fast".equals(value)) {
                    this.cloudSetting = CloudSetting.FAST;
                } else if ("fancy".equals(value)) {
                    this.cloudSetting = CloudSetting.FANCY;
                } else {
                    Iris.logger.error("Unrecognized clouds setting: " + value);
                }
                if (this.dhCloudSetting == CloudSetting.DEFAULT) {
                    this.dhCloudSetting = this.cloudSetting;
                }
            }
            if ("dhClouds".equals(key)) {
                if ("off".equals(value)) {
                    this.dhCloudSetting = CloudSetting.OFF;
                } else if ("on".equals(value) || "fancy".equals(value)) {
                    this.dhCloudSetting = CloudSetting.FANCY;
                } else {
                    Iris.logger.error("Unrecognized DH clouds setting (need off, on): " + value);
                }
            }
            if ("shadow.culling".equals(key)) {
                if ("false".equals(value)) {
                    this.shadowCulling = ShadowCullState.DISTANCE;
                } else if ("true".equals(value)) {
                    this.shadowCulling = ShadowCullState.ADVANCED;
                } else if ("reversed".equals(value)) {
                    this.shadowCulling = ShadowCullState.REVERSED;
                } else {
                    Iris.logger.error("Unrecognized shadow culling setting: " + value);
                }
            }
            ShaderProperties.handleBooleanDirective(key, value, "oldHandLight", bool -> {
                this.oldHandLight = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "dynamicHandLight", bool -> {
                this.dynamicHandLight = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "oldLighting", bool -> {
                this.oldLighting = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "shadowTerrain", bool -> {
                this.shadowTerrain = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "shadowTranslucent", bool -> {
                this.shadowTranslucent = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "shadowEntities", bool -> {
                this.shadowEntities = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "shadowPlayer", bool -> {
                this.shadowPlayer = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "shadowBlockEntities", bool -> {
                this.shadowBlockEntities = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "shadowLightBlockEntities", bool -> {
                this.shadowLightBlockEntities = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "underwaterOverlay", bool -> {
                this.underwaterOverlay = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "sun", bool -> {
                this.sun = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "moon", bool -> {
                this.moon = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "vignette", bool -> {
                this.vignette = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "backFace.solid", bool -> {
                this.backFaceSolid = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "backFace.cutout", bool -> {
                this.backFaceCutout = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "backFace.cutoutMipped", bool -> {
                this.backFaceCutoutMipped = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "backFace.translucent", bool -> {
                this.backFaceTranslucent = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "rain.depth", bool -> {
                this.rainDepth = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "allowConcurrentCompute", bool -> {
                this.concurrentCompute = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "beacon.beam.depth", bool -> {
                this.beaconBeamDepth = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "separateAo", bool -> {
                this.separateAo = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "voxelizeLightBlocks", bool -> {
                this.voxelizeLightBlocks = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "separateEntityDraws", bool -> {
                this.separateEntityDraws = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "frustum.culling", bool -> {
                this.frustumCulling = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "occlusion.culling", bool -> {
                this.occlusionCulling = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "shadow.enabled", bool -> {
                this.shadowEnabled = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "dhShadow.enabled", bool -> {
                this.dhShadowEnabled = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "particles.before.deferred", bool -> {
                if (bool.orElse(false) && this.particleRenderingSettings.isEmpty()) {
                    this.particleRenderingSettings = Optional.of(ParticleRenderingSettings.BEFORE);
                }
            });
            ShaderProperties.handleBooleanDirective(key, value, "prepareBeforeShadow", bool -> {
                this.prepareBeforeShadow = bool;
            });
            ShaderProperties.handleBooleanDirective(key, value, "supportsColorCorrection", bool -> {
                this.supportsColorCorrection = bool;
            });
            if (key.startsWith("particles.ordering")) {
                Optional<ParticleRenderingSettings> settings = ParticleRenderingSettings.fromString(value.trim().toUpperCase(Locale.US));
                if (settings.isPresent()) {
                    this.particleRenderingSettings = settings;
                } else {
                    throw new RuntimeException("Failed to parse particle rendering order! " + value);
                }
            }
            ShaderProperties.handlePassDirective("scale.", key, value, pass -> {
                float scale;
                float offsetX = 0.0f;
                float offsetY = 0.0f;
                String[] parts = value.split(" ");
                try {
                    scale = Float.parseFloat(parts[0]);
                    if (parts.length > 1) {
                        offsetX = Float.parseFloat(parts[1]);
                        offsetY = Float.parseFloat(parts[2]);
                    }
                }
                catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                    Iris.logger.error("Unable to parse scale directive for " + pass + ": " + value, e);
                    return;
                }
                this.viewportScaleOverrides.put(pass, (Object)new ViewportData(scale, offsetX, offsetY));
            });
            ShaderProperties.handlePassDirective("size.buffer.", key, value, pass -> {
                String[] parts = value.split(" ");
                if (parts.length != 2) {
                    Iris.logger.error("Unable to parse size.buffer directive for " + pass + ": " + value);
                    return;
                }
                this.textureScaleOverrides.put(pass, (Object)new TextureScaleOverride(parts[0], parts[1]));
            });
            ShaderProperties.handlePassDirective("alphaTest.", key, value, pass -> {
                float reference;
                if ("off".equals(value) || "false".equals(value)) {
                    this.alphaTestOverrides.put(pass, (Object)AlphaTest.ALWAYS);
                    return;
                }
                String[] parts = value.split(" ");
                if (parts.length > 2) {
                    Iris.logger.warn("Weird alpha test directive for " + pass + " contains more parts than we expected: " + value);
                } else if (parts.length < 2) {
                    Iris.logger.error("Invalid alpha test directive for " + pass + ": " + value);
                    return;
                }
                Optional<AlphaTestFunction> function = AlphaTestFunction.fromString(parts[0]);
                if (!function.isPresent()) {
                    Iris.logger.error("Unable to parse alpha test directive for " + pass + ", unknown alpha test function " + parts[0] + ": " + value);
                    return;
                }
                try {
                    reference = Float.parseFloat(parts[1]);
                }
                catch (NumberFormatException e) {
                    Iris.logger.error("Unable to parse alpha test directive for " + pass + ": " + value, e);
                    return;
                }
                this.alphaTestOverrides.put(pass, (Object)new AlphaTest(function.get(), reference));
            });
            ShaderProperties.handlePassDirective("blend.", key, value, pass -> {
                if (pass.contains(".")) {
                    if (!IrisRenderSystem.supportsBufferBlending()) {
                        throw new RuntimeException("Buffer blending is not supported on this platform, however it was attempted to be used!");
                    }
                    String[] parts = pass.split("\\.");
                    int index = PackRenderTargetDirectives.LEGACY_RENDER_TARGETS.indexOf((Object)parts[1]);
                    if (index == -1 && parts[1].startsWith("colortex")) {
                        String id = parts[1].substring("colortex".length());
                        try {
                            index = Integer.parseInt(id);
                        }
                        catch (NumberFormatException e) {
                            throw new RuntimeException("Failed to parse buffer blend!", e);
                        }
                    }
                    if (index == -1) {
                        throw new RuntimeException("Failed to parse buffer blend! index = " + index);
                    }
                    if ("off".equals(value)) {
                        ((ArrayList)this.bufferBlendOverrides.computeIfAbsent((Object)parts[0], list -> new ArrayList())).add(new BufferBlendInformation(index, null));
                        return;
                    }
                    String[] modeArray = value.split(" ");
                    int[] modes = new int[modeArray.length];
                    int i = 0;
                    for (String modeName : modeArray) {
                        modes[i] = BlendModeFunction.fromString(modeName).get().getGlId();
                        ++i;
                    }
                    ((ArrayList)this.bufferBlendOverrides.computeIfAbsent((Object)parts[0], list -> new ArrayList())).add(new BufferBlendInformation(index, new BlendMode(modes[0], modes[1], modes[2], modes[3])));
                    return;
                }
                if ("off".equals(value)) {
                    this.blendModeOverrides.put(pass, (Object)BlendModeOverride.OFF);
                    return;
                }
                String[] modeArray = value.split(" ");
                int[] modes = new int[modeArray.length];
                int i = 0;
                for (String modeName : modeArray) {
                    modes[i] = BlendModeFunction.fromString(modeName).get().getGlId();
                    ++i;
                }
                this.blendModeOverrides.put(pass, (Object)new BlendModeOverride(new BlendMode(modes[0], modes[1], modes[2], modes[3])));
            });
            ShaderProperties.handlePassDirective("indirect.", key, value, pass -> {
                try {
                    String[] locations = value.split(" ");
                    this.indirectPointers.put(pass, (Object)new IndirectPointer(Integer.parseInt(locations[0]), Long.parseLong(locations[1])));
                }
                catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                    Iris.logger.fatal("Failed to parse indirect command for " + pass + "! " + value);
                }
            });
            ShaderProperties.handleProgramEnabledDirective("program.", key, value, program -> this.conditionallyEnabledPrograms.put(program, (Object)value));
            ShaderProperties.handlePassDirective("bufferObject.", key, value, index -> {
                String[] parts = value.split(" ");
                if (parts.length == 1) {
                    int trueSize;
                    int trueIndex;
                    try {
                        trueIndex = Integer.parseInt(index);
                        trueSize = Integer.parseInt(value);
                    }
                    catch (NumberFormatException e) {
                        Iris.logger.error("Number format exception parsing SSBO index/size!", e);
                        return;
                    }
                    if (trueIndex > 8) {
                        Iris.logger.fatal("SSBO's cannot use buffer numbers higher than 8, they're reserved!");
                        return;
                    }
                    if (trueSize < 1) {
                        return;
                    }
                    this.bufferObjects.put(trueIndex, (Object)new ShaderStorageInfo(trueSize, false, 0.0f, 0.0f));
                } else {
                    float scaleY;
                    float scaleX;
                    boolean isRelative;
                    int trueSize;
                    int trueIndex;
                    try {
                        trueIndex = Integer.parseInt(index);
                        trueSize = Integer.parseInt(parts[0]);
                        isRelative = Boolean.parseBoolean(parts[1]);
                        scaleX = Float.parseFloat(parts[2]);
                        scaleY = Float.parseFloat(parts[3]);
                    }
                    catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                        Iris.logger.error("Number format exception parsing SSBO index/size, or not correct format!", e);
                        return;
                    }
                    if (trueIndex > 8) {
                        Iris.logger.fatal("SSBO's cannot use buffer numbers higher than 8, they're reserved!");
                        return;
                    }
                    if (trueSize < 1) {
                        return;
                    }
                    this.bufferObjects.put(trueIndex, (Object)new ShaderStorageInfo(trueSize, isRelative, scaleX, scaleY));
                }
            });
            ShaderProperties.handleTwoArgDirective("texture.", key, value, (stageName, samplerName) -> {
                String[] parts = value.split(" ");
                samplerName = samplerName.split("\\.")[0];
                Optional<TextureStage> optionalTextureStage = TextureStage.parse(stageName);
                if (!optionalTextureStage.isPresent()) {
                    Iris.logger.warn("Unknown texture stage \"" + stageName + "\", ignoring custom texture directive for " + key);
                    return;
                }
                TextureStage stage = optionalTextureStage.get();
                if (parts.length > 1) {
                    String newSamplerName = "customtex" + this.customTexAmount;
                    ++this.customTexAmount;
                    TextureType type = null;
                    if (parts.length == 6) {
                        type = TextureType.TEXTURE_1D;
                        this.irisCustomTextures.put((Object)newSamplerName, (Object)new TextureDefinition.RawDefinition(parts[0], TextureType.valueOf(parts[1].toUpperCase(Locale.ROOT)), InternalTextureFormat.fromString(parts[2]).orElseThrow(IllegalArgumentException::new), Integer.parseInt(parts[3]), 0, 0, PixelFormat.fromString(parts[4]).orElseThrow(IllegalArgumentException::new), PixelType.fromString(parts[5]).orElseThrow(IllegalArgumentException::new)));
                    } else if (parts.length == 7) {
                        type = TextureType.valueOf(parts[1].toUpperCase(Locale.ROOT));
                        this.irisCustomTextures.put((Object)newSamplerName, (Object)new TextureDefinition.RawDefinition(parts[0], TextureType.valueOf(parts[1].toUpperCase(Locale.ROOT)), InternalTextureFormat.fromString(parts[2]).orElseThrow(IllegalArgumentException::new), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), 0, PixelFormat.fromString(parts[5]).orElseThrow(IllegalArgumentException::new), PixelType.fromString(parts[6]).orElseThrow(IllegalArgumentException::new)));
                    } else if (parts.length == 8) {
                        type = TextureType.TEXTURE_3D;
                        this.irisCustomTextures.put((Object)newSamplerName, (Object)new TextureDefinition.RawDefinition(parts[0], TextureType.valueOf(parts[1].toUpperCase(Locale.ROOT)), InternalTextureFormat.fromString(parts[2]).orElseThrow(IllegalArgumentException::new), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), PixelFormat.fromString(parts[6]).orElseThrow(IllegalArgumentException::new), PixelType.fromString(parts[7]).orElseThrow(IllegalArgumentException::new)));
                    } else {
                        Iris.logger.warn("Unknown texture directive for " + key + ": " + value);
                    }
                    this.customTexturePatching.put(new Tri<String, TextureType, TextureStage>((String)samplerName, type, stage), (Object)newSamplerName);
                    return;
                }
                this.customTextures.computeIfAbsent(stage, _stage -> new Object2ObjectOpenHashMap()).put(samplerName, (Object)new TextureDefinition.PNGDefinition(value));
            });
            ShaderProperties.handlePassDirective("customTexture.", key, value, samplerName -> {
                String[] parts = value.split(" ");
                if (parts.length > 1) {
                    if (parts.length == 6) {
                        this.irisCustomTextures.put(samplerName, (Object)new TextureDefinition.RawDefinition(parts[0], TextureType.valueOf(parts[1].toUpperCase(Locale.ROOT)), InternalTextureFormat.fromString(parts[2]).orElseThrow(IllegalArgumentException::new), Integer.parseInt(parts[3]), 0, 0, PixelFormat.fromString(parts[4]).orElseThrow(IllegalArgumentException::new), PixelType.fromString(parts[5]).orElseThrow(IllegalArgumentException::new)));
                    } else if (parts.length == 7) {
                        this.irisCustomTextures.put(samplerName, (Object)new TextureDefinition.RawDefinition(parts[0], TextureType.valueOf(parts[1].toUpperCase(Locale.ROOT)), InternalTextureFormat.fromString(parts[2]).orElseThrow(IllegalArgumentException::new), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), 0, PixelFormat.fromString(parts[5]).orElseThrow(IllegalArgumentException::new), PixelType.fromString(parts[6]).orElseThrow(IllegalArgumentException::new)));
                    } else if (parts.length == 8) {
                        this.irisCustomTextures.put(samplerName, (Object)new TextureDefinition.RawDefinition(parts[0], TextureType.valueOf(parts[1].toUpperCase(Locale.ROOT)), InternalTextureFormat.fromString(parts[2]).orElseThrow(IllegalArgumentException::new), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), PixelFormat.fromString(parts[6]).orElseThrow(IllegalArgumentException::new), PixelType.fromString(parts[7]).orElseThrow(IllegalArgumentException::new)));
                    } else {
                        Iris.logger.warn("Unknown texture directive for " + key + ": " + value);
                    }
                    return;
                }
                this.irisCustomTextures.put(samplerName, (Object)new TextureDefinition.PNGDefinition(value));
            });
            ShaderProperties.handlePassDirective("image.", key, value, imageName -> {
                ImageInformation image;
                String[] parts = value.split(" ");
                String key2 = key.substring(6);
                if (this.irisCustomImages.size() > 15) {
                    Iris.logger.error("Only up to 16 images are allowed, but tried to add another image! " + key);
                    return;
                }
                String samplerName = parts[0];
                if (samplerName.equals("none")) {
                    samplerName = null;
                }
                PixelFormat format = PixelFormat.fromString(parts[1]).orElse(null);
                InternalTextureFormat internalFormat = InternalTextureFormat.fromString(parts[2]).orElse(null);
                PixelType pixelType = PixelType.fromString(parts[3]).orElse(null);
                if (format == null || internalFormat == null || pixelType == null) {
                    Iris.logger.error("Image " + key2 + " is invalid! Format: " + format + " Internal format: " + internalFormat + " Pixel type: " + pixelType);
                }
                boolean clear = Boolean.parseBoolean(parts[4]);
                boolean relative = Boolean.parseBoolean(parts[5]);
                if (relative) {
                    float relativeWidth = Float.parseFloat(parts[6]);
                    float relativeHeight = Float.parseFloat(parts[7]);
                    image = new ImageInformation(key2, samplerName, TextureType.TEXTURE_2D, format, internalFormat, pixelType, 0, 0, 0, clear, true, relativeWidth, relativeHeight);
                } else {
                    int depth;
                    int height;
                    int width;
                    TextureType type;
                    if (parts.length == 7) {
                        type = TextureType.TEXTURE_1D;
                        width = Integer.parseInt(parts[6]);
                        height = 0;
                        depth = 0;
                    } else if (parts.length == 8) {
                        type = TextureType.TEXTURE_2D;
                        width = Integer.parseInt(parts[6]);
                        height = Integer.parseInt(parts[7]);
                        depth = 0;
                    } else if (parts.length == 9) {
                        type = TextureType.TEXTURE_3D;
                        width = Integer.parseInt(parts[6]);
                        height = Integer.parseInt(parts[7]);
                        depth = Integer.parseInt(parts[8]);
                    } else {
                        Iris.logger.error("Unknown image type! " + key2 + " = " + value);
                        return;
                    }
                    image = new ImageInformation(key2, samplerName, type, format, internalFormat, pixelType, width, height, depth, clear, false, 0.0f, 0.0f);
                }
                this.irisCustomImages.add(image);
            });
            ShaderProperties.handleTwoArgDirective("flip.", key, value, (pass, buffer) -> ShaderProperties.handleBooleanValue(key, value, shouldFlip -> ((Object2BooleanMap)this.explicitFlips.computeIfAbsent(pass, _pass -> new Object2BooleanOpenHashMap())).put(buffer, shouldFlip)));
            ShaderProperties.handlePassDirective("variable.", key, value, pass -> {
                String[] parts = pass.split("\\.");
                if (parts.length != 2) {
                    Iris.logger.warn("Custom variables should take the form of `variable.<type>.<name> = <expression>. Ignoring " + key);
                    return;
                }
                this.customUniforms.addVariable(parts[0], parts[1], value, false);
            });
            ShaderProperties.handlePassDirective("uniform.", key, value, pass -> {
                String[] parts = pass.split("\\.");
                if (parts.length != 2) {
                    Iris.logger.warn("Custom uniforms should take the form of `uniform.<type>.<name> = <expression>. Ignoring " + key);
                    return;
                }
                this.customUniforms.addVariable(parts[0], parts[1], value, true);
            });
        });
        ((Hashtable)original).forEach((keyObject, valueObject) -> {
            String key = (String)keyObject;
            String value = (String)valueObject;
            ShaderProperties.handleWhitespacedListDirective(key, value, "iris.features.required", options -> {
                this.requiredFeatureFlags = options;
            });
            ShaderProperties.handleWhitespacedListDirective(key, value, "iris.features.optional", options -> {
                this.optionalFeatureFlags = options;
            });
            ShaderProperties.handleWhitespacedListDirective(key, value, "sliders", sliders -> {
                this.sliderOptions = sliders;
            });
            ShaderProperties.handlePrefixedWhitespacedListDirective("profile.", key, value, this.profiles::put);
            if (ShaderProperties.handleIntDirective(key, value, "screen.columns", columns -> {
                this.mainScreenColumnCount = columns;
            })) {
                return;
            }
            if (ShaderProperties.handleAffixedIntDirective("screen.", ".columns", key, value, this.subScreenColumnCount::put)) {
                return;
            }
            ShaderProperties.handleWhitespacedListDirective(key, value, "screen", options -> {
                this.mainScreenOptions = options;
            });
            ShaderProperties.handlePrefixedWhitespacedListDirective("screen.", key, value, this.subScreenOptions::put);
        });
    }

    private static void handleBooleanValue(String key, String value, BooleanConsumer handler) {
        if ("true".equals(value)) {
            handler.accept(true);
        } else if ("false".equals(value)) {
            handler.accept(false);
        } else {
            Iris.logger.warn("Unexpected value for boolean key " + key + " in shaders.properties: got " + value + ", but expected either true or false");
        }
    }

    private static void handleBooleanDirective(String key, String value, String expectedKey, Consumer<OptionalBoolean> handler) {
        if (!expectedKey.equals(key)) {
            return;
        }
        if ("true".equals(value)) {
            handler.accept(OptionalBoolean.TRUE);
        } else if ("false".equals(value)) {
            handler.accept(OptionalBoolean.FALSE);
        } else {
            Iris.logger.warn("Unexpected value for boolean key " + key + " in shaders.properties: got " + value + ", but expected either true or false");
        }
    }

    private static boolean handleIntDirective(String key, String value, String expectedKey, Consumer<Integer> handler) {
        if (!expectedKey.equals(key)) {
            return false;
        }
        try {
            int result = Integer.parseInt(value);
            handler.accept(result);
        }
        catch (NumberFormatException nex) {
            Iris.logger.warn("Unexpected value for integer key " + key + " in shaders.properties: got " + value + ", but expected an integer");
        }
        return true;
    }

    private static boolean handleAffixedIntDirective(String prefix, String suffix, String key, String value, BiConsumer<String, Integer> handler) {
        if (key.startsWith(prefix) && key.endsWith(suffix)) {
            int substrBegin = prefix.length();
            int substrEnd = key.length() - suffix.length();
            if (substrEnd <= substrBegin) {
                return false;
            }
            String affixStrippedKey = key.substring(substrBegin, substrEnd);
            try {
                int result = Integer.parseInt(value);
                handler.accept(affixStrippedKey, result);
            }
            catch (NumberFormatException nex) {
                Iris.logger.warn("Unexpected value for integer key " + key + " in shaders.properties: got " + value + ", but expected an integer");
            }
            return true;
        }
        return false;
    }

    private static void handlePassDirective(String prefix, String key, String value, Consumer<String> handler) {
        if (key.startsWith(prefix)) {
            String pass = key.substring(prefix.length());
            handler.accept(pass);
        }
    }

    private static void handleProgramEnabledDirective(String prefix, String key, String value, Consumer<String> handler) {
        if (key.startsWith(prefix)) {
            String program = key.substring(prefix.length(), key.indexOf(".", prefix.length()));
            handler.accept(program);
        }
    }

    private static void handleWhitespacedListDirective(String key, String value, String expectedKey, Consumer<List<String>> handler) {
        if (!expectedKey.equals(key)) {
            return;
        }
        String[] elements = value.split(" +");
        handler.accept(Arrays.asList(elements));
    }

    private static void handlePrefixedWhitespacedListDirective(String prefix, String key, String value, BiConsumer<String, List<String>> handler) {
        if (key.startsWith(prefix)) {
            String prefixStrippedKey = key.substring(prefix.length());
            String[] elements = value.split(" +");
            handler.accept(prefixStrippedKey, Arrays.asList(elements));
        }
    }

    private static void handleTwoArgDirective(String prefix, String key, String value, BiConsumer<String, String> handler) {
        if (key.startsWith(prefix)) {
            int endOfPassIndex = key.indexOf(".", prefix.length());
            String stage = key.substring(prefix.length(), endOfPassIndex);
            String sampler = key.substring(endOfPassIndex + 1);
            handler.accept(stage, sampler);
        }
    }

    public static ShaderProperties empty() {
        return new ShaderProperties();
    }

    public OptionalBoolean getDhShadowEnabled() {
        return this.dhShadowEnabled;
    }

    public CloudSetting getCloudSetting() {
        return this.cloudSetting;
    }

    public OptionalBoolean getOldHandLight() {
        return this.oldHandLight;
    }

    public OptionalBoolean getDynamicHandLight() {
        return this.dynamicHandLight;
    }

    public OptionalBoolean getOldLighting() {
        return this.oldLighting;
    }

    public OptionalBoolean getShadowTerrain() {
        return this.shadowTerrain;
    }

    public OptionalBoolean getShadowTranslucent() {
        return this.shadowTranslucent;
    }

    public OptionalBoolean getShadowEntities() {
        return this.shadowEntities;
    }

    public OptionalBoolean getShadowPlayer() {
        return this.shadowPlayer;
    }

    public OptionalBoolean getShadowBlockEntities() {
        return this.shadowBlockEntities;
    }

    public OptionalBoolean getShadowLightBlockEntities() {
        return this.shadowLightBlockEntities;
    }

    public OptionalBoolean getUnderwaterOverlay() {
        return this.underwaterOverlay;
    }

    public OptionalBoolean getSun() {
        return this.sun;
    }

    public OptionalBoolean getMoon() {
        return this.moon;
    }

    public OptionalBoolean getVignette() {
        return this.vignette;
    }

    public OptionalBoolean getBackFaceSolid() {
        return this.backFaceSolid;
    }

    public OptionalBoolean getBackFaceCutout() {
        return this.backFaceCutout;
    }

    public OptionalBoolean getBackFaceCutoutMipped() {
        return this.backFaceCutoutMipped;
    }

    public OptionalBoolean getBackFaceTranslucent() {
        return this.backFaceTranslucent;
    }

    public OptionalBoolean getRainDepth() {
        return this.rainDepth;
    }

    public OptionalBoolean getBeaconBeamDepth() {
        return this.beaconBeamDepth;
    }

    public OptionalBoolean getSeparateAo() {
        return this.separateAo;
    }

    public OptionalBoolean getVoxelizeLightBlocks() {
        return this.voxelizeLightBlocks;
    }

    public OptionalBoolean getSeparateEntityDraws() {
        return this.separateEntityDraws;
    }

    public OptionalBoolean getFrustumCulling() {
        return this.frustumCulling;
    }

    public OptionalBoolean getOcclusionCulling() {
        return this.occlusionCulling;
    }

    public ShadowCullState getShadowCulling() {
        return this.shadowCulling;
    }

    public Object2ObjectMap<String, AlphaTest> getAlphaTestOverrides() {
        return this.alphaTestOverrides;
    }

    public OptionalBoolean getShadowEnabled() {
        return this.shadowEnabled;
    }

    public Optional<ParticleRenderingSettings> getParticleRenderingSettings() {
        if (this.separateEntityDraws == OptionalBoolean.TRUE) {
            return Optional.of(ParticleRenderingSettings.MIXED);
        }
        return this.particleRenderingSettings;
    }

    public OptionalBoolean getConcurrentCompute() {
        return this.concurrentCompute;
    }

    public OptionalBoolean getPrepareBeforeShadow() {
        return this.prepareBeforeShadow;
    }

    public Object2ObjectMap<String, ViewportData> getViewportScaleOverrides() {
        return this.viewportScaleOverrides;
    }

    public Object2ObjectMap<String, TextureScaleOverride> getTextureScaleOverrides() {
        return this.textureScaleOverrides;
    }

    public Object2ObjectMap<String, BlendModeOverride> getBlendModeOverrides() {
        return this.blendModeOverrides;
    }

    public Object2ObjectMap<String, IndirectPointer> getIndirectPointers() {
        return this.indirectPointers;
    }

    public Object2ObjectMap<String, ArrayList<BufferBlendInformation>> getBufferBlendOverrides() {
        return this.bufferBlendOverrides;
    }

    public Int2ObjectArrayMap<ShaderStorageInfo> getBufferObjects() {
        return this.bufferObjects;
    }

    public EnumMap<TextureStage, Object2ObjectMap<String, TextureDefinition>> getCustomTextures() {
        return this.customTextures;
    }

    public Object2ObjectMap<Tri<String, TextureType, TextureStage>, String> getCustomTexturePatching() {
        return this.customTexturePatching;
    }

    public Object2ObjectMap<String, TextureDefinition> getIrisCustomTextures() {
        return this.irisCustomTextures;
    }

    public List<ImageInformation> getIrisCustomImages() {
        return this.irisCustomImages;
    }

    public Optional<String> getNoiseTexturePath() {
        return Optional.ofNullable(this.noiseTexturePath);
    }

    public Object2ObjectMap<String, String> getConditionallyEnabledPrograms() {
        return this.conditionallyEnabledPrograms;
    }

    public List<String> getSliderOptions() {
        return this.sliderOptions;
    }

    public Map<String, List<String>> getProfiles() {
        return this.profiles;
    }

    public Optional<List<String>> getMainScreenOptions() {
        return Optional.ofNullable(this.mainScreenOptions);
    }

    public Map<String, List<String>> getSubScreenOptions() {
        return this.subScreenOptions;
    }

    public Optional<Integer> getMainScreenColumnCount() {
        return Optional.ofNullable(this.mainScreenColumnCount);
    }

    public Map<String, Integer> getSubScreenColumnCount() {
        return this.subScreenColumnCount;
    }

    public Object2ObjectMap<String, Object2BooleanMap<String>> getExplicitFlips() {
        return this.explicitFlips;
    }

    public List<String> getRequiredFeatureFlags() {
        return this.requiredFeatureFlags;
    }

    public List<String> getOptionalFeatureFlags() {
        return this.optionalFeatureFlags;
    }

    public OptionalBoolean supportsColorCorrection() {
        return this.supportsColorCorrection;
    }

    public CustomUniforms.Builder getCustomUniforms() {
        return this.customUniforms;
    }

    public CloudSetting getDHCloudSetting() {
        return this.dhCloudSetting;
    }
}

