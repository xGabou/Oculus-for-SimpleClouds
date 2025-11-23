/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.brigadier.CommandDispatcher
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.CloudMode
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.FogRenderer
 *  net.minecraft.client.renderer.FogRenderer$FogMode
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraft.server.packs.resources.ResourceManagerReloadListener
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.material.FogType
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingIn
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.client.event.CustomizeGuiOverlayEvent$DebugText
 *  net.minecraftforge.client.event.RegisterClientCommandsEvent
 *  net.minecraftforge.client.event.RegisterClientReloadListenersEvent
 *  net.minecraftforge.client.event.RegisterGuiOverlaysEvent
 *  net.minecraftforge.client.event.ViewportEvent$ComputeFogColor
 *  net.minecraftforge.client.event.ViewportEvent$RenderFog
 *  net.minecraftforge.client.gui.overlay.VanillaGuiOverlay
 *  net.minecraftforge.event.level.LevelEvent$Load
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.config.ModConfig$Type
 *  nonamecrackers2.crackerslib.client.event.impl.AddConfigEntryToMenuEvent
 *  nonamecrackers2.crackerslib.client.event.impl.ConfigMenuButtonEvent
 *  nonamecrackers2.crackerslib.client.event.impl.RegisterConfigScreensEvent
 *  nonamecrackers2.crackerslib.client.gui.ConfigHomeScreen
 *  nonamecrackers2.crackerslib.client.gui.title.ImageTitle
 *  nonamecrackers2.crackerslib.client.gui.title.TitleLogo
 *  nonamecrackers2.crackerslib.common.command.ConfigCommandBuilder
 *  nonamecrackers2.crackerslib.common.config.preset.ConfigPreset
 *  nonamecrackers2.crackerslib.common.config.preset.RegisterConfigPresetsEvent
 *  org.apache.commons.lang3.tuple.Pair
 */
package dev.nonamecrackers2.simpleclouds.client.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.CommandDispatcher;
import dev.nonamecrackers2.simpleclouds.SimpleCloudsMod;
import dev.nonamecrackers2.simpleclouds.api.common.cloud.CloudMode;
import dev.nonamecrackers2.simpleclouds.client.cloud.ClientSideCloudTypeManager;
import dev.nonamecrackers2.simpleclouds.client.cloud.spawning.ClientSideCloudSpawningManager;
import dev.nonamecrackers2.simpleclouds.client.command.ClientCloudCommandHelper;
import dev.nonamecrackers2.simpleclouds.client.command.profiling.ProfilingCommands;
import dev.nonamecrackers2.simpleclouds.client.compat.SimpleCloudsCompatHelper;
import dev.nonamecrackers2.simpleclouds.client.gui.CloudPreviewerScreen;
import dev.nonamecrackers2.simpleclouds.client.gui.SimpleCloudsConfigScreen;
import dev.nonamecrackers2.simpleclouds.client.mesh.LevelOfDetailOptions;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.GenerationInterval;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.MultiRegionCloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.SingleRegionCloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsDebugOverlayRenderer;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.renderer.WorldEffects;
import dev.nonamecrackers2.simpleclouds.client.renderer.settings.CloudsRendererSettings;
import dev.nonamecrackers2.simpleclouds.client.shader.compute.ComputeShader;
import dev.nonamecrackers2.simpleclouds.client.world.ClientCloudManager;
import dev.nonamecrackers2.simpleclouds.client.world.FogRenderMode;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudInfo;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudTypeDataManager;
import dev.nonamecrackers2.simpleclouds.common.config.SimpleCloudsConfig;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import java.awt.Color;
import java.io.Serializable;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import nonamecrackers2.crackerslib.client.event.impl.AddConfigEntryToMenuEvent;
import nonamecrackers2.crackerslib.client.event.impl.ConfigMenuButtonEvent;
import nonamecrackers2.crackerslib.client.event.impl.RegisterConfigScreensEvent;
import nonamecrackers2.crackerslib.client.gui.ConfigHomeScreen;
import nonamecrackers2.crackerslib.client.gui.title.ImageTitle;
import nonamecrackers2.crackerslib.client.gui.title.TitleLogo;
import nonamecrackers2.crackerslib.common.command.ConfigCommandBuilder;
import nonamecrackers2.crackerslib.common.config.preset.ConfigPreset;
import nonamecrackers2.crackerslib.common.config.preset.RegisterConfigPresetsEvent;
import org.apache.commons.lang3.tuple.Pair;

public class SimpleCloudsClientEvents {
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerBelow(VanillaGuiOverlay.DEBUG_TEXT.id(), "simple_clouds_debug", SimpleCloudsDebugOverlayRenderer::render);
    }

    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        CloudTypeDataManager manager = ClientSideCloudTypeManager.getInstance().getClientSideDataManager();
        event.registerReloadListener((PreparableReloadListener)manager);
        ClientSideCloudSpawningManager.optionalInitializeOnClient(manager);
        event.registerReloadListener((PreparableReloadListener)ClientSideCloudSpawningManager.getClientInstance());
        SimpleCloudsRenderer.initialize(CloudsRendererSettings.DEFAULT);
        event.registerReloadListener((PreparableReloadListener)((ResourceManagerReloadListener)m -> ComputeShader.destroyCompiledShaders()));
        event.registerReloadListener((PreparableReloadListener)SimpleCloudsCompatHelper.getRendererReloadListener(SimpleCloudsRenderer.getInstance()));
        CloudPreviewerScreen.addCloudMeshListener(event);
    }

    public static void registerConfigMenu(RegisterConfigScreensEvent event) {
        event.builder(ConfigHomeScreen.builder((TitleLogo)ImageTitle.ofMod((String)"simpleclouds", (int)192, (int)96, (float)1.0f)).crackersDefault("https://github.com/nonamecrackers2/simple-clouds").build(SimpleCloudsConfigScreen::new)).addSpec(ModConfig.Type.CLIENT, SimpleCloudsConfig.CLIENT_SPEC).addSpec(ModConfig.Type.COMMON, SimpleCloudsConfig.COMMON_SPEC).addSpec(ModConfig.Type.SERVER, SimpleCloudsConfig.SERVER_SPEC).register();
    }

    public static void registerConfigMenuButton(ConfigMenuButtonEvent event) {
        event.defaultButtonWithSingleCharacter('S', -5376001);
    }

    public static void registerClientPresets(RegisterConfigPresetsEvent event) {
        event.registerPreset(ModConfig.Type.CLIENT, ConfigPreset.builder((Component)Component.m_237115_((String)"simpleclouds.config.preset.medium")).setDescription((Component)Component.m_237115_((String)"simpleclouds.config.preset.medium.description")).setPreset(SimpleCloudsConfig.CLIENT.framesToGenerateMesh, (Object)10).setPreset(SimpleCloudsConfig.CLIENT.generationInterval, (Object)GenerationInterval.STATIC).setPreset(SimpleCloudsConfig.CLIENT.levelOfDetail, (Object)LevelOfDetailOptions.MEDIUM).setPreset(SimpleCloudsConfig.CLIENT.shadowDistance, (Object)2500).build());
        event.registerPreset(ModConfig.Type.CLIENT, ConfigPreset.builder((Component)Component.m_237115_((String)"simpleclouds.config.preset.low")).setDescription((Component)Component.m_237115_((String)"simpleclouds.config.preset.low.description")).setPreset(SimpleCloudsConfig.CLIENT.framesToGenerateMesh, (Object)20).setPreset(SimpleCloudsConfig.CLIENT.generationInterval, (Object)GenerationInterval.DYNAMIC).setPreset(SimpleCloudsConfig.CLIENT.levelOfDetail, (Object)LevelOfDetailOptions.LOW).setPreset(SimpleCloudsConfig.CLIENT.transparency, (Object)false).setPreset(SimpleCloudsConfig.CLIENT.atmosphericClouds, (Object)false).setPreset(SimpleCloudsConfig.CLIENT.shadowDistance, (Object)2500).setPreset(SimpleCloudsConfig.CLIENT.distantShadows, (Object)false).build());
        event.registerPreset(ModConfig.Type.CLIENT, ConfigPreset.builder((Component)Component.m_237115_((String)"simpleclouds.config.preset.ultra_low")).setDescription((Component)Component.m_237115_((String)"simpleclouds.config.preset.ultra_low.description")).setPreset(SimpleCloudsConfig.CLIENT.framesToGenerateMesh, (Object)20).setPreset(SimpleCloudsConfig.CLIENT.generationInterval, (Object)GenerationInterval.DYNAMIC).setPreset(SimpleCloudsConfig.CLIENT.levelOfDetail, (Object)LevelOfDetailOptions.LOW).setPreset(SimpleCloudsConfig.CLIENT.transparency, (Object)false).setPreset(SimpleCloudsConfig.CLIENT.renderStormFog, (Object)false).setPreset(SimpleCloudsConfig.CLIENT.atmosphericClouds, (Object)false).setPreset(SimpleCloudsConfig.CLIENT.shadowDistance, (Object)1000).setPreset(SimpleCloudsConfig.CLIENT.distantShadows, (Object)false).build());
        event.registerPreset(ModConfig.Type.CLIENT, ConfigPreset.builder((Component)Component.m_237115_((String)"simpleclouds.config.preset.classic_style")).setDescription((Component)Component.m_237115_((String)"simpleclouds.config.preset.classic_style.description")).setPreset(SimpleCloudsConfig.CLIENT.transparency, (Object)false).setPreset(SimpleCloudsConfig.CLIENT.cubeNormals, (Object)true).setPreset(SimpleCloudsConfig.CLIENT.shadedClouds, (Object)false).setPreset(SimpleCloudsConfig.CLIENT.atmosphericClouds, (Object)false).build());
    }

    @SubscribeEvent
    public static void registerClientCommands(RegisterClientCommandsEvent event) {
        ConfigCommandBuilder.builder((CommandDispatcher)event.getDispatcher(), (String)"simpleclouds").addSpec(ModConfig.Type.CLIENT, SimpleCloudsConfig.CLIENT_SPEC).register();
        ClientCloudCommandHelper.register((CommandDispatcher<CommandSourceStack>)event.getDispatcher());
        ProfilingCommands.register((CommandDispatcher<CommandSourceStack>)event.getDispatcher());
    }

    @SubscribeEvent
    public static void onAddConfigOptionToMenu(AddConfigEntryToMenuEvent event) {
        if (event.getModId().equals("simpleclouds") && event.getType() == ModConfig.Type.CLIENT) {
            if (event.isValue(SimpleCloudsConfig.CLIENT.showCloudPreviewerInfoPopup) || event.isValue(SimpleCloudsConfig.CLIENT.showVivecraftNotice)) {
                event.setCanceled(true);
            }
            if (ClientCloudManager.isAvailableServerSide() && (event.isValue(SimpleCloudsConfig.CLIENT.cloudHeight) || event.isValue(SimpleCloudsConfig.CLIENT.speedModifier) || event.isValue(SimpleCloudsConfig.CLIENT.cloudMode) || event.isValue(SimpleCloudsConfig.CLIENT.singleModeCloudType) || event.isValue(SimpleCloudsConfig.CLIENT.cloudSeed) || event.isValue(SimpleCloudsConfig.CLIENT.useSpecificSeed) || event.isValue(SimpleCloudsConfig.CLIENT.whitelistAsBlacklist) || event.isValue(SimpleCloudsConfig.CLIENT.dimensionWhitelist))) {
                event.setCanceled(true);
            }
            if (!SimpleCloudsMod.dhLoaded() && (event.isValue(SimpleCloudsConfig.CLIENT.distantShadows) || event.isValue(SimpleCloudsConfig.CLIENT.shadowDistance))) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel().m_5776_()) {
            SimpleCloudsRenderer.getInstance().getWorldEffectsManager().reset();
        }
    }

    @SubscribeEvent
    public static void onClientLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        CloudManager.get(event.getPlayer().m_9236_()).onPlayerJoin((Player)event.getPlayer());
        SimpleCloudsRenderer.getInstance().requestReload();
    }

    @SubscribeEvent
    public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientSideCloudTypeManager.getInstance().clearSynced();
        SimpleCloudsRenderer.getInstance().getWorldEffectsManager().reset();
    }

    @SubscribeEvent
    public static void modifyFog(ViewportEvent.RenderFog event) {
        if (event.getMode() == FogRenderer.FogMode.FOG_TERRAIN && Minecraft.m_91087_().f_91063_.m_109153_().m_167685_() == FogType.NONE) {
            if (SimpleCloudsConfig.CLIENT.fogMode.get() == FogRenderMode.OFF) {
                FogRenderer.m_109017_();
                return;
            }
            SimpleCloudsRenderer renderer = SimpleCloudsRenderer.getInstance();
            WorldEffects effects = renderer.getWorldEffectsManager();
            float partialTick = (float)event.getPartialTick();
            float storminess = Mth.m_14116_((float)effects.getDarkenFactor(partialTick, 2.0f));
            RenderSystem.setShaderFogStart((float)(RenderSystem.getShaderFogStart() * storminess));
        }
    }

    @SubscribeEvent
    public static void modifyFogColor(ViewportEvent.ComputeFogColor event) {
        if (SimpleCloudsConfig.CLIENT.fogMode.get() != FogRenderMode.OFF && Minecraft.m_91087_().f_91063_.m_109153_().m_167685_() == FogType.NONE) {
            SimpleCloudsRenderer renderer = SimpleCloudsRenderer.getInstance();
            WorldEffects effects = renderer.getWorldEffectsManager();
            float partialTick = (float)event.getPartialTick();
            Color finalCol = effects.calculateFogColor(event.getRed(), event.getGreen(), event.getBlue(), partialTick);
            event.setRed((float)finalCol.getRed() / 255.0f);
            event.setGreen((float)finalCol.getGreen() / 255.0f);
            event.setBlue((float)finalCol.getBlue() / 255.0f);
        }
    }

    @SubscribeEvent
    public static void onRenderDebugOverlay(CustomizeGuiOverlayEvent.DebugText event) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91066_.f_92063_) {
            SimpleCloudsRenderer renderer = SimpleCloudsRenderer.getInstance();
            ArrayList text = event.getRight();
            text.add("");
            text.add(ChatFormatting.GREEN + "simpleclouds: " + SimpleCloudsMod.getModVersion());
            text.add(renderer.getClientCloudManagerString());
            if (SimpleCloudsRenderer.canRenderInDimension(mc.f_91073_)) {
                CloudMeshGenerator generator = renderer.getMeshGenerator();
                Pair<CloudMeshGenerator.MeshGenStatus, CloudMeshGenerator.MeshGenStatus> meshGenResult = generator.getMeshGenStatus();
                CloudMeshGenerator.MeshGenStatus opaqueStatus = (CloudMeshGenerator.MeshGenStatus)((Object)meshGenResult.getLeft());
                CloudMeshGenerator.MeshGenStatus transparentStatus = (CloudMeshGenerator.MeshGenStatus)((Object)meshGenResult.getRight());
                text.add((Serializable)(opaqueStatus.isErroneous() ? ChatFormatting.RED : "") + "Mesh status opaque: " + opaqueStatus.getName());
                text.add((Serializable)(transparentStatus.isErroneous() ? ChatFormatting.RED : "") + "Mesh status transparent: " + transparentStatus.getName());
                String opaqueGeomInfo = SimpleCloudsClientEvents.humanReadableByteCountSI(generator.getOpaqueBufferBytesUsed()) + "/" + SimpleCloudsClientEvents.humanReadableByteCountSI(generator.getOpaqueBufferSize());
                String transparentGeomInfo = SimpleCloudsClientEvents.humanReadableByteCountSI(generator.getTransparentBufferBytesUsed()) + "/" + SimpleCloudsClientEvents.humanReadableByteCountSI(generator.getTransparentBufferSize());
                text.add("O: " + opaqueGeomInfo + " | T: " + transparentGeomInfo);
                int interval = generator.getMeshGenInterval();
                text.add("Mesh gen frames: " + interval + "; Effective FPS: " + mc.m_260875_() / interval);
                text.add("Frustum culling: " + ((Boolean)SimpleCloudsConfig.CLIENT.frustumCulling.get() != false ? "ON" : "OFF"));
                boolean flag = ClientCloudManager.isAvailableServerSide();
                text.add("Server-side: " + (flag ? ChatFormatting.GREEN : ChatFormatting.RED) + flag);
                CloudMode mode = renderer.getSettings().getCurrentCloudMode();
                text.add("Cloud mode: " + mode);
                if (generator instanceof SingleRegionCloudMeshGenerator) {
                    SingleRegionCloudMeshGenerator singleGenerator = (SingleRegionCloudMeshGenerator)generator;
                    text.add("Fade start: " + singleGenerator.getFadeStart() + "; Fade end: " + singleGenerator.getFadeEnd());
                    CloudInfo cloudInfo = singleGenerator.getCloudType();
                    if (cloudInfo instanceof CloudType) {
                        CloudType type = (CloudType)cloudInfo;
                        text.add("Cloud type: " + type.id());
                    }
                } else if (generator instanceof MultiRegionCloudMeshGenerator) {
                    MultiRegionCloudMeshGenerator multiRegion = (MultiRegionCloudMeshGenerator)generator;
                    text.add("Cloud types: " + ClientSideCloudTypeManager.getInstance().getCloudTypes().size());
                    int formationCount = multiRegion.getCloudFormationCount();
                    String formationText = "Cloud formations: " + formationCount + "/10";
                    if (formationCount > 10) {
                        formationText = ChatFormatting.RED + formationText;
                    }
                    text.add(formationText);
                }
                if (mc.f_91073_ != null) {
                    CloudManager<ClientLevel> manager = CloudManager.get(mc.f_91073_);
                    text.add("Speed: " + SimpleCloudsClientEvents.round(manager.getCloudSpeed()) + "; Height: " + manager.getCloudHeight());
                    text.add("Scroll XYZ: " + SimpleCloudsClientEvents.round(manager.getScrollX()) + " / " + SimpleCloudsClientEvents.round(manager.getScrollY()) + " / " + SimpleCloudsClientEvents.round(manager.getScrollZ()));
                    WorldEffects effects = renderer.getWorldEffectsManager();
                    CloudType atCamera = effects.getCloudTypeAtCamera();
                    if (atCamera != null) {
                        text.add(atCamera.id().toString());
                    } else {
                        text.add("UNKNOWN");
                    }
                    String vanillaWeatherOverrideAppend = manager.shouldUseVanillaWeather() ? " (Vanilla Weather Enabled)" : "";
                    text.add("Storminess: " + SimpleCloudsClientEvents.round(effects.getStorminessAtCamera()) + vanillaWeatherOverrideAppend);
                }
            } else {
                text.add(ChatFormatting.RED + "Disabled in this dimension");
            }
        }
    }

    private static String humanReadableByteCountSI(long bytes) {
        if (-1000L < bytes && bytes < 1000L) {
            return bytes + " B";
        }
        StringCharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999950L || bytes >= 999950L) {
            bytes /= 1000L;
            ci.next();
        }
        return String.format("%.1f %cB", (double)bytes / 1000.0, Character.valueOf(ci.current()));
    }

    private static float round(float val) {
        return (float)Math.round(val * 100.0f) / 100.0f;
    }
}

