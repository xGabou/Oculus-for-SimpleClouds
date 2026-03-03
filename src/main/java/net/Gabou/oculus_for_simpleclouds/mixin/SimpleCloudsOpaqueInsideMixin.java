package net.Gabou.oculus_for_simpleclouds.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.cloud.SimpleCloudsConstants;
import dev.nonamecrackers2.simpleclouds.common.noise.NoiseSettings;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SimpleCloudsRenderer.class, remap = false)
public abstract class SimpleCloudsOpaqueInsideMixin {

    @Inject(
            method = "renderCloudsTransparency(Ldev/nonamecrackers2/simpleclouds/client/mesh/generator/CloudMeshGenerator;Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FFFFFFLnet/minecraft/client/renderer/culling/Frustum;Z)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void oculus_for_simpleclouds$skipTransparencyWhenInsideCloud(
            CloudMeshGenerator generator,
            PoseStack stack,
            Matrix4f projMat,
            float fogStart,
            float fogEnd,
            float partialTick,
            float r,
            float g,
            float b,
            Frustum frustum,
            boolean ditherFade,
            CallbackInfo ci
    ) {
        if (oculus_for_simpleclouds$isCameraInsideSimpleCloudVolume()) {
            ci.cancel();
        }
    }

    @Unique
    private static boolean oculus_for_simpleclouds$isCameraInsideSimpleCloudVolume() {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null || mc.gameRenderer == null || mc.gameRenderer.getMainCamera() == null) {
            return false;
        }

        CloudManager<ClientLevel> manager;
        try {
            manager = CloudManager.get(level);
        } catch (NullPointerException | IllegalStateException ignored) {
            return false;
        }
        if (manager == null) {
            return false;
        }

        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        Pair<CloudType, Float> cloudInfo = manager.getCloudTypeAtWorldPos((float) cameraPos.x, (float) cameraPos.z);
        if (cloudInfo == null || cloudInfo.getLeft() == null || cloudInfo.getLeft() == SimpleCloudsConstants.EMPTY) {
            return false;
        }
        Float edgeFade = cloudInfo.getRight();
        if (edgeFade != null && edgeFade >= 1.0F) {
            return false;
        }

        NoiseSettings noise = cloudInfo.getLeft().noiseConfig();
        if (noise == null) {
            return false;
        }

        int startHeight = noise.getStartHeight();
        int endHeight = noise.getEndHeight();
        if (endHeight <= startHeight) {
            return false;
        }

        float cloudBaseY = manager.getCloudHeight();
        float minCloudY = cloudBaseY + (float) startHeight * SimpleCloudsConstants.CLOUD_SCALE;
        float maxCloudY = cloudBaseY + (float) endHeight * SimpleCloudsConstants.CLOUD_SCALE;
        float cameraY = (float) cameraPos.y;
        return cameraY >= minCloudY && cameraY <= maxCloudY;
    }
}
