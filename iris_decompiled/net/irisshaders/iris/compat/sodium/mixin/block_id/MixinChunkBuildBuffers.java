/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap
 *  me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers
 *  me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.BakedChunkModelBuilder
 *  me.jellysquid.mods.sodium.client.render.chunk.terrain.TerrainRenderPass
 *  me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexType
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.compat.sodium.mixin.block_id;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.BakedChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexType;
import net.irisshaders.iris.compat.sodium.impl.block_context.BlockContextHolder;
import net.irisshaders.iris.compat.sodium.impl.block_context.ChunkBuildBuffersExt;
import net.irisshaders.iris.compat.sodium.impl.block_context.ContextAwareVertexWriter;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ChunkBuildBuffers.class})
public class MixinChunkBuildBuffers
implements ChunkBuildBuffersExt {
    @Shadow
    @Final
    private Reference2ReferenceOpenHashMap<TerrainRenderPass, BakedChunkModelBuilder> builders;
    @Unique
    private BlockContextHolder contextHolder;

    @Inject(method={"<init>"}, at={@At(value="RETURN")}, remap=false)
    private void iris$onConstruct(ChunkVertexType vertexType, CallbackInfo ci) {
        Object2IntMap<BlockState> blockStateIds = WorldRenderingSettings.INSTANCE.getBlockStateIds();
        this.contextHolder = blockStateIds != null ? new BlockContextHolder(blockStateIds) : new BlockContextHolder();
    }

    @Inject(method={"<init>"}, remap=false, at={@At(value="TAIL", remap=false)})
    private void iris$redirectWriterCreation(ChunkVertexType vertexType, CallbackInfo ci) {
        for (BakedChunkModelBuilder builder : this.builders.values()) {
            if (!(builder instanceof ContextAwareVertexWriter)) continue;
            ((ContextAwareVertexWriter)builder).iris$setContextHolder(this.contextHolder);
        }
    }

    @Override
    public void iris$setLocalPos(int localPosX, int localPosY, int localPosZ) {
        this.contextHolder.setLocalPos(localPosX, localPosY, localPosZ);
    }

    @Override
    public void iris$setMaterialId(BlockState state, short renderType, byte lightValue) {
        this.contextHolder.set(state, renderType, lightValue);
    }

    @Override
    public void iris$resetBlockContext() {
        this.contextHolder.reset();
    }

    @Override
    public void iris$ignoreMidBlock(boolean state) {
        this.contextHolder.ignoreMidBlock = state;
    }
}

