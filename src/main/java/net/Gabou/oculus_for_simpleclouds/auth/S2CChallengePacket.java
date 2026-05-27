package net.Gabou.oculus_for_simpleclouds.auth;

import net.minecraft.network.FriendlyByteBuf;

public record S2CChallengePacket(long nonce) {
    public static void encode(S2CChallengePacket packet, FriendlyByteBuf buffer) {
        buffer.writeLong(packet.nonce());
    }

    public static S2CChallengePacket decode(FriendlyByteBuf buffer) {
        return new S2CChallengePacket(buffer.readLong());
    }
}
