/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.fml.IExtensionPoint$DisplayTest
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.fml.ModLoadingContext
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.config.IConfigSpec
 *  net.minecraftforge.fml.config.ModConfig$Type
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 *  net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 *  org.apache.maven.artifact.versioning.ArtifactVersion
 */
package dev.nonamecrackers2.simpleclouds;

import dev.nonamecrackers2.simpleclouds.client.config.SimpleCloudsClientConfigListeners;
import dev.nonamecrackers2.simpleclouds.client.dh.SimpleCloudsDhCompatHandler;
import dev.nonamecrackers2.simpleclouds.client.event.SimpleCloudsClientEvents;
import dev.nonamecrackers2.simpleclouds.client.keybind.SimpleCloudsKeybinds;
import dev.nonamecrackers2.simpleclouds.client.renderer.WorldEffects;
import dev.nonamecrackers2.simpleclouds.client.shader.SimpleCloudsShaders;
import dev.nonamecrackers2.simpleclouds.common.api.SimpleCloudsAPIImpl;
import dev.nonamecrackers2.simpleclouds.common.config.SimpleCloudsConfig;
import dev.nonamecrackers2.simpleclouds.common.config.SimpleCloudsConfigListeners;
import dev.nonamecrackers2.simpleclouds.common.event.CloudManagerEvents;
import dev.nonamecrackers2.simpleclouds.common.event.SimpleCloudsDataEvents;
import dev.nonamecrackers2.simpleclouds.common.event.SimpleCloudsEvents;
import dev.nonamecrackers2.simpleclouds.common.init.SimpleCloudsCommandArguments;
import dev.nonamecrackers2.simpleclouds.common.init.SimpleCloudsSounds;
import dev.nonamecrackers2.simpleclouds.common.packet.SimpleCloudsPacketHandlers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.maven.artifact.versioning.ArtifactVersion;

@Mod(value="simpleclouds")
public class SimpleCloudsMod {
    public static final String MODID = "simpleclouds";
    private static final String DH_MODID = "distanthorizons";
    private static ArtifactVersion version;
    private static boolean dhLoaded;

    public SimpleCloudsMod() {
        version = ModLoadingContext.get().getActiveContainer().getModInfo().getVersion();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        modBus.addListener(this::clientInit);
        modBus.addListener(this::commonInit);
        SimpleCloudsSounds.register(modBus);
        SimpleCloudsCommandArguments.register(modBus);
        DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> {
            modBus.addListener(SimpleCloudsClientEvents::registerReloadListeners);
            modBus.addListener(SimpleCloudsKeybinds::registerKeyMappings);
            modBus.addListener(SimpleCloudsClientEvents::registerOverlays);
            modBus.addListener(SimpleCloudsClientEvents::registerClientPresets);
            forgeBus.register(WorldEffects.class);
            SimpleCloudsClientConfigListeners.registerListener();
        });
        modBus.addListener(SimpleCloudsDataEvents::gatherData);
        ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.CLIENT, (IConfigSpec)SimpleCloudsConfig.CLIENT_SPEC);
        context.registerConfig(ModConfig.Type.COMMON, (IConfigSpec)SimpleCloudsConfig.COMMON_SPEC);
        context.registerConfig(ModConfig.Type.SERVER, (IConfigSpec)SimpleCloudsConfig.SERVER_SPEC);
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "OHNOES\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31", (a, b) -> true));
        SimpleCloudsAPIImpl.bootstrap();
    }

    private void commonInit(FMLCommonSetupEvent event) {
        SimpleCloudsPacketHandlers.register();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.register(CloudManagerEvents.class);
        forgeBus.register(SimpleCloudsEvents.class);
        SimpleCloudsConfigListeners.registerListener();
        dhLoaded = ModList.get().isLoaded(DH_MODID);
    }

    private void clientInit(FMLClientSetupEvent event) {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.register(SimpleCloudsShaders.class);
        modBus.addListener(SimpleCloudsClientEvents::registerConfigMenu);
        modBus.addListener(SimpleCloudsClientEvents::registerConfigMenuButton);
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.register(SimpleCloudsClientEvents.class);
        forgeBus.register(SimpleCloudsKeybinds.class);
        if (ModList.get().isLoaded(DH_MODID)) {
            event.enqueueWork(() -> SimpleCloudsDhCompatHandler.initialize());
        }
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static ArtifactVersion getModVersion() {
        return version;
    }

    public static boolean dhLoaded() {
        return dhLoaded;
    }
}

