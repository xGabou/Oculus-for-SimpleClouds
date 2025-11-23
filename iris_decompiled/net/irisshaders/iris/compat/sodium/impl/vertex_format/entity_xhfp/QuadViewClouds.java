/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.system.MemoryUtil
 */
package net.irisshaders.iris.compat.sodium.impl.vertex_format.entity_xhfp;

import java.nio.ByteBuffer;
import net.irisshaders.iris.vertices.views.QuadView;
import org.lwjgl.system.MemoryUtil;

public abstract class QuadViewClouds
implements QuadView {
    long writePointer;
    int stride;

    @Override
    public float x(int index) {
        return this.getFloat(this.writePointer - (long)this.stride * (3L - (long)index));
    }

    @Override
    public float y(int index) {
        return this.getFloat(this.writePointer + 4L - (long)this.stride * (3L - (long)index));
    }

    @Override
    public float z(int index) {
        return this.getFloat(this.writePointer + 8L - (long)this.stride * (3L - (long)index));
    }

    @Override
    public float u(int index) {
        return 0.0f;
    }

    @Override
    public float v(int index) {
        return 0.0f;
    }

    abstract float getFloat(long var1);

    public static class QuadViewCloudsNio
    extends QuadViewClouds {
        private ByteBuffer buffer;

        public void setup(ByteBuffer buffer, int writePointer, int stride) {
            this.buffer = buffer;
            this.writePointer = writePointer;
            this.stride = stride;
        }

        @Override
        float getFloat(long writePointer) {
            return this.buffer.getFloat((int)writePointer);
        }
    }

    public static class QuadViewCloudsUnsafe
    extends QuadViewClouds {
        public void setup(long writePointer, int stride) {
            this.writePointer = writePointer;
            this.stride = stride;
        }

        @Override
        float getFloat(long writePointer) {
            return MemoryUtil.memGetFloat((long)writePointer);
        }
    }
}

