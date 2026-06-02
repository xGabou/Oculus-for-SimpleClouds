package net.Gabou.oculus_for_simpleclouds.auth;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record S2CChallengePacket(long nonce) implements CustomPacketPayload {

    public static final Type<S2CChallengePacket> TYPE =
            new Type<>(AuthNetwork.id("s2c_challenge"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CChallengePacket> STREAM_CODEC =
            StreamCodec.of(
                    S2CChallengePacket::encode,
                    S2CChallengePacket::decode
            );

    private static void encode(RegistryFriendlyByteBuf buffer, S2CChallengePacket packet) {
        buffer.writeLong(packet.nonce());
    }

    private static S2CChallengePacket decode(RegistryFriendlyByteBuf buffer) {
        return new S2CChallengePacket(buffer.readLong());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}