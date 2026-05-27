package net.Gabou.oculus_for_simpleclouds.mixin.dh;

import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiCancelableEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;
import dev.nonamecrackers2.simpleclouds.client.dh.event.SimpleCloudsBeforeDhRenderHandler;
import net.Gabou.oculus_for_simpleclouds.dh.DhReadyTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SimpleCloudsBeforeDhRenderHandler.class, remap = false)
public class SimpleCloudsBeforeDhRenderHandlerMixin {
    private static long oculus_for_simpleclouds$lastLogMs;

    @Inject(method = "beforeRender", at = @At("HEAD"))
    private void oculus_for_simpleclouds$markDhReady(DhApiCancelableEventParam<DhApiRenderParam> event, CallbackInfo ci) {
        DhReadyTracker.dhReady = true;
        long now = System.currentTimeMillis();
        if (now - oculus_for_simpleclouds$lastLogMs > 1000L) {
            oculus_for_simpleclouds$lastLogMs = now;
            System.out.println("[OFSC DEBUG] DH beforeRender callback: fbo="
                    + dev.nonamecrackers2.simpleclouds.client.dh.SimpleCloudsDhCompatHandler._getDhFramebufferId());
        }
    }
}
