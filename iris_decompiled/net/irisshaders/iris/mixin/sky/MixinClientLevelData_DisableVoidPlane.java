/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel$ClientLevelData
 *  net.minecraft.world.level.material.FogType
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.mixin.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ClientLevel.ClientLevelData.class})
public class MixinClientLevelData_DisableVoidPlane {
    @Inject(method={"getHorizonHeight"}, at={@At(value="HEAD")}, cancellable=true)
    private void iris$getHorizonHeight(CallbackInfoReturnable<Double> cir) {
        FogType fogType = Minecraft.m_91087_().f_91063_.m_109153_().m_167685_();
        if (fogType != FogType.NONE) {
            cir.setReturnValue((Object)Double.NEGATIVE_INFINITY);
        }
    }
}

