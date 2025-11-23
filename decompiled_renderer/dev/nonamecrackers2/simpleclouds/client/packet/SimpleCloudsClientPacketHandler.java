/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package dev.nonamecrackers2.simpleclouds.client.packet;

import dev.nonamecrackers2.simpleclouds.client.cloud.ClientSideCloudTypeManager;
import dev.nonamecrackers2.simpleclouds.client.config.SimpleCloudsClientConfigListeners;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.MultiRegionCloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.world.ClientCloudManager;
import dev.nonamecrackers2.simpleclouds.common.config.SimpleCloudsConfig;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.SendCloudManagerPacket;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.SendCloudRegionsPacket;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.SendCloudTypesPacket;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.SpawnLightningPacket;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.UpdateCloudManagerPacket;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.update.NotifyCloudModeUpdatedPacket;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.update.NotifySingleModeCloudTypeUpdatedPacket;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleCloudsClientPacketHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void handleUpdateCloudManagerPacket(UpdateCloudManagerPacket packet) {
        Minecraft mc = Minecraft.m_91087_();
        CloudManager<ClientLevel> manager = CloudManager.get(mc.f_91073_);
        SimpleCloudsClientPacketHandler.handleUpdateCloudManagerPacket(packet, manager);
    }

    public static void handleUpdateCloudManagerPacket(UpdateCloudManagerPacket packet, CloudManager<ClientLevel> manager) {
        manager.setScrollAngle(packet.scrollAngle);
        manager.setCloudSpeed(packet.speed);
        manager.setCloudHeight(packet.cloudHeight);
        if (manager instanceof ClientCloudManager) {
            ClientCloudManager clientManager = (ClientCloudManager)manager;
            clientManager.setReceivedSync();
        }
    }

    public static void handleSendCloudManagerPacket(SendCloudManagerPacket packet) {
        Minecraft mc = Minecraft.m_91087_();
        CloudManager<ClientLevel> manager = CloudManager.get(mc.f_91073_);
        SimpleCloudsClientPacketHandler.handleUpdateCloudManagerPacket(packet, manager);
        manager.setSeed(packet.seed);
        manager.getCloudGenerator().setClouds(packet.cloudRegions);
        SimpleCloudsRenderer renderer = SimpleCloudsRenderer.getInstance();
        if (SimpleCloudsConfig.SERVER_SPEC.isLoaded()) {
            if (renderer.needsReinitialization()) {
                LOGGER.debug("Looks like the server cloud mode or region generator does not match with the client. Requesting a reload...");
                renderer.requestReload();
            }
        } else {
            LOGGER.warn("Server spec is not loaded");
        }
        LOGGER.debug("Received cloud manager info");
    }

    public static void handleSendCloudRegionsPacket(SendCloudRegionsPacket packet) {
        Minecraft mc = Minecraft.m_91087_();
        CloudManager<ClientLevel> manager = CloudManager.get(mc.f_91073_);
        manager.getCloudGenerator().setClouds(packet.cloudRegions);
    }

    public static void handleCloudTypesPacket(SendCloudTypesPacket packet) {
        LOGGER.debug("Received {} synced cloud types", (Object)packet.types.size());
        ClientSideCloudTypeManager.getInstance().receiveSynced(packet.types, packet.indexed);
        CloudMeshGenerator cloudMeshGenerator = SimpleCloudsRenderer.getInstance().getMeshGenerator();
        if (cloudMeshGenerator instanceof MultiRegionCloudMeshGenerator) {
            MultiRegionCloudMeshGenerator meshGenerator = (MultiRegionCloudMeshGenerator)cloudMeshGenerator;
            if (packet.types.size() > 64) {
                LOGGER.warn("The amount of loaded cloud types exceeds the maximum of {}. Please be aware that not all cloud types loaded will be used.", (Object)64);
            } else {
                meshGenerator.updateCloudTypes();
            }
        }
    }

    public static void handleSpawnLightningPacket(SpawnLightningPacket packet) {
        SimpleCloudsRenderer.getInstance().getWorldEffectsManager().spawnLightning(packet.pos, packet.onlySound, packet.seed, packet.maxDepth, packet.branchCount, packet.maxBranchLength, packet.maxWidth, packet.minimumPitch, packet.maximumPitch);
    }

    public static void handleNotifyCloudModeUpdatedPacket(NotifyCloudModeUpdatedPacket packet) {
        SimpleCloudsClientConfigListeners.onCloudModeUpdatedFromServer(packet.newMode);
    }

    public static void handleNotifySingleModeCloudTypeUpdatedPacket(NotifySingleModeCloudTypeUpdatedPacket packet) {
        SimpleCloudsClientConfigListeners.onSingleModeCloudTypeUpdatedFromServer(packet.newType);
    }
}

