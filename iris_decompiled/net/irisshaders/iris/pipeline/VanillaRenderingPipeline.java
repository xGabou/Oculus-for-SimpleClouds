/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMaps
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 */
package net.irisshaders.iris.pipeline;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import java.util.List;
import java.util.OptionalInt;
import net.irisshaders.iris.compat.dh.DHCompat;
import net.irisshaders.iris.features.FeatureFlags;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.helpers.Tri;
import net.irisshaders.iris.mixin.LevelRendererAccessor;
import net.irisshaders.iris.pipeline.SodiumTerrainPipeline;
import net.irisshaders.iris.pipeline.WorldRenderingPhase;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.shaderpack.properties.CloudSetting;
import net.irisshaders.iris.shaderpack.properties.ParticleRenderingSettings;
import net.irisshaders.iris.shaderpack.texture.TextureStage;
import net.irisshaders.iris.targets.RenderTargetStateListener;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;

public class VanillaRenderingPipeline
implements WorldRenderingPipeline {
    public VanillaRenderingPipeline() {
        WorldRenderingSettings.INSTANCE.setDisableDirectionalShading(this.shouldDisableDirectionalShading());
        WorldRenderingSettings.INSTANCE.setUseSeparateAo(false);
        WorldRenderingSettings.INSTANCE.setSeparateEntityDraws(false);
        WorldRenderingSettings.INSTANCE.setAmbientOcclusionLevel(1.0f);
        WorldRenderingSettings.INSTANCE.setUseExtendedVertexFormat(false);
        WorldRenderingSettings.INSTANCE.setVoxelizeLightBlocks(false);
        WorldRenderingSettings.INSTANCE.setBlockTypeIds(null);
    }

    @Override
    public void beginLevelRendering() {
        Minecraft.m_91087_().m_91385_().m_83947_(true);
        GlStateManager._glUseProgram((int)0);
    }

    @Override
    public void renderShadows(LevelRendererAccessor worldRenderer, Camera camera) {
    }

    @Override
    public void addDebugText(List<String> messages) {
    }

    @Override
    public OptionalInt getForcedShadowRenderDistanceChunksForDisplay() {
        return OptionalInt.empty();
    }

    @Override
    public Object2ObjectMap<Tri<String, TextureType, TextureStage>, String> getTextureMap() {
        return Object2ObjectMaps.emptyMap();
    }

    @Override
    public WorldRenderingPhase getPhase() {
        return WorldRenderingPhase.NONE;
    }

    @Override
    public void setPhase(WorldRenderingPhase phase) {
    }

    @Override
    public void setOverridePhase(WorldRenderingPhase phase) {
    }

    @Override
    public RenderTargetStateListener getRenderTargetStateListener() {
        return RenderTargetStateListener.NOP;
    }

    @Override
    public int getCurrentNormalTexture() {
        return 0;
    }

    @Override
    public int getCurrentSpecularTexture() {
        return 0;
    }

    @Override
    public void onSetShaderTexture(int id) {
    }

    @Override
    public void beginHand() {
    }

    @Override
    public void beginTranslucents() {
    }

    @Override
    public void finalizeLevelRendering() {
    }

    @Override
    public void finalizeGameRendering() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public SodiumTerrainPipeline getSodiumTerrainPipeline() {
        return null;
    }

    @Override
    public FrameUpdateNotifier getFrameUpdateNotifier() {
        return new FrameUpdateNotifier();
    }

    @Override
    public boolean shouldDisableVanillaEntityShadows() {
        return false;
    }

    @Override
    public boolean shouldDisableDirectionalShading() {
        return false;
    }

    @Override
    public boolean shouldDisableFrustumCulling() {
        return false;
    }

    @Override
    public boolean shouldDisableOcclusionCulling() {
        return false;
    }

    @Override
    public CloudSetting getCloudSetting() {
        return CloudSetting.DEFAULT;
    }

    @Override
    public boolean shouldRenderUnderwaterOverlay() {
        return true;
    }

    @Override
    public boolean shouldRenderVignette() {
        return true;
    }

    @Override
    public boolean shouldRenderSun() {
        return true;
    }

    @Override
    public boolean shouldRenderMoon() {
        return true;
    }

    @Override
    public boolean shouldWriteRainAndSnowToDepthBuffer() {
        return false;
    }

    @Override
    public ParticleRenderingSettings getParticleRenderingSettings() {
        return ParticleRenderingSettings.MIXED;
    }

    @Override
    public boolean allowConcurrentCompute() {
        return false;
    }

    @Override
    public boolean hasFeature(FeatureFlags flags) {
        return false;
    }

    @Override
    public float getSunPathRotation() {
        return 0.0f;
    }

    @Override
    public DHCompat getDHCompat() {
        return null;
    }
}

