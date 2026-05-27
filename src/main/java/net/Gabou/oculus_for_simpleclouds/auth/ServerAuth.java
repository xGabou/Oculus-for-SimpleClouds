package net.Gabou.oculus_for_simpleclouds.auth;

import net.Gabou.oculus_for_simpleclouds.Oculus_for_simpleclouds;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.Map;
import java.util.UUID;

public final class ServerAuth {
    public static final boolean AUTH_STRICT_OFFLINE_UUID_REJECT = true;
    public static final boolean AUTH_KICK_ON_FAILURE = true;
    public static final int AUTH_CHALLENGE_TIMEOUT_TICKS = 200;

    private ServerAuth() {
    }

    public static boolean onLogin(Connection connection, ServerPlayer player) {
        if (player == null) {
            return true;
        }

        PendingAuthManager.clear(player);

        if (!FMLLoader.isProduction()) {
            Oculus_for_simpleclouds.LOGGER.info(
                "Skipping launcher/auth checks for {} because the game is running in a development environment.",
                player.getGameProfile().getName()
            );
            return true;
        }

        if (player.getUUID() != null && player.getUUID().version() == 3 && AUTH_STRICT_OFFLINE_UUID_REJECT) {
            Oculus_for_simpleclouds.LOGGER.warn(
                "Rejected {} because strict auth mode disallows offline UUID v3 identities.",
                player.getName().getString()
            );
            disconnect(connection, player, Component.literal("Authentication rejected."));
            return false;
        }

        PendingAuthManager.begin(player);
        return true;
    }

    public static void sendChallenge(ServerPlayer player) {
        if (player == null) {
            return;
        }

        PendingAuthManager.PendingAuth pending = PendingAuthManager.get(player.getUUID());
        if (pending == null) {
            return;
        }

        AuthNetwork.sendChallenge(player, new S2CChallengePacket(pending.nonce()));
    }

    public static void onTick(MinecraftServer server) {
        if (server == null) {
            return;
        }

        long now = System.currentTimeMillis();
        long timeoutMs = PendingAuthManager.getTimeoutMs();
        for (Map.Entry<UUID, PendingAuthManager.PendingAuth> entry : PendingAuthManager.snapshot().entrySet()) {
            PendingAuthManager.PendingAuth pending = entry.getValue();
            if (pending == null || now - pending.issuedAtMs() < timeoutMs) {
                continue;
            }

            UUID uuid = entry.getKey();
            PendingAuthManager.clear(uuid);

            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player == null) {
                continue;
            }

            Oculus_for_simpleclouds.LOGGER.warn("Auth challenge timed out for {}", player.getName().getString());
            if (AUTH_KICK_ON_FAILURE) {
                player.connection.disconnect(Component.literal("Authentication timed out."));
            }
        }
    }

    public static void onLogout(ServerPlayer player) {
        if (player == null) {
            return;
        }

        PendingAuthManager.clear(player);
    }

    public static void handleChallengeReply(ServerPlayer player, C2SChallengeReplyPacket packet) {
        if (player == null || packet == null) {
            return;
        }

        String launcherReason = packet.launcherReason();
        if (launcherReason != null && !launcherReason.isBlank() && player.level() instanceof ServerLevel serverLevel) {
            TLauncherDetectedHandler.handle(serverLevel, player, launcherReason);
            PendingAuthManager.clear(player);
            return;
        }

        PendingAuthManager.PendingAuth pending = PendingAuthManager.get(player.getUUID());
        if (pending == null) {
            markInvalid(player, "unexpected auth reply");
            return;
        }
        if (pending.nonce() != packet.nonce()) {
            markInvalid(player, "nonce mismatch");
            return;
        }
        if (!SharedSecret.verifyResponse(player.getUUID(), packet.nonce(), packet.response())) {
            markInvalid(player, "invalid auth response");
            return;
        }

        PendingAuthManager.clear(player);
        Oculus_for_simpleclouds.LOGGER.info("Auth challenge completed for {}", player.getName().getString());
    }

    private static void markInvalid(ServerPlayer player, String reason) {
        PendingAuthManager.clear(player);
        Oculus_for_simpleclouds.LOGGER.warn(
            "Marked {} as failed auth because verification failed: {}",
            player.getName().getString(),
            reason
        );
        if (AUTH_KICK_ON_FAILURE) {
            player.connection.disconnect(Component.literal("Authentication failed."));
        }
    }

    private static void disconnect(Connection connection, ServerPlayer player, Component reason) {
        if (connection != null) {
            connection.disconnect(reason);
        } else if (player.connection != null) {
            player.connection.disconnect(reason);
        }
    }
}
