/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  me.jellysquid.mods.sodium.client.model.quad.ModelQuadView
 *  net.caffeinemc.mods.sodium.api.util.NormI8
 *  net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter
 *  net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatDescription
 *  net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatRegistry
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package net.irisshaders.iris.compat.sodium.impl.vertex_format.entity_xhfp;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import net.caffeinemc.mods.sodium.api.util.NormI8;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatDescription;
import net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatRegistry;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.entity_xhfp.QuadViewEntity;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import net.irisshaders.iris.vertices.NormalHelper;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public final class GlyphVertexExt {
    public static final VertexFormatDescription FORMAT = VertexFormatRegistry.instance().get(IrisVertexFormats.GLYPH);
    public static final int STRIDE = IrisVertexFormats.GLYPH.m_86020_();
    private static final int OFFSET_POSITION = 0;
    private static final int OFFSET_COLOR = 12;
    private static final int OFFSET_TEXTURE = 16;
    private static final int OFFSET_MID_TEXTURE = 38;
    private static final int OFFSET_LIGHT = 24;
    private static final int OFFSET_NORMAL = 28;
    private static final int OFFSET_TANGENT = 46;
    private static final QuadViewEntity.QuadViewEntityUnsafe quad = new QuadViewEntity.QuadViewEntityUnsafe();
    private static final Vector3f saveNormal = new Vector3f();
    private static final Vector3f lastNormal = new Vector3f();
    private static final QuadViewEntity.QuadViewEntityUnsafe quadView = new QuadViewEntity.QuadViewEntityUnsafe();
    private static int vertexCount;
    private static float uSum;
    private static float vSum;

    public static void write(long ptr, float x, float y, float z, int color, float u, float v, int light) {
        long i = ptr;
        ++vertexCount;
        uSum += u;
        vSum += v;
        MemoryUtil.memPutFloat((long)(ptr + 0L), (float)x);
        MemoryUtil.memPutFloat((long)(ptr + 0L + 4L), (float)y);
        MemoryUtil.memPutFloat((long)(ptr + 0L + 8L), (float)z);
        MemoryUtil.memPutInt((long)(ptr + 12L), (int)color);
        MemoryUtil.memPutFloat((long)(ptr + 16L), (float)u);
        MemoryUtil.memPutFloat((long)(ptr + 16L + 4L), (float)v);
        MemoryUtil.memPutInt((long)(ptr + 24L), (int)light);
        MemoryUtil.memPutShort((long)(ptr + 32L), (short)((short)CapturedRenderingState.INSTANCE.getCurrentRenderedEntity()));
        MemoryUtil.memPutShort((long)(ptr + 34L), (short)((short)CapturedRenderingState.INSTANCE.getCurrentRenderedBlockEntity()));
        MemoryUtil.memPutShort((long)(ptr + 36L), (short)((short)CapturedRenderingState.INSTANCE.getCurrentRenderedItem()));
        if (vertexCount == 4) {
            GlyphVertexExt.endQuad(ptr);
        }
    }

    private static void endQuad(long ptr) {
        vertexCount = 0;
        uSum = (float)((double)uSum * 0.25);
        vSum = (float)((double)vSum * 0.25);
        quad.setup(ptr, STRIDE);
        NormalHelper.computeFaceNormal(saveNormal, quad);
        float normalX = GlyphVertexExt.saveNormal.x;
        float normalY = GlyphVertexExt.saveNormal.y;
        float normalZ = GlyphVertexExt.saveNormal.z;
        int normal = NormI8.pack((Vector3f)saveNormal);
        int tangent = NormalHelper.computeTangent(normalX, normalY, normalZ, quad);
        for (long vertex = 0L; vertex < 4L; ++vertex) {
            MemoryUtil.memPutFloat((long)(ptr + 38L - (long)STRIDE * vertex), (float)uSum);
            MemoryUtil.memPutFloat((long)(ptr + 42L - (long)STRIDE * vertex), (float)vSum);
            MemoryUtil.memPutInt((long)(ptr + 28L - (long)STRIDE * vertex), (int)normal);
            MemoryUtil.memPutInt((long)(ptr + 46L - (long)STRIDE * vertex), (int)tangent);
        }
        uSum = 0.0f;
        vSum = 0.0f;
    }

    public static void writeQuadVertices(VertexBufferWriter writer, PoseStack.Pose matrices, ModelQuadView quad, int light, int color) {
        Matrix3f matNormal = matrices.m_252943_();
        Matrix4f matPosition = matrices.m_252922_();
        try (MemoryStack stack = MemoryStack.stackPush();){
            long buffer;
            long ptr = buffer = stack.nmalloc(4 * STRIDE);
            Vector3f n = quad.getLightFace().m_253071_();
            float nx = n.x;
            float ny = n.y;
            float nz = n.z;
            float nxt = matNormal.m00() * nx + matNormal.m10() * ny + matNormal.m20() * nz;
            float nyt = matNormal.m01() * nx + matNormal.m11() * ny + matNormal.m21() * nz;
            float nzt = matNormal.m02() * nx + matNormal.m12() * ny + matNormal.m22() * nz;
            int nt = NormI8.pack((float)nxt, (float)nyt, (float)nzt);
            for (int i = 0; i < 4; ++i) {
                float x = quad.getX(i);
                float y = quad.getY(i);
                float z = quad.getZ(i);
                float xt = matPosition.m00() * x + matPosition.m10() * y + matPosition.m20() * z + matPosition.m30();
                float yt = matPosition.m01() * x + matPosition.m11() * y + matPosition.m21() * z + matPosition.m31();
                float zt = matPosition.m02() * x + matPosition.m12() * y + matPosition.m22() * z + matPosition.m32();
                GlyphVertexExt.write(ptr, xt, yt, zt, color, quad.getTexU(i), quad.getTexV(i), light);
                ptr += (long)STRIDE;
            }
            GlyphVertexExt.endQuad(ptr - (long)STRIDE, nxt, nyt, nzt);
            writer.push(stack, buffer, 4, FORMAT);
        }
    }

    private static void endQuad(long ptr, float normalX, float normalY, float normalZ) {
        quadView.setup(ptr, STRIDE);
        int tangent = NormalHelper.computeTangent(normalX, normalY, normalZ, quadView);
        for (long vertex = 0L; vertex < 4L; ++vertex) {
            MemoryUtil.memPutInt((long)(ptr + 44L - (long)STRIDE * vertex), (int)tangent);
        }
    }

    public static void computeFaceNormal(Vector3f saveTo, ModelQuadView q) {
        float normZ;
        float dx0;
        float dx1;
        float normY;
        float dy1;
        float dz0;
        float x0 = q.getX(0);
        float y0 = q.getY(0);
        float z0 = q.getZ(0);
        float x1 = q.getX(1);
        float y1 = q.getY(1);
        float z1 = q.getZ(1);
        float x2 = q.getX(2);
        float y2 = q.getY(2);
        float z2 = q.getZ(2);
        float x3 = q.getX(3);
        float y3 = q.getY(3);
        float dy0 = y2 - y0;
        float z3 = q.getZ(3);
        float dz1 = z3 - z1;
        float normX = dy0 * dz1 - (dz0 = z2 - z0) * (dy1 = y3 - y1);
        float l = (float)Math.sqrt(normX * normX + (normY = dz0 * (dx1 = x3 - x1) - (dx0 = x2 - x0) * dz1) * normY + (normZ = dx0 * dy1 - dy0 * dx1) * normZ);
        if (l != 0.0f) {
            normX /= l;
            normY /= l;
            normZ /= l;
        }
        saveTo.set(normX, normY, normZ);
    }
}

