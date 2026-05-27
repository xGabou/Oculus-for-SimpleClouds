package net.Gabou.oculus_for_simpleclouds.auth;

import net.minecraft.network.FriendlyByteBuf;

public record C2SChallengeReplyPacket(long nonce, String response, String launcherReason) {
    public static void encode(C2SChallengeReplyPacket packet, FriendlyByteBuf buffer) {
        buffer.writeLong(packet.nonce());
        buffer.writeUtf(packet.response() == null ? "" : packet.response());
        buffer.writeUtf(packet.launcherReason() == null ? "" : packet.launcherReason());
    }

    public static C2SChallengeReplyPacket decode(FriendlyByteBuf buffer) {
        return new C2SChallengeReplyPacket(
            buffer.readLong(),
            buffer.readUtf(),
            buffer.readUtf()
        );
    }
}
