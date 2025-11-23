/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderBuffers
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3d
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 */
package net.irisshaders.iris.shadows;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import net.irisshaders.batchedentityrendering.impl.BatchingDebugMessageHelper;
import net.irisshaders.batchedentityrendering.impl.DrawCallTrackingRenderBuffers;
import net.irisshaders.batchedentityrendering.impl.FullyBufferedMultiBufferSource;
import net.irisshaders.batchedentityrendering.impl.MemoryTrackingRenderBuffers;
import net.irisshaders.batchedentityrendering.impl.RenderBuffersExt;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.compat.dh.DHCompat;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gui.option.IrisVideoSettings;
import net.irisshaders.iris.mixin.LevelRendererAccessor;
import net.irisshaders.iris.shaderpack.programs.ProgramSource;
import net.irisshaders.iris.shaderpack.properties.PackDirectives;
import net.irisshaders.iris.shaderpack.properties.PackShadowDirectives;
import net.irisshaders.iris.shaderpack.properties.ShadowCullState;
import net.irisshaders.iris.shadows.CullingDataCache;
import net.irisshaders.iris.shadows.ShadowCompositeRenderer;
import net.irisshaders.iris.shadows.ShadowMatrices;
import net.irisshaders.iris.shadows.ShadowRenderTargets;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.irisshaders.iris.shadows.frustum.BoxCuller;
import net.irisshaders.iris.shadows.frustum.CullEverythingFrustum;
import net.irisshaders.iris.shadows.frustum.FrustumHolder;
import net.irisshaders.iris.shadows.frustum.advanced.AdvancedShadowCullingFrustum;
import net.irisshaders.iris.shadows.frustum.advanced.ReversedAdvancedShadowCullingFrustum;
import net.irisshaders.iris.shadows.frustum.fallback.BoxCullingFrustum;
import net.irisshaders.iris.shadows.frustum.fallback.NonCullingFrustum;
import net.irisshaders.iris.uniforms.CameraUniforms;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.uniforms.CelestialUniforms;
import net.irisshaders.iris.uniforms.custom.CustomUniforms;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ShadowRenderer {
    public static boolean ACTIVE = false;
    public static List<BlockEntity> visibleBlockEntities;
    public static int renderDistance;
    public static Matrix4f MODELVIEW;
    public static Matrix4f PROJECTION;
    public static Frustum FRUSTUM;
    private final float halfPlaneLength;
    private final float nearPlane;
    private final float farPlane;
    private final float voxelDistance;
    private final float renderDistanceMultiplier;
    private final float entityShadowDistanceMultiplier;
    private final int resolution;
    private final float intervalSize;
    private final Float fov;
    private final ShadowRenderTargets targets;
    private final ShadowCullState packCullingState;
    private final ShadowCompositeRenderer compositeRenderer;
    private final boolean shouldRenderTerrain;
    private final boolean shouldRenderTranslucent;
    private final boolean shouldRenderEntities;
    private final boolean shouldRenderPlayer;
    private final boolean shouldRenderBlockEntities;
    private final boolean shouldRenderDH;
    private final float sunPathRotation;
    private final RenderBuffers buffers;
    private final RenderBuffersExt renderBuffersExt;
    private final List<MipmapPass> mipmapPasses = new ArrayList<MipmapPass>();
    private final String debugStringOverall;
    private final boolean separateHardwareSamplers;
    private final boolean shouldRenderLightBlockEntities;
    private boolean packHasVoxelization;
    private FrustumHolder terrainFrustumHolder;
    private FrustumHolder entityFrustumHolder;
    private String debugStringTerrain = "(unavailable)";
    private int renderedShadowEntities = 0;
    private int renderedShadowBlockEntities = 0;

    public ShadowRenderer(ProgramSource shadow, PackDirectives directives, ShadowRenderTargets shadowRenderTargets, ShadowCompositeRenderer compositeRenderer, CustomUniforms customUniforms, boolean separateHardwareSamplers) {
        this.separateHardwareSamplers = separateHardwareSamplers;
        PackShadowDirectives shadowDirectives = directives.getShadowDirectives();
        this.halfPlaneLength = shadowDirectives.getDistance();
        this.nearPlane = shadowDirectives.getNearPlane();
        this.farPlane = shadowDirectives.getFarPlane();
        this.voxelDistance = shadowDirectives.getVoxelDistance();
        this.renderDistanceMultiplier = shadowDirectives.getDistanceRenderMul();
        this.entityShadowDistanceMultiplier = shadowDirectives.getEntityShadowDistanceMul();
        this.resolution = shadowDirectives.getResolution();
        this.intervalSize = shadowDirectives.getIntervalSize();
        this.shouldRenderTerrain = shadowDirectives.shouldRenderTerrain();
        this.shouldRenderTranslucent = shadowDirectives.shouldRenderTranslucent();
        this.shouldRenderEntities = shadowDirectives.shouldRenderEntities();
        this.shouldRenderPlayer = shadowDirectives.shouldRenderPlayer();
        this.shouldRenderBlockEntities = shadowDirectives.shouldRenderBlockEntities();
        this.shouldRenderLightBlockEntities = shadowDirectives.shouldRenderLightBlockEntities();
        this.shouldRenderDH = shadowDirectives.isDhShadowEnabled().orElse(false);
        this.compositeRenderer = compositeRenderer;
        this.debugStringOverall = "half plane = " + this.halfPlaneLength + " meters @ " + this.resolution + "x" + this.resolution;
        this.terrainFrustumHolder = new FrustumHolder();
        this.entityFrustumHolder = new FrustumHolder();
        this.fov = shadowDirectives.getFov();
        this.targets = shadowRenderTargets;
        if (shadow != null) {
            this.packHasVoxelization = shadow.getGeometrySource().isPresent();
            this.packCullingState = shadowDirectives.getCullingState();
        } else {
            this.packHasVoxelization = false;
            this.packCullingState = ShadowCullState.DEFAULT;
        }
        this.sunPathRotation = directives.getSunPathRotation();
        this.buffers = new RenderBuffers();
        this.renderBuffersExt = this.buffers instanceof RenderBuffersExt ? (RenderBuffersExt)this.buffers : null;
        this.configureSamplingSettings(shadowDirectives);
    }

    public static PoseStack createShadowModelView(float sunPathRotation, float intervalSize) {
        Vector3d cameraPos = CameraUniforms.getUnshiftedCameraPosition();
        double cameraX = cameraPos.x;
        double cameraY = cameraPos.y;
        double cameraZ = cameraPos.z;
        PoseStack modelView = new PoseStack();
        ShadowMatrices.createModelViewMatrix(modelView, ShadowRenderer.getShadowAngle(), intervalSize, sunPathRotation, cameraX, cameraY, cameraZ);
        return modelView;
    }

    private static ClientLevel getLevel() {
        return Objects.requireNonNull(Minecraft.m_91087_().f_91073_);
    }

    private static float getSkyAngle() {
        return ShadowRenderer.getLevel().m_46942_(CapturedRenderingState.INSTANCE.getTickDelta());
    }

    private static float getSunAngle() {
        float skyAngle = ShadowRenderer.getSkyAngle();
        if (skyAngle < 0.75f) {
            return skyAngle + 0.25f;
        }
        return skyAngle - 0.75f;
    }

    private static float getShadowAngle() {
        float shadowAngle = ShadowRenderer.getSunAngle();
        if (!CelestialUniforms.isDay()) {
            shadowAngle -= 0.5f;
        }
        return shadowAngle;
    }

    public void setUsesImages(boolean usesImages) {
        this.packHasVoxelization = this.packHasVoxelization || usesImages;
    }

    private void configureSamplingSettings(PackShadowDirectives shadowDirectives) {
        ImmutableList<PackShadowDirectives.DepthSamplingSettings> depthSamplingSettings = shadowDirectives.getDepthSamplingSettings();
        Int2ObjectMap<PackShadowDirectives.SamplingSettings> colorSamplingSettings = shadowDirectives.getColorSamplingSettings();
        RenderSystem.activeTexture((int)33988);
        this.configureDepthSampler(this.targets.getDepthTexture().getTextureId(), (PackShadowDirectives.DepthSamplingSettings)depthSamplingSettings.get(0));
        this.configureDepthSampler(this.targets.getDepthTextureNoTranslucents().getTextureId(), (PackShadowDirectives.DepthSamplingSettings)depthSamplingSettings.get(1));
        for (int i = 0; i < this.targets.getNumColorTextures(); ++i) {
            if (this.targets.get(i) == null) continue;
            int glTextureId = this.targets.get(i).getMainTexture();
            this.configureSampler(glTextureId, (PackShadowDirectives.SamplingSettings)colorSamplingSettings.computeIfAbsent(i, a -> new PackShadowDirectives.SamplingSettings()));
        }
        RenderSystem.activeTexture((int)33984);
    }

    private void configureDepthSampler(int glTextureId, PackShadowDirectives.DepthSamplingSettings settings) {
        if (settings.getHardwareFiltering() && !this.separateHardwareSamplers) {
            IrisRenderSystem.texParameteri(glTextureId, 3553, 34892, 34894);
        }
        IrisRenderSystem.texParameteriv(glTextureId, 3553, 36422, new int[]{6403, 6403, 6403, 1});
        this.configureSampler(glTextureId, settings);
    }

    private void configureSampler(int glTextureId, PackShadowDirectives.SamplingSettings settings) {
        if (settings.getMipmap()) {
            int filteringMode = settings.getNearest() ? 9984 : 9987;
            this.mipmapPasses.add(new MipmapPass(glTextureId, filteringMode));
        }
        if (!settings.getNearest()) {
            IrisRenderSystem.texParameteri(glTextureId, 3553, 10241, 9729);
            IrisRenderSystem.texParameteri(glTextureId, 3553, 10240, 9729);
        } else {
            IrisRenderSystem.texParameteri(glTextureId, 3553, 10241, 9728);
            IrisRenderSystem.texParameteri(glTextureId, 3553, 10240, 9728);
        }
    }

    private void generateMipmaps() {
        RenderSystem.activeTexture((int)33988);
        for (MipmapPass mipmapPass : this.mipmapPasses) {
            this.setupMipmappingForTexture(mipmapPass.texture(), mipmapPass.targetFilteringMode());
        }
        RenderSystem.activeTexture((int)33984);
    }

    private void setupMipmappingForTexture(int texture, int filteringMode) {
        IrisRenderSystem.generateMipmaps(texture, 3553);
        IrisRenderSystem.texParameteri(texture, 3553, 10241, filteringMode);
    }

    private FrustumHolder createShadowFrustum(float renderMultiplier, FrustumHolder holder) {
        String reason;
        double distance;
        if (this.packCullingState == ShadowCullState.DEFAULT && this.packHasVoxelization || this.packCullingState == ShadowCullState.DISTANCE) {
            distance = this.halfPlaneLength * renderMultiplier;
            reason = this.packCullingState == ShadowCullState.DISTANCE ? "(set by shader pack)" : "(voxelization detected)";
            if (distance <= 0.0 || distance > (double)(Minecraft.m_91087_().f_91066_.m_193772_() * 16)) {
                String distanceInfo = "render distance = " + Minecraft.m_91087_().f_91066_.m_193772_() * 16 + " blocks ";
                distanceInfo = distanceInfo + (Minecraft.m_91087_().m_91090_() ? "(capped by normal render distance)" : "(capped by normal/server render distance)");
                String cullingInfo = "disabled " + reason;
                return holder.setInfo(new NonCullingFrustum(), distanceInfo, cullingInfo);
            }
        } else {
            Object cullingInfo;
            BoxCuller boxCuller;
            String distanceInfo;
            boolean isReversed;
            boolean bl = isReversed = this.packCullingState == ShadowCullState.REVERSED;
            if (isReversed && renderMultiplier < 0.0f) {
                renderMultiplier = 1.0f;
            }
            double distance2 = (isReversed ? this.voxelDistance : this.halfPlaneLength) * renderMultiplier;
            String setter = "(set by shader pack)";
            if (renderMultiplier < 0.0f) {
                distance2 = IrisVideoSettings.shadowDistance * 16;
                setter = "(set by user)";
            }
            if (distance2 >= (double)(Minecraft.m_91087_().f_91066_.m_193772_() * 16) && !isReversed) {
                distanceInfo = "render distance = " + Minecraft.m_91087_().f_91066_.m_193772_() * 16 + " blocks ";
                distanceInfo = (String)distanceInfo + (Minecraft.m_91087_().m_91090_() ? "(capped by normal render distance)" : "(capped by normal/server render distance)");
                boxCuller = null;
            } else {
                distanceInfo = distance2 + " blocks " + setter;
                if (distance2 == 0.0 && !isReversed) {
                    cullingInfo = "no shadows rendered";
                    holder.setInfo(new CullEverythingFrustum(), distanceInfo, (String)cullingInfo);
                }
                boxCuller = new BoxCuller(distance2);
            }
            cullingInfo = (isReversed ? "Reversed" : "Advanced") + " Frustum Culling enabled";
            Vector4f shadowLightPosition = new CelestialUniforms(this.sunPathRotation).getShadowLightPositionInWorldSpace();
            Vector3f shadowLightVectorFromOrigin = new Vector3f(shadowLightPosition.x(), shadowLightPosition.y(), shadowLightPosition.z());
            shadowLightVectorFromOrigin.normalize();
            if (isReversed) {
                return holder.setInfo(new ReversedAdvancedShadowCullingFrustum(CapturedRenderingState.INSTANCE.getGbufferModelView(), this.shouldRenderDH && DHCompat.hasRenderingEnabled() ? DHCompat.getProjection() : CapturedRenderingState.INSTANCE.getGbufferProjection(), shadowLightVectorFromOrigin, boxCuller, new BoxCuller(this.halfPlaneLength * renderMultiplier)), distanceInfo, (String)cullingInfo);
            }
            return holder.setInfo(new AdvancedShadowCullingFrustum(CapturedRenderingState.INSTANCE.getGbufferModelView(), this.shouldRenderDH && DHCompat.hasRenderingEnabled() ? DHCompat.getProjection() : CapturedRenderingState.INSTANCE.getGbufferProjection(), shadowLightVectorFromOrigin, boxCuller), distanceInfo, (String)cullingInfo);
        }
        String distanceInfo = distance + " blocks (set by shader pack)";
        String cullingInfo = "distance only " + reason;
        BoxCuller boxCuller = new BoxCuller(distance);
        holder.setInfo(new BoxCullingFrustum(boxCuller), distanceInfo, cullingInfo);
        return holder;
    }

    public void setupShadowViewport() {
        RenderSystem.viewport((int)0, (int)0, (int)this.resolution, (int)this.resolution);
    }

    public void renderShadows(LevelRendererAccessor levelRenderer, Camera playerCamera) {
        if (IrisVideoSettings.getOverriddenShadowDistance(IrisVideoSettings.shadowDistance) == 0) {
            return;
        }
        Minecraft client = Minecraft.m_91087_();
        levelRenderer.getLevel().m_46473_().m_6182_("shadows");
        ACTIVE = true;
        renderDistance = (int)(this.halfPlaneLength * this.renderDistanceMultiplier / 16.0f);
        if (this.renderDistanceMultiplier < 0.0f) {
            renderDistance = IrisVideoSettings.shadowDistance;
        }
        visibleBlockEntities = new ArrayList<BlockEntity>();
        RenderBuffers playerBuffers = levelRenderer.getRenderBuffers();
        levelRenderer.setRenderBuffers(this.buffers);
        visibleBlockEntities = new ArrayList<BlockEntity>();
        this.setupShadowViewport();
        PoseStack modelView = ShadowRenderer.createShadowModelView(this.sunPathRotation, this.intervalSize);
        MODELVIEW = new Matrix4f((Matrix4fc)modelView.m_85850_().m_252922_());
        levelRenderer.getLevel().m_46473_().m_6180_("terrain_setup");
        if (levelRenderer instanceof CullingDataCache) {
            ((CullingDataCache)((Object)levelRenderer)).saveState();
        }
        levelRenderer.getLevel().m_46473_().m_6180_("initialize frustum");
        this.terrainFrustumHolder = this.createShadowFrustum(this.renderDistanceMultiplier, this.terrainFrustumHolder);
        FRUSTUM = this.terrainFrustumHolder.getFrustum();
        Vector3d cameraPos = CameraUniforms.getUnshiftedCameraPosition();
        double cameraX = cameraPos.x();
        double cameraY = cameraPos.y();
        double cameraZ = cameraPos.z();
        this.terrainFrustumHolder.getFrustum().m_113002_(cameraX, cameraY, cameraZ);
        levelRenderer.getLevel().m_46473_().m_7238_();
        boolean wasChunkCullingEnabled = client.f_90980_;
        client.f_90980_ = false;
        boolean regenerateClouds = levelRenderer.shouldRegenerateClouds();
        ((LevelRenderer)levelRenderer).m_109826_();
        levelRenderer.setShouldRegenerateClouds(regenerateClouds);
        levelRenderer.invokeSetupRender(playerCamera, this.terrainFrustumHolder.getFrustum(), false, false);
        client.f_90980_ = wasChunkCullingEnabled;
        levelRenderer.getLevel().m_46473_().m_6182_("terrain");
        Matrix4f shadowProjection = this.fov != null ? ShadowMatrices.createPerspectiveMatrix(this.fov.floatValue()) : ShadowMatrices.createOrthoMatrix(this.halfPlaneLength, this.nearPlane < 0.0f ? (float)(-DHCompat.getRenderDistance()) : this.nearPlane, this.farPlane < 0.0f ? (float)DHCompat.getRenderDistance() : this.farPlane);
        IrisRenderSystem.setShadowProjection(shadowProjection);
        PROJECTION = shadowProjection;
        RenderSystem.disableCull();
        if (this.shouldRenderTerrain) {
            levelRenderer.invokeRenderChunkLayer(RenderType.m_110451_(), modelView, cameraX, cameraY, cameraZ, shadowProjection);
            levelRenderer.invokeRenderChunkLayer(RenderType.m_110463_(), modelView, cameraX, cameraY, cameraZ, shadowProjection);
            levelRenderer.invokeRenderChunkLayer(RenderType.m_110457_(), modelView, cameraX, cameraY, cameraZ, shadowProjection);
        }
        RenderSystem.viewport((int)0, (int)0, (int)this.resolution, (int)this.resolution);
        levelRenderer.getLevel().m_46473_().m_6182_("entities");
        float tickDelta = CapturedRenderingState.INSTANCE.getTickDelta();
        boolean hasEntityFrustum = false;
        if (this.entityShadowDistanceMultiplier == 1.0f || this.entityShadowDistanceMultiplier < 0.0f) {
            this.entityFrustumHolder.setInfo(this.terrainFrustumHolder.getFrustum(), this.terrainFrustumHolder.getDistanceInfo(), this.terrainFrustumHolder.getCullingInfo());
        } else {
            hasEntityFrustum = true;
            this.entityFrustumHolder = this.createShadowFrustum(this.renderDistanceMultiplier * this.entityShadowDistanceMultiplier, this.entityFrustumHolder);
        }
        Frustum entityShadowFrustum = this.entityFrustumHolder.getFrustum();
        entityShadowFrustum.m_113002_(cameraX, cameraY, cameraZ);
        if (this.renderBuffersExt != null) {
            this.renderBuffersExt.beginLevelRendering();
        }
        if (this.buffers instanceof DrawCallTrackingRenderBuffers) {
            ((DrawCallTrackingRenderBuffers)this.buffers).resetDrawCounts();
        }
        MultiBufferSource.BufferSource bufferSource = this.buffers.m_110104_();
        EntityRenderDispatcher dispatcher = levelRenderer.getEntityRenderDispatcher();
        if (this.shouldRenderEntities) {
            this.renderedShadowEntities = this.renderEntities(levelRenderer, dispatcher, bufferSource, modelView, tickDelta, entityShadowFrustum, cameraX, cameraY, cameraZ);
        } else if (this.shouldRenderPlayer) {
            this.renderedShadowEntities = this.renderPlayerEntity(levelRenderer, dispatcher, bufferSource, modelView, tickDelta, entityShadowFrustum, cameraX, cameraY, cameraZ);
        }
        levelRenderer.getLevel().m_46473_().m_6182_("build blockentities");
        if (this.shouldRenderBlockEntities) {
            this.renderedShadowBlockEntities = ShadowRenderingState.renderBlockEntities(this, bufferSource, modelView, playerCamera, cameraX, cameraY, cameraZ, tickDelta, hasEntityFrustum, false);
        } else if (this.shouldRenderLightBlockEntities) {
            this.renderedShadowBlockEntities = ShadowRenderingState.renderBlockEntities(this, bufferSource, modelView, playerCamera, cameraX, cameraY, cameraZ, tickDelta, hasEntityFrustum, true);
        }
        levelRenderer.getLevel().m_46473_().m_6182_("draw entities");
        if (bufferSource instanceof FullyBufferedMultiBufferSource) {
            FullyBufferedMultiBufferSource fullyBufferedMultiBufferSource = (FullyBufferedMultiBufferSource)bufferSource;
            fullyBufferedMultiBufferSource.readyUp();
        }
        bufferSource.m_109911_();
        this.copyPreTranslucentDepth(levelRenderer);
        levelRenderer.getLevel().m_46473_().m_6182_("translucent terrain");
        if (this.shouldRenderTranslucent) {
            levelRenderer.invokeRenderChunkLayer(RenderType.m_110466_(), modelView, cameraX, cameraY, cameraZ, shadowProjection);
        }
        if (this.renderBuffersExt != null) {
            this.renderBuffersExt.endLevelRendering();
        }
        IrisRenderSystem.restorePlayerProjection();
        this.debugStringTerrain = ((LevelRenderer)levelRenderer).m_109820_();
        levelRenderer.getLevel().m_46473_().m_6182_("generate mipmaps");
        this.generateMipmaps();
        levelRenderer.getLevel().m_46473_().m_6182_("restore gl state");
        RenderSystem.enableCull();
        Minecraft.m_91087_().m_91385_().m_83947_(false);
        RenderSystem.viewport((int)0, (int)0, (int)client.m_91385_().f_83915_, (int)client.m_91385_().f_83916_);
        if (levelRenderer instanceof CullingDataCache) {
            ((CullingDataCache)((Object)levelRenderer)).restoreState();
        }
        this.compositeRenderer.renderAll();
        levelRenderer.setRenderBuffers(playerBuffers);
        visibleBlockEntities = null;
        ACTIVE = false;
        levelRenderer.getLevel().m_46473_().m_7238_();
        levelRenderer.getLevel().m_46473_().m_6182_("updatechunks");
    }

    public int renderBlockEntities(MultiBufferSource.BufferSource bufferSource, PoseStack modelView, Camera camera, double cameraX, double cameraY, double cameraZ, float tickDelta, boolean hasEntityFrustum, boolean lightsOnly) {
        ShadowRenderer.getLevel().m_46473_().m_6180_("build blockentities");
        int shadowBlockEntities = 0;
        BoxCuller culler = null;
        if (hasEntityFrustum) {
            culler = new BoxCuller(this.halfPlaneLength * (this.renderDistanceMultiplier * this.entityShadowDistanceMultiplier));
            culler.setPosition(cameraX, cameraY, cameraZ);
        }
        for (BlockEntity entity : visibleBlockEntities) {
            if (lightsOnly && entity.m_58900_().m_60791_() == 0) continue;
            BlockPos pos = entity.m_58899_();
            if (hasEntityFrustum && culler.isCulled(pos.m_123341_() - 1, pos.m_123342_() - 1, pos.m_123343_() - 1, pos.m_123341_() + 1, pos.m_123342_() + 1, pos.m_123343_() + 1)) continue;
            modelView.m_85836_();
            modelView.m_85837_((double)pos.m_123341_() - cameraX, (double)pos.m_123342_() - cameraY, (double)pos.m_123343_() - cameraZ);
            Minecraft.m_91087_().m_167982_().m_112267_(entity, tickDelta, modelView, (MultiBufferSource)bufferSource);
            modelView.m_85849_();
            ++shadowBlockEntities;
        }
        ShadowRenderer.getLevel().m_46473_().m_7238_();
        return shadowBlockEntities;
    }

    private int renderEntities(LevelRendererAccessor levelRenderer, EntityRenderDispatcher dispatcher, MultiBufferSource.BufferSource bufferSource, PoseStack modelView, float tickDelta, Frustum frustum, double cameraX, double cameraY, double cameraZ) {
        levelRenderer.getLevel().m_46473_().m_6180_("cull");
        ArrayList<Entity> renderedEntities = new ArrayList<Entity>(32);
        for (Entity entity2 : ShadowRenderer.getLevel().m_104735_()) {
            if (!dispatcher.m_114397_(entity2, frustum, cameraX, cameraY, cameraZ) || entity2.m_5833_()) continue;
            renderedEntities.add(entity2);
        }
        levelRenderer.getLevel().m_46473_().m_6182_("sort");
        renderedEntities.sort(Comparator.comparingInt(entity -> entity.m_6095_().hashCode()));
        levelRenderer.getLevel().m_46473_().m_6182_("build entity geometry");
        for (Entity entity2 : renderedEntities) {
            float realTickDelta = CapturedRenderingState.INSTANCE.getRealTickDelta();
            levelRenderer.invokeRenderEntity(entity2, cameraX, cameraY, cameraZ, realTickDelta, modelView, (MultiBufferSource)bufferSource);
        }
        levelRenderer.getLevel().m_46473_().m_7238_();
        return renderedEntities.size();
    }

    private int renderPlayerEntity(LevelRendererAccessor levelRenderer, EntityRenderDispatcher dispatcher, MultiBufferSource.BufferSource bufferSource, PoseStack modelView, float tickDelta, Frustum frustum, double cameraX, double cameraY, double cameraZ) {
        levelRenderer.getLevel().m_46473_().m_6180_("cull");
        LocalPlayer player = Minecraft.m_91087_().f_91074_;
        int shadowEntities = 0;
        if (!dispatcher.m_114397_((Entity)player, frustum, cameraX, cameraY, cameraZ) || player.m_5833_()) {
            levelRenderer.getLevel().m_46473_().m_7238_();
            return 0;
        }
        levelRenderer.getLevel().m_46473_().m_6182_("build geometry");
        if (!player.m_20197_().isEmpty()) {
            for (int i = 0; i < player.m_20197_().size(); ++i) {
                float realTickDelta = CapturedRenderingState.INSTANCE.getRealTickDelta();
                levelRenderer.invokeRenderEntity((Entity)player.m_20197_().get(i), cameraX, cameraY, cameraZ, realTickDelta, modelView, (MultiBufferSource)bufferSource);
                ++shadowEntities;
            }
        }
        if (player.m_20202_() != null) {
            float realTickDelta = CapturedRenderingState.INSTANCE.getRealTickDelta();
            levelRenderer.invokeRenderEntity(player.m_20202_(), cameraX, cameraY, cameraZ, realTickDelta, modelView, (MultiBufferSource)bufferSource);
            ++shadowEntities;
        }
        float realTickDelta = CapturedRenderingState.INSTANCE.getRealTickDelta();
        levelRenderer.invokeRenderEntity((Entity)player, cameraX, cameraY, cameraZ, realTickDelta, modelView, (MultiBufferSource)bufferSource);
        levelRenderer.getLevel().m_46473_().m_7238_();
        return ++shadowEntities;
    }

    private void copyPreTranslucentDepth(LevelRendererAccessor levelRenderer) {
        levelRenderer.getLevel().m_46473_().m_6182_("translucent depth copy");
        this.targets.copyPreTranslucentDepth();
    }

    public void addDebugText(List<String> messages) {
        if (IrisVideoSettings.getOverriddenShadowDistance(IrisVideoSettings.shadowDistance) == 0) {
            messages.add("[Oculus] Shadow Maps: off, shadow distance 0");
            return;
        }
        if (Iris.getIrisConfig().areDebugOptionsEnabled()) {
            messages.add("[Oculus] Shadow Maps: " + this.debugStringOverall);
            messages.add("[Oculus] Shadow Distance Terrain: " + this.terrainFrustumHolder.getDistanceInfo() + " Entity: " + this.entityFrustumHolder.getDistanceInfo());
            messages.add("[Oculus] Shadow Culling Terrain: " + this.terrainFrustumHolder.getCullingInfo() + " Entity: " + this.entityFrustumHolder.getCullingInfo());
            messages.add("[Oculus] Shadow Terrain: " + this.debugStringTerrain + (this.shouldRenderTerrain ? "" : " (no terrain) ") + (this.shouldRenderTranslucent ? "" : "(no translucent)"));
            messages.add("[Oculus] Shadow Entities: " + this.getEntitiesDebugString());
            messages.add("[Oculus] Shadow Block Entities: " + this.getBlockEntitiesDebugString());
            RenderBuffers renderBuffers = this.buffers;
            if (renderBuffers instanceof DrawCallTrackingRenderBuffers) {
                DrawCallTrackingRenderBuffers drawCallTracker = (DrawCallTrackingRenderBuffers)renderBuffers;
                if (this.shouldRenderEntities || this.shouldRenderPlayer) {
                    messages.add("[Oculus] Shadow Entity Batching: " + BatchingDebugMessageHelper.getDebugMessage(drawCallTracker));
                }
            }
        } else {
            messages.add("[Oculus] Shadow info: " + this.debugStringTerrain);
            messages.add("[Oculus] E: " + this.renderedShadowEntities);
            messages.add("[Oculus] BE: " + this.renderedShadowBlockEntities);
        }
    }

    private String getEntitiesDebugString() {
        return this.shouldRenderEntities || this.shouldRenderPlayer ? this.renderedShadowEntities + "/" + Minecraft.m_91087_().f_91073_.m_104813_() : "disabled by pack";
    }

    private String getBlockEntitiesDebugString() {
        return this.shouldRenderBlockEntities || this.shouldRenderLightBlockEntities ? "" + this.renderedShadowBlockEntities : "disabled by pack";
    }

    public void destroy() {
        this.targets.destroy();
        ((MemoryTrackingRenderBuffers)this.buffers).freeAndDeleteBuffers();
    }

    private record MipmapPass(int texture, int targetFilteringMode) {
    }
}

