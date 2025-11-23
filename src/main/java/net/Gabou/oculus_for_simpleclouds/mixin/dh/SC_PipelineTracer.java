package net.Gabou.oculus_for_simpleclouds.mixin.dh;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.nonamecrackers2.simpleclouds.client.event.impl.DetermineCloudRenderPipelineEvent;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.renderer.pipeline.CloudsRenderPipeline;
import net.Gabou.oculus_for_simpleclouds.dh.ShaderAwareDhPipeline;
import net.minecraftforge.common.MinecraftForge;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = SimpleCloudsRenderer.class, remap = false)
public abstract class SC_PipelineTracer {
    private static String lastPipeline = "";

    @Inject(method = "getRenderPipeline", at = @At("RETURN"))
    private void ofscGetPipeline(CallbackInfoReturnable<Object> cir) {
        String pipeline = String.valueOf(cir.getReturnValue());
        if (!pipeline.equals(lastPipeline)) {
            System.out.println("[OFSC TRACE] getRenderPipeline returns " + pipeline);
            lastPipeline = pipeline;
        }
    }
    @Redirect(
            method = "renderBeforeLevel",
            at = @At(
                    value = "NEW",
                    target = "dev/nonamecrackers2/simpleclouds/client/event/impl/DetermineCloudRenderPipelineEvent"
            )
    )
    private DetermineCloudRenderPipelineEvent ofsc_replaceEvent(CloudsRenderPipeline ignored) {
        CloudsRenderPipeline pipeline = CompatHelper.areShadersRunning() ? ShaderAwareDhPipeline.INSTANCE : CloudsRenderPipeline.DEFAULT;
        return new DetermineCloudRenderPipelineEvent(pipeline);
    }






}
