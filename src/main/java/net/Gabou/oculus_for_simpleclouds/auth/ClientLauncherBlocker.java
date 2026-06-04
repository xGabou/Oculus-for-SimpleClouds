package net.Gabou.oculus_for_simpleclouds.auth;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public final class ClientLauncherBlocker {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static boolean listenerRegistered;
    private static boolean disconnectIssued;

    private ClientLauncherBlocker() {
    }

    public static void install() {
        if (listenerRegistered) {
            return;
        }
        listenerRegistered = true;
        MinecraftForge.EVENT_BUS.addListener(ClientLauncherBlocker::onClientTick);
        Minecraft.getInstance().execute(ClientLauncherBlocker::enforceIfNeeded);
    }

    private static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            enforceIfNeeded();
        }
    }

    private static void enforceIfNeeded() {
        String reason = ClientLauncherGuards.getDetectedReason();
        if (reason == null || reason.isBlank()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return;
        }

        if (!disconnectIssued) {
            disconnectIssued = true;
            LOGGER.error("Blocking client because a suspicious launcher was detected: {}", reason);
        }

        if (!(minecraft.screen instanceof LauncherBlockedScreen)) {
            minecraft.setScreen(new LauncherBlockedScreen(reason));
        }
    }
}
