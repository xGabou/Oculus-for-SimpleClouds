/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.blaze3d.platform.GlStateManager$DestFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SourceFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.CloudMode
 *  javax.annotation.Nullable
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.FastColor$ARGB32
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.random.SimpleWeightedRandomList
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.Biome$Precipitation
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  nonamecrackers2.crackerslib.common.compat.CompatHelper
 *  org.apache.commons.lang3.tuple.Pair
 *  org.joml.Vector2f
 *  org.joml.Vector3f
 */
package dev.nonamecrackers2.simpleclouds.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.nonamecrackers2.simpleclouds.api.common.cloud.CloudMode;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.renderer.lightning.LightningBolt;
import dev.nonamecrackers2.simpleclouds.client.renderer.rain.PrecipitationQuad;
import dev.nonamecrackers2.simpleclouds.client.sound.AdjustableAttenuationSoundInstance;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.config.SimpleCloudsConfig;
import dev.nonamecrackers2.simpleclouds.common.init.SimpleCloudsSounds;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class WorldEffects {
    public static final float EFFECTS_STRENGTH_MULTIPLER = 1.2f;
    public static final int RAIN_SCAN_WIDTH = 32;
    public static final int RAIN_SCAN_HEIGHT = 8;
    public static final int RAIN_HEIGHT_OFFSET = 8;
    public static final int RAIN_SOUND_INTERVAL_MODIFIER = 100;
    public static final SimpleWeightedRandomList<Integer> LIGHTNING_COLORS = SimpleWeightedRandomList.m_146263_().m_146271_((Object)-1, 30).m_146271_((Object)-7569153, 13).m_146271_((Object)-7569153, 12).m_146271_((Object)-983116, 10).m_146271_((Object)-19266, 5).m_146270_();
    private final Minecraft mc;
    private final SimpleCloudsRenderer renderer;
    @Nullable
    private CloudType typeAtCamera;
    private float fadeAtCamera;
    private float storminessAtCamera;
    private float storminessSmoothed;
    private float storminessSmoothedO;
    private final List<LightningBolt> lightningBolts = Lists.newArrayList();
    private final Map<BlockPos, PrecipitationQuad> precipitationQuads = Maps.newHashMap();
    private final Map<Biome.Precipitation, List<PrecipitationQuad>> quadsByPrecipitation = Maps.newHashMap();
    private final RandomSource random = RandomSource.m_216327_();
    private int rainDelay = 20;

    protected WorldEffects(Minecraft mc, SimpleCloudsRenderer renderer) {
        this.mc = mc;
        this.renderer = renderer;
    }

    public void renderPost(PoseStack stack, float partialTick, double camX, double camY, double camZ, float scale) {
        CloudType type;
        CloudManager<ClientLevel> manager = CloudManager.get(this.mc.f_91073_);
        Pair<CloudType, Float> result = manager.getCloudTypeAtWorldPos((float)camX, (float)camZ);
        this.typeAtCamera = type = (CloudType)result.getLeft();
        this.fadeAtCamera = ((Float)result.getRight()).floatValue();
        if (!manager.shouldUseVanillaWeather() && type.weatherType().causesDarkening()) {
            float verticalFade = 1.0f - Mth.m_14036_((float)(((float)camY - (type.stormStart() * 8.0f + (float)manager.getCloudHeight())) / 32.0f), (float)0.0f, (float)1.0f);
            float factor = Mth.m_14036_((float)((1.0f - ((Float)result.getRight()).floatValue()) * 3.0f), (float)0.0f, (float)1.0f);
            this.storminessAtCamera = type.storminess() * factor * verticalFade;
        } else {
            this.storminessAtCamera = 0.0f;
        }
        if (!manager.shouldUseVanillaWeather()) {
            float rainLevel = manager.getRainLevel((float)camX, (float)camY, (float)camZ);
            this.mc.f_91073_.m_46734_(rainLevel);
        }
    }

    public void renderRain(LightTexture texture, float partialTick, double camX, double camY, double camZ) {
        Tesselator tesselator = Tesselator.m_85913_();
        BufferBuilder builder = tesselator.m_85915_();
        RenderSystem.depthMask((Minecraft.m_91085_() || CompatHelper.areShadersRunning() ? 1 : 0) != 0);
        RenderSystem.colorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        if (!this.quadsByPrecipitation.isEmpty()) {
            texture.m_109896_();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            RenderSystem.setShader(GameRenderer::m_172829_);
            for (Map.Entry<Biome.Precipitation, List<PrecipitationQuad>> entry : this.quadsByPrecipitation.entrySet()) {
                RenderSystem.setShaderTexture((int)0, (ResourceLocation)PrecipitationQuad.TEXTURE_BY_PRECIPITATION.get(entry.getKey()));
                builder.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85813_);
                PoseStack stack = new PoseStack();
                stack.m_85837_(-camX, -camY, -camZ);
                for (PrecipitationQuad quad : entry.getValue()) {
                    stack.m_85836_();
                    int packedLight = LevelRenderer.m_109541_((BlockAndTintGetter)this.mc.f_91073_, (BlockPos)quad.getBlockPos());
                    quad.render(stack, (VertexConsumer)builder, partialTick, packedLight, camX, camY, camZ);
                    stack.m_85849_();
                }
                tesselator.m_85914_();
            }
            RenderSystem.enableCull();
        }
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public boolean hasLightningToRender() {
        return !this.lightningBolts.isEmpty();
    }

    public void forLightning(Consumer<LightningBolt> consumer) {
        this.lightningBolts.forEach(consumer);
    }

    public void renderLightning(float partialTick, double camX, double camY, double camZ) {
        Tesselator tesselator = Tesselator.m_85913_();
        BufferBuilder builder = tesselator.m_85915_();
        RenderSystem.depthMask((Minecraft.m_91085_() || CompatHelper.areShadersRunning() ? 1 : 0) != 0);
        RenderSystem.colorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        if (this.hasLightningToRender()) {
            float currentFogStart = RenderSystem.getShaderFogStart();
            RenderSystem.setShaderFogStart((float)Float.MAX_VALUE);
            PoseStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.m_85836_();
            RenderSystem.applyModelViewMatrix();
            builder.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
            RenderSystem.setShader(GameRenderer::m_172753_);
            RenderSystem.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE);
            PoseStack stack = new PoseStack();
            stack.m_85836_();
            stack.m_85837_(-camX, -camY, -camZ);
            this.forLightning(bolt -> {
                if (bolt.getPosition().distance((float)camX, (float)camY, (float)camZ) <= 2000.0f && bolt.getFade(partialTick) > 0.5f) {
                    this.mc.f_91073_.m_6580_(2);
                }
                float dist = bolt.getPosition().distance((float)camX, (float)camY, (float)camZ);
                bolt.render(stack, (VertexConsumer)builder, partialTick, 1.0f, 1.0f, 1.0f, this.renderer.getFadeFactorForDistance(dist));
            });
            stack.m_85849_();
            tesselator.m_85914_();
            modelViewStack.m_85849_();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.setShaderFogStart((float)currentFogStart);
        }
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public void spawnLightning(BlockPos pos, boolean onlySound, int seed, int depth, int branchCount, float maxBranchLength, float maxWidth, float minimumPitch, float maximumPitch) {
        float dist;
        Camera camera = this.mc.f_91063_.m_109153_();
        Vec3 cameraPos = camera.m_90583_();
        Vector3f vec = new Vector3f((float)pos.m_123341_() + 0.5f, (float)pos.m_123342_() + 0.5f, (float)pos.m_123343_() + 0.5f);
        CloudManager<ClientLevel> manager = CloudManager.get(this.mc.f_91073_);
        if (manager.getCloudMode() == CloudMode.AMBIENT && (dist = Vector2f.distance((float)vec.x, (float)vec.z, (float)((float)cameraPos.f_82479_), (float)((float)cameraPos.f_82481_))) < 0.5f) {
            return;
        }
        SoundEvent sound = (SoundEvent)SimpleCloudsSounds.DISTANT_THUNDER.get();
        int attenuation = (Integer)SimpleCloudsConfig.CLIENT.thunderAttenuationDistance.get();
        float dist2 = vec.distance((float)cameraPos.f_82479_, (float)cameraPos.f_82480_, (float)cameraPos.f_82481_);
        if (dist2 < 2000.0f) {
            sound = (SoundEvent)SimpleCloudsSounds.CLOSE_THUNDER.get();
            attenuation = 2000;
        }
        float fade = 1.0f - Math.min(Math.max(dist2 - 3000.0f, 0.0f) / 2000.0f, 1.0f);
        RandomSource random = RandomSource.m_216335_((long)seed);
        AdjustableAttenuationSoundInstance instance = new AdjustableAttenuationSoundInstance(sound, SoundSource.WEATHER, 1.0f + this.random.m_188501_() * 4.0f, 0.5f + fade * 0.5f, random, (double)pos.m_123341_() + 0.5, (double)pos.m_123342_() + 0.5, (double)pos.m_123343_() + 0.5, attenuation);
        int time = Mth.m_14143_((float)(dist2 / 2000.0f)) * 20;
        this.mc.m_91106_().m_120369_((SoundInstance)instance, time);
        if (!onlySound) {
            float r = 1.0f;
            float g = 1.0f;
            float b = 1.0f;
            if (((Boolean)SimpleCloudsConfig.CLIENT.lightningColorVariation.get()).booleanValue()) {
                int color = (Integer)LIGHTNING_COLORS.m_216820_(random).get();
                r = (float)FastColor.ARGB32.m_13665_((int)color) / 255.0f;
                g = (float)FastColor.ARGB32.m_13667_((int)color) / 255.0f;
                b = (float)FastColor.ARGB32.m_13669_((int)color) / 255.0f;
            }
            this.lightningBolts.add(new LightningBolt(random, vec, depth, branchCount, maxBranchLength, maxWidth, minimumPitch, maximumPitch, r, g, b));
        }
    }

    public void modifyLightMapTexture(float partialTick, int pixelX, int pixelY, Vector3f color) {
    }

    public float getStorminessAtCamera() {
        return this.storminessAtCamera;
    }

    public void tick() {
        if (this.rainDelay > 0) {
            --this.rainDelay;
        }
        Iterator<LightningBolt> lightning = this.lightningBolts.iterator();
        while (lightning.hasNext()) {
            LightningBolt bolt = lightning.next();
            if (bolt.isDead()) {
                lightning.remove();
            }
            bolt.tick();
        }
        float rainIntensity = this.mc.f_91073_.m_46722_(1.0f);
        BlockPos camPos = this.mc.f_91063_.m_109153_().m_90588_();
        float xRot = ((Double)SimpleCloudsConfig.CLIENT.rainAngle.get()).floatValue() * ((float)Math.PI / 180);
        Vector2f direction = CloudManager.get(this.mc.f_91073_).calculateWindDirection();
        float yRot = (float)(-Mth.m_14136_((double)direction.x, (double)direction.y));
        float xRotCos = Mth.m_14089_((float)(xRot - 1.5707964f));
        int xOffset = Mth.m_14143_((float)(Mth.m_14031_((float)(-yRot)) * xRotCos * 16.0f));
        int zOffset = Mth.m_14143_((float)(Mth.m_14089_((float)(-yRot)) * xRotCos * 16.0f));
        int radius = Mth.m_14143_((float)(16.0f * (Minecraft.m_91405_() ? 1.0f : 0.5f)));
        int minX = camPos.m_123341_() - radius - xOffset;
        int minY = camPos.m_123342_() + 8;
        int minZ = camPos.m_123343_() - radius - zOffset;
        int maxX = camPos.m_123341_() + radius - xOffset;
        int maxY = camPos.m_123342_() + 8 + 8;
        int maxZ = camPos.m_123343_() + radius - zOffset;
        AABB box = new AABB((double)minX, (double)minY, (double)minZ, (double)maxX, (double)maxY, (double)maxZ);
        Biome biome = (Biome)this.mc.f_91073_.m_204166_(camPos).m_203334_();
        if (rainIntensity > 0.0f && biome.m_264473_() && this.rainDelay == 0) {
            for (int x = minX; x < maxX; ++x) {
                for (int z = minZ; z < maxZ; ++z) {
                    int height = this.mc.f_91073_.m_6924_(Heightmap.Types.MOTION_BLOCKING, x, z);
                    for (int y = minY; y < maxY; ++y) {
                        BlockPos pos;
                        Biome.Precipitation precipitation;
                        if (height > y || (precipitation = biome.m_264600_(pos = new BlockPos(x, y, z))) == Biome.Precipitation.NONE) continue;
                        RandomSource blockRandom = RandomSource.m_216335_((long)pos.m_121878_());
                        if (this.precipitationQuads.containsKey(pos) || blockRandom.m_188503_(100) > 2) continue;
                        float widthModifier = precipitation == Biome.Precipitation.SNOW ? 4.0f : 2.0f;
                        PrecipitationQuad quad = new PrecipitationQuad(precipitation, arg_0 -> ((ClientLevel)this.mc.f_91073_).m_45547_(arg_0), pos, xRot + this.random.m_188501_() * 0.1f, yRot + this.random.m_188501_() * 0.1f, 60 + this.random.m_188503_(60), rainIntensity * widthModifier);
                        this.precipitationQuads.put(pos, quad);
                        this.quadsByPrecipitation.computeIfAbsent(precipitation, p -> Lists.newArrayList()).add(quad);
                    }
                }
            }
        }
        Iterator<Map.Entry<BlockPos, PrecipitationQuad>> rain = this.precipitationQuads.entrySet().iterator();
        while (rain.hasNext()) {
            Map.Entry<BlockPos, PrecipitationQuad> entry = rain.next();
            PrecipitationQuad quad = entry.getValue();
            BlockPos pos = entry.getKey();
            if (!box.m_82393_((double)pos.m_123341_() + 0.5, (double)pos.m_123342_() + 0.5, (double)pos.m_123343_() + 0.5) || quad.isDead()) {
                rain.remove();
                this.quadsByPrecipitation.get(quad.getPrecipitation()).remove(quad);
                continue;
            }
            quad.tick();
        }
        this.storminessSmoothedO = this.storminessSmoothed;
        this.storminessSmoothed += (this.storminessAtCamera - this.storminessSmoothed) / 25.0f;
    }

    public Color calculateFogColor(float defaultR, float defaultG, float defaultB, float partialTick) {
        float lerp = this.getDarkenFactor(partialTick);
        return WorldEffects.hsbLerp(defaultR, defaultG, defaultB, 0.68f, 0.2f, -0.05f, lerp);
    }

    public Color calculateSkyColor(float defaultR, float defaultG, float defaultB, float partialTick) {
        float lerp = this.getDarkenFactor(partialTick);
        return WorldEffects.hsbLerp(defaultR, defaultG, defaultB, 0.63f, 0.1f, 0.05f, lerp);
    }

    private static Color hsbLerp(float r, float g, float b, float targetHue, float targetSaturation, float targetBrightness, float lerp) {
        float[] hsbFog = Color.RGBtoHSB((int)(r * 255.0f), (int)(g * 255.0f), (int)(b * 255.0f), null);
        if (targetHue < hsbFog[0]) {
            targetHue += 1.0f;
        }
        float hue = Mth.m_14179_((float)lerp, (float)targetHue, (float)hsbFog[0]);
        float sat = Mth.m_14036_((float)Mth.m_14179_((float)lerp, (float)targetSaturation, (float)hsbFog[1]), (float)0.0f, (float)1.0f);
        float bright = Mth.m_14036_((float)Mth.m_14179_((float)lerp, (float)targetBrightness, (float)hsbFog[2]), (float)0.0f, (float)1.0f);
        return Color.getHSBColor(hue, sat, bright);
    }

    public void reset() {
        this.precipitationQuads.clear();
        this.quadsByPrecipitation.clear();
        this.rainDelay = 20;
    }

    @Nullable
    public CloudType getCloudTypeAtCamera() {
        return this.typeAtCamera;
    }

    public float getFadeRegionAtCamera() {
        return this.fadeAtCamera;
    }

    public float getStorminessSmoothed(float partialTick) {
        return Mth.m_14179_((float)partialTick, (float)this.storminessSmoothedO, (float)this.storminessSmoothed);
    }

    public float getDarkenFactor(float partialTick, float strength) {
        return Mth.m_14036_((float)(1.0f - this.getStorminessSmoothed(partialTick) * strength), (float)0.1f, (float)1.0f);
    }

    public float getDarkenFactor(float partialTick) {
        return this.getDarkenFactor(partialTick, 1.2f);
    }

    public List<LightningBolt> getLightningBolts() {
        return this.lightningBolts;
    }
}

