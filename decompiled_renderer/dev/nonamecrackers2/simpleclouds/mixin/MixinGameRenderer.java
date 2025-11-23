/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GameRenderer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.nonamecrackers2.simpleclouds.mixin;

import dev.nonamecrackers2.simpleclouds.client.gui.CloudPreviewerScreen;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={GameRenderer.class})
public class MixinGameRenderer {
    @Inject(method={"getDepthFar"}, at={@At(value="HEAD")}, cancellable=true)
    public void simpleclouds$extendFarPlane_getDepthFar(CallbackInfoReturnable<Float> ci) {
        CloudMeshGenerator generator = SimpleCloudsRenderer.getInstance().getMeshGenerator();
        if (generator != null) {
            ci.setReturnValue((Object)Float.valueOf((float)generator.getCloudAreaMaxRadius() * 8.0f));
        }
    }

    @Inject(method={"close"}, at={@At(value="TAIL")})
    public void simpleclouds$shutdownRenderer_close(CallbackInfo ci) {
        SimpleCloudsRenderer.getOptionalInstance().ifPresent(SimpleCloudsRenderer::shutdown);
        CloudPreviewerScreen.destroyMeshGenerator();
    }

    @Inject(method={"resize"}, at={@At(value="TAIL")})
    public void simpleclouds$resizeRenderer_resize(int width, int height, CallbackInfo ci) {
        SimpleCloudsRenderer.getOptionalInstance().ifPresent(renderer -> renderer.onMainWindowResize(width, height));
    }
}

