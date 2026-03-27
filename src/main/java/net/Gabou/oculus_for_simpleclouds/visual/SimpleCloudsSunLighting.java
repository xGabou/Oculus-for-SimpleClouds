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

    private SimpleCloudsSunLighting() {
    }

    public static void apply(ShaderInstance shader) {
        if (shader == null) {
            return;
        }

        float sunDirX = DEFAULT_SUN_DIR_X;
        float sunDirY = DEFAULT_SUN_DIR_Y;
        float sunDirZ = DEFAULT_SUN_DIR_Z;
        float sunColorR = DEFAULT_SUN_COLOR_R;
        float sunColorG = DEFAULT_SUN_COLOR_G;
        float sunColorB = DEFAULT_SUN_COLOR_B;
        float sunWarmth = 0.0F;
        float timeOfDay = 0.0F;
        float sunAngleTurns = 0.0F;

        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.level != null) {
            ClientLevel level = mc.level;
            float partialTick = mc.getFrameTime();
            timeOfDay = level.getTimeOfDay(partialTick);
            float dayTicks = positiveModulo((float)(level.getDayTime() % (long)DAY_TICKS) + partialTick, DAY_TICKS);
            float twilightStrength = Math.max(
                computeTwilightStrength(dayTicks, SUNRISE_CENTER_TICKS),
                computeTwilightStrength(dayTicks, SUNSET_CENTER_TICKS)
            );
            sunAngleTurns = getSunAngle(timeOfDay);
            float sunAngle = sunAngleTurns * TWO_PI;

            sunDirY = -Mth.cos(sunAngle);
            sunDirZ = Mth.sin(sunAngle);

            float sunLength = Mth.sqrt(sunDirX * sunDirX + sunDirY * sunDirY + sunDirZ * sunDirZ);
            if (sunLength > EPSILON) {
                sunDirX /= sunLength;
                sunDirY /= sunLength;
                sunDirZ /= sunLength;
            }

            Vec3 cameraPos = mc.gameRenderer != null ? mc.gameRenderer.getMainCamera().getPosition() : Vec3.ZERO;
            Vec3 skyColor = level.getSkyColor(cameraPos, partialTick);
            sunColorR = Mth.clamp((float)skyColor.x, 0.0F, 1.0F);
            sunColorG = Mth.clamp((float)skyColor.y, 0.0F, 1.0F);
            sunColorB = Mth.clamp((float)skyColor.z, 0.0F, 1.0F);
            sunWarmth = computeWarmth(sunColorR, sunColorG, sunColorB);

            float[] sunriseColor = level.effects().getSunriseColor(timeOfDay, partialTick);
            if (sunriseColor != null && sunriseColor.length >= 4) {
                float sunriseStrength = Mth.clamp(sunriseColor[3], 0.0F, 1.0F);
                float broadenedStrength = Math.max(sunriseStrength, twilightStrength * 0.85F);
                sunWarmth = Math.max(sunWarmth, broadenedStrength);
                if (sunriseColor.length >= 3) {
                    float sunriseBlend = Math.max(sunriseStrength, twilightStrength * 0.65F) * 0.75F;
                    sunColorR = Mth.lerp(sunriseBlend, sunColorR, Mth.clamp(sunriseColor[0], 0.0F, 1.0F));
                    sunColorG = Mth.lerp(sunriseBlend, sunColorG, Mth.clamp(sunriseColor[1], 0.0F, 1.0F));
                    sunColorB = Mth.lerp(sunriseBlend, sunColorB, Mth.clamp(sunriseColor[2], 0.0F, 1.0F));
                }
            } else {
                sunWarmth = Math.max(sunWarmth, twilightStrength * 0.80F);
            }

            logInputsOncePerSecond(
                timeOfDay,
                sunAngleTurns,
                skyColor,
                sunriseColor,
                sunColorR,
                sunColorG,
                sunColorB,
                sunWarmth,
                sunDirX,
                sunDirY,
                sunDirZ
            );
        }

        setVec3(shader, "SunDirection", sunDirX, sunDirY, sunDirZ);
        setVec3(shader, "SunColor", sunColorR, sunColorG, sunColorB);
        setFloat(shader, "SunWarmth", sunWarmth);
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

    private static void logInputsOncePerSecond(float timeOfDay, float sunAngleTurns, Vec3 skyColor, float[] sunriseColor, float sunColorR, float sunColorG, float sunColorB, float sunWarmth, float sunDirX, float sunDirY, float sunDirZ) {
        long now = System.currentTimeMillis();
        if (now - lastLogTimeMs < 1000L) {
            return;
        }

        lastLogTimeMs = now;
        String sunriseText = sunriseColor != null && sunriseColor.length >= 4
            ? String.format(Locale.ROOT, "[%.3f, %.3f, %.3f, %.3f]", sunriseColor[0], sunriseColor[1], sunriseColor[2], sunriseColor[3])
            : "null";
        String message = String.format(
            Locale.ROOT,
            "SC sun lighting inputs timeOfDay=%.3f sunAngleTurns=%.3f skyColor=(%.3f, %.3f, %.3f) sunriseColor=%s finalSunColor=(%.3f, %.3f, %.3f) sunWarmth=%.3f computedLightDir=(%.3f, %.3f, %.3f)",
            timeOfDay,
            sunAngleTurns,
            skyColor.x,
            skyColor.y,
            skyColor.z,
            sunriseText,
            sunColorR,
            sunColorG,
            sunColorB,
            sunWarmth,
            sunDirX,
            sunDirY,
            sunDirZ
        );
        LOGGER.info(message);
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
