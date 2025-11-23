/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.LivingEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.mixin;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={GameRenderer.class}, priority=1010)
public class MixinGameRenderer_NightVisionCompat {
    @Inject(method={"getNightVisionScale"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/effect/MobEffectInstance;endsWithin(I)Z")}, cancellable=true, require=0)
    private static void iris$safecheckNightvisionStrength(LivingEntity livingEntity, float partialTicks, CallbackInfoReturnable<Float> cir) {
        if (livingEntity.m_21124_(MobEffects.f_19611_) == null) {
            cir.setReturnValue((Object)Float.valueOf(0.0f));
        }
    }
}

