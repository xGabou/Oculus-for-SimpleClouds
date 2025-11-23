/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.Biomes
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.mixin;

import net.irisshaders.iris.uniforms.BiomeUniforms;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Biomes.class})
public class MixinBiomes {
    private static int currentId = 0;

    @Inject(method={"register"}, at={@At(value="TAIL")})
    private static void iris$registerBiome(String string, CallbackInfoReturnable<ResourceKey<Biome>> cir) {
        BiomeUniforms.getBiomeMap().put((Object)((ResourceKey)cir.getReturnValue()), currentId++);
    }
}

