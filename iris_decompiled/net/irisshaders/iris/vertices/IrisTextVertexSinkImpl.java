/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  org.joml.Vector3f
 *  org.lwjgl.system.MemoryUtil
 */
package net.irisshaders.iris.vertices;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import java.util.function.IntFunction;
import net.irisshaders.iris.api.v0.IrisTextVertexSink;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import net.irisshaders.iris.vertices.NormI8;
import net.irisshaders.iris.vertices.NormalHelper;
import net.irisshaders.iris.vertices.views.QuadView;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

public class IrisTextVertexSinkImpl
implements IrisTextVertexSink {
    private static final int STRIDE = IrisVertexFormats.GLYPH.m_86020_();
    private static final int OFFSET_POSITION = 0;
    private static final int OFFSET_COLOR = 12;
    private static final int OFFSET_TEXTURE = 16;
    private static final int OFFSET_MID_TEXTURE = 38;
    private static final int OFFSET_LIGHT = 24;
    private static final int OFFSET_NORMAL = 28;
    private static final int OFFSET_TANGENT = 46;
    static VertexFormat format = IrisVertexFormats.GLYPH;
    private final ByteBuffer buffer;
    private final TextQuadView quad = new TextQuadView();
    private final Vector3f saveNormal = new Vector3f();
    private int vertexCount;
    private long elementOffset;
    private float uSum;
    private float vSum;

    public IrisTextVertexSinkImpl(int maxQuadCount, IntFunction<ByteBuffer> buffer) {
        this.buffer = buffer.apply(format.m_86020_() * 4 * maxQuadCount);
        this.elementOffset = MemoryUtil.memAddress((ByteBuffer)this.buffer);
    }

    @Override
    public VertexFormat getUnderlyingVertexFormat() {
        return format;
    }

    @Override
    public ByteBuffer getUnderlyingByteBuffer() {
        return this.buffer;
    }

    @Override
    public void quad(float minX, float minY, float maxX, float maxY, float z, int color, float minU, float minV, float maxU, float maxV, int light) {
        this.vertex(minX, minY, z, color, minU, minV, light);
        this.vertex(minX, maxY, z, color, minU, maxV, light);
        this.vertex(maxX, maxY, z, color, maxU, maxV, light);
        this.vertex(maxX, minY, z, color, maxU, minV, light);
    }

    private void vertex(float x, float y, float z, int color, float u, float v, int light) {
        ++this.vertexCount;
        this.uSum += u;
        this.vSum += v;
        long ptr = this.elementOffset;
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
        if (this.vertexCount == 4) {
            this.vertexCount = 0;
            this.uSum *= 0.25f;
            this.vSum *= 0.25f;
            this.quad.setup(this.elementOffset, IrisVertexFormats.GLYPH.m_86020_());
            NormalHelper.computeFaceNormal(this.saveNormal, this.quad);
            float normalX = this.saveNormal.x;
            float normalY = this.saveNormal.y;
            float normalZ = this.saveNormal.z;
            int normal = NormI8.pack(normalX, normalY, normalZ, 0.0f);
            int tangent = NormalHelper.computeTangent(normalX, normalY, normalZ, this.quad);
            for (long vertex = 0L; vertex < 4L; ++vertex) {
                MemoryUtil.memPutFloat((long)(ptr + 38L - (long)STRIDE * vertex), (float)this.uSum);
                MemoryUtil.memPutFloat((long)(ptr + 42L - (long)STRIDE * vertex), (float)this.vSum);
                MemoryUtil.memPutInt((long)(ptr + 28L - (long)STRIDE * vertex), (int)normal);
                MemoryUtil.memPutInt((long)(ptr + 46L - (long)STRIDE * vertex), (int)tangent);
            }
            this.uSum = 0.0f;
            this.vSum = 0.0f;
        }
        this.buffer.position(this.buffer.position() + STRIDE);
        this.elementOffset += (long)STRIDE;
    }

    static class TextQuadView
    implements QuadView {
        long writePointer;
        int stride;

        public void setup(long writePointer, int stride) {
            this.writePointer = writePointer;
            this.stride = stride;
        }

        @Override
        public float x(int index) {
            return MemoryUtil.memGetFloat((long)(this.writePointer - (long)this.stride * (3L - (long)index)));
        }

        @Override
        public float y(int index) {
            return MemoryUtil.memGetFloat((long)(this.writePointer + 4L - (long)this.stride * (3L - (long)index)));
        }

        @Override
        public float z(int index) {
            return MemoryUtil.memGetFloat((long)(this.writePointer + 8L - (long)this.stride * (3L - (long)index)));
        }

        @Override
        public float u(int index) {
            return MemoryUtil.memGetFloat((long)(this.writePointer + 16L - (long)this.stride * (3L - (long)index)));
        }

        @Override
        public float v(int index) {
            return MemoryUtil.memGetFloat((long)(this.writePointer + 20L - (long)this.stride * (3L - (long)index)));
        }
    }
}

