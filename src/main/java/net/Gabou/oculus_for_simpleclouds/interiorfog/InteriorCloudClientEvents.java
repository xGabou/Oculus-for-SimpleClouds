package net.Gabou.oculus_for_simpleclouds.interiorfog;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InteriorCloudClientEvents {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void modifyFog(ViewportEvent.RenderFog event) {
        if (!InteriorCloudConfig.ENABLED.get() || !InteriorCloudConfig.MODIFY_FOG.get() || event.getMode() != FogMode.FOG_TERRAIN) {
            return;
        }
        if (Minecraft.getInstance().gameRenderer.getMainCamera().getFluidInCamera() != FogType.NONE) {
            return;
        }

        float strength = InteriorCloudState.currentStrength((float) event.getPartialTick());
        if (strength <= 0.0F) {
            return;
        }

        float fogStart = Mth.lerp(strength, RenderSystem.getShaderFogStart(), InteriorCloudConfig.FOG_START.get().floatValue());
        float fogEnd = Mth.lerp(strength, RenderSystem.getShaderFogEnd(), InteriorCloudConfig.FOG_END.get().floatValue());
        RenderSystem.setShaderFogStart(Math.min(fogStart, fogEnd - 1.0F));
        RenderSystem.setShaderFogEnd(fogEnd);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void modifyFogColor(ViewportEvent.ComputeFogColor event) {
        if (!InteriorCloudConfig.ENABLED.get() || !InteriorCloudConfig.MODIFY_FOG_COLOR.get()) {
            return;
        }
        if (Minecraft.getInstance().gameRenderer.getMainCamera().getFluidInCamera() != FogType.NONE) {
            return;
        }

        float strength = InteriorCloudState.currentStrength((float) event.getPartialTick());
        if (strength <= 0.0F) {
            return;
        }

        float blend = Mth.clamp(strength * InteriorCloudConfig.FOG_COLOR_BLEND.get().floatValue(), 0.0F, 1.0F);
        event.setRed(Mth.lerp(blend, event.getRed(), InteriorCloudConfig.COLOR_RED.get().floatValue()));
        event.setGreen(Mth.lerp(blend, event.getGreen(), InteriorCloudConfig.COLOR_GREEN.get().floatValue()));
        event.setBlue(Mth.lerp(blend, event.getBlue(), InteriorCloudConfig.COLOR_BLUE.get().floatValue()));
    }

    private InteriorCloudClientEvents() {
    }
}
