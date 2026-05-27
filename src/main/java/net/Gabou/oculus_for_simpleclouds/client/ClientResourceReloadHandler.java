package net.Gabou.oculus_for_simpleclouds.client;

import net.Gabou.oculus_for_simpleclouds.Oculus_for_simpleclouds;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Oculus_for_simpleclouds.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientResourceReloadHandler {
    private ClientResourceReloadHandler() {
    }

    @SubscribeEvent
    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) manager ->
                Minecraft.getInstance().execute(FinalCloudCompositeHandler::resetAfterResourceReload));
    }
}
