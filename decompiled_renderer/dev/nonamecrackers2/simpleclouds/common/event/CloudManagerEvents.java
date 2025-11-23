/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.event.TickEvent$LevelTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerChangedDimensionEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerRespawnEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.network.PacketDistributor
 */
package dev.nonamecrackers2.simpleclouds.common.event;

import dev.nonamecrackers2.simpleclouds.common.cloud.SimpleCloudsConstants;
import dev.nonamecrackers2.simpleclouds.common.cloud.region.CloudRegion;
import dev.nonamecrackers2.simpleclouds.common.packet.SimpleCloudsPacketHandlers;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.SendCloudManagerPacket;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.SendCloudRegionsPacket;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.UpdateCloudManagerPacket;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import dev.nonamecrackers2.simpleclouds.common.world.ServerCloudManager;
import dev.nonamecrackers2.simpleclouds.common.world.SpawnRegion;
import dev.nonamecrackers2.simpleclouds.common.world.SyncType;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

public class CloudManagerEvents {
    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        block7: {
            ServerCloudManager serverManager;
            CloudManager<Level> manager;
            Level level;
            block8: {
                level = event.level;
                if (event.phase != TickEvent.Phase.START) break block7;
                manager = CloudManager.get(level);
                manager.tick();
                if (level.f_46443_ || !(manager instanceof ServerCloudManager)) break block7;
                serverManager = (ServerCloudManager)manager;
                SyncType syncType = serverManager.fetchNextSyncOperation();
                if (syncType == null) break block8;
                switch (syncType) {
                    case BASE_PROPERTIES: {
                        SimpleCloudsPacketHandlers.MAIN.send(PacketDistributor.DIMENSION.with(() -> ((Level)level).m_46472_()), (Object)new SendCloudManagerPacket(serverManager));
                        break block7;
                    }
                    case MOVEMENT: {
                        SimpleCloudsPacketHandlers.MAIN.send(PacketDistributor.DIMENSION.with(() -> ((Level)level).m_46472_()), (Object)new UpdateCloudManagerPacket(serverManager));
                        break block7;
                    }
                    case CLOUD_FORMATIONS: {
                        for (ServerPlayer player : ((ServerLevel)level).m_6907_()) {
                            CloudManagerEvents.sendCloudRegionsToPlayer(player);
                        }
                        break block7;
                    }
                    default: {
                        throw new IllegalArgumentException("Unexpected value: " + syncType);
                    }
                }
            }
            if (manager.getTickCount() % 200 == 0) {
                SimpleCloudsPacketHandlers.MAIN.send(PacketDistributor.DIMENSION.with(() -> ((Level)level).m_46472_()), (Object)new UpdateCloudManagerPacket(serverManager));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        CloudManager.get(event.getEntity().m_9236_()).onPlayerJoin(event.getEntity());
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            CloudManagerEvents.update(player2);
        }
    }

    @SubscribeEvent
    public static void onPlayerSwapDimensions(PlayerEvent.PlayerChangedDimensionEvent event) {
        CloudManager.get(event.getEntity().m_9236_()).onPlayerJoin(event.getEntity());
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            CloudManagerEvents.update(player2);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        CloudManager.get(event.getEntity().m_9236_()).onPlayerJoin(event.getEntity());
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            CloudManagerEvents.update(player2);
        }
    }

    private static void update(ServerPlayer player) {
        SimpleCloudsPacketHandlers.MAIN.send(PacketDistributor.PLAYER.with(() -> player), (Object)new SendCloudManagerPacket(CloudManager.get((ServerLevel)player.m_9236_())));
        CloudManagerEvents.sendCloudRegionsToPlayer(player);
    }

    private static void sendCloudRegionsToPlayer(ServerPlayer player) {
        CloudManager<ServerLevel> manager = CloudManager.get(player.m_284548_());
        SpawnRegion region = new SpawnRegion(player.m_146903_(), player.m_146907_(), SimpleCloudsConstants.SPAWN_RADIUS);
        List<CloudRegion> formationsForPlayer = manager.getCloudGenerator().getCloudsInRegion(region);
        SimpleCloudsPacketHandlers.MAIN.send(PacketDistributor.PLAYER.with(() -> player), (Object)new SendCloudRegionsPacket(formationsForPlayer));
    }
}

