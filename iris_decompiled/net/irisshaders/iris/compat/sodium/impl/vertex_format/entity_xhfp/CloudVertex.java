/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.api.math.MatrixHelper
 *  net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatDescription
 *  net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatRegistry
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 *  org.lwjgl.system.MemoryUtil
 */
package net.irisshaders.iris.compat.sodium.impl.vertex_format.entity_xhfp;

import net.caffeinemc.mods.sodium.api.math.MatrixHelper;
import net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatDescription;
import net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatRegistry;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.entity_xhfp.QuadViewClouds;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import net.irisshaders.iris.vertices.NormI8;
import net.irisshaders.iris.vertices.NormalHelper;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

public final class CloudVertex {
    public static final VertexFormatDescription FORMAT = VertexFormatRegistry.instance().get(IrisVertexFormats.CLOUDS);
    public static final int STRIDE = IrisVertexFormats.CLOUDS.m_86020_();
    private static final int OFFSET_POSITION = 0;
    private static final int OFFSET_COLOR = 12;
    private static final QuadViewClouds.QuadViewCloudsUnsafe quad = new QuadViewClouds.QuadViewCloudsUnsafe();
    private static final Vector3f saveNormal = new Vector3f();
    private static int vertexCount;

    public static void write(long ptr, Matrix4f matrix, float x, float y, float z, int color) {
        float x2 = MatrixHelper.transformPositionX((Matrix4f)matrix, (float)x, (float)y, (float)z);
        float y2 = MatrixHelper.transformPositionY((Matrix4f)matrix, (float)x, (float)y, (float)z);
        float z2 = MatrixHelper.transformPositionZ((Matrix4f)matrix, (float)x, (float)y, (float)z);
        CloudVertex.write(ptr, x2, y2, z2, color);
        if (++vertexCount == 4) {
            vertexCount = 0;
            quad.setup(ptr, STRIDE);
            NormalHelper.computeFaceNormal(saveNormal, quad);
            int normal = NormI8.pack(saveNormal);
            for (long vertex = 0L; vertex < 4L; ++vertex) {
                MemoryUtil.memPutInt((long)(ptr + 16L - (long)STRIDE * vertex), (int)normal);
            }
        }
    }

    public static void write(long ptr, float x, float y, float z, int color) {
        MemoryUtil.memPutFloat((long)(ptr + 0L), (float)x);
        MemoryUtil.memPutFloat((long)(ptr + 0L + 4L), (float)y);
        MemoryUtil.memPutFloat((long)(ptr + 0L + 8L), (float)z);
        MemoryUtil.memPutInt((long)(ptr + 12L), (int)color);
        if (++vertexCount == 4) {
            vertexCount = 0;
            quad.setup(ptr, STRIDE);
            NormalHelper.computeFaceNormal(saveNormal, quad);
            int normal = NormI8.pack(saveNormal);
            for (long vertex = 0L; vertex < 4L; ++vertex) {
                MemoryUtil.memPutInt((long)(ptr + 16L - (long)STRIDE * vertex), (int)normal);
            }
        }
    }
}

