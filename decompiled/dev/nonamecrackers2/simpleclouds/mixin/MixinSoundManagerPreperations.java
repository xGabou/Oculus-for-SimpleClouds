/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.sounds.Sound
 *  net.minecraft.client.resources.sounds.SoundEventRegistration
 *  net.minecraft.client.sounds.Weighted
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.Resource
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 */
package dev.nonamecrackers2.simpleclouds.mixin;

import dev.nonamecrackers2.simpleclouds.client.compat.SimpleCloudsSoundReplacements;
import java.util.Map;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundEventRegistration;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets={"net.minecraft.client.sounds.SoundManager$Preparations"})
public class MixinSoundManagerPreperations {
    @Shadow
    private Map<ResourceLocation, Resource> f_244128_;

    @ModifyVariable(method={"handleRegistration"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/sounds/WeighedSoundEvents;addSound(Lnet/minecraft/client/sounds/Weighted;)V", shift=At.Shift.BEFORE))
    public Weighted<Sound> simpleclouds$overrideVanillaRainSounds_handleRegistration(Weighted<Sound> weighted, ResourceLocation loc, SoundEventRegistration soundReg) {
        return SimpleCloudsSoundReplacements.applyReplacement(weighted, loc, soundReg, this.f_244128_);
    }
}

