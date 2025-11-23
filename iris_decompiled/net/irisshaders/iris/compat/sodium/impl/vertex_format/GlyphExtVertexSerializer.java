/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  net.caffeinemc.mods.sodium.api.vertex.serializer.VertexSerializer
 *  org.joml.Vector3f
 *  org.lwjgl.system.MemoryUtil
 */
package net.irisshaders.iris.compat.sodium.impl.vertex_format;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.caffeinemc.mods.sodium.api.vertex.serializer.VertexSerializer;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.entity_xhfp.QuadViewEntity;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import net.irisshaders.iris.vertices.NormI8;
import net.irisshaders.iris.vertices.NormalHelper;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

public class GlyphExtVertexSerializer
implements VertexSerializer {
    private static final int OFFSET_POSITION = 0;
    private static final int OFFSET_COLOR = 12;
    private static final int OFFSET_TEXTURE = 16;
    private static final int OFFSET_MID_TEXTURE = 38;
    private static final int OFFSET_LIGHT = 24;
    private static final int OFFSET_NORMAL = 28;
    private static final int OFFSET_TANGENT = 46;
    private static final QuadViewEntity.QuadViewEntityUnsafe quad = new QuadViewEntity.QuadViewEntityUnsafe();
    private static final Vector3f saveNormal = new Vector3f();
    private static final int STRIDE = IrisVertexFormats.GLYPH.m_86020_();

    private static void endQuad(float uSum, float vSum, long src, long dst) {
        uSum *= 0.25f;
        vSum *= 0.25f;
        quad.setup(src, IrisVertexFormats.GLYPH.m_86020_());
        NormalHelper.computeFaceNormal(saveNormal, quad);
        float normalX = GlyphExtVertexSerializer.saveNormal.x;
        float normalY = GlyphExtVertexSerializer.saveNormal.y;
        float normalZ = GlyphExtVertexSerializer.saveNormal.z;
        int normal = NormI8.pack(saveNormal);
        int tangent = NormalHelper.computeTangent(normalX, normalY, normalZ, quad);
        for (long vertex = 0L; vertex < 4L; ++vertex) {
            MemoryUtil.memPutFloat((long)(dst + 38L - (long)STRIDE * vertex), (float)uSum);
            MemoryUtil.memPutFloat((long)(dst + 42L - (long)STRIDE * vertex), (float)vSum);
            MemoryUtil.memPutInt((long)(dst + 28L - (long)STRIDE * vertex), (int)normal);
            MemoryUtil.memPutInt((long)(dst + 46L - (long)STRIDE * vertex), (int)tangent);
        }
    }

    public void serialize(long src, long dst, int vertexCount) {
        float uSum = 0.0f;
        float vSum = 0.0f;
        for (int i = 0; i < vertexCount; ++i) {
            float u = MemoryUtil.memGetFloat((long)(src + 16L));
            float v = MemoryUtil.memGetFloat((long)(src + 16L + 4L));
            uSum += u;
            vSum += v;
            MemoryUtil.memCopy((long)src, (long)dst, (long)28L);
            MemoryUtil.memPutShort((long)(dst + 32L), (short)((short)CapturedRenderingState.INSTANCE.getCurrentRenderedEntity()));
            MemoryUtil.memPutShort((long)(dst + 34L), (short)((short)CapturedRenderingState.INSTANCE.getCurrentRenderedBlockEntity()));
            MemoryUtil.memPutShort((long)(dst + 36L), (short)((short)CapturedRenderingState.INSTANCE.getCurrentRenderedItem()));
            src += (long)DefaultVertexFormat.f_85820_.m_86020_();
            dst += (long)IrisVertexFormats.GLYPH.m_86020_();
        }
        GlyphExtVertexSerializer.endQuad(uSum, vSum, src, dst);
    }
}

