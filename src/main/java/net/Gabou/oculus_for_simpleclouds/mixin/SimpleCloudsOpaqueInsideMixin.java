package net.Gabou.oculus_for_simpleclouds.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.nonamecrackers2.simpleclouds.client.mesh.chunk.MeshChunk;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.common.cloud.SimpleCloudsConstants;
import dev.nonamecrackers2.simpleclouds.common.noise.NoiseSettings;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL40;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SimpleCloudsRenderer.class, remap = false)
public abstract class SimpleCloudsOpaqueInsideMixin {

    @Unique
    private static boolean oculus_for_simpleclouds$renderingInteriorPass;

    @Inject(
            method = "renderCloudsTransparency(Ldev/nonamecrackers2/simpleclouds/client/mesh/generator/CloudMeshGenerator;Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FFFFFFLnet/minecraft/client/renderer/culling/Frustum;Z)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void oculus_for_simpleclouds$replaceTransparentPassWhenInsideCloud(
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
        if (oculus_for_simpleclouds$renderingInteriorPass) {
            return;
        }
        if (!oculus_for_simpleclouds$isCameraInsideSimpleCloudBounds(generator)) {
            return;
        }

        oculus_for_simpleclouds$renderInteriorCloudPass(
                generator,
                stack,
                projMat,
                fogStart,
                fogEnd,
                partialTick,
                r,
                g,
                b,
                frustum
        );
        ci.cancel();
    }

    @ModifyArg(
            method = "renderCloudsTransparency(Ldev/nonamecrackers2/simpleclouds/client/mesh/generator/CloudMeshGenerator;Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FFFFFFLnet/minecraft/client/renderer/culling/Frustum;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;depthMask(Z)V",
                    ordinal = 0
            ),
            index = 0
    )
    private static boolean oculus_for_simpleclouds$forceDepthWriteForInteriorPass(boolean depthMask) {
        return oculus_for_simpleclouds$renderingInteriorPass ? true : depthMask;
    }

    @Redirect(
            method = "renderCloudsTransparency(Ldev/nonamecrackers2/simpleclouds/client/mesh/generator/CloudMeshGenerator;Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FFFFFFLnet/minecraft/client/renderer/culling/Frustum;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL40;glBlendFunci(III)V",
                    ordinal = 0
            )
    )
    private static void oculus_for_simpleclouds$overrideAccumBlendForInteriorPass(int buf, int srcFactor, int dstFactor) {
        if (oculus_for_simpleclouds$renderingInteriorPass) {
            GL40.glBlendFunci(buf, GL11.GL_ONE, GL11.GL_ZERO);
            return;
        }
        GL40.glBlendFunci(buf, srcFactor, dstFactor);
    }

    @Redirect(
            method = "renderCloudsTransparency(Ldev/nonamecrackers2/simpleclouds/client/mesh/generator/CloudMeshGenerator;Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FFFFFFLnet/minecraft/client/renderer/culling/Frustum;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL40;glBlendFunci(III)V",
                    ordinal = 1
            )
    )
    private static void oculus_for_simpleclouds$overrideRevealageBlendForInteriorPass(int buf, int srcFactor, int dstFactor) {
        if (oculus_for_simpleclouds$renderingInteriorPass) {
            GL40.glBlendFunci(buf, GL11.GL_ZERO, GL11.GL_ZERO);
            return;
        }
        GL40.glBlendFunci(buf, srcFactor, dstFactor);
    }

    @Unique
    private static void oculus_for_simpleclouds$renderInteriorCloudPass(
            CloudMeshGenerator generator,
            PoseStack stack,
            Matrix4f projMat,
            float fogStart,
            float fogEnd,
            float partialTick,
            float r,
            float g,
            float b,
            Frustum frustum
    ) {
        boolean cullEnabled = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean depthTestEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        boolean depthMaskEnabled = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        float[] prevShaderColor = RenderSystem.getShaderColor().clone();

        try {
            oculus_for_simpleclouds$renderingInteriorPass = true;
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.setShaderColor(r, g, b, 1.0F);
            SimpleCloudsRenderer.renderCloudsTransparency(
                    generator,
                    stack,
                    projMat,
                    fogStart,
                    fogEnd,
                    partialTick,
                    r,
                    g,
                    b,
                    frustum,
                    false
            );
        } finally {
            oculus_for_simpleclouds$renderingInteriorPass = false;
            if (cullEnabled) {
                RenderSystem.enableCull();
            } else {
                RenderSystem.disableCull();
            }
            if (blendEnabled) {
                RenderSystem.enableBlend();
            } else {
                RenderSystem.disableBlend();
            }
            if (depthTestEnabled) {
                RenderSystem.enableDepthTest();
            } else {
                RenderSystem.disableDepthTest();
            }
            RenderSystem.depthMask(depthMaskEnabled);
            RenderSystem.setShaderColor(prevShaderColor[0], prevShaderColor[1], prevShaderColor[2], prevShaderColor[3]);
        }
    }

    @Unique
    private static boolean oculus_for_simpleclouds$isCameraInsideSimpleCloudBounds(CloudMeshGenerator generator) {
        if (oculus_for_simpleclouds$isCameraInsideSimpleCloudVolumeByCloudType()) {
            return true;
        }
        return oculus_for_simpleclouds$isCameraInsideRenderableCloudChunk(generator);
    }

    @Unique
    private static boolean oculus_for_simpleclouds$isCameraInsideRenderableCloudChunk(CloudMeshGenerator generator) {
        if (generator == null || !generator.canRender()) {
            return false;
        }

        final float origin = 0.0F;
        final float pad = 2.0F;
        final boolean[] inside = {false};

        generator.forRenderableMeshChunks(null, MeshChunk::getOpaqueBuffers, (chunk, ignored) -> {
            if (inside[0]) {
                return;
            }

            float minY = Math.min(chunk.getBoundsMinY(), chunk.getMinHeight());
            float maxY = Math.max(chunk.getBoundsMaxY(), chunk.getMaxHeight());
            inside[0] = origin >= chunk.getBoundsMinX() - pad && origin <= chunk.getBoundsMaxX() + pad
                    && origin >= minY - pad && origin <= maxY + pad
                    && origin >= chunk.getBoundsMinZ() - pad && origin <= chunk.getBoundsMaxZ() + pad;
        });

        if (!inside[0] && generator.transparencyEnabled()) {
            generator.forRenderableMeshChunks(null, chunk -> chunk.getTransparentBuffers().orElseGet(chunk::getOpaqueBuffers), (chunk, ignored) -> {
                if (inside[0]) {
                    return;
                }

                float minY = Math.min(chunk.getBoundsMinY(), chunk.getMinHeight());
                float maxY = Math.max(chunk.getBoundsMaxY(), chunk.getMaxHeight());
                inside[0] = origin >= chunk.getBoundsMinX() - pad && origin <= chunk.getBoundsMaxX() + pad
                        && origin >= minY - pad && origin <= maxY + pad
                        && origin >= chunk.getBoundsMinZ() - pad && origin <= chunk.getBoundsMaxZ() + pad;
            });
        }

        return inside[0];
    }

    @Unique
    private static boolean oculus_for_simpleclouds$isCameraInsideSimpleCloudVolumeByCloudType() {
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
        var cloudInfo = manager.getCloudTypeAtWorldPos((float) cameraPos.x, (float) cameraPos.z);
        if (cloudInfo == null || cloudInfo.getLeft() == null || cloudInfo.getLeft() == SimpleCloudsConstants.EMPTY) {
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
        return cameraY >= minCloudY - 2.0F && cameraY <= maxCloudY + 2.0F;
    }
}
