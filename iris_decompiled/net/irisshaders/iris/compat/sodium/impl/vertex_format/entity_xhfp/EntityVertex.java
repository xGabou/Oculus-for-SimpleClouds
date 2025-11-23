/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  me.jellysquid.mods.sodium.client.model.quad.ModelQuadView
 *  me.jellysquid.mods.sodium.client.util.ModelQuadUtil
 *  net.caffeinemc.mods.sodium.api.math.MatrixHelper
 *  net.caffeinemc.mods.sodium.api.util.ColorARGB
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
import me.jellysquid.mods.sodium.client.util.ModelQuadUtil;
import net.caffeinemc.mods.sodium.api.math.MatrixHelper;
import net.caffeinemc.mods.sodium.api.util.ColorARGB;
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

public final class EntityVertex {
    public static final VertexFormatDescription FORMAT = VertexFormatRegistry.instance().get(IrisVertexFormats.ENTITY);
    public static final int STRIDE = IrisVertexFormats.ENTITY.m_86020_();
    private static final int OFFSET_POSITION = 0;
    private static final int OFFSET_COLOR = 12;
    private static final int OFFSET_TEXTURE = 16;
    private static final int OFFSET_MID_TEXTURE = 42;
    private static final int OFFSET_OVERLAY = 24;
    private static final int OFFSET_LIGHT = 28;
    private static final int OFFSET_NORMAL = 32;
    private static final int OFFSET_TANGENT = 50;
    private static final Vector3f lastNormal = new Vector3f();
    private static final QuadViewEntity.QuadViewEntityUnsafe quadView = new QuadViewEntity.QuadViewEntityUnsafe();

    public static void write(long ptr, float x, float y, float z, int color, float u, float v, float midU, float midV, int light, int overlay, int normal, int tangent) {
        MemoryUtil.memPutFloat((long)(ptr + 0L), (float)x);
        MemoryUtil.memPutFloat((long)(ptr + 0L + 4L), (float)y);
        MemoryUtil.memPutFloat((long)(ptr + 0L + 8L), (float)z);
        MemoryUtil.memPutInt((long)(ptr + 12L), (int)color);
        MemoryUtil.memPutFloat((long)(ptr + 16L), (float)u);
        MemoryUtil.memPutFloat((long)(ptr + 16L + 4L), (float)v);
        MemoryUtil.memPutInt((long)(ptr + 28L), (int)light);
        MemoryUtil.memPutInt((long)(ptr + 24L), (int)overlay);
        MemoryUtil.memPutInt((long)(ptr + 32L), (int)normal);
        MemoryUtil.memPutInt((long)(ptr + 50L), (int)tangent);
        MemoryUtil.memPutFloat((long)(ptr + 42L), (float)midU);
        MemoryUtil.memPutFloat((long)(ptr + 42L + 4L), (float)midV);
        MemoryUtil.memPutShort((long)(ptr + 36L), (short)((short)CapturedRenderingState.INSTANCE.getCurrentRenderedEntity()));
        MemoryUtil.memPutShort((long)(ptr + 38L), (short)((short)CapturedRenderingState.INSTANCE.getCurrentRenderedBlockEntity()));
        MemoryUtil.memPutShort((long)(ptr + 40L), (short)((short)CapturedRenderingState.INSTANCE.getCurrentRenderedItem()));
    }

    public static void write2(long ptr, float x, float y, float z, int color, float u, float v, float midU, float midV, int light, int overlay, int normal) {
        MemoryUtil.memPutFloat((long)(ptr + 0L), (float)x);
        MemoryUtil.memPutFloat((long)(ptr + 0L + 4L), (float)y);
        MemoryUtil.memPutFloat((long)(ptr + 0L + 8L), (float)z);
        MemoryUtil.memPutInt((long)(ptr + 12L), (int)color);
        MemoryUtil.memPutFloat((long)(ptr + 16L), (float)u);
        MemoryUtil.memPutFloat((long)(ptr + 16L + 4L), (float)v);
        MemoryUtil.memPutInt((long)(ptr + 28L), (int)light);
        MemoryUtil.memPutInt((long)(ptr + 24L), (int)overlay);
        MemoryUtil.memPutInt((long)(ptr + 32L), (int)normal);
        MemoryUtil.memPutFloat((long)(ptr + 42L), (float)midU);
        MemoryUtil.memPutFloat((long)(ptr + 42L + 4L), (float)midV);
        MemoryUtil.memPutShort((long)(ptr + 36L), (short)((short)CapturedRenderingState.INSTANCE.getCurrentRenderedEntity()));
        MemoryUtil.memPutShort((long)(ptr + 38L), (short)((short)CapturedRenderingState.INSTANCE.getCurrentRenderedBlockEntity()));
        MemoryUtil.memPutShort((long)(ptr + 40L), (short)((short)CapturedRenderingState.INSTANCE.getCurrentRenderedItem()));
    }

    private static int multARGBInts(int colorA, int colorB) {
        if (colorA == -1) {
            return colorB;
        }
        if (colorB == -1) {
            return colorA;
        }
        int a = (int)((float)ColorARGB.unpackAlpha((int)colorA) / 255.0f * ((float)ColorARGB.unpackAlpha((int)colorB) / 255.0f) * 255.0f);
        int b = (int)((float)ColorARGB.unpackBlue((int)colorA) / 255.0f * ((float)ColorARGB.unpackBlue((int)colorB) / 255.0f) * 255.0f);
        int g = (int)((float)ColorARGB.unpackGreen((int)colorA) / 255.0f * ((float)ColorARGB.unpackGreen((int)colorB) / 255.0f) * 255.0f);
        int r = (int)((float)ColorARGB.unpackRed((int)colorA) / 255.0f * ((float)ColorARGB.unpackRed((int)colorB) / 255.0f) * 255.0f);
        return ColorARGB.pack((int)r, (int)g, (int)b, (int)a);
    }

    public static void writeQuadVertices(VertexBufferWriter writer, PoseStack.Pose matrices, ModelQuadView quad, int light, int overlay, int color) {
        Matrix3f matNormal = matrices.m_252943_();
        Matrix4f matPosition = matrices.m_252922_();
        try (MemoryStack stack = MemoryStack.stackPush();){
            long buffer;
            long ptr = buffer = stack.nmalloc(4 * STRIDE);
            float nxt = 0.0f;
            float nyt = 0.0f;
            float nzt = 0.0f;
            float midU = (quad.getTexU(0) + quad.getTexU(1) + quad.getTexU(2) + quad.getTexU(3)) * 0.25f;
            float midV = (quad.getTexV(0) + quad.getTexV(1) + quad.getTexV(2) + quad.getTexV(3)) * 0.25f;
            for (int i = 0; i < 4; ++i) {
                float nz;
                float ny;
                float nx;
                float x = quad.getX(i);
                float y = quad.getY(i);
                float z = quad.getZ(i);
                float xt = MatrixHelper.transformPositionX((Matrix4f)matPosition, (float)x, (float)y, (float)z);
                float yt = MatrixHelper.transformPositionY((Matrix4f)matPosition, (float)x, (float)y, (float)z);
                float zt = MatrixHelper.transformPositionZ((Matrix4f)matPosition, (float)x, (float)y, (float)z);
                int n = quad.getForgeNormal(i);
                if ((n & 0xFFFFFF) == 0) {
                    Vector3f n0 = quad.getLightFace().m_253071_();
                    nx = n0.x;
                    ny = n0.y;
                    nz = n0.z;
                } else {
                    nx = NormI8.unpackX((int)n);
                    ny = NormI8.unpackY((int)n);
                    nz = NormI8.unpackZ((int)n);
                }
                nxt = MatrixHelper.transformNormalX((Matrix3f)matNormal, (float)nx, (float)ny, (float)nz);
                nyt = MatrixHelper.transformNormalY((Matrix3f)matNormal, (float)nx, (float)ny, (float)nz);
                nzt = MatrixHelper.transformNormalZ((Matrix3f)matNormal, (float)nx, (float)ny, (float)nz);
                int nt = NormI8.pack((float)nxt, (float)nyt, (float)nzt);
                EntityVertex.write2(ptr, xt, yt, zt, EntityVertex.multARGBInts(quad.getColor(i), color), quad.getTexU(i), quad.getTexV(i), midU, midV, ModelQuadUtil.mergeBakedLight((int)quad.getLight(i), (int)light), overlay, nt);
                ptr += (long)STRIDE;
            }
            EntityVertex.endQuad(ptr - (long)STRIDE, nxt, nyt, nzt);
            writer.push(stack, buffer, 4, FORMAT);
        }
    }

    private static void endQuad(long ptr, float normalX, float normalY, float normalZ) {
        quadView.setup(ptr, STRIDE);
        int tangent = NormalHelper.computeTangent(normalX, normalY, normalZ, quadView);
        for (long vertex = 0L; vertex < 4L; ++vertex) {
            MemoryUtil.memPutInt((long)(ptr + 50L - (long)STRIDE * vertex), (int)tangent);
        }
    }
}

