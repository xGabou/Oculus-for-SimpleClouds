/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraftforge.event.AddReloadListenerEvent
 *  net.minecraftforge.event.OnDatapackSyncEvent
 *  net.minecraftforge.event.RegisterCommandsEvent
 *  net.minecraftforge.event.entity.player.SleepingTimeCheckEvent
 *  net.minecraftforge.event.level.SleepFinishedTimeEvent
 *  net.minecraftforge.eventbus.api.Event$Result
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.config.ModConfig$Type
 *  net.minecraftforge.network.PacketDistributor
 *  net.minecraftforge.network.PacketDistributor$PacketTarget
 *  nonamecrackers2.crackerslib.common.command.ConfigCommandBuilder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package dev.nonamecrackers2.simpleclouds.common.event;

import com.mojang.brigadier.CommandDispatcher;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudTypeDataManager;
import dev.nonamecrackers2.simpleclouds.common.cloud.region.CloudRegion;
import dev.nonamecrackers2.simpleclouds.common.cloud.spawning.CloudGenerator;
import dev.nonamecrackers2.simpleclouds.common.cloud.spawning.CloudSpawningDataManager;
import dev.nonamecrackers2.simpleclouds.common.command.CloudCommandSource;
import dev.nonamecrackers2.simpleclouds.common.command.CloudCommands;
import dev.nonamecrackers2.simpleclouds.common.config.SimpleCloudsConfig;
import dev.nonamecrackers2.simpleclouds.common.packet.SimpleCloudsPacketHandlers;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.SendCloudTypesPacket;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.PacketDistributor;
import nonamecrackers2.crackerslib.common.command.ConfigCommandBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleCloudsEvents {
    private static final Logger LOGGER = LogManager.getLogger((String)"simpleclouds/SimpleCloudsEvents");

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        ConfigCommandBuilder.builder((CommandDispatcher)event.getDispatcher(), (String)"simpleclouds").addSpec(ModConfig.Type.SERVER, SimpleCloudsConfig.SERVER_SPEC).addSpec(ModConfig.Type.COMMON, SimpleCloudsConfig.COMMON_SPEC).register();
        CloudCommands.register((CommandDispatcher<CommandSourceStack>)event.getDispatcher(), "clouds", src -> src.m_6761_(2), CloudCommandSource.SERVER, CloudTypeDataManager.getServerInstance());
    }

    @SubscribeEvent
    public static void registerReloadListeners(AddReloadListenerEvent event) {
        CloudTypeDataManager manager = CloudTypeDataManager.getServerInstance();
        event.addListener((PreparableReloadListener)manager);
        CloudSpawningDataManager.optionalInitialize(manager);
        event.addListener((PreparableReloadListener)CloudSpawningDataManager.getInstance());
    }

    @SubscribeEvent
    public static void onDataSync(OnDatapackSyncEvent event) {
        PacketDistributor.PacketTarget target = event.getPlayer() != null ? PacketDistributor.PLAYER.with(() -> ((OnDatapackSyncEvent)event).getPlayer()) : PacketDistributor.ALL.noArg();
        SimpleCloudsPacketHandlers.MAIN.send(target, (Object)new SendCloudTypesPacket(CloudTypeDataManager.getServerInstance()));
    }

    @SubscribeEvent
    public static void allowSleepingDuringThunderClouds(SleepingTimeCheckEvent event) {
        Player player = event.getEntity();
        CloudManager<Level> manager = CloudManager.get(player.m_9236_());
        if (!manager.shouldUseVanillaWeather() && ((CloudType)manager.getCloudTypeAtWorldPos((float)player.m_20185_(), (float)player.m_20189_()).getLeft()).weatherType().includesThunder()) {
            event.setResult(Event.Result.ALLOW);
        }
    }

    @SubscribeEvent
    public static void removeStormsAfterSleeping(SleepFinishedTimeEvent event) {
        LevelAccessor levelAccessor = event.getLevel();
        if (levelAccessor instanceof ServerLevel) {
            ServerLevel level = (ServerLevel)levelAccessor;
            CloudManager<ServerLevel> manager = CloudManager.get(level);
            if (manager.shouldUseVanillaWeather()) {
                return;
            }
            CloudGenerator generator = manager.getCloudGenerator();
            for (Player player : level.m_6907_()) {
                CloudRegion region = generator.getCloudAtWorldPosition((float)player.m_20185_(), (float)player.m_20189_());
                if (region == null) continue;
                CloudType type = manager.getCloudTypeForId(region.getCloudTypeId());
                if (type == null) {
                    LOGGER.warn("Could not find cloud type with ID '{}' for cloud region; this should not happen!", (Object)region.getCloudTypeId());
                    continue;
                }
                if (!type.weatherType().includesThunder()) continue;
                generator.removeClouds(r -> r == region);
            }
        }
    }
}

