package net.Gabou.oculus_for_simpleclouds.auth;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientAuth {
    private ClientAuth() {
    }

    public static void handleChallenge(S2CChallengePacket packet) {
        if (packet == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        AuthNetwork.sendChallengeReply(new C2SChallengeReplyPacket(
                packet.nonce(),
                SharedSecret.computeResponse(minecraft.player.getUUID(), packet.nonce()),
                ClientLauncherGuards.getDetectedReason()
        ));
    }
}