package net.Gabou.oculus_for_simpleclouds.mixin.dh;

import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;
import dev.nonamecrackers2.simpleclouds.client.dh.SimpleCloudsDhCompatHandler;
import dev.nonamecrackers2.simpleclouds.client.dh.event.SimpleCloudsAfterDhRenderHandler;
import net.Gabou.oculus_for_simpleclouds.dh.DhReadyTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SimpleCloudsAfterDhRenderHandler.class, remap = false)
public class SimpleCloudsAfterDhEventGuardMixin {
    @Inject(method = "afterRender", at = @At("HEAD"), cancellable = true)
    private void oculus_for_simpleclouds$skipUntilReady(DhApiEventParam<Void> event, CallbackInfo ci) {
        if (!DhReadyTracker.dhReady) {
            SimpleCloudsDhCompatHandler._markPassComplete(true);
            ci.cancel();
        }
    }
}
