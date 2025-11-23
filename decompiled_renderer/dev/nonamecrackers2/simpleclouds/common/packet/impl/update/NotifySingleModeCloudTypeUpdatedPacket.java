/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraftforge.network.NetworkEvent$Context
 *  nonamecrackers2.crackerslib.common.packet.Packet
 */
package dev.nonamecrackers2.simpleclouds.common.packet.impl.update;

import dev.nonamecrackers2.simpleclouds.client.packet.SimpleCloudsClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import nonamecrackers2.crackerslib.common.packet.Packet;

public class NotifySingleModeCloudTypeUpdatedPacket
extends Packet {
    public String newType;

    public NotifySingleModeCloudTypeUpdatedPacket(String type) {
        super(true);
        this.newType = type;
    }

    public NotifySingleModeCloudTypeUpdatedPacket() {
        super(false);
    }

    protected void decode(FriendlyByteBuf buffer) {
        this.newType = buffer.m_130277_();
    }

    protected void encode(FriendlyByteBuf buffer) {
        buffer.m_130070_(this.newType);
    }

    public Runnable getProcessor(NetworkEvent.Context context) {
        return NotifySingleModeCloudTypeUpdatedPacket.client(() -> SimpleCloudsClientPacketHandler.handleNotifySingleModeCloudTypeUpdatedPacket(this));
    }
}

