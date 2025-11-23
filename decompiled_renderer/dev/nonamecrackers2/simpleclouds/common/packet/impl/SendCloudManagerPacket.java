/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package dev.nonamecrackers2.simpleclouds.common.packet.impl;

import dev.nonamecrackers2.simpleclouds.client.packet.SimpleCloudsClientPacketHandler;
import dev.nonamecrackers2.simpleclouds.common.cloud.region.CloudRegion;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.UpdateCloudManagerPacket;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

public class SendCloudManagerPacket
extends UpdateCloudManagerPacket {
    public List<CloudRegion> cloudRegions;
    public long seed;

    public SendCloudManagerPacket(CloudManager<ServerLevel> manager) {
        super(manager);
        this.cloudRegions = manager.getClouds();
        this.seed = manager.getSeed();
    }

    public SendCloudManagerPacket() {
    }

    @Override
    protected void decode(FriendlyByteBuf buffer) {
        super.decode(buffer);
        this.cloudRegions = buffer.m_236845_(CloudRegion::new);
        this.seed = buffer.readLong();
    }

    @Override
    protected void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.m_236828_(this.cloudRegions, (b, c) -> c.toPacket((FriendlyByteBuf)b));
        buffer.writeLong(this.seed);
    }

    @Override
    public Runnable getProcessor(NetworkEvent.Context context) {
        return SendCloudManagerPacket.client(() -> SimpleCloudsClientPacketHandler.handleSendCloudManagerPacket(this));
    }
}

