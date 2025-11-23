/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.MemoryTracker
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  javax.annotation.Nullable
 *  org.lwjgl.opengl.GL15
 *  org.lwjgl.opengl.GL30
 *  org.lwjgl.opengl.GL31
 *  org.lwjgl.system.MemoryUtil
 */
package dev.nonamecrackers2.simpleclouds.client.mesh.instancing;

import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryUtil;

public class InstanceableMesh {
    private int arrayObjectId = -1;
    private int vertexBufferId = -1;
    private int indexBufferId = -1;
    @Nullable
    private ByteBuffer vertexBuffer;
    @Nullable
    private ByteBuffer indexBuffer;
    private int totalIndices;

    public InstanceableMesh(int vertexBufferSize, int indexBufferSize, VertexFormat format, Consumer<ByteBuffer> vertexBufferGenerator, Function<ByteBuffer, Integer> indexBufferGenerator) {
        RenderSystem.assertOnRenderThread();
        this.arrayObjectId = GL30.glGenVertexArrays();
        this.vertexBufferId = GL15.glGenBuffers();
        this.indexBufferId = GL15.glGenBuffers();
        GL30.glBindVertexArray((int)this.arrayObjectId);
        GL15.glBindBuffer((int)34962, (int)this.vertexBufferId);
        this.vertexBuffer = MemoryTracker.m_182527_((int)vertexBufferSize);
        vertexBufferGenerator.accept(this.vertexBuffer);
        GL15.glBufferData((int)34962, (ByteBuffer)this.vertexBuffer, (int)35044);
        format.m_166912_();
        GL15.glBindBuffer((int)34963, (int)this.indexBufferId);
        this.indexBuffer = MemoryTracker.m_182527_((int)indexBufferSize);
        this.totalIndices = indexBufferGenerator.apply(this.indexBuffer);
        GL15.glBufferData((int)34963, (ByteBuffer)this.indexBuffer, (int)35044);
        GL30.glBindVertexArray((int)0);
    }

    public static InstanceableMesh defaultSide() {
        return new InstanceableMesh(48, 24, DefaultVertexFormat.f_85814_, buffer -> {
            buffer.putFloat(-1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(1.0f);
            buffer.putFloat(1.0f);
            buffer.rewind();
        }, buffer -> {
            buffer.putInt(0);
            buffer.putInt(1);
            buffer.putInt(2);
            buffer.putInt(0);
            buffer.putInt(2);
            buffer.putInt(3);
            buffer.rewind();
            return 6;
        });
    }

    public static InstanceableMesh defaultNonCulledSide() {
        return new InstanceableMesh(48, 48, DefaultVertexFormat.f_85814_, buffer -> {
            buffer.putFloat(-1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(1.0f);
            buffer.putFloat(1.0f);
            buffer.rewind();
        }, buffer -> {
            buffer.putInt(0);
            buffer.putInt(1);
            buffer.putInt(2);
            buffer.putInt(0);
            buffer.putInt(2);
            buffer.putInt(3);
            buffer.putInt(2);
            buffer.putInt(1);
            buffer.putInt(0);
            buffer.putInt(3);
            buffer.putInt(2);
            buffer.putInt(0);
            buffer.rewind();
            return 12;
        });
    }

    public static InstanceableMesh defaultCube() {
        return new InstanceableMesh(96, 144, DefaultVertexFormat.f_85814_, buffer -> {
            buffer.putFloat(-1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(1.0f);
            buffer.putFloat(1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(1.0f);
            buffer.putFloat(1.0f);
            buffer.putFloat(1.0f);
            buffer.putFloat(1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(1.0f);
            buffer.putFloat(1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(-1.0f);
            buffer.putFloat(1.0f);
            buffer.rewind();
        }, buffer -> {
            buffer.putInt(0);
            buffer.putInt(1);
            buffer.putInt(2);
            buffer.putInt(0);
            buffer.putInt(2);
            buffer.putInt(3);
            buffer.putInt(4);
            buffer.putInt(7);
            buffer.putInt(6);
            buffer.putInt(4);
            buffer.putInt(6);
            buffer.putInt(5);
            buffer.putInt(7);
            buffer.putInt(0);
            buffer.putInt(3);
            buffer.putInt(7);
            buffer.putInt(3);
            buffer.putInt(6);
            buffer.putInt(1);
            buffer.putInt(4);
            buffer.putInt(5);
            buffer.putInt(1);
            buffer.putInt(5);
            buffer.putInt(2);
            buffer.putInt(1);
            buffer.putInt(0);
            buffer.putInt(7);
            buffer.putInt(1);
            buffer.putInt(7);
            buffer.putInt(4);
            buffer.putInt(5);
            buffer.putInt(6);
            buffer.putInt(3);
            buffer.putInt(5);
            buffer.putInt(3);
            buffer.putInt(2);
            buffer.rewind();
            return 36;
        });
    }

    public void drawInstanced(int count) {
        RenderSystem.assertOnRenderThread();
        GL30.glBindVertexArray((int)this.arrayObjectId);
        GL31.glDrawElementsInstanced((int)4, (int)this.totalIndices, (int)5125, (long)0L, (int)count);
    }

    public void destroy() {
        this.totalIndices = 0;
        if (this.arrayObjectId >= 0) {
            RenderSystem.glDeleteVertexArrays((int)this.arrayObjectId);
            this.arrayObjectId = -1;
        }
        if (this.vertexBufferId >= 0) {
            RenderSystem.glDeleteBuffers((int)this.vertexBufferId);
            this.vertexBufferId = -1;
        }
        if (this.vertexBuffer != null) {
            MemoryUtil.memFree((Buffer)this.vertexBuffer);
            this.vertexBuffer = null;
        }
        if (this.indexBufferId >= 0) {
            RenderSystem.glDeleteBuffers((int)this.indexBufferId);
            this.indexBufferId = -1;
        }
        if (this.indexBuffer != null) {
            MemoryUtil.memFree((Buffer)this.indexBuffer);
            this.indexBuffer = null;
        }
    }
}

