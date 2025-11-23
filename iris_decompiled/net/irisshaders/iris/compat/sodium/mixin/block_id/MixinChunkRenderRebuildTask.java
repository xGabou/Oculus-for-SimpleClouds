/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadFacing
 *  me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers
 *  me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildContext
 *  me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildOutput
 *  me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder
 *  me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderCache
 *  me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext
 *  me.jellysquid.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask
 *  me.jellysquid.mods.sodium.client.render.chunk.data.BuiltSectionInfo$Builder
 *  me.jellysquid.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials
 *  me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder$Vertex
 *  me.jellysquid.mods.sodium.client.util.task.CancellationToken
 *  me.jellysquid.mods.sodium.client.world.WorldSlice
 *  net.minecraft.client.renderer.chunk.VisGraph
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.world.level.block.LightBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package net.irisshaders.iris.compat.sodium.mixin.block_id;

import me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadFacing;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderCache;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import me.jellysquid.mods.sodium.client.render.chunk.data.BuiltSectionInfo;
import me.jellysquid.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder;
import me.jellysquid.mods.sodium.client.util.task.CancellationToken;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.irisshaders.iris.compat.sodium.impl.block_context.ChunkBuildBuffersExt;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value={ChunkBuilderMeshingTask.class})
public class MixinChunkRenderRebuildTask {
    private final ChunkVertexEncoder.Vertex[] vertices = ChunkVertexEncoder.Vertex.uninitializedQuad();

    @Inject(method={"execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;"}, at={@At(value="INVOKE", target="net/minecraft/world/level/block/state/BlockState.getRenderShape()Lnet/minecraft/world/level/block/RenderShape;")}, locals=LocalCapture.CAPTURE_FAILHARD)
    private void iris$setLocalPos(ChunkBuildContext context, CancellationToken cancellationSource, CallbackInfoReturnable<ChunkBuildOutput> cir, BuiltSectionInfo.Builder renderData, VisGraph occluder, ChunkBuildBuffers buffers, BlockRenderCache cacheLocal, WorldSlice slice, int baseX, int baseY, int baseZ, int maxX, int maxY, int maxZ, BlockPos.MutableBlockPos pos, BlockPos.MutableBlockPos renderOffset, BlockRenderContext context2, int relY, int relZ, int relX, BlockState blockState) {
        if (WorldRenderingSettings.INSTANCE.shouldVoxelizeLightBlocks() && blockState.m_60734_() instanceof LightBlock) {
            ChunkModelBuilder buildBuffers = buffers.get(DefaultMaterials.CUTOUT);
            ((ChunkBuildBuffersExt)buffers).iris$setLocalPos(0, 0, 0);
            ((ChunkBuildBuffersExt)buffers).iris$ignoreMidBlock(true);
            ((ChunkBuildBuffersExt)buffers).iris$setMaterialId(blockState, (short)0, (byte)blockState.m_60791_());
            for (int i = 0; i < 4; ++i) {
                this.vertices[i].x = (float)(relX & 0xF) + 0.25f;
                this.vertices[i].y = (float)(relY & 0xF) + 0.25f;
                this.vertices[i].z = (float)(relZ & 0xF) + 0.25f;
                this.vertices[i].u = 0.0f;
                this.vertices[i].v = 0.0f;
                this.vertices[i].color = 0;
                this.vertices[i].light = blockState.m_60791_() << 4 | blockState.m_60791_() << 20;
            }
            buildBuffers.getVertexBuffer(ModelQuadFacing.UNASSIGNED).push(this.vertices, DefaultMaterials.CUTOUT);
            ((ChunkBuildBuffersExt)buffers).iris$ignoreMidBlock(false);
            return;
        }
        if (context.buffers instanceof ChunkBuildBuffersExt) {
            ((ChunkBuildBuffersExt)context.buffers).iris$setLocalPos(relX, relY, relZ);
        }
    }

    @Inject(method={"execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/block/BlockModelShaper;getBlockModel(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/resources/model/BakedModel;")}, locals=LocalCapture.CAPTURE_FAILHARD)
    private void iris$wrapGetBlockLayer(ChunkBuildContext context, CancellationToken cancellationSource, CallbackInfoReturnable<ChunkBuildOutput> cir, BuiltSectionInfo.Builder renderData, VisGraph occluder, ChunkBuildBuffers buffers, BlockRenderCache cacheLocal, WorldSlice slice, int baseX, int baseY, int baseZ, int maxX, int maxY, int maxZ, BlockPos.MutableBlockPos pos, BlockPos.MutableBlockPos renderOffset, BlockRenderContext context2, int relY, int relZ, int relX, BlockState blockState) {
        if (context.buffers instanceof ChunkBuildBuffersExt) {
            ((ChunkBuildBuffersExt)context.buffers).iris$setMaterialId(blockState, (short)-1, (byte)blockState.m_60791_());
        }
    }

    @Inject(method={"execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;"}, at={@At(value="INVOKE", target="Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/FluidRenderer;render(Lme/jellysquid/mods/sodium/client/world/WorldSlice;Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildBuffers;)V")}, locals=LocalCapture.CAPTURE_FAILHARD, remap=false)
    private void iris$wrapGetFluidLayer(ChunkBuildContext context, CancellationToken cancellationSource, CallbackInfoReturnable<ChunkBuildOutput> cir, BuiltSectionInfo.Builder renderData, VisGraph occluder, ChunkBuildBuffers buffers, BlockRenderCache cacheLocal, WorldSlice slice, int baseX, int baseY, int baseZ, int maxX, int maxY, int maxZ, BlockPos.MutableBlockPos pos, BlockPos.MutableBlockPos renderOffset, BlockRenderContext context2, int relY, int relZ, int relX, BlockState blockState, FluidState fluidState) {
        if (context.buffers instanceof ChunkBuildBuffersExt) {
            ((ChunkBuildBuffersExt)context.buffers).iris$setMaterialId(fluidState.m_76188_(), (short)1, (byte)blockState.m_60791_());
        }
    }

    @Inject(method={"execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/block/state/BlockState;hasBlockEntity()Z")})
    private void iris$resetContext(ChunkBuildContext buildContext, CancellationToken cancellationSource, CallbackInfoReturnable<ChunkBuildOutput> cir) {
        if (buildContext.buffers instanceof ChunkBuildBuffersExt) {
            ((ChunkBuildBuffersExt)buildContext.buffers).iris$resetBlockContext();
        }
    }
}

