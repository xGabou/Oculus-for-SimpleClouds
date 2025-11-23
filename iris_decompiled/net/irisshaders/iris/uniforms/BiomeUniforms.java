/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.Holder
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.tags.BiomeTags
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.Biome$Precipitation
 */
package net.irisshaders.iris.uniforms;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;
import net.irisshaders.iris.gl.uniform.FloatSupplier;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.parsing.BiomeCategories;
import net.irisshaders.iris.parsing.ExtendedBiome;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;

public class BiomeUniforms {
    private static final Object2IntMap<ResourceKey<Biome>> biomeMap = new Object2IntOpenHashMap();

    public static Object2IntMap<ResourceKey<Biome>> getBiomeMap() {
        return biomeMap;
    }

    public static void addBiomeUniforms(UniformHolder uniforms) {
        uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "biome", BiomeUniforms.playerI(player -> biomeMap.getInt(player.m_9236_().m_204166_(player.m_20183_()).m_203543_().orElse(null)))).uniform1i(UniformUpdateFrequency.PER_TICK, "biome_category", BiomeUniforms.playerI(player -> {
            Holder holder = player.m_9236_().m_204166_(player.m_20183_());
            ExtendedBiome extendedBiome = (ExtendedBiome)holder.m_203334_();
            if (extendedBiome.getBiomeCategory() == -1) {
                extendedBiome.setBiomeCategory(BiomeUniforms.getBiomeCategory((Holder<Biome>)holder).ordinal());
                return extendedBiome.getBiomeCategory();
            }
            return extendedBiome.getBiomeCategory();
        })).uniform1i(UniformUpdateFrequency.PER_TICK, "biome_precipitation", BiomeUniforms.playerI(player -> {
            Biome.Precipitation precipitation = ((Biome)player.m_9236_().m_204166_(player.m_20183_()).m_203334_()).m_264600_(player.m_20183_());
            return switch (precipitation) {
                default -> throw new IncompatibleClassChangeError();
                case Biome.Precipitation.NONE -> 0;
                case Biome.Precipitation.RAIN -> 1;
                case Biome.Precipitation.SNOW -> 2;
            };
        })).uniform1f(UniformUpdateFrequency.PER_TICK, "rainfall", BiomeUniforms.playerF(player -> ((ExtendedBiome)player.m_9236_().m_204166_(player.m_20183_()).m_203334_()).getDownfall())).uniform1f(UniformUpdateFrequency.PER_TICK, "temperature", BiomeUniforms.playerF(player -> ((Biome)player.m_9236_().m_204166_(player.m_20183_()).m_203334_()).m_47554_()));
    }

    private static BiomeCategories getBiomeCategory(Holder<Biome> holder) {
        if (holder.m_203656_(BiomeTags.f_215807_)) {
            return BiomeCategories.NONE;
        }
        if (holder.m_203656_(BiomeTags.f_207593_)) {
            return BiomeCategories.ICY;
        }
        if (holder.m_203656_(BiomeTags.f_207608_)) {
            return BiomeCategories.EXTREME_HILLS;
        }
        if (holder.m_203656_(BiomeTags.f_207609_)) {
            return BiomeCategories.TAIGA;
        }
        if (holder.m_203656_(BiomeTags.f_207603_)) {
            return BiomeCategories.OCEAN;
        }
        if (holder.m_203656_(BiomeTags.f_207610_)) {
            return BiomeCategories.JUNGLE;
        }
        if (holder.m_203656_(BiomeTags.f_207611_)) {
            return BiomeCategories.FOREST;
        }
        if (holder.m_203656_(BiomeTags.f_207607_)) {
            return BiomeCategories.MESA;
        }
        if (holder.m_203656_(BiomeTags.f_207612_)) {
            return BiomeCategories.NETHER;
        }
        if (holder.m_203656_(BiomeTags.f_215818_)) {
            return BiomeCategories.THE_END;
        }
        if (holder.m_203656_(BiomeTags.f_207604_)) {
            return BiomeCategories.BEACH;
        }
        if (holder.m_203656_(BiomeTags.f_207614_)) {
            return BiomeCategories.DESERT;
        }
        if (holder.m_203656_(BiomeTags.f_207605_)) {
            return BiomeCategories.RIVER;
        }
        if (holder.m_203656_(BiomeTags.f_215802_)) {
            return BiomeCategories.SWAMP;
        }
        if (holder.m_203656_(BiomeTags.f_215801_)) {
            return BiomeCategories.UNDERGROUND;
        }
        if (holder.m_203656_(BiomeTags.f_215805_)) {
            return BiomeCategories.MUSHROOM;
        }
        if (holder.m_203656_(BiomeTags.f_207606_)) {
            return BiomeCategories.MOUNTAIN;
        }
        return BiomeCategories.PLAINS;
    }

    static IntSupplier playerI(ToIntFunction<LocalPlayer> function) {
        return () -> {
            LocalPlayer player = Minecraft.m_91087_().f_91074_;
            if (player == null) {
                return 0;
            }
            return function.applyAsInt(player);
        };
    }

    static FloatSupplier playerF(ToFloatFunction<LocalPlayer> function) {
        return () -> {
            LocalPlayer player = Minecraft.m_91087_().f_91074_;
            if (player == null) {
                return 0.0f;
            }
            return function.applyAsFloat(player);
        };
    }

    @FunctionalInterface
    public static interface ToFloatFunction<T> {
        public float applyAsFloat(T var1);
    }
}

