/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.GameRenderer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.irisshaders.iris.Iris;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GameRenderer.class})
public class MixinTweakFarPlane {
    @Shadow
    private float f_109062_;

    @Shadow
    public float m_172790_() {
        throw new AssertionError();
    }

    @Redirect(method={"getProjectionMatrix"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/GameRenderer;getDepthFar()F"))
    private float iris$tweakViewDistanceToMatchOptiFine(GameRenderer renderer) {
        if (!Iris.getCurrentPack().isPresent()) {
            return this.m_172790_();
        }
        float tweakedViewDistance = this.f_109062_;
        return tweakedViewDistance += 1024.0f;
    }

    private void iris$tweakViewDistanceBasedOnFog(float f, long l, PoseStack poseStack, CallbackInfo ci) {
        if (!Iris.getCurrentPack().isPresent()) {
            return;
        }
        this.f_109062_ *= 0.95f;
    }
}

