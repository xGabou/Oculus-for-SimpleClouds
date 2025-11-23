/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.PoseStack
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  net.minecraft.client.renderer.ChunkBufferBuilderPack
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.BlockRenderDispatcher
 *  net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$RenderChunk$RebuildTask$CompileResults
 *  net.minecraft.client.renderer.chunk.RenderChunkRegion
 *  net.minecraft.client.renderer.chunk.VisGraph
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraftforge.client.model.data.ModelData
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package net.irisshaders.iris.mixin.vertices.block_rendering;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Iterator;
import java.util.Set;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.vertices.BlockSensitiveBufferBuilder;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(targets={"net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$RenderChunk$RebuildTask"}, priority=999)
public class MixinChunkRebuildTask {
    @Unique
    private static final String RENDER = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask;compile(FFFLnet/minecraft/client/renderer/ChunkBufferBuilderPack;)Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask$CompileResults;";
    @Unique
    private final Object2IntMap<BlockState> blockStateIds = this.getBlockStateIds();
    @Unique
    private BlockSensitiveBufferBuilder lastBufferBuilder;

    @Unique
    private Object2IntMap<BlockState> getBlockStateIds() {
        return WorldRenderingSettings.INSTANCE.getBlockStateIds();
    }

    @Unique
    private short resolveBlockId(BlockState state) {
        if (this.blockStateIds == null) {
            return -1;
        }
        return (short)this.blockStateIds.getOrDefault((Object)state, -1);
    }

    @Inject(method={"Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask;compile(FFFLnet/minecraft/client/renderer/ChunkBufferBuilderPack;)Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask$CompileResults;"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderLiquid(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V")}, locals=LocalCapture.CAPTURE_FAILHARD)
    private void iris$onRenderLiquid(float cameraX, float cameraY, float cameraZ, ChunkBufferBuilderPack buffers, CallbackInfoReturnable<ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults> cir, ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults results, int i, BlockPos blockPos, BlockPos blockPos2, VisGraph chunkOcclusionDataBuilder, RenderChunkRegion chunkRendererRegion, PoseStack poseStack, Set<?> set2, RandomSource random, BlockRenderDispatcher blockRenderManager, Iterator<BlockPos> var15, BlockPos blockPos3, BlockState blockState, BlockState blockState2, FluidState fluidState, RenderType renderType, BufferBuilder bufferBuilder2) {
        if (bufferBuilder2 instanceof BlockSensitiveBufferBuilder) {
            this.lastBufferBuilder = (BlockSensitiveBufferBuilder)bufferBuilder2;
            this.lastBufferBuilder.beginBlock(this.resolveBlockId(fluidState.m_76188_()), (short)1, blockPos3.m_123341_() & 0xF, blockPos3.m_123342_() & 0xF, blockPos3.m_123343_() & 0xF);
        }
    }

    @Inject(method={"Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask;compile(FFFLnet/minecraft/client/renderer/ChunkBufferBuilderPack;)Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask$CompileResults;"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderLiquid(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V", shift=At.Shift.AFTER)})
    private void iris$finishRenderingLiquid(float cameraX, float cameraY, float cameraZ, ChunkBufferBuilderPack buffers, CallbackInfoReturnable<ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults> cir) {
        if (this.lastBufferBuilder != null) {
            this.lastBufferBuilder.endBlock();
            this.lastBufferBuilder = null;
        }
    }

    @Inject(method={"Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask;compile(FFFLnet/minecraft/client/renderer/ChunkBufferBuilderPack;)Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask$CompileResults;"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V")}, locals=LocalCapture.CAPTURE_FAILHARD)
    private void iris$onRenderBlock(float cameraX, float cameraY, float cameraZ, ChunkBufferBuilderPack buffers, CallbackInfoReturnable<ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults> cir, ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults results, int i, BlockPos blockPos, BlockPos blockPos2, VisGraph chunkOcclusionDataBuilder, RenderChunkRegion chunkRendererRegion, PoseStack poseStack, Set set2, RandomSource random, BlockRenderDispatcher blockRenderManager, Iterator<BlockPos> var15, BlockPos blockPos3, BlockState blockState, BlockState state2, FluidState fluidState, BakedModel model, ModelData mData, Iterator it, RenderType renderType, BufferBuilder bufferBuilder2) {
        if (bufferBuilder2 instanceof BlockSensitiveBufferBuilder) {
            this.lastBufferBuilder = (BlockSensitiveBufferBuilder)bufferBuilder2;
            this.lastBufferBuilder.beginBlock(this.resolveBlockId(blockState), (short)-1, blockPos3.m_123341_() & 0xF, blockPos3.m_123342_() & 0xF, blockPos3.m_123343_() & 0xF);
        }
    }

    @Inject(method={"Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask;compile(FFFLnet/minecraft/client/renderer/ChunkBufferBuilderPack;)Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask$CompileResults;"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V", shift=At.Shift.AFTER)})
    private void iris$finishRenderingBlock(float cameraX, float cameraY, float cameraZ, ChunkBufferBuilderPack buffers, CallbackInfoReturnable<ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults> cir) {
        if (this.lastBufferBuilder != null) {
            this.lastBufferBuilder.endBlock();
            this.lastBufferBuilder = null;
        }
    }
}

