package net.Gabou.oculus_for_simpleclouds.auth;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public final class ClientLauncherBlocker {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static boolean listenerRegistered;
    private static boolean blockLogged;

    private ClientLauncherBlocker() {
    }

    public static void install() {
        if (listenerRegistered) {
            return;
        }
        listenerRegistered = true;
        NeoForge.EVENT_BUS.addListener(ClientLauncherBlocker::onClientTick);
        Minecraft.getInstance().execute(ClientLauncherBlocker::enforceIfNeeded);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        enforceIfNeeded();
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

        if (!blockLogged) {
            blockLogged = true;
            LOGGER.error("Blocking client because a suspicious launcher was detected: {}", reason);
        }

        if (!(minecraft.screen instanceof LauncherBlockedScreen)) {
            minecraft.setScreen(new LauncherBlockedScreen(reason));
        }
    }
}
