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
    private static long oculus_for_simpleclouds$lastLogMs;

    @Inject(method = "afterRender", at = @At("HEAD"), cancellable = true)
    private void oculus_for_simpleclouds$skipUntilReady(DhApiEventParam<Void> event, CallbackInfo ci) {
        long now = System.currentTimeMillis();
        if (now - oculus_for_simpleclouds$lastLogMs > 1000L) {
            oculus_for_simpleclouds$lastLogMs = now;
            System.out.println("[OFSC DEBUG] DH afterRender callback: ready=" + DhReadyTracker.dhReady
                    + " passComplete=" + SimpleCloudsDhCompatHandler._isPassComplete());
        }
        if (!DhReadyTracker.dhReady) {
            SimpleCloudsDhCompatHandler._markPassComplete(true);
            ci.cancel();
        }
    }
}
