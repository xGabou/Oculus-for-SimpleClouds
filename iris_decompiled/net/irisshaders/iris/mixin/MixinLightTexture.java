/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.world.entity.LivingEntity
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.mixin;

import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={LightTexture.class})
public class MixinLightTexture {
    @Shadow
    @Final
    private Minecraft f_109876_;

    @Inject(method={"updateLightTexture"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/ClientLevel;getSkyDarken(F)F")})
    private void resetDarknessValue(float $$0, CallbackInfo ci) {
        CapturedRenderingState.INSTANCE.setDarknessLightFactor(0.0f);
    }

    @Inject(method={"calculateDarknessScale"}, at={@At(value="RETURN")})
    private void storeDarknessValue(LivingEntity $$0, float $$1, float $$2, CallbackInfoReturnable<Float> cir) {
        CapturedRenderingState.INSTANCE.setDarknessLightFactor((float)((double)((Float)cir.getReturnValue()).floatValue() * (Double)this.f_109876_.f_91066_.m_231926_().m_231551_()));
    }
}

