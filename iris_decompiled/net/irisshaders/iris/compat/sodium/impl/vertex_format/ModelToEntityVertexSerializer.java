/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.api.util.NormI8
 *  net.caffeinemc.mods.sodium.api.vertex.serializer.VertexSerializer
 *  org.lwjgl.system.MemoryUtil
 */
package net.irisshaders.iris.compat.sodium.impl.vertex_format;

import net.caffeinemc.mods.sodium.api.util.NormI8;
import net.caffeinemc.mods.sodium.api.vertex.serializer.VertexSerializer;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import net.irisshaders.iris.vertices.NormalHelper;
import org.lwjgl.system.MemoryUtil;

public class ModelToEntityVertexSerializer
implements VertexSerializer {
    public void serialize(long src, long dst, int vertexCount) {
        int quadCount = vertexCount / 4;
        for (int i = 0; i < quadCount; ++i) {
            int normal = MemoryUtil.memGetInt((long)(src + 32L));
            int tangent = NormalHelper.computeTangent(NormI8.unpackX((int)normal), NormI8.unpackY((int)normal), NormI8.unpackZ((int)normal), MemoryUtil.memGetFloat((long)src), MemoryUtil.memGetFloat((long)(src + 4L)), MemoryUtil.memGetFloat((long)(src + 8L)), MemoryUtil.memGetFloat((long)(src + 16L)), MemoryUtil.memGetFloat((long)(src + 20L)), MemoryUtil.memGetFloat((long)(src + 36L)), MemoryUtil.memGetFloat((long)(src + 4L + 36L)), MemoryUtil.memGetFloat((long)(src + 8L + 36L)), MemoryUtil.memGetFloat((long)(src + 16L + 36L)), MemoryUtil.memGetFloat((long)(src + 20L + 36L)), MemoryUtil.memGetFloat((long)(src + 36L + 36L)), MemoryUtil.memGetFloat((long)(src + 4L + 36L + 36L)), MemoryUtil.memGetFloat((long)(src + 8L + 36L + 36L)), MemoryUtil.memGetFloat((long)(src + 16L + 36L + 36L)), MemoryUtil.memGetFloat((long)(src + 20L + 36L + 36L)));
            float midU = 0.0f;
            float midV = 0.0f;
            for (int vertex = 0; vertex < 4; ++vertex) {
                midU += MemoryUtil.memGetFloat((long)(src + 16L + (long)(36 * vertex)));
                midV += MemoryUtil.memGetFloat((long)(src + 20L + (long)(36 * vertex)));
            }
            midU /= 4.0f;
            midV /= 4.0f;
            for (int j = 0; j < 4; ++j) {
                MemoryUtil.memCopy((long)src, (long)dst, (long)36L);
                MemoryUtil.memPutShort((long)(dst + 36L), (short)((short)CapturedRenderingState.INSTANCE.getCurrentRenderedEntity()));
                MemoryUtil.memPutShort((long)(dst + 38L), (short)((short)CapturedRenderingState.INSTANCE.getCurrentRenderedBlockEntity()));
                MemoryUtil.memPutShort((long)(dst + 40L), (short)((short)CapturedRenderingState.INSTANCE.getCurrentRenderedItem()));
                MemoryUtil.memPutFloat((long)(dst + 42L), (float)midU);
                MemoryUtil.memPutFloat((long)(dst + 46L), (float)midV);
                MemoryUtil.memPutInt((long)(dst + 50L), (int)tangent);
                src += 36L;
                dst += (long)IrisVertexFormats.ENTITY.m_86020_();
            }
        }
    }
}

