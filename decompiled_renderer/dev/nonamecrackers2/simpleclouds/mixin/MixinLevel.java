/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.nonamecrackers2.simpleclouds.mixin;

import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManagerHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Level.class})
public class MixinLevel {
    @Inject(method={"isRainingAt"}, at={@At(value="HEAD")}, cancellable=true)
    public void simpleclouds$localizedWeather_isRainingAt(BlockPos pos, CallbackInfoReturnable<Boolean> ci) {
        CloudManager<Level> manager;
        if (this instanceof CloudManagerHolder && !(manager = CloudManager.get((Level)this)).shouldUseVanillaWeather()) {
            ci.setReturnValue((Object)manager.hasPrecipitationAt(pos));
        }
    }
}

