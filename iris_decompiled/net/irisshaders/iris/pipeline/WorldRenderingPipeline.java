/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  net.minecraft.client.Camera
 */
package net.irisshaders.iris.pipeline;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import java.util.List;
import java.util.OptionalInt;
import net.irisshaders.iris.compat.dh.DHCompat;
import net.irisshaders.iris.features.FeatureFlags;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.helpers.Tri;
import net.irisshaders.iris.mixin.LevelRendererAccessor;
import net.irisshaders.iris.pipeline.SodiumTerrainPipeline;
import net.irisshaders.iris.pipeline.WorldRenderingPhase;
import net.irisshaders.iris.shaderpack.properties.CloudSetting;
import net.irisshaders.iris.shaderpack.properties.ParticleRenderingSettings;
import net.irisshaders.iris.shaderpack.texture.TextureStage;
import net.irisshaders.iris.targets.RenderTargetStateListener;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import net.minecraft.client.Camera;

public interface WorldRenderingPipeline {
    public void beginLevelRendering();

    public void renderShadows(LevelRendererAccessor var1, Camera var2);

    public void addDebugText(List<String> var1);

    public OptionalInt getForcedShadowRenderDistanceChunksForDisplay();

    public Object2ObjectMap<Tri<String, TextureType, TextureStage>, String> getTextureMap();

    public WorldRenderingPhase getPhase();

    public void setPhase(WorldRenderingPhase var1);

    public void setOverridePhase(WorldRenderingPhase var1);

    public RenderTargetStateListener getRenderTargetStateListener();

    public int getCurrentNormalTexture();

    public int getCurrentSpecularTexture();

    public void onSetShaderTexture(int var1);

    public void beginHand();

    public void beginTranslucents();

    public void finalizeLevelRendering();

    public void finalizeGameRendering();

    public void destroy();

    public SodiumTerrainPipeline getSodiumTerrainPipeline();

    public FrameUpdateNotifier getFrameUpdateNotifier();

    public boolean shouldDisableVanillaEntityShadows();

    public boolean shouldDisableDirectionalShading();

    public boolean shouldDisableFrustumCulling();

    public boolean shouldDisableOcclusionCulling();

    public CloudSetting getCloudSetting();

    public boolean shouldRenderUnderwaterOverlay();

    public boolean shouldRenderVignette();

    public boolean shouldRenderSun();

    public boolean shouldRenderMoon();

    public boolean shouldWriteRainAndSnowToDepthBuffer();

    public ParticleRenderingSettings getParticleRenderingSettings();

    public boolean allowConcurrentCompute();

    public boolean hasFeature(FeatureFlags var1);

    public float getSunPathRotation();

    public DHCompat getDHCompat();
}

