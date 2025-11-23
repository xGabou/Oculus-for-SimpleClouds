/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.projectile.AbstractArrow
 *  net.minecraft.world.entity.projectile.ThrownTrident
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.nonamecrackers2.simpleclouds.mixin.vanillacompat;

import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={ThrownTrident.class})
public abstract class MixinThrownTrident
extends AbstractArrow {
    protected MixinThrownTrident(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
        throw new IllegalAccessError();
    }

    @Redirect(method={"onHitEntity"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;isThundering()Z"))
    public boolean simpleclouds$channelingFix_onHitEntity(Level level) {
        CloudManager<Level> manager = CloudManager.get(level);
        if (manager.shouldUseVanillaWeather()) {
            return level.m_46470_();
        }
        return ((CloudType)manager.getCloudTypeAtWorldPos((float)this.m_20185_(), (float)this.m_20189_()).getLeft()).weatherType().includesThunder();
    }
}

