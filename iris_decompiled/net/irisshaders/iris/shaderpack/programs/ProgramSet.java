/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.shaderpack.programs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.features.FeatureFlags;
import net.irisshaders.iris.gl.blending.BlendModeOverride;
import net.irisshaders.iris.shaderpack.ShaderPack;
import net.irisshaders.iris.shaderpack.include.AbsolutePackPath;
import net.irisshaders.iris.shaderpack.loading.ProgramId;
import net.irisshaders.iris.shaderpack.parsing.ComputeDirectiveParser;
import net.irisshaders.iris.shaderpack.parsing.ConstDirectiveParser;
import net.irisshaders.iris.shaderpack.parsing.DispatchingDirectiveHolder;
import net.irisshaders.iris.shaderpack.programs.ComputeSource;
import net.irisshaders.iris.shaderpack.programs.ProgramSetInterface;
import net.irisshaders.iris.shaderpack.programs.ProgramSource;
import net.irisshaders.iris.shaderpack.properties.PackDirectives;
import net.irisshaders.iris.shaderpack.properties.PackRenderTargetDirectives;
import net.irisshaders.iris.shaderpack.properties.ShaderProperties;

public class ProgramSet
implements ProgramSetInterface {
    private final PackDirectives packDirectives;
    private final ProgramSource shadow;
    private final ComputeSource[] shadowCompute;
    private final ProgramSource[] shadowcomp;
    private final ComputeSource[][] shadowCompCompute;
    private final ProgramSource[] begin;
    private final ComputeSource[][] beginCompute;
    private final ProgramSource[] prepare;
    private final ComputeSource[][] prepareCompute;
    private final ComputeSource[] setup;
    private final ProgramSource gbuffersBasic;
    private final ProgramSource gbuffersLine;
    private final ProgramSource gbuffersBeaconBeam;
    private final ProgramSource gbuffersTextured;
    private final ProgramSource gbuffersTexturedLit;
    private final ProgramSource gbuffersTerrain;
    private final ProgramSource gbuffersTerrainSolid;
    private final ProgramSource gbuffersTerrainCutout;
    private final ProgramSource gbuffersSkyBasic;
    private final ProgramSource gbuffersSkyTextured;
    private final ProgramSource gbuffersClouds;
    private final ProgramSource gbuffersWeather;
    private final ProgramSource gbuffersEntities;
    private final ProgramSource gbuffersEntitiesTrans;
    private final ProgramSource gbuffersParticles;
    private final ProgramSource gbuffersParticlesTrans;
    private final ProgramSource gbuffersEntitiesGlowing;
    private final ProgramSource gbuffersGlint;
    private final ProgramSource gbuffersEntityEyes;
    private final ProgramSource gbuffersBlock;
    private final ProgramSource gbuffersBlockTrans;
    private final ProgramSource gbuffersHand;
    private final ProgramSource[] deferred;
    private final ComputeSource[][] deferredCompute;
    private final ProgramSource gbuffersWater;
    private final ProgramSource gbuffersHandWater;
    private final ProgramSource[] composite;
    private final ComputeSource[][] compositeCompute;
    private final ProgramSource compositeFinal;
    private final ComputeSource[] finalCompute;
    private final ProgramSource dhTerrain;
    private final ProgramSource dhWater;
    private final ProgramSource dhShadow;
    private final ShaderPack pack;
    private final ProgramSource gbuffersDamagedBlock;

    public ProgramSet(AbsolutePackPath directory, Function<AbsolutePackPath, String> sourceProvider, ShaderProperties shaderProperties, ShaderPack pack) {
        int i;
        this.packDirectives = new PackDirectives(PackRenderTargetDirectives.BASELINE_SUPPORTED_RENDER_TARGETS, shaderProperties);
        this.pack = pack;
        boolean readTesselation = pack.hasFeature(FeatureFlags.TESSELLATION_SHADERS);
        this.shadow = ProgramSet.readProgramSource(directory, sourceProvider, "shadow", this, shaderProperties, BlendModeOverride.OFF, readTesselation);
        this.shadowCompute = this.readComputeArray(directory, sourceProvider, "shadow", shaderProperties);
        this.shadowcomp = this.readProgramArray(directory, sourceProvider, "shadowcomp", shaderProperties, readTesselation);
        this.shadowCompCompute = new ComputeSource[this.shadowcomp.length][];
        for (i = 0; i < this.shadowcomp.length; ++i) {
            this.shadowCompCompute[i] = this.readComputeArray(directory, sourceProvider, "shadowcomp" + (Serializable)(i == 0 ? "" : Integer.valueOf(i)), shaderProperties);
        }
        this.setup = this.readProgramArray(directory, sourceProvider, "setup", shaderProperties);
        this.begin = this.readProgramArray(directory, sourceProvider, "begin", shaderProperties, readTesselation);
        this.beginCompute = new ComputeSource[this.begin.length][];
        for (i = 0; i < this.begin.length; ++i) {
            this.beginCompute[i] = this.readComputeArray(directory, sourceProvider, "begin" + (Serializable)(i == 0 ? "" : Integer.valueOf(i)), shaderProperties);
        }
        this.prepare = this.readProgramArray(directory, sourceProvider, "prepare", shaderProperties, readTesselation);
        this.prepareCompute = new ComputeSource[this.prepare.length][];
        for (i = 0; i < this.prepare.length; ++i) {
            this.prepareCompute[i] = this.readComputeArray(directory, sourceProvider, "prepare" + (Serializable)(i == 0 ? "" : Integer.valueOf(i)), shaderProperties);
        }
        this.gbuffersBasic = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_basic", this, shaderProperties, readTesselation);
        this.gbuffersLine = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_line", this, shaderProperties, readTesselation);
        this.gbuffersBeaconBeam = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_beaconbeam", this, shaderProperties, readTesselation);
        this.gbuffersTextured = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_textured", this, shaderProperties, readTesselation);
        this.gbuffersTexturedLit = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_textured_lit", this, shaderProperties, readTesselation);
        this.gbuffersTerrain = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_terrain", this, shaderProperties, readTesselation);
        this.gbuffersTerrainSolid = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_terrain_solid", this, shaderProperties, readTesselation);
        this.gbuffersTerrainCutout = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_terrain_cutout", this, shaderProperties, readTesselation);
        this.gbuffersDamagedBlock = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_damagedblock", this, shaderProperties, readTesselation);
        this.gbuffersSkyBasic = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_skybasic", this, shaderProperties, readTesselation);
        this.gbuffersSkyTextured = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_skytextured", this, shaderProperties, readTesselation);
        this.gbuffersClouds = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_clouds", this, shaderProperties, readTesselation);
        this.gbuffersWeather = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_weather", this, shaderProperties, readTesselation);
        this.gbuffersEntities = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_entities", this, shaderProperties, readTesselation);
        this.gbuffersEntitiesTrans = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_entities_translucent", this, shaderProperties, readTesselation);
        this.gbuffersParticles = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_particles", this, shaderProperties, readTesselation);
        this.gbuffersParticlesTrans = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_particles_translucent", this, shaderProperties, readTesselation);
        this.gbuffersEntitiesGlowing = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_entities_glowing", this, shaderProperties, readTesselation);
        this.gbuffersGlint = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_armor_glint", this, shaderProperties, readTesselation);
        this.gbuffersEntityEyes = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_spidereyes", this, shaderProperties, readTesselation);
        this.gbuffersBlock = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_block", this, shaderProperties, readTesselation);
        this.gbuffersBlockTrans = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_block_translucent", this, shaderProperties, readTesselation);
        this.gbuffersHand = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_hand", this, shaderProperties, readTesselation);
        this.dhTerrain = ProgramSet.readProgramSource(directory, sourceProvider, "dh_terrain", this, shaderProperties, readTesselation);
        this.dhWater = ProgramSet.readProgramSource(directory, sourceProvider, "dh_water", this, shaderProperties, readTesselation);
        this.dhShadow = ProgramSet.readProgramSource(directory, sourceProvider, "dh_shadow", this, shaderProperties, readTesselation);
        this.deferred = this.readProgramArray(directory, sourceProvider, "deferred", shaderProperties, readTesselation);
        this.deferredCompute = new ComputeSource[this.deferred.length][];
        for (i = 0; i < this.deferred.length; ++i) {
            this.deferredCompute[i] = this.readComputeArray(directory, sourceProvider, "deferred" + (Serializable)(i == 0 ? "" : Integer.valueOf(i)), shaderProperties);
        }
        this.gbuffersWater = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_water", this, shaderProperties, readTesselation);
        this.gbuffersHandWater = ProgramSet.readProgramSource(directory, sourceProvider, "gbuffers_hand_water", this, shaderProperties, readTesselation);
        this.composite = this.readProgramArray(directory, sourceProvider, "composite", shaderProperties, readTesselation);
        this.compositeCompute = new ComputeSource[this.composite.length][];
        for (i = 0; i < this.deferred.length; ++i) {
            this.compositeCompute[i] = this.readComputeArray(directory, sourceProvider, "composite" + (Serializable)(i == 0 ? "" : Integer.valueOf(i)), shaderProperties);
        }
        this.compositeFinal = ProgramSet.readProgramSource(directory, sourceProvider, "final", this, shaderProperties, readTesselation);
        this.finalCompute = this.readComputeArray(directory, sourceProvider, "final", shaderProperties);
        this.locateDirectives();
    }

    @SafeVarargs
    private static <T> Optional<T> first(Optional<T> ... candidates) {
        for (Optional<T> candidate : candidates) {
            if (!candidate.isPresent()) continue;
            return candidate;
        }
        return Optional.empty();
    }

    private static ProgramSource readProgramSource(AbsolutePackPath directory, Function<AbsolutePackPath, String> sourceProvider, String program, ProgramSet programSet, ShaderProperties properties, boolean readTesselation) {
        return ProgramSet.readProgramSource(directory, sourceProvider, program, programSet, properties, null, readTesselation);
    }

    private static ProgramSource readProgramSource(AbsolutePackPath directory, Function<AbsolutePackPath, String> sourceProvider, String program, ProgramSet programSet, ShaderProperties properties, BlendModeOverride defaultBlendModeOverride, boolean readTesselation) {
        AbsolutePackPath vertexPath = directory.resolve(program + ".vsh");
        String vertexSource = sourceProvider.apply(vertexPath);
        AbsolutePackPath geometryPath = directory.resolve(program + ".gsh");
        String geometrySource = sourceProvider.apply(geometryPath);
        String tessControlSource = null;
        String tessEvalSource = null;
        if (readTesselation) {
            AbsolutePackPath tessControlPath = directory.resolve(program + ".tcs");
            tessControlSource = sourceProvider.apply(tessControlPath);
            AbsolutePackPath tessEvalPath = directory.resolve(program + ".tes");
            tessEvalSource = sourceProvider.apply(tessEvalPath);
        }
        AbsolutePackPath fragmentPath = directory.resolve(program + ".fsh");
        String fragmentSource = sourceProvider.apply(fragmentPath);
        if (vertexSource == null && fragmentSource != null) {
            Iris.logger.warn("Found a program (" + program + ") that has a fragment shader but no vertex shader? This is very legacy behavior and might not work right.");
            vertexSource = "#version 120\n\nvarying vec4 irs_texCoords[3];\nvarying vec4 irs_Color;\n\nvoid main() {\n\tgl_Position = ftransform();\n\tirs_texCoords[0] = gl_TextureMatrix[0] * gl_MultiTexCoord0;\n\tirs_texCoords[1] = gl_TextureMatrix[1] * gl_MultiTexCoord1;\n\tirs_texCoords[2] = gl_TextureMatrix[1] * gl_MultiTexCoord2;\n\tirs_Color = gl_Color;\n}\n";
        }
        return new ProgramSource(program, vertexSource, geometrySource, tessControlSource, tessEvalSource, fragmentSource, programSet, properties, defaultBlendModeOverride);
    }

    private static ComputeSource readComputeSource(AbsolutePackPath directory, Function<AbsolutePackPath, String> sourceProvider, String program, ProgramSet programSet, ShaderProperties properties) {
        AbsolutePackPath computePath = directory.resolve(program + ".csh");
        String computeSource = sourceProvider.apply(computePath);
        if (computeSource == null) {
            return null;
        }
        return new ComputeSource(program, computeSource, programSet, properties);
    }

    private ProgramSource[] readProgramArray(AbsolutePackPath directory, Function<AbsolutePackPath, String> sourceProvider, String name, ShaderProperties shaderProperties, boolean readTesselation) {
        ProgramSource[] programs = new ProgramSource[100];
        for (int i = 0; i < programs.length; ++i) {
            String suffix = i == 0 ? "" : Integer.toString(i);
            programs[i] = ProgramSet.readProgramSource(directory, sourceProvider, name + suffix, this, shaderProperties, readTesselation);
        }
        return programs;
    }

    private ComputeSource[] readProgramArray(AbsolutePackPath directory, Function<AbsolutePackPath, String> sourceProvider, String name, ShaderProperties properties) {
        ComputeSource[] programs = new ComputeSource[100];
        for (int i = 0; i < programs.length; ++i) {
            String suffix = i == 0 ? "" : Integer.toString(i);
            programs[i] = ProgramSet.readComputeSource(directory, sourceProvider, name + suffix, this, properties);
        }
        return programs;
    }

    private ComputeSource[] readComputeArray(AbsolutePackPath directory, Function<AbsolutePackPath, String> sourceProvider, String name, ShaderProperties properties) {
        ComputeSource[] programs = new ComputeSource[27];
        programs[0] = ProgramSet.readComputeSource(directory, sourceProvider, name, this, properties);
        for (char c = 'a'; c <= 'z'; c = (char)(c + '\u0001')) {
            String suffix = "_" + c;
            programs[c - 96] = ProgramSet.readComputeSource(directory, sourceProvider, name + suffix, this, properties);
            if (programs[c - 96] == null) break;
        }
        return programs;
    }

    private void locateDirectives() {
        ArrayList<ProgramSource> programs = new ArrayList<ProgramSource>();
        ArrayList<ComputeSource> computes = new ArrayList<ComputeSource>();
        programs.add(this.shadow);
        programs.addAll(Arrays.asList(this.shadowcomp));
        programs.addAll(Arrays.asList(this.begin));
        programs.addAll(Arrays.asList(this.prepare));
        programs.addAll(Arrays.asList(this.gbuffersBasic, this.gbuffersBeaconBeam, this.gbuffersTextured, this.gbuffersTexturedLit, this.gbuffersTerrain, this.gbuffersTerrainSolid, this.gbuffersTerrainCutout, this.gbuffersDamagedBlock, this.gbuffersSkyBasic, this.gbuffersSkyTextured, this.gbuffersClouds, this.gbuffersWeather, this.gbuffersEntities, this.gbuffersEntitiesTrans, this.gbuffersEntitiesGlowing, this.gbuffersGlint, this.gbuffersEntityEyes, this.gbuffersBlock, this.gbuffersBlockTrans, this.gbuffersHand, this.dhShadow, this.dhTerrain, this.dhWater));
        for (ComputeSource computeSource : this.setup) {
            if (computeSource == null) continue;
            computes.add(computeSource);
        }
        for (ComputeSource computeSource : this.beginCompute) {
            computes.addAll(Arrays.asList(computeSource));
        }
        for (ComputeSource computeSource : this.compositeCompute) {
            computes.addAll(Arrays.asList(computeSource));
        }
        for (ComputeSource computeSource : this.deferredCompute) {
            computes.addAll(Arrays.asList(computeSource));
        }
        for (ComputeSource computeSource : this.prepareCompute) {
            computes.addAll(Arrays.asList(computeSource));
        }
        for (ComputeSource computeSource : this.shadowCompCompute) {
            computes.addAll(Arrays.asList(computeSource));
        }
        Collections.addAll(computes, this.finalCompute);
        Collections.addAll(computes, this.shadowCompute);
        for (ComputeSource source : computes) {
            if (source == null) continue;
            source.getSource().map(ConstDirectiveParser::findDirectives).ifPresent(constDirectives -> {
                for (ConstDirectiveParser.ConstDirective directive : constDirectives) {
                    if (directive.getType() == ConstDirectiveParser.Type.IVEC3 && directive.getKey().equals("workGroups")) {
                        ComputeDirectiveParser.setComputeWorkGroups(source, directive);
                        continue;
                    }
                    if (directive.getType() != ConstDirectiveParser.Type.VEC2 || !directive.getKey().equals("workGroupsRender")) continue;
                    ComputeDirectiveParser.setComputeWorkGroupsRelative(source, directive);
                }
            });
        }
        programs.addAll(Arrays.asList(this.deferred));
        programs.add(this.gbuffersWater);
        programs.add(this.gbuffersHandWater);
        programs.addAll(Arrays.asList(this.composite));
        programs.add(this.compositeFinal);
        DispatchingDirectiveHolder packDirectiveHolder = new DispatchingDirectiveHolder();
        this.packDirectives.acceptDirectivesFrom(packDirectiveHolder);
        for (ProgramSource source : programs) {
            if (source == null) continue;
            source.getFragmentSource().map(ConstDirectiveParser::findDirectives).ifPresent(directives -> {
                for (ConstDirectiveParser.ConstDirective directive : directives) {
                    packDirectiveHolder.processDirective(directive);
                }
            });
        }
        this.packDirectives.getRenderTargetDirectives().getRenderTargetSettings().forEach((index, settings) -> Iris.logger.debug("Render target settings for colortex" + index + ": " + settings));
    }

    public Optional<ProgramSource> getShadow() {
        return this.shadow.requireValid();
    }

    public ProgramSource[] getShadowComposite() {
        return this.shadowcomp;
    }

    public ProgramSource[] getBegin() {
        return this.begin;
    }

    public ProgramSource[] getPrepare() {
        return this.prepare;
    }

    public ComputeSource[] getSetup() {
        return this.setup;
    }

    public Optional<ProgramSource> getGbuffersBasic() {
        return this.gbuffersBasic.requireValid();
    }

    public Optional<ProgramSource> getGbuffersBeaconBeam() {
        return this.gbuffersBeaconBeam.requireValid();
    }

    public Optional<ProgramSource> getGbuffersTextured() {
        return this.gbuffersTextured.requireValid();
    }

    public Optional<ProgramSource> getGbuffersTexturedLit() {
        return this.gbuffersTexturedLit.requireValid();
    }

    public Optional<ProgramSource> getGbuffersTerrain() {
        return this.gbuffersTerrain.requireValid();
    }

    public Optional<ProgramSource> getGbuffersTerrainSolid() {
        return this.gbuffersTerrainSolid.requireValid();
    }

    public Optional<ProgramSource> getGbuffersTerrainCutout() {
        return this.gbuffersTerrainCutout.requireValid();
    }

    public Optional<ProgramSource> getGbuffersDamagedBlock() {
        return this.gbuffersDamagedBlock.requireValid();
    }

    public Optional<ProgramSource> getGbuffersSkyBasic() {
        return this.gbuffersSkyBasic.requireValid();
    }

    public Optional<ProgramSource> getGbuffersSkyTextured() {
        return this.gbuffersSkyTextured.requireValid();
    }

    public Optional<ProgramSource> getGbuffersClouds() {
        return this.gbuffersClouds.requireValid();
    }

    public Optional<ProgramSource> getGbuffersWeather() {
        return this.gbuffersWeather.requireValid();
    }

    public Optional<ProgramSource> getGbuffersEntities() {
        return this.gbuffersEntities.requireValid();
    }

    public Optional<ProgramSource> getGbuffersEntitiesTrans() {
        return this.gbuffersEntitiesTrans.requireValid();
    }

    public Optional<ProgramSource> getGbuffersParticles() {
        return this.gbuffersParticles.requireValid();
    }

    public Optional<ProgramSource> getGbuffersParticlesTrans() {
        return this.gbuffersParticlesTrans.requireValid();
    }

    public Optional<ProgramSource> getGbuffersEntitiesGlowing() {
        return this.gbuffersEntitiesGlowing.requireValid();
    }

    public Optional<ProgramSource> getGbuffersGlint() {
        return this.gbuffersGlint.requireValid();
    }

    public Optional<ProgramSource> getGbuffersEntityEyes() {
        return this.gbuffersEntityEyes.requireValid();
    }

    public Optional<ProgramSource> getGbuffersBlock() {
        return this.gbuffersBlock.requireValid();
    }

    public Optional<ProgramSource> getGbuffersBlockTrans() {
        return this.gbuffersBlockTrans.requireValid();
    }

    public Optional<ProgramSource> getGbuffersHand() {
        return this.gbuffersHand.requireValid();
    }

    public Optional<ProgramSource> getDhTerrain() {
        return this.dhTerrain.requireValid();
    }

    public Optional<ProgramSource> getDhWater() {
        return this.dhWater.requireValid();
    }

    public Optional<ProgramSource> getDhShadow() {
        return this.dhShadow.requireValid();
    }

    public Optional<ProgramSource> get(ProgramId programId) {
        return switch (programId) {
            case ProgramId.Shadow -> this.getShadow();
            case ProgramId.Basic -> this.getGbuffersBasic();
            case ProgramId.Line -> this.gbuffersLine.requireValid();
            case ProgramId.Textured -> this.getGbuffersTextured();
            case ProgramId.TexturedLit -> this.getGbuffersTexturedLit();
            case ProgramId.SkyBasic -> this.getGbuffersSkyBasic();
            case ProgramId.SkyTextured -> this.getGbuffersSkyTextured();
            case ProgramId.Clouds -> this.getGbuffersClouds();
            case ProgramId.Terrain -> this.getGbuffersTerrain();
            case ProgramId.TerrainSolid -> this.getGbuffersTerrainSolid();
            case ProgramId.TerrainCutout -> this.getGbuffersTerrainCutout();
            case ProgramId.DamagedBlock -> this.getGbuffersDamagedBlock();
            case ProgramId.Block -> this.getGbuffersBlock();
            case ProgramId.BlockTrans -> this.getGbuffersBlockTrans();
            case ProgramId.BeaconBeam -> this.getGbuffersBeaconBeam();
            case ProgramId.Entities -> this.getGbuffersEntities();
            case ProgramId.EntitiesTrans -> this.getGbuffersEntitiesTrans();
            case ProgramId.Particles -> this.getGbuffersParticles();
            case ProgramId.ParticlesTrans -> this.getGbuffersParticlesTrans();
            case ProgramId.EntitiesGlowing -> this.getGbuffersEntitiesGlowing();
            case ProgramId.ArmorGlint -> this.getGbuffersGlint();
            case ProgramId.SpiderEyes -> this.getGbuffersEntityEyes();
            case ProgramId.Hand -> this.getGbuffersHand();
            case ProgramId.Weather -> this.getGbuffersWeather();
            case ProgramId.Water -> this.getGbuffersWater();
            case ProgramId.HandWater -> this.getGbuffersHandWater();
            case ProgramId.Final -> this.getCompositeFinal();
            case ProgramId.DhTerrain -> this.getDhTerrain();
            case ProgramId.DhWater -> this.getDhWater();
            case ProgramId.DhShadow -> this.getDhShadow();
            default -> Optional.empty();
        };
    }

    public ProgramSource[] getDeferred() {
        return this.deferred;
    }

    public Optional<ProgramSource> getGbuffersWater() {
        return this.gbuffersWater.requireValid();
    }

    public Optional<ProgramSource> getGbuffersHandWater() {
        return this.gbuffersHandWater.requireValid();
    }

    public ProgramSource[] getComposite() {
        return this.composite;
    }

    public Optional<ProgramSource> getCompositeFinal() {
        return this.compositeFinal.requireValid();
    }

    public ComputeSource[] getShadowCompute() {
        return this.shadowCompute;
    }

    public ComputeSource[][] getShadowCompCompute() {
        return this.shadowCompCompute;
    }

    public ComputeSource[][] getBeginCompute() {
        return this.beginCompute;
    }

    public ComputeSource[][] getPrepareCompute() {
        return this.prepareCompute;
    }

    public ComputeSource[][] getDeferredCompute() {
        return this.deferredCompute;
    }

    public ComputeSource[][] getCompositeCompute() {
        return this.compositeCompute;
    }

    public ComputeSource[] getFinalCompute() {
        return this.finalCompute;
    }

    public PackDirectives getPackDirectives() {
        return this.packDirectives;
    }

    public ShaderPack getPack() {
        return this.pack;
    }
}

