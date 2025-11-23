/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.DimensionSpecialEffects
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.material.FogType
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.mixin.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={DimensionSpecialEffects.class})
public class MixinDimensionSpecialEffects {
    @Inject(method={"getSunriseColor"}, at={@At(value="HEAD")}, cancellable=true)
    private void iris$getSunriseColor(float timeOfDay, float partialTicks, CallbackInfoReturnable<float[]> cir) {
        FogType fogType;
        boolean hasBlindness;
        Entity cameraEntity = Minecraft.m_91087_().m_91288_();
        boolean bl = hasBlindness = cameraEntity instanceof LivingEntity && ((LivingEntity)cameraEntity).m_21023_(MobEffects.f_19610_);
        if (hasBlindness) {
            cir.setReturnValue(null);
        }
        if ((fogType = Minecraft.m_91087_().f_91063_.m_109153_().m_167685_()) != FogType.NONE) {
            cir.setReturnValue(null);
        }
    }
}

