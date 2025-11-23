/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.BlockState
 */
package net.irisshaders.iris.compat.sodium.impl.block_context;

import net.minecraft.world.level.block.state.BlockState;

public interface ChunkBuildBuffersExt {
    public void iris$setLocalPos(int var1, int var2, int var3);

    public void iris$setMaterialId(BlockState var1, short var2, byte var3);

    public void iris$resetBlockContext();

    public void iris$ignoreMidBlock(boolean var1);
}

