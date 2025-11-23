/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.blockentity.BeaconRenderer
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.shadows;

import com.mojang.blaze3d.vertex.PoseStack;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={BeaconRenderer.class})
public class MixinBeaconRenderer {
    @Inject(method={"renderBeaconBeam(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/resources/ResourceLocation;FFJII[FFF)V"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$noLightBeamInShadowPass(PoseStack poseStack, MultiBufferSource multiBufferSource, ResourceLocation resourceLocation, float f, float g, long l, int i, int j, float[] fs, float h, float k, CallbackInfo ci) {
        if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
            ci.cancel();
        }
    }
}

