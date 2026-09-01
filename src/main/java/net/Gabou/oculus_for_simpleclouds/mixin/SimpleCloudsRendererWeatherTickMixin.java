package net.Gabou.oculus_for_simpleclouds.mixin;

import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.common.cloud.SimpleCloudsConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Refreshes localized SimpleClouds weather before WorldEffects.tick() builds the
 * rain geometry for the next frame.
 */
@Mixin(value = SimpleCloudsRenderer.class, remap = false)
public class SimpleCloudsRendererWeatherTickMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void oculus_for_simpleclouds$updateWorldEffectsBeforeTick(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.gameRenderer == null || mc.gameRenderer.getMainCamera() == null) {
            return;
        }

        Vec3 camera = mc.gameRenderer.getMainCamera().getPosition();
        ((SimpleCloudsRenderer) (Object) this).getWorldEffectsManager().renderPost(
                new Matrix4f(),
                1.0F,
                camera.x,
                camera.y,
                camera.z,
                (float) SimpleCloudsConstants.CLOUD_SCALE
        );
    }
}
