package net.Gabou.oculus_for_simpleclouds.auth;

import net.Gabou.oculus_for_simpleclouds.Oculus_for_simpleclouds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class AuthNetwork {
    private static final String PROTOCOL_VERSION = "1";

    private AuthNetwork() {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Oculus_for_simpleclouds.MODID, path);
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Oculus_for_simpleclouds.MODID)
                .versioned(PROTOCOL_VERSION);

        registrar.playToClient(
                S2CChallengePacket.TYPE,
                S2CChallengePacket.STREAM_CODEC,
                AuthNetwork::handleChallenge
        );

        registrar.playToServer(
                C2SChallengeReplyPacket.TYPE,
                C2SChallengeReplyPacket.STREAM_CODEC,
                AuthNetwork::handleChallengeReply
        );
    }

    public static void sendChallenge(ServerPlayer player, S2CChallengePacket packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    public static void sendChallengeReply(C2SChallengeReplyPacket packet) {
        PacketDistributor.sendToServer(packet);
    }

    private static void handleChallenge(S2CChallengePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (FMLEnvironment.dist.isClient()) {
                ClientAuth.handleChallenge(packet);
            }
        });
    }

    private static void handleChallengeReply(C2SChallengeReplyPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ServerAuth.handleChallengeReply(player, packet);
            }
        });
    }
}