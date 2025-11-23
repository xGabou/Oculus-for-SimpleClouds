/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$BlockStateBase
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package net.irisshaders.iris.mixin;

import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={BlockBehaviour.BlockStateBase.class}, priority=990)
public abstract class MixinBlockStateBehavior {
    @Shadow
    public abstract Block m_60734_();

    @Shadow
    protected abstract BlockState m_7160_();

    @Overwrite
    public float m_60792_(BlockGetter blockGetter, BlockPos blockPos) {
        float originalValue = this.m_60734_().m_7749_(this.m_7160_(), blockGetter, blockPos);
        float aoLightValue = WorldRenderingSettings.INSTANCE.getAmbientOcclusionLevel();
        return 1.0f - aoLightValue * (1.0f - originalValue);
    }
}

