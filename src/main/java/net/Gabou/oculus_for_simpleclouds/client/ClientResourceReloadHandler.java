package net.Gabou.oculus_for_simpleclouds.client;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

public final class ClientResourceReloadHandler {
    private ClientResourceReloadHandler() {
    }

    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) manager ->
                Minecraft.getInstance().execute(FinalCloudCompositeHandler::resetAfterResourceReload));
    }
}
