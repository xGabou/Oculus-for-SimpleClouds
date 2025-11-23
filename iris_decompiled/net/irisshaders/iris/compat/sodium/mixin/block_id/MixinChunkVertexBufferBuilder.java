/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.render.chunk.vertex.builder.ChunkMeshBufferBuilder
 *  me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package net.irisshaders.iris.compat.sodium.mixin.block_id;

import me.jellysquid.mods.sodium.client.render.chunk.vertex.builder.ChunkMeshBufferBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder;
import net.irisshaders.iris.compat.sodium.impl.block_context.BlockContextHolder;
import net.irisshaders.iris.compat.sodium.impl.block_context.ContextAwareVertexWriter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ChunkMeshBufferBuilder.class})
public class MixinChunkVertexBufferBuilder
implements ContextAwareVertexWriter {
    @Shadow
    @Final
    private ChunkVertexEncoder encoder;

    @Override
    public void iris$setContextHolder(BlockContextHolder holder) {
        if (this.encoder instanceof ContextAwareVertexWriter) {
            ((ContextAwareVertexWriter)this.encoder).iris$setContextHolder(holder);
        }
    }

    @Override
    public void flipUpcomingQuadNormal() {
        if (this.encoder instanceof ContextAwareVertexWriter) {
            ((ContextAwareVertexWriter)this.encoder).flipUpcomingQuadNormal();
        }
    }
}

