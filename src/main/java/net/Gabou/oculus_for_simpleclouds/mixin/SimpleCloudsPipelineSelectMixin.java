package net.Gabou.oculus_for_simpleclouds.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.nonamecrackers2.simpleclouds.SimpleCloudsMod;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.renderer.pipeline.CloudsRenderPipeline;
import net.Gabou.oculus_for_simpleclouds.dh.ShaderAwareNoDhPipeline;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SimpleCloudsRenderer.class, remap = false)
public abstract class SimpleCloudsPipelineSelectMixin {
    @Shadow private CloudsRenderPipeline renderPipelineThisPass;

    @Inject(method = "renderBeforeLevel", at = @At("TAIL"))
    private void oculus_for_simpleclouds$forceShaderAwarePipeline(PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        if (!CompatHelper.areShadersRunning()) {
            return;
        }
        if (SimpleCloudsMod.dhLoaded()) {
            return;
        }
        this.renderPipelineThisPass = ShaderAwareNoDhPipeline.INSTANCE;
    }
}
