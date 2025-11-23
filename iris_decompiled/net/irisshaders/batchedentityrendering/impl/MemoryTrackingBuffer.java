/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.batchedentityrendering.impl;

public interface MemoryTrackingBuffer {
    public int getAllocatedSize();

    public int getUsedSize();

    public void freeAndDeleteBuffer();
}

