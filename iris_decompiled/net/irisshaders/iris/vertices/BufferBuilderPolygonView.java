/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.vertices;

import java.nio.ByteBuffer;
import net.irisshaders.iris.vertices.views.QuadView;

public class BufferBuilderPolygonView
implements QuadView {
    private ByteBuffer buffer;
    private int writePointer;
    private int stride = 48;
    private int vertexAmount;

    public void setup(ByteBuffer buffer, int writePointer, int stride, int vertexAmount) {
        this.buffer = buffer;
        this.writePointer = writePointer;
        this.stride = stride;
        this.vertexAmount = vertexAmount;
    }

    @Override
    public float x(int index) {
        return this.buffer.getFloat(this.writePointer - this.stride * (this.vertexAmount - index));
    }

    @Override
    public float y(int index) {
        return this.buffer.getFloat(this.writePointer + 4 - this.stride * (this.vertexAmount - index));
    }

    @Override
    public float z(int index) {
        return this.buffer.getFloat(this.writePointer + 8 - this.stride * (this.vertexAmount - index));
    }

    @Override
    public float u(int index) {
        return this.buffer.getFloat(this.writePointer + 16 - this.stride * (this.vertexAmount - index));
    }

    @Override
    public float v(int index) {
        return this.buffer.getFloat(this.writePointer + 20 - this.stride * (this.vertexAmount - index));
    }
}

