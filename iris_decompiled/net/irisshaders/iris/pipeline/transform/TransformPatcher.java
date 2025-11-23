/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.irisshaders.iris.pipeline.transform;

import io.github.douira.glsl_transformer.ast.node.Profile;
import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.node.Version;
import io.github.douira.glsl_transformer.ast.node.VersionStatement;
import io.github.douira.glsl_transformer.ast.print.PrintType;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.query.RootSupplier;
import io.github.douira.glsl_transformer.ast.transform.EnumASTTransformer;
import io.github.douira.glsl_transformer.ast.transform.TransformationException;
import io.github.douira.glsl_transformer.parser.ParsingException;
import io.github.douira.glsl_transformer.token_filter.ChannelFilter;
import io.github.douira.glsl_transformer.token_filter.TokenChannel;
import io.github.douira.glsl_transformer.token_filter.TokenFilter;
import io.github.douira.glsl_transformer.util.LRUCache;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.blending.AlphaTest;
import net.irisshaders.iris.gl.shader.ShaderCompileException;
import net.irisshaders.iris.gl.state.ShaderAttributeInputs;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.helpers.Tri;
import net.irisshaders.iris.pipeline.transform.Patch;
import net.irisshaders.iris.pipeline.transform.PatchShaderType;
import net.irisshaders.iris.pipeline.transform.ShaderPrinter;
import net.irisshaders.iris.pipeline.transform.parameter.ComputeParameters;
import net.irisshaders.iris.pipeline.transform.parameter.DHParameters;
import net.irisshaders.iris.pipeline.transform.parameter.Parameters;
import net.irisshaders.iris.pipeline.transform.parameter.SodiumParameters;
import net.irisshaders.iris.pipeline.transform.parameter.TextureStageParameters;
import net.irisshaders.iris.pipeline.transform.parameter.VanillaParameters;
import net.irisshaders.iris.pipeline.transform.transformer.CommonTransformer;
import net.irisshaders.iris.pipeline.transform.transformer.CompatibilityTransformer;
import net.irisshaders.iris.pipeline.transform.transformer.CompositeCoreTransformer;
import net.irisshaders.iris.pipeline.transform.transformer.CompositeTransformer;
import net.irisshaders.iris.pipeline.transform.transformer.DHGenericTransformer;
import net.irisshaders.iris.pipeline.transform.transformer.DHTerrainTransformer;
import net.irisshaders.iris.pipeline.transform.transformer.SodiumCoreTransformer;
import net.irisshaders.iris.pipeline.transform.transformer.SodiumTransformer;
import net.irisshaders.iris.pipeline.transform.transformer.TextureTransformer;
import net.irisshaders.iris.pipeline.transform.transformer.VanillaCoreTransformer;
import net.irisshaders.iris.pipeline.transform.transformer.VanillaTransformer;
import net.irisshaders.iris.shaderpack.texture.TextureStage;
import oculus.org.antlr.v4.runtime.Token;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TransformPatcher {
    private static final boolean useCache = true;
    private static final Map<CacheKey, Map<PatchShaderType, String>> cache = new LRUCache<CacheKey, Map<PatchShaderType, String>>(400);
    private static final List<String> internalPrefixes = List.of((Object)"iris_", (Object)"irisMain", (Object)"moj_import");
    private static final Pattern versionPattern = Pattern.compile("^.*#version\\s+(\\d+)", 32);
    private static final EnumASTTransformer<Parameters, PatchShaderType> transformer;
    static Logger LOGGER;
    static TokenFilter<Parameters> parseTokenFilter;

    private static Map<PatchShaderType, String> transformInternal(String name, Map<PatchShaderType, String> inputs, Parameters parameters) {
        try {
            return transformer.transform(inputs, parameters);
        }
        catch (TransformationException | ParsingException | IllegalArgumentException | IllegalStateException e) {
            ShaderPrinter.printProgram("errored_" + name).addSources(inputs).print();
            throw new ShaderCompileException(name, e);
        }
    }

    private static Map<PatchShaderType, String> transform(String name, String vertex, String geometry, String tessControl, String tessEval, String fragment, Parameters parameters) {
        if (vertex == null && geometry == null && tessControl == null && tessEval == null && fragment == null) {
            return null;
        }
        Map<PatchShaderType, String> result = null;
        CacheKey key = new CacheKey(parameters, vertex, geometry, tessControl, tessEval, fragment);
        if (cache.containsKey(key)) {
            result = cache.get(key);
        }
        if (result == null) {
            transformer.setPrintType(Iris.getIrisConfig().areDebugOptionsEnabled() ? PrintType.INDENTED : PrintType.SIMPLE);
            EnumMap<PatchShaderType, String> inputs = new EnumMap<PatchShaderType, String>(PatchShaderType.class);
            inputs.put(PatchShaderType.VERTEX, vertex);
            inputs.put(PatchShaderType.GEOMETRY, geometry);
            inputs.put(PatchShaderType.TESS_CONTROL, tessControl);
            inputs.put(PatchShaderType.TESS_EVAL, tessEval);
            inputs.put(PatchShaderType.FRAGMENT, fragment);
            result = TransformPatcher.transformInternal(name, inputs, parameters);
            cache.put(key, result);
        }
        return result;
    }

    private static Map<PatchShaderType, String> transformCompute(String name, String compute, Parameters parameters) {
        if (compute == null) {
            return null;
        }
        Map<PatchShaderType, String> result = null;
        CacheKey key = new CacheKey(parameters, compute);
        if (cache.containsKey(key)) {
            result = cache.get(key);
        }
        if (result == null) {
            transformer.setPrintType(Iris.getIrisConfig().areDebugOptionsEnabled() ? PrintType.INDENTED : PrintType.SIMPLE);
            EnumMap<PatchShaderType, String> inputs = new EnumMap<PatchShaderType, String>(PatchShaderType.class);
            inputs.put(PatchShaderType.COMPUTE, compute);
            result = TransformPatcher.transformInternal(name, inputs, parameters);
            cache.put(key, result);
        }
        return result;
    }

    public static Map<PatchShaderType, String> patchVanilla(String name, String vertex, String geometry, String tessControl, String tessEval, String fragment, AlphaTest alpha, boolean isLines, boolean hasChunkOffset, ShaderAttributeInputs inputs, Object2ObjectMap<Tri<String, TextureType, TextureStage>, String> textureMap) {
        return TransformPatcher.transform(name, vertex, geometry, tessControl, tessEval, fragment, new VanillaParameters(Patch.VANILLA, textureMap, alpha, isLines, hasChunkOffset, inputs, geometry != null, tessControl != null || tessEval != null));
    }

    public static Map<PatchShaderType, String> patchDHTerrain(String name, String vertex, String tessControl, String tessEval, String geometry, String fragment, Object2ObjectMap<Tri<String, TextureType, TextureStage>, String> textureMap) {
        return TransformPatcher.transform(name, vertex, geometry, tessControl, tessEval, fragment, new DHParameters(Patch.DH_TERRAIN, textureMap));
    }

    public static Map<PatchShaderType, String> patchDHGeneric(String name, String vertex, String tessControl, String tessEval, String geometry, String fragment, Object2ObjectMap<Tri<String, TextureType, TextureStage>, String> textureMap) {
        return TransformPatcher.transform(name, vertex, geometry, tessControl, tessEval, fragment, new DHParameters(Patch.DH_GENERIC, textureMap));
    }

    public static Map<PatchShaderType, String> patchSodium(String name, String vertex, String geometry, String tessControl, String tessEval, String fragment, AlphaTest alpha, ShaderAttributeInputs inputs, Object2ObjectMap<Tri<String, TextureType, TextureStage>, String> textureMap) {
        return TransformPatcher.transform(name, vertex, geometry, tessControl, tessEval, fragment, new SodiumParameters(Patch.SODIUM, textureMap, alpha, inputs));
    }

    public static Map<PatchShaderType, String> patchComposite(String name, String vertex, String geometry, String fragment, TextureStage stage, Object2ObjectMap<Tri<String, TextureType, TextureStage>, String> textureMap) {
        return TransformPatcher.transform(name, vertex, geometry, null, null, fragment, new TextureStageParameters(Patch.COMPOSITE, stage, textureMap));
    }

    public static String patchCompute(String name, String compute, TextureStage stage, Object2ObjectMap<Tri<String, TextureType, TextureStage>, String> textureMap) {
        return TransformPatcher.transformCompute(name, compute, new ComputeParameters(Patch.COMPUTE, stage, textureMap)).getOrDefault((Object)PatchShaderType.COMPUTE, null);
    }

    static {
        LOGGER = LogManager.getLogger(TransformPatcher.class);
        parseTokenFilter = new ChannelFilter<Parameters>(TokenChannel.PREPROCESSOR){

            @Override
            public boolean isTokenAllowed(Token token) {
                if (!super.isTokenAllowed(token)) {
                    throw new IllegalArgumentException("Unparsed preprocessor directives such as '" + token.getText() + "' may not be present at this stage of shader processing!");
                }
                return true;
            }
        };
        transformer = new EnumASTTransformer<Parameters, PatchShaderType>(PatchShaderType.class){
            {
                this.setRootSupplier(RootSupplier.PREFIX_UNORDERED_ED_EXACT);
            }

            @Override
            public TranslationUnit parseTranslationUnit(Root rootInstance, String input) {
                Matcher matcher = versionPattern.matcher(input);
                if (!matcher.find()) {
                    throw new IllegalArgumentException("No #version directive found in source code! See debugging.md for more information.");
                }
                TransformPatcher.transformer.getLexer().version = Version.fromNumber(Integer.parseInt(matcher.group(1)));
                return super.parseTranslationUnit(rootInstance, input);
            }
        };
        transformer.setTransformation((trees, parameters) -> {
            for (PatchShaderType type : PatchShaderType.values()) {
                TranslationUnit tree = (TranslationUnit)trees.get((Object)type);
                if (tree == null) continue;
                tree.outputOptions.enablePrintInfo();
                parameters.type = type;
                Root root = tree.getRoot();
                internalPrefixes.stream().flatMap(root.getPrefixIdentifierIndex()::prefixQueryFlat).findAny().ifPresent(id -> {
                    throw new IllegalArgumentException("Detected a potential reference to unstable and internal Iris shader interfaces (iris_, irisMain and moj_import). This isn't currently supported. Violation: " + id.getName() + ". See debugging.md for more information.");
                });
                root.indexBuildSession(() -> {
                    VersionStatement versionStatement = tree.getVersionStatement();
                    if (versionStatement == null) {
                        throw new IllegalStateException("Missing the version statement!");
                    }
                    Profile profile = versionStatement.profile;
                    Version version = versionStatement.version;
                    if (Objects.requireNonNull(parameters.patch) == Patch.COMPUTE) {
                        versionStatement.profile = Profile.CORE;
                        CommonTransformer.transform(transformer, tree, root, parameters, true);
                    } else {
                        boolean isLine;
                        boolean bl = isLine = parameters.patch == Patch.VANILLA && ((VanillaParameters)parameters).isLines();
                        if (profile == Profile.CORE || version.number >= 150 && profile == null || isLine) {
                            if (version.number < 410) {
                                versionStatement.version = Version.GLSL41;
                            }
                            switch (parameters.patch) {
                                case COMPOSITE: {
                                    CompositeCoreTransformer.transform(transformer, tree, root, parameters);
                                    break;
                                }
                                case SODIUM: {
                                    SodiumParameters sodiumParameters = (SodiumParameters)parameters;
                                    SodiumCoreTransformer.transform(transformer, tree, root, sodiumParameters);
                                    break;
                                }
                                case VANILLA: {
                                    VanillaCoreTransformer.transform(transformer, tree, root, (VanillaParameters)parameters);
                                    break;
                                }
                                default: {
                                    throw new UnsupportedOperationException("Unknown patch type: " + parameters.patch);
                                }
                            }
                            if (parameters.type == PatchShaderType.FRAGMENT) {
                                CompatibilityTransformer.transformFragmentCore(transformer, tree, root, parameters);
                            }
                        } else {
                            if (version.number < 410) {
                                versionStatement.version = Version.GLSL41;
                            }
                            versionStatement.profile = Profile.CORE;
                            switch (parameters.patch) {
                                case COMPOSITE: {
                                    CompositeTransformer.transform(transformer, tree, root, parameters);
                                    break;
                                }
                                case SODIUM: {
                                    SodiumParameters sodiumParameters = (SodiumParameters)parameters;
                                    SodiumTransformer.transform(transformer, tree, root, sodiumParameters);
                                    break;
                                }
                                case VANILLA: {
                                    VanillaTransformer.transform(transformer, tree, root, (VanillaParameters)parameters);
                                    break;
                                }
                                case DH_TERRAIN: {
                                    DHTerrainTransformer.transform(transformer, tree, root, parameters);
                                    break;
                                }
                                case DH_GENERIC: {
                                    DHGenericTransformer.transform(transformer, tree, root, parameters);
                                    break;
                                }
                                default: {
                                    throw new UnsupportedOperationException("Unknown patch type: " + parameters.patch);
                                }
                            }
                        }
                    }
                    TextureTransformer.transform(transformer, tree, root, parameters.getTextureStage(), parameters.getTextureMap());
                    CompatibilityTransformer.transformEach(transformer, tree, root, parameters);
                });
            }
            CompatibilityTransformer.transformGrouped(transformer, trees, parameters);
        });
        transformer.setTokenFilter(parseTokenFilter);
    }

    private static class CacheKey {
        final Parameters parameters;
        final String vertex;
        final String geometry;
        final String tessControl;
        final String tessEval;
        final String fragment;
        final String compute;

        public CacheKey(Parameters parameters, String vertex, String geometry, String tessControl, String tessEval, String fragment) {
            this.parameters = parameters;
            this.vertex = vertex;
            this.geometry = geometry;
            this.tessControl = tessControl;
            this.tessEval = tessEval;
            this.fragment = fragment;
            this.compute = null;
        }

        public CacheKey(Parameters parameters, String compute) {
            this.parameters = parameters;
            this.vertex = null;
            this.geometry = null;
            this.tessControl = null;
            this.tessEval = null;
            this.fragment = null;
            this.compute = compute;
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.parameters == null ? 0 : this.parameters.hashCode());
            result = 31 * result + (this.vertex == null ? 0 : this.vertex.hashCode());
            result = 31 * result + (this.geometry == null ? 0 : this.geometry.hashCode());
            result = 31 * result + (this.tessControl == null ? 0 : this.tessControl.hashCode());
            result = 31 * result + (this.tessEval == null ? 0 : this.tessEval.hashCode());
            result = 31 * result + (this.fragment == null ? 0 : this.fragment.hashCode());
            result = 31 * result + (this.compute == null ? 0 : this.compute.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            CacheKey other = (CacheKey)obj;
            if (this.parameters == null ? other.parameters != null : !this.parameters.equals(other.parameters)) {
                return false;
            }
            if (this.vertex == null ? other.vertex != null : !this.vertex.equals(other.vertex)) {
                return false;
            }
            if (this.geometry == null ? other.geometry != null : !this.geometry.equals(other.geometry)) {
                return false;
            }
            if (this.tessControl == null ? other.tessControl != null : !this.tessControl.equals(other.tessControl)) {
                return false;
            }
            if (this.tessEval == null ? other.tessEval != null : !this.tessEval.equals(other.tessEval)) {
                return false;
            }
            if (this.fragment == null ? other.fragment != null : !this.fragment.equals(other.fragment)) {
                return false;
            }
            if (this.compute == null) {
                return other.compute == null;
            }
            return this.compute.equals(other.compute);
        }
    }
}

