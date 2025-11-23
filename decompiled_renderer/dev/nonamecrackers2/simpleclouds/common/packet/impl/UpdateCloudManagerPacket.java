/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraftforge.network.NetworkEvent$Context
 *  nonamecrackers2.crackerslib.common.packet.Packet
 */
package dev.nonamecrackers2.simpleclouds.common.packet.impl;

import dev.nonamecrackers2.simpleclouds.client.packet.SimpleCloudsClientPacketHandler;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;
import nonamecrackers2.crackerslib.common.packet.Packet;

public class UpdateCloudManagerPacket
extends Packet {
    public float speed;
    public float scrollAngle;
    public int cloudHeight;

    public UpdateCloudManagerPacket(CloudManager<ServerLevel> manager) {
        super(true);
        this.speed = manager.getCloudSpeed();
        this.scrollAngle = manager.getScrollAngle();
        this.cloudHeight = manager.getCloudHeight();
    }

    public UpdateCloudManagerPacket() {
        super(false);
    }

    protected void decode(FriendlyByteBuf buffer) {
        this.speed = buffer.readFloat();
        this.scrollAngle = buffer.readFloat();
        this.cloudHeight = buffer.m_130242_();
    }

    protected void encode(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.speed);
        buffer.writeFloat(this.scrollAngle);
        buffer.m_130130_(this.cloudHeight);
    }

    public Runnable getProcessor(NetworkEvent.Context context) {
        return UpdateCloudManagerPacket.client(() -> SimpleCloudsClientPacketHandler.handleUpdateCloudManagerPacket(this));
    }
}

