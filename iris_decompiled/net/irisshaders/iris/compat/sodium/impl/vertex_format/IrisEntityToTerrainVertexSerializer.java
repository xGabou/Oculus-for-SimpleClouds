/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.api.vertex.serializer.VertexSerializer
 *  org.lwjgl.system.MemoryUtil
 */
package net.irisshaders.iris.compat.sodium.impl.vertex_format;

import net.caffeinemc.mods.sodium.api.vertex.serializer.VertexSerializer;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.entity_xhfp.EntityVertex;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import org.lwjgl.system.MemoryUtil;

public class IrisEntityToTerrainVertexSerializer
implements VertexSerializer {
    public void serialize(long src, long dst, int vertexCount) {
        for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex) {
            MemoryUtil.memPutFloat((long)dst, (float)MemoryUtil.memGetFloat((long)src));
            MemoryUtil.memPutFloat((long)(dst + 4L), (float)MemoryUtil.memGetFloat((long)(src + 4L)));
            MemoryUtil.memPutFloat((long)(dst + 8L), (float)MemoryUtil.memGetFloat((long)(src + 8L)));
            MemoryUtil.memPutInt((long)(dst + 12L), (int)MemoryUtil.memGetInt((long)(src + 12L)));
            MemoryUtil.memPutFloat((long)(dst + 16L), (float)MemoryUtil.memGetFloat((long)(src + 16L)));
            MemoryUtil.memPutFloat((long)(dst + 20L), (float)MemoryUtil.memGetFloat((long)(src + 20L)));
            MemoryUtil.memPutInt((long)(dst + 24L), (int)MemoryUtil.memGetInt((long)(src + 28L)));
            MemoryUtil.memPutInt((long)(dst + 28L), (int)MemoryUtil.memGetInt((long)(src + 32L)));
            MemoryUtil.memPutInt((long)(dst + 32L), (int)0);
            MemoryUtil.memPutInt((long)(dst + 36L), (int)MemoryUtil.memGetInt((long)(src + 36L)));
            MemoryUtil.memPutInt((long)(dst + 40L), (int)MemoryUtil.memGetInt((long)(src + 40L)));
            MemoryUtil.memPutInt((long)(dst + 44L), (int)MemoryUtil.memGetInt((long)(src + 44L)));
            src += (long)EntityVertex.STRIDE;
            dst += (long)IrisVertexFormats.TERRAIN.m_86020_();
        }
    }
}

