package net.Gabou.oculus_for_simpleclouds.visual;

import com.mojang.blaze3d.shaders.AbstractUniform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class SimpleCloudsSunLighting {
    private static final float TWO_PI = (float)(Math.PI * 2.0);

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

        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.level != null) {
            ClientLevel level = mc.level;
            float partialTick = mc.getFrameTime();
            float timeOfDay = level.getTimeOfDay(partialTick);
            float sunAngle = timeOfDay * TWO_PI;

            sunDirY = Mth.cos(sunAngle);
            sunDirZ = Mth.sin(sunAngle);

            float sunLength = Mth.sqrt(sunDirX * sunDirX + sunDirY * sunDirY + sunDirZ * sunDirZ);
            if (sunLength > 1.0E-4F) {
                sunDirX /= sunLength;
                sunDirY /= sunLength;
                sunDirZ /= sunLength;
            }

            Vec3 cloudColor = level.getCloudColor(partialTick);
            sunColorR = Mth.clamp((float)cloudColor.x, 0.0F, 1.0F);
            sunColorG = Mth.clamp((float)cloudColor.y, 0.0F, 1.0F);
            sunColorB = Mth.clamp((float)cloudColor.z, 0.0F, 1.0F);

            float[] sunriseColor = level.effects().getSunriseColor(timeOfDay, partialTick);
            if (sunriseColor != null && sunriseColor.length >= 4) {
                sunWarmth = Mth.clamp(sunriseColor[3] * 1.25F, 0.0F, 1.0F);
                if (sunriseColor.length >= 3) {
                    sunColorR = Mth.lerp(sunWarmth, sunColorR, Mth.clamp(sunriseColor[0], 0.0F, 1.0F));
                    sunColorG = Mth.lerp(sunWarmth, sunColorG, Mth.clamp(sunriseColor[1], 0.0F, 1.0F));
                    sunColorB = Mth.lerp(sunWarmth, sunColorB, Mth.clamp(sunriseColor[2], 0.0F, 1.0F));
                }
            }
        }

        setVec3(shader, "SunDirection", sunDirX, sunDirY, sunDirZ);
        setVec3(shader, "SunColor", sunColorR, sunColorG, sunColorB);
        setFloat(shader, "SunWarmth", sunWarmth);
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
