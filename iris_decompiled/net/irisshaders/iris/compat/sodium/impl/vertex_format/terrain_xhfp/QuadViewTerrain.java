/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.system.MemoryUtil
 */
package net.irisshaders.iris.compat.sodium.impl.vertex_format.terrain_xhfp;

import java.nio.ByteBuffer;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.terrain_xhfp.XHFPModelVertexType;
import net.irisshaders.iris.vertices.views.QuadView;
import org.lwjgl.system.MemoryUtil;

public abstract class QuadViewTerrain
implements QuadView {
    long writePointer;
    int stride;

    @Override
    public float x(int index) {
        return XHFPModelVertexType.decodePosition(this.getShort(this.writePointer - (long)this.stride * (long)(3 - index)));
    }

    @Override
    public float y(int index) {
        return XHFPModelVertexType.decodePosition(this.getShort(this.writePointer + 2L - (long)this.stride * (long)(3 - index)));
    }

    @Override
    public float z(int index) {
        return XHFPModelVertexType.decodePosition(this.getShort(this.writePointer + 4L - (long)this.stride * (long)(3 - index)));
    }

    @Override
    public float u(int index) {
        return XHFPModelVertexType.decodeBlockTexture(this.getShort(this.writePointer + 12L - (long)this.stride * (long)(3 - index)));
    }

    @Override
    public float v(int index) {
        return XHFPModelVertexType.decodeBlockTexture(this.getShort(this.writePointer + 14L - (long)this.stride * (long)(3 - index)));
    }

    abstract short getShort(long var1);

    public static class QuadViewTerrainNio
    extends QuadViewTerrain {
        private ByteBuffer buffer;

        public void setup(ByteBuffer buffer, int writePointer, int stride) {
            this.buffer = buffer;
            this.writePointer = writePointer;
            this.stride = stride;
        }

        @Override
        short getShort(long writePointer) {
            return this.buffer.getShort((int)writePointer);
        }
    }

    public static class QuadViewTerrainUnsafe
    extends QuadViewTerrain {
        public void setup(long writePointer, int stride) {
            this.writePointer = writePointer;
            this.stride = stride;
        }

        @Override
        short getShort(long writePointer) {
            return MemoryUtil.memGetShort((long)writePointer);
        }
    }
}

