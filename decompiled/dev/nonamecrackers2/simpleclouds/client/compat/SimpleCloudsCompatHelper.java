/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.packs.resources.ResourceManagerReloadListener
 *  net.minecraftforge.fml.ModList
 *  nonamecrackers2.crackerslib.common.compat.CompatHelper
 */
package dev.nonamecrackers2.simpleclouds.client.compat;

import com.mojang.blaze3d.pipeline.RenderTarget;
import dev.nonamecrackers2.simpleclouds.SimpleCloudsMod;
import dev.nonamecrackers2.simpleclouds.client.gui.SimpleCloudsNoticeScreen;
import dev.nonamecrackers2.simpleclouds.client.mesh.RendererInitializeResult;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.vivecraft.SimpleCloudsReloadVivecraftCompatWrapper;
import dev.nonamecrackers2.simpleclouds.client.vivecraft.SimpleCloudsVivecraftCompatHandler;
import dev.nonamecrackers2.simpleclouds.common.config.SimpleCloudsConfig;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.fml.ModList;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;

public class SimpleCloudsCompatHelper {
    public static ResourceManagerReloadListener getRendererReloadListener(SimpleCloudsRenderer renderer) {
        if (CompatHelper.isVivecraftLoaded()) {
            return new SimpleCloudsReloadVivecraftCompatWrapper(renderer);
        }
        return renderer;
    }

    public static boolean renderCustomRain() {
        return (Boolean)SimpleCloudsConfig.CLIENT.renderCustomRain.get() != false && !ModList.get().isLoaded("particlerain");
    }

    public static boolean useCustomRainSounds() {
        return (Boolean)SimpleCloudsConfig.CLIENT.customRainSounds.get();
    }

    @Nullable
    public static RenderTarget getMainRenderTarget() {
        if (CompatHelper.isVivecraftLoaded()) {
            return SimpleCloudsVivecraftCompatHandler.getMainFrameBuffer();
        }
        return Minecraft.m_91087_().m_91385_();
    }

    public static boolean renderThisPass() {
        if (CompatHelper.isVivecraftLoaded()) {
            return SimpleCloudsVivecraftCompatHandler.renderThisPass();
        }
        return true;
    }

    public static boolean isPrimaryPass() {
        if (CompatHelper.isVivecraftLoaded()) {
            return SimpleCloudsVivecraftCompatHandler.isPrimaryPass();
        }
        return true;
    }

    public static int getStormFogResolutionDivisor() {
        if (CompatHelper.isVivecraftLoaded()) {
            return SimpleCloudsVivecraftCompatHandler.getStormFogResolutionDivisor();
        }
        return 4;
    }

    @Nullable
    public static SimpleCloudsNoticeScreen createNotice() {
        if (CompatHelper.isVivecraftLoaded() && ((Boolean)SimpleCloudsConfig.CLIENT.showVivecraftNotice.get()).booleanValue()) {
            SimpleCloudsConfig.CLIENT.showVivecraftNotice.set((Object)false);
            return new SimpleCloudsNoticeScreen((Component)Component.m_237115_((String)"gui.simpleclouds.notice.vivecraft"));
        }
        return null;
    }

    @Nullable
    public static RendererInitializeResult findCompatErrors() {
        RendererInitializeResult.Builder result = RendererInitializeResult.builder();
        if (CompatHelper.isOculusLoaded() && SimpleCloudsMod.dhLoaded()) {
            result.addError(null, "Simple Clouds Notice", (Component)Component.m_237115_((String)"gui.simpleclouds.error.compat.dh_oculus"));
        }
        return result.build();
    }
}

