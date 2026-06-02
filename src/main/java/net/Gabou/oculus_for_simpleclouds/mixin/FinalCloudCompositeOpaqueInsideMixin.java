package net.Gabou.oculus_for_simpleclouds.mixin;

import dev.nonamecrackers2.simpleclouds.common.cloud.SimpleCloudsConstants;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import net.Gabou.oculus_for_simpleclouds.client.FinalCloudCompositeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FinalCloudCompositeHandler.class, remap = false)
public abstract class FinalCloudCompositeOpaqueInsideMixin {

    @Redirect(
            method = "*",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glBlendFunc(II)V"
            ),
            require = 0
    )
    private static void oculus_for_simpleclouds$forceOpaqueCompositeBlendWhenInsideCloud(int srcFactor, int dstFactor) {
        if (oculus_for_simpleclouds$isCameraInsideSimpleCloudVolume()) {
            // Source replace: write cloud color directly instead of alpha blending.
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO);
            return;
        }
        GL11.glBlendFunc(srcFactor, dstFactor);
    }

    @Inject(method = "compositeClouds", at = @At("RETURN"))
    private static void oculus_for_simpleclouds$restoreDefaultBlendFunc(CallbackInfo ci) {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
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
        var cloudInfo = manager.getCloudTypeAtWorldPos((float) cameraPos.x, (float) cameraPos.z);
        if (cloudInfo == null || cloudInfo.getLeft() == null || cloudInfo.getLeft() == SimpleCloudsConstants.EMPTY) {
            return false;
        }
        var noise = cloudInfo.getLeft().noiseConfig();
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
