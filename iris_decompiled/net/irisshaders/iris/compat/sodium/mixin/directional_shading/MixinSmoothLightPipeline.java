/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.model.light.data.QuadLightData
 *  me.jellysquid.mods.sodium.client.model.light.smooth.SmoothLightPipeline
 *  net.minecraft.core.Direction
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.compat.sodium.mixin.directional_shading;

import me.jellysquid.mods.sodium.client.model.light.data.QuadLightData;
import me.jellysquid.mods.sodium.client.model.light.smooth.SmoothLightPipeline;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={SmoothLightPipeline.class})
public class MixinSmoothLightPipeline {
    @Inject(method={"applySidedBrightness"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void iris$disableDirectionalShading(QuadLightData out, Direction face, boolean shade, CallbackInfo ci) {
        if (WorldRenderingSettings.INSTANCE.shouldDisableDirectionalShading()) {
            ci.cancel();
        }
    }
}

