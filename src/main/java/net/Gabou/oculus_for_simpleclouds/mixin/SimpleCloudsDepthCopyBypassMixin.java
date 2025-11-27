package net.Gabou.oculus_for_simpleclouds.mixin;

import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.renderer.pipeline.ShaderSupportPipeline;
import net.Gabou.oculus_for_simpleclouds.dh.ShaderAwareDhPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ShaderSupportPipeline.class, remap = false)
public abstract class SimpleCloudsDepthCopyBypassMixin {

    @Redirect(
            method = "afterLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/nonamecrackers2/simpleclouds/client/renderer/SimpleCloudsRenderer;copyDepthFromMainToClouds()V"
            )
    )
    private void oculus_for_simpleclouds$skipDepthCopyWhenShaderAware(SimpleCloudsRenderer renderer) {
        if (ShaderAwareDhPipeline.skipDepthCopy) {
            return;
        }
        renderer.copyDepthFromMainToClouds();
    }
}
