/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraftforge.network.NetworkEvent$Context
 *  nonamecrackers2.crackerslib.common.packet.Packet
 */
package dev.nonamecrackers2.simpleclouds.common.packet.impl;

import dev.nonamecrackers2.simpleclouds.client.packet.SimpleCloudsClientPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import nonamecrackers2.crackerslib.common.packet.Packet;

public class SpawnLightningPacket
extends Packet {
    public BlockPos pos;
    public boolean onlySound;
    public int seed;
    public int maxDepth;
    public int branchCount;
    public float maxBranchLength;
    public float maxWidth;
    public float minimumPitch;
    public float maximumPitch;

    public SpawnLightningPacket(BlockPos pos, boolean onlySound, int seed, int maxDepth, int branchCount, float maxBranchLength, float maxWidth, float minimumPitch, float maximumPitch) {
        super(true);
        this.pos = pos;
        this.onlySound = onlySound;
        this.seed = seed;
        this.maxDepth = maxDepth;
        this.branchCount = branchCount;
        this.maxBranchLength = maxBranchLength;
        this.maxWidth = maxWidth;
        this.minimumPitch = minimumPitch;
        this.maximumPitch = maximumPitch;
    }

    public SpawnLightningPacket() {
        super(false);
    }

    protected void decode(FriendlyByteBuf buffer) {
        this.pos = buffer.m_130135_();
        this.onlySound = buffer.readBoolean();
        this.seed = buffer.m_130242_();
        this.maxDepth = buffer.m_130242_();
        this.branchCount = buffer.m_130242_();
        this.maxBranchLength = buffer.readFloat();
        this.maxWidth = buffer.readFloat();
        this.minimumPitch = buffer.readFloat();
        this.maximumPitch = buffer.readFloat();
    }

    protected void encode(FriendlyByteBuf buffer) {
        buffer.m_130064_(this.pos);
        buffer.writeBoolean(this.onlySound);
        buffer.m_130130_(this.seed);
        buffer.m_130130_(this.maxDepth);
        buffer.m_130130_(this.branchCount);
        buffer.writeFloat(this.maxBranchLength);
        buffer.writeFloat(this.maxWidth);
        buffer.writeFloat(this.minimumPitch);
        buffer.writeFloat(this.maximumPitch);
    }

    public Runnable getProcessor(NetworkEvent.Context context) {
        return SpawnLightningPacket.client(() -> SimpleCloudsClientPacketHandler.handleSpawnLightningPacket(this));
    }
}

