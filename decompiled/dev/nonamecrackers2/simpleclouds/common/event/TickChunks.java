/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.GameRules
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.Biome$Precipitation
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.SnowLayerBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 */
package dev.nonamecrackers2.simpleclouds.common.event;

import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;

public class TickChunks {
    public static void rainAndSnowVanillaCompatibility(ServerLevel level, ChunkAccess chunk) {
        CloudManager<ServerLevel> manager = CloudManager.get(level);
        if (level.f_46441_.m_188503_(16) == 0) {
            int blockX = chunk.m_7697_().m_45604_();
            int blockZ = chunk.m_7697_().m_45605_();
            BlockPos checkPos = level.m_5452_(Heightmap.Types.MOTION_BLOCKING, level.m_46496_(blockX, 0, blockZ, 15));
            boolean isRaining = manager.isRainingAt(checkPos);
            boolean isSnowing = manager.isSnowingAt(checkPos);
            if (isSnowing || isRaining) {
                int snowAccumulationHeight;
                BlockPos belowPos = checkPos.m_7495_();
                Biome biome = (Biome)level.m_204166_(belowPos).m_203334_();
                Biome.Precipitation biomePrecipitation = biome.m_264600_(belowPos);
                if (biomePrecipitation != Biome.Precipitation.NONE) {
                    BlockState blockState = level.m_8055_(belowPos);
                    blockState.m_60734_().m_141997_(blockState, (Level)level, belowPos, biomePrecipitation);
                }
                if ((snowAccumulationHeight = level.m_46469_().m_46215_(GameRules.f_254637_)) > 0 && biome.m_47519_((LevelReader)level, checkPos)) {
                    BlockState blockStateAtCheckPos = level.m_8055_(checkPos);
                    if (blockStateAtCheckPos.m_60713_(Blocks.f_50125_)) {
                        int layers = (Integer)blockStateAtCheckPos.m_61143_((Property)SnowLayerBlock.f_56581_);
                        if (layers < Math.min(snowAccumulationHeight, 8)) {
                            BlockState updatedBlockState = (BlockState)blockStateAtCheckPos.m_61124_((Property)SnowLayerBlock.f_56581_, (Comparable)Integer.valueOf(layers + 1));
                            Block.m_49897_((BlockState)blockStateAtCheckPos, (BlockState)updatedBlockState, (LevelAccessor)level, (BlockPos)checkPos);
                            level.m_46597_(checkPos, updatedBlockState);
                        }
                    } else {
                        level.m_46597_(checkPos, Blocks.f_50125_.m_49966_());
                    }
                }
            }
        }
    }
}

