/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.MemoryTracker
 *  com.mojang.blaze3d.systems.RenderSystem
 *  javax.annotation.Nullable
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.AABB
 *  org.lwjgl.opengl.GL15
 *  org.lwjgl.system.MemoryUtil
 */
package dev.nonamecrackers2.simpleclouds.client.mesh.chunk;

import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.nonamecrackers2.simpleclouds.client.mesh.lod.PreparedChunk;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryUtil;

public class MeshChunk {
    private final PreparedChunk preparedChunk;
    private final BufferSet opaqueBuffers;
    private final Optional<BufferSet> transparentBuffers;
    private float boundsMinX;
    private float boundsMinY;
    private float boundsMinZ;
    private float boundsMaxX;
    private float boundsMaxY;
    private float boundsMaxZ;
    private float minHeight;
    private float maxHeight;
    private int ticksSinceLastGen;
    private boolean fadeEnabled;
    private float alpha;
    private float alphaO;

    public MeshChunk(PreparedChunk preparedChunk, int opaqueElements, int opaqueElementOffset, int bytesPerOpaqueElement, int transparentElements, int transparentElementOffset, int bytesPerTransparentElement, boolean useTransparency) {
        this.preparedChunk = preparedChunk;
        this.opaqueBuffers = new BufferSet(opaqueElements, opaqueElementOffset, bytesPerOpaqueElement);
        this.transparentBuffers = useTransparency ? Optional.of(new BufferSet(transparentElements, transparentElementOffset, bytesPerTransparentElement)) : Optional.empty();
        AABB bounds = preparedChunk.bounds();
        this.boundsMinX = (float)bounds.f_82288_;
        this.boundsMinY = (float)bounds.f_82289_;
        this.boundsMinZ = (float)bounds.f_82290_;
        this.boundsMaxX = (float)bounds.f_82291_;
        this.boundsMaxY = (float)bounds.f_82292_;
        this.boundsMaxZ = (float)bounds.f_82293_;
        this.minHeight = this.boundsMinY;
        this.maxHeight = this.boundsMaxY;
    }

    public void tick() {
        ++this.ticksSinceLastGen;
        this.alphaO = this.alpha;
        if (this.fadeEnabled && this.alpha < 1.0f) {
            this.alpha += 0.2f;
            if (this.alpha > 1.0f) {
                this.alpha = 1.0f;
            }
        }
    }

    public void setFadeEnabled(boolean flag) {
        this.fadeEnabled = flag;
    }

    public void resetAlpha() {
        this.alpha = 0.0f;
        this.alphaO = 0.0f;
    }

    public PreparedChunk getChunkInfo() {
        return this.preparedChunk;
    }

    public BufferSet getOpaqueBuffers() {
        return this.opaqueBuffers;
    }

    public Optional<BufferSet> getTransparentBuffers() {
        return this.transparentBuffers;
    }

    public void clearChunk() {
        this.opaqueBuffers.setTotalElementCount(0);
        this.transparentBuffers.ifPresent(bufferSet -> bufferSet.setTotalElementCount(0));
    }

    public void setBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.boundsMinX = minX;
        this.boundsMinY = minY;
        this.boundsMinZ = minZ;
        this.boundsMaxX = maxX;
        this.boundsMaxY = maxY;
        this.boundsMaxZ = maxZ;
    }

    public void setHeights(float minHeight, float maxHeight) {
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    public void resetLastGenTime() {
        this.ticksSinceLastGen = 0;
    }

    public int getTicksSinceLastGen() {
        return this.ticksSinceLastGen;
    }

    public float getBoundsMinX() {
        return this.boundsMinX;
    }

    public float getBoundsMinY() {
        return this.boundsMinY;
    }

    public float getBoundsMinZ() {
        return this.boundsMinZ;
    }

    public float getBoundsMaxX() {
        return this.boundsMaxX;
    }

    public float getBoundsMaxY() {
        return this.boundsMaxY;
    }

    public float getBoundsMaxZ() {
        return this.boundsMaxZ;
    }

    public float getMinHeight() {
        return this.minHeight;
    }

    public float getMaxHeight() {
        return this.maxHeight;
    }

    public float getAlpha(float partialTick) {
        return Mth.m_14179_((float)partialTick, (float)this.alphaO, (float)this.alpha);
    }

    public void destroy() {
        this.opaqueBuffers.destroy();
        this.transparentBuffers.ifPresent(BufferSet::destroy);
    }

    public static class BufferSet {
        private int bufferId = GL15.glGenBuffers();
        @Nullable
        private ByteBuffer buffer;
        private int elementCount;
        private final int bufferSize;
        private final int maxElements;
        private final int elementOffset;

        public BufferSet(int maxElements, int elementOffset, int bytesPerElement) {
            this.maxElements = maxElements;
            this.elementOffset = elementOffset;
            this.bufferSize = maxElements * bytesPerElement;
            this.buffer = MemoryTracker.m_182527_((int)this.bufferSize);
            GL15.glBindBuffer((int)37074, (int)this.bufferId);
            GL15.glBufferData((int)37074, (ByteBuffer)this.buffer, (int)35048);
            GL15.glBindBuffer((int)37074, (int)0);
        }

        public void setTotalElementCount(int count) {
            this.elementCount = count;
        }

        public int getElementCount() {
            return this.elementCount;
        }

        public int getMaxElements() {
            return this.maxElements;
        }

        public int getElementOffset() {
            return this.elementOffset;
        }

        public int getBufferSize() {
            return this.bufferSize;
        }

        public int getBufferId() {
            return this.bufferId;
        }

        public void destroy() {
            this.elementCount = 0;
            if (this.bufferId >= 0) {
                RenderSystem.glDeleteBuffers((int)this.bufferId);
                this.bufferId = -1;
            }
            if (this.buffer != null) {
                MemoryUtil.memFree((Buffer)this.buffer);
                this.buffer = null;
            }
        }
    }
}

