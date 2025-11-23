/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Position
 *  net.minecraft.tags.BiomeTags
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.Biome$Precipitation
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Math
 */
package net.irisshaders.iris.uniforms;

import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.uniforms.CameraUniforms;
import net.irisshaders.iris.uniforms.CelestialUniforms;
import net.irisshaders.iris.uniforms.CommonUniforms;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import net.irisshaders.iris.uniforms.SystemTimeUniforms;
import net.irisshaders.iris.uniforms.WorldTimeUniforms;
import net.irisshaders.iris.uniforms.transforms.SmoothedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;

public class HardcodedCustomUniforms {
    private static final Minecraft client = Minecraft.m_91087_();
    private static Holder<Biome> storedBiome;

    public static void addHardcodedCustomUniforms(UniformHolder holder, FrameUpdateNotifier updateNotifier) {
        updateNotifier.addListener(() -> {
            storedBiome = Minecraft.m_91087_().f_91073_ != null ? Minecraft.m_91087_().f_91073_.m_204166_(Minecraft.m_91087_().m_91288_().m_20183_()) : null;
        });
        CameraUniforms.CameraPositionTracker tracker = new CameraUniforms.CameraPositionTracker(updateNotifier);
        SmoothedFloat eyeInCave = new SmoothedFloat(6.0f, 12.0f, HardcodedCustomUniforms::getEyeInCave, updateNotifier);
        SmoothedFloat rainStrengthS = HardcodedCustomUniforms.rainStrengthS(updateNotifier, 15.0f, 15.0f);
        SmoothedFloat rainStrengthShining = HardcodedCustomUniforms.rainStrengthS(updateNotifier, 10.0f, 11.0f);
        SmoothedFloat rainStrengthS2 = HardcodedCustomUniforms.rainStrengthS(updateNotifier, 70.0f, 1.0f);
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "timeAngle", HardcodedCustomUniforms::getTimeAngle);
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "timeBrightness", HardcodedCustomUniforms::getTimeBrightness);
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "moonBrightness", HardcodedCustomUniforms::getMoonBrightness);
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "shadowFade", HardcodedCustomUniforms::getShadowFade);
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "rainStrengthS", rainStrengthS);
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "rainStrengthShiningStars", rainStrengthShining);
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "rainStrengthS2", rainStrengthS2);
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "blindFactor", HardcodedCustomUniforms::getBlindFactor);
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "isDry", new SmoothedFloat(20.0f, 10.0f, () -> HardcodedCustomUniforms.getRawPrecipitation() == 0.0f ? 1.0f : 0.0f, updateNotifier));
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "isRainy", new SmoothedFloat(20.0f, 10.0f, () -> HardcodedCustomUniforms.getRawPrecipitation() == 1.0f ? 1.0f : 0.0f, updateNotifier));
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "isSnowy", new SmoothedFloat(20.0f, 10.0f, () -> HardcodedCustomUniforms.getRawPrecipitation() == 2.0f ? 1.0f : 0.0f, updateNotifier));
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "isEyeInCave", () -> CommonUniforms.isEyeInWater() == 0 ? eyeInCave.getAsFloat() : 0.0f);
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "velocity", () -> HardcodedCustomUniforms.getVelocity(tracker));
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "starter", HardcodedCustomUniforms.getStarter(tracker, updateNotifier));
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "frameTimeSmooth", new SmoothedFloat(5.0f, 5.0f, SystemTimeUniforms.TIMER::getLastFrameTime, updateNotifier));
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "eyeBrightnessM", new SmoothedFloat(5.0f, 5.0f, HardcodedCustomUniforms::getEyeBrightnessM, updateNotifier));
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "rainFactor", rainStrengthS);
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "inSwamp", new SmoothedFloat(5.0f, 5.0f, () -> {
            if (storedBiome == null) {
                return 0.0f;
            }
            return storedBiome.m_203656_(BiomeTags.f_215802_) ? 1.0f : 0.0f;
        }, updateNotifier));
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "BiomeTemp", () -> {
            if (storedBiome == null) {
                return 0.0f;
            }
            return ((Biome)storedBiome.m_203334_()).m_47554_();
        });
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "day", HardcodedCustomUniforms::getDay);
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "night", HardcodedCustomUniforms::getNight);
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "dawnDusk", HardcodedCustomUniforms::getDawnDusk);
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "shdFade", HardcodedCustomUniforms::getShdFade);
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "isPrecipitationRain", new SmoothedFloat(6.0f, 6.0f, () -> HardcodedCustomUniforms.getRawPrecipitation() == 1.0f && tracker.getCurrentCameraPosition().y < 96.0 ? 1.0f : 0.0f, updateNotifier));
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "touchmybody", new SmoothedFloat(0.0f, 0.1f, HardcodedCustomUniforms::getHurtFactor, updateNotifier));
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "sneakSmooth", new SmoothedFloat(2.0f, 0.9f, HardcodedCustomUniforms::getSneakFactor, updateNotifier));
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "burningSmooth", new SmoothedFloat(1.0f, 2.0f, HardcodedCustomUniforms::getBurnFactor, updateNotifier));
        SmoothedFloat smoothSpeed = new SmoothedFloat(1.0f, 1.5f, () -> HardcodedCustomUniforms.getVelocity(tracker) / SystemTimeUniforms.TIMER.getLastFrameTime(), updateNotifier);
        holder.uniform1f(UniformUpdateFrequency.PER_FRAME, "effectStrength", () -> HardcodedCustomUniforms.getHyperSpeedStrength(smoothSpeed));
    }

    private static float getHyperSpeedStrength(SmoothedFloat smoothSpeed) {
        return (float)(1.0 - Math.exp((double)(-smoothSpeed.getAsFloat() * 0.003906f)));
    }

    private static float getBurnFactor() {
        return Minecraft.m_91087_().f_91074_.m_6060_() ? 1.0f : 0.0f;
    }

    private static float getSneakFactor() {
        return Minecraft.m_91087_().f_91074_.m_6047_() ? 1.0f : 0.0f;
    }

    private static float getHurtFactor() {
        LocalPlayer player = Minecraft.m_91087_().f_91074_;
        return player.f_20916_ > 0 || player.f_20919_ > 0 ? 0.4f : 0.0f;
    }

    private static float getEyeInCave() {
        if (client.m_91288_().m_20188_() < 5.0) {
            return 1.0f - HardcodedCustomUniforms.getEyeSkyBrightness() / 240.0f;
        }
        return 0.0f;
    }

    private static float getEyeBrightnessM() {
        return HardcodedCustomUniforms.getEyeSkyBrightness() / 240.0f;
    }

    private static float getEyeSkyBrightness() {
        if (HardcodedCustomUniforms.client.f_91075_ == null || HardcodedCustomUniforms.client.f_91073_ == null) {
            return 0.0f;
        }
        Vec3 feet = HardcodedCustomUniforms.client.f_91075_.m_20182_();
        Vec3 eyes = new Vec3(feet.f_82479_, HardcodedCustomUniforms.client.f_91075_.m_20188_(), feet.f_82481_);
        BlockPos eyeBlockPos = BlockPos.m_274446_((Position)eyes);
        int skyLight = HardcodedCustomUniforms.client.f_91073_.m_45517_(LightLayer.SKY, eyeBlockPos);
        return skyLight * 16;
    }

    private static float getVelocity(CameraUniforms.CameraPositionTracker tracker) {
        float difX = (float)(tracker.getCurrentCameraPosition().x - tracker.getPreviousCameraPosition().x);
        float difY = (float)(tracker.getCurrentCameraPosition().y - tracker.getPreviousCameraPosition().y);
        float difZ = (float)(tracker.getCurrentCameraPosition().z - tracker.getPreviousCameraPosition().z);
        return Math.sqrt((float)(difX * difX + difY * difY + difZ * difZ));
    }

    private static SmoothedFloat getStarter(CameraUniforms.CameraPositionTracker tracker, FrameUpdateNotifier notifier) {
        return new SmoothedFloat(20.0f, 20.0f, new SmoothedFloat(0.0f, 3.1536E7f, () -> HardcodedCustomUniforms.getMoving(tracker), notifier), notifier);
    }

    private static float getMoving(CameraUniforms.CameraPositionTracker tracker) {
        float difX = (float)(tracker.getCurrentCameraPosition().x - tracker.getPreviousCameraPosition().x);
        float difY = (float)(tracker.getCurrentCameraPosition().y - tracker.getPreviousCameraPosition().y);
        float difZ = (float)(tracker.getCurrentCameraPosition().z - tracker.getPreviousCameraPosition().z);
        float difSum = Math.abs((float)difX) + Math.abs((float)difY) + Math.abs((float)difZ);
        return difSum > 0.0f && difSum < 1.0f ? 1.0f : 0.0f;
    }

    private static float getTimeAngle() {
        return (float)HardcodedCustomUniforms.getWorldDayTime() / 24000.0f;
    }

    private static int getWorldDayTime() {
        ClientLevel level = Minecraft.m_91087_().f_91073_;
        long timeOfDay = level.m_46468_();
        long dayTime = level.m_6042_().f_63854_().orElse(timeOfDay % 24000L);
        return (int)dayTime;
    }

    private static float getTimeBrightness() {
        return (float)java.lang.Math.max(java.lang.Math.sin((double)HardcodedCustomUniforms.getTimeAngle() * java.lang.Math.PI * 2.0), 0.0);
    }

    private static float getMoonBrightness() {
        return (float)java.lang.Math.max(java.lang.Math.sin((double)HardcodedCustomUniforms.getTimeAngle() * java.lang.Math.PI * -2.0), 0.0);
    }

    private static float getShadowFade() {
        return (float)Math.clamp((double)0.0, (double)1.0, (double)(1.0 - (java.lang.Math.abs(java.lang.Math.abs((double)CelestialUniforms.getSunAngle() - 0.5) - 0.25) - 0.23) * 100.0));
    }

    private static SmoothedFloat rainStrengthS(FrameUpdateNotifier updateNotifier, float halfLifeUp, float halfLifeDown) {
        return new SmoothedFloat(halfLifeUp, halfLifeDown, CommonUniforms::getRainStrength, updateNotifier);
    }

    private static float getRawPrecipitation() {
        if (storedBiome == null) {
            return 0.0f;
        }
        Biome.Precipitation precipitation = ((Biome)storedBiome.m_203334_()).m_264600_(Minecraft.m_91087_().f_91075_.m_20183_());
        return switch (precipitation) {
            case Biome.Precipitation.RAIN -> 1.0f;
            case Biome.Precipitation.SNOW -> 2.0f;
            default -> 0.0f;
        };
    }

    private static float getBlindFactor() {
        float blindFactorSqrt = (float)Math.clamp((double)0.0, (double)1.0, (double)((double)CommonUniforms.getBlindness() * 2.0 - 1.0));
        return blindFactorSqrt * blindFactorSqrt;
    }

    private static float frac(float value) {
        return java.lang.Math.abs(value % 1.0f);
    }

    private static float getAdjTime() {
        return Math.abs((float)(((float)WorldTimeUniforms.getWorldDayTime() / 1000.0f + 6.0f) % 24.0f - 12.0f));
    }

    private static float getDay() {
        return Math.clamp((float)0.0f, (float)1.0f, (float)(5.4f - HardcodedCustomUniforms.getAdjTime()));
    }

    private static float getNight() {
        return Math.clamp((float)0.0f, (float)1.0f, (float)(HardcodedCustomUniforms.getAdjTime() - 6.0f));
    }

    private static float getDawnDusk() {
        return 1.0f - HardcodedCustomUniforms.getDay() - HardcodedCustomUniforms.getNight();
    }

    private static float getShdFade() {
        return (float)Math.clamp((double)0.0, (double)1.0, (double)(1.0 - (Math.abs((double)(Math.abs((double)((double)CelestialUniforms.getSunAngle() - 0.5)) - 0.25)) - 0.225) * 40.0));
    }
}

