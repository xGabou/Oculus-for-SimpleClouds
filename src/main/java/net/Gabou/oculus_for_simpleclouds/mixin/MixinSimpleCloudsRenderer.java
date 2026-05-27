package net.Gabou.oculus_for_simpleclouds.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.nonamecrackers2.simpleclouds.SimpleCloudsMod;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.common.cloud.SimpleCloudsConstants;
import net.Gabou.oculus_for_simpleclouds.dh.ShaderAwareDhPipeline;
import net.Gabou.oculus_for_simpleclouds.interiorfog.InteriorCloudState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.Vec3;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SimpleCloudsRenderer.class, remap = false)
public class MixinSimpleCloudsRenderer {
    @Inject(method = "tick", at = @At("HEAD"), require = 0)
    private void oculus_for_simpleclouds$updateWorldEffectsBeforeTick(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.gameRenderer == null || mc.gameRenderer.getMainCamera() == null) {
            return;
        }
        Vec3 camera = mc.gameRenderer.getMainCamera().getPosition();
        ((SimpleCloudsRenderer) (Object) this).getWorldEffectsManager().renderPost(
                new PoseStack(),
                1.0F,
                camera.x,
                camera.y,
                camera.z,
                (float) SimpleCloudsConstants.CLOUD_SCALE
        );
    }

    @Inject(method = "renderWeather", at = @At("TAIL"), require = 0)
    private void oculus_for_simpleclouds$renderDhLightningWeatherFallback(LightTexture texture, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        if (!CompatHelper.areShadersRunning() || !SimpleCloudsMod.dhLoaded()) {
            return;
        }
        SimpleCloudsRenderer renderer = (SimpleCloudsRenderer) (Object) this;
        if (!renderer.getWorldEffectsManager().hasLightningToRender() || !ShaderAwareDhPipeline.shouldRenderWeatherLightningFallback()) {
            return;
        }
        renderer.getWorldEffectsManager().renderLightning(partialTick, camX, camY, camZ);
        ShaderAwareDhPipeline.markLightningRendered();
    }

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
