/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraftforge.network.NetworkEvent$Context
 *  nonamecrackers2.crackerslib.common.packet.Packet
 */
package dev.nonamecrackers2.simpleclouds.common.packet.impl;

import dev.nonamecrackers2.simpleclouds.client.packet.SimpleCloudsClientPacketHandler;
import dev.nonamecrackers2.simpleclouds.common.cloud.region.CloudRegion;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import nonamecrackers2.crackerslib.common.packet.Packet;

public class SendCloudRegionsPacket
extends Packet {
    public List<CloudRegion> cloudRegions;

    public SendCloudRegionsPacket(List<CloudRegion> cloudRegions) {
        super(true);
        this.cloudRegions = cloudRegions;
    }

    public SendCloudRegionsPacket() {
        super(false);
    }

    protected void decode(FriendlyByteBuf buffer) {
        this.cloudRegions = buffer.m_236845_(CloudRegion::new);
    }

    protected void encode(FriendlyByteBuf buffer) {
        buffer.m_236828_(this.cloudRegions, (b, c) -> c.toPacket((FriendlyByteBuf)b));
    }

    public Runnable getProcessor(NetworkEvent.Context context) {
        return SendCloudRegionsPacket.client(() -> SimpleCloudsClientPacketHandler.handleSendCloudRegionsPacket(this));
    }
}

