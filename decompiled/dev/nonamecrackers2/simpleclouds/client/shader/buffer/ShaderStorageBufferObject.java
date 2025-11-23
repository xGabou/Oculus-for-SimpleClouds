/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.platform.MemoryTracker
 *  com.mojang.blaze3d.systems.RenderSystem
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL15
 *  org.lwjgl.opengl.GL30
 *  org.lwjgl.opengl.GL43
 *  org.lwjgl.system.MemoryUtil
 */
package dev.nonamecrackers2.simpleclouds.client.shader.buffer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.nonamecrackers2.simpleclouds.client.shader.buffer.WithBinding;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryUtil;

public class ShaderStorageBufferObject
implements WithBinding {
    private static final Logger LOGGER = LogManager.getLogger((String)"simpleclouds/ShaderStorageBufferObject");
    private static int maxSize = -1;
    protected int id;
    protected final int binding;
    protected final int usage;
    @Nullable
    protected ByteBuffer buffer;

    public ShaderStorageBufferObject(int id, int binding, int usage) {
        this.id = id;
        this.binding = binding;
        this.usage = usage;
    }

    public static int getMaxSize() {
        if (maxSize == -1) {
            maxSize = GL11.glGetInteger((int)37086);
        }
        return maxSize;
    }

    public void bindToProgram(String name, int programId) {
        this.bindToProgram(name, programId, true);
    }

    public void optionalBindToProgram(String name, int programId) {
        this.bindToProgram(name, programId, false);
    }

    private void bindToProgram(String name, int programId, boolean throwIfMissing) {
        RenderSystem.assertOnRenderThreadOrInit();
        this.assertValid();
        int index = GL43.glGetProgramResourceIndex((int)programId, (int)37606, (CharSequence)name);
        if (index == -1 && throwIfMissing) {
            throw new NullPointerException("Unknown block index with name '" + name + "'");
        }
        if (index != -1) {
            GL43.glShaderStorageBlockBinding((int)programId, (int)index, (int)this.binding);
        }
    }

    public void uploadData(ByteBuffer buffer) {
        int size = buffer.remaining();
        if (size > ShaderStorageBufferObject.getMaxSize()) {
            throw new IllegalArgumentException("Size exceeds the SSBO maximum supported by current hardware, wanted: " + size + " bytes, maximum: " + maxSize + " bytes");
        }
        RenderSystem.assertOnRenderThread();
        this.assertValid();
        GlStateManager._glBindBuffer((int)37074, (int)this.id);
        GlStateManager._glBufferData((int)37074, (ByteBuffer)buffer, (int)this.usage);
        GlStateManager._glBindBuffer((int)37074, (int)0);
        this.buffer = buffer;
    }

    public int allocateBuffer(int bytes) {
        int size = Math.min(bytes, ShaderStorageBufferObject.getMaxSize());
        this.uploadData(MemoryTracker.m_182527_((int)size));
        return size;
    }

    @Override
    public void close() {
        RenderSystem.assertOnRenderThread();
        if (this.id != -1) {
            LOGGER.debug("Deleting buffer id={}, binding={}", (Object)this.id, (Object)this.binding);
            GL15.glDeleteBuffers((int)this.id);
            this.id = -1;
        }
        if (this.buffer != null) {
            MemoryUtil.memFree((Buffer)this.buffer);
            this.buffer = null;
        }
    }

    public void fetchData(Consumer<ByteBuffer> consumer, int access, int size) {
        RenderSystem.assertOnRenderThread();
        this.assertValid();
        if (size <= 0) {
            throw new IllegalArgumentException("Invalid size, please use a size greater than 0");
        }
        GlStateManager._glBindBuffer((int)37074, (int)this.id);
        consumer.accept(GL30.glMapBufferRange((int)37074, (long)0L, (long)size, (int)access, (ByteBuffer)this.buffer));
        GlStateManager._glUnmapBuffer((int)37074);
        GlStateManager._glBindBuffer((int)37074, (int)0);
    }

    public void readData(Consumer<ByteBuffer> consumer, int size) {
        this.fetchData(consumer, 1, size);
    }

    public void writeData(Consumer<ByteBuffer> consumer, int size, boolean invalidate) {
        int access = 2;
        if (invalidate) {
            access |= 8;
        }
        this.fetchData(consumer, access, size);
    }

    public void readWriteData(Consumer<ByteBuffer> consumer, int size) {
        this.fetchData(consumer, 3, size);
    }

    @Override
    public int getBinding() {
        return this.binding;
    }

    public int getId() {
        return this.id;
    }

    public int getUsage() {
        return this.usage;
    }

    protected void assertValid() {
        if (this.id == -1) {
            throw new IllegalStateException("Buffer is no longer valid!");
        }
    }

    public String toString() {
        return String.format("SSBO[binding=%s,id=%s,usage=%s]", this.binding, this.id, this.usage);
    }
}

