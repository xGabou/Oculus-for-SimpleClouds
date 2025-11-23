/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.BiomeColors
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.BlockAndTintGetter
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.nonamecrackers2.simpleclouds.mixin;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Deprecated
@Mixin(value={BiomeColors.class})
public class MixinBiomeColors {
    @Inject(method={"getAverageWaterColor"}, at={@At(value="RETURN")})
    private static void simpleclouds$modifyWaterColor_getAverageWaterColor(BlockAndTintGetter level, BlockPos pos, CallbackInfoReturnable<Integer> ci) {
    }
}

