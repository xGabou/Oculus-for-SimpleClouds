/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraftforge.client.ChunkRenderTypeSet
 *  net.minecraftforge.client.model.data.ModelData
 *  net.minecraftforge.registries.ForgeRegistries
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package net.irisshaders.iris.compat.sodium.mixin.shader_overrides;

import java.util.Map;
import me.jellysquid.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={ChunkBuilderMeshingTask.class})
public class MixinChunkBuilderMeshingTask {
    @Redirect(method={"execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/resources/model/BakedModel;getRenderTypes(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;)Lnet/minecraftforge/client/ChunkRenderTypeSet;"), remap=false)
    private ChunkRenderTypeSet oculus$overrideRenderTypes(BakedModel instance, BlockState blockState, RandomSource randomSource, ModelData modelData) {
        ChunkRenderTypeSet type;
        Map<Holder.Reference<Block>, ChunkRenderTypeSet> idMap = WorldRenderingSettings.INSTANCE.getBlockTypeIds();
        if (idMap != null && (type = idMap.get(ForgeRegistries.BLOCKS.getDelegateOrThrow((Object)blockState.m_60734_()))) != null) {
            return type;
        }
        return instance.getRenderTypes(blockState, randomSource, modelData);
    }
}

