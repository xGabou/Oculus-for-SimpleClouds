/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.render.chunk.terrain.material.Material
 *  me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder
 *  me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder$Vertex
 *  org.joml.Vector3f
 *  org.lwjgl.system.MemoryUtil
 */
package net.irisshaders.iris.compat.sodium.impl.vertex_format.terrain_xhfp;

import me.jellysquid.mods.sodium.client.render.chunk.terrain.material.Material;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder;
import net.irisshaders.iris.compat.sodium.impl.block_context.BlockContextHolder;
import net.irisshaders.iris.compat.sodium.impl.block_context.ContextAwareVertexWriter;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.terrain_xhfp.QuadViewTerrain;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.terrain_xhfp.XHFPModelVertexType;
import net.irisshaders.iris.vertices.ExtendedDataHelper;
import net.irisshaders.iris.vertices.NormI8;
import net.irisshaders.iris.vertices.NormalHelper;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

public class XHFPTerrainVertex
implements ChunkVertexEncoder,
ContextAwareVertexWriter {
    private final QuadViewTerrain.QuadViewTerrainUnsafe quad = new QuadViewTerrain.QuadViewTerrainUnsafe();
    private final Vector3f normal = new Vector3f();
    private BlockContextHolder contextHolder;
    private int vertexCount;
    private float uSum;
    private float vSum;
    private boolean flipUpcomingNormal;

    @Override
    public void iris$setContextHolder(BlockContextHolder holder) {
        this.contextHolder = holder;
    }

    @Override
    public void flipUpcomingQuadNormal() {
        this.flipUpcomingNormal = true;
    }

    public long write(long ptr, Material material, ChunkVertexEncoder.Vertex vertex, int chunkId) {
        this.uSum += vertex.u;
        this.vSum += vertex.v;
        ++this.vertexCount;
        MemoryUtil.memPutShort((long)ptr, (short)XHFPModelVertexType.encodePosition(vertex.x));
        MemoryUtil.memPutShort((long)(ptr + 2L), (short)XHFPModelVertexType.encodePosition(vertex.y));
        MemoryUtil.memPutShort((long)(ptr + 4L), (short)XHFPModelVertexType.encodePosition(vertex.z));
        MemoryUtil.memPutByte((long)(ptr + 6L), (byte)((byte)material.bits()));
        MemoryUtil.memPutByte((long)(ptr + 7L), (byte)((byte)chunkId));
        MemoryUtil.memPutInt((long)(ptr + 8L), (int)vertex.color);
        MemoryUtil.memPutInt((long)(ptr + 12L), (int)XHFPModelVertexType.encodeTexture(vertex.u, vertex.v));
        MemoryUtil.memPutInt((long)(ptr + 16L), (int)vertex.light);
        MemoryUtil.memPutShort((long)(ptr + 32L), (short)this.contextHolder.blockId);
        MemoryUtil.memPutShort((long)(ptr + 34L), (short)this.contextHolder.renderType);
        MemoryUtil.memPutInt((long)(ptr + 36L), (int)(this.contextHolder.ignoreMidBlock ? 0 : ExtendedDataHelper.computeMidBlock(vertex.x, vertex.y, vertex.z, this.contextHolder.localPosX, this.contextHolder.localPosY, this.contextHolder.localPosZ)));
        MemoryUtil.memPutByte((long)(ptr + 39L), (byte)this.contextHolder.lightValue);
        if (this.vertexCount == 4) {
            this.vertexCount = 0;
            this.uSum *= 0.25f;
            this.vSum *= 0.25f;
            int midUV = XHFPModelVertexType.encodeTexture(this.uSum, this.vSum);
            MemoryUtil.memPutInt((long)(ptr + 20L), (int)midUV);
            MemoryUtil.memPutInt((long)(ptr + 20L - 40L), (int)midUV);
            MemoryUtil.memPutInt((long)(ptr + 20L - 80L), (int)midUV);
            MemoryUtil.memPutInt((long)(ptr + 20L - 120L), (int)midUV);
            this.uSum = 0.0f;
            this.vSum = 0.0f;
            this.quad.setup(ptr, 40);
            if (this.flipUpcomingNormal) {
                NormalHelper.computeFaceNormalFlipped(this.normal, this.quad);
                this.flipUpcomingNormal = false;
            } else {
                NormalHelper.computeFaceNormal(this.normal, this.quad);
            }
            int packedNormal = NormI8.pack(this.normal);
            MemoryUtil.memPutInt((long)(ptr + 28L), (int)packedNormal);
            MemoryUtil.memPutInt((long)(ptr + 28L - 40L), (int)packedNormal);
            MemoryUtil.memPutInt((long)(ptr + 28L - 80L), (int)packedNormal);
            MemoryUtil.memPutInt((long)(ptr + 28L - 120L), (int)packedNormal);
            int tangent = NormalHelper.computeTangent(this.normal.x, this.normal.y, this.normal.z, this.quad);
            MemoryUtil.memPutInt((long)(ptr + 24L), (int)tangent);
            MemoryUtil.memPutInt((long)(ptr + 24L - 40L), (int)tangent);
            MemoryUtil.memPutInt((long)(ptr + 24L - 80L), (int)tangent);
            MemoryUtil.memPutInt((long)(ptr + 24L - 120L), (int)tangent);
        }
        return ptr + 40L;
    }
}

