package net.Gabou.oculus_for_simpleclouds.mixin.dh;

import dev.nonamecrackers2.simpleclouds.SimpleCloudsMod;
import dev.nonamecrackers2.simpleclouds.client.event.impl.DetermineCloudRenderPipelineEvent;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.renderer.pipeline.CloudsRenderPipeline;
import net.Gabou.oculus_for_simpleclouds.dh.ShaderAwareDhPipeline;
import net.Gabou.oculus_for_simpleclouds.dh.ShaderAwareNoDhPipeline;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
        CloudsRenderPipeline pipeline;
        if (CompatHelper.areShadersRunning()) {
            pipeline = SimpleCloudsMod.dhLoaded() ? ShaderAwareDhPipeline.INSTANCE : ShaderAwareNoDhPipeline.INSTANCE;
        } else {
            pipeline = CloudsRenderPipeline.DEFAULT;
        }
        return new DetermineCloudRenderPipelineEvent(pipeline);
    }






}
