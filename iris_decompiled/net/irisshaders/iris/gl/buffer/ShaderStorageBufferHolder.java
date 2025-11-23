/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
 */
package net.irisshaders.iris.gl.buffer;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import java.util.Collections;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.buffer.ShaderStorageBuffer;
import net.irisshaders.iris.gl.buffer.ShaderStorageInfo;
import net.irisshaders.iris.gl.sampler.SamplerLimits;

public class ShaderStorageBufferHolder {
    private int cachedWidth;
    private int cachedHeight;
    private ShaderStorageBuffer[] buffers;
    private boolean destroyed = false;

    public ShaderStorageBufferHolder(Int2ObjectArrayMap<ShaderStorageInfo> overrides, int width, int height) {
        this.cachedWidth = width;
        this.cachedHeight = height;
        this.buffers = new ShaderStorageBuffer[(Integer)Collections.max(overrides.keySet()) + 1];
        overrides.forEach((index, bufferInfo) -> {
            if ((long)bufferInfo.size() > IrisRenderSystem.getVRAM()) {
                throw new OutOfVideoMemoryError("We only have " + ShaderStorageBufferHolder.toMib(IrisRenderSystem.getVRAM()) + "MiB of RAM to work with, but the pack is requesting " + bufferInfo.size() + "! Can't continue.");
            }
            if (index > SamplerLimits.get().getMaxShaderStorageUnits()) {
                throw new IllegalStateException("We don't have enough SSBO units??? (index: " + index + ", max: " + SamplerLimits.get().getMaxShaderStorageUnits());
            }
            this.buffers[index.intValue()] = new ShaderStorageBuffer((int)index, (ShaderStorageInfo)((Object)bufferInfo));
            int buffer = this.buffers[index].getId();
            if (bufferInfo.relative()) {
                this.buffers[index].resizeIfRelative(width, height);
            } else {
                GlStateManager._glBindBuffer((int)37074, (int)buffer);
                IrisRenderSystem.bufferStorage(37074, bufferInfo.size(), 0);
                IrisRenderSystem.clearBufferSubData(37074, 33321, 0L, bufferInfo.size(), 6403, 5120, new int[]{0});
                IrisRenderSystem.bindBufferBase(37074, index, buffer);
            }
        });
        GlStateManager._glBindBuffer((int)37074, (int)0);
    }

    private static long toMib(long x) {
        return x / 1024L / 1024L;
    }

    public void hasResizedScreen(int width, int height) {
        if (width != this.cachedWidth || height != this.cachedHeight) {
            this.cachedWidth = width;
            this.cachedHeight = height;
            for (ShaderStorageBuffer buffer : this.buffers) {
                if (buffer == null) continue;
                buffer.resizeIfRelative(width, height);
            }
        }
    }

    public void setupBuffers() {
        if (this.destroyed) {
            throw new IllegalStateException("Tried to use destroyed buffer objects");
        }
        for (ShaderStorageBuffer buffer : this.buffers) {
            if (buffer == null) continue;
            buffer.bind();
        }
    }

    public int getBufferIndex(int index) {
        if (this.buffers.length < index || this.buffers[index] == null) {
            throw new RuntimeException("Tried to query a buffer for indirect dispatch that doesn't exist!");
        }
        return this.buffers[index].getId();
    }

    public void destroyBuffers() {
        for (ShaderStorageBuffer buffer : this.buffers) {
            if (buffer == null) continue;
            buffer.destroy();
        }
        this.buffers = null;
        this.destroyed = true;
    }

    private static class OutOfVideoMemoryError
    extends RuntimeException {
        public OutOfVideoMemoryError(String s) {
            super(s);
        }
    }
}

