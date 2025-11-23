/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.CloudMode
 *  net.minecraft.server.MinecraftServer
 *  net.minecraftforge.fml.config.ModConfig$Type
 *  net.minecraftforge.network.PacketDistributor
 *  net.minecraftforge.server.ServerLifecycleHooks
 *  nonamecrackers2.crackerslib.common.config.listener.ConfigListener
 */
package dev.nonamecrackers2.simpleclouds.common.config;

import dev.nonamecrackers2.simpleclouds.api.common.cloud.CloudMode;
import dev.nonamecrackers2.simpleclouds.common.config.SimpleCloudsConfig;
import dev.nonamecrackers2.simpleclouds.common.packet.SimpleCloudsPacketHandlers;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.update.NotifyCloudModeUpdatedPacket;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.update.NotifySingleModeCloudTypeUpdatedPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;
import nonamecrackers2.crackerslib.common.config.listener.ConfigListener;

public class SimpleCloudsConfigListeners {
    public static void registerListener() {
        ConfigListener.builder((ModConfig.Type)ModConfig.Type.SERVER, (String)"simpleclouds").addListener(SimpleCloudsConfig.SERVER.cloudMode, (o, n) -> SimpleCloudsConfigListeners.onCloudModeChanged(n)).addListener(SimpleCloudsConfig.SERVER.singleModeCloudType, (o, n) -> SimpleCloudsConfigListeners.onSingleModeCloudTypeChanged(n)).buildAndRegister();
    }

    public static void onCloudModeChanged(CloudMode newMode) {
        SimpleCloudsConfigListeners.executeOnServerThread(() -> SimpleCloudsPacketHandlers.MAIN.send(PacketDistributor.ALL.noArg(), (Object)new NotifyCloudModeUpdatedPacket(newMode)));
    }

    public static void onSingleModeCloudTypeChanged(String newType) {
        SimpleCloudsConfigListeners.executeOnServerThread(() -> SimpleCloudsPacketHandlers.MAIN.send(PacketDistributor.ALL.noArg(), (Object)new NotifySingleModeCloudTypeUpdatedPacket(newType)));
    }

    private static void executeOnServerThread(Runnable runnable) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            server.execute(runnable);
        }
    }
}

