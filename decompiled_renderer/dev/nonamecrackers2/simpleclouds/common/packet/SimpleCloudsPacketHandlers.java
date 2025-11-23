/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.network.NetworkRegistry
 *  net.minecraftforge.network.simple.SimpleChannel
 *  nonamecrackers2.crackerslib.common.packet.PacketUtil
 */
package dev.nonamecrackers2.simpleclouds.common.packet;

import dev.nonamecrackers2.simpleclouds.SimpleCloudsMod;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.SendCloudManagerPacket;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.SendCloudRegionsPacket;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.SendCloudTypesPacket;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.SpawnLightningPacket;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.UpdateCloudManagerPacket;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.update.NotifyCloudModeUpdatedPacket;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.update.NotifySingleModeCloudTypeUpdatedPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import nonamecrackers2.crackerslib.common.packet.PacketUtil;

public class SimpleCloudsPacketHandlers {
    public static final String VERSION = "1.1";
    public static final SimpleChannel MAIN = NetworkRegistry.newSimpleChannel((ResourceLocation)SimpleCloudsMod.id("main"), () -> "1.1", v -> true, "1.1"::equals);

    public static void register() {
        PacketUtil.registerToClient((SimpleChannel)MAIN, UpdateCloudManagerPacket.class);
        PacketUtil.registerToClient((SimpleChannel)MAIN, SendCloudManagerPacket.class);
        PacketUtil.registerToClient((SimpleChannel)MAIN, SendCloudRegionsPacket.class);
        PacketUtil.registerToClient((SimpleChannel)MAIN, SendCloudTypesPacket.class);
        PacketUtil.registerToClient((SimpleChannel)MAIN, SpawnLightningPacket.class);
        PacketUtil.registerToClient((SimpleChannel)MAIN, NotifyCloudModeUpdatedPacket.class);
        PacketUtil.registerToClient((SimpleChannel)MAIN, NotifySingleModeCloudTypeUpdatedPacket.class);
    }
}

