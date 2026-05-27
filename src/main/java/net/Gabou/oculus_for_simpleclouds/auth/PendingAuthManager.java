package net.Gabou.oculus_for_simpleclouds.auth;

import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PendingAuthManager {
    public record PendingAuth(long nonce, long issuedAtMs) {
    }

    private static final Map<UUID, PendingAuth> PENDING = new HashMap<>();

    private PendingAuthManager() {
    }

    public static PendingAuth begin(ServerPlayer player) {
        PendingAuth pending = new PendingAuth(SharedSecret.createNonce(), System.currentTimeMillis());
        if (player != null) {
            PENDING.put(player.getUUID(), pending);
        }
        return pending;
    }

    public static PendingAuth get(UUID uuid) {
        return uuid == null ? null : PENDING.get(uuid);
    }

    public static void clear(UUID uuid) {
        if (uuid != null) {
            PENDING.remove(uuid);
        }
    }

    public static void clear(ServerPlayer player) {
        if (player != null) {
            clear(player.getUUID());
        }
    }

    public static Map<UUID, PendingAuth> snapshot() {
        return new HashMap<>(PENDING);
    }

    public static long getTimeoutMs() {
        return Math.max(1, ServerAuth.AUTH_CHALLENGE_TIMEOUT_TICKS) * 50L;
    }
}
