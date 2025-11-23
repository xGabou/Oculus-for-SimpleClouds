/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.CloudMode
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraftforge.network.NetworkEvent$Context
 *  nonamecrackers2.crackerslib.common.packet.Packet
 */
package dev.nonamecrackers2.simpleclouds.common.packet.impl.update;

import dev.nonamecrackers2.simpleclouds.api.common.cloud.CloudMode;
import dev.nonamecrackers2.simpleclouds.client.packet.SimpleCloudsClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import nonamecrackers2.crackerslib.common.packet.Packet;

public class NotifyCloudModeUpdatedPacket
extends Packet {
    public CloudMode newMode;

    public NotifyCloudModeUpdatedPacket(CloudMode mode) {
        super(true);
        this.newMode = mode;
    }

    public NotifyCloudModeUpdatedPacket() {
        super(false);
    }

    protected void decode(FriendlyByteBuf buffer) {
        this.newMode = (CloudMode)buffer.m_130066_(CloudMode.class);
    }

    protected void encode(FriendlyByteBuf buffer) {
        buffer.m_130068_((Enum)this.newMode);
    }

    public Runnable getProcessor(NetworkEvent.Context context) {
        return NotifyCloudModeUpdatedPacket.client(() -> SimpleCloudsClientPacketHandler.handleNotifyCloudModeUpdatedPacket(this));
    }
}

