/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  dev.nonamecrackers2.simpleclouds.api.SimpleCloudsAPI
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.CloudMode
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundGameEventPacket
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.players.PlayerList
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.network.PacketDistributor
 *  org.apache.commons.lang3.tuple.Pair
 */
package dev.nonamecrackers2.simpleclouds.common.world;

import com.google.common.collect.Queues;
import dev.nonamecrackers2.simpleclouds.api.SimpleCloudsAPI;
import dev.nonamecrackers2.simpleclouds.api.common.cloud.CloudMode;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudTypeDataManager;
import dev.nonamecrackers2.simpleclouds.common.cloud.spawning.CloudSpawningDataManager;
import dev.nonamecrackers2.simpleclouds.common.cloud.spawning.ServerCloudGenerator;
import dev.nonamecrackers2.simpleclouds.common.config.SimpleCloudsConfig;
import dev.nonamecrackers2.simpleclouds.common.packet.SimpleCloudsPacketHandlers;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.SpawnLightningPacket;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import dev.nonamecrackers2.simpleclouds.common.world.SpawnRegion;
import dev.nonamecrackers2.simpleclouds.common.world.SyncType;
import dev.nonamecrackers2.simpleclouds.mixin.MixinServerLevelAccessor;
import java.util.List;
import java.util.Queue;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.tuple.Pair;

public class ServerCloudManager
extends CloudManager<ServerLevel> {
    private Queue<SyncType> toSync = Queues.newArrayDeque();

    public ServerCloudManager(ServerLevel level) {
        super(level, CloudTypeDataManager.getServerInstance(), CloudSpawningDataManager.getInstance()::getConfig, ServerCloudGenerator::new);
    }

    @Override
    public ServerCloudGenerator getCloudGenerator() {
        return (ServerCloudGenerator)super.getCloudGenerator();
    }

    @Override
    public CloudMode getCloudMode() {
        return (CloudMode)SimpleCloudsConfig.SERVER.cloudMode.get();
    }

    @Override
    public String getSingleModeCloudTypeRawId() {
        return (String)SimpleCloudsConfig.SERVER.singleModeCloudType.get();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.useVanillaWeather && !SimpleCloudsAPI.getApi().getHooks().isExternalWeatherControlEnabled()) {
            ((ServerLevel)this.level).m_46734_(0.0f);
        }
        if (this.isCloudGeneratorActive() && this.getCloudGenerator().checkAndResetSync()) {
            this.queueSync(SyncType.CLOUD_FORMATIONS);
        }
    }

    @Override
    protected void resetVanillaWeather() {
        ((MixinServerLevelAccessor)this.level).simpleclouds$invokeResetWeatherCycle();
        PlayerList list = ((ServerLevel)this.level).m_7654_().m_6846_();
        list.m_11270_((Packet)new ClientboundGameEventPacket(ClientboundGameEventPacket.f_132160_, 0.0f), ((ServerLevel)this.level).m_46472_());
        list.m_11270_((Packet)new ClientboundGameEventPacket(ClientboundGameEventPacket.f_132161_, 0.0f), ((ServerLevel)this.level).m_46472_());
    }

    @Override
    protected void attemptToSpawnLightning() {
        List<SpawnRegion> regions = ServerCloudManager.regionsFromEntities(((ServerLevel)this.level).m_6907_(), 10000);
        SpawnRegion.randomPointForEachRegion(regions, this.random, 12, (r, p) -> {
            Pair<CloudType, Float> info = this.getCloudTypeAtWorldPos((float)p.x + 0.5f, (float)p.y + 0.5f);
            CloudType type = (CloudType)info.getLeft();
            if (!ServerCloudManager.isValidLightning(type, ((Float)info.getRight()).floatValue(), this.random)) {
                return false;
            }
            this.spawnLightning(type, ((Float)info.getRight()).floatValue(), p.x, p.y, this.random.m_188503_(3) == 0);
            return true;
        });
    }

    @Override
    protected void spawnLightning(CloudType type, float fade, int x, int z, boolean soundOnly) {
        int y = (int)(type.stormStart() * 8.0f + (float)this.getCloudHeight());
        float spreadnessFactor = this.random.m_188501_();
        float length = spreadnessFactor * 300.0f + 200.0f;
        float minPitch = 20.0f + spreadnessFactor * 40.0f;
        float maxPitch = 80.0f + spreadnessFactor * 10.0f;
        SimpleCloudsPacketHandlers.MAIN.send(PacketDistributor.DIMENSION.with(() -> ((ServerLevel)this.level).m_46472_()), (Object)new SpawnLightningPacket(new BlockPos(x, y, z), soundOnly, this.random.m_188502_(), 4, 2, length, 20.0f, minPitch, maxPitch));
    }

    public void queueSync(SyncType syncType) {
        if (!this.toSync.contains((Object)syncType)) {
            this.toSync.add(syncType);
        }
    }

    @Nullable
    public SyncType fetchNextSyncOperation() {
        return this.toSync.poll();
    }

    public static List<SpawnRegion> regionsFromEntities(List<? extends Entity> entities, int radius) {
        return entities.stream().map(e -> new SpawnRegion(e.m_146903_(), e.m_146907_(), radius)).toList();
    }
}

