package net.Gabou.oculus_for_simpleclouds.mixin;

import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = SimpleCloudsRenderer.class, remap = false)
public abstract class SimpleCloudsFrustumBypassMixin {

    @ModifyArg(
            method = "renderBeforeLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/nonamecrackers2/simpleclouds/client/mesh/generator/CloudMeshGenerator;genTick(DDDLnet/minecraft/client/renderer/culling/Frustum;F)V"
            ),
            index = 3
    )
    private Frustum oculus_for_simpleclouds$disableMeshGenFrustum(Frustum frustum) {
        return null;
    }

    @ModifyArg(
            method = "renderCloudsOpaque(Ldev/nonamecrackers2/simpleclouds/client/mesh/generator/CloudMeshGenerator;Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FFFFFFLnet/minecraft/client/renderer/culling/Frustum;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/nonamecrackers2/simpleclouds/client/mesh/generator/CloudMeshGenerator;forRenderableMeshChunks(Lnet/minecraft/client/renderer/culling/Frustum;Ljava/util/function/Function;Ljava/util/function/BiConsumer;Z)V"
            ),
            index = 0
    )
    private static Frustum oculus_for_simpleclouds$disableOpaqueRenderFrustum(Frustum frustum) {
        return null;
    }

    @ModifyArg(
            method = "renderCloudsTransparency(Ldev/nonamecrackers2/simpleclouds/client/mesh/generator/CloudMeshGenerator;Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FFFFFFLnet/minecraft/client/renderer/culling/Frustum;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/nonamecrackers2/simpleclouds/client/mesh/generator/CloudMeshGenerator;forRenderableMeshChunks(Lnet/minecraft/client/renderer/culling/Frustum;Ljava/util/function/Function;Ljava/util/function/BiConsumer;Z)V"
            ),
            index = 0
    )
    private static Frustum oculus_for_simpleclouds$disableTransparentRenderFrustum(Frustum frustum) {
        return null;
    }
}
