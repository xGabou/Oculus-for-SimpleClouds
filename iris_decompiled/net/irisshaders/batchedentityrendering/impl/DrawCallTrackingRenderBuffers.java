/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.batchedentityrendering.impl;

public interface DrawCallTrackingRenderBuffers {
    public int getDrawCalls();

    public int getRenderTypes();

    public void resetDrawCounts();
}

