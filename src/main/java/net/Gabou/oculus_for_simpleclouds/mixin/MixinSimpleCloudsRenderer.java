package net.Gabou.oculus_for_simpleclouds.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import net.Gabou.oculus_for_simpleclouds.interiorfog.InteriorCloudState;
import net.minecraft.client.renderer.culling.Frustum;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SimpleCloudsRenderer.class, remap = false)
public class MixinSimpleCloudsRenderer {
    @Inject(
            method = "renderCloudsOpaque(Ldev/nonamecrackers2/simpleclouds/client/mesh/generator/CloudMeshGenerator;Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FFFFFFLnet/minecraft/client/renderer/culling/Frustum;Z)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void simplecloudsinteriorfog$cancelOpaqueCloudMesh(CloudMeshGenerator generator, PoseStack stack, Matrix4f projMat, float fogStart, float fogEnd, float partialTick, float r, float g, float b, Frustum frustum, boolean ditherFade, CallbackInfo ci) {
        if (InteriorCloudState.shouldSuppressOpaqueMesh(partialTick)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "renderCloudsTransparency(Ldev/nonamecrackers2/simpleclouds/client/mesh/generator/CloudMeshGenerator;Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FFFFFFLnet/minecraft/client/renderer/culling/Frustum;Z)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void simplecloudsinteriorfog$cancelTransparentCloudMesh(CloudMeshGenerator generator, PoseStack stack, Matrix4f projMat, float fogStart, float fogEnd, float partialTick, float r, float g, float b, Frustum frustum, boolean ditherFade, CallbackInfo ci) {
        if (InteriorCloudState.shouldSuppressTransparentMesh(partialTick)) {
            ci.cancel();
        }
    }
}
