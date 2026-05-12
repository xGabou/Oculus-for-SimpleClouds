package net.Gabou.oculus_for_simpleclouds.visual;

import com.mojang.blaze3d.shaders.AbstractUniform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class SimpleCloudsSunLighting {
    private static final Logger LOGGER = LogManager.getLogger("oculus_for_simpleclouds/SimpleCloudsSunLighting");
    private static final boolean DEBUG_SUN_INPUTS = Boolean.getBoolean("ofsc.debug.sunInputs");
    private static final float DAY_TICKS = 24000.0F;
    private static final float SUNRISE_CENTER_TICKS = 0.0F;
    private static final float SUNSET_CENTER_TICKS = 12000.0F;
    private static final float TWILIGHT_INNER_RADIUS_TICKS = 1200.0F;
    private static final float TWILIGHT_OUTER_RADIUS_TICKS = 6200.0F;
    private static final float TWO_PI = (float)(Math.PI * 2.0);
    private static final float EPSILON = 1.0E-4F;
    private static long lastLogTimeMs = 0L;

    private static final float DEFAULT_SUN_DIR_X = 0.0F;
    private static final float DEFAULT_SUN_DIR_Y = 1.0F;
    private static final float DEFAULT_SUN_DIR_Z = 0.0F;

    private static final float DEFAULT_SUN_COLOR_R = 1.0F;
    private static final float DEFAULT_SUN_COLOR_G = 1.0F;
    private static final float DEFAULT_SUN_COLOR_B = 1.0F;
    private static ClientLevel cachedLevel = null;
    private static long cachedDayTime = Long.MIN_VALUE;
    private static float cachedPartialTick = Float.NaN;
    private static double cachedCameraX = Double.NaN;
    private static double cachedCameraY = Double.NaN;
    private static double cachedCameraZ = Double.NaN;
    private static double cachedSkyX = 0.0D;
    private static double cachedSkyY = 0.0D;
    private static double cachedSkyZ = 0.0D;
    private static boolean cachedHasSunriseColor = false;
    private static float cachedSunriseR = 0.0F;
    private static float cachedSunriseG = 0.0F;
    private static float cachedSunriseB = 0.0F;
    private static float cachedSunriseA = 0.0F;
    private static float cachedSunDirX = DEFAULT_SUN_DIR_X;
    private static float cachedSunDirY = DEFAULT_SUN_DIR_Y;
    private static float cachedSunDirZ = DEFAULT_SUN_DIR_Z;
    private static float cachedSunColorR = DEFAULT_SUN_COLOR_R;
    private static float cachedSunColorG = DEFAULT_SUN_COLOR_G;
    private static float cachedSunColorB = DEFAULT_SUN_COLOR_B;
    private static float cachedSunWarmth = 0.0F;
    private static float cachedTimeOfDay = 0.0F;
    private static float cachedSunAngleTurns = 0.0F;

    private SimpleCloudsSunLighting() {
    }

    public static void apply(ShaderInstance shader) {
        if (shader == null) {
            return;
        }

        updateCachedInputs();
        setVec3(shader, "SunDirection", cachedSunDirX, cachedSunDirY, cachedSunDirZ);
        setVec3(shader, "SunColor", cachedSunColorR, cachedSunColorG, cachedSunColorB);
        setFloat(shader, "SunWarmth", cachedSunWarmth);
    }

    private static void updateCachedInputs() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null) {
            cachedLevel = null;
            cachedDayTime = Long.MIN_VALUE;
            cachedPartialTick = Float.NaN;
            cachedSunDirX = DEFAULT_SUN_DIR_X;
            cachedSunDirY = DEFAULT_SUN_DIR_Y;
            cachedSunDirZ = DEFAULT_SUN_DIR_Z;
            cachedSunColorR = DEFAULT_SUN_COLOR_R;
            cachedSunColorG = DEFAULT_SUN_COLOR_G;
            cachedSunColorB = DEFAULT_SUN_COLOR_B;
            cachedSunWarmth = 0.0F;
            cachedHasSunriseColor = false;
            return;
        }

        ClientLevel level = mc.level;
        float partialTick = mc.getFrameTime();
        long dayTime = level.getDayTime();
        Vec3 cameraPos = mc.gameRenderer != null ? mc.gameRenderer.getMainCamera().getPosition() : Vec3.ZERO;
        if (level == cachedLevel
            && dayTime == cachedDayTime
            && Float.compare(partialTick, cachedPartialTick) == 0
            && Double.compare(cameraPos.x, cachedCameraX) == 0
            && Double.compare(cameraPos.y, cachedCameraY) == 0
            && Double.compare(cameraPos.z, cachedCameraZ) == 0) {
            return;
        }

        cachedLevel = level;
        cachedDayTime = dayTime;
        cachedPartialTick = partialTick;
        cachedCameraX = cameraPos.x;
        cachedCameraY = cameraPos.y;
        cachedCameraZ = cameraPos.z;

        cachedTimeOfDay = level.getTimeOfDay(partialTick);
        float dayTicks = positiveModulo((float)(dayTime % (long)DAY_TICKS) + partialTick, DAY_TICKS);
        float twilightStrength = Math.max(
            computeTwilightStrength(dayTicks, SUNRISE_CENTER_TICKS),
            computeTwilightStrength(dayTicks, SUNSET_CENTER_TICKS)
        );
        cachedSunAngleTurns = getSunAngle(cachedTimeOfDay);
        float sunAngle = cachedSunAngleTurns * TWO_PI;

        cachedSunDirX = DEFAULT_SUN_DIR_X;
        cachedSunDirY = -Mth.cos(sunAngle);
        cachedSunDirZ = Mth.sin(sunAngle);

        float sunLength = Mth.sqrt(cachedSunDirX * cachedSunDirX + cachedSunDirY * cachedSunDirY + cachedSunDirZ * cachedSunDirZ);
        if (sunLength > EPSILON) {
            cachedSunDirX /= sunLength;
            cachedSunDirY /= sunLength;
            cachedSunDirZ /= sunLength;
        }

        Vec3 skyColor = level.getSkyColor(cameraPos, partialTick);
        cachedSkyX = skyColor.x;
        cachedSkyY = skyColor.y;
        cachedSkyZ = skyColor.z;
        cachedSunColorR = Mth.clamp((float)skyColor.x, 0.0F, 1.0F);
        cachedSunColorG = Mth.clamp((float)skyColor.y, 0.0F, 1.0F);
        cachedSunColorB = Mth.clamp((float)skyColor.z, 0.0F, 1.0F);
        cachedSunWarmth = computeWarmth(cachedSunColorR, cachedSunColorG, cachedSunColorB);

        float[] sunriseColor = level.effects().getSunriseColor(cachedTimeOfDay, partialTick);
        cachedHasSunriseColor = sunriseColor != null && sunriseColor.length >= 4;
        if (cachedHasSunriseColor) {
            cachedSunriseR = sunriseColor[0];
            cachedSunriseG = sunriseColor[1];
            cachedSunriseB = sunriseColor[2];
            cachedSunriseA = sunriseColor[3];
            float sunriseStrength = Mth.clamp(cachedSunriseA, 0.0F, 1.0F);
            float broadenedStrength = Math.max(sunriseStrength, twilightStrength * 0.85F);
            cachedSunWarmth = Math.max(cachedSunWarmth, broadenedStrength);
            float sunriseBlend = Math.max(sunriseStrength, twilightStrength * 0.65F) * 0.75F;
            cachedSunColorR = Mth.lerp(sunriseBlend, cachedSunColorR, Mth.clamp(cachedSunriseR, 0.0F, 1.0F));
            cachedSunColorG = Mth.lerp(sunriseBlend, cachedSunColorG, Mth.clamp(cachedSunriseG, 0.0F, 1.0F));
            cachedSunColorB = Mth.lerp(sunriseBlend, cachedSunColorB, Mth.clamp(cachedSunriseB, 0.0F, 1.0F));
        } else {
            cachedSunriseR = 0.0F;
            cachedSunriseG = 0.0F;
            cachedSunriseB = 0.0F;
            cachedSunriseA = 0.0F;
            cachedSunWarmth = Math.max(cachedSunWarmth, twilightStrength * 0.80F);
        }

        if (DEBUG_SUN_INPUTS) {
            logInputsOncePerSecond();
        }
    }

    private static float getSunAngle(float timeOfDay) {
        return timeOfDay < 0.75F ? timeOfDay + 0.25F : timeOfDay - 0.75F;
    }

    private static float positiveModulo(float value, float modulus) {
        float result = value % modulus;
        return result < 0.0F ? result + modulus : result;
    }

    private static float computeTwilightStrength(float dayTicks, float centerTicks) {
        float distance = wrappedDistance(dayTicks, centerTicks, DAY_TICKS);
        float fade = 1.0F - Mth.clamp((distance - TWILIGHT_INNER_RADIUS_TICKS) / (TWILIGHT_OUTER_RADIUS_TICKS - TWILIGHT_INNER_RADIUS_TICKS), 0.0F, 1.0F);
        return fade * fade * (3.0F - 2.0F * fade);
    }

    private static float wrappedDistance(float value, float center, float wrap) {
        float distance = Math.abs(value - center);
        return Math.min(distance, wrap - distance);
    }

    private static float computeWarmth(float r, float g, float b) {
        float warmth = Math.max(r - b, 0.0F);
        warmth += 0.35F * Math.max(g - b, 0.0F);
        warmth += 0.15F * Math.max(r - g, 0.0F);
        return Mth.clamp(warmth, 0.0F, 1.0F);
    }

    private static void logInputsOncePerSecond() {
        long now = System.currentTimeMillis();
        if (now - lastLogTimeMs < 1000L) {
            return;
        }

        lastLogTimeMs = now;
        String sunriseText = cachedHasSunriseColor
            ? String.format(Locale.ROOT, "[%.3f, %.3f, %.3f, %.3f]", cachedSunriseR, cachedSunriseG, cachedSunriseB, cachedSunriseA)
            : "null";
        String message = String.format(
            Locale.ROOT,
            "SC sun lighting inputs timeOfDay=%.3f sunAngleTurns=%.3f skyColor=(%.3f, %.3f, %.3f) sunriseColor=%s finalSunColor=(%.3f, %.3f, %.3f) sunWarmth=%.3f computedLightDir=(%.3f, %.3f, %.3f)",
            cachedTimeOfDay,
            cachedSunAngleTurns,
            cachedSkyX,
            cachedSkyY,
            cachedSkyZ,
            sunriseText,
            cachedSunColorR,
            cachedSunColorG,
            cachedSunColorB,
            cachedSunWarmth,
            cachedSunDirX,
            cachedSunDirY,
            cachedSunDirZ
        );
        LOGGER.debug(message);
    }

    private static void setVec3(ShaderInstance shader, String name, float x, float y, float z) {
        AbstractUniform uniform = shader.safeGetUniform(name);
        if (uniform != null) {
            uniform.set(x, y, z);
        }
    }

    private static void setFloat(ShaderInstance shader, String name, float value) {
        AbstractUniform uniform = shader.safeGetUniform(name);
        if (uniform != null) {
            uniform.set(value);
        }
    }
}
