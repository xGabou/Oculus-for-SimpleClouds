package net.Gabou.oculus_for_simpleclouds.mixin;

import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import net.Gabou.oculus_for_simpleclouds.visual.SimpleCloudsSunLighting;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SimpleCloudsRenderer.class, remap = false)
public abstract class SimpleCloudsSunLightingMixin {

    @Inject(method = "prepareShader(Lnet/minecraft/client/renderer/ShaderInstance;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FF)V", at = @At("TAIL"))
    private static void oculus_for_simpleclouds$applySunLightingUniforms(ShaderInstance shader, Matrix4f modelView, Matrix4f projMat, float fogStart, float fogEnd, CallbackInfo ci) {
        SimpleCloudsSunLighting.apply(shader);
    }
}
