package net.Gabou.oculus_for_simpleclouds.auth;

import net.Gabou.oculus_for_simpleclouds.Oculus_for_simpleclouds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.Supplier;

public final class AuthNetwork {
    private static final String PROTOCOL_VERSION = "1";
    private static int packetId;

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(Oculus_for_simpleclouds.MODID, "auth"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    private AuthNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(
            nextPacketId(),
            S2CChallengePacket.class,
            S2CChallengePacket::encode,
            S2CChallengePacket::decode,
            AuthNetwork::handleChallenge,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
        CHANNEL.registerMessage(
            nextPacketId(),
            C2SChallengeReplyPacket.class,
            C2SChallengeReplyPacket::encode,
            C2SChallengeReplyPacket::decode,
            AuthNetwork::handleChallengeReply,
            Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
    }

    public static void sendChallenge(ServerPlayer player, S2CChallengePacket packet) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendChallengeReply(C2SChallengeReplyPacket packet) {
        CHANNEL.sendToServer(packet);
    }

    private static int nextPacketId() {
        return packetId++;
    }

    private static void handleChallenge(S2CChallengePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
            net.minecraftforge.api.distmarker.Dist.CLIENT,
            () -> () -> ClientAuth.handleChallenge(packet)
        ));
        context.setPacketHandled(true);
    }

    private static void handleChallengeReply(C2SChallengeReplyPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ServerAuth.handleChallengeReply(player, packet);
            }
        });
        context.setPacketHandled(true);
    }
}
