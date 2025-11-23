/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.batchedentityrendering.impl;

public interface MemoryTrackingRenderBuffers {
    public int getEntityBufferAllocatedSize();

    public int getMiscBufferAllocatedSize();

    public int getMaxBegins();

    public void freeAndDeleteBuffers();
}

