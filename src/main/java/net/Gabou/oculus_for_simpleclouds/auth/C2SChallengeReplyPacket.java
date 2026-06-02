package net.Gabou.oculus_for_simpleclouds.auth;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record C2SChallengeReplyPacket(
        long nonce,
        String response,
        String launcherReason
) implements CustomPacketPayload {

    public static final Type<C2SChallengeReplyPacket> TYPE =
            new Type<>(AuthNetwork.id("c2s_challenge_reply"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SChallengeReplyPacket> STREAM_CODEC =
            StreamCodec.of(
                    C2SChallengeReplyPacket::encode,
                    C2SChallengeReplyPacket::decode
            );

    public static void encode(RegistryFriendlyByteBuf buffer, C2SChallengeReplyPacket packet) {
        buffer.writeLong(packet.nonce());
        buffer.writeUtf(packet.response() == null ? "" : packet.response());
        buffer.writeUtf(packet.launcherReason() == null ? "" : packet.launcherReason());
    }

    public static C2SChallengeReplyPacket decode(RegistryFriendlyByteBuf buffer) {
        return new C2SChallengeReplyPacket(
                buffer.readLong(),
                buffer.readUtf(),
                buffer.readUtf()
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}