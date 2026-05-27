package net.Gabou.oculus_for_simpleclouds.mixin.dh;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.nonamecrackers2.simpleclouds.SimpleCloudsMod;
import dev.nonamecrackers2.simpleclouds.client.event.impl.DetermineCloudRenderPipelineEvent;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.renderer.pipeline.CloudsRenderPipeline;
import net.Gabou.oculus_for_simpleclouds.dh.ShaderAwareDhEventBridge;
import net.Gabou.oculus_for_simpleclouds.dh.ShaderAwareDhPipeline;
import net.Gabou.oculus_for_simpleclouds.dh.ShaderAwareNoDhPipeline;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SimpleCloudsRenderer.class, remap = false)
public abstract class SC_PipelineTracer {
    @Redirect(
            method = "renderBeforeLevel",
            at = @At(
                    value = "NEW",
                    target = "dev/nonamecrackers2/simpleclouds/client/event/impl/DetermineCloudRenderPipelineEvent"
            )
    )
    private DetermineCloudRenderPipelineEvent ofsc_replaceEvent(CloudsRenderPipeline ignored) {
        CloudsRenderPipeline pipeline;
        if (CompatHelper.areShadersRunning()) {
            pipeline = SimpleCloudsMod.dhLoaded() ? ShaderAwareDhPipeline.INSTANCE : ShaderAwareNoDhPipeline.INSTANCE;
        } else {
            pipeline = CloudsRenderPipeline.DEFAULT;
        }
        return new DetermineCloudRenderPipelineEvent(pipeline);
    }

    @Inject(method = "renderBeforeLevel", at = @At("TAIL"))
    private void ofsc_logRenderBeforeLevel(PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        if (SimpleCloudsMod.dhLoaded()) {
            ShaderAwareDhEventBridge.register();
        }
    }





}
