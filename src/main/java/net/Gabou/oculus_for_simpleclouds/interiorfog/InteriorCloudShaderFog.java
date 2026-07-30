package net.Gabou.oculus_for_simpleclouds.interiorfog;

import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;

public final class InteriorCloudShaderFog {
    private InteriorCloudShaderFog() {
    }

    public static void apply(ShaderInstance shader) {
        if (shader == null || !InteriorCloudConfig.ENABLED.get()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        float partialTick = mc == null ? 0.0F : mc.getFrameTime();
        float strength = InteriorCloudState.currentStrength(partialTick);
        if (strength <= 0.0F) {
            return;
        }

        if (InteriorCloudConfig.MODIFY_FOG.get()) {
            float fogStart = Mth.lerp(strength, RenderSystem.getShaderFogStart(), InteriorCloudConfig.FOG_START.get().floatValue());
            float fogEnd = Mth.lerp(strength, RenderSystem.getShaderFogEnd(), InteriorCloudConfig.FOG_END.get().floatValue());
            setFloat(shader, "FogStart", Math.min(fogStart, fogEnd - 1.0F));
            setFloat(shader, "FogEnd", fogEnd);
        }

        if (InteriorCloudConfig.MODIFY_FOG_COLOR.get()) {
            float[] fogColor = RenderSystem.getShaderFogColor();
            float blend = Mth.clamp(strength * InteriorCloudConfig.FOG_COLOR_BLEND.get().floatValue(), 0.0F, 1.0F);
            float red = Mth.lerp(blend, fogColor[0], InteriorCloudConfig.COLOR_RED.get().floatValue());
            float green = Mth.lerp(blend, fogColor[1], InteriorCloudConfig.COLOR_GREEN.get().floatValue());
            float blue = Mth.lerp(blend, fogColor[2], InteriorCloudConfig.COLOR_BLUE.get().floatValue());
            float alpha = fogColor.length > 3 ? fogColor[3] : 1.0F;
            setVec4(shader, "FogColor", red, green, blue, alpha);
        }
    }

    private static void setFloat(ShaderInstance shader, String name, float value) {
        AbstractUniform uniform = shader.safeGetUniform(name);
        if (uniform != null) {
            uniform.set(value);
        }
    }

    private static void setVec4(ShaderInstance shader, String name, float x, float y, float z, float w) {
        AbstractUniform uniform = shader.safeGetUniform(name);
        if (uniform != null) {
            uniform.set(x, y, z, w);
        }
    }
}
