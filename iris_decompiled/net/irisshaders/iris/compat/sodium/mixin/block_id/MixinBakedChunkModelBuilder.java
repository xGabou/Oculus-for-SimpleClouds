/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.BakedChunkModelBuilder
 *  me.jellysquid.mods.sodium.client.render.chunk.vertex.builder.ChunkMeshBufferBuilder
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package net.irisshaders.iris.compat.sodium.mixin.block_id;

import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.BakedChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.builder.ChunkMeshBufferBuilder;
import net.irisshaders.iris.compat.sodium.impl.block_context.BlockContextHolder;
import net.irisshaders.iris.compat.sodium.impl.block_context.ContextAwareVertexWriter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={BakedChunkModelBuilder.class})
public class MixinBakedChunkModelBuilder
implements ContextAwareVertexWriter {
    @Shadow
    @Final
    private ChunkMeshBufferBuilder[] vertexBuffers;

    @Override
    public void iris$setContextHolder(BlockContextHolder holder) {
        for (ChunkMeshBufferBuilder builder : this.vertexBuffers) {
            ((ContextAwareVertexWriter)builder).iris$setContextHolder(holder);
        }
    }

    @Override
    public void flipUpcomingQuadNormal() {
        for (ChunkMeshBufferBuilder builder : this.vertexBuffers) {
            ((ContextAwareVertexWriter)builder).flipUpcomingQuadNormal();
        }
    }
}

