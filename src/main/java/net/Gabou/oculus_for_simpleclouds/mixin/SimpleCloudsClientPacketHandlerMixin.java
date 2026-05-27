package net.Gabou.oculus_for_simpleclouds.mixin;

import dev.nonamecrackers2.simpleclouds.client.packet.SimpleCloudsClientPacketHandler;
import dev.nonamecrackers2.simpleclouds.client.world.ClientCloudManager;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.SendCloudManagerPacket;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.SendCloudRegionsPacket;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.SendCloudTypesPacket;
import dev.nonamecrackers2.simpleclouds.common.packet.impl.UpdateCloudManagerPacket;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SimpleCloudsClientPacketHandler.class, remap = false)
public abstract class SimpleCloudsClientPacketHandlerMixin {

    @Inject(
            method = "handleUpdateCloudManagerPacket(Ldev/nonamecrackers2/simpleclouds/common/packet/impl/UpdateCloudManagerPacket;Ldev/nonamecrackers2/simpleclouds/common/world/CloudManager;)V",
            at = @At("RETURN")
    )
    private static void oculus_for_simpleclouds$doNotEnableServerSyncForMovementPackets(
            UpdateCloudManagerPacket packet,
            CloudManager<ClientLevel> manager,
            CallbackInfo ci
    ) {
        if (manager instanceof ClientCloudManager clientManager && !(packet instanceof SendCloudManagerPacket)) {
            ((ClientCloudManagerAccessor) clientManager).oculus_for_simpleclouds$setReceivedSync(false);
        }
    }

    @Inject(
            method = "handleSendCloudManagerPacket(Ldev/nonamecrackers2/simpleclouds/common/packet/impl/SendCloudManagerPacket;)V",
            at = @At("TAIL")
    )
    private static void oculus_for_simpleclouds$logCloudSnapshot(SendCloudManagerPacket packet, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        CloudManager<ClientLevel> manager = CloudManager.get(mc.level);
        int regionCount = packet.cloudRegions == null ? -1 : packet.cloudRegions.size();
        int meshRegionCount = manager.getCloudGenerator() == null ? -1 : manager.getCloudGenerator().getTotalCloudRegions();
        boolean receivedSync = manager instanceof ClientCloudManager clientManager && ((ClientCloudManagerAccessor) clientManager).oculus_for_simpleclouds$hasReceivedSync();
        System.out.println("[OFSC DEBUG] Cloud snapshot received: seed=" + packet.seed
                + " packetRegions=" + regionCount
                + " managerRegions=" + meshRegionCount
                + " sync=" + receivedSync);
    }

    @Inject(
            method = "handleSendCloudRegionsPacket(Ldev/nonamecrackers2/simpleclouds/common/packet/impl/SendCloudRegionsPacket;)V",
            at = @At("TAIL")
    )
    private static void oculus_for_simpleclouds$logCloudRegionUpdate(SendCloudRegionsPacket packet, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        int regionCount = packet.cloudRegions == null ? -1 : packet.cloudRegions.size();
        int managerRegionCount = -1;
        boolean receivedSync = false;
        if (mc.level != null) {
            CloudManager<ClientLevel> manager = CloudManager.get(mc.level);
            managerRegionCount = manager.getCloudGenerator() == null ? -1 : manager.getCloudGenerator().getTotalCloudRegions();
            receivedSync = manager instanceof ClientCloudManager clientManager && ((ClientCloudManagerAccessor) clientManager).oculus_for_simpleclouds$hasReceivedSync();
        }
        System.out.println("[OFSC DEBUG] Cloud region update received: regions=" + regionCount
                + " managerRegions=" + managerRegionCount
                + " sync=" + receivedSync);
    }

    @Inject(
            method = "handleCloudTypesPacket(Ldev/nonamecrackers2/simpleclouds/common/packet/impl/SendCloudTypesPacket;)V",
            at = @At("TAIL")
    )
    private static void oculus_for_simpleclouds$logCloudTypeUpdate(SendCloudTypesPacket packet, CallbackInfo ci) {
        System.out.println("[OFSC DEBUG] Cloud types update received: types=" + (packet.types == null ? -1 : packet.types.size()));
    }
}
