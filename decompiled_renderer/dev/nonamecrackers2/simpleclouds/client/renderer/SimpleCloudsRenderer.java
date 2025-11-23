/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  com.mojang.blaze3d.pipeline.TextureTarget
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferBuilder$RenderedBuffer
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.math.Axis
 *  dev.nonamecrackers2.simpleclouds.api.client.event.ModifyCloudRenderDistanceEvent
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.CloudMode
 *  javax.annotation.Nullable
 *  net.minecraft.CrashReport
 *  net.minecraft.CrashReportCategory
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.EffectInstance
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.PostChain
 *  net.minecraft.client.renderer.PostPass
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.ResourceManagerReloadListener
 *  net.minecraft.util.FastColor$ARGB32
 *  net.minecraft.util.Mth
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffectInstance$FactorData
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.StartupMessageManager
 *  net.minecraftforge.fml.loading.ImmediateWindowHandler
 *  nonamecrackers2.crackerslib.common.compat.CompatHelper
 *  org.apache.commons.lang3.mutable.MutableInt
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.maven.artifact.versioning.ArtifactVersion
 *  org.apache.maven.artifact.versioning.DefaultArtifactVersion
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector2f
 *  org.joml.Vector3f
 *  org.lwjgl.opengl.GL30
 *  org.lwjgl.opengl.GL40
 */
package dev.nonamecrackers2.simpleclouds.client.renderer;

import com.google.common.collect.Lists;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import dev.nonamecrackers2.simpleclouds.SimpleCloudsMod;
import dev.nonamecrackers2.simpleclouds.api.client.event.ModifyCloudRenderDistanceEvent;
import dev.nonamecrackers2.simpleclouds.api.common.cloud.CloudMode;
import dev.nonamecrackers2.simpleclouds.client.cloud.ClientSideCloudTypeManager;
import dev.nonamecrackers2.simpleclouds.client.compat.SimpleCloudsCompatHelper;
import dev.nonamecrackers2.simpleclouds.client.event.impl.DetermineCloudRenderPipelineEvent;
import dev.nonamecrackers2.simpleclouds.client.framebuffer.CloudRenderTarget;
import dev.nonamecrackers2.simpleclouds.client.framebuffer.ShadowMapBuffer;
import dev.nonamecrackers2.simpleclouds.client.framebuffer.WeightedBlendingTarget;
import dev.nonamecrackers2.simpleclouds.client.mesh.RendererInitializeResult;
import dev.nonamecrackers2.simpleclouds.client.mesh.chunk.MeshChunk;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.GenerationInterval;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.MultiRegionCloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.SingleRegionCloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.mesh.lod.LevelOfDetailConfig;
import dev.nonamecrackers2.simpleclouds.client.mesh.lod.PreparedChunk;
import dev.nonamecrackers2.simpleclouds.client.renderer.AtmosphericCloudsRenderHandler;
import dev.nonamecrackers2.simpleclouds.client.renderer.WorldEffects;
import dev.nonamecrackers2.simpleclouds.client.renderer.lightning.LightningBolt;
import dev.nonamecrackers2.simpleclouds.client.renderer.pipeline.CloudsRenderPipeline;
import dev.nonamecrackers2.simpleclouds.client.renderer.settings.CloudsRendererSettings;
import dev.nonamecrackers2.simpleclouds.client.shader.SimpleCloudsShaders;
import dev.nonamecrackers2.simpleclouds.client.shader.SingleSSBOShaderInstance;
import dev.nonamecrackers2.simpleclouds.client.shader.buffer.BindingManager;
import dev.nonamecrackers2.simpleclouds.client.shader.buffer.ShaderStorageBufferObject;
import dev.nonamecrackers2.simpleclouds.client.world.ClientCloudManager;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.cloud.SimpleCloudsConstants;
import dev.nonamecrackers2.simpleclouds.common.cloud.region.CloudGetter;
import dev.nonamecrackers2.simpleclouds.common.config.SimpleCloudsConfig;
import dev.nonamecrackers2.simpleclouds.mixin.MixinPostChain;
import java.awt.Color;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.StartupMessageManager;
import net.minecraftforge.fml.loading.ImmediateWindowHandler;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;

public class SimpleCloudsRenderer
implements ResourceManagerReloadListener {
    private static final Logger LOGGER = LogManager.getLogger((String)"simpleclouds/SimpleCloudsRenderer");
    private static final Vector3f DIFFUSE_LIGHT_0 = new Vector3f(0.2f, 1.0f, -0.7f).normalize();
    private static final Vector3f DIFFUSE_LIGHT_1 = new Vector3f(-0.2f, 1.0f, 0.7f).normalize();
    private static final ResourceLocation STORM_POST_PROCESSING_LOC = SimpleCloudsMod.id("shaders/post/storm_post.json");
    private static final ResourceLocation BLUR_POST_PROCESSING_LOC = SimpleCloudsMod.id("shaders/post/blur_post.json");
    private static final ResourceLocation SCREEN_SPACE_WORLD_FOG_LOC = SimpleCloudsMod.id("shaders/post/screen_space_world_fog.json");
    private static final ResourceLocation CLOUD_SHADOWS_LOC = SimpleCloudsMod.id("shaders/post/cloud_shadows.json");
    public static final ResourceLocation FINAL_COMPOSITE_LOC = SimpleCloudsMod.id("shaders/post/final_composite.json");
    public static final ResourceLocation FINAL_COMPOSITE_NO_TRANSPARENCY_LOC = SimpleCloudsMod.id("shaders/post/final_composite_no_transparency.json");
    private static final ResourceLocation DITHER_TEXTURE = SimpleCloudsMod.id("textures/shader/bayer_matrix.png");
    private static final ArtifactVersion REQUIRED_OPENGL_VERSION = new DefaultArtifactVersion("4.3");
    public static final int SHADOW_MAP_SIZE = 1024;
    public static final int SHADOW_MAP_SPAN = 10000;
    public static final int MAX_LIGHTNING_BOLTS = 16;
    public static final int BYTES_PER_LIGHTNING_BOLT = 16;
    public static final float CHUNK_FADE_IN_ALPHA_PER_TICK = 0.2f;
    public static final float DITHER_SCALE = 0.05f;
    @Nullable
    private static SimpleCloudsRenderer instance;
    private final CloudsRendererSettings settings;
    private final Minecraft mc;
    private final WorldEffects worldEffectsManager;
    private final AtmosphericCloudsRenderHandler atmoshpericClouds;
    @Nullable
    private ClientCloudManager cloudManager;
    private ArtifactVersion openGlVersion;
    private CloudMeshGenerator meshGenerator;
    @Nullable
    private CloudsRenderPipeline renderPipelineThisPass;
    @Nullable
    private RenderTarget cloudTarget;
    @Nullable
    private WeightedBlendingTarget cloudTransparencyTarget;
    @Nullable
    private RenderTarget stormFogTarget;
    private int stormFogResolutionDivisor = 4;
    @Nullable
    private RenderTarget blurTarget;
    private final List<PostChain> postChains = Lists.newArrayList();
    @Nullable
    private PostChain finalComposite;
    @Nullable
    private PostChain stormPostProcessing;
    @Nullable
    private PostChain blurPostProcessing;
    @Nullable
    private PostChain screenSpaceWorldFog;
    @Nullable
    private PostChain cloudShadows;
    @Nullable
    private ShaderStorageBufferObject lightningBoltPositions;
    @Nullable
    private ShadowMapBuffer stormFogShadowMap;
    private Optional<ShadowMapBuffer> shadowMap = Optional.empty();
    @Nullable
    private Frustum cullFrustum;
    private float fogStart;
    private float fogEnd;
    @Nullable
    private PoseStack stormFogShadowMapStack;
    @Nullable
    private PoseStack shadowMapStack;
    private boolean failedToCopyDepthBuffer;
    private boolean needsReload;
    @Nullable
    private RendererInitializeResult initialInitializationResult;

    private SimpleCloudsRenderer(CloudsRendererSettings settings, Minecraft mc) {
        this.settings = settings;
        this.mc = mc;
        this.worldEffectsManager = new WorldEffects(mc, this);
        this.atmoshpericClouds = new AtmosphericCloudsRenderHandler(mc);
    }

    public String getClientCloudManagerString() {
        return this.cloudManager != null ? this.cloudManager.toString() : "null";
    }

    public CloudMeshGenerator getMeshGenerator() {
        return this.meshGenerator;
    }

    public CloudsRenderPipeline getRenderPipeline() {
        return Objects.requireNonNull(this.renderPipelineThisPass, "Pipeline not determined");
    }

    public WorldEffects getWorldEffectsManager() {
        return this.worldEffectsManager;
    }

    public AtmosphericCloudsRenderHandler getAtmosphericCloudRenderer() {
        return this.atmoshpericClouds;
    }

    public CloudsRendererSettings getSettings() {
        return this.settings;
    }

    @Nullable
    public RendererInitializeResult getInitialInitializationResult() {
        return this.initialInitializationResult;
    }

    public ShadowMapBuffer getStormFogShadowMap() {
        return this.stormFogShadowMap;
    }

    public Optional<ShadowMapBuffer> getShadowMap() {
        return this.shadowMap;
    }

    @Nullable
    public PoseStack getStormFogShadowMapStack() {
        return this.stormFogShadowMapStack;
    }

    @Nullable
    public PoseStack getShadowMapStack() {
        return this.shadowMapStack;
    }

    public RenderTarget getBlurTarget() {
        return this.blurTarget;
    }

    public RenderTarget getStormFogTarget() {
        return this.stormFogTarget;
    }

    public RenderTarget getCloudTarget() {
        return this.cloudTarget;
    }

    public WeightedBlendingTarget getCloudTransparencyTarget() {
        return this.cloudTransparencyTarget;
    }

    public float getFogStart() {
        return this.fogStart;
    }

    public float getFogEnd() {
        return this.fogEnd;
    }

    public float getFadeFactorForDistance(float distance) {
        return 1.0f - Math.min(Math.max(distance - this.fogStart, 0.0f) / (this.fogEnd - this.fogStart), 1.0f);
    }

    @Nullable
    public Frustum getCullFrustum() {
        return this.cullFrustum;
    }

    public void onCloudManagerChange(ClientCloudManager manager) {
        this.cloudManager = manager;
        CloudMeshGenerator cloudMeshGenerator = this.meshGenerator;
        if (cloudMeshGenerator instanceof MultiRegionCloudMeshGenerator) {
            MultiRegionCloudMeshGenerator generator = (MultiRegionCloudMeshGenerator)cloudMeshGenerator;
            generator.setCloudGetter(manager);
        }
    }

    private void prepareMeshGenerator(float partialTicks) {
        CloudMeshGenerator cloudMeshGenerator = this.meshGenerator;
        if (cloudMeshGenerator instanceof SingleRegionCloudMeshGenerator) {
            SingleRegionCloudMeshGenerator generator = (SingleRegionCloudMeshGenerator)cloudMeshGenerator;
            generator.setFadeDistances((float)((Integer)SimpleCloudsConfig.CLIENT.singleModeFadeStartPercentage.get()).intValue() / 100.0f, (float)((Integer)SimpleCloudsConfig.CLIENT.singleModeFadeEndPercentage.get()).intValue() / 100.0f);
        }
        this.meshGenerator.setTransparencyRenderDistance((float)((Integer)SimpleCloudsConfig.CLIENT.transparencyRenderDistancePercentage.get()).intValue() / 100.0f);
        this.meshGenerator.setTestFacesFacingAway((Boolean)SimpleCloudsConfig.CLIENT.testSidesThatAreOccluded.get());
        if (this.mc.f_91073_ != null) {
            this.meshGenerator.setScroll(this.cloudManager.getScrollX(partialTicks), this.cloudManager.getScrollY(partialTicks), this.cloudManager.getScrollZ(partialTicks));
        }
    }

    public boolean needsReinitialization() {
        return this.settings.needsReinitialization(this.meshGenerator);
    }

    public void requestReload() {
        LOGGER.debug("Requesting reload...");
        this.needsReload = true;
    }

    public void m_6213_(ResourceManager manager) {
        RenderSystem.assertOnRenderThreadOrInit();
        this.initialInitializationResult = null;
        ArtifactVersion openGlVersion = this.openGlVersion;
        if (openGlVersion == null) {
            openGlVersion = new DefaultArtifactVersion(ImmediateWindowHandler.getGLVersion());
        }
        if (openGlVersion.compareTo((Object)REQUIRED_OPENGL_VERSION) < 0) {
            LOGGER.error("Simple Clouds renderer could not initialize. OpenGL version is {}, minimum required is {}", (Object)openGlVersion, (Object)REQUIRED_OPENGL_VERSION);
            this.initialInitializationResult = RendererInitializeResult.builder().errorOpenGL().build();
            this.openGlVersion = openGlVersion;
            return;
        }
        if (!SimpleCloudsShaders.areShadersInitialized()) {
            LOGGER.error("Simple Clouds renderer could not initialize. Core shaders are not initialized.");
            this.initialInitializationResult = RendererInitializeResult.builder().coreShadersNotInitialized(SimpleCloudsShaders.getError()).build();
            SimpleCloudsRenderer.saveAndPrintCrashReports(this.mc, this.initialInitializationResult);
            return;
        }
        RendererInitializeResult compatError = SimpleCloudsCompatHelper.findCompatErrors();
        if (compatError.getState() == RendererInitializeResult.State.ERROR) {
            LOGGER.error("Simple Clouds renderer could not initialize due to compat error(s): {}", (Object)compatError.getErrors().stream().map(e -> e.text().getString()).toList());
            this.initialInitializationResult = compatError;
            SimpleCloudsRenderer.saveAndPrintCrashReports(this.mc, this.initialInitializationResult);
            return;
        }
        StartupMessageManager.addModMessage((String)"Initializing Simple Clouds renderer");
        LOGGER.debug("OpenGL {}", (Object)openGlVersion);
        Instant started = Instant.now();
        LOGGER.debug("Beginning Simple Clouds renderer initialization");
        this.failedToCopyDepthBuffer = false;
        boolean highPrecisionDepth = SimpleCloudsMod.dhLoaded();
        RenderTarget main = SimpleCloudsCompatHelper.getMainRenderTarget();
        if (main == null) {
            this.initialInitializationResult = RendererInitializeResult.builder().errorUnknown(new NullPointerException("Main framebuffer is null"), "Simple Clouds Renderer").build();
            SimpleCloudsRenderer.saveAndPrintCrashReports(this.mc, this.initialInitializationResult);
            return;
        }
        if (this.cloudTarget != null) {
            this.cloudTarget.m_83930_();
        }
        this.cloudTarget = new CloudRenderTarget(main.f_83915_, main.f_83916_, Minecraft.f_91002_, highPrecisionDepth);
        this.cloudTarget.m_83931_(0.0f, 0.0f, 0.0f, 0.0f);
        if (this.cloudTransparencyTarget != null) {
            this.cloudTransparencyTarget.m_83930_();
        }
        this.cloudTransparencyTarget = new WeightedBlendingTarget(main.f_83915_, main.f_83916_, Minecraft.f_91002_, highPrecisionDepth);
        this.stormFogResolutionDivisor = SimpleCloudsCompatHelper.getStormFogResolutionDivisor();
        if (this.stormFogTarget != null) {
            this.stormFogTarget.m_83930_();
        }
        this.stormFogTarget = new TextureTarget(main.f_83915_ / this.stormFogResolutionDivisor, main.f_83916_ / this.stormFogResolutionDivisor, false, Minecraft.f_91002_);
        this.stormFogTarget.m_83931_(0.0f, 0.0f, 0.0f, 0.0f);
        this.stormFogTarget.m_83936_(9729);
        if (this.blurTarget != null) {
            this.blurTarget.m_83930_();
        }
        this.blurTarget = new TextureTarget(main.f_83915_, main.f_83916_, false, Minecraft.f_91002_);
        this.blurTarget.m_83931_(0.0f, 0.0f, 0.0f, 0.0f);
        this.blurTarget.m_83936_(9729);
        this.setupMeshGenerator();
        this.prepareMeshGenerator(0.0f);
        RendererInitializeResult result = this.meshGenerator.init(manager);
        if (this.initialInitializationResult == null) {
            this.initialInitializationResult = result;
        }
        if (this.stormFogShadowMap != null) {
            this.stormFogShadowMap.close();
            this.stormFogShadowMap = null;
        }
        this.shadowMap.ifPresent(buffer -> buffer.close());
        int span = this.meshGenerator.getLodConfig().getEffectiveChunkSpan() * 32 * 8;
        this.stormFogShadowMap = new ShadowMapBuffer(span, span, 1024, 1024, 0.0f, 10000.0f, true, false);
        if (((Boolean)SimpleCloudsConfig.CLIENT.distantShadows.get()).booleanValue() && SimpleCloudsMod.dhLoaded()) {
            int distantShadowSpan = (Integer)SimpleCloudsConfig.CLIENT.shadowDistance.get() * 2;
            distantShadowSpan = Math.min(distantShadowSpan, span);
            this.shadowMap = Optional.of(new ShadowMapBuffer(distantShadowSpan, distantShadowSpan, 1024, 1024, 0.0f, 10000.0f, false, true));
        } else {
            this.shadowMap = Optional.empty();
        }
        this.destroyPostChains();
        if (this.lightningBoltPositions != null) {
            BindingManager.freeSSBO(this.lightningBoltPositions);
            this.lightningBoltPositions = null;
        }
        this.lightningBoltPositions = BindingManager.createSSBO(35048);
        this.lightningBoltPositions.allocateBuffer(256);
        this.stormPostProcessing = this.createPostChain(manager, STORM_POST_PROCESSING_LOC, this.stormFogTarget, pass -> {
            EffectInstance effect = pass.m_110074_();
            effect.m_108954_("ShadowMap", () -> this.stormFogShadowMap.getDepthTexId());
            effect.m_108954_("ShadowMapColor", () -> this.stormFogShadowMap.getColorTexId());
            effect.m_108954_("DepthSampler", () -> this.cloudTarget.m_83980_());
            this.lightningBoltPositions.optionalBindToProgram("LightningBolts", effect.m_108943_());
        });
        this.blurPostProcessing = this.createPostChain(manager, BLUR_POST_PROCESSING_LOC, this.blurTarget);
        this.blurPostProcessing.m_110036_("swap").m_83936_(9729);
        this.screenSpaceWorldFog = this.createPostChain(manager, SCREEN_SPACE_WORLD_FOG_LOC, main, pass -> {
            EffectInstance effect = pass.m_110074_();
            effect.m_108954_("StormFogSampler", () -> this.blurTarget.m_83975_());
            effect.m_108954_("CloudDepthSampler", () -> this.cloudTarget.m_83980_());
        });
        this.finalComposite = this.createPostChain(manager, this.settings.useTransparency() ? FINAL_COMPOSITE_LOC : FINAL_COMPOSITE_NO_TRANSPARENCY_LOC, main, pass -> {
            EffectInstance effect = pass.m_110074_();
            if (this.settings.useTransparency()) {
                effect.m_108954_("AccumTexture", () -> this.cloudTransparencyTarget.m_83975_());
                effect.m_108954_("RevealageTexture", () -> this.cloudTransparencyTarget.getRevealageTextureId());
            }
            effect.m_108954_("CloudsTexture", () -> this.cloudTarget.m_83975_());
        });
        if (this.shadowMap.isPresent()) {
            ShadowMapBuffer map = this.shadowMap.get();
            this.cloudShadows = this.createPostChain(manager, CLOUD_SHADOWS_LOC, main, pass -> {
                EffectInstance effect = pass.m_110074_();
                effect.m_108954_("ShadowMap", () -> this.shadowMap.get().getDepthTexId());
                effect.m_108960_("ShadowSpan").m_5985_((float)Math.min(map.getViewWidth(), map.getViewHeight()));
            });
        }
        this.atmoshpericClouds.init(manager);
        long duration = Duration.between(started, Instant.now()).toMillis();
        LOGGER.info("Finished initialization, took {} ms", (Object)duration);
        LOGGER.debug("Total LODs: {}", (Object)(this.meshGenerator.getLodConfig().getLods().length + 1));
        LOGGER.debug("Highest detail (primary) chunk span: {}", (Object)this.meshGenerator.getLodConfig().getPrimaryChunkSpan());
        LOGGER.debug("Effective chunk span with LODs (total viewable area): {}", (Object)this.meshGenerator.getLodConfig().getEffectiveChunkSpan());
        LOGGER.debug("Total span in blocks: {}", (Object)(this.meshGenerator.getLodConfig().getEffectiveChunkSpan() * 32 * 8));
        SimpleCloudsRenderer.saveAndPrintCrashReports(this.mc, result);
    }

    private static void saveAndPrintCrashReports(Minecraft mc, RendererInitializeResult result) {
        switch (result.getState()) {
            case ERROR: {
                List<CrashReport> reports = result.createCrashReports();
                LOGGER.error("---------CRASH REPORT BEGIN---------");
                for (CrashReport report : reports) {
                    mc.m_91354_(report);
                    LOGGER.error("{}", (Object)report.m_127526_());
                }
                LOGGER.error("---------CRASH REPORT END---------");
                result.saveCrashReports(mc.f_91069_);
                break;
            }
        }
    }

    private void setupMeshGenerator() {
        CloudMeshGenerator useMultiRegion2;
        if (this.settings.checkAndOrBeginInitialization(this.meshGenerator)) {
            CloudMode mode;
            if (this.meshGenerator != null) {
                this.meshGenerator.close();
                this.meshGenerator = null;
            }
            boolean isAmbientMode = (mode = this.settings.getCurrentCloudMode()) == CloudMode.AMBIENT;
            boolean useMultiRegion2 = isAmbientMode || mode == CloudMode.DEFAULT;
            boolean shadedClouds = this.settings.shadedClouds();
            boolean useFixedMeshDataSectionSize = this.settings.useFixedMeshDataSectionSize();
            boolean useTransparency = this.settings.useTransparency();
            LevelOfDetailConfig lod = this.settings.getCurrentLod().getConfig();
            CloudMeshGenerator.Builder builder = CloudMeshGenerator.builder().fadeNearOrigin(isAmbientMode).shadedClouds(shadedClouds).fixedMeshDataSectionSize(useFixedMeshDataSectionSize).meshGenInterval(SimpleCloudsRenderer::calculateMeshGenInterval).lodConfig(lod).useTransparency(useTransparency);
            if (useMultiRegion2) {
                if (isAmbientMode) {
                    builder.fadeStart(0.25f).fadeEnd(0.5f);
                }
                this.meshGenerator = builder.createMultiRegion();
            } else if (mode == CloudMode.SINGLE) {
                float fadeStart = (float)((Integer)SimpleCloudsConfig.CLIENT.singleModeFadeStartPercentage.get()).intValue() / 100.0f;
                float fadeEnd = (float)((Integer)SimpleCloudsConfig.CLIENT.singleModeFadeEndPercentage.get()).intValue() / 100.0f;
                this.meshGenerator = builder.fadeStart(fadeStart).fadeEnd(fadeEnd).createSingleRegion(SimpleCloudsConstants.EMPTY);
            } else {
                throw new IllegalArgumentException("Not sure how to handle cloud mode " + mode);
            }
        }
        if ((useMultiRegion2 = this.meshGenerator) instanceof MultiRegionCloudMeshGenerator) {
            MultiRegionCloudMeshGenerator multiRegionGenerator = (MultiRegionCloudMeshGenerator)useMultiRegion2;
            multiRegionGenerator.setCloudGetter(this.cloudManager != null ? this.cloudManager : CloudGetter.EMPTY);
        } else {
            useMultiRegion2 = this.meshGenerator;
            if (useMultiRegion2 instanceof SingleRegionCloudMeshGenerator) {
                SingleRegionCloudMeshGenerator singleRegionGenerator = (SingleRegionCloudMeshGenerator)useMultiRegion2;
                CloudType type = this.settings.getSingleModeCloudType();
                if (!ClientCloudManager.isAvailableServerSide() && !ClientSideCloudTypeManager.isValidClientSideSingleModeCloudType(type)) {
                    type = SimpleCloudsConstants.EMPTY;
                }
                if (type == null) {
                    type = SimpleCloudsConstants.EMPTY;
                }
                singleRegionGenerator.setCloudType(type);
            } else {
                throw new IllegalArgumentException("Not sure how to handle generator: " + this.meshGenerator);
            }
        }
    }

    private void destroyPostChains() {
        this.postChains.forEach(PostChain::close);
        this.postChains.clear();
    }

    @Nullable
    private PostChain createPostChain(ResourceManager manager, ResourceLocation loc, RenderTarget target) {
        return this.createPostChain(manager, loc, target, effect -> {});
    }

    @Nullable
    private PostChain createPostChain(ResourceManager manager, ResourceLocation loc, RenderTarget target, Consumer<PostPass> passConsumer) {
        try {
            PostChain chain = new PostChain(this.mc.m_91097_(), manager, target, loc);
            chain.m_110025_(target.f_83915_, target.f_83916_);
            for (PostPass pass : ((MixinPostChain)chain).simpleclouds$getPostPasses()) {
                passConsumer.accept(pass);
            }
            this.postChains.add(chain);
            return chain;
        }
        catch (JsonSyntaxException e) {
            LOGGER.warn("Failed to parse post shader: {}", (Object)loc, (Object)e);
        }
        catch (IOException e) {
            LOGGER.warn("Failed to load post shader: {}", (Object)loc, (Object)e);
        }
        return null;
    }

    public void onMainWindowResize(int width, int height) {
        this.atmoshpericClouds.onResize(width, height);
        RenderTarget main = SimpleCloudsCompatHelper.getMainRenderTarget();
        if (main == null) {
            return;
        }
        width = main.f_83915_;
        height = main.f_83916_;
        if (this.cloudTarget != null) {
            this.cloudTarget.m_83941_(width, height, Minecraft.f_91002_);
        }
        if (this.cloudTransparencyTarget != null) {
            this.cloudTransparencyTarget.m_83941_(width, height, Minecraft.f_91002_);
        }
        this.stormFogResolutionDivisor = SimpleCloudsCompatHelper.getStormFogResolutionDivisor();
        if (this.stormFogTarget != null) {
            this.stormFogTarget.m_83941_(width / this.stormFogResolutionDivisor, height / this.stormFogResolutionDivisor, Minecraft.f_91002_);
            this.stormFogTarget.m_83936_(9729);
        }
        if (this.blurTarget != null) {
            this.blurTarget.m_83941_(width, height, Minecraft.f_91002_);
            this.blurTarget.m_83936_(9729);
        }
        for (PostChain chain : this.postChains) {
            RenderTarget chainTarget = ((MixinPostChain)chain).simpleclouds$getScreenTarget();
            chain.m_110025_(chainTarget.f_83915_, chainTarget.f_83916_);
        }
        if (this.blurPostProcessing != null) {
            this.blurPostProcessing.m_110036_("swap").m_83936_(9729);
        }
    }

    public void shutdown() {
        if (this.cloudTarget != null) {
            this.cloudTarget.m_83930_();
        }
        if (this.cloudTransparencyTarget != null) {
            this.cloudTransparencyTarget.m_83930_();
        }
        if (this.stormFogTarget != null) {
            this.stormFogTarget.m_83930_();
        }
        if (this.blurTarget != null) {
            this.blurTarget.m_83930_();
        }
        this.cloudTarget = null;
        this.cloudTransparencyTarget = null;
        this.stormFogTarget = null;
        this.blurTarget = null;
        this.destroyPostChains();
        if (this.meshGenerator != null) {
            this.meshGenerator.close();
        }
        if (this.stormFogShadowMap != null) {
            this.stormFogShadowMap.close();
            this.stormFogShadowMap = null;
        }
        if (this.shadowMap.isPresent()) {
            this.shadowMap.get().close();
            this.shadowMap = Optional.empty();
        }
        if (this.lightningBoltPositions != null) {
            BindingManager.freeSSBO(this.lightningBoltPositions);
            this.lightningBoltPositions = null;
        }
        this.atmoshpericClouds.close();
    }

    public void baseTick() {
        if (this.needsReload) {
            this.m_6213_(this.mc.m_91098_());
            this.needsReload = false;
        }
    }

    public void tick() {
        this.worldEffectsManager.tick();
        if (this.cloudManager != null) {
            this.atmoshpericClouds.setWindDirection(this.cloudManager.calculateWindDirection());
        }
        this.atmoshpericClouds.tick();
        if (this.meshGenerator != null) {
            this.meshGenerator.worldTick();
        }
    }

    public static void renderCloudsOpaque(CloudMeshGenerator generator, PoseStack stack, Matrix4f projMat, float fogStart, float fogEnd, float partialTick, float r, float g, float b, @Nullable Frustum frustum) {
        SimpleCloudsRenderer.renderCloudsOpaque(generator, stack, projMat, fogStart, fogEnd, partialTick, r, g, b, frustum, true);
    }

    public static void renderCloudsOpaque(CloudMeshGenerator generator, PoseStack stack, Matrix4f projMat, float fogStart, float fogEnd, float partialTick, float r, float g, float b, @Nullable Frustum frustum, boolean ditherFade) {
        RenderSystem.assertOnRenderThread();
        BufferUploader.m_166835_();
        if (!generator.canRender()) {
            return;
        }
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        SingleSSBOShaderInstance shader = SimpleCloudsShaders.getCloudsShader();
        RenderSystem.setShader(() -> shader);
        TextureManager manager = Minecraft.m_91087_().m_91097_();
        AbstractTexture ditherTexture = manager.m_118506_(DITHER_TEXTURE);
        shader.m_173350_("BayerMatrixSampler", ditherTexture);
        shader.m_173356_("DitherScale").m_5985_(0.05f);
        SimpleCloudsRenderer.prepareShader(shader, stack.m_85850_().m_252922_(), projMat, fogStart, fogEnd);
        shader.m_173363_();
        generator.forRenderableMeshChunks(frustum, MeshChunk::getOpaqueBuffers, (chunk, opaqueBuffers) -> {
            if (ditherFade) {
                RenderSystem.setShaderColor((float)r, (float)g, (float)b, (float)chunk.getAlpha(partialTick));
                shader.f_173312_.m_5941_(RenderSystem.getShaderColor());
                shader.f_173312_.m_85633_();
            }
            GL30.glBindBufferBase((int)37074, (int)shader.getShaderStorageBinding(), (int)opaqueBuffers.getBufferId());
            generator.getSideMesh().drawInstanced(opaqueBuffers.getElementCount());
        }, ditherFade);
        GL30.glBindBufferBase((int)37074, (int)shader.getShaderStorageBinding(), (int)0);
        shader.m_173362_();
        GL30.glBindVertexArray((int)0);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.enableCull();
    }

    public static void renderCloudsTransparency(CloudMeshGenerator generator, PoseStack stack, Matrix4f projMat, float fogStart, float fogEnd, float partialTick, float r, float g, float b, @Nullable Frustum frustum) {
        SimpleCloudsRenderer.renderCloudsTransparency(generator, stack, projMat, fogStart, fogEnd, partialTick, r, g, b, frustum, true);
    }

    public static void renderCloudsTransparency(CloudMeshGenerator generator, PoseStack stack, Matrix4f projMat, float fogStart, float fogEnd, float partialTick, float r, float g, float b, @Nullable Frustum frustum, boolean ditherFade) {
        RenderSystem.assertOnRenderThread();
        BufferUploader.m_166835_();
        if (!generator.canRender() || !generator.transparencyEnabled()) {
            return;
        }
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask((boolean)false);
        SingleSSBOShaderInstance shader = SimpleCloudsShaders.getCloudsTransparencyShader();
        RenderSystem.setShader(() -> shader);
        TextureManager manager = Minecraft.m_91087_().m_91097_();
        AbstractTexture ditherTexture = manager.m_118506_(DITHER_TEXTURE);
        shader.m_173350_("BayerMatrixSampler", ditherTexture);
        shader.m_173356_("DitherScale").m_5985_(0.05f);
        SimpleCloudsRenderer.prepareShader(shader, stack.m_85850_().m_252922_(), projMat, fogStart, fogEnd);
        shader.m_173363_();
        GL30.glEnablei((int)3042, (int)0);
        GL30.glEnablei((int)3042, (int)1);
        GL40.glBlendEquationi((int)0, (int)32774);
        GL40.glBlendEquationi((int)1, (int)32774);
        GL40.glBlendFunci((int)0, (int)1, (int)1);
        GL40.glBlendFunci((int)1, (int)0, (int)769);
        generator.forRenderableMeshChunks(frustum, c -> c.getTransparentBuffers().get(), (chunk, transparentBuffers) -> {
            if (ditherFade) {
                RenderSystem.setShaderColor((float)r, (float)g, (float)b, (float)chunk.getAlpha(partialTick));
                shader.f_173312_.m_5941_(RenderSystem.getShaderColor());
                shader.f_173312_.m_85633_();
            }
            GL30.glBindBufferBase((int)37074, (int)shader.getShaderStorageBinding(), (int)transparentBuffers.getBufferId());
            generator.getCubeMesh().drawInstanced(transparentBuffers.getElementCount());
        }, ditherFade);
        GL30.glBindBufferBase((int)37074, (int)shader.getShaderStorageBinding(), (int)0);
        shader.m_173362_();
        GL30.glDisablei((int)3042, (int)0);
        GL30.glDisablei((int)3042, (int)1);
        GL40.glBlendFuncSeparatei((int)0, (int)770, (int)771, (int)1, (int)0);
        GL40.glBlendFuncSeparatei((int)1, (int)770, (int)771, (int)1, (int)0);
        GL30.glBindVertexArray((int)0);
        RenderSystem.depthMask((boolean)true);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    private PoseStack createShadowMapStack(ShadowMapBuffer shadowMap, double camX, double camY, double camZ, Consumer<PoseStack> transformApplier) {
        PoseStack stack = new PoseStack();
        stack.m_166856_();
        double depthCenter = ((double)shadowMap.getNear() + (double)shadowMap.getFar()) * -0.5;
        stack.m_85837_((double)shadowMap.getViewWidth() / 2.0, (double)shadowMap.getViewHeight() / 2.0, depthCenter);
        transformApplier.accept(stack);
        float chunkSizeUpscaled = 256.0f;
        float camOffsetX = (float)Mth.m_14107_((double)(camX / (double)chunkSizeUpscaled)) * chunkSizeUpscaled;
        float camOffsetZ = (float)Mth.m_14107_((double)(camZ / (double)chunkSizeUpscaled)) * chunkSizeUpscaled;
        stack.m_85837_((double)(-camOffsetX), -((double)this.cloudManager.getCloudHeight()), (double)(-camOffsetZ));
        return stack;
    }

    private void renderShadowMap(ShadowMapBuffer shadowMap, PoseStack stack, SingleSSBOShaderInstance shader, @Nullable Frustum frustum) {
        RenderSystem.assertOnRenderThread();
        stack.m_85836_();
        this.translateClouds(stack, 0.0, 0.0, 0.0);
        RenderSystem.setShader(() -> shader);
        SimpleCloudsRenderer.prepareShader(shader, stack.m_85850_().m_252922_(), shadowMap.getProjMatrix(), this.fogStart, this.fogEnd);
        shader.m_173363_();
        shadowMap.bind();
        shadowMap.clear(Minecraft.f_91002_);
        this.meshGenerator.forRenderableMeshChunks(frustum, MeshChunk::getOpaqueBuffers, (chunk, opaqueBuffers) -> {
            GL30.glBindBufferBase((int)37074, (int)shader.getShaderStorageBinding(), (int)opaqueBuffers.getBufferId());
            this.meshGenerator.getSideMesh().drawInstanced(opaqueBuffers.getElementCount());
        });
        GL30.glBindBufferBase((int)37074, (int)shader.getShaderStorageBinding(), (int)0);
        GL30.glBindVertexArray((int)0);
        shadowMap.unbind();
        shader.m_173362_();
        stack.m_85849_();
    }

    private float determineShadowMapAngle(float partialTick) {
        float timeOfDay = this.mc.f_91073_.m_46942_(partialTick);
        return 45.0f * Mth.m_14031_((float)((float)Math.PI * 2 * timeOfDay));
    }

    private void renderShadowMaps(double camX, double camY, double camZ, float partialTick) {
        RenderSystem.assertOnRenderThread();
        BufferUploader.m_166835_();
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.disableCull();
        this.stormFogShadowMapStack = this.createShadowMapStack(this.stormFogShadowMap, camX, camY, camZ, s -> {
            Vector2f direction = this.cloudManager.calculateWindDirection();
            float yaw = (float)Mth.m_14136_((double)direction.x, (double)direction.y);
            s.m_252781_(Axis.f_252529_.m_252977_(((Double)SimpleCloudsConfig.CLIENT.stormFogAngle.get()).floatValue()));
            s.m_252781_(Axis.f_252436_.m_252961_(yaw));
        });
        this.renderShadowMap(this.stormFogShadowMap, this.stormFogShadowMapStack, SimpleCloudsShaders.getStormFogShadowMapShader(), this.cullFrustum);
        this.shadowMapStack = this.shadowMap.map(buffer -> {
            PoseStack stack = this.createShadowMapStack((ShadowMapBuffer)buffer, camX, camY, camZ, s -> {
                s.m_252781_(Axis.f_252529_.m_252977_(90.0f));
                s.m_252781_(Axis.f_252393_.m_252977_(this.determineShadowMapAngle(partialTick)));
            });
            this.renderShadowMap((ShadowMapBuffer)buffer, stack, SimpleCloudsShaders.getCloudsShadowMapShader(), null);
            return stack;
        }).orElse(null);
        RenderSystem.enableCull();
        this.mc.m_91385_().m_83947_(true);
    }

    public static void renderCloudsDebug(CloudMeshGenerator generator, PoseStack stack, Matrix4f projMat, float partialTick, float fogStart, float fogEnd, @Nullable Frustum frustum, boolean chunkBoundaries, boolean noiseBoundaries) {
        RenderSystem.assertOnRenderThread();
        if (!generator.canRender()) {
            return;
        }
        BufferUploader.m_166835_();
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.disableCull();
        Tesselator tesselator = Tesselator.m_85913_();
        BufferBuilder builder = tesselator.m_85915_();
        builder.m_166779_(VertexFormat.Mode.LINES, DefaultVertexFormat.f_166851_);
        generator.forRenderableMeshChunks(frustum, MeshChunk::getOpaqueBuffers, (chunk, bufferSet) -> {
            PreparedChunk preparedChunk = chunk.getChunkInfo();
            if (chunkBoundaries) {
                int color = Color.HSBtoRGB((float)preparedChunk.lodLevel() / ((float)generator.getLodConfig().getLods().length + 1.0f), 1.0f, 1.0f);
                float r = (float)FastColor.ARGB32.m_13665_((int)color) / 255.0f;
                float g = (float)FastColor.ARGB32.m_13667_((int)color) / 255.0f;
                float b = (float)FastColor.ARGB32.m_13669_((int)color) / 255.0f;
                LevelRenderer.m_172965_((VertexConsumer)builder, (double)(chunk.getBoundsMinX() + 1.0f), (double)(chunk.getBoundsMinY() + 1.0f), (double)(chunk.getBoundsMinZ() + 1.0f), (double)(chunk.getBoundsMaxX() - 1.0f), (double)(chunk.getBoundsMaxY() - 1.0f), (double)(chunk.getBoundsMaxZ() - 1.0f), (float)r, (float)g, (float)b, (float)1.0f);
            }
            if (noiseBoundaries) {
                LevelRenderer.m_172965_((VertexConsumer)builder, (double)(chunk.getBoundsMinX() + 1.0f), (double)(chunk.getMinHeight() + 1.0f), (double)(chunk.getBoundsMinZ() + 1.0f), (double)(chunk.getBoundsMaxX() - 1.0f), (double)(chunk.getMaxHeight() - 1.0f), (double)(chunk.getBoundsMaxZ() - 1.0f), (float)1.0f, (float)1.0f, (float)0.0f, (float)1.0f);
            }
        });
        RenderSystem.setShader(GameRenderer::m_172757_);
        ShaderInstance shader = RenderSystem.getShader();
        SimpleCloudsRenderer.prepareShader(shader, stack.m_85850_().m_252922_(), projMat, fogStart, fogEnd);
        shader.f_173318_.m_5985_(2.5f);
        shader.f_173315_.m_5985_(Float.MAX_VALUE);
        shader.m_173363_();
        BufferUploader.m_231209_((BufferBuilder.RenderedBuffer)builder.m_231175_());
        shader.m_173362_();
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();
        builder.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
        generator.forRenderableMeshChunks(frustum, MeshChunk::getOpaqueBuffers, (chunk, bufferSet) -> {
            PreparedChunk preparedChunk = chunk.getChunkInfo();
            if (chunkBoundaries) {
                int color = Color.HSBtoRGB((float)preparedChunk.lodLevel() / ((float)generator.getLodConfig().getLods().length + 1.0f), 1.0f, 1.0f);
                float r = (float)FastColor.ARGB32.m_13665_((int)color) / 255.0f;
                float g = (float)FastColor.ARGB32.m_13667_((int)color) / 255.0f;
                float b = (float)FastColor.ARGB32.m_13669_((int)color) / 255.0f;
                SimpleCloudsRenderer.renderChunkBox((VertexConsumer)builder, chunk.getBoundsMinX() + 1.0f, chunk.getBoundsMinY() + 1.0f, chunk.getBoundsMinZ() + 1.0f, chunk.getBoundsMaxX() - 1.0f, chunk.getBoundsMaxY() - 1.0f, chunk.getBoundsMaxZ() - 1.0f, r, g, b, 0.4f);
            }
            if (noiseBoundaries) {
                SimpleCloudsRenderer.renderChunkBox((VertexConsumer)builder, chunk.getBoundsMinX() + 1.0f, chunk.getMinHeight() + 1.0f, chunk.getBoundsMinZ() + 1.0f, chunk.getBoundsMaxX() - 1.0f, chunk.getMaxHeight() - 1.0f, chunk.getBoundsMaxZ() - 1.0f, 1.0f, 1.0f, 0.0f, 0.4f);
            }
        });
        RenderSystem.setShader(GameRenderer::m_172811_);
        shader = RenderSystem.getShader();
        SimpleCloudsRenderer.prepareShader(shader, stack.m_85850_().m_252922_(), projMat, fogStart, fogEnd);
        shader.m_173363_();
        BufferUploader.m_231209_((BufferBuilder.RenderedBuffer)builder.m_231175_());
        shader.m_173362_();
        RenderSystem.disableBlend();
    }

    public float[] getCloudColor(float partialTick) {
        Vec3 cloudCol = this.mc.f_91073_.m_104808_(partialTick);
        float factor = this.worldEffectsManager.getDarkenFactor(partialTick, 0.8f);
        float skyFlashFactor = Math.max(0.0f, ((float)this.mc.f_91073_.m_104819_() - partialTick) * 1.0f);
        float r = Mth.m_14036_((float)((float)cloudCol.f_82479_ * (factor += skyFlashFactor)), (float)0.0f, (float)1.0f);
        float g = Mth.m_14036_((float)((float)cloudCol.f_82480_ * factor), (float)0.0f, (float)1.0f);
        float b = Mth.m_14036_((float)((float)cloudCol.f_82481_ * factor), (float)0.0f, (float)1.0f);
        return new float[]{r, g, b};
    }

    public void translateClouds(PoseStack stack, double camX, double camY, double camZ) {
        stack.m_85837_(-camX, -camY + (double)this.cloudManager.getCloudHeight(), -camZ);
        stack.m_85841_(8.0f, 8.0f, 8.0f);
    }

    public void renderWeather(LightTexture texture, float partialTick, double camX, double camY, double camZ) {
        if (SimpleCloudsCompatHelper.renderCustomRain()) {
            this.worldEffectsManager.renderRain(texture, partialTick, camX, camY, camZ);
        }
        if (!SimpleCloudsMod.dhLoaded()) {
            this.worldEffectsManager.renderLightning(partialTick, camX, camY, camZ);
        }
    }

    public void renderBeforeLevel(PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ) {
        if (!SimpleCloudsCompatHelper.renderThisPass()) {
            return;
        }
        CloudsRenderPipeline pipeline = CompatHelper.areShadersRunning() ? CloudsRenderPipeline.SHADER_SUPPORT : CloudsRenderPipeline.DEFAULT;
        DetermineCloudRenderPipelineEvent pipelineEvent = new DetermineCloudRenderPipelineEvent(pipeline);
        MinecraftForge.EVENT_BUS.post((Event)pipelineEvent);
        this.renderPipelineThisPass = pipeline;
        if (pipelineEvent.getOverridenPipeline() != null) {
            this.renderPipelineThisPass = pipelineEvent.getOverridenPipeline();
        }
        float factor = this.worldEffectsManager.getDarkenFactor(partialTick);
        float renderDistance = (float)this.meshGenerator.getCloudAreaMaxRadius() * 8.0f * factor;
        if (renderDistance < 2867.0f) {
            renderDistance = 2867.0f;
        }
        ModifyCloudRenderDistanceEvent renderDistEvent = new ModifyCloudRenderDistanceEvent(renderDistance);
        MinecraftForge.EVENT_BUS.post((Event)renderDistEvent);
        renderDistance = renderDistEvent.getRenderDistance();
        this.fogStart = renderDistance / 4.0f;
        this.fogEnd = renderDistance;
        Entity cameraEntity = this.mc.f_91063_.m_109153_().m_90592_();
        if (cameraEntity instanceof LivingEntity) {
            MobEffectInstance instance;
            LivingEntity living = (LivingEntity)cameraEntity;
            Map map = living.m_21221_();
            if (map.containsKey(MobEffects.f_19610_)) {
                MobEffectInstance instance2 = (MobEffectInstance)map.get(MobEffects.f_19610_);
                float effectFactor = instance2.m_267577_() ? 5.0f : Mth.m_14179_((float)Math.min(1.0f, (float)instance2.m_19557_() / 20.0f), (float)renderDistance, (float)5.0f);
                this.fogStart = 0.0f;
                this.fogEnd = effectFactor * 0.8f;
            } else if (map.containsKey(MobEffects.f_216964_) && (instance = (MobEffectInstance)map.get(MobEffects.f_216964_)).m_216895_().isPresent()) {
                float f = Mth.m_14179_((float)((MobEffectInstance.FactorData)instance.m_216895_().get()).m_238413_(living, partialTick), (float)renderDistance, (float)15.0f);
                this.fogStart = 0.0f;
                this.fogEnd = f;
            }
        }
        this.meshGenerator.setCullDistance(this.fogEnd / 8.0f);
        this.mc.m_91307_().m_6180_("simple_clouds_prepare");
        this.cullFrustum = new Frustum(stack.m_85850_().m_252922_(), projMat);
        float scale = 8.0f;
        double originX = camX / (double)scale;
        double originY = (camY - (double)this.cloudManager.getCloudHeight()) / (double)scale;
        double originZ = camZ / (double)scale;
        this.cullFrustum.m_113002_(originX, originY, originZ);
        ProfilerFiller p = this.mc.m_91307_();
        if (((Boolean)SimpleCloudsConfig.CLIENT.generateMesh.get()).booleanValue() && SimpleCloudsCompatHelper.isPrimaryPass()) {
            p.m_6180_("mesh_generation");
            this.prepareMeshGenerator(partialTick);
            this.meshGenerator.genTick(originX, originY, originZ, (Boolean)SimpleCloudsConfig.CLIENT.frustumCulling.get() != false ? this.cullFrustum : null, partialTick);
            p.m_7238_();
        }
        if (((Boolean)SimpleCloudsConfig.CLIENT.renderClouds.get()).booleanValue() && SimpleCloudsCompatHelper.isPrimaryPass()) {
            p.m_6180_("shadow_map");
            this.renderShadowMaps(camX, camY, camZ, partialTick);
            this.getRenderPipeline().prepare(this.mc, this, stack, projMat, partialTick, camX, camY, camZ, this.cullFrustum);
            p.m_7238_();
        }
        this.mc.m_91307_().m_7238_();
    }

    public void renderAfterSky(PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ) {
        if (!SimpleCloudsCompatHelper.renderThisPass()) {
            return;
        }
        this.mc.m_91307_().m_6180_("simple_clouds_after_sky");
        this.getRenderPipeline().afterSky(this.mc, this, stack, projMat, partialTick, camX, camY, camZ, this.cullFrustum);
        this.mc.m_91307_().m_7238_();
    }

    public void renderBeforeWeather(PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ) {
        if (!SimpleCloudsCompatHelper.renderThisPass()) {
            return;
        }
        this.mc.m_91307_().m_6180_("simple_clouds_before_weather");
        this.getRenderPipeline().beforeWeather(this.mc, this, stack, projMat, partialTick, camX, camY, camZ, this.cullFrustum);
        this.mc.m_91307_().m_7238_();
    }

    public void renderAfterLevel(PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ) {
        if (!SimpleCloudsCompatHelper.renderThisPass()) {
            return;
        }
        this.mc.m_91307_().m_6180_("simple_clouds");
        this.getRenderPipeline().afterLevel(this.mc, this, stack, projMat, partialTick, camX, camY, camZ, this.cullFrustum);
        this.mc.m_91307_().m_7238_();
        this.mc.m_91307_().m_6180_("world_effects");
        this.worldEffectsManager.renderPost(stack, partialTick, camX, camY, camZ, 8.0f);
        this.mc.m_91307_().m_7238_();
    }

    public void doBlurPostProcessing(float partialTick) {
        if (this.blurPostProcessing != null) {
            RenderSystem.disableDepthTest();
            RenderSystem.resetTextureMatrix();
            RenderSystem.disableBlend();
            RenderSystem.depthMask((boolean)false);
            this.blurPostProcessing.m_110023_(partialTick);
            RenderSystem.depthMask((boolean)true);
        }
    }

    public void doScreenSpaceWorldFog(PoseStack stack, Matrix4f projMat, float partialTick) {
        if (this.screenSpaceWorldFog != null) {
            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.resetTextureMatrix();
            RenderSystem.depthMask((boolean)false);
            Matrix4f invertedProjMat = new Matrix4f((Matrix4fc)projMat).invert();
            Matrix4f invertedModelViewMat = new Matrix4f((Matrix4fc)stack.m_85850_().m_252922_()).invert();
            for (PostPass pass : ((MixinPostChain)this.screenSpaceWorldFog).simpleclouds$getPostPasses()) {
                EffectInstance effect = pass.m_110074_();
                effect.m_108960_("InverseWorldProjMat").m_5679_(invertedProjMat);
                effect.m_108960_("InverseModelViewMat").m_5679_(invertedModelViewMat);
                effect.m_108960_("FogStart").m_5985_(RenderSystem.getShaderFogStart());
                effect.m_108960_("FogEnd").m_5985_(RenderSystem.getShaderFogEnd());
                float[] fogCol = RenderSystem.getShaderFogColor();
                effect.m_108960_("FogColor").m_5889_(fogCol[0], fogCol[1], fogCol[2]);
                effect.m_108960_("FogShape").m_142617_(RenderSystem.getShaderFogShape().m_202324_());
            }
            this.screenSpaceWorldFog.m_110023_(partialTick);
            RenderSystem.depthMask((boolean)true);
        }
    }

    public void doFinalCompositePass(PoseStack stack, float partialTick, Matrix4f projMat) {
        if (this.finalComposite != null) {
            RenderSystem.disableDepthTest();
            RenderSystem.resetTextureMatrix();
            RenderSystem.depthMask((boolean)false);
            this.finalComposite.m_110023_(partialTick);
            RenderSystem.depthMask((boolean)true);
        }
    }

    public void doStormPostProcessing(PoseStack stack, float partialTick, Matrix4f projMat, double camX, double camY, double camZ, float r, float g, float b) {
        if (this.stormPostProcessing == null || this.stormFogShadowMapStack == null || this.stormFogShadowMapStack == null) {
            return;
        }
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.resetTextureMatrix();
        RenderSystem.depthMask((boolean)false);
        this.stormFogTarget.m_83954_(Minecraft.f_91002_);
        this.stormFogTarget.m_83947_(true);
        MutableInt size = new MutableInt();
        boolean flag = (Boolean)SimpleCloudsConfig.CLIENT.stormFogLightningFlashes.get();
        if (flag) {
            List<LightningBolt> lightningBolts = this.worldEffectsManager.getLightningBolts();
            size.setValue(Math.min(lightningBolts.size(), 16));
            if (size.getValue() > 0) {
                this.lightningBoltPositions.writeData(buffer -> {
                    for (int i = 0; i < size.getValue(); ++i) {
                        LightningBolt bolt = (LightningBolt)lightningBolts.get(i);
                        Vector3f pos = bolt.getPosition();
                        buffer.putFloat(pos.x);
                        buffer.putFloat(pos.y);
                        buffer.putFloat(pos.z);
                        buffer.putFloat(bolt.getFade(partialTick));
                    }
                    buffer.rewind();
                }, size.getValue() * 16, false);
            }
        }
        Matrix4f invertedProjMat = new Matrix4f((Matrix4fc)projMat).invert();
        Matrix4f invertedModelViewMat = new Matrix4f((Matrix4fc)stack.m_85850_().m_252922_()).invert();
        for (PostPass pass : ((MixinPostChain)this.stormPostProcessing).simpleclouds$getPostPasses()) {
            EffectInstance effect = pass.m_110074_();
            effect.m_108960_("InverseWorldProjMat").m_5679_(invertedProjMat);
            effect.m_108960_("InverseModelViewMat").m_5679_(invertedModelViewMat);
            effect.m_108960_("ShadowProjMat").m_5679_(this.stormFogShadowMap.getProjMatrix());
            effect.m_108960_("ShadowModelViewMat").m_5679_(this.stormFogShadowMapStack.m_85850_().m_252922_());
            effect.m_108960_("CameraPos").m_5889_((float)camX, (float)camY, (float)camZ);
            effect.m_108960_("FogStart").m_5985_(this.fogEnd / 2.0f);
            effect.m_108960_("FogEnd").m_5985_(this.fogEnd);
            effect.m_108960_("ColorModulator").m_5805_(r, g, b, 1.0f);
            float factor = this.worldEffectsManager.getDarkenFactor(partialTick);
            effect.m_108960_("CutoffDistance").m_5985_(1000.0f * factor);
            effect.m_108960_("TotalLightningBolts").m_142617_(size.getValue().intValue());
        }
        this.stormPostProcessing.m_110023_(partialTick);
        RenderSystem.depthMask((boolean)true);
    }

    public void doCloudShadowProcessing(PoseStack stack, float partialTick, Matrix4f projMat, double camX, double camY, double camZ, int depthBufferId) {
        if (this.cloudShadows == null || this.shadowMap.isEmpty() || this.shadowMapStack == null) {
            return;
        }
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.resetTextureMatrix();
        RenderSystem.depthMask((boolean)false);
        Matrix4f invertedProjMat = new Matrix4f((Matrix4fc)projMat).invert();
        Matrix4f invertedModelViewMat = new Matrix4f((Matrix4fc)stack.m_85850_().m_252922_()).invert();
        float minimumRadius = this.mc.f_91063_.m_109152_();
        for (PostPass pass : ((MixinPostChain)this.cloudShadows).simpleclouds$getPostPasses()) {
            EffectInstance effect = pass.m_110074_();
            effect.m_108954_("DepthSampler", () -> depthBufferId);
            effect.m_108960_("InverseWorldProjMat").m_5679_(invertedProjMat);
            effect.m_108960_("InverseModelViewMat").m_5679_(invertedModelViewMat);
            effect.m_108960_("ShadowProjMat").m_5679_(this.shadowMap.get().getProjMatrix());
            effect.m_108960_("ShadowModelViewMat").m_5679_(this.shadowMapStack.m_85850_().m_252922_());
            effect.m_108960_("CameraPos").m_5889_((float)camX, (float)camY, (float)camZ);
            effect.m_108960_("MinimumRadius").m_5985_(minimumRadius);
        }
        this.cloudShadows.m_110023_(partialTick);
        RenderSystem.depthMask((boolean)true);
    }

    public static void prepareShader(ShaderInstance shader, Matrix4f modelView, Matrix4f projMat, float fogStart, float fogEnd) {
        for (int i = 0; i < 12; ++i) {
            int j = RenderSystem.getShaderTexture((int)i);
            shader.m_173350_("Sampler" + i, (Object)j);
        }
        if (shader.f_173308_ != null) {
            shader.f_173308_.m_5679_(modelView);
        }
        if (shader.f_173309_ != null) {
            shader.f_173309_.m_5679_(projMat);
        }
        if (shader.f_200956_ != null) {
            shader.f_200956_.m_200759_(RenderSystem.getInverseViewRotationMatrix());
        }
        if (shader.f_173312_ != null) {
            shader.f_173312_.m_5941_(RenderSystem.getShaderColor());
        }
        if (shader.f_267422_ != null) {
            shader.f_267422_.m_5985_(RenderSystem.getShaderGlintAlpha());
        }
        if (shader.f_173315_ != null) {
            shader.f_173315_.m_5985_(fogStart);
        }
        if (shader.f_173316_ != null) {
            shader.f_173316_.m_5985_(fogEnd);
        }
        if (shader.f_173317_ != null) {
            shader.f_173317_.m_5941_(RenderSystem.getShaderFogColor());
        }
        if (shader.f_202432_ != null) {
            shader.f_202432_.m_142617_(RenderSystem.getShaderFogShape().m_202324_());
        }
        if (shader.f_173310_ != null) {
            shader.f_173310_.m_5679_(RenderSystem.getTextureMatrix());
        }
        if (shader.f_173319_ != null) {
            shader.f_173319_.m_5985_(RenderSystem.getShaderGameTime());
        }
        if (shader.f_173311_ != null) {
            Window window = Minecraft.m_91087_().m_91268_();
            shader.f_173311_.m_7971_((float)window.m_85441_(), (float)window.m_85442_());
        }
        shader.m_173356_("UseNormals").m_142617_((Boolean)SimpleCloudsConfig.CLIENT.cubeNormals.get() != false ? 1 : 0);
        RenderSystem.setShaderLights((Vector3f)DIFFUSE_LIGHT_0, (Vector3f)DIFFUSE_LIGHT_1);
        RenderSystem.setupShaderLights((ShaderInstance)shader);
    }

    public void copyDepthFromCloudsToMain() {
        this._copyDepthSafe(this.mc.m_91385_(), this.cloudTarget);
    }

    public void copyDepthFromMainToClouds() {
        this._copyDepthSafe(this.cloudTarget, this.mc.m_91385_());
    }

    public void copyDepthFromCloudsToTransparency() {
        this._copyDepthSafe(this.cloudTransparencyTarget, this.cloudTarget);
    }

    private void _copyDepthSafe(RenderTarget to, RenderTarget from) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._getError();
        if (!this.failedToCopyDepthBuffer) {
            to.m_83947_(false);
            to.m_83945_(from);
            if (GlStateManager._getError() != 1282) {
                return;
            }
            boolean enabledStencil = false;
            if (to.isStencilEnabled() && !from.isStencilEnabled()) {
                from.enableStencil();
                enabledStencil = true;
            } else if (from.isStencilEnabled() && !to.isStencilEnabled()) {
                to.enableStencil();
                enabledStencil = true;
            }
            if (enabledStencil) {
                to.m_83945_(from);
                if (GlStateManager._getError() == 1282) {
                    LOGGER.error("Unable to copy depth between the main and clouds frame buffers, even after enabling stencil. Please note that the clouds may not render properly.");
                    this.failedToCopyDepthBuffer = true;
                } else {
                    LOGGER.info("NOTE: Please ignore the above OpenGL error. Simple Clouds had to toggle stencil in order to copy the depth buffer between the main and clouds frame buffers.");
                }
            } else {
                LOGGER.error("Unable to copy depth between the main and clouds frame buffers. Please note that the clouds may not render properly.");
                this.failedToCopyDepthBuffer = true;
            }
        }
    }

    public void fillReport(CrashReport report) {
        CrashReportCategory category = report.m_127514_("Simple Clouds Renderer");
        category.m_128159_("Cloud Mode", (Object)this.settings.getCurrentCloudMode());
        category.m_128159_("Cloud Target Available", (Object)(this.cloudTarget != null ? 1 : 0));
        category.m_128159_("Storm Fog Target Active", (Object)(this.stormFogTarget != null ? 1 : 0));
        category.m_128159_("Blur Target Active", (Object)(this.blurTarget != null ? 1 : 0));
        category.m_128159_("Transparency Target Active", (Object)(this.cloudTransparencyTarget != null ? 1 : 0));
        category.m_128159_("Post Chains", (Object)this.postChains.toString());
        category.m_128159_("Lightning Bolt SSBO", (Object)this.lightningBoltPositions);
        category.m_128159_("Clouds Shadow Map", (Object)this.stormFogShadowMap);
        category.m_128159_("Storm Fog Shadow Map", (Object)this.stormFogShadowMap);
        category.m_128159_("Failed to copy depth buffer", (Object)this.failedToCopyDepthBuffer);
        category.m_128159_("Needs Reload", (Object)this.needsReload);
        CrashReportCategory meshGenCategory = report.m_127514_("Cloud Mesh Generator");
        if (this.meshGenerator != null) {
            meshGenCategory.m_128159_("Type", (Object)this.meshGenerator.toString());
            this.meshGenerator.fillReport(meshGenCategory);
        } else {
            meshGenCategory.m_128159_("Type", (Object)"Mesh generator is not initialized");
        }
    }

    public static void initialize(CloudsRendererSettings settings) {
        RenderSystem.assertOnRenderThread();
        if (instance != null) {
            throw new IllegalStateException("Simple Clouds renderer is already initialized");
        }
        instance = new SimpleCloudsRenderer(settings, Minecraft.m_91087_());
        LOGGER.debug("Clouds render initialized");
    }

    public static SimpleCloudsRenderer getInstance() {
        return Objects.requireNonNull(instance, "Renderer not initialized!");
    }

    public static Optional<SimpleCloudsRenderer> getOptionalInstance() {
        return Optional.ofNullable(instance);
    }

    public static boolean canRenderInDimension(@Nullable ClientLevel level) {
        boolean useAsBlacklist;
        List whitelist;
        if (level == null) {
            return false;
        }
        if (ClientCloudManager.isAvailableServerSide() && SimpleCloudsConfig.SERVER_SPEC.isLoaded()) {
            whitelist = (List)SimpleCloudsConfig.SERVER.dimensionWhitelist.get();
            useAsBlacklist = (Boolean)SimpleCloudsConfig.SERVER.whitelistAsBlacklist.get();
        } else {
            whitelist = (List)SimpleCloudsConfig.CLIENT.dimensionWhitelist.get();
            useAsBlacklist = (Boolean)SimpleCloudsConfig.CLIENT.whitelistAsBlacklist.get();
        }
        boolean flag = whitelist.stream().anyMatch(val -> level.m_46472_().m_135782_().toString().equals(val));
        return useAsBlacklist ? !flag : flag;
    }

    private static void renderChunkBox(VertexConsumer consumer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float r, float g, float b, float a) {
        consumer.m_5483_((double)minX, (double)minY, (double)maxZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)minX, (double)maxY, (double)maxZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)minX, (double)maxY, (double)minZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)minX, (double)minY, (double)minZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)maxX, (double)minY, (double)minZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)maxX, (double)maxY, (double)minZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)maxX, (double)maxY, (double)maxZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)maxX, (double)minY, (double)maxZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)maxX, (double)minY, (double)minZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)maxX, (double)minY, (double)maxZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)minX, (double)minY, (double)maxZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)minX, (double)minY, (double)minZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)minX, (double)maxY, (double)minZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)minX, (double)maxY, (double)maxZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)maxX, (double)maxY, (double)maxZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)maxX, (double)maxY, (double)minZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)minX, (double)minY, (double)minZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)minX, (double)maxY, (double)minZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)maxX, (double)maxY, (double)minZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)maxX, (double)minY, (double)minZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)maxX, (double)minY, (double)maxZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)maxX, (double)maxY, (double)maxZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)minX, (double)maxY, (double)maxZ).m_85950_(r, g, b, a).m_5752_();
        consumer.m_5483_((double)minX, (double)minY, (double)maxZ).m_85950_(r, g, b, a).m_5752_();
    }

    private static int calculateMeshGenInterval() {
        int fps = Minecraft.m_91087_().m_260875_();
        switch ((GenerationInterval)((Object)SimpleCloudsConfig.CLIENT.generationInterval.get())) {
            case STATIC: {
                return (Integer)SimpleCloudsConfig.CLIENT.framesToGenerateMesh.get();
            }
            case DYNAMIC: {
                return Math.max(Mth.m_14167_((float)((130.0f - (float)fps) / 30.0f)) + 5, 1);
            }
            case TARGET_FPS: {
                return Math.max(Mth.m_14167_((float)((float)fps / (float)((Integer)SimpleCloudsConfig.CLIENT.targetMeshGenFps.get()).intValue())), 1);
            }
        }
        return 5;
    }
}
