/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonParser
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.network.NetworkEvent$Context
 *  nonamecrackers2.crackerslib.common.packet.Packet
 */
package dev.nonamecrackers2.simpleclouds.common.packet.impl;

import com.google.common.collect.Maps;
import com.google.gson.JsonParser;
import dev.nonamecrackers2.simpleclouds.client.packet.SimpleCloudsClientPacketHandler;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudTypeDataManager;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import nonamecrackers2.crackerslib.common.packet.Packet;

public class SendCloudTypesPacket
extends Packet {
    public Map<ResourceLocation, CloudType> types;
    public CloudType[] indexed;

    public SendCloudTypesPacket(CloudTypeDataManager manager) {
        super(true);
        this.types = manager.getCloudTypes();
        this.indexed = manager.getIndexedCloudTypes();
    }

    public SendCloudTypesPacket() {
        super(false);
    }

    protected void decode(FriendlyByteBuf buffer) {
        int count = buffer.m_130242_();
        HashMap map = Maps.newHashMap();
        CloudType[] indexed = new CloudType[count];
        for (int i = 0; i < count; ++i) {
            ResourceLocation id = buffer.m_130281_();
            CloudType type = CloudType.readFromJson(id, JsonParser.parseString((String)buffer.m_130277_()).getAsJsonObject());
            map.put(id, type);
            indexed[i] = type;
        }
        this.types = map;
        this.indexed = indexed;
    }

    protected void encode(FriendlyByteBuf buffer) {
        buffer.m_130130_(this.indexed.length);
        for (CloudType type : this.indexed) {
            buffer.m_130085_(type.id());
            buffer.m_130070_(type.toJson().toString());
        }
    }

    public Runnable getProcessor(NetworkEvent.Context context) {
        return SendCloudTypesPacket.client(() -> SimpleCloudsClientPacketHandler.handleCloudTypesPacket(this));
    }
}

