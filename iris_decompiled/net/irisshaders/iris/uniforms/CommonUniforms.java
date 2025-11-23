/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$BlendState
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffectInstance$FactorData
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.material.FogType
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Math
 *  org.joml.Vector2f
 *  org.joml.Vector2i
 *  org.joml.Vector3d
 *  org.joml.Vector4f
 *  org.joml.Vector4i
 */
package net.irisshaders.iris.uniforms;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.irisshaders.iris.compat.dh.DHCompat;
import net.irisshaders.iris.gl.state.FogMode;
import net.irisshaders.iris.gl.state.StateUpdateNotifiers;
import net.irisshaders.iris.gl.uniform.DynamicUniformHolder;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.helpers.JomlConversions;
import net.irisshaders.iris.layer.GbufferPrograms;
import net.irisshaders.iris.mixin.GlStateManagerAccessor;
import net.irisshaders.iris.mixin.statelisteners.BooleanStateAccessor;
import net.irisshaders.iris.mixin.texture.TextureAtlasAccessor;
import net.irisshaders.iris.shaderpack.IdMap;
import net.irisshaders.iris.shaderpack.properties.PackDirectives;
import net.irisshaders.iris.texture.TextureInfoCache;
import net.irisshaders.iris.texture.TextureTracker;
import net.irisshaders.iris.uniforms.BiomeUniforms;
import net.irisshaders.iris.uniforms.CameraUniforms;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.uniforms.CelestialUniforms;
import net.irisshaders.iris.uniforms.ExternallyManagedUniforms;
import net.irisshaders.iris.uniforms.FogUniforms;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import net.irisshaders.iris.uniforms.IdMapUniforms;
import net.irisshaders.iris.uniforms.IrisExclusiveUniforms;
import net.irisshaders.iris.uniforms.IrisInternalUniforms;
import net.irisshaders.iris.uniforms.IrisTimeUniforms;
import net.irisshaders.iris.uniforms.MatrixUniforms;
import net.irisshaders.iris.uniforms.SystemTimeUniforms;
import net.irisshaders.iris.uniforms.ViewportUniforms;
import net.irisshaders.iris.uniforms.WorldTimeUniforms;
import net.irisshaders.iris.uniforms.transforms.SmoothedFloat;
import net.irisshaders.iris.uniforms.transforms.SmoothedVec2f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector4f;
import org.joml.Vector4i;

public final class CommonUniforms {
    private static final Minecraft client = Minecraft.m_91087_();
    private static final Vector2i ZERO_VECTOR_2i = new Vector2i();
    private static final Vector4i ZERO_VECTOR_4i = new Vector4i(0, 0, 0, 0);
    private static final Vector3d ZERO_VECTOR_3d = new Vector3d();

    private CommonUniforms() {
    }

    public static void addDynamicUniforms(DynamicUniformHolder uniforms, FogMode fogMode) {
        ExternallyManagedUniforms.addExternallyManagedUniforms117(uniforms);
        FogUniforms.addFogUniforms(uniforms, fogMode);
        IrisInternalUniforms.addFogUniforms(uniforms, fogMode);
        uniforms.uniform1i("entityId", CapturedRenderingState.INSTANCE::getCurrentRenderedEntity, StateUpdateNotifiers.fallbackEntityNotifier);
        uniforms.uniform2i("atlasSize", () -> {
            int glId = RenderSystem.getShaderTexture((int)0);
            AbstractTexture texture = TextureTracker.INSTANCE.getTexture(glId);
            if (texture instanceof TextureAtlas) {
                TextureAtlas atlas = (TextureAtlas)texture;
                TextureAtlasAccessor atlasAccessor = (TextureAtlasAccessor)atlas;
                return new Vector2i(atlasAccessor.callGetWidth(), atlasAccessor.callGetHeight());
            }
            return ZERO_VECTOR_2i;
        }, listener -> {});
        uniforms.uniform2i("gtextureSize", () -> {
            int glId = GlStateManagerAccessor.getTEXTURES()[0].f_84801_;
            TextureInfoCache.TextureInfo info = TextureInfoCache.INSTANCE.getInfo(glId);
            return new Vector2i(info.getWidth(), info.getHeight());
        }, StateUpdateNotifiers.bindTextureNotifier);
        uniforms.uniform4i("blendFunc", () -> {
            GlStateManager.BlendState blend = GlStateManagerAccessor.getBLEND();
            if (((BooleanStateAccessor)blend.f_84577_).isEnabled()) {
                return new Vector4i(blend.f_84578_, blend.f_84579_, blend.f_84580_, blend.f_84581_);
            }
            return ZERO_VECTOR_4i;
        }, StateUpdateNotifiers.blendFuncNotifier);
        uniforms.uniform1i("renderStage", () -> GbufferPrograms.getCurrentPhase().ordinal(), StateUpdateNotifiers.phaseChangeNotifier);
    }

    public static void addCommonUniforms(DynamicUniformHolder uniforms, IdMap idMap, PackDirectives directives, FrameUpdateNotifier updateNotifier, FogMode fogMode) {
        CommonUniforms.addNonDynamicUniforms(uniforms, idMap, directives, updateNotifier);
        CommonUniforms.addDynamicUniforms(uniforms, fogMode);
    }

    public static void addNonDynamicUniforms(UniformHolder uniforms, IdMap idMap, PackDirectives directives, FrameUpdateNotifier updateNotifier) {
        CameraUniforms.addCameraUniforms(uniforms, updateNotifier);
        ViewportUniforms.addViewportUniforms(uniforms);
        WorldTimeUniforms.addWorldTimeUniforms(uniforms);
        SystemTimeUniforms.addSystemTimeUniforms(uniforms);
        BiomeUniforms.addBiomeUniforms(uniforms);
        new CelestialUniforms(directives.getSunPathRotation()).addCelestialUniforms(uniforms);
        IrisExclusiveUniforms.addIrisExclusiveUniforms(uniforms);
        IrisTimeUniforms.addTimeUniforms(uniforms);
        MatrixUniforms.addMatrixUniforms(uniforms, directives);
        IdMapUniforms.addIdMapUniforms(updateNotifier, uniforms, idMap, directives.isOldHandLight());
        CommonUniforms.generalCommonUniforms(uniforms, updateNotifier, directives);
    }

    public static void generalCommonUniforms(UniformHolder uniforms, FrameUpdateNotifier updateNotifier, PackDirectives directives) {
        ExternallyManagedUniforms.addExternallyManagedUniforms117(uniforms);
        SmoothedVec2f eyeBrightnessSmooth = new SmoothedVec2f(directives.getEyeBrightnessHalfLife(), directives.getEyeBrightnessHalfLife(), CommonUniforms::getEyeBrightness, updateNotifier);
        uniforms.uniform1b(UniformUpdateFrequency.PER_FRAME, "hideGUI", () -> CommonUniforms.client.f_91066_.f_92062_).uniform1b(UniformUpdateFrequency.PER_FRAME, "isRightHanded", () -> CommonUniforms.client.f_91066_.m_232107_().m_231551_() == HumanoidArm.RIGHT).uniform1i(UniformUpdateFrequency.PER_FRAME, "isEyeInWater", CommonUniforms::isEyeInWater).uniform1f(UniformUpdateFrequency.PER_FRAME, "blindness", CommonUniforms::getBlindness).uniform1f(UniformUpdateFrequency.PER_FRAME, "darknessFactor", CommonUniforms::getDarknessFactor).uniform1f(UniformUpdateFrequency.PER_FRAME, "darknessLightFactor", CapturedRenderingState.INSTANCE::getDarknessLightFactor).uniform1f(UniformUpdateFrequency.PER_FRAME, "nightVision", CommonUniforms::getNightVision).uniform1b(UniformUpdateFrequency.PER_FRAME, "is_sneaking", CommonUniforms::isSneaking).uniform1b(UniformUpdateFrequency.PER_FRAME, "is_sprinting", CommonUniforms::isSprinting).uniform1b(UniformUpdateFrequency.PER_FRAME, "is_hurt", CommonUniforms::isHurt).uniform1b(UniformUpdateFrequency.PER_FRAME, "is_invisible", CommonUniforms::isInvisible).uniform1b(UniformUpdateFrequency.PER_FRAME, "is_burning", CommonUniforms::isBurning).uniform1b(UniformUpdateFrequency.PER_FRAME, "is_on_ground", CommonUniforms::isOnGround).uniform1f(UniformUpdateFrequency.PER_FRAME, "screenBrightness", () -> (Double)CommonUniforms.client.f_91066_.m_231927_().m_231551_()).uniform4f(UniformUpdateFrequency.ONCE, "entityColor", () -> new Vector4f(0.0f, 0.0f, 0.0f, 0.0f)).uniform1i(UniformUpdateFrequency.ONCE, "blockEntityId", () -> -1).uniform1i(UniformUpdateFrequency.ONCE, "currentRenderedItemId", () -> -1).uniform1f(UniformUpdateFrequency.ONCE, "pi", () -> Math.PI).uniform1f(UniformUpdateFrequency.PER_TICK, "playerMood", CommonUniforms::getPlayerMood).uniform2i(UniformUpdateFrequency.PER_FRAME, "eyeBrightness", CommonUniforms::getEyeBrightness).uniform2i(UniformUpdateFrequency.PER_FRAME, "eyeBrightnessSmooth", () -> {
            Vector2f smoothed = eyeBrightnessSmooth.get();
            return new Vector2i((int)smoothed.x(), (int)smoothed.y());
        }).uniform1f(UniformUpdateFrequency.PER_TICK, "rainStrength", CommonUniforms::getRainStrength).uniform1f(UniformUpdateFrequency.PER_TICK, "wetness", new SmoothedFloat(directives.getWetnessHalfLife(), directives.getDrynessHalfLife(), CommonUniforms::getRainStrength, updateNotifier)).uniform3d(UniformUpdateFrequency.PER_FRAME, "skyColor", CommonUniforms::getSkyColor).uniform1f(UniformUpdateFrequency.PER_FRAME, "dhFarPlane", DHCompat::getFarPlane).uniform1f(UniformUpdateFrequency.PER_FRAME, "dhNearPlane", DHCompat::getNearPlane).uniform1i(UniformUpdateFrequency.PER_FRAME, "dhRenderDistance", DHCompat::getRenderDistance);
    }

    private static boolean isOnGround() {
        return CommonUniforms.client.f_91074_ != null && CommonUniforms.client.f_91074_.m_20096_();
    }

    private static boolean isHurt() {
        if (CommonUniforms.client.f_91074_ != null) {
            return CommonUniforms.client.f_91074_.f_20916_ > 0;
        }
        return false;
    }

    private static boolean isInvisible() {
        if (CommonUniforms.client.f_91074_ != null) {
            return CommonUniforms.client.f_91074_.m_20145_();
        }
        return false;
    }

    private static boolean isBurning() {
        if (CommonUniforms.client.f_91074_ != null) {
            return CommonUniforms.client.f_91074_.m_6060_();
        }
        return false;
    }

    private static boolean isSneaking() {
        if (CommonUniforms.client.f_91074_ != null) {
            return CommonUniforms.client.f_91074_.m_6047_();
        }
        return false;
    }

    private static boolean isSprinting() {
        if (CommonUniforms.client.f_91074_ != null) {
            return CommonUniforms.client.f_91074_.m_20142_();
        }
        return false;
    }

    private static Vector3d getSkyColor() {
        if (CommonUniforms.client.f_91073_ == null || CommonUniforms.client.f_91075_ == null) {
            return ZERO_VECTOR_3d;
        }
        return JomlConversions.fromVec3(CommonUniforms.client.f_91073_.m_171660_(CommonUniforms.client.f_91075_.m_20182_(), CapturedRenderingState.INSTANCE.getTickDelta()));
    }

    static float getBlindness() {
        MobEffectInstance blindness;
        Entity cameraEntity = client.m_91288_();
        if (cameraEntity instanceof LivingEntity && (blindness = ((LivingEntity)cameraEntity).m_21124_(MobEffects.f_19610_)) != null) {
            if (blindness.m_267577_()) {
                return 1.0f;
            }
            return org.joml.Math.clamp((float)0.0f, (float)1.0f, (float)((float)blindness.m_19557_() / 20.0f));
        }
        return 0.0f;
    }

    static float getDarknessFactor() {
        MobEffectInstance darkness;
        Entity cameraEntity = client.m_91288_();
        if (cameraEntity instanceof LivingEntity && (darkness = ((LivingEntity)cameraEntity).m_21124_(MobEffects.f_216964_)) != null && darkness.m_216895_().isPresent()) {
            return ((MobEffectInstance.FactorData)darkness.m_216895_().get()).m_238413_((LivingEntity)cameraEntity, CapturedRenderingState.INSTANCE.getTickDelta());
        }
        return 0.0f;
    }

    private static float getPlayerMood() {
        if (!(CommonUniforms.client.f_91075_ instanceof LocalPlayer)) {
            return 0.0f;
        }
        return org.joml.Math.clamp((float)0.0f, (float)1.0f, (float)((LocalPlayer)CommonUniforms.client.f_91075_).m_108762_());
    }

    static float getRainStrength() {
        if (CommonUniforms.client.f_91073_ == null) {
            return 0.0f;
        }
        return org.joml.Math.clamp((float)0.0f, (float)1.0f, (float)CommonUniforms.client.f_91073_.m_46722_(CapturedRenderingState.INSTANCE.getTickDelta()));
    }

    private static Vector2i getEyeBrightness() {
        if (CommonUniforms.client.f_91075_ == null || CommonUniforms.client.f_91073_ == null) {
            return ZERO_VECTOR_2i;
        }
        Vec3 feet = CommonUniforms.client.f_91075_.m_20182_();
        Vec3 eyes = new Vec3(feet.f_82479_, CommonUniforms.client.f_91075_.m_20188_(), feet.f_82481_);
        BlockPos eyeBlockPos = BlockPos.m_274446_((Position)eyes);
        int blockLight = CommonUniforms.client.f_91073_.m_45517_(LightLayer.BLOCK, eyeBlockPos);
        int skyLight = CommonUniforms.client.f_91073_.m_45517_(LightLayer.SKY, eyeBlockPos);
        return new Vector2i(blockLight * 16, skyLight * 16);
    }

    private static float getNightVision() {
        float underwaterVisibility;
        Entity cameraEntity = client.m_91288_();
        if (cameraEntity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)cameraEntity;
            try {
                float nightVisionStrength = GameRenderer.m_109108_((LivingEntity)livingEntity, (float)CapturedRenderingState.INSTANCE.getTickDelta());
                if (nightVisionStrength > 0.0f) {
                    return org.joml.Math.clamp((float)0.0f, (float)1.0f, (float)nightVisionStrength);
                }
            }
            catch (NullPointerException e) {
                return 0.0f;
            }
        }
        if (CommonUniforms.client.f_91074_ != null && CommonUniforms.client.f_91074_.m_21023_(MobEffects.f_19592_) && (underwaterVisibility = CommonUniforms.client.f_91074_.m_108639_()) > 0.0f) {
            return org.joml.Math.clamp((float)0.0f, (float)1.0f, (float)underwaterVisibility);
        }
        return 0.0f;
    }

    static int isEyeInWater() {
        FogType submersionType = CommonUniforms.client.f_91063_.m_109153_().m_167685_();
        if (submersionType == FogType.WATER) {
            return 1;
        }
        if (submersionType == FogType.LAVA) {
            return 2;
        }
        if (submersionType == FogType.POWDER_SNOW) {
            return 3;
        }
        return 0;
    }

    static {
        GbufferPrograms.init();
    }
}

