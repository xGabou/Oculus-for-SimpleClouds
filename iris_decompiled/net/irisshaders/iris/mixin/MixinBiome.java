/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.Biome$ClimateSettings
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package net.irisshaders.iris.mixin;

import net.irisshaders.iris.parsing.ExtendedBiome;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={Biome.class}, priority=990)
public class MixinBiome
implements ExtendedBiome {
    @Shadow
    @Final
    private Biome.ClimateSettings f_47437_;
    private int biomeCategory = -1;

    @Override
    public int getBiomeCategory() {
        return this.biomeCategory;
    }

    @Override
    public void setBiomeCategory(int biomeCategory) {
        this.biomeCategory = biomeCategory;
    }

    @Override
    public float getDownfall() {
        return this.f_47437_.f_47683_();
    }
}

